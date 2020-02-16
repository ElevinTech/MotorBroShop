package com.elevintech.motorbroshop.Branches

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        MotorBroDatabase().getBranch(shopId, branchId){ branch ->
            updateUi(branch)


        }


    }

    private fun updateUi(branch: Branch) {

        branchNameText.text = branch.name
        branchAddress.text = branch.address
        branchContactNumber.text = branch.contactNumber
        branchEmail.text = branch.email

    }
}
