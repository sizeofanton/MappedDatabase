package com.sizeofanton.mappeddatabaseandroid.ui.admin_usr

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.AdminUserContract
import com.sizeofanton.mappeddatabaseandroid.data.local.CurrentUser
import com.sizeofanton.mappeddatabaseandroid.data.pojo.UserInfo

class AdminUserRvAdapter(
    private val context: Context,
    private var list: List<UserInfo>
) : RecyclerView.Adapter<AdminUserRvAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var view: AdminUserContract.View
    private lateinit var vm: AdminUserContract.ViewModel

    fun bindView(v: AdminUserContract.View) {
        this.view = v
    }

    fun bindViewModel(vm: AdminUserContract.ViewModel) {
        this.vm = vm
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.user_card, parent, false)
        return ViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(users: List<UserInfo>) {
        list = users
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]
        holder.id = user.userId
        holder.tvUserName.text = user.userName
        holder.cbAdmin.isChecked = user.isAdmin
        holder.cbActive.isChecked = user.isActive

        holder.btnEditUser.setOnClickListener {
            if ((user.userName == "admin") && (CurrentUser.getUser() != "admin")) {
                Toast.makeText(context, "You can't edit admin user", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (user.userName != CurrentUser.getUser()) {
                Toast.makeText(context, "You can't edit another admins!", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            val dialog = EditUserDialog(vm, user)
            dialog.show( (context as AppCompatActivity).supportFragmentManager, "EditUserDialog")
        }

        holder.btnDeleteUser.setOnClickListener {
            if (user.userName == "admin") {
                Toast.makeText(context, "You can't delete admin user", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (user.isAdmin && (CurrentUser.getUser() != "admin")) {
                Toast.makeText(
                    context,
                    "You can't delete only non-admin accounts",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            if (CurrentUser.getUser() == user.userName) {
                Toast.makeText(context, "You can't delete yourself!", Toast.LENGTH_LONG).show()
            }
            view.deleteUserClicked(holder.id)
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var id: Int = -1
        val tvUserName: TextView = v.findViewById(R.id.tvUserName)
        val cbAdmin: CheckBox = v.findViewById(R.id.cbAdmin)
        val cbActive: CheckBox = v.findViewById(R.id.cbActive)
        val btnEditUser: Button = v.findViewById(R.id.btnEditUser)
        val btnDeleteUser: Button = v.findViewById(R.id.btnDeleteUser)
    }
}
