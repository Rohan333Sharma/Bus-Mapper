package com.example.busmapper

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.FrameLayout
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
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buses_search_by_route)

        recyclerView = findViewById(R.id.buses_recyclerView)
        fireStore = FirebaseFirestore.getInstance()
        recyclerView.layoutManager = LinearLayoutManager(this)
        frameLayout = findViewById(R.id.progress)
        Log.d("Tag123","busesOnRouteArraytoString()")
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            frameLayout.visibility = View.VISIBLE
            fireStore.collection("Buses").get().addOnSuccessListener {
                val buses = it.documents
                val busesOnRouteArray = ArrayList<BusesOnRouteModel>()
                for (bus in buses) {
                    fireStore.collection("Buses").document(bus.id).get().addOnSuccessListener {
                        run {
                            if (it.get("from")==intent.getStringExtra("from") && it.get("to")==intent.getStringExtra("to")) {
                                busesOnRouteArray.add(BusesOnRouteModel(bus.id, resources.getString(R.string.bus_path,bus.get("from"),bus.get("to"))))
                            }
                        }
                    }
                }
                Log.d("Tag123",busesOnRouteArray.toString())
                recyclerView.adapter = BusesOnRouteRecyclerViewAdapter(context,this, busesOnRouteArray)
            }
            handler.post {
                frameLayout.visibility = View.GONE
            }
        }

    }

    private fun progressTask()
    {
        Thread {
            frameLayout.visibility = View.VISIBLE
            fireStore.collection("Buses").get().addOnSuccessListener {
                val buses = it.documents
                val busesOnRouteArray = ArrayList<BusesOnRouteModel>()
                for (bus in buses) {
                    fireStore.collection("Buses").document(bus.id).get().addOnSuccessListener {
                        run {
                            if (it.get("from")==intent.getStringExtra("from") && it.get("to")==intent.getStringExtra("to")) {
                                busesOnRouteArray.add(BusesOnRouteModel(bus.id, resources.getString(R.string.bus_path,bus.get("from"),bus.get("to"))))
                            }
                        }
                    }
                }
                Log.d("Tag123",busesOnRouteArray.toString())
                recyclerView.adapter = BusesOnRouteRecyclerViewAdapter(context,this, busesOnRouteArray)
            }

            runOnUiThread {
                frameLayout.visibility = View.GONE
            }
        }
    }
}