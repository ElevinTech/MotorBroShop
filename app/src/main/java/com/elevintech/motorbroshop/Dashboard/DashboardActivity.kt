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
import com.elevintech.motorbroshop.Consumer.ConsumerProfileActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Employees.EmployeeListActivity
import com.elevintech.motorbroshop.Login.LoginActivity
import com.elevintech.motorbroshop.Model.ShopUser
import com.elevintech.motorbroshop.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.drawer_dashboard.*
import kotlinx.android.synthetic.main.drawer_header.view.*



class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_dashboard)


        buildNavigationDrawer()
        setUpBottomNav()

        val db = MotorBroDatabase()

        db.getUser {
            println("Got User")
            println(it.firstName)

            setValuesNavHeader(it)
        }
    }

    private fun setValuesNavHeader(user: ShopUser) {

        val navHeader = nav_view.getHeaderView(0)

        val navUserName = navHeader.usersNameText
        val navUserEmail = navHeader.userEmailText

        navUserName.setText(user.firstName + " " + user.lastName)
        navUserEmail.setText(user.email)
    }

    private fun buildNavigationDrawer(){

        // set toolbar as our created view
        setSupportActionBar(toolbar)

        // remove default title, so we can set it through XML
        getSupportActionBar()!!.setDisplayShowTitleEnabled(false)

        val drawerLayout = drawer_layout
        println("is layout nil")
        println(drawerLayout)

        // create navigation drawer
        var toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.employees -> {
                val intent = Intent(this, EmployeeListActivity::class.java)
                startActivity(intent)
            }

            R.id.sign_out -> {
                logOut()
            }

        }

        return true
    }

    private fun setUpBottomNav() {

//        homeFragment = HomeFragment()
//        partsFragment = PartsFragment()
//        shopFragment = ShopFragment()
//        remindersFragment = RemindersFragment()
//        historyFragment = HistoryFragment()

        val menuView = bottom_nav.getChildAt(0) as? ViewGroup ?: return
        menuView.forEach {
            it.findViewById<View>(R.id.largeLabel)?.setPadding(0, 0, 0, 0)
        }

        val bottomNavigation : BottomNavigationView = bottom_nav
        bottomNavigation.setOnNavigationItemSelectedListener { item ->

            when(item.itemId) {
                R.id.option1 -> {
//                    supportFragmentManager
//                        .beginTransaction()
//                        .replace(R.id.frame_layout, homeFragment, "homeFragmentTag")
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                        .commit()

                }

                R.id.scanQr -> {
                    val scanner = IntentIntegrator(this)
                    scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                    scanner.setPrompt("Scan a barcode")
                    scanner.setOrientationLocked(true)
                    scanner.setBeepEnabled(true)
                    scanner.initiateScan()
                }


            }

            true
        }

//        // default fragment
//        bottomNavigation.selectedItemId = R.id.tabhome

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result.contents != null) {

            MotorBroDatabase().getConsumer(result.contents) {
                if (it == null) {

                    Toast.makeText(this, "Invalid QR code", Toast.LENGTH_LONG).show()

                } else {

                    val intent = Intent(this, ConsumerProfileActivity::class.java)
                    intent.putExtra("consumer", it)
                    startActivity(intent)

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
