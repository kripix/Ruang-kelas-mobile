package com.kripix.dev.ruangkelas

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.kripix.dev.ruangkelas.databinding.ActivityMainBinding
import com.kripix.dev.ruangkelas.data.api.kelas.KelasApiRetrofit
import com.kripix.dev.ruangkelas.ui.kelas.KelasFragment
import com.kripix.dev.ruangkelas.ui.user.PengaturanFragment
import com.kripix.dev.ruangkelas.ui.todo.TodoFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val api by lazy { KelasApiRetrofit().endpoint }

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



        replaceFragment(KelasFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.kelas -> replaceFragment(KelasFragment())
                R.id.toDo -> replaceFragment(TodoFragment())
                R.id.settings -> replaceFragment(PengaturanFragment())
            }
            true
        }
    }private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main,fragment)
        fragmentTransaction.commit()
    }


}