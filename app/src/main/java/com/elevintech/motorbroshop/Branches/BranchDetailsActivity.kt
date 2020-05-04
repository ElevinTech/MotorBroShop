package com.elevintech.motorbroshop.Branches

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Branch
import com.elevintech.motorbroshop.R
import kotlinx.android.synthetic.main.activity_branch_details.*

class BranchDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_branch_details)
    }

    override fun onResume() {
        super.onResume()

        val shopId = intent.getStringExtra("shopId")!!
        val branchId = intent.getStringExtra("branchId")!!

        editBranch.setOnClickListener {

            val intent = Intent(this, EditBranch::class.java)
            intent.putExtra("shopId", shopId)
            intent.putExtra("branchId", branchId)
            startActivity(intent)

        }

        MotorBroDatabase().getBranch(branchId){ branch ->
            updateUi(branch)
        }

        gloveboxBackImageView.setOnClickListener {

            finish()

        }

    }

    private fun updateUi(branch: Branch) {

        branchNameText.text = branch.name
        branchAddress.text = branch.address
        branchContactNumber.text = branch.contactNumber
        branchEmail.text = branch.email
        employeeCountEditText.text = branch.employeeCount.toString()
        customerCountEditText.text = branch.customerCount.toString()

        if(branch.imageUrl != "")
            Glide.with(this).load(branch.imageUrl).into(emptyImageIcon)



    }
}
