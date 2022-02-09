package com.example.guido.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import com.example.guido.R


/**
 * A simple [Fragment] subclass.
 * Use the [VolumeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VolumeFragment : Fragment() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()
    private lateinit var seekBar: SeekBar

    fun playNote(note: String) {
        mediaPlayer = MediaPlayer.create(context, R.raw.notes)
        initializeSeekBar()
        mediaPlayer.start()
    }

    private fun initializeSeekBar() {
        seekBar.max = mediaPlayer.seconds

        runnable = Runnable {
            seekBar.progress = mediaPlayer.currentSeconds

            //tv_pass.text = "${mediaPlayer.currentSeconds} sec"
            //val diff = mediaPlayer.seconds - mediaPlayer.currentSeconds
            //tv_due.text = "$diff sec"

            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }

    val MediaPlayer.seconds:Int
        get() {
            return this.duration / 1000
        }

    val MediaPlayer.currentSeconds:Int
        get() {
            return this.currentPosition / 1000
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_volume, container, false)

        val buttonSi = root.findViewById<Button>(R.id.buttonListen)
        buttonSi.setOnClickListener {
            playNote("Si")
        }

        seekBar = root.findViewById<SeekBar>(R.id.seekBar)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    mediaPlayer.seekTo(i * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment VolumeFragment.
         */
        @JvmStatic
        fun newInstance() =
            VolumeFragment().apply {
            }
    }
}