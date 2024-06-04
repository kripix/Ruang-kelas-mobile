package com.kripix.dev.ruangkelas.ui.kelas.pertemuan

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.kripix.dev.ruangkelas.R
import com.kripix.dev.ruangkelas.data.api.kelas.KelasApiRetrofit
import com.kripix.dev.ruangkelas.data.model.KELAS_DATA_EXTRA
import com.kripix.dev.ruangkelas.databinding.ActivityRuangBinding
import com.kripix.dev.ruangkelas.data.model.KELAS_ID_EKSTRA
import com.kripix.dev.ruangkelas.data.model.KelasModel
import com.kripix.dev.ruangkelas.data.model.TambahKelasModel
import com.kripix.dev.ruangkelas.ui.kelas.pertemuan.ubah.UbahKelasActivity
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RuangActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRuangBinding
    private lateinit var kelas: KelasModel.Data
    private val api by lazy { KelasApiRetrofit().endpoint }

    companion object {
        private const val REQUEST_EDIT_KELAS = 1
    }

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

        validation()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnEdit.setOnClickListener { editKelas() }
        binding.btnMore.setOnClickListener { showPopupMenu() }
    }

    fun showPopupMenu() {
        val popupMenu = PopupMenu(this, binding.btnMore)
        popupMenu.menuInflater.inflate(R.menu.pop_menu_ruang, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.hapus -> {
                    showConfirmationDialog()
                    true // Return true to indicate that the menu item click is handled
                }
                else -> false // Return false for unhandled menu items
            }
        }
        popupMenu.show()
    }

    fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi")
        builder.setMessage("Apakah Anda yakin ingin menghapus?")
        builder.setPositiveButton("Ya") { dialog, which ->
            deleteKelas()
        }
        builder.setNegativeButton("Tidak") { dialog, which ->
            // Do nothing, close the dialog
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun deleteKelas() {
        api.delete(kelas.id).enqueue(object : Callback<TambahKelasModel> {
            override fun onResponse(call: Call<TambahKelasModel>, response: Response<TambahKelasModel>) {
                if (response.isSuccessful) {
                    val submit = response.body()
                    Toast.makeText(applicationContext, submit!!.message, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // Handle unsuccessful response here if needed
                }
            }

            override fun onFailure(call: Call<TambahKelasModel>, t: Throwable) {
                // Handle failure here if needed
            }
        })
    }



    private fun validation(){
        val kelasData = intent.getSerializableExtra(KELAS_DATA_EXTRA) as KelasModel.Data
        if (kelasData != null) {
            kelas = kelasData
            displayKelasInfo(kelas)
        } else {
            Toast.makeText(this, "Failed to retrieve class data", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayKelasInfo(kelas: KelasModel.Data) {
        binding.kelasGrade.text = kelas.nama_grade
        binding.kelasTitle.text = kelas.nama_kelas
        binding.kelasCreator.text = kelas.wali_kelas
        binding.kelasDeskripsi.text = kelas.deskripsi
        Picasso.get().load(kelas.icon_kelas).into(binding.kelasImg)
    }

    private fun editKelas() {
        val intent = Intent(this, UbahKelasActivity::class.java).apply {
            putExtra(KELAS_ID_EKSTRA, kelas.id)
            putExtra(KELAS_DATA_EXTRA, kelas)
        }
        startActivityForResult(intent, REQUEST_EDIT_KELAS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EDIT_KELAS && resultCode == Activity.RESULT_OK) {
            data?.let {
                val updatedKelas = it.getSerializableExtra(KELAS_DATA_EXTRA) as? KelasModel.Data
                updatedKelas?.let { kelas ->
                    this.kelas = kelas
                    displayKelasInfo(kelas)
                }
            }
        }
    }
}
