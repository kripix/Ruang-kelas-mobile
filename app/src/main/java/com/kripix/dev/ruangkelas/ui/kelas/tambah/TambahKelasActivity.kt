package com.kripix.dev.ruangkelas.ui.kelas.tambah

import IconKelasAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.kripix.dev.ruangkelas.R
import com.kripix.dev.ruangkelas.data.MediaHelper
import com.kripix.dev.ruangkelas.data.api.kelas.KelasApiRetrofit
import com.kripix.dev.ruangkelas.data.model.IconKelasModel
import com.kripix.dev.ruangkelas.data.model.TambahKelasModel
import com.kripix.dev.ruangkelas.databinding.ActivityTambahKelasBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class TambahKelasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahKelasBinding
    private lateinit var iconAdapter: IconKelasAdapter
    private lateinit var mediaHelper: MediaHelper

    private val api by lazy { KelasApiRetrofit().endpoint }

    var imStr = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTambahKelasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            finish()
        }


        setupListener()

        mediaHelper = MediaHelper(this)
        binding.pvImg.setOnClickListener{
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent, mediaHelper.getRcGallery())
        }


        iconAdapter = IconKelasAdapter(arrayListOf())
        getIcon()
        setupIconRecyclerView()
        Log.d("TambahKelasActivity", "onCreate called")
    }

    private fun setupIconRecyclerView() {
        binding.rvIconKelas.apply {
            layoutManager = GridLayoutManager(applicationContext, 5)
            adapter = iconAdapter
        }
    }

    private fun getIcon() {
        api.get_icon().enqueue(object : Callback<IconKelasModel> {
            override fun onResponse(call: Call<IconKelasModel>, response: Response<IconKelasModel>) {
                if (response.isSuccessful) {
                    val listData = response.body()?.iconKelas
                    listData?.let {
                        for (iconData in it) {
                            Log.d("TambahKelasActivity", "id: ${iconData.id}")
                            Log.d("TambahKelasActivity", "icon: ${iconData.icon_kelas}")
                        }
                        iconAdapter.setData(it)
                    }
                } else {
                    Log.e("TambahKelasActivity", "Failed to fetch data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<IconKelasModel>, t: Throwable) {
                Log.e("TambahKelasActivity", "API call failed: ${t.message}")
            }
        })
    }

    fun setupListener(){
        binding.buatKelas.setOnClickListener{
            val kelas = binding.namaKelas.text.toString()
            val grade = binding.namaGrade.text.toString()
            val deskripsi = binding.deskripsi.text.toString()

            val icon = Random.nextInt(1, 13)
            val user = 1
            val kode = "AGAGAGA"

            if (kelas.isNotEmpty()) {
                Log.e("TambahKelasActivity", kelas)
                api.create(kelas,grade,deskripsi,icon,user,kode)
                    .enqueue(object : Callback<TambahKelasModel> {
                    override fun onResponse(
                        p0: Call<TambahKelasModel>,
                        p1: Response<TambahKelasModel>
                    ) {
                        if (p1.isSuccessful) {
                            val submit = p1.body()
                            Toast.makeText(
                                applicationContext,
                                submit!!.message
                                , Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }

                    override fun onFailure(p0: Call<TambahKelasModel>, p1: Throwable) {

                    }

                })
            } else {
                Toast.makeText(applicationContext,"Tidak boleh kosong",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == mediaHelper.getRcGallery()){
                imStr = mediaHelper.getBitmapToString(data!!.data, binding.pvImg)
            }
        }
    }

}
