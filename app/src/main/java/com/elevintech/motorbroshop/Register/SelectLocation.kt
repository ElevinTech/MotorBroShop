package com.elevintech.motorbroshop.Register

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.elevintech.motorbroshop.Model.Address
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

        val array = Gson().fromJson<ArrayList<Address>>(jsonString)
        val search = array.filter { it.city.contains(searchText, true) || it.province.contains(searchText, true)}

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

    // CREATE DIALOG FOR ASKING THE STREET
    private fun onSelectCity(address: Address){

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_ask_street)

        val streetAddressText = dialog.findViewById<TextView>(R.id.streetAddressText)
        val submitButton = dialog.findViewById<Button>(R.id.submitButton)

        streetAddressText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(searchText: CharSequence, start: Int,
                                       before: Int, count: Int) {

                if (count == 0){
                    submitButton.alpha = 0.65f
                    submitButton.isEnabled = false
                } else {
                    submitButton.alpha = 1f
                    submitButton.isEnabled = true
                }

            }
        })

        submitButton.setOnClickListener {
            dialog.dismiss()

            val returnIntent = Intent()
            address.street = streetAddressText.text.toString()
            returnIntent.putExtra("address", address)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

        dialog.show()

    }

    inner class LocationItem(val address: Address): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.employeeName.text = address.city + " - " + address.province
            viewHolder.itemView.employeeId.text = ""
            viewHolder.itemView.setOnClickListener {
                onSelectCity(address)
            }
        }

        override fun getLayout(): Int {
            return R.layout.row_employee

        }
    }




}
