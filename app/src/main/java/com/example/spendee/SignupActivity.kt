package com.example.spendee

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spendee.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private lateinit var signupEmailEditText: EditText
    private lateinit var signupPasswordEditText: EditText
    private lateinit var signupButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        signupEmailEditText = findViewById(R.id.signupEmailEditText)
        signupPasswordEditText = findViewById(R.id.signupPasswordEditText)
        signupButton = findViewById(R.id.signupButton)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        signupButton.setOnClickListener {
            createUser()
        }
    }

    private fun createUser() {
        val email = signupEmailEditText.text.toString().trim()
        val password = signupPasswordEditText.text.toString().trim()

        // Validate input fields
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Create user using FirebaseAuth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    // Save user info to Firebase Realtime Database
                    saveUserToDatabase(user?.uid ?: "", email)

                    Toast.makeText(this@SignupActivity, "Signup Successful", Toast.LENGTH_SHORT).show()
                    // Redirect to MainActivity after successful signup
                    startActivity(Intent(this@SignupActivity, MainActivity::class.java))
                    finish() // Close SignupActivity to prevent the user from navigating back
                } else {
                    Toast.makeText(this@SignupActivity, "Signup Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToDatabase(userId: String, email: String) {
        val user = User(email)
        databaseReference.child(userId).setValue(user)
    }

    // Data class to represent user
    data class User(val email: String) {
        constructor() : this("") // Default constructor required for Firebase
    }
}
