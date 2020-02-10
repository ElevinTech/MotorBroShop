package com.elevintech.motorbroshop.Login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Register.RegisterAccount
import com.elevintech.motorbroshop.Register.RegisterOwner
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        createAccountText.setOnClickListener {
            val intent = Intent(applicationContext, RegisterOwner::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            doLogin()
        }
    }

    fun doLogin(){

        if (userNameEditText.text.toString() == "" || passwordEditText.text.toString() == ""){
            Toast.makeText(baseContext, "Please Fill up the username and password field", Toast.LENGTH_SHORT).show()
        } else {

            var progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Logging in....")
            progressDialog.setCancelable(false)
            progressDialog.show()

            FirebaseAuth.getInstance().signInWithEmailAndPassword("${userNameEditText.text}", "${passwordEditText.text}")
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        progressDialog.hide()

                        val intent = Intent(applicationContext, DashboardActivity::class.java)
                        startActivity(intent)
                    } else {
                        progressDialog.hide()
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }
}
