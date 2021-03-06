package com.elevintech.motorbroshop.Employees

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Employee
import com.elevintech.motorbroshop.Model.ShopOwner
import com.elevintech.motorbroshop.Model.ShopUser
import com.elevintech.motorbroshop.Model.UserType
import com.elevintech.motorbroshop.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_employee_list.*
import kotlinx.android.synthetic.main.row_employee.view.*

class EmployeeListActivity : AppCompatActivity() {

    var shopId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_list)

        shopId = intent.getStringExtra("shopId")

        floatingActionButton.setOnClickListener {

            val intent = Intent(this, CreateEmployeeActivity::class.java)
            intent.putExtra("shopId", shopId)
            startActivity(intent)

        }

        back_button.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        getShopEmployees()
        getUserType()
    }

    private fun getUserType(){

        MotorBroDatabase().getUserType {

            if (it == UserType.Type.EMPLOYEE)
                (floatingActionButton as View).visibility = View.GONE

        }

    }

    private fun getShopEmployees() {

        MotorBroDatabase().getShopEmployees(shopId){

            displayEmployeeList(it)

        }

    }

    private fun displayEmployeeList(employeeList: MutableList<Employee>) {

        recycler_view_employees.layoutManager = LinearLayoutManager(this)
        var employeeListAdapter = GroupAdapter<ViewHolder>()

        if (employeeList.isNotEmpty()){
            for(employee in employeeList){
                employeeListAdapter.add(employeeItem(employee))
            }

        }

        recycler_view_employees.adapter = employeeListAdapter
    }

    inner class employeeItem(val employee: Employee): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.employeeName.text = employee.firstName + " " + employee.lastName
            viewHolder.itemView.employeeId.text = employee.employeeCode
            viewHolder.itemView.setOnClickListener {



            }
        }

        override fun getLayout(): Int {
            return R.layout.row_employee

        }
    }


}
