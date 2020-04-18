package com.elevintech.motorbroshop.Dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.forEach
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.Branches.BranchListActivity
import com.elevintech.motorbroshop.Chat.ChatListActivity
import com.elevintech.motorbroshop.Customer.CustomerProfileActivity
import com.elevintech.motorbroshop.Dashboard.Fragments.CustomerListFragment
import com.elevintech.motorbroshop.Dashboard.Fragments.HomeFragment
import com.elevintech.motorbroshop.Dashboard.Fragments.MoreFragment
import com.elevintech.motorbroshop.Dashboard.Fragments.PartsServicesFragment
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Documents.DocumentsActivity
import com.elevintech.motorbroshop.Employees.EmployeeListActivity
import com.elevintech.motorbroshop.FeedbackActivity
import com.elevintech.motorbroshop.Login.LoginActivity
import com.elevintech.motorbroshop.Model.*
import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Shop.ShopActivity
import com.elevintech.motorbroshop.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.iid.FirebaseInstanceId
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.drawer_dashboard.*
import kotlinx.android.synthetic.main.drawer_header.view.*



class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var homeFragment: HomeFragment
    lateinit var customersListFragment: CustomerListFragment
    lateinit var partsServicesFragment: PartsServicesFragment
    lateinit var moreFragment: MoreFragment

    var user: User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_dashboard)

//        buildNavigationDrawer()
        setUpBottomNav()
        val db = MotorBroDatabase()

        db.getUserType{ userType ->

            if ( userType == UserType.Type.OWNER) {

                db.getOwner {
                    user = it
//                    setValuesNavHeader()
                }
            } else if ( userType == UserType.Type.EMPLOYEE ){

                db.getEmployee {
                    user = it
//                    setValuesNavHeader()
                }
            }
        }

        MotorBroDatabase().checkRegistrationToken()

    }

//    private fun setValuesNavHeader() {
//
//        val navHeader = nav_view.getHeaderView(0)
//        Glide.with(this).load(user.profilePictureUrl).into(navHeader.imageProfileView)
//        navHeader.usersNameText.text = user.firstName + " " + user.lastName
//        navHeader.userEmailText.text = user.email
//
//    }

//    private fun buildNavigationDrawer(){
//
//        // set toolbar as our created view
//        setSupportActionBar(toolbar)
//
//        // remove default title, so we can set it through XML
//        getSupportActionBar()!!.setDisplayShowTitleEnabled(false)
//
//        val drawerLayout = drawer_layout
//
//        // create navigation drawer
//        var toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
//
//        nav_view.setNavigationItemSelectedListener(this)
//    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.shop -> {

                val intent = Intent(this, ShopActivity::class.java)
                intent.putExtra("shopId", user.shopId)
                startActivity(intent)

            }

            R.id.employees -> {
                val intent = Intent(this, EmployeeListActivity::class.java)
                intent.putExtra("shopId", user.shopId)
                startActivity(intent)
            }

            R.id.documents -> {

                val intent = Intent(this, DocumentsActivity::class.java)
                intent.putExtra("shopId", user.shopId)
                startActivity(intent)

            }

            R.id.branches -> {

                val intent = Intent(this, BranchListActivity::class.java)
                intent.putExtra("shopId", user.shopId)
                startActivity(intent)

            }

            R.id.feedback -> {
                val intent = Intent(this, FeedbackActivity::class.java)
                startActivity(intent)
            }

            R.id.sign_out -> {
                MotorBroDatabase().deleteUserToken()
                MotorBroDatabase().deleteShopToken(user.shopId)
                logOut()
            }

        }

        return true
    }

    private fun setUpBottomNav() {

        homeFragment = HomeFragment()
        customersListFragment = CustomerListFragment()
        partsServicesFragment = PartsServicesFragment()
        moreFragment = MoreFragment()

        val menuView = bottom_nav.getChildAt(0) as? ViewGroup ?: return
        menuView.forEach {
            it.findViewById<View>(R.id.largeLabel)?.setPadding(0, 0, 0, 0)
        }

        val bottomNavigation : BottomNavigationView = bottom_nav
        bottomNavigation.setOnNavigationItemSelectedListener { item ->

            when(item.itemId) {
                R.id.home -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, homeFragment, "homeFragmentTag")
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()

                }

                R.id.customers -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, customersListFragment, "customersListFragmentTag")
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()

                }

                R.id.scanQr -> {
                    val scanner = IntentIntegrator(this)
                    scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                    scanner.setPrompt("Scan a QR code")
                    scanner.setOrientationLocked(true)
                    scanner.setBeepEnabled(true)
                    scanner.initiateScan()
                }

                R.id.partsServices -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, partsServicesFragment, "partsservicesFragmentTag")
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

                R.id.more -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, moreFragment, "moreFragmentTag")
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

            }

            true
        }

        // default fragment
        bottomNavigation.selectedItemId = R.id.home

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result.contents != null) {

            val progressDialog = Utils().easyProgressDialog(this, "Pulling Customer Details....")
            progressDialog.show()

            val customerId = result.contents

            MotorBroDatabase().getCustomer(customerId) { customer ->
                if (customer != null) {

                    val customerShopData = CustomerShopData(Utils().getCurrentTimestamp().toString(), customerId)
                    MotorBroDatabase().addShopCustomer(user.shopId, customerShopData){

                        progressDialog.dismiss()
                        val intent = Intent(this, CustomerProfileActivity::class.java)
                        intent.putExtra("customer", customer)
                        startActivity(intent)
                    }


                } else {

                    progressDialog.dismiss()
                    Toast.makeText(this, "Invalid QR code", Toast.LENGTH_LONG).show()

                }
            }
        }
    }

    private fun logOut() {

        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}
