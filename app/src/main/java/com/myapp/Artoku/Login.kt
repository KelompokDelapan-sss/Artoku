package com.myapp.Artoku

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.myapp.Artoku.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set warna status bar
        window.statusBarColor = getColor(R.color.hijau) // Ganti R.color.green dengan warna hijau di `res/values/colors.xml`

        // Ubah ikon status bar menjadi terang (ikon putih)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                0, // Tidak ada flag (ikon terang)
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() // Hapus ikon gelap
        }

        // Tombol buat akun
        binding.createAccount.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        // Tombol lupa kata sandi
        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
        }

        emailLogin()
    }

    private fun emailLogin() {
        binding.loginBtn.setOnClickListener {

            val email = binding.email.text.toString()
            val pass = binding.password.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()){
                binding.progressBar.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful){ //if the login successful, then change activity to main activity
                        val intent = Intent(this, MainActivity::class.java)
                        Toast.makeText(this,"Masuk sukses bre", Toast.LENGTH_LONG).show()
                        binding.progressBar.visibility = View.GONE
                        startActivity(intent)
                    }else{
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this,"Masuk gagal bre", Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                Toast.makeText(this, "Kolom kosong tidak diperbolehkan", Toast.LENGTH_LONG).show()
            }

        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            Intent(this, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // tujuan flag agar tidak bisa menggunakan back
                startActivity(it)
            }
        }
    }
}

