package com.elevintech.motorbroshop.BikeView

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.BikeInfo
import com.elevintech.motorbroshop.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_bike_view.*

class BikeViewActivity : AppCompatActivity() {

    lateinit var bike: BikeInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bike_view)

        bike = intent.getSerializableExtra("bike") as BikeInfo

        getBike()

    }

    override fun onResume() {
        super.onResume()
        getBike()
    }

    fun getBike(){

        // the default bikes created at the start of registration do not have a bikeId
        // set it as the user ID
        // TODO: on registration set a value for the user ID
        if (bike.bikeId == "")
            bike.bikeId = FirebaseAuth.getInstance().uid!!

        MotorBroDatabase().getBikeById(bike.bikeId){ bike ->

            if (bike.imageUrl != "") {
                Glide.with(this).load(bike.imageUrl).into(bikeImageView)
            } else {
                // Put an empty image here
            }

            brandAndModelText.text = bike.brand.capitalize() + " " + bike.model.capitalize()
            plateNumberText.text = "Plate # " + bike.plateNumber.capitalize()
            nicknameText.text = "Nickname: " + bike.nickname
            yearBoughtText.text = "Year Bought: " + bike.yearBought
            brandText.text = bike.brand.capitalize()
            modelText.text = bike.model.capitalize()

            if (bike.primary)
                isPrimary.visibility = View.VISIBLE

            backView.setOnClickListener {
                finish()
            }

        }


    }
}
