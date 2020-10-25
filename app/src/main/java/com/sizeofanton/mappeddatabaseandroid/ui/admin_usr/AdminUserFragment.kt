package com.sizeofanton.mappeddatabaseandroid.ui.admin_usr

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.AdminUserContract
import com.sizeofanton.mappeddatabaseandroid.data.local.EncryptedPreferences
import com.sizeofanton.mappeddatabaseandroid.data.pojo.UserInfo
import com.sizeofanton.mappeddatabaseandroid.ui.MainActivity
import org.koin.android.ext.android.inject

class AdminUserFragment : Fragment(), AdminUserContract.View {

    companion object {
        fun newInstance() =
            AdminUserFragment()
    }

    private val viewModel: AdminUserViewModel by inject()
    private lateinit var rvUsers: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        (activity as MainActivity).title = getString(R.string.manage_users)
        return inflater.inflate(R.layout.admin_users_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rvUsers = activity?.findViewById(R.id.rvUsers) as RecyclerView
        val adapter = AdminUserRvAdapter(
            activity as Context,
            listOf<UserInfo>()
        ).apply {
            bindView(this@AdminUserFragment as AdminUserContract.View)
            bindViewModel(viewModel)
        }
        rvUsers.adapter = adapter
        rvUsers.layoutManager = LinearLayoutManager(activity as Context)
        lifecycle.addObserver(viewModel)
        viewModel.getUsersList().observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })
        viewModel.getMessage().observe(viewLifecycleOwner, Observer {
            showSnack(it)
        })
        viewModel.getNetworkErrors().observe(viewLifecycleOwner, Observer {
            if (it > 5) backToLogin()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.admin_user_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_user -> {
                val dialog = CreateUserDialog(viewModel)
                dialog.show(requireFragmentManager(), "CreateUserDialog")
            }
            R.id.menu_back_to_map -> (activity as MainActivity).launchMapFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun addUserClicked(username: String, password: String, isAdmin: Boolean) =
        viewModel.addNewUser(username, password, isAdmin)

    override fun editUserClicked(id: Int, password: String, isAdmin: Boolean, isActive: Boolean) =
        viewModel.editUser(id, password, isAdmin, isActive)

    override fun deleteUserClicked(id: Int) = viewModel.deleteUser(id)


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

    override fun showSnack(msg: String) =
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG).show()


}
