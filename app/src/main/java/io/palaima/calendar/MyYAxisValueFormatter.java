package io.palaima.calendar;


import java.text.DecimalFormat;

import io.palaima.chart.YAxis;
import io.palaima.chart.YAxisValueFormatter;

public class MyYAxisValueFormatter implements YAxisValueFormatter {

    private DecimalFormat mFormat;

    public MyYAxisValueFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0.0");
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return mFormat.format(value) + " $";
    }
}
