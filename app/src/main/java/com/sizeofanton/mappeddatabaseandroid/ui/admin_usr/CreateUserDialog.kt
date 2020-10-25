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

class CreateUserDialog(private val vm: AdminUserContract.ViewModel): DialogFragment(), View.OnClickListener {

    private lateinit var etNewUser: EditText
    private lateinit var etNewUserPassword: EditText
    private lateinit var cbIsAdmin: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.user_create_dialog, null)
        etNewUser = v.findViewById(R.id.etNewUser)
        etNewUserPassword = v.findViewById(R.id.etNewUserPass)
        cbIsAdmin = v.findViewById(R.id.cbAdmin)
        v.findViewById<Button>(R.id.btnAddNewUser).setOnClickListener(this)
        v.findViewById<Button>(R.id.btnCancel).setOnClickListener(this)
        return v
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnAddNewUser -> {
                vm.addNewUser(
                    etNewUser.text.toString(),
                    etNewUserPassword.text.toString(),
                    cbIsAdmin.isChecked
                )
                dismiss()
            }

            R.id.btnCancel -> dismiss()
        }
    }
}