package com.kripix.dev.ruangkelas.ui.activity

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.MediaController
import com.kripix.dev.ruangkelas.R
import com.kripix.dev.ruangkelas.databinding.ActivityLatihanMultimediaBinding
import java.util.concurrent.TimeUnit

class LatihanMultimediaActivity : AppCompatActivity(), View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private lateinit var binding: ActivityLatihanMultimediaBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaController: MediaController
    private val handler = Handler()

    private val songList = intArrayOf(R.raw.music_1, R.raw.music_2, R.raw.music_3, R.raw.music_4)
    private val coverList = intArrayOf(
        R.drawable.cover_1,
        R.drawable.cover_2,
        R.drawable.cover_3,
        R.drawable.cover_4
    )
    private val songTitle = arrayOf("Lo fi", "Blue Bird", "Badineri - Bach", "Magnetic - ILLIT")
    private val videoList = intArrayOf(R.raw.video_1, R.raw.video_2, R.raw.video_3, R.raw.video_4)

    private var songNow = 0
    private var videoNow = 0

    private val nextVid = View.OnClickListener { v:View ->
        if (videoNow < videoList.size - 1) {
            videoNow++
        } else {
            videoNow = 0
        }
        videoSet(videoNow)
    }

    private val prevVid = View.OnClickListener { v:View ->
        if (videoNow > 0) {
            videoNow--
        } else {
            videoNow = videoList.size - 1
        }
        videoSet(videoNow)
    }

    private fun videoSet(pos: Int) {
        binding.videoView.setVideoURI(Uri.parse("android.resource://"+packageName+"/"+videoList[pos]))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLatihanMultimediaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mediaController = MediaController(this)

        binding.sbMusic.max = 100
        binding.sbMusic.progress = 0
        binding.sbMusic.setOnSeekBarChangeListener(this)
        binding.btnPrev.setOnClickListener(this)
        binding.btnPlay.setOnClickListener(this)
        binding.btnPause.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)

        initMediaPlayer()

        mediaController.setPrevNextListeners(nextVid, prevVid)
        mediaController.setAnchorView(binding.videoView)
        binding.videoView.setMediaController(mediaController)
        videoSet(videoNow)

        binding.btnBack.setOnClickListener {
            // Navigating up to the previous destination
            finish()
        }
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener {
            audioNext()
        }
    }

    private fun releaseMediaPlayer() {
        mediaPlayer.release()
        initMediaPlayer()
    }

    private fun miliSecondtoString(ms: Int): String {
        val sec = TimeUnit.MILLISECONDS.toSeconds(ms.toLong()) % 60
        val min = TimeUnit.MILLISECONDS.toMinutes(ms.toLong())
        return "$min:$sec"
    }

    private fun updateSeekBarProgress() {
        val currentTime = mediaPlayer.currentPosition
        binding.tvPlayTime.text = miliSecondtoString(currentTime)
        binding.sbMusic.progress = currentTime
        if (mediaPlayer.isPlaying)
            handler.postDelayed({ updateSeekBarProgress() }, 50)
    }

    private fun audioPlay(pos: Int) {
        releaseMediaPlayer()
        mediaPlayer = MediaPlayer.create(this, songList[pos])
        binding.sbMusic.max = mediaPlayer.duration
        binding.tvTotalTime.text = miliSecondtoString(mediaPlayer.duration)
        binding.imgCover.setImageResource(coverList[pos])
        binding.tvJudulMusic.text = songTitle[pos]
        // Don't start the media player automatically
        // mediaPlayer.start()
        // updateSeekBarProgress()
    }

    private fun audioNext() {
        if (songNow < songList.size - 1) {
            songNow++
        } else {
            songNow = 0
        }
        audioPlay(songNow)
        mediaPlayer.start()
        updateSeekBarProgress()
    }

    private fun audioPrev() {
        if (songNow > 0) {
            songNow--
        } else {
            songNow = songList.size - 1
        }
        audioPlay(songNow)
        mediaPlayer.start()
        updateSeekBarProgress()
    }

    private fun audioStop() {
        mediaPlayer.pause()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_play -> {
                mediaPlayer.start()
                updateSeekBarProgress()
            }
            R.id.btn_pause -> {
                audioStop()
            }
            R.id.btn_prev -> {
                audioPrev()
            }
            R.id.btn_next -> {
                audioNext()
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        seekBar?.let { mediaPlayer.seekTo(it.progress) }
    }
}
