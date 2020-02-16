package com.elevintech.motorbroshop.Employees

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.elevintech.motorbroshop.Constants
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Branch
import com.elevintech.motorbroshop.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_select_branch.*
import kotlinx.android.synthetic.main.row_branch.view.*

class SelectBranchActivity : AppCompatActivity() {

    var shopId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_branch)

        shopId = intent.getStringExtra("shopId")
        getShops(shopId)
    }

    private fun getShops(shopId: String) {
        MotorBroDatabase().getShopBranches(shopId){

            displayBranchList(it)

        }
    }

    private fun displayBranchList(branchList: MutableList<Branch>) {

        recycler_view_branches.layoutManager = LinearLayoutManager(this)
        var branchListAdapter = GroupAdapter<ViewHolder>()

        if (branchList.isNotEmpty()){
            for(branch in branchList){
                branchListAdapter.add(BranchItem(branch))
            }

        }

        recycler_view_branches.adapter = branchListAdapter
    }

    inner class BranchItem(val branch: Branch): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            val row = viewHolder.itemView
            row.branchName.text = branch.name

            row.setOnClickListener {
                // create intent
                val intent = Intent()

                // set result ok
                setResult(Activity.RESULT_OK, intent)

                // pass the image path in intent
                intent.putExtra("branch", branch)

                // go back loan activity with the intent
                finishActivity(Constants.REQUEST_CODES.SELECT_BRANCH)
                finish()
            }

        }

        override fun getLayout(): Int {
            return R.layout.row_branch

        }
    }
}
