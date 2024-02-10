package com.example.busmapper

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.busmapper.adapters.ViewPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager : ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewPager = findViewById(R.id.viewPager)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val viewPagerAdapter = ViewPagerAdapter(this)

        viewPager.isUserInputEnabled = false
        bottomNavigationView.menu.getItem(1).isChecked = true

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            viewPager.adapter = viewPagerAdapter
            viewPager.setCurrentItem(1,false)
        }
        else
        {
            val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()){

                viewPager.adapter = viewPagerAdapter
                viewPager.setCurrentItem(1,false)

                if(!it)
                {
                    Toast.makeText(this,"Please allow location permission",Toast.LENGTH_SHORT).show()
                }
            }
            locationPermissionRequest.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

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