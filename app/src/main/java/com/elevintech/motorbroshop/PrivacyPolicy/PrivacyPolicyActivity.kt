package com.elevintech.motorbroshop.PrivacyPolicy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbroshop.R
import kotlinx.android.synthetic.main.activity_terms_services.*

class PrivacyPolicyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        btnBack.setOnClickListener {
            finish()
        }
    }
}
