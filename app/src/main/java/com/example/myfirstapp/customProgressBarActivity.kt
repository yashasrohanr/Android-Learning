package com.example.myfirstapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myfirstapp.databinding.ActivityCustomProgressBarBinding

class customProgressBarActivity : AppCompatActivity() {

    private var binding_ : ActivityCustomProgressBarBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding_ = ActivityCustomProgressBarBinding.inflate(layoutInflater)
        var binding = binding_!!
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarProgressBarActivity)

        if(supportActionBar!= null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbarProgressBarActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.customProgressBarBase.setProgress(140)
        binding.customProgressBarGreen.setProgress(35)
        binding.customProgressBarYellow.setProgress(70)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding_ = null
    }
}