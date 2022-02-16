package com.example.guido.fragments

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.guido.R
import com.example.guido.models.Body
import com.example.guido.models.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.io.InputStream


private const val DEBUG_RECORDING_AUDIO = "RECORDING_AUDIO"

/**
 * A simple [Fragment] subclass.
 * Use the [RecordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordFragment : Fragment() {

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
    private lateinit var seekBar: SeekBar
    private lateinit var textView: TextView
    private var tempo: Int = 0

    lateinit var file : InputStream
    lateinit var values : ByteArray

    override fun onDestroy() {
        super.onDestroy()
        nbNotes = 0
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
                    if (response.isSuccessful && response.body() != null) {
                        val content = response.body()
                        show(content.toString())
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

    private fun show(result: String) {
        Log.d("result : ",result)
        val parts = result.split(" ")
        parts.forEach{
            Log.d("note : ",it)
            ajoutNote(constraintLayout,it,"blanche")
        }
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

        var stor = File(activity?.applicationContext?.getFileStreamPath("audioRecord.3gp")?.path.toString())
        file = stor.inputStream()
        values = file.readBytes();
        Log.d("lenght", values.size.toString())
    }

    fun calculatePos(note: String): Pair<Int,Int> {
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels
        Log.d("width", width.toString())
        Log.d("height", height.toString())

        var toSub = -1
        if (note == "do")  toSub = -4
        else if (note == "ré")  toSub = 8
        else if (note == "mi")  toSub = 20
        else if (note == "fa")  toSub = 32
        else if (note == "sol") toSub = 44
        else if (note == "la")  toSub = 57
        else if (note == "si")  toSub = 69
        else return Pair(-1,-1)
        toSub += 34

        val reste:Int = nbNotes % 13

        val div:Int = nbNotes / 13
        if (div == 3) toSub += 3
        if (div > 3) toSub -= 2

        Log.d("div", div.toString())
        Log.d("test", yPositions[0].toString())

        return Pair(startx + (width / adjustImageViewToScreen(18) * reste), yPositions[div] - toSub)
    }

    fun ajoutNote(layout: ConstraintLayout, note: String,couleur:String){
        if (nbNotes < 78) {
            val imageView = ImageView(activity)
            if(couleur == "blanche") imageView.setImageResource(R.drawable.blanche)
            if(couleur == "noire") imageView.setImageResource(R.drawable.noire)
            if(couleur == "ronde") imageView.setImageResource(R.drawable.ronde)
            imageView.layoutParams = ConstraintLayout.LayoutParams(adjustImageViewToScreen(100), adjustImageViewToScreen(100))
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

    private fun setUpSeekBar(){
        seekBar = root.findViewById<SeekBar>(R.id.seekBar)
        textView = root.findViewById<TextView>(R.id.textView)
        textView.text ="0 BPM"
        seekBar.max = 60
        seekBar.progress = 30
        tempo = 30
        textView.text = "30 BPM"
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if(progress==0) tempo = progress + 1
                else tempo = progress
                textView.text = tempo.toString() + " BPM"
                couleurNote(2)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun couleurNote(sec: Int)/*: String*/{
        var bpm = 60/tempo
        var duree = (sec*bpm).toInt()
        when(duree){
            in 0..1 -> Log.d("duree",duree.toString())
            in 1..2 -> Log.d("duree",duree.toString())
            in 2..4 -> Log.d("duree",duree.toString())
            else -> {
                Log.d("duree",duree.toString())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_record, container, false)
        constraintLayout = root.findViewById<ConstraintLayout>(R.id.detect)
        setUpSeekBar()

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
        }

        buttonRecording = root.findViewById<ImageButton>(R.id.buttonRecording)
        buttonRecording.visibility = View.GONE
        buttonRecording.setOnClickListener {
            stopRecording();
            makecomplexCall();
        }

        // Inflate the layout for this fragment
        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment RecordFragment.
         */
        @JvmStatic
        fun newInstance() =
            RecordFragment().apply {
            }
    }
}