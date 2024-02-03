package com.example.busmapper

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.busmapper.adapters.BusNumberRecyclerViewAdapter
import com.example.busmapper.adapters.PlacesRecyclerViewAdapter
import com.example.busmapper.models.PlaceModel
import com.google.firebase.firestore.FirebaseFirestore

class PlaceSearchActivity : AppCompatActivity() {

    private lateinit var fireStore : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_search)

        val recyclerView = findViewById<RecyclerView>(R.id.location_list_recyclerView)
        val searchEditText = findViewById<EditText>(R.id.location_search_editText)
        val context : Context = this
        val activity : Activity = this
        var locations : MutableMap<String,Any> = hashMapOf()
        fireStore = FirebaseFirestore.getInstance()

        recyclerView.layoutManager = LinearLayoutManager(this)

        fireStore.collection("Locations").document("Cities").get().addOnSuccessListener {
            locations = it.data!!
        }

        searchEditText.requestFocus()

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val locationArray = ArrayList<PlaceModel>()
                for (location in locations.keys)
                {
                    if(location.startsWith(s.toString(),true) && s.toString() != "")
                    {
                        locationArray.add(PlaceModel(location, locations[location].toString()))
                    }
                }
                recyclerView.adapter = PlacesRecyclerViewAdapter(context,activity,locationArray)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

    }
}