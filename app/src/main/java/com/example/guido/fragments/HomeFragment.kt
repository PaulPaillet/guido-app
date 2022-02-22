package com.example.guido.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.guido.R
import com.example.guido.models.Body
import com.example.guido.models.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File
import java.io.InputStream


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    lateinit var file : InputStream
    lateinit var values : ByteArray
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var stor = File(activity?.applicationContext?.getFileStreamPath("audioRecord.3gp")?.path.toString())
        file = stor.inputStream()
        values = file.readBytes();
        Log.d("lenght", values.size.toString())
    }

    private fun makecomplexCall(){
        var body : Body = Body()
        body.value=values
        body.sampleRate = 48000
        body.sampleWidth = 4
        CoroutineScope(Dispatchers.IO).launch {
            GlobalScope.launch {
                try {
                    val response : Response<Note> = ApiClient.apiService.getNote(body);
                    Log.d("alo",response.body().toString())
                    if (response.isSuccessful && response.body() != null) {
                        val content = response.body()
                        button.setText(content.toString())
//do something
                    } else {
                        Log.d("error",response.body().toString())
                    }
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        button = root.findViewById<Button>(R.id.buttonTest)
        button.setOnClickListener {
            makecomplexCall()
        }
        // Inflate the layout for this fragment
        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment SettingsFragment.
         */
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
            }
    }
}