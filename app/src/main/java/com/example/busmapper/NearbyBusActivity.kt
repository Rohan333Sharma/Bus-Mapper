package com.example.busmapper

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.busmapper.databinding.ActivityNearbyBusBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.SphericalUtil

class NearbyBusActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var binding: ActivityNearbyBusBinding
    private  lateinit var fireStore : FirebaseFirestore
    private  lateinit var firebase : FirebaseDatabase
    private lateinit var currentLocation : LatLng
    private var latitude = 0.0
    private var longitude = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNearbyBusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fireStore = FirebaseFirestore.getInstance()
        firebase = Firebase.database

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                val location = it
                longitude = location.longitude
                latitude = location.latitude
                mapFragment.getMapAsync(this)
            }
        }
    }

    private fun getNearbyBuses(map : GoogleMap) {

        val icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_bus)

        fireStore.collection("Buses").get().addOnCompleteListener { its ->
            if (its.isSuccessful) {
                val buses = its.result.documents
                for (bus in buses) {
                    val firebaseReference = firebase.getReference(bus.id)
                    firebaseReference.get().addOnSuccessListener {
                        val lat = it.child("latitude").value.toString().toDouble()
                        val lng = it.child("longitude").value.toString().toDouble()
                        val busCurrentLocation = LatLng(lat, lng)
                        val distance = SphericalUtil.computeDistanceBetween(currentLocation, busCurrentLocation)
                        if (distance < 8000.0) {
                            val marker = map.addMarker(MarkerOptions().position(busCurrentLocation).title(bus.id).snippet(resources.getString(R.string.bus_path,bus.getString("from"),bus.getString("to"))).icon(icon))!!
                            firebaseReference.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    changeLocation(bus.id,marker)

                                }

                                override fun onCancelled(error: DatabaseError) {
                                }

                            })
                        }
                    }
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        map.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).title("You"))
        currentLocation = LatLng(latitude,longitude)
        getNearbyBuses(map)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,14.5f))
    }

    private fun changeLocation(busId : String, marker : Marker) {

        firebase.getReference(busId).get().addOnSuccessListener {

            val lat = it.child("latitude").value.toString().toDouble()
            val lng = it.child("longitude").value.toString().toDouble()
            val busCurrentLocation = LatLng(lat, lng)
            marker.position = busCurrentLocation

        }
    }

}