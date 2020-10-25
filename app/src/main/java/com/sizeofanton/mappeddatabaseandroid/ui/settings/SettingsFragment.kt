package com.sizeofanton.mappeddatabaseandroid.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.SettingsContract
import com.sizeofanton.mappeddatabaseandroid.data.local.CurrentUser
import com.sizeofanton.mappeddatabaseandroid.data.service.MessagingService
import com.sizeofanton.mappeddatabaseandroid.ui.MainActivity
import kotlinx.android.synthetic.main.settings_fragment.*
import org.koin.core.KoinComponent
import org.koin.core.inject

const val SHOW_MY_LOCATION = "SHOW_MY_LOCATION"
const val COLOR_MARKERS = "COLOR_MARKERS"
const val NOTIFICATION = "NOTIFICATION"

class SettingsFragment : Fragment(), SettingsContract.View, KoinComponent {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private val viewModel: SettingsViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v =  inflater.inflate(R.layout.settings_fragment, container, false)
        setHasOptionsMenu(true)
        (activity as MainActivity).title = getString(R.string.settings)
        v.findViewById<TextView>(R.id.tvUsername).text = CurrentUser.getUser()
        return v
    }

    override fun onStart() {
        super.onStart()
        initUI()
    }

    private fun initUI() {
        val showMyLocation = getPreferenceValue(SHOW_MY_LOCATION)
        swMyLocation.isChecked = showMyLocation

        val colorMarkers = getPreferenceValue(COLOR_MARKERS)
        swColorMarkers.isChecked = colorMarkers

        val notification = getPreferenceValue(NOTIFICATION)
        swNotifications.isChecked = notification


        swMyLocation.setOnCheckedChangeListener { _, b ->
            if (b) {
                setPreferenceValue(SHOW_MY_LOCATION, true)
            } else {
                setPreferenceValue(SHOW_MY_LOCATION, false)
            }
        }

        swColorMarkers.setOnCheckedChangeListener { _, b ->
            if (b) {
                setPreferenceValue(COLOR_MARKERS, true)
            } else {
                setPreferenceValue(COLOR_MARKERS, false)
            }
        }

        swNotifications.setOnCheckedChangeListener { _, b ->
            if (b) {
                setPreferenceValue(NOTIFICATION, true)
                MessagingService.enableNotification = true
            } else {
                setPreferenceValue(NOTIFICATION, false)
                MessagingService.enableNotification = false
            }
        }

        btnChangePassword.setOnClickListener {
            val dialog = ChangePassDialog(viewModel)
            dialog.show(requireFragmentManager(), "Change password dialog")
        }

        viewModel.getMessage().observe(viewLifecycleOwner, Observer {
            showSnack(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settings_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_back_to_map -> (activity as MainActivity).launchMapFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setPreferenceValue(id: String, b: Boolean) {
        activity?.getPreferences(Context.MODE_PRIVATE)?.edit()?.apply {
            putBoolean(id, b)
            apply()
        }
    }

    private fun getPreferenceValue(id: String): Boolean {
        return activity?.getPreferences(Context.MODE_PRIVATE)?.getBoolean(id, false) ?: false
    }

    private fun showSnack(msg: String) =
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG).show()

}