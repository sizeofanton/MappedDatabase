package com.sizeofanton.mappeddatabaseandroid.ui.admin_loc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.AdminLocContract
import com.sizeofanton.mappeddatabaseandroid.data.pojo.LocationInfo
import org.koin.core.KoinComponent
import org.koin.core.get

class AdminLocRvAdapter(private val context: Context, private var locations: List<LocationInfo>) : RecyclerView.Adapter<AdminLocRvAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var view: AdminLocContract.View
    private lateinit var vm: AdminLocContract.ViewModel

    fun bindView(v: AdminLocContract.View) {
        this.view = v
    }

    fun bindViewModel(vm: AdminLocContract.ViewModel) {
        this.vm = vm
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.location_card, parent, false)
        return ViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = locations.get(position)
        holder.id = location.id
        holder.tvLocTitle.text = location.title
        holder.tvLatitude.text = location.latitude.toString()
        holder.tvLongitude.text = location.longitude.toString()
        holder.btnEditLoc.setOnClickListener {
            val dialog = EditLocationDialog(vm, location)
            dialog.show((context as AppCompatActivity).supportFragmentManager, "EditLocationDialog")
        }

        holder.btnDeleteLoc.setOnClickListener {
            view.deleteLocationClick(holder.id)
        }
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val tvLocTitle: TextView = v.findViewById(R.id.tvLocTitle)
        val tvLatitude: TextView = v.findViewById(R.id.tvLatitude)
        val tvLongitude: TextView = v.findViewById(R.id.tvLongitude)
        val btnEditLoc: Button = v.findViewById(R.id.btnEditLoc)
        val btnDeleteLoc: Button = v.findViewById(R.id.btnDeleteLoc)
        var id: Int = -1
    }

    fun setData(items: List<LocationInfo>) {
        locations = items
        notifyDataSetChanged()
    }
}
