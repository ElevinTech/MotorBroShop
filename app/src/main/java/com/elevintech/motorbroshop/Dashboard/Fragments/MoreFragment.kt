package com.elevintech.motorbroshop.Dashboard.Fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.Branches.BranchListActivity
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Documents.DocumentsActivity
import com.elevintech.motorbroshop.Employees.EmployeeListActivity
import com.elevintech.motorbroshop.FeedbackActivity
import com.elevintech.motorbroshop.Login.LoginActivity
import com.elevintech.motorbroshop.Model.User

import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Shop.ShopActivity
import com.elevintech.motorbroshop.Shop.UpdateShop
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_more.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MoreFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MoreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MoreFragment : Fragment() {
    // TODO: Rename and change types of parameters

    lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = (activity as DashboardActivity).user

        userFirstName.text = user.firstName
        emailText.text = user.email

        if (user.profilePictureUrl != "") {
            Glide.with(this).load(user.profilePictureUrl).into(userProfileImage)
        }

        editShopLayout.setOnClickListener {
            val intent = Intent(activity, UpdateShop::class.java)
            intent.putExtra("shopId", user.shopId)
            startActivity(intent)
        }

        branchesLayout.setOnClickListener {
            val intent = Intent(activity, BranchListActivity::class.java)
            intent.putExtra("shopId", user.shopId)
            startActivity(intent)
        }

        documentsLayout.setOnClickListener {
            val intent = Intent(activity, DocumentsActivity::class.java)
            intent.putExtra("shopId", user.shopId)
            startActivity(intent)
        }

        employeesLayout.setOnClickListener {
            val intent = Intent(activity, EmployeeListActivity::class.java)
            intent.putExtra("shopId", user.shopId)
            startActivity(intent)
        }

        feedbackLayout.setOnClickListener {
            val intent = Intent(activity, FeedbackActivity::class.java)
            startActivity(intent)
        }

        termsServicesLayout.setOnClickListener {

        }

        signoutView.setOnClickListener {
            MotorBroDatabase().deleteUserToken()
            MotorBroDatabase().deleteShopToken(user.shopId)
            logOut()
        }

    }

    private fun logOut() {

        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

    }

}
