package com.example.myfirstapp

import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.myfirstapp.databinding.ActivityEndBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class EndActivity : AppCompatActivity() {
    private var binding : ActivityEndBinding? = null
    val TAG = "end-activity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEndBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)
        if(supportActionBar != null)
        {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.btnFinish?.setOnClickListener {
            finish()
        }

        val dao = (application as WorkOutApp).db.historyDao()
        //addDataToDatabase(dao)
    }

    private fun addDataToDatabase(historyDao: HistoryDao){

        val calendarInstance = Calendar.getInstance()
        val dateTime = calendarInstance.time
        val sdf = SimpleDateFormat("dd MMM yyyy HH::mm::ss", Locale.getDefault())
        val date = sdf.format(dateTime)
        Log.e(TAG, "formatted date : $date")
        lifecycleScope.launch{
            historyDao.insert(HistoryEntity(date))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}