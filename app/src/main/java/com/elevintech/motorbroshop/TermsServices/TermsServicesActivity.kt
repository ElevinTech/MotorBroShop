package com.elevintech.motorbroshop.TermsServices

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbroshop.R
import kotlinx.android.synthetic.main.activity_terms_services.*

class TermsServicesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_services)

        btnBack.setOnClickListener {
            finish()
        }
    }
}
