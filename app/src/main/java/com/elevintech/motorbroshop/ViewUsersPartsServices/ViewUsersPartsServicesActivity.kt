package com.elevintech.motorbroshop.ViewUsersPartsServices

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.Customer.CustomerProfileActivity
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.BikeParts
import com.elevintech.motorbroshop.Model.Customer
import com.elevintech.motorbroshop.Model.Product
import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Utils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_view_users_parts_services.*
import kotlinx.android.synthetic.main.row_product.view.*
import kotlinx.android.synthetic.main.row_product.view.dateText
import kotlinx.android.synthetic.main.row_product.view.productImage
import kotlinx.android.synthetic.main.row_user_parts.view.*

class ViewUsersPartsServicesActivity : AppCompatActivity() {

    lateinit var customer: Customer

    var partsAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_users_parts_services)

        customer = intent.getSerializableExtra("customer") as Customer

        partsHeader.text = "${customer.firstName.capitalize()}'s Parts / Services"
        setupRecyclerView()

        getUserPartsServices()

        btnBack.setOnClickListener {
            finish()
        }
    }

    fun setupRecyclerView() {
        recycler_view_parts.layoutManager = LinearLayoutManager(this)
        recycler_view_parts.isNestedScrollingEnabled = true
        recycler_view_parts.adapter = partsAdapter
    }

    private fun getUserPartsServices() {
        MotorBroDatabase().getUserBikeParts(customer.uid){
            displayProducts(it)
        }
    }

    private fun displayProducts(partsList: MutableList<BikeParts>) {
        if (partsList.isNotEmpty()){
            for(part in partsList){
                partsAdapter.add(PartsItem(part))
            }

//            if (noDataFullLayout != null){
//                noDataFullLayout.visibility = View.GONE
//            }
        } else {
            noBikes.visibility = View.VISIBLE
        }
    }

    inner class PartsItem(val bikePart: BikeParts): Item<ViewHolder>() {


        override fun bind(viewHolder: ViewHolder, position: Int) {
//            viewHolder.itemView.partsName.text = bikePart.typeOfParts
            viewHolder.itemView.odometerText.text = "Brand: " + bikePart.brand
//            viewHolder.itemView.cashText.text = " ₱" + bikePart.price.toString()


            if (bikePart.shopId != "") {
                viewHolder.itemView.partsShopName.visibility = View.VISIBLE

                MotorBroDatabase().getShop(bikePart.shopId) {
                    if (it.name != "") {
                        viewHolder.itemView.partsShopName.text = "Shop: " + it.name
                    } else {
                        viewHolder.itemView.partsShopName.text = "Error getting name"
                    }

                }

            }


            val dateString = Utils().convertMillisecondsToDate(bikePart.dateLong * 1000, "MMM d, yyyy")

            viewHolder.itemView.partsNameCombine.text = "$dateString - ${bikePart.typeOfParts} - ${bikePart.brand} - ${bikePart.price}"
            viewHolder.itemView.partsOdoCombine.text = "Odo = ${bikePart.odometer} km"

            var note = ""

            if (bikePart.note.isEmpty()) {
                note = "No note"
            } else {
                note = bikePart.note
            }

            viewHolder.itemView.noteText.text = note

            if (bikePart.dateLong != 0.toLong()) {
                viewHolder.itemView.dateText.text = dateString
            } else {
                viewHolder.itemView.dateText.text = "Date not supported"
            }


            if (bikePart.bikeId != "") {
                MotorBroDatabase().getBikeById(bikePart.bikeId) {
                    viewHolder.itemView.primaryBikeName.text = "Bike: " + it.yearBought + " " + it.brand + " " + it.model
                }
            }

//            if (bikePart.createdByShop){
//                MotorBroDatabase().getShop(bikePart.shopId) {
//                    viewHolder.itemView.createdByShopLayout.visibility = View.VISIBLE
//                    viewHolder.itemView.createdByShopText.text = "Shop: " + it.name
//                }
//            }

            if (bikePart.imageUrl.isNotEmpty()) {
                viewHolder.itemView.productImage.visibility = View.VISIBLE
                Glide.with(this@ViewUsersPartsServicesActivity).load(bikePart.imageUrl).into(viewHolder.itemView.productImage)
            } else {
                viewHolder.itemView.productImage.visibility = View.GONE
            }


            // Get date bought
        }

        override fun getLayout(): Int {
            return R.layout.row_user_parts
        }
    }


}
