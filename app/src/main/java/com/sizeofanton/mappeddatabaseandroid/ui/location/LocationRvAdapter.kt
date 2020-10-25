package com.sizeofanton.mappeddatabaseandroid.ui.location

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.LocationContract
import com.sizeofanton.mappeddatabaseandroid.data.pojo.ItemInfo

class LocationRvAdapter(
    private val context: Context,
    private var list: List<ItemInfo>
) : RecyclerView.Adapter<LocationRvAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var viewModel: LocationContract.ViewModel
    private lateinit var fragmentManager: FragmentManager

    fun bindViewModel(vm: LocationContract.ViewModel) {
        viewModel = vm
    }

    fun bindFragmentManager(fm: FragmentManager) {
        fragmentManager = fm
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun attachViewModel(vm: LocationContract.ViewModel) {

    }

    fun setData(items: List<ItemInfo>) {
        list = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_card, parent, false)
        return ViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.id = item.id
        holder.tvItemTitle.text = item.title
        holder.tvCount.text = item.count.toString()
        holder.cbRequired.isChecked = item.required
        holder.btnEditItem.setOnClickListener {

            val dialog =
                ItemEditDialog(
                    holder.id,
                    holder.tvItemTitle.text.toString(),
                    holder.tvCount.text.toString().toInt(),
                    holder.cbRequired.isEnabled,
                    viewModel
                )

            dialog.show(fragmentManager, "")
        }

        holder.btnDeleteItem.setOnClickListener {
            viewModel.deleteItem(holder.id)
        }
    }



    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvItemTitle: TextView = v.findViewById(R.id.tvItemTitle)
        val tvCount: TextView = v.findViewById(R.id.tvCount)
        val cbRequired: CheckBox = v.findViewById(R.id.cbRequired)
        val btnEditItem: Button = v.findViewById(R.id.btnEditItem)
        val btnDeleteItem: Button = v.findViewById(R.id.btnDeleteItem)
        var id = -1
    }
}
