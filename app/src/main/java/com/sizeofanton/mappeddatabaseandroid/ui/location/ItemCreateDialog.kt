package com.sizeofanton.mappeddatabaseandroid.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.LocationContract

class ItemCreateDialog(val locId: Int, val vm: LocationContract.ViewModel) : DialogFragment(), View.OnClickListener {


    private lateinit var etTitle: EditText
    private lateinit var etCount: EditText
    private lateinit var cbRequired: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setTitle("Add new item")
        val v = inflater.inflate(R.layout.item_create_dialog, null)
        v.findViewById<Button>(R.id.btnAdd).setOnClickListener(this)
        v.findViewById<Button>(R.id.btnCancel).setOnClickListener(this)
        etTitle = v.findViewById(R.id.etTitle)
        etCount = v.findViewById(R.id.etCount)
        cbRequired = v.findViewById(R.id.cbRequired)
        return v
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnAdd -> {
                if (etCount.text.toString().toIntOrNull() == null) {
                    return
                }
                vm.createItem(
                    locId,
                    etTitle.text.toString(),
                    etCount.text.toString().toInt(),
                    cbRequired.isChecked
                )
                dismiss()
            }
            R.id.btnCancel -> dismiss()
        }
    }

}