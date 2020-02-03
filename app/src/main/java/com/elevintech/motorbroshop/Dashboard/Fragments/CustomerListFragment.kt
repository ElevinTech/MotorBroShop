package com.elevintech.motorbroshop.Dashboard.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Consumer

import com.elevintech.motorbroshop.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_customer_list.*
import kotlinx.android.synthetic.main.row_consumer.view.*

/**
 * A simple [Fragment] subclass.
 */
class CustomerListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_list, container, false)
    }


    override fun onResume() {
        super.onResume()

        getCustomers()

    }

    private fun getCustomers() {

        MotorBroDatabase().getCustomers{

            displayConsumers(it)

        }

    }

    private fun displayConsumers(consumersList: MutableList<Consumer>) {

        recycler_view_consumers.layoutManager = LinearLayoutManager(activity)
        var consumersListAdapter = GroupAdapter<ViewHolder>()

        if (consumersList.isNotEmpty()){

            for(consumer in consumersList){

                consumersListAdapter.add(employeeItem(consumer))
            }

        }

        recycler_view_consumers.adapter = consumersListAdapter
    }

    inner class employeeItem(val consumer: Consumer): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.consumerName.text = consumer.firstName + " " + consumer.lastName
        }

        override fun getLayout(): Int {
            return R.layout.row_consumer

        }
    }


}
