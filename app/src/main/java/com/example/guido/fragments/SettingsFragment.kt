package com.example.guido.fragments

import CallApi
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.guido.R
import com.example.guido.models.Body
import com.example.guido.models.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service = retrofit.create(CallApi::class.java)
    lateinit var file : InputStream
    lateinit var values : ByteArray
    private lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        file = resources.openRawResource(R.raw.do1)
        values = file.readBytes();
        Log.d("lenght",values.size.toString())
    }

    fun makecomplexCall(){
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
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
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
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}