package com.example.ynbdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnShowLamppost = findViewById(R.id.btnShowLamppost) as Button
        btnShowLamppost.setOnClickListener {
            Toast.makeText(this@MainActivity, "Show Lamp Post", Toast.LENGTH_LONG).show()
            val intent = Intent(this, LamppostActivity::class.java).apply {
                //putExtra(EXTRA_MESSAGE, message)
            }
            startActivity(intent)
        }

        val btnShowTrackable = findViewById(R.id.btnShowTrackable) as Button
        btnShowTrackable.setOnClickListener {
            Toast.makeText(this@MainActivity, "Show Trackable", Toast.LENGTH_LONG).show()
            val intent = Intent(this, TrackableActivity::class.java).apply {
                //putExtra(EXTRA_MESSAGE, message)
            }
            startActivity(intent)
        }
    }

}