package com.example.busmapper

import android.content.Intent
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
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager : ViewPager2
    private var isLoginActivityOpened = false
    private lateinit var drawerLayout : DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewPager = findViewById(R.id.viewPager)
        drawerLayout = findViewById(R.id.drawer_layout)
        val firebaseAuth = FirebaseAuth.getInstance()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val viewPagerAdapter = ViewPagerAdapter(this)
        val drawerNavigationView = findViewById<NavigationView>(R.id.drawer_navigationView)
        var intent = Intent()

        viewPager.isUserInputEnabled = false
        bottomNavigationView.menu.getItem(1).isChecked = true

        if(firebaseAuth.currentUser!=null)
        {
            drawerNavigationView.menu.findItem(R.id.signupOrLogin_menu).isVisible = false
            drawerNavigationView.menu.findItem(R.id.logout_menu).isVisible = true
        }

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

        drawerNavigationView.setNavigationItemSelectedListener {

            when(it.itemId)
            {
                R.id.signupOrLogin_menu ->
                {
                    intent = Intent(this,LoginActivity::class.java)
                    isLoginActivityOpened = true
                }
                R.id.favourite_menu -> {
                    if(firebaseAuth.currentUser!=null)
                    {
                        intent = Intent(this, FavouriteActivity::class.java)
                    }
                    else
                    {
                        Toast.makeText(this,"SignUp/Login required",Toast.LENGTH_SHORT).show()
                        return@setNavigationItemSelectedListener true
                    }
                }
                R.id.sos_menu ->
                    intent = Intent(this,SosActivity::class.java)
                R.id.aboutUs_menu ->
                    intent = Intent(this,AboutActivity::class.java)
                R.id.logout_menu ->{
                    firebaseAuth.signOut()
                    this.recreate()
                    return@setNavigationItemSelectedListener true
                }
            }
            startActivity(intent)
            drawerLayout.close()
            true
        }

        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

    }

    override fun onResume() {
        super.onResume()
        if(isLoginActivityOpened)
        {
            recreate()
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {

            if(drawerLayout.isOpen)
            {
                drawerLayout.close()
            }
            else if(viewPager.currentItem != 1)
            {
                viewPager.currentItem = 1
            }
            else
            {
                finish()
            }

        }

    }

}