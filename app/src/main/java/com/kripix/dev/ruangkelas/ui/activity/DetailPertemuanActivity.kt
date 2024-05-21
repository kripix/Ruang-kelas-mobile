package com.kripix.dev.ruangkelas.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kripix.dev.ruangkelas.R
import com.kripix.dev.ruangkelas.databinding.ActivityDetailPertemuanBinding
import com.kripix.dev.ruangkelas.logic.kelas.PERTEMUAN_ID_EKSTRA
import com.kripix.dev.ruangkelas.logic.kelas.Pertemuan
import com.kripix.dev.ruangkelas.logic.kelas.kelasList

class DetailPertemuanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPertemuanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailPertemuanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        val kelasID = intent.getIntExtra("kelas",-1)
        val pertemuanID = intent.getIntExtra(PERTEMUAN_ID_EKSTRA,-1)

        val pertemuan = pertemuanById(pertemuanID)

        if (pertemuan != null) {
//            nomorTextView.text = pertemuan.nomor.toString()
//            kelasTextView.text = pertemuan.kelas.mapel
            binding.tvTema.text = pertemuan.judul
            binding.tvDeskripsi.text = pertemuan.deskripsi
        } else {
            // Tampilkan pesan error jika pertemuan tidak ditemukan
            // ...
        }



    }

    private fun pertemuanById(id: Int): Pertemuan? {
        for (kelas in kelasList) {
            for (pertemuan in kelas.pertemuanList) {
                if (pertemuan.id == id) {
                    return pertemuan
                }
            }
        }
        return null
    }


}