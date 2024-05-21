package com.kripix.dev.ruangkelas.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.kripix.dev.ruangkelas.R
import com.kripix.dev.ruangkelas.databinding.ActivityRuangBinding
import com.kripix.dev.ruangkelas.logic.kelas.KELAS_ID_EKSTRA
import com.kripix.dev.ruangkelas.logic.kelas.Kelas
import com.kripix.dev.ruangkelas.logic.kelas.PERTEMUAN_ID_EKSTRA
import com.kripix.dev.ruangkelas.logic.kelas.Pertemuan
import com.kripix.dev.ruangkelas.logic.kelas.kelasList
import com.kripix.dev.ruangkelas.logic.kelas.pertemuanAdapter
import com.kripix.dev.ruangkelas.logic.kelas.pertemuanClickListener

class RuangActivity : AppCompatActivity(), pertemuanClickListener {
    private lateinit var binding: ActivityRuangBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRuangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val kelasID = intent.getIntExtra(KELAS_ID_EKSTRA, -1)
        val kelas = kelasById(kelasID)


        if (kelas != null)
        {
            binding.kelasImg.setImageResource(kelas.img)
            binding.kelasCreator.text = kelas.creator
            binding.kelasTitle.text = kelas.mapel

            val pertemuanList = kelas.pertemuanList

            binding.rvRuang.apply {
                layoutManager = GridLayoutManager(applicationContext, 5)
                adapter = pertemuanAdapter(pertemuanList,this@RuangActivity)
            }
        }


    }

    private fun kelasById (kelasID: Int): Kelas? {
        for (kelas in kelasList) {
            if (kelas.id == kelasID)
                return kelas
        }
        return null
    }

    override fun onClick(pertemuan: Pertemuan) {


        if(pertemuan.id == 9){
            val intent = Intent(applicationContext, LatihanSharedPreferenceActivity::class.java)
            intent.putExtra(PERTEMUAN_ID_EKSTRA, pertemuan.id)
            startActivity(intent)
        } else if(pertemuan.id == 10){
            val intent = Intent(applicationContext, LatihanMultimediaActivity::class.java)
            intent.putExtra(PERTEMUAN_ID_EKSTRA, pertemuan.id)
            startActivity(intent)
        }

        else {
            val intent = Intent(applicationContext, DetailPertemuanActivity::class.java)
            intent.putExtra(PERTEMUAN_ID_EKSTRA, pertemuan.id)
            startActivity(intent)
        }

    }

}