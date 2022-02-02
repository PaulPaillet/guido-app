package com.example.guido.fragments

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.guido.R
import java.io.File
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val DEBUG_RECORDING_AUDIO = "RECORDING_AUDIO"

/**
 * A simple [Fragment] subclass.
 * Use the [RecordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var nbNotes: Int = 0
    private var startx: Int = 170
    private var starty: Int = 155
    private var yPositions: IntArray = intArrayOf(220, 470, 720, 982, 1228, 1480)
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var root: View

    private var mediaRecorder: MediaRecorder? = null
    private lateinit var mediaPlayer: MediaPlayer
    private var recording: Boolean = false
    private lateinit var buttonMicro: ImageButton
    private lateinit var buttonListen: ImageButton
    private lateinit var buttonRecording: ImageButton
    private lateinit var audioRecordFile: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        nbNotes = 0
    }

    private fun startRecording() {
        //audioRecordFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath + "/audioRecord.3gp"
        // Storing audio file in application storage space
        audioRecordFile = activity?.applicationContext?.getFileStreamPath("audioRecord.3gp")?.path.toString()

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(audioRecordFile)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
                Log.d(DEBUG_RECORDING_AUDIO, "MediaRecorder prepare() ok")
            } catch (e: IOException) {
                Log.d(DEBUG_RECORDING_AUDIO, "MediaRecorder prepare() failed")
                Log.d(DEBUG_RECORDING_AUDIO, e.toString())
                return
            }
        }

        Toast.makeText(context, "A l'écoute", Toast.LENGTH_SHORT).show()
        mediaRecorder?.start()
        Log.d(DEBUG_RECORDING_AUDIO, "MediaRecorder started")
        recording = true

        buttonMicro.visibility = View.GONE
        buttonListen.visibility = View.GONE
        buttonRecording.visibility = View.VISIBLE
    }

    private fun stopRecording() {
        Toast.makeText(context, "Enregistrement terminé", Toast.LENGTH_SHORT).show()
        mediaRecorder?.stop()
        mediaRecorder?.release()
        Log.d(DEBUG_RECORDING_AUDIO, "MediaRecorder stopped and released")
        recording = false

        buttonRecording.visibility = View.GONE
        buttonMicro.visibility = View.VISIBLE
        buttonListen.visibility = View.VISIBLE
    }

    fun calculatePos(note: String): Pair<Int,Int> {
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels
        Log.d("width", width.toString())
        Log.d("height", height.toString())

        var toSub = -1
        if (note == "Do")  toSub = -4
        if (note == "Re")  toSub = 8
        if (note == "Mi")  toSub = 20
        if (note == "Fa")  toSub = 32
        if (note == "Sol") toSub = 44
        if (note == "La")  toSub = 57
        if (note == "Si")  toSub = 69
        toSub += 10

        val reste:Int = nbNotes % 13

        val div:Int = nbNotes / 13
        if (div == 3) toSub += 3
        if (div > 3) toSub -= 2

        Log.d("div", div.toString())
        Log.d("test", yPositions[0].toString())

        return Pair(startx + (width / adjustImageViewToScreen(18) * reste), yPositions[div] - toSub)
    }

    fun ajoutNote(layout: ConstraintLayout, note: String){
        if (nbNotes < 78) {
            val imageView = ImageView(activity)
            imageView.setImageResource(R.drawable.blanchecrochetqueue)
            imageView.layoutParams = ConstraintLayout.LayoutParams(adjustImageViewToScreen(65), adjustImageViewToScreen(65))
            val (x, y) = calculatePos(note)
            Log.d("y : ", y.toString())
            imageView.x = adjustImageViewToScreen(x).toFloat()
            imageView.y = adjustImageViewToScreen(y).toFloat()
            imageView.bringToFront()
            layout.addView(imageView)
            nbNotes++
        }
    }

    fun adjustImageViewToScreen(toAdjust: Int): Int {
        val pxOriginX: Int = 1080

        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val pxActualX: Int = displayMetrics.widthPixels

        return toAdjust * pxActualX / pxOriginX
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_record, container, false)
        constraintLayout = root.findViewById<ConstraintLayout>(R.id.detect)

        val partition = ImageView(activity)
        partition.setImageResource(R.drawable.partition)
        partition.layoutParams = ConstraintLayout.LayoutParams(adjustImageViewToScreen(2 * 607), adjustImageViewToScreen(2 * 734))
        partition.x = adjustImageViewToScreen(0).toFloat()
        partition.y = adjustImageViewToScreen(100).toFloat()
        constraintLayout.addView(partition)

        buttonMicro = root.findViewById<ImageButton>(R.id.buttonMicro)
        buttonMicro.setOnClickListener {
            startRecording();
        }

        buttonListen = root.findViewById<ImageButton>(R.id.buttonListen)
        buttonListen.visibility = View.GONE
        buttonListen.setOnClickListener {
            val file = File(audioRecordFile)
            val uri = Uri.fromFile(file)
            mediaPlayer = MediaPlayer.create(context, uri)
            mediaPlayer.start()
            //ajoutNote(layout, "Do")
            ajoutNote(constraintLayout, "Do")            
            ajoutNote(constraintLayout, "Re")
            ajoutNote(constraintLayout, "Mi")
            ajoutNote(constraintLayout, "Fa")
            ajoutNote(constraintLayout, "Sol")
            ajoutNote(constraintLayout, "La")
            ajoutNote(constraintLayout, "Si")
        }

        buttonRecording = root.findViewById<ImageButton>(R.id.buttonRecording)
        buttonRecording.visibility = View.GONE
        buttonRecording.setOnClickListener {
            stopRecording();
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
         * @return A new instance of fragment RecordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}