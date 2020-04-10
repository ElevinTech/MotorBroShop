package com.elevintech.motorbroshop.ShopView

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Shop
import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Utils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_shop_view.*

class ShopViewActivity : AppCompatActivity() {

    lateinit var shop: Shop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_view)

        // Add the shop data here
        shop = intent.getSerializableExtra("shop") as Shop

        if (shop.imageUrl != "") {
            Glide.with(this).load(shop.imageUrl).into(shopImageView)
        }

        shopName.text = shop.name.capitalize()
        shopDescription.text = shop.description.capitalize()

        val longDate: Long? = shop.dateEstablished.toLongOrNull()

        if (longDate != null) {
            calendarText.text =
                "Founded: " + Utils().convertMillisecondsToDate(longDate, "MMM d, yyyy")
        } else {
            calendarText.text = "Founded: Unkown"
        }

        mainLocationText.text = shop.address.capitalize()
        branchesLocationText.text = ""

        backButton.setOnClickListener {
            finish()
        }

        MotorBroDatabase().getShopProducts(shop.shopId) {

            var productsVar = ""
            for (product in it) {
                if (productsVar.isEmpty()) {
                    productsVar = product.brand + " " + product.type
                } else {
                    productsVar += ", " + product.brand + " " + product.type
                }
            }

            if (productsVar != "") {
                listOfPartsText.text = productsVar
            } else {
                listOfPartsText.text = "This Shop has no parts / services."
            }

        }

    }
}
