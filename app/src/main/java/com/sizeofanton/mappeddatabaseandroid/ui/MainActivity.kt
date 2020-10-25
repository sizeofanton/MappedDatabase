package com.sizeofanton.mappeddatabaseandroid.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.data.local.CurrentUser
import com.sizeofanton.mappeddatabaseandroid.data.local.EncryptedPreferences
import com.sizeofanton.mappeddatabaseandroid.data.remote.NetworkService
import com.sizeofanton.mappeddatabaseandroid.data.service.CurrentLocationService
import com.sizeofanton.mappeddatabaseandroid.data.service.MessagingService
import com.sizeofanton.mappeddatabaseandroid.ui.admin_loc.AdminLocFragment
import com.sizeofanton.mappeddatabaseandroid.ui.admin_usr.AdminUserFragment
import com.sizeofanton.mappeddatabaseandroid.ui.create_account.CreateAccountFragment
import com.sizeofanton.mappeddatabaseandroid.ui.location.LocationFragment
import com.sizeofanton.mappeddatabaseandroid.ui.login.LoginFragment
import com.sizeofanton.mappeddatabaseandroid.ui.map.MapFragment
import com.sizeofanton.mappeddatabaseandroid.ui.settings.SettingsFragment
import com.sizeofanton.mappeddatabaseandroid.util.HttpStatusCode
import kotlinx.android.synthetic.main.main_activity.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity(), KoinComponent {

    private val networkService: NetworkService by inject()

    private var firebaseTokenSent = false
    private var userLanguageSent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val preferencesInit = getPreferences(Context.MODE_PRIVATE)
            .getBoolean("IS_INIT",false)
        if (!preferencesInit) initPreferences()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, LoginFragment.newInstance())
                    .commitNow()
        }
    }


    fun launchMapFragment() {
        requestPermission()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MapFragment.newInstance())
            .commitNow()

        if (!userLanguageSent) sendUserLanguage()
        if (!firebaseTokenSent) sendFirebaseToken()
    }

    fun launchBrowseLocationFragment(i: Int, t: String) {
        val args = Bundle()
        args.putInt("LOCATION_ID", i)
        args.putString("LOCATION_TITLE", t)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, LocationFragment.newInstance().apply {
                arguments = args
            })
            .commitNow()
    }

    fun launchAdminUsersFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AdminUserFragment.newInstance())
            .commitNow()
    }

    fun launchAdminLocationsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AdminLocFragment.newInstance())
            .commitNow()
    }

    fun launchLoginFragment() {
        firebaseTokenSent = false
        userLanguageSent = false
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, LoginFragment.newInstance())
            .commitNow()
    }

    fun launchNewAccountFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, CreateAccountFragment.newInstance())
            .commitNow()
    }

    fun launchSettingsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SettingsFragment.newInstance())
            .commitNow()
    }

    override fun onDestroy() {
        CurrentUser.clear()
        super.onDestroy()
    }

    private fun requestPermission(){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        else startService(Intent(this, CurrentLocationService::class.java))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startService(Intent(this, CurrentLocationService::class.java))
                }
            }
        }
    }

    private fun sendUserLanguage() {
        val language = Locale.getDefault().language
        networkService.getAuthApi().setUserLanguage(CurrentUser.getToken(),  language)
            .enqueue(object: Callback<String>{
                override fun onFailure(call: Call<String>, t: Throwable) {
                    showSnack("Error sending user language to server")
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.code() == HttpStatusCode.OK) userLanguageSent = true
                }
            })
    }

    private fun sendFirebaseToken() {
        val firebaseToken = MessagingService.getToken(this)
        networkService.getAuthApi().registerFirebaseToken(CurrentUser.getToken(), firebaseToken)
            .enqueue(object: Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    showSnack("Error sending firebase token to server")
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.code() == HttpStatusCode.OK) firebaseTokenSent = true
                }
            })
    }

    private fun showSnack(msg: String) = Snackbar.make(container, msg, Snackbar.LENGTH_LONG).show()


    private fun initPreferences() {
        getPreferences(Context.MODE_PRIVATE).edit().apply {
            putBoolean("NOTIFICATIONS", true)
            putBoolean("COLOR_MARKERS", true)
            putBoolean("SHOW_MY_LOCATION", true)
            putBoolean("IS_INIT", true)
            apply()
        }
    }
}
