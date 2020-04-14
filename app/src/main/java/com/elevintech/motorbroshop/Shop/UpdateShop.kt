package com.elevintech.motorbroshop.Shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbroshop.R
import com.google.firebase.firestore.FirebaseFirestore
import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.StrictMode
import com.github.florent37.runtimepermission.RuntimePermission
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Address
import com.elevintech.motorbroshop.Model.Shop
import com.elevintech.motorbroshop.Register.SelectLocation
import com.elevintech.motorbroshop.Utils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_update_shop.*
import java.util.*
import java.io.File
import java.text.DecimalFormat

class UpdateShop : AppCompatActivity() {

    var imageUri: Uri? = null
    var OPEN_CAMERA = 10
    var OPEN_GALLERY = 11
    var SELECT_LOCATION = 12

    lateinit var shop: Shop

    lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener
    var dateEstablished = ""
    var address = Address()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_shop)

        // uri exposure fix
        var builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        saveShopButton.setOnClickListener {
            if (hasCompletedValues()) {
                saveShop()
            }
        }

        imgMainProfile.setOnClickListener {
            askUploadSource()
        }

        shopDateEstablished.setOnClickListener {
            setDatePickerAction(it as EditText)
            openDatePicker()
        }

        val shopId = intent.getStringExtra("shopId")
        MotorBroDatabase().getShop(shopId){
            shop = it
            updateUi(it)
        }

        backButton.setOnClickListener {
            finish()
        }

        shopAddressEditText.setOnClickListener {

            val intent = Intent(this, SelectLocation::class.java)
            startActivityForResult(intent, SELECT_LOCATION)

        }
    }

    private fun updateUi(shop: Shop) {
        shopNameEditText.setText(shop.name.capitalize())
        shopAddressEditText.setText(shop.address.capitalize())
        shopDateEstablished.setText( Utils().convertMillisecondsToDate(shop.dateEstablished.toLong(),"MMM d yyyy") )
        shopDescriptionEditText.setText(shop.description)

        if (shop.imageUrl != ""){
            Glide.with(this).load(shop.imageUrl).into(mainProfilePhoto)
            mainProfilePhoto.visibility = View.VISIBLE
            emptyImageIcon.visibility = View.GONE
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

        if (requestCode == OPEN_GALLERY && data!= null)
            imageUri = data.data

        if (resultCode == RESULT_OK && imageUri != null) {

            // do something with the image here
            // example: imageView.setImageURI(imageUri)
            mainProfilePhoto.setImageURI(imageUri)
            mainProfilePhoto.visibility = View.VISIBLE
            emptyImageIcon.visibility = View.GONE

        }

        if (requestCode == SELECT_LOCATION && data!=null){

            address = data.getSerializableExtra("address")!! as Address

            shopAddressEditText.setText( address.province + ", " + address.city +  ", " + address.street )

        }

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

    private fun saveShop() {

        var updatedShop = Shop()
        updatedShop.shopId = shop.shopId
        updatedShop.imageUrl = shop.imageUrl
        updatedShop.name = shopNameEditText.text.toString()
        updatedShop.ownerId = FirebaseAuth.getInstance().uid!!
        updatedShop.address = shopAddressEditText.text.toString()
        updatedShop.dateEstablished = if (dateEstablished != ""){ dateEstablished } else { shop.dateEstablished }
        updatedShop.description = shopDescriptionEditText.text.toString()
        updatedShop.fullAddress = address
        updatedShop.searchTags = createListOfSearchTag()

        val progressDialog = Utils().easyProgressDialog(this, "Updating Shop...")
        progressDialog.show()

        if (imageUri != null){

            MotorBroDatabase().uploadImageToFirebaseStorage(imageUri!!){ imageUrl ->
                updatedShop.imageUrl = imageUrl

                MotorBroDatabase().updateShop(updatedShop){
                    progressDialog.dismiss()
                    finish()
                }

            }

        } else {

            MotorBroDatabase().updateShop(updatedShop){
                progressDialog.dismiss()
                finish()
            }

        }



    }

    private fun createListOfSearchTag(): ArrayList<String> {
        val province = stringToWords(address.province) // sample value: ["Ilocos", "Norte"]
        val city = stringToWords(address.city) // sample value: ["San", "Nicolas"]
        val street = stringToWords(address.street)// sample value: ["Lentils", "drive"]
        val shopName = stringToWords(shopNameEditText.text.toString()) // sample value: ["Harley", "Davidson"]
        val returnList = ArrayList<String>()

        returnList.addAll(province)
        returnList.addAll(city)
        returnList.addAll(street)
        returnList.addAll(shopName)

        return returnList
    }

    private fun stringToWords(mnemonic: String): List<String> {
        val words = ArrayList<String>()
        for (w in mnemonic.trim(' ').split(" ")) {
            if (w.isNotEmpty()) {
                words.add(w.toLowerCase())
            }
        }
        return words
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

        return true
    }


}
