package com.elevintech.motorbroshop.Documents

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.CustomCamera.CustomCamera
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Document.DocumentType.BIR
import com.elevintech.motorbroshop.Model.Document.DocumentType.DTI
import com.elevintech.motorbroshop.Model.Document.DocumentType.MAYORS_PERMIT
import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Utils
import kotlinx.android.synthetic.main.activity_add_edit_document.*
import com.github.florent37.runtimepermission.RuntimePermission
import java.io.File
import java.util.*

class AddEditDocument : AppCompatActivity() {

    var imageUri: Uri? = null
    val CAMERA_INTENT = 1
    val GALLERY_INTENT = 2

    var documentType = ""
    var shopId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_document)

        // uri exposure fix
        var builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        documentType = intent.getStringExtra("documentType")!!
        shopId = intent.getStringExtra("shopId")!!


        setActionBarTitle()
        setOnClickListeners()
        getExistingDocumet()


    }

    private fun setActionBarTitle() {
        if (documentType == DTI){
            actionBarTitle.text = "DTI"
        }

        if (documentType == MAYORS_PERMIT){
            actionBarTitle.text = "MAYOR'S PERMIT"
        }

        if (documentType == BIR){
            actionBarTitle.text = "BIR"
        }
    }

    private fun setOnClickListeners() {
        imageView.setOnClickListener {
            askUploadSource()
        }

        saveButton.setOnClickListener {

            if (imageUri != null){
                val dialogProgress = Utils().easyProgressDialog(this, "Uploading your document")
                dialogProgress.show()

                MotorBroDatabase().saveShopDocument(shopId, documentType, imageUri!!){
                    dialogProgress.dismiss()
                    finish()
                }
            }

        }
    }

    private fun getExistingDocumet() {
        MotorBroDatabase().getDocument(documentType, shopId){
            if (it != null){
                Glide.with(this).load(it.imageUrl).into(imageView)
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
                        openGallery(GALLERY_INTENT)

                    }
                    .ask()

            }else if(which == 1){

                // ASK PERMISSION TO OPEN CAMERA
                RuntimePermission.askPermission(this)
                    .request(Manifest.permission.CAMERA)
                    .onAccepted{

                        // OPEN CAMERA
                        openCamera(CAMERA_INTENT)
                    }
                    .ask()

            }
        }
        builder.show()
    }

    fun openCamera(requestCode: Int){


//        val intent = Intent(this, CustomCamera::class.java)
//        startActivityForResult(intent, requestCode)
//
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        var filename = UUID.randomUUID().toString() + ".jpg"
        var file = File(this.externalCacheDir, filename)
        imageUri = Uri.fromFile(file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, requestCode)
    }

    fun openGallery(requestCode: Int){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_INTENT && data!= null) imageUri = data!!.data

        if (resultCode == RESULT_OK && imageUri != null) {

            imageView.setImageURI(imageUri)

        }

    }
}
