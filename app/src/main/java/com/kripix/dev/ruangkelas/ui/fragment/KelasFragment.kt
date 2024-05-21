package com.kripix.dev.ruangkelas.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
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
import com.kripix.dev.ruangkelas.ui.activity.TambahKelasActivity
import com.kripix.dev.ruangkelas.logic.bg.MenuBackgroundItem
import com.kripix.dev.ruangkelas.logic.bg.bgAdapter
import com.kripix.dev.ruangkelas.databinding.FragmentKelasBinding
import com.kripix.dev.ruangkelas.logic.kelas.KELAS_ID_EKSTRA
import com.kripix.dev.ruangkelas.logic.kelas.Kelas
import com.kripix.dev.ruangkelas.logic.kelas.Pertemuan
import com.kripix.dev.ruangkelas.logic.kelas.kelasAdapter
import com.kripix.dev.ruangkelas.logic.kelas.kelasClickListener
import com.kripix.dev.ruangkelas.logic.kelas.kelasList
import com.kripix.dev.ruangkelas.logic.music.BottomSheetMusic
import com.kripix.dev.ruangkelas.logic.music.MenuMusicItem
import com.kripix.dev.ruangkelas.ui.activity.NotificationActivity
import com.kripix.dev.ruangkelas.ui.activity.RuangActivity

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class KelasFragment : Fragment(), kelasClickListener {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentKelasBinding
    private lateinit var intentIntegrator: IntentIntegrator
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mediaPlayer: MediaPlayer

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
        daftarKelas()
        setupListeners()
        setupQRCodeScanner()
        setupKelasRecyclerView()
        loadBackground()
        return binding.root
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

    private fun setupKelasRecyclerView() {
        binding.rvKelas.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = kelasAdapter(kelasList, this@KelasFragment)
        }
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

    private fun daftarKelas ()
    {
        val pertemuan0 = listOf(
            Pertemuan(1,0,1,"Pertemuan 1 Mobile", "Deskripsi pertemuan 1 Pemrograman Mobile"),
            Pertemuan(2,0,2,"Pertemuan 2 Mobile", "Deskripsi pertemuan 2 Pemrograman Mobile"),
            Pertemuan(3,0,3,"Pertemuan 3 Mobile", "Deskripsi pertemuan 3 Pemrograman Mobile"),
            Pertemuan(4,0,4,"Pertemuan 4 Mobile", "Deskripsi pertemuan 4 Pemrograman Mobile"),
            Pertemuan(5,0,5,"Pertemuan 5 Mobile", "Deskripsi pertemuan 5 Pemrograman Mobile"),
            Pertemuan(6,0,6,"Pertemuan 6 Mobile", "Deskripsi pertemuan 6 Pemrograman Mobile"),
            Pertemuan(7,0,7,"Pertemuan 7 Mobile", "Deskripsi pertemuan 7 Pemrograman Mobile"),
            Pertemuan(8,0,8,"Pertemuan 8 Mobile", "Deskripsi pertemuan 8 Pemrograman Mobile"),
            Pertemuan(9,0,9,"Pertemuan 9 Mobile", "Shared Preferences"),
            Pertemuan(10,0,10,"Pertemuan 10 Mobile", "Multiplayer"),
            Pertemuan(11,0,11,"Multiplayer", "Deskripsi Multiplayer"),
            Pertemuan(12,0,12,"Pertemuan 12 Mobile", "Deskripsi pertemuan 12 Pemrograman Mobile"),
            Pertemuan(13,0,13,"Pertemuan 13 Mobile", "Deskripsi pertemuan 13 Pemrograman Mobile"),
            )
        val pertemuan1 = listOf(
            Pertemuan(1, 1, 1, "Pertemuan 1 Matematika", "Deskripsi pertemuan 1 Matematika"),
            Pertemuan(2, 1, 2, "Pertemuan 2 Matematika", "Deskripsi pertemuan 2 Matematika"),
            Pertemuan(3, 1, 3, "Pertemuan 1 Matematika", "Deskripsi pertemuan 1 Matematika"),
            Pertemuan(4, 1, 4, "Pertemuan 1 Matematika", "Deskripsi pertemuan 1 Matematika"),
            Pertemuan(5, 1, 5, "Pertemuan 2 Matematika", "Deskripsi pertemuan 2 Matematika"),
            Pertemuan(6, 1, 6, "Pertemuan 1 Matematika", "Deskripsi pertemuan 1 Matematika"),
            Pertemuan(7, 1, 7, "Pertemuan 1 Matematika", "Deskripsi pertemuan 1 Matematika"),
            Pertemuan(8, 1, 8, "Pertemuan 2 Matematika", "Deskripsi pertemuan 2 Matematika"),
            Pertemuan(9, 1, 9, "Pertemuan 1 Matematika", "Deskripsi pertemuan 1 Matematika"),
            Pertemuan(10, 1, 10, "Pertemuan 1 Matematika", "Deskripsi pertemuan 1 Matematika"),
            Pertemuan(11, 1, 11, "Pertemuan 2 Matematika", "Deskripsi pertemuan 2 Matematika"),
            Pertemuan(12, 1, 12, "Pertemuan 1 Matematika", "Deskripsi pertemuan 1 Matematika"),
            Pertemuan(13, 1, 13, "Pertemuan 2 Matematika", "Deskripsi pertemuan 2 Matematika")
        )

        val pertemuan2 = listOf(
            Pertemuan(1, 2, 1, "Pertemuan 1 Bahasa Inggris", "Deskripsi pertemuan 1 Bahasa Inggris"),
            Pertemuan(2, 2, 2, "Pertemuan 2 Bahasa Inggris", "Deskripsi pertemuan 2 Bahasa Inggris")
        )

        val pertemuan3 = listOf(
            Pertemuan(1, 3, 1, "Pertemuan 1 Biologi", "Deskripsi pertemuan 1 Biologi"),
            Pertemuan(2, 3, 2, "Pertemuan 2 Biologi", "Deskripsi pertemuan 2 Biologi")
        )
        val kelas0 = Kelas(
            R.drawable.icon_mobile_1, "MI 2E", "Mobile", "Creator", pertemuan0)
        kelasList.add(kelas0)
        val kelas1 = Kelas(
            R.drawable.icon_matematika_1, "X IPA 1", "Matematika", "John Doe",pertemuan1)
        kelasList.add(kelas1)
        val kelas2 = Kelas(
            R.drawable.icon_bahasa_1, "XI IPS 2", "Bahasa Inggris", "Jane Doe",pertemuan2)
        kelasList.add(kelas2)
        val kelas3 = Kelas(
            R.drawable.icon_biologi_1, "XI IPS 2", "Biologi", "Jane Doe",pertemuan3)
        kelasList.add(kelas3)
    }

}