package com.elevintech.motorbroshop.Register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.elevintech.motorbroshop.Model.City
import com.elevintech.motorbroshop.Model.Location
import com.elevintech.motorbroshop.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_select_location.*
import kotlinx.android.synthetic.main.row_employee.view.*

class SelectLocation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)


        searchText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(searchText: CharSequence, start: Int,
                                       before: Int, count: Int) {

                /*if (count >= 3)*/
                    readJson(searchText)

            }
        })

    }

    private fun readJson(searchText: CharSequence) {

        var employeeListAdapter = GroupAdapter<ViewHolder>()
        employeeListAdapter.clear()

        val jsonString = application.assets.open("cities.json").bufferedReader().use{
            it.readText()
        }

        val array = Gson().fromJson<ArrayList<City>>(jsonString)
        val search = array.filter { it.name.contains(searchText, true) }

        for (city in search){
            employeeListAdapter.add(LocationItem(city))

        }

        locationRecyclerView.layoutManager = LinearLayoutManager(this)
        locationRecyclerView.adapter = employeeListAdapter
        employeeListAdapter.notifyDataSetChanged()

        searchMessageLayout.visibility = View.GONE
        locationRecyclerView.visibility = View.VISIBLE
    }

    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)


    inner class LocationItem(val city: City): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.employeeName.text = city.name + " - " + city.province
            viewHolder.itemView.employeeId.text = ""
        }

        override fun getLayout(): Int {
            return R.layout.row_employee

        }
    }




}
