package com.elevintech.motorbroshop.Register

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Employee
import com.elevintech.motorbroshop.Model.ShopOwner
import com.elevintech.motorbroshop.Model.User
import com.elevintech.motorbroshop.Model.UserType
import com.elevintech.motorbroshop.R
import com.github.florent37.runtimepermission.RuntimePermission
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register_employee.*
import java.io.File
import java.util.*

class RegisterEmployee : AppCompatActivity() {

    var imageUri: Uri? = null
    var OPEN_CAMERA = 10
    var OPEN_GALLERY = 11
    lateinit var employee: Employee

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_employee)

        employee = intent.getSerializableExtra("employee") as Employee

        createAccountButton.setOnClickListener {

            if (hasCompletedValues())
                registerUser()

        }

        imgMainProfile.setOnClickListener {
            askUploadSource()
        }

        getShop()

    }

    fun getShop(){

        employeeName.text = employee.firstName

        MotorBroDatabase().getShop(employee.shopId){ shop ->

            if (employee.shopId == employee.branchId){

                shopEditText.text = shop.name
                branchEditText.text = "Main Branch"
            }

            else {

                MotorBroDatabase().getBranch(employee.branchId){ branch ->
                    shopEditText.text = shop.name
                    branchEditText.text = branch.name
                }

            }

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
            .addOnCompleteListener(this) { task ->

                // SUCCESSFUL CREATE OF ACCOUNT
                if (task.isSuccessful) {

                    // GET DEVICE TOKEN
                    firebaseDatabase.getDeviceToken { token ->

                        // SAVE THE USER DOCUMENT
                        val uid = task.result!!.user!!.uid
                        val employeeId = employee.employeeId
                        val user = UserType( employeeId, UserType.Type.EMPLOYEE, token)
                        firebaseDatabase.createNewUser(uid, user){

                            // UPLOAD IMAGE
                            firebaseDatabase.uploadImageToFirebaseStorage(imageUri!!) { imageUrl ->

                                // UPDATE THE EMPLOYEE DOCUMENT
                                firebaseDatabase.updateEmployeeFields(employeeId, email, uid, imageUrl){

                                    progressDialog.dismiss()
                                    val intent = Intent(applicationContext, DashboardActivity::class.java)
                                    startActivity(intent)

                                    finish()

                                }

                            }

                        }

                    }



                } else {

                    // FAILED CREATE OF ACCOUNT
                    Toast.makeText(baseContext, "${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            }

    }

    fun hasCompletedValues(): Boolean {

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

        if (confirmPasswordEditText.text.toString() != passwordEditText.text.toString()) {
            Toast.makeText(this, "Please check that the password you entered are the same", Toast.LENGTH_LONG).show()
            return false
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_LONG).show()
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
