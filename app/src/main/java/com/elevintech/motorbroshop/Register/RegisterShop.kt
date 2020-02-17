package com.elevintech.motorbroshop.Register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Shop
import com.elevintech.motorbroshop.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register_shop.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import android.Manifest
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.StrictMode
import com.github.florent37.runtimepermission.RuntimePermission
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import com.elevintech.motorbroshop.Utils
import java.util.*
import java.io.File
import java.text.DecimalFormat


class RegisterShop : AppCompatActivity() {

    var imageUri: Uri? = null
    var OPEN_CAMERA = 10
    var OPEN_GALLERY = 11

    lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener
    var dateEstablished = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_shop)

        // uri exposure fix
        var builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        saveShopButton.setOnClickListener {
            if (hasCompletedValues()) {
                createShop()
            }
        }

        imgMainProfile.setOnClickListener {
            askUploadSource()
        }

        shopDateEstablished.setOnClickListener {
            setDatePickerAction(it as EditText)
            openDatePicker()
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

    private fun createShop() {

        var shop = Shop()
        shop.shopId = FirebaseFirestore.getInstance().collection("shops").document().id
        shop.name = shopNameEditText.text.toString()
        shop.ownerId = FirebaseAuth.getInstance().uid!!
        shop.address = shopAddressEditText.text.toString()
        shop.dateEstablished = dateEstablished
        shop.description = shopDescriptionEditText.text.toString()

        val progressDialog = Utils().easyProgressDialog(this, "Registering shop...")
        progressDialog.show()

        MotorBroDatabase().uploadImageToFirebaseStorage(imageUri!!){ imageUrl ->
            shop.imageUrl = imageUrl

            MotorBroDatabase().createShop(shop){

                progressDialog.dismiss()

                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }

        }


    }

    fun hasCompletedValues(): Boolean {

        if (shopNameEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the shop name field", Toast.LENGTH_LONG).show()
            return false
        }

        if (shopAddressEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the shop address field", Toast.LENGTH_LONG).show()
            return false
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please upload your shop's image", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun setDatePickerAction(view: EditText){


        // SET ACTION AFTER SELECTING THE DATE
        mDateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->

            // FORMAT  DAY and MONTH to always show two digits (add zero if single digit)
            var mFormat = DecimalFormat("00");
            var monthString = mFormat.format(month + 1)
            var monthInWords = Utils().monthInWords(monthString)
            var dayString = mFormat.format(day)

            dateEstablished = Utils().convertDateToMilliseconds(year, month, day,12, 0, 0).toString()

            // PUT THE SELECTED DATE ON THE DATE PLACEHOLDER
            view.setText( monthInWords + " " + dayString + " " + year )


        }

    }

    private fun openDatePicker() {

        // INSTANTIATE CALENDAR
        var cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, 0)

        var year = cal.get(Calendar.YEAR)
        var month = cal.get(Calendar.MONTH)
        var day = cal.get(Calendar.DAY_OF_MONTH)

        // INSTANTIATE DATE PICKER DIALOG
        var dialog = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day)

        // SET DATE PICKER DIALOG STYLE
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // SHOW THE DATE PICKER DIALOG
        dialog.show()

    }
}
