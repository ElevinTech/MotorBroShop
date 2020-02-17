package com.elevintech.motorbroshop.Register

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.ShopOwner
import com.elevintech.motorbroshop.Model.ShopUser
import com.elevintech.motorbroshop.R
import com.github.florent37.runtimepermission.RuntimePermission
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register_owner.*
import java.io.File
import java.util.*

class RegisterOwner : AppCompatActivity() {

    var imageUri: Uri? = null
    var OPEN_CAMERA = 10
    var OPEN_GALLERY = 11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_owner)

        createAccountButton.setOnClickListener {
            if (hasCompletedValues()) {
                registerUser()
            }
        }

        imgMainProfile.setOnClickListener {
            askUploadSource()
        }

    }

    fun registerUser() {

        val auth = FirebaseAuth.getInstance()
        val firebaseDatabase = MotorBroDatabase()

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Registering your profile....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                    var user = ShopOwner()
                    user.uid        = it.user!!.uid
                    user.firstName  = firstNameEditText.text.toString()
                    user.lastName   = lastNameEditText.text.toString()
                    user.shopId     = "" // shop wil be created on the next step, account creation first
                    user.email      = emailEditText.text.toString()


                    firebaseDatabase.uploadImageToFirebaseStorage(imageUri!!){ imageUrl ->
                        user.profilePictureUrl = imageUrl

                        firebaseDatabase.createShopOwner(user) {

                            progressDialog.dismiss()
                            val intent = Intent(applicationContext, RegisterShop::class.java)
                            startActivity(intent)

                            finish()
                        }
                    }

            }.addOnFailureListener {  e ->

                progressDialog.dismiss()
                Toast.makeText(baseContext, "Creation failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()

            }

    }

    fun hasCompletedValues(): Boolean {

        if (firstNameEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the first name field", Toast.LENGTH_LONG).show()
            return false
        }

        if (lastNameEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the last name field", Toast.LENGTH_LONG).show()
            return false
        }

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

    private fun askUploadSource(){

        val options = arrayOf("Open Gallery", "Capture Photo")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Please pick your image source")
        builder.setItems(options){ _, which ->

            if(which == 0){

                // ASK PERMISSION TO OPEN GALLERY
                RuntimePermission.askPermission(this)
                    .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .onAccepted{

                        // OPEN GALLERY
                        openGallery()

                    }
                    .ask()

            }else if(which == 1){

                // ASK PERMISSION TO OPEN CAMERA
                RuntimePermission.askPermission(this)
                    .request(Manifest.permission.CAMERA)
                    .onAccepted{

                        // OPEN CAMERA
                        openCamera()
                    }
                    .ask()

            }
        }
        builder.show()
    }

    fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        var filename = UUID.randomUUID().toString() + ".jpg"
        var file = File(this.externalCacheDir, filename)
        imageUri = Uri.fromFile(file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, OPEN_CAMERA)
    }

    fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, OPEN_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == OPEN_GALLERY && data!= null) { imageUri = data!!.data }

        if (resultCode == RESULT_OK && imageUri != null) {

            // do something with the image here
            // example: imageView.setImageURI(imageUri)
            mainProfilePhoto.setImageURI(imageUri)
            mainProfilePhoto.visibility = View.VISIBLE
            emptyImageIcon.visibility = View.GONE

        }

    }
}
