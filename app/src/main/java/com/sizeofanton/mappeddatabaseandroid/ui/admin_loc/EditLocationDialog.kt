package com.sizeofanton.mappeddatabaseandroid.ui.admin_loc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.AdminLocContract
import com.sizeofanton.mappeddatabaseandroid.data.pojo.LocationInfo

class EditLocationDialog(
    private val vm: AdminLocContract.ViewModel,
    private val loc: LocationInfo
):  DialogFragment(),
    View.OnClickListener {

    private lateinit var etLocationTitle: EditText
    private lateinit var etLatitude: EditText
    private lateinit var etLongitude: EditText


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.location_edit_dialog, null)
        etLocationTitle = v.findViewById(R.id.etLocationTitle)
        etLatitude = v.findViewById(R.id.etLatitude)
        etLongitude = v.findViewById(R.id.etLongitude)

        etLocationTitle.setText(loc.title)
        etLatitude.setText(loc.latitude.toString())
        etLongitude.setText(loc.longitude.toString())

        v.findViewById<Button>(R.id.btnAdd).setOnClickListener(this)
        v.findViewById<Button>(R.id.btnCancel).setOnClickListener(this)

        return v
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnAdd -> {
                vm.editLocation(
                    loc.id,
                    etLocationTitle.text.toString(),
                    etLatitude.text.toString().toDouble(),
                    etLongitude.text.toString().toDouble()
                )
                dismiss()
            }

            R.id.btnCancel -> dismiss()
        }
    }

}