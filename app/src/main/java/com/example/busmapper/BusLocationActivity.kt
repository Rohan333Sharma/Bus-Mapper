package com.example.busmapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.busmapper.databinding.ActivityBusLocationBinding
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class BusLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityBusLocationBinding
    private  lateinit var fireStore : FirebaseFirestore
    private  lateinit var firebase : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBusLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fireStore = FirebaseFirestore.getInstance()
        firebase = Firebase.database

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.bus_location_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        mMap = googleMap
        val busId = intent.getStringExtra("busId").toString()

        val firebaseReference = firebase.getReference(busId)

        firebaseReference.get().addOnSuccessListener {
            val lat = it.child("latitude").value.toString().toDouble()
            val lng = it.child("longitude").value.toString().toDouble()
            val busCurrentLocation = LatLng(lat, lng)
            val height = 130
            val width = 130
            val b = BitmapFactory.decodeResource(resources, R.drawable.icon_bus_3d)
            val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
            val icon = BitmapDescriptorFactory.fromBitmap(smallMarker)
            val marker = mMap.addMarker(MarkerOptions().position(busCurrentLocation).title(busId).icon(icon))!!
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(busCurrentLocation, 16f))

            firebaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    changeLocation(busId,marker)

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
    }
    private fun changeLocation(busId : String, marker : Marker) {

        firebase.getReference(busId).get().addOnSuccessListener {

            val lat = it.child("latitude").value.toString().toDouble()
            val lng = it.child("longitude").value.toString().toDouble()
            val busCurrentLocation = LatLng(lat, lng)
            marker.position = busCurrentLocation
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(busCurrentLocation,16f))

        }

    }
}