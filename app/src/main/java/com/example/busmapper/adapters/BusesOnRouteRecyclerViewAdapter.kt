package com.example.busmapper.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.busmapper.R
import com.example.busmapper.models.BusesOnRouteModel
import com.example.busmapper.models.PlaceModel

class BusesOnRouteRecyclerViewAdapter(val context: Context, val activity: Activity, private val arrayList: ArrayList<BusesOnRouteModel>) : RecyclerView.Adapter<BusesOnRouteRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val placeNameTextView: TextView = itemView.findViewById(R.id.place_name_textView)
        val stateNameTextView: TextView = itemView.findViewById(R.id.state_name_textView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.bus_on_route_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.placeNameTextView.text = arrayList[position].placeName
        holder.stateNameTextView.text = arrayList[position].stateName

        holder.placeNameTextView.setOnClickListener {
            val intent = Intent()
            intent.putExtra("name",arrayList[position].placeName)
            activity.setResult(Activity.RESULT_OK,intent)
            activity.finish()
        }
        holder.stateNameTextView.setOnClickListener {
            holder.placeNameTextView.callOnClick()
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}