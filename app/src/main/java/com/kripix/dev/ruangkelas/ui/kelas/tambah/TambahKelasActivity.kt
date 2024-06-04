package com.kripix.dev.ruangkelas.ui.kelas.tambah

import IconKelasAdapter
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
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
import com.kripix.dev.ruangkelas.data.model.IconUploadResponse
import com.kripix.dev.ruangkelas.data.model.TambahKelasModel
import com.kripix.dev.ruangkelas.databinding.ActivityTambahKelasBinding
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import kotlin.random.Random

class TambahKelasActivity : AppCompatActivity(), IconClickListener {

    private lateinit var binding: ActivityTambahKelasBinding
    private lateinit var mediaHelper: MediaHelper
    private lateinit var iconAdapter: IconKelasAdapter
    private val api by lazy { KelasApiRetrofit().endpoint }

    private var selectedIconId: Int? = null
    private var imStr = ""

    val user = 1

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

        mediaHelper = MediaHelper(this)
        iconAdapter = IconKelasAdapter(arrayListOf(), this)
        setupListener()
        getIcon()
        setupIconRecyclerView()
    }

    private fun setupListener() {
        with(binding) {
            buatKelas.setOnClickListener { createKelas() }
            btnBack.setOnClickListener { finish() }
            pvImg.setOnClickListener {

                selectedIconId = null
                iconAdapter.clearSelection()
                intentGallery()
            }
        }
    }

    private fun intentGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
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
                    response.body()?.iconKelas?.let { iconAdapter.setData(it) }
                } else {
                    Log.e("TambahActivity", "Failed to fetch data: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<IconKelasModel>, t: Throwable) {
                Log.e("TambahActivity", "API call failed: ${t.message}")
            }
        })
    }

    private fun createKelas() {
        val kelas = binding.namaKelas.text.toString()
        val grade = binding.namaGrade.text.toString()
        val deskripsi = binding.deskripsi.text.toString()

        if (kelas.isNotEmpty()) {
            selectedIconId?.let {
                createKelasWithIcon(it, kelas, grade, deskripsi, user)
            } ?: run {
                if (imStr.isNotEmpty()) {
                    uploadIconAndCreateKelas(kelas, grade, deskripsi, user)
                    Log.d("imStr", imStr)
                } else {
                    Toast.makeText(applicationContext, "Icon tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(applicationContext, "Nama kelas tidak boleh kosong", Toast.LENGTH_SHORT).show()
        }
    }



    private fun createKelasWithIcon(icon: Int, kelas: String, grade: String, deskripsi: String, user: Int) {
        api.create(kelas, grade, deskripsi, icon, user).enqueue(object : Callback<TambahKelasModel> {
            override fun onResponse(call: Call<TambahKelasModel>, response: Response<TambahKelasModel>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_SHORT).show()
                    Log.d("CekCreate", "icon: $icon, kelas: $kelas, grade: $grade, deskripsi: $deskripsi, user: $user")
                    finish()
                }
            }
            override fun onFailure(call: Call<TambahKelasModel>, t: Throwable) {
                Log.e("TambahKelasActivity", "API call failed: ${t.message}")
            }
        })
    }

    private fun uploadIconAndCreateKelas(kelas: String, grade: String, deskripsi: String, user: Int) {
        val file = File(imStr)
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        api.upload_icon(body).enqueue(object : Callback<IconUploadResponse> {
            override fun onResponse(call: Call<IconUploadResponse>, response: Response<IconUploadResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        createKelasWithIcon(it.iconId, kelas, grade, deskripsi, user)
                        Log.d("CekCreate", "icon: ${it.iconId}, kelas: $kelas, grade: $grade, deskripsi: $deskripsi, user: $user")
                    }
                } else {
                    Log.e("TambahKelasActivity", "Failed to upload icon: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<IconUploadResponse>, t: Throwable) {
                Log.e("TambahKelasActivity", "API call failed: ${t.message}")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == mediaHelper.getRcGallery()) {
                data?.data?.let {
                    val filePath = mediaHelper.getRealPathFromURI(it)
                    if (filePath != null) {
                        imStr = filePath
                        binding.pvImg.setImageBitmap(BitmapFactory.decodeFile(filePath))
                    }
                }
            }
        }
    }

    override fun onClick(icon: IconKelasModel.Data) {
        selectedIconId = icon.id
        Picasso.get().load(icon.icon_kelas).into(binding.pvImg)
    }
}

