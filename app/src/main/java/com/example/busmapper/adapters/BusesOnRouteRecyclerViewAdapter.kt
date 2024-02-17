package com.example.busmapper.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.busmapper.BusDetailsActivity
import com.example.busmapper.R
import com.example.busmapper.models.BusesOnRouteModel

class BusesOnRouteRecyclerViewAdapter(val context: Context, private val arrayList: ArrayList<BusesOnRouteModel>) : RecyclerView.Adapter<BusesOnRouteRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val timingsTextView: TextView = itemView.findViewById(R.id.timings_textView)
        val durationTextView: TextView = itemView.findViewById(R.id.duration_textView)
        val priceTextView: TextView = itemView.findViewById(R.id.price_textView)
        val companyNameTextView: TextView = itemView.findViewById(R.id.company_name_textView)
        val seatTypeTextView: TextView = itemView.findViewById(R.id.seat_types_textView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.bus_on_route_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.timingsTextView.text = context.resources.getString(R.string.from_to,arrayList[position].boardingTime, arrayList[position].droppingTime)
        holder.priceTextView.text = context.resources.getString(R.string.rupees,arrayList[position].price)
        holder.durationTextView.text = calculateDuration(arrayList[position].boardingTime, arrayList[position].droppingTime)
        holder.companyNameTextView.text = arrayList[position].companyName
        holder.seatTypeTextView.text = arrayList[position].seatType

        holder.itemView.setOnClickListener{
            val intent = Intent(context,BusDetailsActivity::class.java)
            intent.putExtra("busId",arrayList[position].busId)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    private fun calculateDuration(startTime : String, endTime : String) : String
    {
        val time1 = startTime.split(":")
        val time2 = endTime.split(":")
        var durationHours = 0
        var durationMinutes = 0

        if(time1[0]<time2[0])
        {
            durationHours = time2[0].toInt() - time1[0].toInt()
        }
        else if(time1[0]>time2[0])
        {
            durationHours = (24 - time1[0].toInt()) + time2[0].toInt()
        }
        else if(time1[0]==time2[0])
        {
            durationHours = 24
        }

        if(time1[1]<time2[1])
        {
            durationMinutes = time2[1].toInt() - time1[1].toInt()
        }
        else if(time1[1]>time2[1])
        {
            durationMinutes = 60 - (time1[1].toInt() - time2[1].toInt())
            durationHours -= 1
        }
        else if(time1[1]==time2[1])
        {
            durationMinutes = 0
        }

        return durationHours.toString()+"h "+durationMinutes.toString()+"m"
    }

}