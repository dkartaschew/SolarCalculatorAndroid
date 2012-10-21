package com.anonymous.solar.android;

import java.util.List;

import org.afree.chart.AFreeChart;
import org.afree.chart.ChartFactory;
import org.afree.chart.axis.NumberAxis;
import org.afree.chart.demo.DemoView;
import org.afree.chart.plot.PlotOrientation;
import org.afree.chart.plot.XYPlot;
import org.afree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.afree.data.xy.XYSeries;
import org.afree.data.xy.XYSeriesCollection;
import org.afree.graphics.GradientColor;
import org.afree.graphics.SolidColor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

public class WizardResultGraph extends DemoView {

	private String header;
	private String row;

	/**
	 * constructor
	 * 
	 * @param context
	 */
	public WizardResultGraph(Context context, String header, String row, List<List<Double>> results,
			List<String> columns) {
		super(context);
		this.header = header;
		this.row = row;
		XYSeriesCollection dataset = createDataset(results, columns);
		AFreeChart chart = createChart(dataset);

		setChart(chart);
	}

	/**
	 * Create a dataset from the given results.
	 * 
	 * @return The dataset.
	 */
	private XYSeriesCollection createDataset(List<List<Double>> results, List<String> columns) {

		XYSeriesCollection dataset = new XYSeriesCollection();

		for (int i = 0; i < columns.size(); i++) {
			
			List<Double> resultSet = results.get(i);

			// Add in cumulative savings
			double cumulativeSavings = 0.0;
			XYSeries cumulativeSavingsSeries = new XYSeries(columns.get(i));
			for (int j = 0; j < resultSet.size(); j++) {
				cumulativeSavingsSeries.add(j, resultSet.get(j));
			}

			dataset.addSeries(cumulativeSavingsSeries);
		}

		return dataset;

	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            the dataset.
	 * 
	 * @return The chart.
	 */
	private AFreeChart createChart(XYSeriesCollection dataset) {

		AFreeChart chart = ChartFactory.createXYLineChart(header, row, "ROI ($)", dataset,
				PlotOrientation.VERTICAL, true, true, false);

		// get a reference to the plot for further customisation
		XYPlot plot = chart.getXYPlot();

		// change the auto tick unit selection to integer units only
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

		// // set the background color and border for the chart...
		chart.setBackgroundPaintType(new SolidColor(Color.BLACK));
		chart.setBorderVisible(false);
		chart.getTitle().setPaintType(new SolidColor(Color.LTGRAY));
		chart.getTitle().setBackgroundPaint(new Paint(Color.BLACK));
		chart.getLegend().setBackgroundPaintType(new SolidColor(Color.BLACK));
		chart.getLegend().setItemPaintType(new SolidColor(Color.LTGRAY));

		// Get the line renderer.
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		// renderer.setDrawBarOutline(false);

		// set up gradient paints for series...
		GradientColor gp0 = new GradientColor(0xFF33B5E5, Color.rgb(0, 0, 64));
		GradientColor gp1 = new GradientColor(Color.GREEN, Color.rgb(0, 64, 0));
		GradientColor gp2 = new GradientColor(Color.RED, Color.rgb(64, 0, 0));
		renderer.setSeriesPaintType(0, gp0);
		renderer.setSeriesPaintType(1, gp1);
		renderer.setSeriesPaintType(2, gp2);

		// Set the plot area background, and gridline colour
		plot.setBackgroundPaintType(new SolidColor(Color.BLACK));
		plot.setDomainGridlinePaintType(new SolidColor(Color.DKGRAY));
		plot.setRangeGridlinePaintType(new SolidColor(Color.DKGRAY));
		plot.setBackgroundAlpha(0);

		// OPTIONAL CUSTOMISATION COMPLETED.

		return chart;

	}

}
