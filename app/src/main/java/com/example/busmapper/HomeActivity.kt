package com.example.busmapper

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.busmapper.adapters.ViewPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    lateinit var viewPager : ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewPager = findViewById(R.id.viewPager)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val viewPagerAdapter = ViewPagerAdapter(this)

        viewPager.adapter = viewPagerAdapter
        viewPager.isUserInputEnabled = false
        viewPager.setCurrentItem(1,false)
        bottomNavigationView.menu.getItem(1).isChecked = true


        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bus_stops ->
                    viewPager.setCurrentItem(0,false)

                R.id.home ->
                    viewPager.setCurrentItem(1,false)

                R.id.more ->
                    viewPager.setCurrentItem(2,false)
            }
            return@setOnItemSelectedListener true
        }

        viewPager.registerOnPageChangeCallback(object:ViewPager2.OnPageChangeCallback()
        {
            override fun onPageSelected(position: Int) {
                when(position)
                {
                    0 -> bottomNavigationView.menu.getItem(0).isChecked = true
                    1 -> bottomNavigationView.menu.getItem(1).isChecked = true
                    2 -> bottomNavigationView.menu.getItem(2).isChecked = true
                }
                super.onPageSelected(position)
            }
        })

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
            }
            }
        }

// ...

// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION))

        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {

            val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
            if(drawerLayout.isOpen)
            {
                drawerLayout.close()
            }
            else
            {
                finish()
            }

        }

    }

}