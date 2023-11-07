package com.example.myfirstapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.myfirstapp.databinding.ActivityCustomProgressBarBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.Random

class customProgressBarActivity : AppCompatActivity() {


    private var binding_ : ActivityCustomProgressBarBinding? = null
    private lateinit var binding: ActivityCustomProgressBarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding_ = ActivityCustomProgressBarBinding.inflate(layoutInflater)
        binding = binding_!!
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

        setUpGraph()

    }

    fun setUpGraph(){
        val sleepGraph = binding.graph
        val entries: ArrayList<Entry> = generateRandomData()
        val linedata = LineData()

        if (entries.size >= 2) {
            for (i in 1 until entries.size - 1) {
                val interpolatedEntries: ArrayList<Entry> = interpolateEntries(entries[i], entries[i + 1], 30)
                val colors = interpolateColors(entries[i], entries[i + 1], 30)
                val dataset = LineDataSet(interpolatedEntries, "Sleep stages")
                dataset.setDrawValues(false)
                dataset.setDrawCircles(false)
                dataset.mode = LineDataSet.Mode.LINEAR
                dataset.setColors(colors)
                dataset.lineWidth = 3f
                linedata.addDataSet(dataset)
            }
        }
        sleepGraph.data = linedata
        sleepGraph.invalidate()

        sleepGraph.axisLeft.setDrawLabels(false)
        sleepGraph.xAxis.setDrawLabels(false)
        sleepGraph.axisRight.isEnabled = true
        sleepGraph.axisRight.setDrawLabels(true)
        sleepGraph.legend.isEnabled = false
        sleepGraph.axisRight.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return when (value) {
                    1f -> "val4"
                    2f -> "val3"
                    3f -> "val2"
                    4f -> "val1"
                    else -> ""
                }
            }
        }
    }

    private fun adjustAlpha(dataset: LineDataSet, alpha: Float) {
        val color = dataset.color
        val newColor = Color.argb((Color.alpha(color) * alpha).toInt(), Color.red(color), Color.green(color), Color.blue(color))
        //dataset.color = newColor
    }

    fun getEntryColor(x : Int) : Int{
        return when(x){
            1 -> Color.BLUE
            2 -> Color.MAGENTA
            3 -> Color.CYAN
            4 -> Color.RED
            else -> Color.RED
        }
    }
    fun interpolateEntries(entry1: Entry, entry2: Entry, numberOfInterpolations : Int): ArrayList<Entry> {
        val interpolatedEntries: ArrayList<Entry> = ArrayList()

        val numOfInt = numberOfInterpolations.coerceAtLeast(1)
        val dx = (entry2.x - entry1.x) / numOfInt
        val dy = (entry2.y - entry1.y) / numOfInt

        for (i in 0 until numOfInt.toInt()) {
            val x = entry1.x + dx * i
            val y = entry1.y + dy * i
            interpolatedEntries.add(Entry(x, y))
        }
        return interpolatedEntries
    }

    fun interpolateColors(entry1: Entry, entry2: Entry, numberOfInterpolations: Int): ArrayList<Int> {
        val colors: ArrayList<Int> = ArrayList()
        val alphaValues = interpolateAlphas(entry1.y.toInt(), entry2.y.toInt(), numberOfInterpolations)

        val startColor = getEntryColor(entry1.y.toInt())
        val endColor = getEntryColor(entry2.y.toInt())

        //val numInterpolations = (entry2.y - entry1.y) * 15 // 15 points per stage increase
        val interpolatedEntries = interpolateEntries(entry1, entry2, numberOfInterpolations)
        for (i in 0 until interpolatedEntries.size) {
            val fraction = i / interpolatedEntries.size.toFloat()
            val currentColor = when {
                entry1.y < entry2.y -> { // If moving to a higher y value
                    when {
                        fraction < 1 / 3f -> getInterpolatedColor(startColor, Color.GREEN, 3 * fraction)
                        fraction < 2 / 3f -> getInterpolatedColor(Color.GREEN, Color.DKGRAY, 3 * (fraction - 1 / 3f))
                        else -> getInterpolatedColor(Color.DKGRAY, endColor, 3 * (fraction - 2 / 3f))
                    }
                }
                entry1.y > entry2.y -> { // If moving to a lower y value
                    when {
                        fraction < 1 / 3f -> getInterpolatedColor(startColor, Color.DKGRAY, 3 * fraction)
                        fraction < 2 / 3f -> getInterpolatedColor(Color.DKGRAY, Color.GREEN, 3 * (fraction - 1 / 3f))
                        else -> getInterpolatedColor(Color.GREEN, endColor, 3 * (fraction - 2 / 3f))
                    }
                }
                else -> { // If the same y value, apply the standard gradient
                    getInterpolatedColor(startColor, endColor, fraction)
                }
            }
            colors.add(Color.argb(alphaValues[i], Color.red(currentColor), Color.green(currentColor), Color.blue(currentColor)))
        }
        return colors
    }
    fun getInterpolatedColor(startColor: Int, endColor: Int, fraction: Float): Int {
        val startA = Color.alpha(startColor)
        val startR = Color.red(startColor)
        val startG = Color.green(startColor)
        val startB = Color.blue(startColor)

        val endA = Color.alpha(endColor)
        val endR = Color.red(endColor)
        val endG = Color.green(endColor)
        val endB = Color.blue(endColor)

        val resultA = (startA + (fraction * (endA - startA))).toInt()
        val resultR = (startR + (fraction * (endR - startR))).toInt()
        val resultG = (startG + (fraction * (endG - startG))).toInt()
        val resultB = (startB + (fraction * (endB - startB))).toInt()

        return Color.argb(resultA, resultR, resultG, resultB)
    }
    fun interpolateAlphas(y1: Int, y2: Int, numberOfInterpolations : Int): ArrayList<Int> {
        val alphas: ArrayList<Int> = ArrayList()
        val alphaStart = 255
        val alphaEnd = 50

        for (i in 0 until numberOfInterpolations) {
            val alpha = alphaStart - (alphaStart - alphaEnd) * i / numberOfInterpolations
            alphas.add(alpha)
        }
        return alphas
    }
    fun generateRandomData(): ArrayList<Entry> {
        val randomEntries: ArrayList<Entry> = ArrayList()
        val random = Random()
        for (i in 1..20) {
            val y = random.nextInt(4) + 1 // Random y value between 1 and 4
            val entry = Entry(i.toFloat(), y.toFloat())
            randomEntries.add(entry)
        }
        return randomEntries
    }
    override fun onDestroy() {
        super.onDestroy()
        binding_ = null
    }
}