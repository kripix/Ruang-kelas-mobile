package com.kripix.dev.ruangkelas.ui.kelas

import IconKelasAdapter
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.integration.android.IntentIntegrator
import com.kripix.dev.ruangkelas.R
import com.kripix.dev.ruangkelas.databinding.ActivityMainBinding
import com.kripix.dev.ruangkelas.data.api.kelas.KelasApiRetrofit
import com.kripix.dev.ruangkelas.data.model.KELAS_DATA_EXTRA
import com.kripix.dev.ruangkelas.data.model.KELAS_ID_EKSTRA
//import com.kripix.dev.ruangkelas.data.model.KELAS_LIST_EXTRA
import com.kripix.dev.ruangkelas.data.model.KelasModel
import com.kripix.dev.ruangkelas.data.model.UserModel
import com.kripix.dev.ruangkelas.ui.kelas.navbar.notifikasi.NotificationActivity
import com.kripix.dev.ruangkelas.ui.kelas.navbar.tema.MenuBackgroundItem
import com.kripix.dev.ruangkelas.ui.kelas.navbar.tema.bgAdapter
import com.kripix.dev.ruangkelas.ui.kelas.pertemuan.RuangActivity
import com.kripix.dev.ruangkelas.ui.kelas.tambah.IconClickListener
import com.kripix.dev.ruangkelas.ui.kelas.tambah.PhotoActivity
import com.kripix.dev.ruangkelas.ui.kelas.tambah.TambahKelasActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), KelasClickListener {

    private lateinit var binding: ActivityMainBinding


    private lateinit var intentIntegrator: IntentIntegrator
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mediaPlayer: MediaPlayer
    private val api by lazy { KelasApiRetrofit().endpoint }
    private lateinit var dialog: Dialog
    private lateinit var btn_join: Button
    private lateinit var btn_create: Button
    private var userId: Int = 0

    private lateinit var kelasAdapter: KelasAdapter
    private var originalKelasList: List<KelasModel.Data> = listOf()

    private val menuBackgroundList = listOf(
        MenuBackgroundItem("Sore hari", R.drawable.bg_0),
        MenuBackgroundItem("Kamping", R.drawable.bg_1),
        MenuBackgroundItem("Api unggun", R.drawable.bg_2),
        MenuBackgroundItem("Air terjun", R.drawable.bg_3),
        MenuBackgroundItem("Hutan", R.drawable.bg_4)
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        kelasAdapter = KelasAdapter(arrayListOf(),this)
        setupKelasRecyclerView()
        setupListeners()
        setupDialog()
//        setupQRCodeScanner()
        loadBackground()
    }



    override fun onStart(){
        super.onStart()
        getUser()
        getKelas()
    }

    private fun setupListeners() {
        with(binding) {
            btnBg.setOnClickListener { showMenuTheme() }
            btnNotif.setOnClickListener { startActivity(Intent(applicationContext, PhotoActivity::class.java)) }
            btnAddKelas.setOnClickListener { dialog.show() }
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { searchClasses(it) }
                    return true
                }
            })
        }
    }

    private fun setupDialog(){
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_kelas)
        dialog.getWindow()?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        btn_join = dialog.findViewById(R.id.btn_join)
        btn_create = dialog.findViewById(R.id.btn_create)
        val kodeKelas = dialog.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_code).text.toString()

        btn_join.setOnClickListener {

        }

        btn_create.setOnClickListener {
            startActivity(Intent(applicationContext, TambahKelasActivity::class.java))
            dialog.dismiss()
        }
    }

    private fun searchClasses(query: String) {
        val filteredList = if (query.isEmpty()) {
            originalKelasList
        } else {
            originalKelasList.filter { kelas ->
                kelas.nama_kelas?.contains(query, true) == true
            }
        }
        kelasAdapter.setData(filteredList)
    }


    private fun setupKelasRecyclerView() {
        binding.rvKelas.apply {
            layoutManager = GridLayoutManager(applicationContext, 3)
            adapter = kelasAdapter
        }
    }

    private fun getUser(){
        userId = 2
        api.get_user(userId).enqueue(object : Callback<UserModel> {
            override fun onResponse(p0: Call<UserModel>, p1: Response<UserModel>) {
                if (p1.isSuccessful) {
                    val listData = p1.body()?.user
                    listData?.let {
                        for (userData in it) {
                            binding.namaNavbar.text = userData.nama_lengkap
                        }
                    }
                }
            }

            override fun onFailure(p0: Call<UserModel>, p1: Throwable) {
            }
        })
    }


    private fun getKelas() {
        api.data(userId).enqueue(object : Callback<KelasModel> {
            override fun onResponse(call: Call<KelasModel>, response: Response<KelasModel>) {
                if (response.isSuccessful) {
                    val listData = response.body()?.kelas
                    listData?.let {
                        Log.d("KelasFragment", "Received ${it.size} items from API")
                        for (kelasData in it) {
                            Log.d("KelasFragment", "Kelas ID: ${kelasData.id}")
                            Log.d("KelasFragment", "Kelas Kode: ${kelasData.kode_kelas ?: "N/A"}")
                            Log.d("KelasFragment", "Kelas Nama: ${kelasData.nama_kelas ?: "N/A"}")
                            Log.d("KelasFragment", "Kelas Grade: ${kelasData.nama_grade ?: "N/A"}")
                            Log.d("KelasFragment", "Wali Kelas: ${kelasData.wali_kelas ?: "N/A"}")
                            Log.d("KelasFragment", "Deskripsi: ${kelasData.deskripsi ?: "N/A"}")
                            Log.d("KelasFragment", "Icon Kelas: ${kelasData.icon_kelas}")
                            Log.d("KelasFragment", "Created At: ${kelasData.created_at ?: "N/A"}")
                        }
                        originalKelasList = it
                        kelasAdapter.setData(it)
                    }
                } else {
                    Log.e("KelasFragment", "Failed to fetch data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<KelasModel>, t: Throwable) {
                Log.e("KelasFragment", "API call failed: ${t.message}")
            }
        })
    }




//    private fun setupQRCodeScanner() {
//        intentIntegrator = IntentIntegrator.forSupportFragment(this as Fragment)
//        binding.btnScanQr.setOnClickListener { intentIntegrator.setBeepEnabled(true).initiateScan() }
//    }

    private fun showMenuTheme() {
        val bottomSheetDialog = BottomSheetDialog(this) // Use 'this' instead of 'applicationContext'
        val view = layoutInflater.inflate(R.layout.bottom_sheet_option_bg, null)
        bottomSheetDialog.setContentView(view)
        val backgroundListView = view.findViewById<ListView>(R.id.lv_bg)
        val adapter = bgAdapter(this, menuBackgroundList) { selectedBackground -> // Use 'this' instead of 'applicationContext'
            Glide.with(this) // Use 'this' instead of 'applicationContext'
                .load(selectedBackground.image)
                .into(binding.backgroundHome)
            saveBackground(selectedBackground.image)
            bottomSheetDialog.dismiss()
        }
        backgroundListView.adapter = adapter
        bottomSheetDialog.show()
    }

    private fun saveBackground(backgroundImage: Int) {
        sharedPreferences.edit().putInt("background_image", backgroundImage).apply()
    }

    private fun loadBackground() {
        val backgroundImage = sharedPreferences.getInt("background_image", R.drawable.bg_0)
        Glide.with(applicationContext)
            .load(backgroundImage)
            .into(binding.backgroundHome)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            if (intentResult.contents != null) {

            } else {
                Toast.makeText(applicationContext, "Scanned code is not a valid class code", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(applicationContext, "Cancelled", Toast.LENGTH_LONG).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(kelas: KelasModel.Data) {
        val intent = Intent(applicationContext, RuangActivity::class.java).apply {
            putExtra(KELAS_ID_EKSTRA, kelas.id) // Pass the ID of the clicked class
            putExtra(KELAS_DATA_EXTRA, kelas)
        }
        startActivity(intent)
    }
}