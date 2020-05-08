package com.elevintech.motorbroshop.Dashboard.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.Chat.ChatListActivity
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Shop
import com.elevintech.motorbroshop.Model.User
import com.elevintech.motorbroshop.Model.UserType

import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Shop.ShopActivity
import com.elevintech.motorbroshop.Shop.UpdateShop
import com.elevintech.motorbroshop.ShopView.ShopViewActivity
import com.elevintech.motorbroshop.Utils
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.row_employee_dashboard.view.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    var user = User()
    var shop = Shop()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        //user = (activity as DashboardActivity).user
        getUserType(view)


        return view
    }


    private fun displayMessageBadge(view: View){

        MotorBroDatabase().getUnreadMessageCount(shop.shopId){ unreadMessageCount ->

            if (!isAdded) {
                return@getUnreadMessageCount
            }
            view.chatImageView.setBadgeValue(unreadMessageCount)

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatImageView.setOnClickListener {
            val intent = Intent(context, ChatListActivity::class.java)
            intent.putExtra("shopId", user.shopId)
            startActivity(intent)
        }

        viewShopButton.setOnClickListener {
            val intent = Intent(activity, ShopViewActivity::class.java)
            intent.putExtra("shop", shop)
            startActivity(intent)
        }

        editShopButton.setOnClickListener {
            val intent = Intent(activity, UpdateShop::class.java)
            intent.putExtra("shopId", user.shopId)
            startActivity(intent)
        }

        scanLayout.setOnClickListener {
            
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (!isAdded) {
            return
        }
    }

    private fun getUserType(view: View) {
        MotorBroDatabase().getUserType{ userType ->
            if ( userType == UserType.Type.OWNER) {
                MotorBroDatabase().getOwner {
                    user = it
                    setupShop(view)
                }
            } else if ( userType == UserType.Type.EMPLOYEE ){
                MotorBroDatabase().getEmployee {
                    user = it
                    setupShop(view)
                }
            }
        }

    }
    private fun setupShop(view: View) {

        if (!isAdded) {
            return
        }

        if (view.shopName == null) {
            return
        }

        val db = MotorBroDatabase()

        if (user.shopId.isEmpty()) {
            return
        }

        db.getShop(user.shopId) {
            if (!isAdded) {
                return@getShop
            }

            this.shop = it
            val shop = it

            view.shopName.text = shop.name

            // GET DATE ESTABLISHED
            if (shop.dateEstablished != "") {
                //val viewInflated = layoutInflater.inflate(R.layout.row_employee_dashboard, null)
                val date = Utils().convertMillisecondsToDate(shop.dateEstablished, "MMM dd, yyyy")
                view.shopEstablished.text = "Acquired: $date"
//                if (view.shopImageView != null) {
//                    Glide.with(this).load(shop.imageUrl).into(view.shopImageView)
//                }

                if (view.shopImageView != null) {
                    if (shop.imageUrl != "") {
                        Glide.with(this).load(shop.imageUrl).into(view.shopImageView)
                    } else {
                        // Put an empty image here
                        Glide.with(this).load(R.drawable.users_icon).into(view.shopImageView)
                    }
                }

            } else {
                view.shopEstablished.text = ""
            }

            // GET EMPLOYEES
            MotorBroDatabase().getShopEmployees(user.shopId){ employeeList ->

                if (!isAdded) {
                    return@getShopEmployees
                }

                if (employeeList.size != 0) {
                    lastScannedUserLayout.visibility = View.VISIBLE

                    for (employee in employeeList){
                        // instantiate the view for the dialog
                        val viewInflated = layoutInflater.inflate(R.layout.row_employee_dashboard, null)
                        viewInflated.employeeName.text = employee.firstName + " " + employee.lastName
                        Glide.with(this).load(employee.profilePictureUrl).into(viewInflated.lastEmployeeImage)
                        view.employeeLayout.addView(viewInflated)
                    }

                } else {

                    view.employeeHeaderText.text = "You have no employees yet."
                }
            }

            // GET CUSTOMERS
            MotorBroDatabase().getShopCustomerData(user.shopId){ customerList ->

                if (!isAdded) {
                    return@getShopCustomerData
                }

                val customerCount = customerList.count()
                view.shopCustomersNumber.text = "$customerCount customer(s)"

                // GET FIRST CUSTOMER
                if (customerCount > 0){
                    view.lastScannedUserLayout.visibility = View.VISIBLE
                    view.lastScannedHeader.visibility = View.VISIBLE

                    val firstCustomerBasicData = customerList[0]

                    MotorBroDatabase().getCustomerByUid( firstCustomerBasicData.customerId ){ firstCustomerAllData ->

                        if (!isAdded) {
                            return@getCustomerByUid
                        }

                        view.lastScannedUserName.text = "${firstCustomerAllData!!.firstName}  ${firstCustomerAllData!!.lastName}"
                        view.lastScannedUserDate.text = Utils().convertMillisecondsToDate((firstCustomerBasicData.dateScanned.toLong() * 1000), "MMM dd, yyyy")

                        if(view.lastScannedUserImage != null) {
                            if (firstCustomerAllData.profileImage != "") {
                                Glide.with(this).load(firstCustomerAllData.profileImage).into(view.lastScannedUserImage)
                            } else {
                                // Put an empty image here
                                Glide.with(this).load(R.drawable.users_icon).into(view.lastScannedUserImage)
                            }

                        }


                    }
                } else {
                    view.lastScannedUserLayout.visibility = View.GONE
                    view.lastScannedHeader.visibility = View.GONE
                    view.lastScannedHeader.text = "No User Scanned Yet, Get Started by scanning a user."
                }

            }

            // GET PARTS/SERVICES
            MotorBroDatabase().getShopProducts(user.shopId){ productsList ->

                if (!isAdded) {
                    return@getShopProducts
                }

                view.lastAddedPartsLayout.visibility = View.VISIBLE
                view.lastAddedPartsHeader.visibility = View.VISIBLE

                val productCount = productsList.count()
                view.shopPartsServicesNumber.text = "$productCount parts/services"
                if (productCount != 0) {

                    // GET FIRST CUSTOMER
                    if (productCount > 0){
                        val latestProduct = productsList[0]
                        if (!latestProduct.dateCreated.isEmpty()) {
                            view.latestProductName.text = "${latestProduct.brand} ${latestProduct.name}"
                            view.latestProductDate.text = Utils().convertMillisecondsToDate((latestProduct.dateCreated.toLong() * 1000), "MMM dd, yyyy")
                        }
                    }
                } else {
                    view.lastAddedPartsHeader.text = "No Parts / Services yet!"
                    view.lastAddedPartsLayout.visibility = View.GONE
                    view.lastAddedPartsHeader.visibility = View.GONE
                }


            }

            // GET UNREAD MESSAGES
            displayMessageBadge(view)
        }
    }


}
