package com.kripix.dev.ruangkelas

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.compose.material3.ModalBottomSheet
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.kripix.dev.ruangkelas.bg.MenuBackgroundItem
import com.kripix.dev.ruangkelas.bg.bgAdapter
import com.kripix.dev.ruangkelas.databinding.FragmentKelasBinding
import com.kripix.dev.ruangkelas.kelas.kelas
import com.kripix.dev.ruangkelas.kelas.kelasAdapter

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [KelasFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class KelasFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentKelasBinding
    lateinit var intentIntegrator: IntentIntegrator

    val menuBackgroundList = listOf(
        MenuBackgroundItem("Sore hari", R.drawable.bg_0),
        MenuBackgroundItem("Kamping", R.drawable.bg_1),
        MenuBackgroundItem("Api unggun", R.drawable.bg_2),
        MenuBackgroundItem("Air terjun", R.drawable.bg_3),
        MenuBackgroundItem("Hutan", R.drawable.bg_4)
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentKelasBinding.inflate(inflater, container, false)
        intentIntegrator = IntentIntegrator.forSupportFragment(this)

        Glide.with(this).load(R.drawable.bg_0).into(binding.backgroundHome)
        // val bottomSheetbg = bottomSheetBg()

        binding.btnBg.setOnClickListener {
            showBottomSheetDialog()
        }

        binding.btnScanQr.setOnClickListener {
            intentIntegrator.setBeepEnabled(true).initiateScan()
        }

//        binding.btnBuatQr.setOnClickListener {
//            val kodeKelas = binding.kodeKelas.text.toString()
//
//            if (kodeKelas.isEmpty()) {
//                // Kode kelas kosong, tampilkan toast
//                Toast.makeText(requireActivity(), "Kode kelas tidak boleh kosong", Toast.LENGTH_SHORT).show()
//            } else {
//                val barcodeEndcoder = BarcodeEncoder()
//                val bitmap = barcodeEndcoder.encodeBitmap(kodeKelas, BarcodeFormat.QR_CODE, 220, 220)
//                binding.imgQr.setImageBitmap(bitmap)
//            }
//        }


        val kelasList = listOf(
            kelas(R.drawable.icon_matematika_1, "X IPA 1", "Matematika", "John Doe"),
            kelas(R.drawable.icon_bahasa_1, "XI IPS 2", "Bahasa Inggris", "Jane Doe"),
            kelas(R.drawable.icon_fisika_2, "XII IPA 3", "Fisika", "Alex Smith"),
            kelas(R.drawable.icon_matematika_1, "X IPA 1", "Matematika", "John Doe"),
            kelas(R.drawable.icon_bahasa_1, "XI IPS 2", "Bahasa Inggris", "Jane Doe"),
            kelas(R.drawable.icon_matematika_1, "X IPA 1", "Matematika", "John Doe"),
            kelas(R.drawable.icon_bahasa_1, "XI IPS 2", "Bahasa Inggris", "Jane Doe"),
            kelas(R.drawable.icon_matematika_1, "X IPA 1", "Matematika", "John Doe"),
            kelas(R.drawable.icon_bahasa_1, "XI IPS 2", "Bahasa Inggris", "Jane Doe"),            kelas(R.drawable.icon_matematika_1, "X IPA 1", "Matematika", "John Doe"),
            kelas(R.drawable.icon_bahasa_1, "XI IPS 2", "Bahasa Inggris", "Jane Doe"),
            kelas(R.drawable.icon_matematika_1, "X IPA 1", "Matematika", "John Doe"),
            kelas(R.drawable.icon_bahasa_1, "XI IPS 2", "Bahasa Inggris", "Jane Doe"),
            kelas(R.drawable.icon_matematika_1, "X IPA 1", "Matematika", "John Doe"),
            kelas(R.drawable.icon_bahasa_1, "XI IPS 2", "Bahasa Inggris", "Jane Doe"),
            kelas(R.drawable.icon_matematika_1, "X IPA 1", "Matematika", "John Doe"),
            kelas(R.drawable.icon_bahasa_1, "XI IPS 2", "Bahasa Inggris", "Jane Doe"),
            kelas(R.drawable.icon_matematika_1, "X IPA 1", "Matematika", "John Doe"),
            kelas(R.drawable.icon_bahasa_1, "XI IPS 2", "Bahasa Inggris", "Jane Doe"),
            kelas(R.drawable.icon_matematika_1, "X IPA 1", "Matematika", "John Doe"),
            kelas(R.drawable.icon_bahasa_1, "XI IPS 2", "Bahasa Inggris", "Jane Doe")

        )


        binding.rvKelas.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = kelasAdapter(kelasList)
        }


        return binding.root
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
            bottomSheetDialog.dismiss()
        }
        backgroundListView.adapter = adapter
        bottomSheetDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            if (intentResult.contents !== null) {
                val scannedCode = intentResult.contents
                // **Data Type Validation:**
                // Check if the scanned data format is plain text (replace with appropriate validation logic)
                if (intentResult.contents !== null) {
                    binding.kodeKelas.setText(scannedCode)
                } else {
                    Toast.makeText(requireContext(), "Scanned code is not a valid class code", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_LONG).show()
            }
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
}