package com.elevintech.motorbroshop.Register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.R
import kotlinx.android.synthetic.main.activity_enter_employee_id.*

class EnterEmployeeID : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_employee_id)


        submitButton.setOnClickListener {

            MotorBroDatabase().getEmployee( employeeIdText.text.toString() ){ employee ->


                if (employee != null){

                    if (!employee.hasSetupLogin){

                        val intent = Intent(this, RegisterEmployee::class.java)
                        intent.putExtra("employee", employee)
                        startActivity(intent)
                    } else {

                        Toast.makeText(this, "Hey [employee name], seems like you have already set up your login account. Please sign in through the login page.", Toast.LENGTH_SHORT).show()
                    }

                } else {

                    Toast.makeText(this, "Woops, we can't seem to find the associated employee. Please make sure you have typed the correct one", Toast.LENGTH_LONG).show()
                }


            }


        }



    }
}
