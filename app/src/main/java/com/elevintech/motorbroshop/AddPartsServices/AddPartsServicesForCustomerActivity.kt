package com.elevintech.motorbroshop.AddPartsServices

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.elevintech.motorbroshop.Customer.CustomerSelectorActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.BikeParts
import com.elevintech.motorbroshop.Model.Customer
import com.elevintech.motorbroshop.Model.Product
import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Utils
import com.github.florent37.runtimepermission.RuntimePermission
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_parts_services.*
import java.io.File
import java.text.DecimalFormat
import java.util.*

class AddPartsServicesForCustomerActivity : AppCompatActivity() {

    var selectedCustomerId = ""
    var imageUri: Uri? = null
    var OPEN_CAMERA = 10
    var OPEN_GALLERY = 11
    var SELECT_CUSTOMER = 21

    var birthDayInMilliseconds = 0.toLong()
    lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_parts_services)

        customerSelectorLabel.visibility = View.VISIBLE
        customerSelectorLayout.visibility = View.VISIBLE

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

        dateText.setOnClickListener {
            setDatePickerAction()
            openDatePicker()
        }

        customerName.setOnClickListener {

            val shopId = intent.getStringExtra("shopId")
            val intent = Intent(this, CustomerSelectorActivity::class.java)
            intent.putExtra("shopId", shopId)
            startActivityForResult(intent, SELECT_CUSTOMER)

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

    private fun setDatePickerAction(){


        // SET ACTION AFTER SELECTING THE DATE
        mDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->

            // FORMAT  DAY and MONTH to always show two digits (add zero if single digit)
            var mFormat = DecimalFormat("00");
            var monthString = mFormat.format(month + 1)
            var dayString = mFormat.format(day)

            birthDayInMilliseconds = Utils().convertDateToMilliseconds(year,month,day,12, 0, 0)

            // PUT THE SELECTED DATE ON THE DATE PLACEHOLDER
            dateText.setText("${year}-${monthString}-${dayString}")

            // TODO: How to get current AGE??

            // If todays month is greater than current month
            // if todays day is greater than or equals to day

        }

    }

    private fun saveProduct() {

//        val shopId = intent.getStringExtra("shopId")
//
//        // SHOW PROGRESS DIALOG
//        val progressDialog = Utils().easyProgressDialog(this, "Saving Product...")
//        progressDialog.show()
//
//        MotorBroDatabase().uploadImageToFirebaseStorage(imageUri!!){ imageUrl ->
//            var product = Product()
//            product.name = nameEditText.text.toString()
//            product.description = descriptionEditText.text.toString()
//            product.price = priceEditText.text.toString()
//            product.imageUrl = imageUrl
//            product.type = typeEditText.text.toString()
//            product.brand = brandEditText.text.toString()
//            product.shopId = shopId
//            product.id = FirebaseFirestore.getInstance().collection("shops").document(shopId).collection("products").document().id
//            MotorBroDatabase().saveProduct(shopId, product){
//                progressDialog.dismiss()
//                finish()
//            }
//        }

        val showDialog = Utils().showProgressDialog(this, "Saving bike part")

        val bikeParts = BikeParts()
        bikeParts.date = dateText.text.toString()
        bikeParts.dateLong = Utils().convertDateToTimestamp(dateText.text.toString(), "yyyy-MM-dd")
        bikeParts.odometer = odometerText.text.toString().toDouble()
        bikeParts.typeOfParts = typeEditText.text.toString()
        bikeParts.brand = brandEditText.text.toString()
        bikeParts.price = priceEditText.text.toString().toDouble()
        bikeParts.note = descriptionEditText.text.toString()
        bikeParts.userId = FirebaseAuth.getInstance().uid!!
        bikeParts.createdByShop = true

        val database = MotorBroDatabase()
        database.saveBikeParts(bikeParts, selectedCustomerId) {
                showDialog.dismiss()
                Toast.makeText(this, "Successfully saved bike part", Toast.LENGTH_SHORT).show()
                finish()
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

        if (resultCode == RESULT_OK && data!= null && requestCode == SELECT_CUSTOMER) {

            val customer = data!!.getSerializableExtra("selectedCustomer") as Customer
            selectedCustomerId = customer.uid
            customerName.setText(capitalize(customer.firstName) + " " + capitalize(customer.lastName))

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

    fun capitalize(string: String) : String{
        return (string.toLowerCase()).capitalize()
    }
}
