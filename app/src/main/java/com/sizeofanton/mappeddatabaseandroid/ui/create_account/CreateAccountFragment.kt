package com.sizeofanton.mappeddatabaseandroid.ui.create_account

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.CreateAccountContract
import com.sizeofanton.mappeddatabaseandroid.ui.MainActivity
import com.sizeofanton.mappeddatabaseandroid.util.ext.hideKeyboard
import kotlinx.android.synthetic.main.create_account_fragment.*

class CreateAccountFragment : Fragment(), CreateAccountContract.View {

    companion object {
        fun newInstance() = CreateAccountFragment()
    }

    private lateinit var viewModel: CreateAccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.create_account_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CreateAccountViewModel::class.java)

        viewModel.getMessage().observe(viewLifecycleOwner, Observer {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
            etUserName.setText("")
            etPassword.setText("")
            etPasswordRepeat.setText("")
        })
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).title = "Create new account"
        btnCreateAccount.setOnClickListener {
            createAccountClick()
        }
    }

    override fun createAccountClick() {
        hideKeyboard()

        val password = etPassword.text.toString()
        val passwordRepeat = etPasswordRepeat.text.toString()

        if (password != passwordRepeat) {
            Snackbar.make(requireView(), "Password don't match", Snackbar.LENGTH_LONG).show()
            return
        }

        viewModel.createAccount(
            etUserName.text.toString(),
            password
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.add(0, 1, 1, "Back to Login")
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> (activity as MainActivity).launchLoginFragment()
        }
        return super.onOptionsItemSelected(item)
    }
}