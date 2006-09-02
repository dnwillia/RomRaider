package enginuity.logger.ui.handler;

import enginuity.logger.definition.EcuData;
import static enginuity.logger.ui.SpringUtilities.makeCompactGrid;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import static org.jfree.chart.plot.PlotOrientation.VERTICAL;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import static java.util.Collections.synchronizedMap;
import java.util.HashMap;
import java.util.Map;

public final class GraphUpdateHandler implements DataUpdateHandler {
    private final JPanel graphPanel;
    private final Map<EcuData, ChartPanel> chartMap = synchronizedMap(new HashMap<EcuData, ChartPanel>());
    private final Map<EcuData, XYSeries> seriesMap = synchronizedMap(new HashMap<EcuData, XYSeries>());
    private int loggerCount = 0;

    public GraphUpdateHandler(JPanel graphPanel) {
        this.graphPanel = graphPanel;
    }

    public void registerData(EcuData ecuData) {
        // add to charts
        final XYSeries series = new XYSeries(ecuData.getName());
        //TODO: Make chart max item count configurable via settings
        series.setMaximumItemCount(100);
        final XYDataset xyDataset = new XYSeriesCollection(series);
        final JFreeChart chart = ChartFactory.createXYLineChart(ecuData.getName(), "Time (sec)", ecuData.getName()
                + " (" + ecuData.getConvertor().getUnits() + ")", xyDataset, VERTICAL, false, true, false);
        ChartPanel chartPanel = new ChartPanel(chart, false, true, true, true, true);
        graphPanel.add(chartPanel);
        seriesMap.put(ecuData, series);
        chartMap.put(ecuData, chartPanel);
        makeCompactGrid(graphPanel, ++loggerCount, 1, 10, 10, 20, 20);
        repaintGraphPanel(2);
    }

    public void handleDataUpdate(EcuData ecuData, byte[] value, long timestamp) {
        // update chart
        XYSeries series = seriesMap.get(ecuData);
        series.add(timestamp / 1000.0, ecuData.getConvertor().convert(value));
    }

    public void deregisterData(EcuData ecuData) {
        // remove from charts
        graphPanel.remove(chartMap.get(ecuData));
        chartMap.remove(ecuData);
        makeCompactGrid(graphPanel, --loggerCount, 1, 10, 10, 20, 20);
        repaintGraphPanel(1);
    }

    public void cleanUp() {
    }

    private void repaintGraphPanel(int parentRepaintLevel) {
        if (loggerCount < parentRepaintLevel) {
            graphPanel.doLayout();
            graphPanel.repaint();
        } else {
            if (loggerCount == 1) {
                graphPanel.doLayout();
            }
            graphPanel.getParent().doLayout();
            graphPanel.getParent().repaint();
        }
    }
}