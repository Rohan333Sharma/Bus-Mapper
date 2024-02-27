package com.example.busmapper

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.busmapper.adapters.BusStopRecyclerViewAdapter
import com.example.busmapper.models.PlaceModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.SphericalUtil

class BusStopFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private var longitude : Double = 0.0
    private var latitude : Double = 0.0
    private lateinit var currentLocation : LatLng
    private lateinit var fireStore : FirebaseFirestore
    private lateinit var busStopSearchEditText : EditText
    private lateinit var mMap: GoogleMap
    private lateinit var marker: Marker
    private var initial = true
    private lateinit var icon : BitmapDescriptor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val fragmentView = inflater.inflate(R.layout.fragment_bus_stop, container, false)
        busStopSearchEditText = fragmentView.findViewById(R.id.bus_stop_search_editText)
        val recyclerView = fragmentView.findViewById<RecyclerView>(R.id.stop_list_recyclerView)
        var stops : MutableList<DocumentSnapshot> = mutableListOf()
        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_stop_marker)

        fireStore = FirebaseFirestore.getInstance()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.bus_stop_map) as SupportMapFragment
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                val location = it
                longitude = location.longitude
                latitude = location.latitude
                mapFragment.getMapAsync(this)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fireStore.collection("Bus_Stops").get().addOnSuccessListener {
            stops = it.documents
        }

        busStopSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val busStopArray = ArrayList<PlaceModel>()
                for (stop in stops)
                {
                    if(stop.id.contains(s.toString(),true) && s.toString() != "")
                    {
                        recyclerView.visibility = View.VISIBLE
                        busStopArray.add((PlaceModel(stop.id, stop.getString("state").toString())))
                    }
                    else
                    {
                        recyclerView.visibility = View.GONE
                    }

                }
                recyclerView.adapter = BusStopRecyclerViewAdapter(requireContext(),recyclerView,busStopArray,this@BusStopFragment)

            }

            override fun afterTextChanged(s: Editable?) {
            }

        })


        // Inflate the layout for this fragment
        return fragmentView
    }

    private fun getNearbyStops(map : GoogleMap) {

        fireStore.collection("Bus_Stops").get().addOnCompleteListener {
            if (it.isSuccessful) {
                val buses = it.result.documents
                for (bus in buses) {
                    val lat = bus.getString("latitude")!!.toDouble()
                    val lng = bus.getString("longitude")!!.toDouble()
                    val stopLocation = LatLng(lat, lng)
                    val distance = SphericalUtil.computeDistanceBetween(currentLocation, stopLocation)
                    if (distance < 10000.0) {
                        map.addMarker(MarkerOptions().position(stopLocation).icon(icon))
                    }
                }
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(map: GoogleMap) {
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
        map.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).title("You"))
        currentLocation = LatLng(latitude,longitude)
        mMap = map
        getNearbyStops(map)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,14.5f))
    }

    fun locateStop(stop :String)
    {
        busStopSearchEditText.setText(stop)

        fireStore.collection("Bus_Stops").document(stop).get().addOnSuccessListener{

            val lat = it.getString("latitude")!!.toDouble()
            val lng = it.getString("longitude")!!.toDouble()
            val stopLocation = LatLng(lat, lng)

            if(initial)
            {
                marker = mMap.addMarker(MarkerOptions().position(stopLocation).title(it.id).icon(icon))!!
                initial = false
            }
            else
            {
                marker.position = stopLocation
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stopLocation,15f))
        }
    }

}