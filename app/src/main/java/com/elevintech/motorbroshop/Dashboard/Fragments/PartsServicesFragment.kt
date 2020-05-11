package com.elevintech.motorbroshop.Dashboard.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.AddPartsServices.AddPartsServicesActivity
import com.elevintech.motorbroshop.AddPartsServices.AddPartsServicesForCustomerActivity
import com.elevintech.motorbroshop.Chat.ChatListActivity
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Product
import com.elevintech.motorbroshop.Model.User

import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Utils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_parts_services.*
//import kotlinx.android.synthetic.main.fragment_parts_services.chatImageView
import kotlinx.android.synthetic.main.row_product.view.*

/**
 * A simple [Fragment] subclass.
 */
class PartsServicesFragment : Fragment() {

    lateinit var user: User
    var customersListAdapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        user = (activity as DashboardActivity).user

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parts_services, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        floating_button.setOnClickListener {
            val intent = Intent(activity, AddPartsServicesForCustomerActivity::class.java)
            intent.putExtra("shopId", user.shopId)
            startActivity(intent)
//            askWhoPartIsFor()
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recycler_view_type_of_parts.layoutManager = LinearLayoutManager(activity)
        recycler_view_type_of_parts.isNestedScrollingEnabled = true
        recycler_view_type_of_parts.adapter = customersListAdapter
    }

    private fun askWhoPartIsFor() {
        val options = arrayOf(" Add to inventory (default)", "Assign to a customer")
        val builder = android.app.AlertDialog.Builder(activity)
        builder.setTitle("What are you creating the item for?")
        builder.setItems(options){ _, which ->

            if(which == 0){

                val intent = Intent(activity, AddPartsServicesActivity::class.java)
                intent.putExtra("shopId", user.shopId)
                startActivity(intent)

            }else if(which == 1){

                val intent = Intent(activity, AddPartsServicesForCustomerActivity::class.java)
                intent.putExtra("shopId", user.shopId)
                startActivity(intent)

            }
        }
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        customersListAdapter.clear()
        getProducts()
    }


    private fun getProducts() {
        MotorBroDatabase().getShopProducts(user.shopId){
            displayProducts(it)
        }
    }

    private fun displayProducts(customersList: MutableList<Product>) {
        if (customersList.isNotEmpty()){
            for(customers in customersList){
                customersListAdapter.add(customerItem(customers))
            }

            if (noDataFullLayout != null){
                noDataFullLayout.visibility = View.GONE
            }

        }
    }

    inner class customerItem(val product: Product): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            var isShopProduct = "Shop - "

            if (product.isShopProduct == false) {
                /*isShopProduct = "Customer - "*/

                MotorBroDatabase().getCustomer(product.customerId){ customer ->
                    isShopProduct = "${customer!!.firstName} ${customer!!.lastName} - "
                    viewHolder.itemView.partsName.text = isShopProduct + product.brand + " " + product.type
                }
            } else {
                viewHolder.itemView.partsName.text = isShopProduct + product.brand + " " + product.type
            }

//            viewHolder.itemView.odometerText.text = product.odometer.toString() + "km"
            viewHolder.itemView.cashText.text = " ₱" + product.price
//            viewHolder.itemView.noteText.text = product.description

//            if (product.isShopProduct == false) {
//                if (product.customerId != "") {
//                    MotorBroDatabase().getCustomerByUid(product.customerId) {
//                        var userDescription = it
//                    }
//                }
//            }

            if (!product.dateCreated.isEmpty()) {
                viewHolder.itemView.dateText.text = Utils().convertMillisecondsToDate(product.dateCreated, "MMM d, yyyy")
            }

//            if(product.dateLong != 0.toLong()) {
//                viewHolder.itemView.dateText.text = Utils().convertMillisecondsToDate(product.dateLong, "MMM d, yyyy")
//            }
            //viewHolder.itemView.typeOfParts.text =
            // TODO: Fix thiss
            if (product.imageUrl != "") {
                viewHolder.itemView.productImage.visibility = View.VISIBLE
                Glide.with(activity as DashboardActivity).load(product.imageUrl).into(viewHolder.itemView.productImage)
            } else {
                viewHolder.itemView.productImage.visibility = View.GONE
            }

            playAnimation(viewHolder.itemView, position)


        }

        private fun playAnimation(itemView: View, position: Int) {

            itemView.visibility = View.GONE

            Utils().doAfterDelay((position * 300).toLong()){

                if (itemView != null && activity!= null){
                    val rightToLeft = AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left)
                    rightToLeft.duration = 250
                    itemView.startAnimation(rightToLeft)
                    itemView.visibility = View.VISIBLE
                }


            }

        }

        override fun getLayout(): Int {
            return R.layout.row_product

        }
    }

}
