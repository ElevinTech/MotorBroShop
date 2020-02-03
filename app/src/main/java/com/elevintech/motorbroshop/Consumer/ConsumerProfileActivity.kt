package com.elevintech.motorbroshop.Consumer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbroshop.Model.Consumer
import com.elevintech.motorbroshop.R
import kotlinx.android.synthetic.main.activity_consumer_profile.*

class ConsumerProfileActivity : AppCompatActivity() {

    private lateinit var consumer: Consumer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consumer_profile)

        consumer = intent.getSerializableExtra("consumer") as Consumer

        updateUI()

    }

    private fun updateUI() {

        consumerNameText.setText(consumer.firstName + " " + consumer.lastName)

    }
}
