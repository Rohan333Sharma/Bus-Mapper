package com.example.busmapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.FirebaseFirestore

class BusDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_details)

        val busIdTextView = findViewById<TextView>(R.id.bus_id_textView)
        val companyNameTextView = findViewById<TextView>(R.id.company_name_textView)
        val boardingTimeTextView = findViewById<TextView>(R.id.boarding_time_textView)
        val droppingTimeTextView = findViewById<TextView>(R.id.dropping_time_textView)
        val durationTextView = findViewById<TextView>(R.id.duration_textView)
        val boardingCityTextView = findViewById<TextView>(R.id.boarding_city_textView)
        val droppingCityTextView = findViewById<TextView>(R.id.dropping_city_textView)
        val busRouteLinear = findViewById<LinearLayout>(R.id.bus_route_linear)
        val fireStore = FirebaseFirestore.getInstance()

        val busId = intent.getStringExtra("busId").toString()

        busIdTextView.text = busId

        fireStore.collection("Buses").document(busId).get().addOnSuccessListener {
            companyNameTextView.text = it.getString("company")
            boardingTimeTextView.text = it.getString("boarding_time")
            droppingTimeTextView.text = it.getString("dropping_time")
            boardingCityTextView.text = it.getString("from")
            droppingCityTextView.text = it.getString("to")
            durationTextView.text = calculateDuration(it.getString("boarding_time").toString(),it.getString("dropping_time").toString())

            val amenities = it.getString("amenities").toString().split(",")
            for (amenity in amenities)
            {
                when (amenity){
                    "ac" -> findViewById<TextView>(R.id.ac_textView).visibility = View.VISIBLE
                    "cctv" -> findViewById<TextView>(R.id.cctv_textView).visibility = View.VISIBLE
                    "charging" -> findViewById<TextView>(R.id.charging_point_textView).visibility = View.VISIBLE
                    "water" -> findViewById<TextView>(R.id.water_bottle_textView).visibility = View.VISIBLE
                }
            }

            val busPath = it.getString("path").toString().split(",")
            for (i in busPath.indices)
            {
                val textView = TextView(this)
                val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                textView.layoutParams = layoutParams
                textView.textSize = 16f
                textView.text = busPath[i]
                textView.setPadding(20,0,0,0)
                textView.gravity = Gravity.CENTER_VERTICAL
                if(i==0)
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.starting,0,0,0)
                else if(i==busPath.size-1)
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ending,0,0,0)
                else
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.middle,0,0,0)
                textView.compoundDrawablePadding = 50
                busRouteLinear.addView(textView)
            }

        }

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