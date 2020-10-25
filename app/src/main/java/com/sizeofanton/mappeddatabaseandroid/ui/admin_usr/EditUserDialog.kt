package com.sizeofanton.mappeddatabaseandroid.ui.admin_usr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.AdminUserContract
import com.sizeofanton.mappeddatabaseandroid.data.pojo.UserInfo

class EditUserDialog(
    private val vm: AdminUserContract.ViewModel,
    private val user: UserInfo
): DialogFragment(), View.OnClickListener {

    private lateinit var etNewPassword: EditText
    private lateinit var cbAdmin: CheckBox
    private lateinit var cbActive: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.user_edit_dialog, null)
        etNewPassword = v.findViewById(R.id.etNewPassword)
        cbAdmin = v.findViewById(R.id.cbAdmin)
        cbActive = v.findViewById(R.id.cbActive)
        cbAdmin.isChecked = user.isAdmin
        cbActive.isChecked = user.isActive
        v.findViewById<Button>(R.id.btnFinishEditUser).setOnClickListener(this)
        v.findViewById<Button>(R.id.btnCancel).setOnClickListener(this)
        return v
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnFinishEditUser -> {
                vm.editUser(
                    user.userId,
                    etNewPassword.text.toString(),
                    cbAdmin.isChecked,
                    cbActive.isChecked
                )
                dismiss()
            }

            R.id.btnCancel -> dismiss()
        }
    }
}