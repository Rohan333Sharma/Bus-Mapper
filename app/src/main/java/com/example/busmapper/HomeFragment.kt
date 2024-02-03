package com.example.busmapper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.SphericalUtil
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView : MapView
    private var MAPVIEW_BUNDLE_KEY : String= "MapViewBundleKey";
    lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    var longitude : Double = 0.0
    var latitude : Double = 0.0
    private lateinit var currentLocation : LatLng
    lateinit var fireStore : FirebaseFirestore
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var nearbyBuses : ArrayList<LatLng>
    private lateinit var fromTextView : TextView
    private lateinit var toTextView : TextView
    private var placeNameForTextview = ""
    lateinit var intent: Intent
    private val startActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK)
        {
            if(placeNameForTextview == "from")
            {
                fromTextView.text = it.data?.getStringExtra("name").toString()
            }
            else
            {
                toTextView.text = it.data?.getStringExtra("name").toString()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val fragmentView = inflater.inflate(R.layout.fragment_home, container, false)
        val searchTextView = fragmentView.findViewById<TextView>(R.id.search_textView)
        val calendarHintTextView = fragmentView.findViewById<TextView>(R.id.calender_hint_textView)
        val calendarTextView = fragmentView.findViewById<TextView>(R.id.calender_textView)
        val swapImage = fragmentView.findViewById<ImageView>(R.id.swap_image)
        val calendar = Calendar.getInstance()
        val searchBusButton = fragmentView.findViewById<Button>(R.id.search_button)
        val toggleImageButton = fragmentView.findViewById<ImageButton>(R.id.toggle_imageButton)
        val calendarView = fragmentView.findViewById<CalendarView>(R.id.calendarView)
        val simpleDateFormat = SimpleDateFormat("E, dd MMM",Locale.ENGLISH)
        fromTextView = fragmentView.findViewById(R.id.from_textView)
        toTextView = fragmentView.findViewById(R.id.to_textView)
        mapView = fragmentView.findViewById(R.id.mapView)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fireStore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        nearbyBuses = ArrayList()
        calendarView.visibility = View.GONE
        calendarView.minDate = System.currentTimeMillis()
        calendarView.maxDate = System.currentTimeMillis() + 5259600000

        getLastLocation()

        toggleImageButton.setOnClickListener{
            val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.open()
        }

        searchTextView.setOnClickListener {
            intent = Intent(requireActivity(),BusSearchByNumberActivity::class.java)
            Thread.sleep(50)
            startActivity(intent)
        }

        fromTextView.setOnClickListener {
            Thread.sleep(50)
            placeNameForTextview = "from"
            startActivityResult.launch(Intent(context,PlaceSearchActivity::class.java))
        }
        toTextView.setOnClickListener {
            Thread.sleep(50)
            placeNameForTextview = "to"
            startActivityResult.launch(Intent(context,PlaceSearchActivity::class.java))
        }
        calendarTextView.setOnClickListener {
            if(calendarView.visibility == View.GONE)
            {
                calendarView.visibility = View.VISIBLE
            }
            else if (calendarView.visibility == View.VISIBLE)
            {
                calendarView.visibility = View.GONE
            }

            calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                calendar.set(year,month,dayOfMonth)
                val time = simpleDateFormat.format(calendar.time)
                calendarTextView.text = time
                calendarView.visibility = View.GONE
            }
        }

        calendarTextView.text = simpleDateFormat.format(calendar.time)
        calendarHintTextView.text = resources.getString(R.string.date_of_journey)

        swapImage.setOnClickListener {
            val str = fromTextView.text
            fromTextView.text = toTextView.text
            toTextView.text = str
        }

        searchBusButton.setOnClickListener {
            intent = Intent(requireActivity(),BusesSearchByRouteActivity::class.java)
            Thread.sleep(50)
            startActivity(intent)
        }

        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mapView.onCreate(mapViewBundle);


        // Inflate the layout for this fragment
        return fragmentView

    }

    private fun getLastLocation() {

        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                val location = it
                longitude = location.longitude
                latitude = location.latitude
                mapView.getMapAsync(this)
            }
        }
    }

    private fun getNearbyBuses(map : GoogleMap) {
        fireStore.collection("Bus_Location").get().addOnCompleteListener {
            if(it.isSuccessful)
            {
                val buses = it.result.documents
                val icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_bus)
                for (bus in buses)
                {
                    val lat = bus.getDouble("latitude")
                    val lng = bus.getDouble("longitude")
                    val busLatLng = LatLng(lat!!,lng!!)
                    val distance = SphericalUtil.computeDistanceBetween(currentLocation,busLatLng)
                    if(distance<2000.0)
                    {
                        map.addMarker(MarkerOptions().position(busLatLng).icon(icon))
                    }
                }
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(map: GoogleMap) {
        map.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).title("You"))
        currentLocation = LatLng(latitude,longitude)
        getNearbyBuses(map)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,14.5f))
        map.setOnMarkerClickListener{
            intent = Intent(requireContext(),NearbyBusActivity::class.java)
            startActivity(intent)
            true
        }
        map.setOnMapClickListener {
            intent = Intent(requireContext(),NearbyBusActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
