package com.app.notesappfirebase.authentication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.app.notesappfirebase.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        setupViews()
    }

    private fun setupViews() {
        binding.run {
            tvSignUp.setOnClickListener {
                startActivity(
                    Intent(
                        this@ForgotPasswordActivity,
                        SignUpActivity::class.java
                    )
                )
                finish()
            }
            tvLoginAccount.setOnClickListener {
                startActivity(
                    Intent(
                        this@ForgotPasswordActivity,
                        LoginActivity::class.java
                    )
                )
                finish()
            }

            btnResetPassword.setOnClickListener {
                val email = etForgotEmail.text.toString()
                if (email.isEmpty()) {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Email cannot be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    hideKeyboard()
                    return@setOnClickListener
                }

                if (!isValidEmail(email)) {
                    etForgotEmail.error = "Invalid Email"
                    return@setOnClickListener

                } else {
                    etForgotEmail.error = null
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Success, please check your email!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                            startActivity(
                                Intent(
                                    this@ForgotPasswordActivity,
                                    LoginActivity::class.java
                                )
                            )
                        } else {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Email wrong or not existed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }

                hideKeyboard()
            }


        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}