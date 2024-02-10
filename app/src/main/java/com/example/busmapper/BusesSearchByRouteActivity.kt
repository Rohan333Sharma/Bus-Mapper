package com.example.busmapper

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.busmapper.adapters.BusNumberRecyclerViewAdapter
import com.example.busmapper.adapters.BusesOnRouteRecyclerViewAdapter
import com.example.busmapper.models.BusModel
import com.example.busmapper.models.BusesOnRouteModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.Executors

class BusesSearchByRouteActivity : AppCompatActivity() {

    private lateinit var fireStore : FirebaseFirestore
    private lateinit var recyclerView : RecyclerView
    private lateinit var frameLayout: FrameLayout
    private lateinit var busesOnRouteArray : ArrayList<BusesOnRouteModel>
    private lateinit var totalBusesTextView : TextView
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buses_search_by_route)

        val fromToTextView = findViewById<TextView>(R.id.from_to_textView)
        val dateOfJourneyTextView = findViewById<TextView>(R.id.date_of_journey_textView)
        totalBusesTextView = findViewById(R.id.total_buses_textview)
        recyclerView = findViewById(R.id.buses_recyclerView)
        fireStore = FirebaseFirestore.getInstance()
        recyclerView.layoutManager = LinearLayoutManager(this)
        frameLayout = findViewById(R.id.progress)
        busesOnRouteArray = arrayListOf()

        fromToTextView.text = resources.getString(R.string.from_to,intent.getStringExtra("from"),intent.getStringExtra("to"))
        dateOfJourneyTextView.text = intent.getStringExtra("date")

        showBusesWithAnimation()
    }

    private fun showBusesWithAnimation()
    {
        frameLayout.visibility = View.VISIBLE
        fireStore.collection("Buses").get().addOnSuccessListener {
            val buses = it.documents
            var totalBuses = 0
            for (bus in buses) {
                if (bus.get("from")==intent.getStringExtra("from") && bus.get("to")==intent.getStringExtra("to"))
                {
                    totalBuses += 1
                    busesOnRouteArray.add(BusesOnRouteModel(bus.getString("boarding_time").toString(),bus.getString("dropping_time").toString(),bus.getString("price").toString()))
                }
            }
            if(totalBuses>1){
                totalBusesTextView.text = resources.getString(R.string.total_buses,totalBuses,"Buses")
            }
            else{
                totalBusesTextView.text = resources.getString(R.string.total_buses,totalBuses,"Bus")
            }
            recyclerView.adapter = BusesOnRouteRecyclerViewAdapter(context,this, busesOnRouteArray)
            frameLayout.visibility = View.GONE
        }
    }
}