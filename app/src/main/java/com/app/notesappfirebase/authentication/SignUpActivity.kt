package com.app.notesappfirebase.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.notesappfirebase.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun setupViews() {
        binding.run {
            tvLoginAccount.setOnClickListener {
                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            btnSignUp.setOnClickListener {
                val email = etNewEmail.text.toString()
                val password = etNewPassword.text.toString()
                val repeatPassword = etNewPasswordRepeat.text.toString()

                if (email.isEmpty() && password.isEmpty() && repeatPassword.isEmpty()) {
                    etNewEmail.error = "Invalid Email!"
                    etNewPassword.error = "Password must be at least 6 characters!"
                    etNewPasswordRepeat.error = "Password doesn't match!"
                    return@setOnClickListener
                }

                if (isValidEmail(email)) {
                    etNewEmail.error = null
                } else {
                    etNewEmail.error = "Invalid Email!"
                    return@setOnClickListener
                }

                if (isValidPassword(password)) {
                    etNewPassword.error = null
                } else {
                    etNewPassword.error = "Password must be at least 6 characters!"
                    return@setOnClickListener
                }

                if (password != repeatPassword) {
                    etNewPasswordRepeat.error = "Password doesn't match!"
                    return@setOnClickListener
                } else {
                    etNewPasswordRepeat.error = null
                }

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this@SignUpActivity, "Sign Up successfully", Toast.LENGTH_SHORT)
                            .show()
                        sendEmailVerification()
                    } else {
                        Toast.makeText(this@SignUpActivity, "Sign Up failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                hideKeyboard()
            }
        }
    }

    private fun sendEmailVerification() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener {
                Toast.makeText(
                    this@SignUpActivity,
                    "Verification Email was sent, login again",
                    Toast.LENGTH_SHORT
                ).show()
                firebaseAuth.signOut()
                startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                finish()

            }
        } else {
            Toast.makeText(
                this@SignUpActivity,
                "Verification failed",
                Toast.LENGTH_SHORT
            ).show()
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
}