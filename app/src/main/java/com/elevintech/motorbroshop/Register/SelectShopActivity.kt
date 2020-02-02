package com.elevintech.motorbroshop.Register

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Shop
import com.elevintech.motorbroshop.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_select_shop.*
import kotlinx.android.synthetic.main.row_shop.view.*

class SelectShopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_shop)
    }

    override fun onResume() {
        super.onResume()

        getShops()
    }

    fun getShops(){

        MotorBroDatabase().getShops{

            displayShops(it)


        }

    }

    private fun displayShops(shopList: MutableList<Shop>) {


        var shopListAdapter = GroupAdapter<ViewHolder>()

        for (shop in shopList){
            shopListAdapter.add(shopItem(shop))
        }

        recycler_view_shop.adapter = shopListAdapter
    }


    inner class shopItem(val shop: Shop): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.shop_name.text = shop.name
            viewHolder.itemView.setOnClickListener {
                val returnIntent = Intent()
                returnIntent.putExtra("selected", shop)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }

        override fun getLayout(): Int {
            return R.layout.row_shop

        }
    }
}
