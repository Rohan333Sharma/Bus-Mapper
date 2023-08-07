package com.example.busmapper

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdaptor(val context: Context, val arrayList: ArrayList<BusModel>) : RecyclerView.Adapter<RecyclerViewAdaptor.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val busNumberTextView: TextView = itemView.findViewById(R.id.bus_number_textView)
        val busPathTextView: TextView = itemView.findViewById(R.id.bus_path_textView)
        val busCurrentLocationImageButton : ImageButton = itemView.findViewById(R.id.bus_current_location_imageButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.bus_view, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.busNumberTextView.text = arrayList[position].busNumber
        holder.busPathTextView.text = arrayList[position].busPath

        holder.busNumberTextView.setOnClickListener {
            val intent = Intent(context,BusDetailsActivity::class.java)
            context.startActivity(intent)
        }
        holder.busPathTextView.setOnClickListener {
            holder.busNumberTextView.callOnClick()
        }

        holder.busCurrentLocationImageButton.setOnClickListener {
            val intent = Intent(context,BusLocationActivity::class.java)
            intent.putExtra("busId", arrayList[position].busNumber)
            context.startActivity(intent)
        }

    }
}