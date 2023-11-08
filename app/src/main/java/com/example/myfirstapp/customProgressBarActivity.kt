package com.example.myfirstapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.Dataset
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.myfirstapp.databinding.ActivityCustomProgressBarBinding
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import java.util.Random

class customProgressBarActivity : AppCompatActivity() {

    private var binding_ : ActivityCustomProgressBarBinding? = null
    private lateinit var binding: ActivityCustomProgressBarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding_ = ActivityCustomProgressBarBinding.inflate(layoutInflater)
        binding = binding_!!
        setContentView(binding.root)

        setSupportActionBar(binding.actionBar)

        if(supportActionBar!= null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.actionBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        //binding.customProgressBarBase.setProgress(140)
        //binding.customProgressBarGreen.setProgress(35)
        //binding.customProgressBarYellow.setProgress(70)

        setUpGraph()

    }
    class customRenderer(chart: LineDataProvider, animator : ChartAnimator, vph : ViewPortHandler, context: Context) : LineChartRenderer(chart, animator, vph){
        val mchart = chart
        val mcontext = context
        private fun getColor(stage : Float): Int {
            val colorRes = when(stage){
                1f -> R.color.graphBlue
                2f -> R.color.graphPurple
                3f -> R.color.graphCyan
                4f -> R.color.graphRed
                else -> R.color.graphRed
            }
            return ContextCompat.getColor(mcontext, colorRes)
        }
        override fun drawLinear(c: Canvas?, dataSet: ILineDataSet?) {
            val transformer = mchart.getTransformer(dataSet?.axisDependency)
            mRenderPaint.strokeJoin = Paint.Join.ROUND
            if (dataSet?.entryCount!! > 0) {
                for (i in 0..dataSet.entryCount - 2) {
                    val e1 = dataSet.getEntryForIndex(i)
                    val e2 = dataSet.getEntryForIndex(i+1)

                    if(e1.y - e2.y == 0f){
                        //same level no gradient, horizontal line
                        val color = getColor(e2.y)
                        mRenderPaint.color = color
                        mRenderPaint.strokeWidth = 25f
                        //mRenderPaint.strokeJoin = Paint.Join.ROUND
                        val p1 = transformer.getPixelForValues(e1.x, e1.y)
                        val p2 = transformer.getPixelForValues(e2.x, e2.y)

                        c?.drawLine(p1.x.toFloat(), p1.y.toFloat(), p2.x.toFloat(), p2.y.toFloat(), mRenderPaint)
                    }

                    ///////////////////////////////////////////////////////////////////////////////////
                    else{
                        //different level, different gradient
                        mRenderPaint.strokeWidth = 4f
                        //mRenderPaint.strokeJoin = Paint.Join.ROUND

                        val dx = e2.x - e1.x
                        val c4 = getColor(4f)
                        val c3 = getColor(3f)
                        val c2 = getColor(2f)
                        val c1 = getColor(1f)
                        if(e2.y == 1f)  //final stage ends at 1f
                        {
                            if(e1.y == 4f){
                                //3 stage
                                //4f to 1f = 4f to 3f, 3f to 2f, 2f to 1f
                                val px1 = transformer.getPixelForValues(e1.x, 4f)
                                val px2 = transformer.getPixelForValues(e2.x, 1f)

                                val colorArray = intArrayOf(c4, c3, c2, c1)
                                val posArray = floatArrayOf(0f,0.33f, 0.66f, 1f)

                                val gradient1 = LinearGradient(
                                    px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(),
                                    colorArray, posArray,
                                    Shader.TileMode.CLAMP
                                )
                                mRenderPaint.shader = gradient1
                                c?.drawLine(px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(), mRenderPaint)

                            }else if(e1.y == 3f){
                                //2 stage
                                //3f to 1f = 3f to 2f, 2f to 1f
                                val px1 = transformer.getPixelForValues(e1.x, 3f)
                                val px2 = transformer.getPixelForValues(e2.x, 1f)

                                val colorArray = intArrayOf(c3, c2, c1)
                                val posArray = floatArrayOf(0f,0.5f, 1f)

                                val gradient1 = LinearGradient(
                                    px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(),
                                    colorArray, posArray,
                                    Shader.TileMode.CLAMP
                                )
                                mRenderPaint.shader = gradient1
                                c?.drawLine(px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(), mRenderPaint)

                            }else{
                                //e1.y == 2f
                                //1 stage
                                //2f to 1f
                                val px1 = transformer.getPixelForValues(e1.x, 2f)
                                val px2 = transformer.getPixelForValues(e2.x, 1f)

                                val colorArray = intArrayOf(c2, c1)
                                val posArray = floatArrayOf(0f,1f)

                                val gradient1 = LinearGradient(
                                    px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(),
                                    colorArray, posArray,
                                    Shader.TileMode.CLAMP
                                )
                                mRenderPaint.shader = gradient1
                                c?.drawLine(px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(), mRenderPaint)

                            }
                        }

                        else if(e2.y == 2f )
                        {
                            if(e1.y == 4f){
                                // 2 stage
                                //4f to 2f = 4 to 3, 3 to 2
                                val px1 = transformer.getPixelForValues(e1.x, 4f)
                                val px2 = transformer.getPixelForValues(e2.x, 2f)

                                val colorArray = intArrayOf(c4, c3, c2)
                                val posArray = floatArrayOf(0f,0.5f, 1f)

                                val gradient1 = LinearGradient(
                                    px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(),
                                    colorArray, posArray,
                                    Shader.TileMode.CLAMP
                                )
                                mRenderPaint.shader = gradient1
                                c?.drawLine(px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(), mRenderPaint)


                            }else if(e1.y == 3f){
                                //1 stage
                                //3f to 2f = 3 to 2
                                val px1 = transformer.getPixelForValues(e1.x, 3f)
                                val px2 = transformer.getPixelForValues(e2.x, 2f)

                                val colorArray = intArrayOf(c3, c2)
                                val posArray = floatArrayOf(0f,1f)

                                val gradient1 = LinearGradient(
                                    px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(),
                                    colorArray, posArray,
                                    Shader.TileMode.CLAMP
                                )
                                mRenderPaint.shader = gradient1
                                c?.drawLine(px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(), mRenderPaint)


                            }else{
                                //e1.y == 1f
                                //1 stage
                                //1f to 2f = 1 to 2
                                val px1 = transformer.getPixelForValues(e1.x, 1f)
                                val px2 = transformer.getPixelForValues(e2.x, 2f)

                                val colorArray = intArrayOf(c1, c2)
                                val posArray = floatArrayOf(0f,1f)

                                val gradient1 = LinearGradient(
                                    px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(),
                                    colorArray, posArray,
                                    Shader.TileMode.CLAMP
                                )
                                mRenderPaint.shader = gradient1
                                c?.drawLine(px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(), mRenderPaint)
                            }

                        }

                        else if(e2.y == 3f)
                        {
                            if(e1.y == 4f) {
                                //1 stage transform
                                //4f to 3f = 4 to 3
                                val px1 = transformer.getPixelForValues(e1.x, 4f)
                                val px2 = transformer.getPixelForValues(e2.x, 3f)

                                val colorArray = intArrayOf(c4, c3)
                                val posArray = floatArrayOf(0f,1f)

                                val gradient1 = LinearGradient(
                                    px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(),
                                    colorArray, posArray,
                                    Shader.TileMode.CLAMP
                                )
                                mRenderPaint.shader = gradient1
                                c?.drawLine(px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(), mRenderPaint)

                            }else if (e1.y == 1f){
                                //2 stage transform
                                //1f to 3f = 1 to 2, 2 to 3
                                val px1 = transformer.getPixelForValues(e1.x, 1f)
                                val px2 = transformer.getPixelForValues(e2.x, 3f)

                                val colorArray = intArrayOf(c1, c2, c3)
                                val posArray = floatArrayOf(0f,0.5f, 1f)

                                val gradient1 = LinearGradient(
                                    px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(),
                                    colorArray, posArray,
                                    Shader.TileMode.CLAMP
                                )
                                mRenderPaint.shader = gradient1
                                c?.drawLine(px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(), mRenderPaint)

                            }else {
                                //e1.y == 2f
                                //1 stage transform
                                //2f - 3f = 2 -3
                                val px1 = transformer.getPixelForValues(e1.x, 2f)
                                val px2 = transformer.getPixelForValues(e2.x, 3f)

                                val colorArray = intArrayOf(c2, c3)
                                val posArray = floatArrayOf(0f,1f)

                                val gradient1 = LinearGradient(
                                    px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(),
                                    colorArray, posArray,
                                    Shader.TileMode.CLAMP
                                )
                                mRenderPaint.shader = gradient1
                                c?.drawLine(px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(), mRenderPaint)
                            }
                        }

                        else if( e2.y == 4f)
                        {
                            if(e1.y == 1f){
                                //3 stage transform
                                //1f to 4f = 1 to 2, 2 to 3, 3 to 4
                                val px1 = transformer.getPixelForValues(e1.x, 1f)
                                val px2 = transformer.getPixelForValues(e2.x, 4f)

                                val colorArray = intArrayOf(c1, c2, c3, c4)
                                val posArray = floatArrayOf(0f,0.33f, 0.66f, 1f)

                                val gradient1 = LinearGradient(
                                    px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(),
                                    colorArray, posArray,
                                    Shader.TileMode.CLAMP
                                )
                                mRenderPaint.shader = gradient1
                                c?.drawLine(px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(), mRenderPaint)

                            }else if( e1.y == 2f){
                                //2 stage transform
                                //2f to 4f = 2 to 3, 3 to 4
                                val px1 = transformer.getPixelForValues(e1.x, 2f)
                                val px2 = transformer.getPixelForValues(e2.x, 4f)

                                val colorArray = intArrayOf(c2, c3, c4)
                                val posArray = floatArrayOf(0f,0.5f, 1f)

                                val gradient1 = LinearGradient(
                                    px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(),
                                    colorArray, posArray,
                                    Shader.TileMode.CLAMP
                                )
                                mRenderPaint.shader = gradient1
                                c?.drawLine(px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(), mRenderPaint)

                            }else {
                                //e1.y == 3f
                                //1 stage transform
                                //3f to 4f =  3 to 4
                                val px1 = transformer.getPixelForValues(e1.x, 3f)
                                val px2 = transformer.getPixelForValues(e2.x, 4f)

                                val colorArray = intArrayOf(c3, c4)
                                val posArray = floatArrayOf(0f,1f)

                                val gradient1 = LinearGradient(
                                    px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(),
                                    colorArray, posArray,
                                    Shader.TileMode.CLAMP
                                )
                                mRenderPaint.shader = gradient1
                                c?.drawLine(px1.x.toFloat(), px1.y.toFloat(), px2.x.toFloat(), px2.y.toFloat(), mRenderPaint)
                            }
                        }
                    }
                }
            }
        }
    }
    fun setUpGraph() {
        val sleepGraph = binding.graph
        val entries: ArrayList<Entry> = generateRandomData()
        val linedata = LineData()
        val dataset = LineDataSet(entries, "Sleep stages")
        dataset.setDrawValues(false)
        val customRenderer = customRenderer(sleepGraph, sleepGraph.animator, sleepGraph.viewPortHandler, applicationContext)
        sleepGraph.renderer = customRenderer

        sleepGraph.axisLeft.setDrawLabels(false)
        sleepGraph.xAxis.setDrawLabels(false)
        sleepGraph.axisRight.isEnabled = true
        sleepGraph.axisRight.setDrawLabels(true)
        sleepGraph.legend.isEnabled = false
        

        val rAxis = sleepGraph.axisRight
        rAxis.isEnabled = true
        rAxis.setDrawLabels(true)
        rAxis.axisMinimum = 1f
        rAxis.axisMaximum = 4f
        rAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return when (value) {
                    1f -> "Deep"
                    2f -> "LIGHT"
                    3f -> "REM"
                    4f -> "AWAKE"
                }
            }
        }

        dataset.mode = LineDataSet.Mode.LINEAR
        dataset.setDrawCircles(false)
        linedata.addDataSet(dataset)
        sleepGraph.data = linedata

        sleepGraph.invalidate()

    }


    private fun adjustAlpha(dataset: LineDataSet, alpha: Float) {
        val color = dataset.color
        val newColor = Color.argb((Color.alpha(color) * alpha).toInt(), Color.red(color), Color.green(color), Color.blue(color))
        //dataset.color = newColor
    }


