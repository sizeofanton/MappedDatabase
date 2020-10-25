package com.sizeofanton.mappeddatabaseandroid.ui.location

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.LocationContract
import com.sizeofanton.mappeddatabaseandroid.data.local.EncryptedPreferences
import com.sizeofanton.mappeddatabaseandroid.data.pojo.ItemInfo
import com.sizeofanton.mappeddatabaseandroid.ui.MainActivity
import kotlinx.android.synthetic.main.location_fragment.*
import org.koin.core.KoinComponent
import org.koin.core.inject


class LocationFragment : Fragment(), LocationContract.View, KoinComponent {

    private var locationId = -1
    private var recyclerView: RecyclerView? = null

    companion object {
        fun newInstance() =
            LocationFragment()
    }

    private val viewModel: LocationViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.location_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView = activity?.findViewById(R.id.rvItems)
        val adapter = LocationRvAdapter(
            activity as Context,
            listOf<ItemInfo>()
        ).apply {
            bindViewModel(viewModel)
            bindFragmentManager(requireFragmentManager())
        }
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(activity)

        lifecycle.addObserver(viewModel)
        viewModel.getItemsList().observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
            pbItems.visibility = View.GONE
        })

        viewModel.getMessage().observe(viewLifecycleOwner, Observer {
            showSnack(it)
        })

        viewModel.getNetworkStatus().observe(viewLifecycleOwner, Observer {
            if (!it) backToLogin()
        })

    }

    override fun onStart() {
        super.onStart()
        if (arguments != null) {
            locationId = requireArguments().getInt("LOCATION_ID", -1)
            val locationTitle = requireArguments().getString("LOCATION_TITLE", "")
            (activity as MainActivity).title = locationTitle
        }
        viewModel.setLocationId(locationId)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.location_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_item -> {
                val dialog =
                    ItemCreateDialog(
                        locationId,
                        viewModel
                    )
                dialog.show(requireFragmentManager(), "Create item dialog")
            }
            R.id.menu_back_to_map -> (activity as MainActivity).launchMapFragment()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun createItemClick(
        locationId: Int,
        title: String,
        count: Int,
        isRequired: Boolean
    ) {
        viewModel.createItem(locationId, title, count, isRequired)
    }

    override fun editItemClick(id: Int, title: String, count: Int, isRequired: Boolean) {
        viewModel.editItem(id, title, count, isRequired)
    }

    override fun deleteItemClick(id: Int) {
        viewModel.deleteItem(id)
    }


    override fun showSnack(msg: String) {
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG).show()
    }

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
