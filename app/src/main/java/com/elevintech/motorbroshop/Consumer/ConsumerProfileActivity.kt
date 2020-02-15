package com.elevintech.motorbroshop.Consumer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Consumer
import com.elevintech.motorbroshop.R
import kotlinx.android.synthetic.main.activity_consumer_profile.*

class ConsumerProfileActivity : AppCompatActivity() {

    lateinit var consumer: Consumer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consumer_profile)

        val consumerId = intent.getStringExtra("consumer")

        MotorBroDatabase().getConsumer(consumerId){
            consumer = it!!
            updateUI()
        }


    }

    private fun updateUI() {

        userNameToolbar.setText(consumer.firstName + " " + consumer.lastName)
        userNameFull.setText(consumer.firstName + " " + consumer.lastName)
    }
}
