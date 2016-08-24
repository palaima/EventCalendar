package io.palaima.calendar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.palaima.chart.*
import java.util.*

class MainActivity : AppCompatActivity(), OnChartValueSelectedListener {

    val mMonths = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec")

    private lateinit var mChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mChart = findViewById(R.id.chart1) as BarChart
        mChart.setOnChartValueSelectedListener(this)

        mChart.setDrawBarShadow(false)
        mChart.setDrawValueAboveBar(true)

        mChart.setDescription("")

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60)

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(true)

        mChart.setDrawGridBackground(true)

        val xAxis = mChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(true)
        xAxis.spaceBetweenLabels = 2

        val custom = MyYAxisValueFormatter()

        val leftAxis = mChart.axisLeft
        leftAxis.setLabelCount(8, false)
        leftAxis.valueFormatter = custom
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.spaceTop = 15f

        val rightAxis = mChart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.setLabelCount(8, false)
        rightAxis.valueFormatter = custom
        rightAxis.spaceTop = 15f

        val l = mChart.legend
        l.position = Legend.LegendPosition.BELOW_CHART_LEFT
        l.form = Legend.LegendForm.SQUARE
        l.formSize = 9f
        l.textSize = 11f
        l.xEntrySpace = 4f
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });

        setData(100, 100f)
    }

    private fun setData(count: Int, range: Float) {

        val xVals = ArrayList<String>()
        for (i in 0..count - 1) {
            xVals.add(mMonths[i % 12])
        }

        val yVals1 = ArrayList<BarEntry>()

        for (i in 0..count - 1) {
            val mult = range + 1
            val `val` = (Math.random() * mult).toFloat()
            yVals1.add(BarEntry(`val`, i))
        }

        val set1 = BarDataSet(yVals1, "DataSet")
        set1.barSpacePercent = 35f

        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set1)

        val data = BarData(xVals, dataSets)
        data.setValueTextSize(10f)

        mChart.data = data
    }

    override fun onValueSelected(e: Entry?, dataSetIndex: Int, h: Highlight) {

        if (e == null)
            return

        val bounds = mChart.getBarBounds(e as BarEntry?)
        val position = mChart.getPosition(e, YAxis.AxisDependency.LEFT)

        Log.i("bounds", bounds.toString())
        Log.i("position", position.toString())

        Log.i("x-index",
                "low: " + mChart.lowestVisibleXIndex + ", high: "
                        + mChart.highestVisibleXIndex)
    }

    override fun onNothingSelected() {

    }
}
