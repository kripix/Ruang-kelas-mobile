package com.kripix.dev.ruangkelas.data.music

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.Switch
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kripix.dev.ruangkelas.R
import com.kripix.dev.ruangkelas.databinding.BottomSheetOptionMusicBinding
import java.util.concurrent.TimeUnit

class BottomSheetMusic(
    private val context: Context,
    private val menuMusicList: List<MenuMusicItem>
) : View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private lateinit var mediaPlayer: MediaPlayer
    private val handler = Handler()
    private lateinit var binding: BottomSheetOptionMusicBinding
    private var musicMenu: BottomSheetDialog = BottomSheetDialog(context)
    private var currentMusicResource: Int? = null
    private var isMusicPlaying: Boolean = false

    init {
        initMediaPlayer()
        setupMusicMenu()
    }

    private fun setupMusicMenu() {
        binding = BottomSheetOptionMusicBinding.inflate(LayoutInflater.from(context))
        musicMenu.setContentView(binding.root)

        // Set up the adapter with the correct click listener
        binding.lvPlaylist.adapter = musicAdapter(context, menuMusicList) { selectedMusic ->
            updateNowPlaying(selectedMusic)
            playMusic(selectedMusic.resource)
        }

        binding.btnPlay.setOnClickListener(this)
        binding.btnPause.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
        binding.btnPrev.setOnClickListener(this)
        binding.sbMusicNp.setOnSeekBarChangeListener(this)

        val switchMusic = binding.root.findViewById<Switch>(R.id.switch_music)
        switchMusic.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (currentMusicResource != null) {
                    playMusic(currentMusicResource!!)
                } else {
                    // Play the first song in the list by default
                    if (menuMusicList.isNotEmpty()) {
                        updateNowPlaying(menuMusicList[0])
                        playMusic(menuMusicList[0].resource)
                    }
                }
            } else {
                stopMusic()
            }
        }
    }

    private fun stopMusic() {
        mediaPlayer.pause()
        isMusicPlaying = false
    }

    private fun updateNowPlaying(musicItem: MenuMusicItem) {
        Glide.with(context)
            .load(musicItem.image)
            .into(binding.imgCoverNp)
        binding.tvJudulNp.text = musicItem.judul
        binding.tvAuthorNp.text = musicItem.author
        binding.tvTotalTime.text = miliSecondtoString(mediaPlayer.duration)
        binding.switchMusic.isChecked = true
    }

    private fun playMusic(musicResource: Int) {
        // Stop the currently playing music if any
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
            initMediaPlayer()
        }

        // Play the new music
        currentMusicResource = musicResource
        mediaPlayer = MediaPlayer.create(context, musicResource)
        mediaPlayer.start()
        updateSeekBarProgress()
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
        binding.sbMusicNp.progress = currentTime
        if (mediaPlayer.isPlaying)
            handler.postDelayed({ updateSeekBarProgress() }, 50)
    }

    private fun audioPlay(pos: Int) {
        releaseMediaPlayer()
        val musicItem = menuMusicList[pos]
        playMusic(musicItem.resource)
        binding.sbMusicNp.max = mediaPlayer.duration
        binding.tvTotalTime.text = miliSecondtoString(mediaPlayer.duration)
        Glide.with(context)
            .load(musicItem.image)
            .into(binding.imgCoverNp)
        binding.tvJudulNp.text = musicItem.judul
    }

    private fun audioNext() {
        val currentPos = menuMusicList.indexOfFirst { it.resource == currentMusicResource }
        val nextPos = (currentPos + 1) % menuMusicList.size
        audioPlay(nextPos)
    }

    private fun audioPrev() {
        val currentPos = menuMusicList.indexOfFirst { it.resource == currentMusicResource }
        val prevPos = if (currentPos > 0) currentPos - 1 else menuMusicList.size - 1
        audioPlay(prevPos)
    }

    private fun audioStop() {
        mediaPlayer.pause()
        binding.switchMusic.isChecked = false
    }

    fun show() {
        musicMenu.show()
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