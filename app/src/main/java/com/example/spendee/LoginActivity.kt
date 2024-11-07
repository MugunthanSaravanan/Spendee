package com.example.spendee

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.util.Patterns

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupTextView: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupTextView = findViewById(R.id.signupTextView)
        progressBar = findViewById(R.id.progressBar)

        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            loginUser()
        }

        signupTextView.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return
        }

        // Show progress bar while logging in
        progressBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressBar.visibility = View.GONE // Hide the progress bar after the operation
                if (task.isSuccessful) {
                    // Login successful, save the login status
                    saveLoginStatus(true)

                    // Open MainActivity
                    Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish() // Finish LoginActivity to prevent it from appearing in the back stack
                } else {
                    // Authentication failed, show the error message
                    val errorMessage = task.exception?.message ?: "Authentication Failed"
                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Function to save the login status in SharedPreferences
    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", isLoggedIn)
        editor.apply()
    }
}
