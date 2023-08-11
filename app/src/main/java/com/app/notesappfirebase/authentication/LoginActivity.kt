package com.app.notesappfirebase.authentication

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.notesappfirebase.broadcast.ConnectivityReceiver
import com.app.notesappfirebase.databinding.ActivityLoginBinding
import com.app.notesappfirebase.home.NoteActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var connectivityReceiver: ConnectivityReceiver
    private val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            finish()
            startActivity(Intent(this@LoginActivity, NoteActivity::class.java))
        }

        connectivityReceiver = ConnectivityReceiver()
        registerReceiver(connectivityReceiver, filter)

//        if (isConnectedToInternet()) {
//            setupViews()
//        } else {
//            val snackBar = Snackbar.make(
//                binding.root,
//                "No internet connection available.",
//                Snackbar.LENGTH_LONG
//            )
//            snackBar.show()
//        }
    }

    private fun setupViews() {
        binding.run {
            tvSignUp.setOnClickListener {
                val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            }
            tvResetPassword.setOnClickListener {
                startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
                finish()
            }

            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                if (email.isEmpty() && password.isEmpty()) {
                    etEmail.error = "Invalid Email!"
                    etPassword.error = "Password must be at least 6 characters!"
                    return@setOnClickListener
                }
                if (isValidEmail(email)) {
                    etEmail.error = null
                } else {
                    etEmail.error = "Invalid Email!"
                    return@setOnClickListener
                }
                if (isValidPassword(password)) {
                    etPassword.error = null
                } else {
                    etPassword.error = "Password must be at least 6 characters!"
                    return@setOnClickListener
                }

                progressBarLogin.visibility = View.VISIBLE

                if (isValidEmail(email) && isValidPassword(password)) {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            checkMailVerification()
                            progressBarLogin.visibility = View.INVISIBLE
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Wrong Email or Password",
                                Toast.LENGTH_SHORT
                            ).show()
                            progressBarLogin.visibility = View.INVISIBLE
                        }
                    }
                }
                hideKeyboard()
            }
        }
    }

    private fun checkMailVerification() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            if (firebaseUser.isEmailVerified) {
                Toast.makeText(this@LoginActivity, "Login successfully", Toast.LENGTH_SHORT).show()
                finish()
                startActivity(Intent(this@LoginActivity, NoteActivity::class.java))
            } else {
                Toast.makeText(this@LoginActivity, "Email not verified", Toast.LENGTH_SHORT).show()
                firebaseAuth.signOut()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun handleInternetConnection(isConnected: Boolean) {
        if (isConnected) {
            // Khi có kết nối internet, thực hiện các thao tác cần thiết
            setupViews()
        } else {
            // Khi không có kết nối internet, hiển thị thông báo hoặc xử lý tùy ý
            val snackBar = Snackbar.make(
                binding.root,
                "No internet connection available.",
                Snackbar.LENGTH_LONG
            )
            snackBar.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
    }

}