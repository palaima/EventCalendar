package io.palaima.calendar


import io.palaima.chart.YAxis
import io.palaima.chart.YAxisValueFormatter
import java.text.DecimalFormat

class MyYAxisValueFormatter : YAxisValueFormatter {

    private val mFormat: DecimalFormat

    init {
        mFormat = DecimalFormat("###,###,###,##0.0")
    }

    override fun getFormattedValue(value: Float, yAxis: YAxis): String {
        return mFormat.format(value.toDouble()) + " $"
    }
}
