//package com.kripix.dev.ruangkelas.ui.kelas.pertemuan.detail
//
//import android.content.Context
//import android.content.SharedPreferences
//import android.os.Bundle
//import android.view.View
//import android.widget.SeekBar
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.kripix.dev.ruangkelas.R
//import com.kripix.dev.ruangkelas.databinding.ActivityLatihanSharedPreferenceBinding
//import com.kripix.dev.ruangkelas.data.kelas.PERTEMUAN_ID_EKSTRA
//import com.kripix.dev.ruangkelas.data.kelas.Pertemuan
//import com.kripix.dev.ruangkelas.data.kelas.kelasList
//
//class LatihanSharedPreferenceActivity : AppCompatActivity(), View.OnClickListener {
//
//    private lateinit var binding: ActivityLatihanSharedPreferenceBinding
//    lateinit var sharedPreferences: SharedPreferences
//    val pref_nama = "Setting"
//    val field_font_size = "font_size"
//    val field_text = "tekes"
//    val def_font_size = 12
//    val def_text = "NIM nae"
//
//    val onSeek = object : SeekBar.OnSeekBarChangeListener{
//        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//            binding.edTxt.setTextSize(progress.toFloat())
//        }
//
//        override fun onStartTrackingTouch(seekBar: SeekBar?) {
//        }
//
//        override fun onStopTrackingTouch(seekBar: SeekBar?) {
//        }
//
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        binding = ActivityLatihanSharedPreferenceBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        binding.btnBack.setOnClickListener {
//            // Navigating up to the previous destination
//            finish()
//        }
//
//        val pertemuanID = intent.getIntExtra(PERTEMUAN_ID_EKSTRA,-1)
//
//        val pertemuan = pertemuanById(pertemuanID)
//
//        if (pertemuan != null) {
////            nomorTextView.text = pertemuan.nomor.toString()
////            kelasTextView.text = pertemuan.kelas.mapel
//            binding.tvTema.text = pertemuan.judul
//            binding.tvDeskripsi.text = pertemuan.deskripsi
//        } else {
//            // Tampilkan pesan error jika pertemuan tidak ditemukan
//            // ...
//        }
//
//        sharedPreferences = getSharedPreferences(pref_nama, Context.MODE_PRIVATE)
//        binding.edTxt.setText(sharedPreferences.getString(field_text,def_text))
//        binding.edTxt.textSize = sharedPreferences.getInt(field_font_size,def_font_size).toFloat()
//        binding.seekBar.progress = sharedPreferences.getInt(field_font_size, def_font_size)
//        binding.seekBar.setOnSeekBarChangeListener(onSeek)
//        binding.btnSimpan.setOnClickListener(this)
//    }
//
//    override fun onClick(v: View?) {
//        sharedPreferences = getSharedPreferences(pref_nama, Context.MODE_PRIVATE)
//        val prefEdit = sharedPreferences.edit()
//        prefEdit.putString(field_text,binding.edTxt.text.toString())
//        prefEdit.putInt(field_font_size,binding.seekBar.progress)
//        prefEdit.commit()
//
//        Toast.makeText(this,"Perubahan disimpan", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun pertemuanById(id: Int): Pertemuan? {
//        for (kelas in kelasList) {
//            for (pertemuan in kelas.pertemuanList) {
//                if (pertemuan.id == id) {
//                    return pertemuan
//                }
//            }
//        }
//        return null
//    }
//
//
//
//}