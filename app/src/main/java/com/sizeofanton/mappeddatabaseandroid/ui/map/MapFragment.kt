package com.sizeofanton.mappeddatabaseandroid.ui.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.MapContract
import com.sizeofanton.mappeddatabaseandroid.data.local.CurrentUser
import com.sizeofanton.mappeddatabaseandroid.data.local.EncryptedPreferences
import com.sizeofanton.mappeddatabaseandroid.data.pojo.LocationInfo
import com.sizeofanton.mappeddatabaseandroid.ui.MainActivity
import kotlinx.android.synthetic.main.map_fragment.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class MapFragment :
    Fragment(),
    OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    MapContract.View,
    KoinComponent {

    private var mapView: MapView? = null
    var map: GoogleMap? = null
    private val markers = mutableListOf<Pair<Marker, Int>>()
    private var lastMarker: Marker? = null
    private var userLocation: LatLng? = null
    private lateinit var receiver: BroadcastReceiver

    private var showUserLocation: Boolean = true
    private var colorMarkers: Boolean = true

    companion object {
        fun newInstance() =
            MapFragment()
    }

    private val viewModel: MapViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.map_fragment, container, false)

        setHasOptionsMenu(true)

        mapView = v.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        mapView?.onResume()

        try {
            MapsInitializer.initialize(activity?.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        (activity as MainActivity).title = getString(R.string.locations)

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lifecycle.addObserver(viewModel)
        viewModel.getLocationsList().observe(viewLifecycleOwner, Observer {
            updateMarkersAndColors(it)
        })

        viewModel.getMessage().observe(viewLifecycleOwner, Observer {
            showSnack(it)
        })

        viewModel.getNetworkStatus().observe(viewLifecycleOwner, Observer {
            if (!it) backToLogin()
        })

        val encryptedPreferences = EncryptedPreferences.getInstance(activity as Context)
        val savedLatLng = LatLng(
            encryptedPreferences.getFloat("LATITUDE").toDouble(),
            encryptedPreferences.getFloat("LONGITUDE").toDouble()
        )
        if (userLocation == null) userLocation = savedLatLng


        receiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "ACTION_CURRENT_LOCATION") {
                    val latitude = intent.getDoubleExtra("LATITUDE", 0.0)
                    val longitude = intent.getDoubleExtra("LONGITUDE", 0.0)
                    if (latitude != 0.0 && longitude != 0.0)
                        userLocation = LatLng(latitude, longitude)
                }
            }
        }

        activity?.registerReceiver(receiver, IntentFilter("ACTION_CURRENT_LOCATION"))
    }

    override fun onStart() {
        super.onStart()
        showUserLocation =
            activity?.getPreferences(Context.MODE_PRIVATE)?.getBoolean("SHOW_MY_LOCATION", true)!!
        colorMarkers =
            activity?.getPreferences(Context.MODE_PRIVATE)?.getBoolean("COLOR_MARKERS", true)!!

        if (!showUserLocation) fabMyLocation.visibility = View.INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        if (!CurrentUser.checkIsAdmin()) menu.setGroupVisible(R.id.menu_admin_only, false)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> backToLogin()
            R.id.menu_manage_users -> (activity as MainActivity).launchAdminUsersFragment()
            R.id.menu_manage_locations -> (activity as MainActivity).launchAdminLocationsFragment()
            R.id.menu_settings -> (activity as MainActivity).launchSettingsFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.setOnMarkerClickListener(this)

        fabLocInfo.setOnClickListener {
            onLocationInfoClick()
        }

        fabMyLocation.setOnClickListener {
            myLocationClick()
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        lastMarker = marker
        marker.showInfoWindow()
        return true
    }

    override fun onLocationInfoClick() {
        if (lastMarker != null && lastMarker!!.title != "You") {
            (activity as MainActivity).launchBrowseLocationFragment(
                markers.find { it.first.title == lastMarker!!.title }!!.second,
                lastMarker!!.title
            )
        } else {
            Snackbar.make(requireView(), getString(R.string.please_select_location), Snackbar.LENGTH_LONG).show()
        }
    }

    override fun myLocationClick() {
        if (userLocation != null) {
            if (userLocation?.latitude != 0.0 || userLocation?.longitude != 0.0) {
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation, 10f)
                map?.animateCamera(cameraUpdate)
            }
        }
    }

    override fun updateMarkersAndColors(list: List<LocationInfo>) {
        if (list.isEmpty()) return
        markers.clear()
        for ((i, e) in list.withIndex()) {
            markers.add(
                Pair(
                    map?.addMarker(
                        MarkerOptions()
                            .position(LatLng(e.latitude, e.longitude))
                            .title(e.title)
                    )!!, e.id
                )
            )

            if (colorMarkers) {
                val color: Float = when (e.color) {
                    0 -> BitmapDescriptorFactory.HUE_GREEN
                    1 -> BitmapDescriptorFactory.HUE_YELLOW
                    2 -> BitmapDescriptorFactory.HUE_RED
                    else -> BitmapDescriptorFactory.HUE_RED
                }
                markers[i].first.setIcon(BitmapDescriptorFactory.defaultMarker(color))
            }
        }

        Timber.d("User location - lat: ${userLocation?.latitude} lng: ${userLocation?.longitude}")
        if (userLocation != null && showUserLocation) {
            if (userLocation?.latitude != 0.0 || userLocation?.longitude != 0.0) {
                map?.addMarker(MarkerOptions().position(userLocation!!).title("You"))!!
                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker))
            }
        }

    }

    override fun onDestroyView() {
        activity?.unregisterReceiver(receiver)
        viewModel.stopUpdateTimer()
        super.onDestroyView()
    }

    override fun showSnack(msg: String) = Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG).show()

    override fun backToLogin() {
        EncryptedPreferences
            .getInstance(activity as Context)
            .clearAuthData()
        viewModel.logout(CurrentUser.getUser())
        CurrentUser.clear()
        EncryptedPreferences.getInstance(activity as Context).clearLocationData()
        (activity as MainActivity).launchLoginFragment()
    }


}
