package com.elevintech.motorbroshop.Login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.User
import com.elevintech.motorbroshop.Model.UserType
import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Register.SelectUserType
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.iid.FirebaseInstanceId


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        createAccountText.setOnClickListener {
            val intent = Intent(applicationContext, SelectUserType::class.java)
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
                .addOnSuccessListener {

                    checkUserType(progressDialog)

                }.addOnFailureListener {  e ->

                    progressDialog.dismiss()
                    Toast.makeText(baseContext, "Authentication failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()

                }

        }
    }

    private fun checkUserType(progressDialog: ProgressDialog) {
        MotorBroDatabase().getUserType{ userType ->

            progressDialog.dismiss()

            if (userType == UserType.Type.CUSTOMER){
                Toast.makeText(baseContext, "Please use the MotorBroConsumer app", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
            } else {
                getDeviceToken( userType )

                val intent = Intent(applicationContext, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
    }

    private fun getDeviceToken( userType: String ) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get new Instance ID token
                    val token = task.result?.token!!
                    MotorBroDatabase().getUserCommonData { user ->
                        if (user.token != token){
                            MotorBroDatabase().updateFcmToken(token)
                        }
                    }

                } else {
                    println("getInstanceId failed" + task.exception)
                }

            })
    }

}
