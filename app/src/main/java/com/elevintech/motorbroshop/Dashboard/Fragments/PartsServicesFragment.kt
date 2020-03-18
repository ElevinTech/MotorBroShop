package com.elevintech.motorbroshop.Dashboard.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.AddPartsServices.AddPartsServicesActivity
import com.elevintech.motorbroshop.AddPartsServices.AddPartsServicesForCustomerActivity
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Product
import com.elevintech.motorbroshop.Model.User

import com.elevintech.motorbroshop.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_parts_services.*
import kotlinx.android.synthetic.main.row_product.view.*

/**
 * A simple [Fragment] subclass.
 */
class PartsServicesFragment : Fragment() {

    lateinit var user: User

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

            askWhoPartIsFor()

        }
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

        getProducts()

    }

    private fun getProducts() {

        MotorBroDatabase().getShopProducts(user.shopId){

            displayProducts(it)

        }

    }

    private fun displayProducts(customersList: MutableList<Product>) {

        recycler_view_type_of_parts.layoutManager = LinearLayoutManager(activity)
        var customersListAdapter = GroupAdapter<ViewHolder>()


        if (customersList.isNotEmpty()){

            for(customers in customersList){

                customersListAdapter.add(customerItem(customers))
            }

            noDataLayout.visibility = View.GONE

        }

        recycler_view_type_of_parts.adapter = customersListAdapter

    }

    inner class customerItem(val product: Product): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.productName.text = product.name
            viewHolder.itemView.productBrand.text = product.brand

            Glide.with(activity as DashboardActivity).load(product.imageUrl).into(viewHolder.itemView.productImage)
        }

        override fun getLayout(): Int {
            return R.layout.row_product

        }
    }

}
