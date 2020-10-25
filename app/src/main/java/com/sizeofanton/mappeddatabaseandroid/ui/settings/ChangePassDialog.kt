package com.sizeofanton.mappeddatabaseandroid.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.SettingsContract
import timber.log.Timber

class ChangePassDialog(private val vm: SettingsContract.ViewModel): DialogFragment(), View.OnClickListener {

    private lateinit var etCurrPass: EditText
    private lateinit var etNewPassOne: EditText
    private lateinit var etNewPassTwo: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setTitle("Change password")
        val v = inflater.inflate(R.layout.change_pass_dialog, null)
        etCurrPass = v.findViewById(R.id.etCurrPass)
        etNewPassOne = v.findViewById(R.id.etNewPassOne)
        etNewPassTwo = v.findViewById(R.id.etNewPassTwo)
        v.findViewById<Button>(R.id.btnChangePasswordDialog).setOnClickListener(this)
        return v
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnChangePasswordDialog -> {
                if (etNewPassOne.text.toString() != etNewPassTwo.text.toString())
                    vm.showToast("Passwords do not match!")
                else {
                    vm.changePassword(
                        etCurrPass.text.toString(),
                        etNewPassOne.text.toString()
                    )
                    dismiss()
                }
            }
        }
    }

}