package com.elevintech.motorbroshop.AddPartsServices

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbroshop.R
import android.Manifest
import android.content.Intent
import com.github.florent37.runtimepermission.RuntimePermission
import android.provider.MediaStore
import java.util.*
import java.io.File
import android.net.Uri
import android.os.StrictMode
import android.view.View
import android.widget.Toast
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Branch
import com.elevintech.motorbroshop.Model.Product
import com.elevintech.motorbroshop.Utils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_parts_services.*

class AddPartsServicesActivity : AppCompatActivity() {

    var imageUri: Uri? = null
    var OPEN_CAMERA = 10
    var OPEN_GALLERY = 11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_parts_services)

        // uri exposure fix
        var builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        imgMainProfile.setOnClickListener {
            askUploadSource()
        }

        checkMarkButton.setOnClickListener {
            if (hasCompletedValues())
                saveProduct()
        }
    }

    private fun saveProduct() {

        val shopId = intent.getStringExtra("shopId")

        // SHOW PROGRESS DIALOG
        val progressDialog = Utils().easyProgressDialog(this, "Saving Product...")
        progressDialog.show()

        MotorBroDatabase().uploadImageToFirebaseStorage(imageUri!!){ imageUrl ->
            var product = Product()
            product.name = nameEditText.text.toString()
            product.description = descriptionEditText.text.toString()
            product.price = priceEditText.text.toString()
            product.imageUrl = imageUrl
            product.type = typeEditText.text.toString()
            product.brand = brandEditText.text.toString()
            product.shopId = shopId
            product.id = FirebaseFirestore.getInstance().collection("shops").document(shopId).collection("products").document().id
            MotorBroDatabase().saveProduct(shopId, product){
                progressDialog.dismiss()
                finish()
            }
        }


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

    fun hasCompletedValues(): Boolean {

        if (nameEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the Title field", Toast.LENGTH_LONG).show()
            return false
        }

//        if (dateText.text.isEmpty()) {
//            Toast.makeText(this, "Please fill up the Date field", Toast.LENGTH_LONG).show()
//            return false
//        }

        if (typeEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the Type of part field", Toast.LENGTH_LONG).show()
            return false
        }

        if (brandEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the Brand field", Toast.LENGTH_LONG).show()
            return false
        }

        if (priceEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the Price field", Toast.LENGTH_LONG).show()
            return false
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please fill up the Part type image field", Toast.LENGTH_LONG).show()
            return false
        }


        return true
    }
}
