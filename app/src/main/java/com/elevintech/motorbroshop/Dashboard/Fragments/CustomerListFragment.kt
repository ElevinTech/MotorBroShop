package com.elevintech.motorbroshop.Dashboard.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Customer
import com.elevintech.motorbroshop.Model.User

import com.elevintech.motorbroshop.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_customer_list.*
import kotlinx.android.synthetic.main.row_customer.view.*

/**
 * A simple [Fragment] subclass.
 */
class CustomerListFragment : Fragment() {

    lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        user = (activity as DashboardActivity).user

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_list, container, false)
    }


    override fun onResume() {
        super.onResume()

        getCustomers()

    }

    private fun getCustomers() {

        MotorBroDatabase().getShopCustomers(user.shopId){

            displayCustomers(it)

        }

    }

    private fun displayCustomers(customersList: MutableList<Customer>) {

        recycler_view_customers.layoutManager = LinearLayoutManager(activity)
        var customersListAdapter = GroupAdapter<ViewHolder>()

        recycler_view_customers.adapter = customersListAdapter

        if (customersList.isNotEmpty()){

            for(customers in customersList){

                customersListAdapter.add(customerItem(customers))
            }

        }


    }

    inner class customerItem(val customer: Customer): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.customerName.text = customer.firstName + " " + customer.lastName
        }

        override fun getLayout(): Int {
            return R.layout.row_customer

        }
    }


}
