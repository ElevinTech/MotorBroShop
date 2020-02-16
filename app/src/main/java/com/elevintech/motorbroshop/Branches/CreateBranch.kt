package com.elevintech.motorbroshop.Branches

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Branch
import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Utils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_edit_branch.*

class CreateBranch : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_branch)

        var shopId = intent.getStringExtra("shopId")!!

        saveBranchButton.setOnClickListener {

            // SHOW PROGRESS DIALOG
            val progressDialog = Utils().easyProgressDialog(this, "Saving Branch")
            progressDialog.show()

            var branch = Branch()
            branch.id = FirebaseFirestore.getInstance().collection("shops").document(shopId).collection("branches").document().id
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
