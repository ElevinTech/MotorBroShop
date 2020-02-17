package com.elevintech.motorbroshop.Documents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Document
import com.elevintech.motorbroshop.Model.Document.DocumentType.BIR
import com.elevintech.motorbroshop.Model.Document.DocumentType.DTI
import com.elevintech.motorbroshop.Model.Document.DocumentType.MAYORS_PERMIT
import com.elevintech.motorbroshop.R
import kotlinx.android.synthetic.main.activity_documents.*

class DocumentsActivity : AppCompatActivity() {

    var shopId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documents)

        shopId = intent.getStringExtra("shopId")!!


        dti.setOnClickListener {

            goToEditDocument(DTI)

        }

        mayorsPermit.setOnClickListener {

            goToEditDocument(MAYORS_PERMIT)

        }

        bir.setOnClickListener {

            goToEditDocument(BIR)

        }

        gloveboxBackImageView.setOnClickListener {
            finish()
        }

    }

    fun goToEditDocument(documentType: String){
        val intent = Intent(this, AddEditDocument::class.java)
        intent.putExtra("shopId", shopId)
        intent.putExtra("documentType", documentType)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        val db = MotorBroDatabase()

        db.getDocument(DTI, shopId){
            if (it != null){
                dtiHasAdded.visibility = View.VISIBLE
                dtiNoData.visibility = View.GONE
            }
            else {
                dtiHasAdded.visibility = View.GONE
                dtiNoData.visibility = View.VISIBLE
            }
        }

        db.getDocument(MAYORS_PERMIT, shopId){
            if (it != null){
                mayorPermitHasAdded.visibility = View.VISIBLE
                mayorPermitNoData.visibility = View.GONE
            }
            else {
                mayorPermitHasAdded.visibility = View.GONE
                mayorPermitNoData.visibility = View.VISIBLE
            }
        }

        db.getDocument(BIR, shopId){
            if (it != null){
                birHasAdded.visibility = View.VISIBLE
                birNoData.visibility = View.GONE
            }
            else {
                birHasAdded.visibility = View.GONE
                birNoData.visibility = View.VISIBLE
            }
        }

    }
}
