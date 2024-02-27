package com.example.busmapper

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.SphericalUtil

class BusStopFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private var longitude : Double = 0.0
    private var latitude : Double = 0.0
    private lateinit var currentLocation : LatLng
    private lateinit var fireStore : FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val fragmentView = inflater.inflate(R.layout.fragment_bus_stop, container, false)

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

        // Inflate the layout for this fragment
        return fragmentView
    }

    private fun getNearbyBuses(map : GoogleMap) {

        val icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_stop_marker)

        fireStore.collection("Bus_Stops").get().addOnCompleteListener { it ->
            if (it.isSuccessful) {
                val buses = it.result.documents
                for (bus in buses) {
                    val lat = bus.getString("latitude")!!.toDouble()
                    val lng = bus.getString("longitude")!!.toDouble()
                    val busCurrentLocation = LatLng(lat, lng)
                    val distance = SphericalUtil.computeDistanceBetween(currentLocation, busCurrentLocation)
                    if (distance < 10000.0) {
                        map.addMarker(MarkerOptions().position(busCurrentLocation).icon(icon))
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
        getNearbyBuses(map)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,14.5f))
//        map.setOnMarkerClickListener{
//            intent = Intent(requireContext(),NearbyBusActivity::class.java)
//            startActivity(intent)
//            true
//        }
    }

}