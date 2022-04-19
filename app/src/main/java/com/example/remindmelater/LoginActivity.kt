package com.example.remindmelater

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private val oAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var emailExists = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener(){
            val email = txtUsername.text.toString()
            val password = txtPassword.text.toString()

            signIn(email, password)
        }

        btnSignUp.setOnClickListener(){
            val email = txtUsername.text.toString()
            val password = txtPassword.text.toString()

            registerUser(email, password)
        }
    }

    fun UsernameTextFieldClicked(view: View) {
        txtUsername.text.clear()
        txtUsername.requestFocus()
        Log.d("Username", "Cleared")
    }

    fun PasswordTextFieldClicked(view: View) {
        txtPassword.text.clear()
        txtPassword.requestFocus()
        Log.d("Password", "Cleared")
    }

    private fun signIn(email: String, password: String) {
        oAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
            when{
                it.isSuccessful -> {
                    Log.d("Sidned in as", email)
                    val mainScreen = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(mainScreen)
                }
                else -> {
                    Log.d("Sign in", "Failed")
                }
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        oAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
            when{
                it.isSuccessful -> {
                    signIn(email, password)
                    Log.d("Registered account with email", email)
                }
                else -> {
                    Log.d("Registration Failed for", email)
                }
            }
        }
    }

    private fun checkIfUserLoggedIn(email: String, password: String){
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                if (it.size() == 0) {
                    emailExists = false
                    Log.d("Email", "Not found")
                }else{
                    signIn(email, password)
                }
            }
    }

}