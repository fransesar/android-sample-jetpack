package com.example.jetpack.ui.listjetpack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.jetpack.R
import com.example.jetpack.databinding.ItemJetpackBinding
import com.example.jetpack.data.JetpackModel
import com.example.jetpack.util.JetpackClickListener
import kotlinx.android.synthetic.main.item_jetpack.view.*

class JetpackListAdapter(private val jetpackList: ArrayList<JetpackModel>) : RecyclerView.Adapter<JetpackListAdapter.JetpackViewHolder>(),
    JetpackClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JetpackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemJetpackBinding>(inflater, R.layout.item_jetpack, parent, false)
        return JetpackViewHolder(
            view
        )
    }

    override fun getItemCount() = jetpackList.size

    override fun onBindViewHolder(holder: JetpackViewHolder, position: Int) {
        holder.view.jetpack = jetpackList[position]
        holder.view.listener = this
    }

    class JetpackViewHolder(var view: ItemJetpackBinding) : RecyclerView.ViewHolder(view.root)

    override fun onJetpackClicked(v: View) {
        val uuid = v.jetpackId.text.toString().toInt()
        val action =
            ListFragmentDirections.actionDetailFragment(
                uuid
            )
        Navigation.findNavController(v).navigate(action)
    }

    fun updateJetpackList(newJetpackList: List<JetpackModel>) {
        jetpackList.clear()
        jetpackList.addAll(newJetpackList)
        notifyDataSetChanged()
    }
}