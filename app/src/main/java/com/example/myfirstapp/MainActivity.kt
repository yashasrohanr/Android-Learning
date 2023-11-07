package com.example.myfirstapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import com.example.myfirstapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding : ActivityMainBinding? = null
    private lateinit var flStartButton : FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.flStart?.setOnClickListener {
            startActivity(Intent(this, ExerciseActivity::class.java))
        }

        binding?.flBMI?.setOnClickListener {
            startActivity(Intent(this, BmiActivity::class.java))
        }
        binding?.mainPageImage?.setOnClickListener{
            startActivity(Intent(this, customProgressBarActivity::class.java))
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}