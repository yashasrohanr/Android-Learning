package com.example.myfirstapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope

class BmiViewModel : ViewModel() {
    private val TAG = "bmi_activity"
    private val _bmiVal = MutableLiveData<String>()
    val bmiVal: LiveData<String> = _bmiVal

    private val _bmiOneLiner = MutableLiveData<String>()
    val bmiOneLiner: LiveData<String> = _bmiOneLiner

    private val _bmiDesc = MutableLiveData<String>()
    val bmiDesc: LiveData<String> = _bmiDesc

    fun initialise() {
        _bmiVal.value = "__"
        _bmiOneLiner.value = ""
        _bmiDesc.value = ""
    }

     fun calculateBmiResult(bmi: Float) {

        val bmiLabel: String
        val bmiDescription: String
        when {
            bmi <= 15f -> {
                bmiLabel = "Very Severely Underweight"
                bmiDescription = "You are in a critical condition. Seek medical attention immediately."
            }
            bmi <= 16f -> {
                bmiLabel = "Severely Underweight"
                bmiDescription = "You are severely underweight. Please consult a doctor."
            }
            bmi <= 18.5f -> {
                bmiLabel = "Underweight"
                bmiDescription = "You are underweight. Consider a balanced diet."
            }
            bmi <= 25f -> {
                bmiLabel = "Normal Weight"
                bmiDescription = "Congratulations! You are in a healthy weight range."
            }
            bmi <= 30f -> {
                bmiLabel = "Overweight"
                bmiDescription = "You are overweight. Consider a fitness regimen."
            }
            bmi <= 35f -> {
                bmiLabel = "Obese Class I (Moderately obese)"
                bmiDescription = "You are moderately obese. Seek advice from a healthcare professional."
            }
            bmi <= 40f -> {
                bmiLabel = "Obese Class II (Severely obese)"
                bmiDescription = "You are severely obese. Consult a doctor for guidance."
            }
            else -> {
                bmiLabel = "Obese Class III (Very Severely obese)"
                bmiDescription = "You are in a very dangerous condition. Immediate medical attention is necessary."
            }
        }


        Log.w(TAG, "calc status : label = $bmiLabel & desc = $bmiDescription")
        // Round the result value to 2 decimal places
        val bmiValue = String.format("%.2f", bmi)

         _bmiVal.value = bmiValue
         _bmiOneLiner.value = bmiLabel
         _bmiDesc.value = bmiDescription
         
    }

}