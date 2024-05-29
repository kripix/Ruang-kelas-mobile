package com.kripix.dev.ruangkelas.ui.kelas

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.integration.android.IntentIntegrator
import com.kripix.dev.ruangkelas.R
import com.kripix.dev.ruangkelas.ui.kelas.navbar.tema.MenuBackgroundItem
import com.kripix.dev.ruangkelas.ui.kelas.navbar.tema.bgAdapter
import com.kripix.dev.ruangkelas.databinding.FragmentKelasBinding
import com.kripix.dev.ruangkelas.data.api.kelas.KelasApiRetrofit
import com.kripix.dev.ruangkelas.data.model.KelasModel
import com.kripix.dev.ruangkelas.data.kelas.KELAS_ID_EKSTRA
import com.kripix.dev.ruangkelas.data.kelas.Kelas
import com.kripix.dev.ruangkelas.data.kelas.kelasClickListener
import com.kripix.dev.ruangkelas.data.music.BottomSheetMusic
import com.kripix.dev.ruangkelas.data.music.MenuMusicItem
import com.kripix.dev.ruangkelas.ui.kelas.navbar.notifikasi.NotificationActivity
import com.kripix.dev.ruangkelas.ui.kelas.pertemuan.RuangActivity
import com.kripix.dev.ruangkelas.ui.kelas.tambah.TambahKelasActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class KelasFragment : Fragment(), kelasClickListener {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentKelasBinding
    private lateinit var intentIntegrator: IntentIntegrator
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mediaPlayer: MediaPlayer

    private val api by lazy { KelasApiRetrofit().endpoint }
    private lateinit var kelasAdapter: KelasAdapter

    private val menuBackgroundList = listOf(
        MenuBackgroundItem("Sore hari", R.drawable.bg_0),
        MenuBackgroundItem("Kamping", R.drawable.bg_1),
        MenuBackgroundItem("Api unggun", R.drawable.bg_2),
        MenuBackgroundItem("Air terjun", R.drawable.bg_3),
        MenuBackgroundItem("Hutan", R.drawable.bg_4)
    )

    private val menuMusicList = listOf(
        MenuMusicItem("Music 1","Author 1", R.drawable.pf_icon_lk1, R.raw.music_1),
        MenuMusicItem("Music 2","Author 2", R.drawable.pf_icon_lk5, R.raw.music_2),
        MenuMusicItem("Music 3","Author 3", R.drawable.pf_icon_lk3, R.raw.music_3)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("background_pref", Context.MODE_PRIVATE)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        mediaPlayer = MediaPlayer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKelasBinding.inflate(inflater, container, false)
        kelasAdapter = KelasAdapter(arrayListOf())

        setupKelasRecyclerView()
        setupListeners()
        setupQRCodeScanner()
        loadBackground()
        return binding.root
    }

    override fun onStart(){
        super.onStart()
        getKelas()

    }

    private fun setupKelasRecyclerView() {
        binding.rvKelas.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = kelasAdapter
        }
    }

    private fun getKelas() {
        api.data().enqueue(object : Callback<KelasModel> {
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


    private fun setupListeners() {
        with(binding) {
            btnMp.setOnClickListener { showMusicMenu() }
            btnBg.setOnClickListener { showBottomSheetDialog() }
            btnNotif.setOnClickListener { startActivity(Intent(requireContext(), NotificationActivity::class.java)) }
            btnAddKelas.setOnClickListener { startActivity(Intent(requireContext(), TambahKelasActivity::class.java)) }
        }
    }

    private fun setupQRCodeScanner() {
        intentIntegrator = IntentIntegrator.forSupportFragment(this)
        binding.btnScanQr.setOnClickListener { intentIntegrator.setBeepEnabled(true).initiateScan() }
    }



    private fun showMusicMenu() {
        val bottomSheetMusic = BottomSheetMusic(requireContext(), menuMusicList)
        bottomSheetMusic.show()
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_option_bg, null)
        bottomSheetDialog.setContentView(view)
        val backgroundListView = view.findViewById<ListView>(R.id.lv_bg)
        val adapter = bgAdapter(requireContext(), menuBackgroundList) { selectedBackground ->
            Glide.with(requireContext())
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
        Glide.with(requireContext())
            .load(backgroundImage)
            .into(binding.backgroundHome)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            if (intentResult.contents != null) {
                binding.kodeKelas.setText(intentResult.contents)
            } else {
                Toast.makeText(requireContext(), "Scanned code is not a valid class code", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_LONG).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            KelasFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(kelas: Kelas) {
        val intent = Intent(requireContext(), RuangActivity::class.java).apply {
            putExtra(KELAS_ID_EKSTRA, kelas.id)
        }
        startActivity(intent)
    }


}