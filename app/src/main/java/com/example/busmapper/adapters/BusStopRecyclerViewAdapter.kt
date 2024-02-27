package com.example.busmapper.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.busmapper.BusStopFragment
import com.example.busmapper.R
import com.example.busmapper.models.PlaceModel

class BusStopRecyclerViewAdapter(val context: Context, private val recyclerView: RecyclerView, private val arrayList: ArrayList<PlaceModel>, private val fragment: BusStopFragment) : RecyclerView.Adapter<BusStopRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val placeNameTextView: TextView = itemView.findViewById(R.id.place_name_textView)
        val stateNameTextView: TextView = itemView.findViewById(R.id.state_name_textView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.place_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.placeNameTextView.text = arrayList[position].placeName
        holder.stateNameTextView.text = arrayList[position].stateName

        holder.placeNameTextView.setOnClickListener {
            fragment.locateStop(arrayList[position].placeName)
            recyclerView.visibility = View.GONE

        }
        holder.stateNameTextView.setOnClickListener {
            holder.placeNameTextView.callOnClick()
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}