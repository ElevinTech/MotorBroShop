package com.elevintech.motorbroshop.Branches

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Branch
import com.elevintech.motorbroshop.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_branch.*
import kotlinx.android.synthetic.main.row_branch.view.*

class BranchListActivity : AppCompatActivity() {

    var shopId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_branch)

        shopId = intent.getStringExtra("shopId")

        floatingActionButton.setOnClickListener {

            val intent = Intent(this, CreateBranch::class.java)
            intent.putExtra("shopId", shopId)
            startActivity(intent)

        }
    }

    override fun onResume() {
        super.onResume()

        getShopBranches()
    }

    private fun getShopBranches() {

        MotorBroDatabase().getShopBranches(shopId){

            displayBranchList(it)

        }

    }

    private fun displayBranchList(branchList: MutableList<Branch>) {

        recycler_view_branches.layoutManager = LinearLayoutManager(this)
        var branchListAdapter = GroupAdapter<ViewHolder>()

        if (branchList.isNotEmpty()){
            for(branch in branchList){
                branchListAdapter.add(branchItem(branch))
            }

        }

        recycler_view_branches.adapter = branchListAdapter
    }

    inner class branchItem(val branch: Branch): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            val row = viewHolder.itemView
            row.branchName.text = branch.name

            row.setOnClickListener {
                val intent = Intent(this@BranchListActivity, BranchDetailsActivity::class.java)
                intent.putExtra("shopId", shopId)
                intent.putExtra("branchId", branch.id)
                startActivity(intent)
            }

        }

        override fun getLayout(): Int {
            return R.layout.row_branch

        }
    }
}
