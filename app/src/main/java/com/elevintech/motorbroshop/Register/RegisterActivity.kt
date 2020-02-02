package com.elevintech.motorbroshop.Register

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Shop
import com.elevintech.motorbroshop.Model.ShopUser
import com.elevintech.motorbroshop.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var selectedShop: Shop = Shop()

    companion object {
        val SELECT_SHOP = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        shopEditText.setOnClickListener {

            val intent = Intent(applicationContext, SelectShopActivity::class.java)
            startActivityForResult(intent, SELECT_SHOP)

        }

        createAccountButton.setOnClickListener {
            if (hasCompletedValues()) {
                registerUser()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if (data != null){
                if (requestCode == SELECT_SHOP){
                    selectedShop = data!!.getSerializableExtra("selected") as Shop
                    shopEditText.setText(selectedShop.name)
                }
            }
        }
    }

    fun registerUser() {

        val firebaseDatabase = MotorBroDatabase()

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Registering your profile....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Samp", "createUserWithEmail:success")

                    val user = ShopUser(firstNameEditText.text.toString(), lastNameEditText.text.toString(), emailEditText.text.toString(), task.result!!.user!!.uid, selectedShop.shopId)

                    firebaseDatabase.registerUser(user) {
                        println("New Success")
                        progressDialog.hide()
                        val intent = Intent(applicationContext, DashboardActivity::class.java)
                        startActivity(intent)

                        finish()
                    }

                } else {
                    Toast.makeText(baseContext, "${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            }

    }

    fun hasCompletedValues(): Boolean {
        if (shopEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the shop name field", Toast.LENGTH_LONG).show()
            return false
        }

        if (firstNameEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the first name field", Toast.LENGTH_LONG).show()
            return false
        }

        if (lastNameEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the last name field", Toast.LENGTH_LONG).show()
            return false
        }

       /* if (!genderIsSelected) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_LONG).show()
            return false
        }*/

        if (emailEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the email field", Toast.LENGTH_LONG).show()
            return false
        }

        if (passwordEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the password field", Toast.LENGTH_LONG).show()
            return false
        }

        if (confirmPasswordEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the confirm password field", Toast.LENGTH_LONG).show()
            return false
        }

        if (passwordEditText.text.toString() != confirmPasswordEditText.text.toString()) {
            Toast.makeText(this, "Please make sure your password is the same as your confirm password text", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }
}
