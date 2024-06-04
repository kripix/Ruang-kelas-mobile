package com.kripix.dev.ruangkelas.ui.kelas.pertemuan.ubah

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
import androidx.recyclerview.widget.RecyclerView
import com.kripix.dev.ruangkelas.R
import com.kripix.dev.ruangkelas.data.helper.MediaHelper
import com.kripix.dev.ruangkelas.data.api.kelas.KelasApiRetrofit
import com.kripix.dev.ruangkelas.data.model.IconKelasModel
import com.kripix.dev.ruangkelas.data.model.KELAS_DATA_EXTRA
import com.kripix.dev.ruangkelas.data.model.KelasModel
import com.kripix.dev.ruangkelas.data.model.TambahKelasModel
import com.kripix.dev.ruangkelas.databinding.ActivityUbahKelasBinding
import com.kripix.dev.ruangkelas.ui.kelas.tambah.IconClickListener
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class UbahKelasActivity : AppCompatActivity(), IconClickListener {
    private lateinit var binding: ActivityUbahKelasBinding
    private lateinit var mediaHelper: MediaHelper
    private lateinit var iconAdapter: IconKelasAdapter
    private val api by lazy { KelasApiRetrofit().endpoint }
    private lateinit var kelas: KelasModel.Data
    var imStr = ""
    private var selectedIconId: Int? = null
    var id: Int = 0
    var kode: String = null.toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUbahKelasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mediaHelper = MediaHelper(this)

        iconAdapter = IconKelasAdapter(arrayListOf(),this)
        setupListener()
        validation()
        getIcon()
        setupIconRecyclerView()

    }

    fun setupListener(){
        with(binding) {
            btnUpdate.setOnClickListener{updateKelas()}
            btnBack.setOnClickListener {finish()}
            pvImg.setOnClickListener{intentGallery()}
        }
    }

    private fun validation(){
        val kelasData = intent.getSerializableExtra(KELAS_DATA_EXTRA) as KelasModel.Data
        if (kelasData != null) {
            kelas = kelasData
            setupView(kelas)
        } else {
            Toast.makeText(this, "Failed to retrieve class data", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupView(kelas: KelasModel.Data) {
        id = kelas.id
        kode = kelas.kode_kelas.toString()
        binding.namaKelas.setText(kelas.nama_kelas)
        binding.namaGrade.setText(kelas.nama_grade)
        binding.deskripsi.setText(kelas.deskripsi)
        Picasso.get().load(kelas.icon_kelas).into(binding.pvImg)
    }

    fun intentGallery(){
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, mediaHelper.getRcGallery())
    }

    private fun setupIconRecyclerView() {
        binding.rvIcKelas.apply {
            layoutManager = GridLayoutManager(applicationContext, 2, RecyclerView.HORIZONTAL, false)
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
                            Log.e("TambahActivity", "id: ${iconData.id}")
                            Log.e("TambahActivity", "icon: ${iconData.icon_kelas}")
                        }
                        iconAdapter.setData(it)
                    }
                } else {
                    Log.e("TambahActivity", "Failed to fetch data: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<IconKelasModel>, t: Throwable) {
                Log.e("TambahActivity", "API call failed: ${t.message}")
            }
        })
    }



    private fun updateKelas() {
        val kelas = binding.namaKelas.text.toString()
        val grade = binding.namaGrade.text.toString()
        val deskripsi = binding.deskripsi.text.toString()

        val icon = selectedIconId ?: Random.nextInt(1, 10)
        val user = 1

        if (kelas.isNotEmpty()) {
            api.update(id, kelas, grade, deskripsi, icon, user, kode)
                .enqueue(object : Callback<TambahKelasModel> {
                    override fun onResponse(call: Call<TambahKelasModel>, response: Response<TambahKelasModel>) {
                        if (response.isSuccessful) {
                            val submit = response.body()
                            Toast.makeText(applicationContext, submit!!.message, Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK) // Set result to OK
                            finish()
                        }
                    }

                    override fun onFailure(call: Call<TambahKelasModel>, t: Throwable) {
                        // Handle the error
                    }
                })
        } else {
            Toast.makeText(applicationContext, "Tidak boleh kosong", Toast.LENGTH_SHORT).show()
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

    override fun onClick(icon: IconKelasModel.Data) {
        selectedIconId = icon.id
        Picasso.get().load(icon.icon_kelas).into(binding.pvImg)
    }
}
