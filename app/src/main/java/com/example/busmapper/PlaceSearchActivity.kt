package com.example.busmapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.libraries.places.api.Places
import java.util.Locale

class PlaceSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_search)

        Places.initialize(this,resources.getString(R.string.api_key), Locale.ENGLISH)

        val places = Places.createClient(this)




    }
}