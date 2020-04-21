package com.elevintech.motorbroshop.Dashboard.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.Chat.ChatListActivity
import com.elevintech.motorbroshop.Customer.CustomerProfileActivity
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Customer
import com.elevintech.motorbroshop.Model.User

import com.elevintech.motorbroshop.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_customer_list.*
//import kotlinx.android.synthetic.main.fragment_customer_list.chatImageView
import kotlinx.android.synthetic.main.fragment_customer_list.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.row_customer.view.*

/**
 * A simple [Fragment] subclass.
 */
class CustomerListFragment : Fragment() {

    lateinit var user: User
    lateinit var thisView: View
    var customersListAdapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        user = (activity as DashboardActivity).user

        // Inflate the layout for this fragment
        thisView = inflater.inflate(R.layout.fragment_customer_list, container, false)


        return thisView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isAdded) {
            return
        }

    }


    override fun onResume() {
        super.onResume()
        customersListAdapter.clear()
        getCustomers(thisView)

    }

    private fun getCustomers(view: View) {

        MotorBroDatabase().getShopCustomers(user.shopId){
            displayCustomers(it, view)
        }

    }

    private fun displayCustomers(customersList: MutableList<Customer>, view: View) {


        view.recycler_view_customers.adapter = customersListAdapter

        view.recycler_view_customers.layoutManager = LinearLayoutManager(activity)


        if (customersList.isNotEmpty()){
            for(customers in customersList){
                customersListAdapter.add(customerItem(customers))
            }
        }


    }

    inner class customerItem(val customer: Customer): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.customerName.text = customer.firstName + " " + customer.lastName

            if (user.profilePictureUrl != "") {
                activity?.let { Glide.with(it).load(user.profilePictureUrl).into( viewHolder.itemView.userProfileImage) }
            }

            viewHolder.itemView.setOnClickListener {
                val intent = Intent(activity, CustomerProfileActivity::class.java)
                intent.putExtra("customer", customer)
                startActivity(intent)
            }
        }

        override fun getLayout(): Int {
            return R.layout.row_customer
        }
    }


}