//    fun interpolateEntries(entry1: Entry, entry2: Entry, numberOfInterpolations : Int): ArrayList<Entry> {
//        val interpolatedEntries: ArrayList<Entry> = ArrayList()
//
//        val numOfInt = numberOfInterpolations.coerceAtLeast(1)
//        val dx = (entry2.x - entry1.x) / numOfInt
//        val dy = (entry2.y - entry1.y) / numOfInt
//
//        for (i in 0 until numOfInt.toInt()) {
//            val x = entry1.x + dx * i
//            val y = entry1.y + dy * i
//            interpolatedEntries.add(Entry(x, y))
//        }
//        return interpolatedEntries
//    }

//    fun interpolateColors(entry1: Entry, entry2: Entry, numberOfInterpolations: Int): ArrayList<Int> {
//        val colors: ArrayList<Int> = ArrayList()
//        val alphaValues = interpolateAlphas(entry1.y.toInt(), entry2.y.toInt(), numberOfInterpolations)
//
//        val startColor = getEntryColor(entry1.y.toInt())
//        val endColor = getEntryColor(entry2.y.toInt())
//
//        //val numInterpolations = (entry2.y - entry1.y) * 15 // 15 points per stage increase
//        val interpolatedEntries = interpolateEntries(entry1, entry2, numberOfInterpolations)
//        for (i in 0 until interpolatedEntries.size) {
//            val fraction = i / interpolatedEntries.size.toFloat()
//            val currentColor = when {
//                entry1.y < entry2.y -> { // If moving to a higher y value
//                    when {
//                        fraction < 1 / 3f -> getInterpolatedColor(startColor, Color.GREEN, 3 * fraction)
//                        fraction < 2 / 3f -> getInterpolatedColor(Color.GREEN, Color.DKGRAY, 3 * (fraction - 1 / 3f))
//                        else -> getInterpolatedColor(Color.DKGRAY, endColor, 3 * (fraction - 2 / 3f))
//                    }
//                }
//                entry1.y > entry2.y -> { // If moving to a lower y value
//                    when {
//                        fraction < 1 / 3f -> getInterpolatedColor(startColor, Color.DKGRAY, 3 * fraction)
//                        fraction < 2 / 3f -> getInterpolatedColor(Color.DKGRAY, Color.GREEN, 3 * (fraction - 1 / 3f))
//                        else -> getInterpolatedColor(Color.GREEN, endColor, 3 * (fraction - 2 / 3f))
//                    }
//                }
//                else -> { // If the same y value, apply the standard gradient
//                    getInterpolatedColor(startColor, endColor, fraction)
//                }
//            }
//            colors.add(Color.argb(alphaValues[i], Color.red(currentColor), Color.green(currentColor), Color.blue(currentColor)))
//        }
//        return colors
//    }
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
        var xx = 0.1f
        for (i in 1..200) {
            val y = random.nextInt(4) + 1 // Random y value between 1 and 4
            val entry = Entry(xx, y.toFloat())
            xx += 0.1f
            randomEntries.add(entry)
        }

        return randomEntries
    }

    fun generateCustomData(): ArrayList<Entry> {
        val customEntries: ArrayList<Entry> = ArrayList()
        var xx = 0.1f

        // First 10 values as 1f
        repeat(10) {
            customEntries.add(Entry(xx, 1f))
            xx += 0.1f
        }

        // Next 20 values as 2f
        repeat(20) {
            customEntries.add(Entry(xx, 2f))
            xx += 0.1f
        }

        // Next 5 values as 4f
        repeat(5) {
            customEntries.add(Entry(xx, 4f))
            xx += 0.1f
        }

        // Next 30 values as 3f
        repeat(30) {
            customEntries.add(Entry(xx, 3f))
            xx += 0.1f
        }

        // Next 10 values as 4f
        repeat(10) {
            customEntries.add(Entry(xx, 4f))
            xx += 0.1f
        }

        return customEntries
    }

    override fun onDestroy() {
        super.onDestroy()
        binding_ = null
    }
}