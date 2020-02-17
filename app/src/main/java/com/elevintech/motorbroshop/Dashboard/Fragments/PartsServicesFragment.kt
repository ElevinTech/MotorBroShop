package com.elevintech.motorbroshop.Dashboard.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elevintech.motorbroshop.AddPartsServices.AddPartsServicesActivity
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Model.User

import com.elevintech.motorbroshop.R
import kotlinx.android.synthetic.main.fragment_parts_services.*

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

            val intent = Intent(activity, AddPartsServicesActivity::class.java)
            intent.putExtra("shopId", user.shopId)
            startActivity(intent)

        }
    }


}
