package com.example.guido

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.guido.fragments.QuestionAnswerFragment
import com.example.guido.fragments.RecordFragment
import com.example.guido.fragments.SettingsFragment
import com.example.guido.fragments.VolumeFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import CallApi

private const val REQUEST_PERMISSIONS_CODE = 200

class MainActivity : AppCompatActivity() {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service = retrofit.create(CallApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        val questionAnswerFragment = QuestionAnswerFragment()
        val recordFragment = RecordFragment()
        val settingsFragment = SettingsFragment()
        val volumeFragment = VolumeFragment()

        makeCurrentFragment(recordFragment)
        val bottomNavigation = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.ic_question_answer -> makeCurrentFragment(questionAnswerFragment)
                R.id.ic_record -> makeCurrentFragment(recordFragment)
                R.id.ic_volume -> makeCurrentFragment(volumeFragment)
                R.id.ic_settings -> makeCurrentFragment(settingsFragment)
            }
            true
        }

        // we make the api call
        CoroutineScope(Dispatchers.IO).launch {
            executeCall();
        }


        //val intent : Intent =  Intent(this,DetectionActivity::class.java)
        //startActivity(intent)

        // Verify and request permissions to record audio and to write data in storage
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_CODE)
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }

    private fun executeCall() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response= ApiClient.apiService.getApi();

                if (response.isSuccessful && response.body() != null) {
                    val content = response.body()
                    Toast.makeText(this@MainActivity,
                    "call succeded :${content}",
                    Toast.LENGTH_LONG).show()
                    Log.d("value","${content}");
//do something
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Error Occurred: ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Error Occurred: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
