package com.example.busmapper.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
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

        val timingsTextView: TextView = itemView.findViewById(R.id.timings_textView)
        val durationTextView: TextView = itemView.findViewById(R.id.duration_textView)
        val priceTextView: TextView = itemView.findViewById(R.id.price_textView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.bus_on_route_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.timingsTextView.text = context.resources.getString(R.string.from_to,arrayList[position].boardingTime, arrayList[position].droppingTime)
        holder.priceTextView.text = context.resources.getString(R.string.rupees,arrayList[position].price)
        holder.durationTextView.text = (arrayList[position].droppingTime.split(":")[0].toInt() - arrayList[position].boardingTime.split(":")[0].toInt()).toString()

        holder.timingsTextView.setOnClickListener {
//            val intent = Intent()
//            intent.putExtra("name",arrayList[position].placeName)
//            activity.setResult(Activity.RESULT_OK,intent)
//            activity.finish()
        }
        holder.priceTextView.setOnClickListener {
            holder.timingsTextView.callOnClick()
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}