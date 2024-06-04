package com.kripix.dev.ruangkelas.ui.kelas.tambah

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.kripix.dev.ruangkelas.R
import com.kripix.dev.ruangkelas.data.helper.PhotoHelper
import com.kripix.dev.ruangkelas.databinding.ActivityPhotoBinding
import com.permissionx.guolindev.PermissionX
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PhotoActivity: AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityPhotoBinding
    lateinit var photoHelper: PhotoHelper

    val url = "http://192.168.210.92/ruangKelas/api/kelas/upload_foto.php"
    var imstr: String = ""
    var namaFile: String = ""
    lateinit var fileUri: Uri

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_ambil -> {
                requestPermission()
            }
            R.id.btn_kirim -> {
                uploadFile()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
            m.invoke(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        photoHelper = PhotoHelper()
        binding.btnAmbil.setOnClickListener(this)
        binding.btnKirim.setOnClickListener(this)
    }

    fun uploadFile() {
        val request = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val kode = jsonObject.getString("kode")
                if (kode == "000") {
                    Toast.makeText(this, "Upload sukses", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Upload gagal", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                Toast.makeText(this, "Volley Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String, String>()
                hm["imstr"] = imstr
                hm["namaFile"] = namaFile
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }


    fun requestPermission() {
        PermissionX.init(this)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "Izin dibutuhkan untuk mengakses foto", "OK", "Tolak")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    fileUri = photoHelper.getOutputMediaFileUri()
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
                    startActivityForResult(intent, photoHelper.getRcCamera())
                } else {
                    Toast.makeText(this, "Permissions denied", Toast.LENGTH_LONG).show()
                }
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == photoHelper.getRcCamera()) {
                imstr = photoHelper.getBitMapToString(binding.ivPhoto, fileUri)
                namaFile = photoHelper.getMyFileName()
            }
        }
    }
}

