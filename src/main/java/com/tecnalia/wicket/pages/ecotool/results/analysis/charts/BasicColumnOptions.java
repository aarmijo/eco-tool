package com.tecnalia.wicket.pages.ecotool.results.analysis.charts;

import com.googlecode.wickedcharts.highcharts.options.Axis;
import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.CreditOptions;
import com.googlecode.wickedcharts.highcharts.options.Function;
import com.googlecode.wickedcharts.highcharts.options.HorizontalAlignment;
import com.googlecode.wickedcharts.highcharts.options.Legend;
import com.googlecode.wickedcharts.highcharts.options.LegendLayout;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.PlotOptions;
import com.googlecode.wickedcharts.highcharts.options.PlotOptionsChoice;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.Tooltip;
import com.googlecode.wickedcharts.highcharts.options.VerticalAlignment;
import com.googlecode.wickedcharts.highcharts.options.color.HexColor;
import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;
 
public class BasicColumnOptions extends Options {
 
  private static final long serialVersionUID = 1L;
 
  public BasicColumnOptions() {
 
    setChartOptions(new ChartOptions().setType(SeriesType.COLUMN));
 
    setTitle(new Title("Monthly Average Rainfall"));
 
    setSubtitle(new Title("Source: WorldClimate.com"));
 
    setCredits(new CreditOptions().setEnabled(false));
    
    setxAxis(new Axis().setCategories(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"));
 
    setyAxis(new Axis().setMin(0).setTitle(new Title("Rainfall (mm)")));
 
    setLegend(new Legend()
        .setLayout(LegendLayout.VERTICAL)
        .setBackgroundColor(new HexColor("#FFFFFF"))
        .setAlign(HorizontalAlignment.LEFT)
        .setVerticalAlign(VerticalAlignment.TOP)
        .setX(100)
        .setY(70)
        .setFloating(Boolean.TRUE)
        .setShadow(Boolean.TRUE));
 
    setTooltip(new Tooltip()
        .setFormatter(new Function()
            .setFunction(" return ''+ this.x +': '+ this.y +' mm';")));
 
    setPlotOptions(new PlotOptionsChoice()
        .setColumn(new PlotOptions()
            .setPointPadding(0.2f)
            .setBorderWidth(0)));
 
    addSeries(new SimpleSeries()
        .setName("Tokyo")
        .setData(
            49.9,
            71.5,
            106.4,
            129.2,
            144.0,
            176.0,
            135.6,
            148.5,
            216.4,
            194.1,
            95.6,
            54.4));
 
    addSeries(new SimpleSeries()
        .setName("New York")
        .setData(
            83.6,
            78.8,
            98.5,
            93.4,
            106.0,
            84.5,
            105.0,
            104.3,
            91.2,
            83.5,
            106.6,
            92.3));
 
    addSeries(new SimpleSeries()
        .setName("London")
        .setData(
            48.9,
            38.8,
            39.3,
            41.4,
            47.0,
            48.3,
            59.0,
            59.6,
            52.4,
            65.2,
            59.3,
            51.2));
 
    addSeries(new SimpleSeries()
        .setName("Berlin")
        .setData(
            42.4,
            33.2,
            34.5,
            39.7,
            52.6,
            75.5,
            57.4,
            60.4,
            47.6,
            39.1,
            46.8,
            51.1)); 
  } 

}
