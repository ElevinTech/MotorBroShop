package com.elevintech.motorbroshop.Branches

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Address
import com.elevintech.motorbroshop.Model.Branch
import com.elevintech.motorbroshop.Model.Shop
import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Register.SelectLocation
import com.elevintech.motorbroshop.Utils
import com.github.florent37.runtimepermission.RuntimePermission
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_edit_branch.*
import java.io.File
import java.util.*

class EditBranch : AppCompatActivity() {

    var imageUri: Uri? = null
    var OPEN_CAMERA = 10
    var OPEN_GALLERY = 11

    var SELECT_LOCATION = 12
    var address = Address()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_branch)

        val shopId = intent.getStringExtra("shopId")!!
        val branchId = intent.getStringExtra("branchId")!!

        imgMainProfile.setOnClickListener {
            askUploadSource()
        }

        backButton.setOnClickListener {
            finish()
        }

        addressEditText.setOnClickListener {
            val intent = Intent(this, SelectLocation::class.java)
            startActivityForResult(intent, SELECT_LOCATION)
        }

        MotorBroDatabase().getBranch(branchId){ branch ->

            address = branch.fullAddress
            updateUI(branch)

            saveBranchButton.setOnClickListener {

                saveBranch(shopId, branch)

            }

        }

        saveBranchButton.text = "Update Branch"


    }

    private fun saveBranch(shopId: String, branch: Branch){
        // SHOW PROGRESS DIALOG
        val progressDialog = Utils().easyProgressDialog(this, "Saving Branch")
        progressDialog.show()

        branch.shopId = shopId
        branch.name = branchNameEditText.text.toString()
        branch.description = descriptionEditText.text.toString()
        branch.address = addressEditText.text.toString()
        branch.fullAddress = address
        branch.searchTags = createListOfSearchTag()
        branch.contactNumber = contactNumberEditText.text.toString()
        branch.email = emailEditText.text.toString()
        branch.ownerId = FirebaseAuth.getInstance().uid!! // get the logged in user ID, since only owners can create branches TODO: restrict employees from creating branches

        if(imageUri != null){

            MotorBroDatabase().uploadImageToFirebaseStorage(imageUri!!){ imageUrl ->
                branch.imageUrl = imageUrl

                MotorBroDatabase().saveBranch(branch){
                    progressDialog.dismiss()
                    finish()
                }

            }

        } else {

            MotorBroDatabase().saveBranch(branch){
                progressDialog.dismiss()
                finish()
            }


        }


    }

    private fun createListOfSearchTag(): ArrayList<String> {
        val province = stringToWords(address.province) // sample value: ["Ilocos", "Norte"]
        val city = stringToWords(address.city) // sample value: ["San", "Nicolas"]
        val street = stringToWords(address.street)// sample value: ["Lentils", "drive"]
        val shopName = stringToWords(branchNameEditText.text.toString()) // sample value: ["Harley", "Davidson"]
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

    private fun updateUI(branch: Branch) {

        addressEditText.setText(branch.address)
        branchNameEditText.setText(branch.name)
        contactNumberEditText.setText(branch.contactNumber)
        emailEditText.setText(branch.email)
        descriptionEditText.setText(branch.description)

        if (branch.imageUrl != "") {
            Glide.with(this).load(branch.imageUrl).into(branchPhoto)
            branchPhoto.visibility = View.VISIBLE
            emptyImageIcon.visibility = View.INVISIBLE
        }



//        if (branch.isMain) isMainSwitch.isChecked = true

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

            branchPhoto.setImageURI(imageUri)
            branchPhoto.visibility = View.VISIBLE
            emptyImageIcon.visibility = View.GONE

        }

        if (requestCode == SELECT_LOCATION && data!=null){
            address = data.getSerializableExtra("address")!! as Address
            addressEditText.setText( address.province + ", " + address.city +  ", " + address.street )
        }

    }



}
