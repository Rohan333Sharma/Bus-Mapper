package com.example.busmapper

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.busmapper.adapters.BusNumberRecyclerViewAdapter
import com.example.busmapper.models.BusModel
import com.google.firebase.firestore.FirebaseFirestore

class BusSearchByNumberActivity : AppCompatActivity() {

    lateinit var fireStore : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_search_by_number)

        val recyclerView = findViewById<RecyclerView>(R.id.bus_list_recyclerView)
        val searchEditText = findViewById<EditText>(R.id.search_editText)
        val context : Context = this
        fireStore = FirebaseFirestore.getInstance()

        recyclerView.layoutManager = LinearLayoutManager(this)

        searchEditText.requestFocus()

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fireStore.collection("Buses").get().addOnSuccessListener {
                    val buses = it.documents
                    val busNumberArray = ArrayList<BusModel>()
                    for (bus in buses)
                    {
                        if(bus.id.contains(s.toString(),true) && s.toString() != "")
                        {
                            busNumberArray.add(BusModel(bus.id,resources.getString(R.string.bus_path,bus.get("from"),bus.get("to"))))
                        }
                    }
                    recyclerView.adapter = BusNumberRecyclerViewAdapter(context,busNumberArray)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

    }
}