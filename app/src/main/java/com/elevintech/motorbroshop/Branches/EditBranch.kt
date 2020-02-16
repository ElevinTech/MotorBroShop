package com.elevintech.motorbroshop.Branches

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Branch
import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Utils
import kotlinx.android.synthetic.main.activity_add_edit_branch.*

class EditBranch : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_branch)

        val shopId = intent.getStringExtra("shopId")!!
        val branchId = intent.getStringExtra("branchId")!!

        MotorBroDatabase().getBranch(shopId, branchId){ branch ->

            updateUI(branch)

            saveBranchButton.setOnClickListener {

                // SHOW PROGRESS DIALOG
                val progressDialog = Utils().easyProgressDialog(this, "Saving Branch")
                progressDialog.show()

                branch.address = addressEditText.text.toString()
                branch.name = branchNameEditText.text.toString()
                branch.contactNumber = contactNumberEditText.text.toString()
                branch.imageUrl = ""
                branch.email = emailEditText.text.toString()
                branch.isMain = (isMainSwitch.isChecked)

                MotorBroDatabase().saveBranch(shopId, branch){
                    progressDialog.dismiss()
                    finish()
                }

            }

        }


    }

    private fun updateUI(branch: Branch) {

        addressEditText.setText(branch.address)
        branchNameEditText.setText(branch.name)
        contactNumberEditText.setText(branch.contactNumber)
        emailEditText.setText(branch.email)

        if (branch.imageUrl != "") Glide.with(this).load(branch.imageUrl).into(branchPhoto)
        if (branch.isMain) isMainSwitch.isChecked = true

    }


}
