package com.example.myfirstapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BmiActivity : AppCompatActivity() {

    private var binding : ActivityBmiBinding? = null
    private val TAG = "bmi_activity"

    private var viewmodel: BmiViewModel? = null
    private var units : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        viewmodel = ViewModelProvider(this).get(BmiViewModel::class.java)
        //viewmodel?.initialise()


        setSupportActionBar(binding?.toolbarBMIActivity)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarBMIActivity?.setNavigationOnClickListener{
            onBackPressed()
        }

        init()
        binding?.btnCalculate?.setOnClickListener {
            if(units)
            {
                Log.w(TAG, "current system for calc is metric")
                if(validateInputFieldsMetric()) {
                    var h : Float = binding?.tiHeightMetric?.text.toString()!!.toFloat()
                    var w : Float = binding?.tiWeightMetric?.text.toString()!!.toFloat()

                    var bmi = w / (h*h)
                    Log.w(TAG, "Entered h = $h and w = $w and bmi = $bmi")
                    viewmodel?.calculateBmiResult(bmi)
                }else{
                    Toast.makeText(this, "Please enter valid inputs", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Log.w(TAG, "current system for calc is american")
                if(validateInputFieldsAmerican()) {
                    var h : Float = (binding?.tiHeightAmerican?.text.toString()!!.toDouble() * 0.3048).toFloat()
                    var w : Float = (binding?.tiWeightAmerican?.text.toString()!!.toDouble() * 0.453592).toFloat()

                    var bmi = w / (h*h)
                    Log.w(TAG, "Entered h = $h and w = $w and bmi = $bmi")
                    viewmodel?.calculateBmiResult(bmi)
                }else{
                    Toast.makeText(this, "Please enter valid inputs", Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewmodel?.bmiDesc?.observe(this) { bmiDesc ->
            Log.w(TAG, "desc posted with $bmiDesc")
            binding?.tvDescription?.text = bmiDesc.toString()
        }

        viewmodel?.bmiVal?.observe(this) { bmi ->
            Log.w(TAG, "value posted with $bmi")
            binding?.tvBMIvalue?.text = bmi.toString()
        }
        viewmodel?.bmiOneLiner?.observe(this) { bmiLabel ->
            Log.w(TAG, "label posted with $bmiLabel")
            binding?.tvOneLiner?.text = bmiLabel.toString()
        }

        binding?.switchTrigger?.setOnCheckedChangeListener{_, isChecked ->
            Log.w(TAG, "switch triggered with checked = $isChecked")
            if(!isChecked){
                units = true
                binding?.tiHeightMetric?.visibility = View.VISIBLE
                binding?.tiWeightMetric?.visibility = View.VISIBLE
                binding?.tiWeightAmerican?.visibility = View.INVISIBLE
                binding?.tiHeightAmerican?.visibility = View.INVISIBLE
                binding?.tiHeightAmerican?.text?.clear()
                binding?.tiWeightAmerican?.text?.clear()
            }else{
                units = false
                binding?.tiHeightMetric?.visibility = View.INVISIBLE
                binding?.tiWeightMetric?.visibility = View.INVISIBLE
                binding?.tiWeightAmerican?.visibility = View.VISIBLE
                binding?.tiHeightAmerican?.visibility = View.VISIBLE
                binding?.tiHeightMetric?.text?.clear()
                binding?.tiWeightMetric?.text?.clear()
            }

        }

    }

    private fun init(){
        Log.w(TAG, "performing init")
        units = true
        binding?.tiHeightMetric?.text?.clear()
        binding?.tiWeightMetric?.text?.clear()
        binding?.tiHeightAmerican?.text?.clear()
        binding?.tiWeightAmerican?.text?.clear()
        if(viewmodel?.bmiVal?.value != "__")
        {
            val x= viewmodel?.bmiVal?.value
            Log.w(TAG, "value stored from before = $x")
            binding?.tvDescription?.text = viewmodel?.bmiDesc?.value
            binding?.tvOneLiner?.text = viewmodel?.bmiOneLiner?.value
            binding?.tvBMIvalue?.text = viewmodel?.bmiVal?.value
        }else{
            val x= viewmodel?.bmiVal?.value
            Log.w(TAG, "value not stored from before = $x")
            binding?.tvDescription?.text = ""
            binding?.tvOneLiner?.text = ""
            binding?.tvBMIvalue?.text = "__"
        }
    }

    private fun validateInputFieldsMetric() : Boolean{
        var res :Boolean
        res = (!binding?.tiHeightMetric?.text.toString().isNullOrBlank()
                && !binding?.tiWeightMetric?.text.toString().isNullOrBlank()
                && binding?.tiHeightMetric?.text.toString().isNotEmpty()
                && binding?.tiWeightMetric?.text.toString().isNotEmpty())
        return res
    }

    private fun validateInputFieldsAmerican() : Boolean{
        var res :Boolean
        res = (!binding?.tiHeightAmerican?.text.toString().isNullOrBlank()
                && !binding?.tiWeightAmerican?.text.toString().isNullOrBlank()
                && binding?.tiHeightAmerican?.text.toString().isNotEmpty()
                && binding?.tiWeightAmerican?.text.toString().isNotEmpty())
        return res
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "destroying bmi viewmodel status = $viewmodel")
        binding = null
    }
}