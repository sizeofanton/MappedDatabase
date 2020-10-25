package com.sizeofanton.mappeddatabaseandroid.ui.admin_loc

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.AdminLocContract
import com.sizeofanton.mappeddatabaseandroid.data.local.EncryptedPreferences
import com.sizeofanton.mappeddatabaseandroid.data.pojo.LocationInfo
import com.sizeofanton.mappeddatabaseandroid.ui.MainActivity
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class AdminLocFragment : Fragment(), AdminLocContract.View {

    companion object {
        fun newInstance() =
            AdminLocFragment()
    }

    private val viewModel: AdminLocViewModel by viewModel()
    private lateinit var rvLocations: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        (activity as MainActivity).title = getString(R.string.manage_locations)
        return inflater.inflate(R.layout.admin_locations_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rvLocations = activity?.findViewById(R.id.rvLocations)!!
        rvLocations.layoutManager = LinearLayoutManager(activity)
        val adapter =
            AdminLocRvAdapter(
                activity as Context,
                listOf<LocationInfo>()
            )
        rvLocations.adapter = adapter.apply {
            bindView(this@AdminLocFragment)
            bindViewModel(viewModel)
        }
        lifecycle.addObserver(viewModel)
        viewModel.getLocationsList().observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })
        viewModel.getMessage().observe(viewLifecycleOwner, Observer {
            showSnack(it)
        })
        viewModel.getNetworkStatus().observe(viewLifecycleOwner, Observer {
            if (it == false) backToLogin()
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.admin_loc_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_location -> {
                val dialog = CreateLocationDialog(viewModel)
                dialog.show(parentFragmentManager, "CreateLocationDialog")
            }
            R.id.menu_back_to_map -> (activity as MainActivity).launchMapFragment()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun editLocationClick(id: Int, title: String, latitude: Double, longitude: Double) =
        viewModel.editLocation(id, title, latitude, longitude)

    override fun deleteLocationClick(id: Int) =
        viewModel.deleteLocation(id)

    override fun addLocationClick(title: String, latitude: Double, longitude: Double) =
        viewModel.addLocation(title, latitude, longitude)



    override fun showSnack(msg: String) =
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG).show()


    override fun backToLogin() {
        Snackbar.make(
            requireView(),
            getString(R.string.too_many_network_errors),
            Snackbar.LENGTH_LONG
        ).show()
        EncryptedPreferences
            .getInstance(activity as Context)
            .clearAuthData()
        (activity as MainActivity).launchLoginFragment()
    }
}

