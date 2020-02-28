package com.elevintech.motorbroshop.Customer

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Customer
import com.elevintech.motorbroshop.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_customer_selector.*
import kotlinx.android.synthetic.main.activity_customer_selector.view.*
import kotlinx.android.synthetic.main.activity_customer_selector.view.recycler_view
import kotlinx.android.synthetic.main.row_customer.view.*

class CustomerSelectorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_selector)

        getCustomers()

    }

    private fun getCustomers() {

        val shopId = intent.getStringExtra("shopId")

        MotorBroDatabase().getShopCustomers(shopId){
            displayCustomers(it)
        }

    }

    private fun displayCustomers(customersList: MutableList<Customer>) {

        var customersListAdapter = GroupAdapter<ViewHolder>()
        recycler_view.adapter = customersListAdapter

        recycler_view.layoutManager = LinearLayoutManager(this)


        if (customersList.isNotEmpty()){
            for(customers in customersList){
                customersListAdapter.add(customerItem(customers))
            }
        }


    }

    inner class customerItem(val customer: Customer): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.customerName.text = customer.firstName + " " + customer.lastName
            viewHolder.itemView.setOnClickListener {
                val returnIntent = Intent()
                returnIntent.putExtra("selectedCustomer", customer)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }

        override fun getLayout(): Int {
            return R.layout.row_customer

        }
    }



}
