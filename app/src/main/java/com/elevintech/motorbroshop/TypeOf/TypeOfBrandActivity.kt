package com.elevintech.motorbroshop.TypeOf

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elevintech.motorbroshop.AddTypeOfParts.AddBrandActivity
import com.elevintech.motorbroshop.Constants
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_type_of_brand.*
import kotlinx.android.synthetic.main.row_type_of_brand.view.*

class TypeOfBrandActivity : AppCompatActivity() {

    private lateinit var viewAdapter : RecyclerView.Adapter<*>
    private val totalList = ArrayList<CheckboxObj>()
    private var isFromAddParts = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_of_brand)

        isFromAddParts = intent.getBooleanExtra("fromAddExtra", false)
        viewAdapter = BrandAdapter(totalList)
        recycler_view_type_of_brands.apply {
            //this.adapter = partsListAdapter
            this.adapter = viewAdapter
            setHasFixedSize(true)
        }

        // If from nav bar then set click will be different
        add_parts_floating_button.setOnClickListener {
            val intent = Intent(applicationContext, AddBrandActivity::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish()
        }

        deleteItemsButton.setOnClickListener {

            val listToDelete = totalList.filter { it.isChecked }
            var filteredList = totalList.filter { !it.isChecked }

            if (listToDelete.isEmpty()) {
                Snackbar.make(addItemsButton, "Please pick at least one custom brand", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val totalArrayList = ArrayList<CheckboxObj>(filteredList)
            totalList.clear()
            totalList.addAll(totalArrayList)
            viewAdapter.notifyDataSetChanged()

            for (part in listToDelete) {
                val db = MotorBroDatabase()
                db.saveDeletedBrands(part.name) {
                    println("deleted ${part.name}")
                }
            }
        }

        addItemsButton.setOnClickListener {

            val selectedBrands = totalList.filter { it.isChecked }
//            if (selectedBrand.count() != 1){
//                Snackbar.make(addItemsButton, "Please select only one brand", Snackbar.LENGTH_LONG).show()
//            }

//            else {


            if (selectedBrands.isEmpty()) {
                Snackbar.make(addItemsButton, "Please pick at least one brand", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            var partsRemaining = ""
            var isFirst = true

            for (part in selectedBrands) {
                if (!isFirst) {
                    partsRemaining += ", "
                }
                partsRemaining += part.name
                isFirst = false
            }

            val returnIntent = Intent()
            returnIntent.putExtra("selectedBrand", partsRemaining)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()

        }
    }

    override fun onResume() {
        super.onResume()
        displayParts()
    }

    private fun checkIfBrandIsActive(deletedBrands: List<String>, brand: String):Boolean{

        // isActive == not in the deleted parts list
        var isActive = true

        for (deletedBrand in deletedBrands){

            if (deletedBrand.trim().toLowerCase() == brand.trim().toLowerCase() ){
                isActive = false
            }

        }

        return isActive
    }

    private fun displayParts() {

        totalList.clear()
        viewAdapter.notifyDataSetChanged()
        MotorBroDatabase().getUser{

            val defaultBrands = Constants.TYPE_OF.brands
            val customBrands = it.customBrands
            val deletedBrands = it.deletedBrands

            val allBrands = defaultBrands + customBrands
            val allBrandWithoutDeletedBrand = allBrands.filter { checkIfBrandIsActive(deletedBrands, it) }

            for (brand in allBrandWithoutDeletedBrand) {
                val brand = CheckboxObj(brand, false)
                totalList.add(brand)
            }

            viewAdapter.notifyDataSetChanged()
        }
    }

    inner class BrandAdapter(private val myDataset: ArrayList<CheckboxObj>) :
        RecyclerView.Adapter<BrandAdapter.MyViewHolder>() {

        private var removedPosition: Int = 0
        private var removedItem: String = ""
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        inner class MyViewHolder(val v: View) : RecyclerView.ViewHolder(v)


        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): BrandAdapter.MyViewHolder {
            // create a new view
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_type_of_brand, parent, false)
            // set the view's size, margins, paddings and layout parameters

            return MyViewHolder(v)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            val part = myDataset[position]
            viewHolder.itemView.parts_name.text = part.name

            viewHolder.itemView.setOnClickListener {
                if (isFromAddParts) {
                    val returnIntent = Intent()
                    returnIntent.putExtra("selectedBrand", part.name)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }

            }

            viewHolder.itemView.checkbox.isChecked = part.isChecked

            viewHolder.itemView.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->

                part.isChecked = isChecked

                val partsChecked = myDataset.filter { it.isChecked }
                if (partsChecked.count() == 0){
                    addItemsButton.alpha = 0.65f
                    deleteItemsButton.alpha = 0.65f
                } else {
                    addItemsButton.alpha = 1f
                    deleteItemsButton.alpha = 1f
                }

            }

            // DEFAULT BRANDS CANNOT BE DELETED
            val defaultBrands: List<String> = Constants.TYPE_OF.brands

            // HIDE CHECKBOX IF IT IS ONE
            if ( defaultBrands.contains(part.name) ){
                viewHolder.itemView.checkbox.visibility = View.GONE
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size

//        private fun removeItem(position: Int, viewHolder: RecyclerView.ViewHolder) {
//            removedItem = myDataset[position]
//            removedPosition = position
//
//            myDataset.removeAt(position)
//            notifyItemRemoved(position)
//
//            val db = MotoroBroDatabase()
//            db.saveDeletedBrands(removedItem) {
//                println("deleted $removedItem")
//            }
//
//            Snackbar.make(viewHolder.itemView, "$removedItem removed", Snackbar.LENGTH_LONG).show()
//        }
    }
}
