package com.elevintech.motorbroshop.TypeOf

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elevintech.motorbroshop.AddTypeOfParts.AddTypeOfParts
import com.elevintech.motorbroshop.Constants
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_type_of_parts.*
import kotlinx.android.synthetic.main.row_type_of_parts.view.*

class TypeOfPartsActivity : AppCompatActivity() {

    private lateinit var viewAdapter : RecyclerView.Adapter<*>
    val totalList = ArrayList<CheckboxObj>()
    var selectedParts = ArrayList<String>()
    //private val partsListAdapter = MyAdapter<MyAdapter.MyViewHolder>()

    var isFromAddParts = false
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "mindorks-welcome"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_of_parts)

        //isFromAddParts = intent.getSerializableExtra("fromAddExtra", false)
        isFromAddParts = intent.getBooleanExtra("fromAddExtra", false)
        val previousExtraParts = intent.getStringExtra("previousParts")



        if (previousExtraParts != null) {
            val previousParts = previousExtraParts.split(",")
            for (part in previousParts) {
                val trimmedPart = part.trimStart()
                selectedParts.add(trimmedPart)
            }
        }

        viewAdapter = MyAdapter(totalList)

        if (isFromAddParts) {
            addItemsButton.visibility = View.VISIBLE
        }

        recycler_view_type_of_parts.apply {
            //this.adapter = partsListAdapter
            this.adapter = viewAdapter
            setHasFixedSize(true)
        }

        // If from nav bar then set click will be different
        add_parts_floating_button.setOnClickListener {
            val intent = Intent(applicationContext, AddTypeOfParts::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish()
        }

        deleteItemsButton.setOnClickListener {
            val listToDelete = totalList.filter { it.isChecked }
            var filteredList = totalList.filter { !it.isChecked }

            if (listToDelete.isEmpty()) {
                Snackbar.make(addItemsButton, "Please pick at least one parts / services", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val totalArrayList = ArrayList<CheckboxObj>(filteredList)
            totalList.clear()
            totalList.addAll(totalArrayList)
            viewAdapter.notifyDataSetChanged()

            //remove these items 1by1
            for (part in listToDelete) {
                val db = MotorBroDatabase()
                db.saveDeletedParts(part.name) {
                    println("deleted ${part.name}")
                }
            }
        }

        addItemsButton.setOnClickListener {
            val listToAdd = totalList.filter { it.isChecked }

            if (listToAdd.isEmpty()) {
                Snackbar.make(addItemsButton, "Please pick at least one parts / services", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            var partsRemaining = ""
            var isFirst = true

            for (part in listToAdd) {
                if (!isFirst) {
                    partsRemaining += ", "
                }
                partsRemaining += part.name
                isFirst = false
            }

            val returnIntent = Intent()
            returnIntent.putExtra("selectedPart", partsRemaining)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        displayParts()
    }

    private fun checkIfPartIsActive(deletedParts: List<String>, part: String):Boolean{

        // isActive == not in the deleted parts list
        var isActive = true

        for (deletedPart in deletedParts){
            if (deletedPart.trim().toLowerCase() == part.trim().toLowerCase() ){
                isActive = false
            }
        }
        return isActive
    }

    private fun displayParts() {
        totalList.clear()
        viewAdapter.notifyDataSetChanged()

        MotorBroDatabase().getUser {

            val defaultParts = Constants.TYPE_OF.parts
            val customParts = it.customParts
            val deletedParts = it.deletedParts

            val allParts = defaultParts + customParts
            val allPartsWithoutDeletedParts = allParts.filter { checkIfPartIsActive(deletedParts, it) }
            val allPartsWithoutDeletedPartsSorted = allPartsWithoutDeletedParts.sortedBy { it }

            for (part in allPartsWithoutDeletedPartsSorted) {
                val properlyCapitalized = (part.toLowerCase()).capitalize().trim()
                val part = CheckboxObj(properlyCapitalized, false)
                totalList.add(part)
            }


            viewAdapter.notifyDataSetChanged()

        }

    }

    inner class MyAdapter(private val myDataset: ArrayList<CheckboxObj>) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

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
        ): MyAdapter.MyViewHolder {
            // create a new view
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_type_of_parts, parent, false)
            // set the view's size, margins, paddings and layout parameters

            return MyViewHolder(v)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            val part = myDataset[position]
            viewHolder.itemView.parts_name.text = part.name

            if (selectedParts.contains(part.name.trimStart())){
                part.isChecked = true
            }

            viewHolder.itemView.checkbox.isChecked = part.isChecked

            viewHolder.itemView.checkbox.setOnClickListener {
                part.isChecked = !part.isChecked

                val partsChecked = myDataset.filter { it.isChecked }
                if (partsChecked.count() == 0){
                    addItemsButton.alpha = 0.65f
                    deleteItemsButton.alpha = 0.65f
                } else {
                    addItemsButton.alpha = 1f
                    deleteItemsButton.alpha = 1f
                }
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size

    }
}

class CheckboxObj(var name: String = "", var isChecked: Boolean = false)