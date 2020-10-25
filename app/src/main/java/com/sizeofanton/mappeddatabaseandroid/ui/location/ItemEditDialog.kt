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

class ItemEditDialog(
    val itemId: Int,
    val title: String,
    val count: Int,
    val isRequired: Boolean,
    val vm: LocationContract.ViewModel
): DialogFragment(), View.OnClickListener {

    private lateinit var etNewTitle: EditText
    private lateinit var etNewCount: EditText
    private lateinit var cbRequired: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setTitle("Edit item")
        val v = inflater.inflate(R.layout.item_edit_dialog, null)
        v.findViewById<Button>(R.id.btnEdit).setOnClickListener(this)
        v.findViewById<Button>(R.id.btnCancel).setOnClickListener(this)
        etNewTitle = v.findViewById(R.id.etNewTitle)
        etNewCount = v.findViewById(R.id.etNewCount)
        cbRequired = v.findViewById(R.id.cbRequired)



        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        etNewTitle.setText(title)
        etNewCount.setText(count.toString())
        cbRequired.isChecked = isRequired
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnEdit -> {
                vm.editItem(
                    itemId,
                    etNewTitle.text.toString(),
                    etNewCount.text.toString().toInt(),
                    cbRequired.isChecked
                )
                dismiss()
            }

            R.id.btnCancel -> dismiss()
        }
    }

}