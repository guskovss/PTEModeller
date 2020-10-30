/*
 * Pipeline Telluric Effect Modeller
 */
package ru.gss.ptemodeller.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import ru.gss.ptemodeller.data.DataList;
import ru.gss.ptemodeller.data.Reper;

/**
 * Chart.
 * @version 1.1.0 09.04.2020
 * @author Sergey Guskov
 */
public class ChartMaker {

    /**
     * Parent frame.
     */
    private Component parent;
    /**
     * Data.
     */
    private DataList data;
    /**
     * Dialog of chart of pipeline resistance per unit length.
     */
    private DlgChart dlgChartZ;
    /**
     * Dialog of chart of insulation conductivity per unit length.
     */
    private DlgChart dlgChartY;
    /**
     * Dialog of chart of external electric field strength.
     */
    private DlgChart dlgChartE;
    /**
     * Dialog of chart of pipeline current.
     */
    private DlgChart dlgChartI;
    /**
     * Dialog of chart of pipeline potential.
     */
    private DlgChart dlgChartU;
    /**
     * Dialog of chart of insulation current per unit length.
     */
    private DlgChart dlgChartD;

    /**
     * Constructor.
     * @param aParent parent frame
     * @param aData data
     */
    public ChartMaker(final Component aParent, final DataList aData) {
        parent = aParent;
        data = aData;
    }

    /**
     * Create chart.
     * @param dataset data
     * @param labelX name of axis x
     * @param labelY name of axis y
     * @param isStepPlot step chart
     * @return chart
     */
    public static JFreeChart createChart(final XYDataset dataset, final String labelX, final String labelY, final boolean isStepPlot) {
        XYPlot plot = createPlot(dataset, labelX, labelY, isStepPlot);
        JFreeChart chart = new JFreeChart("", plot);
        //Settings
        chart.setBackgroundPaint(Color.white);
        chart.getLegend().setPosition(RectangleEdge.RIGHT);
        chart.getLegend().setBorder(0, 0, 0, 0);
        return chart;  
    }

    /**
     * Create plot.
     * @param dataset data
     * @param labelX name of axis x
     * @param labelY name of axis y
     * @param isStepPlot step chart
     * @return plot
     */
    private static XYPlot createPlot(final XYDataset dataset, 
            final String labelX, final String labelY, final boolean isStepPlot) {
        NumberAxis xAxis = new NumberAxis(labelX);
        xAxis.setTickLabelFont(new Font("Tahoma", Font.PLAIN, 12));
        xAxis.setLabelFont(new Font("Tahoma", Font.BOLD, 12));
        xAxis.setAutoRangeIncludesZero(false);
        xAxis.setLowerMargin(0);
        xAxis.setUpperMargin(0);
        NumberAxis yAxis = new NumberAxis(labelY);
        yAxis.setTickLabelFont(new Font("Tahoma", Font.PLAIN, 12));
        yAxis.setLabelFont(new Font("Tahoma", Font.BOLD, 12));
        yAxis.setNumberFormatOverride(new DecimalFormat("0.00"));
        yAxis.setAutoRangeIncludesZero(false);        
        //Parameters of series
        XYItemRenderer renderer;
        if (isStepPlot) {
            renderer = new XYStepRenderer();
        } else {
            renderer = new XYLineAndShapeRenderer();
            for (int i = 0; i < 10; i++) {
                ((XYLineAndShapeRenderer) renderer).setSeriesShapesVisible(i, false);
            }
        }
        //Colors
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesPaint(2, new Color(0, 170, 0));
        renderer.setSeriesPaint(3, Color.BLACK);
        renderer.setSeriesPaint(4, Color.DARK_GRAY);
        renderer.setSeriesPaint(5, Color.GRAY);
        renderer.setSeriesPaint(6, Color.LIGHT_GRAY);
        renderer.setSeriesPaint(7, Color.MAGENTA);
        renderer.setSeriesPaint(8, Color.ORANGE);    
        //Tooltips
        for (int i = 0; i < 10; i++) {
            renderer.setSeriesToolTipGenerator(i, new StandardXYToolTipGenerator("{1}; {2}", NumberFormat.getNumberInstance(), NumberFormat.getNumberInstance()));
        }
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.darkGray);
        plot.setRangeGridlinePaint(Color.darkGray);
        plot.getRangeAxis().setAutoRangeMinimumSize(0.2);          
        plot.setAxisOffset(new RectangleInsets(4, 4, 4, 4));
        return plot;
    }

    /**
     * Create chart.
     * @param data data
     * @return chart
     */
    public static JFreeChart createChart(final DataList data) {
        JFreeChart chart = null;    
        NumberAxis domainAxis = new NumberAxis("x, км");
        domainAxis.setTickLabelFont(new Font("Tahoma", Font.PLAIN, 12));
        domainAxis.setLabelFont(new Font("Tahoma", Font.BOLD, 12));
        domainAxis.setTickMarksVisible(false);
        domainAxis.setAutoRangeIncludesZero(false);
        domainAxis.setLowerMargin(0);
        domainAxis.setUpperMargin(0);
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(domainAxis);
        if (data.getParameter().isShowChartZ()) {
            XYPlot subplot = choisePlot(data, 0);
            plot.add(subplot, 1);
        }
        if (data.getParameter().isShowChartY()) {
            XYPlot subplot = choisePlot(data, 1);
            plot.add(subplot, 1);
        }
        if (data.getParameter().isShowChartE()) {
            XYPlot subplot = choisePlot(data, 2);
            plot.add(subplot, 1);
        }
        if (data.getParameter().isShowChartI()) {
            XYPlot subplot = choisePlot(data, 3);
            plot.add(subplot, 1);
        }
        if (data.getParameter().isShowChartU()) {
            XYPlot subplot = choisePlot(data, 4);
            plot.add(subplot, 1);
        }
        if (data.getParameter().isShowChartD()) {
            XYPlot subplot = choisePlot(data, 5);
            plot.add(subplot, 1);
        }
        chart = new JFreeChart("", plot);
        chart.setBackgroundPaint(Color.white);
        chart.getLegend().setPosition(RectangleEdge.RIGHT);
        chart.getLegend().setBorder(0, 0, 0, 0);
        JFreeChart chartTemp = createChart(data.createDatasetZ(), null, null, true);
        LegendItemSource[] lis = chartTemp.getLegend().getSources();
        chart.getLegend().setSources(lis);
        if (data.getParameter().isShowLegend()) {
            chart.getLegend().setVisible(true);
            chart.setPadding(new RectangleInsets(0, 0, 0, 5));
        } else {
            chart.getLegend().setVisible(false);
            chart.setPadding(new RectangleInsets(0, 0, 0, 38));
        }
        return chart;
    }

    /**
     * Choise plot.
     * @param data data
     * @param index index of chart
     * @return plot
     */
    public static XYPlot choisePlot(final DataList data, final int index) {
        XYPlot plot = null;
        switch (index) {
            case 0:
                plot = createPlot(data.createDatasetZ(), "x, км", "Z, мкОм/м", true);
                break;
            case 1:
                plot = createPlot(data.createDatasetY(), "x, км", "Y, мкСм/м", true);
                break;
            case 2:
                plot = createPlot(data.createDatasetE(), "x, км", "E, мкВ/м", true);
                break;
            case 3:
                plot = createPlot(data.createDatasetI(), "x, км", "I, А", false);
                break;
            case 4:
                plot = createPlot(data.createDatasetU(), "x, км", "U, В", false);
                break;
            default:
                plot = createPlot(data.createDatasetD(), "x, км", "ΔI/Δx, мА/м", false);
                break;
        }
        createMarker(data, plot);
        return plot;
    }

    /**
     * Create markers.
     * @param data data
     * @param plot plot
     */
    public static void createMarker(final DataList data, final XYPlot plot) {
        for (int k = 0; k < data.getSite().size(); k++) {
            //Repers
            if (data.getSite().get(k).isShowReper()) {
                for (int i = 0; i < data.getSite().get(k).getReper().size(); i++) {
                    Reper rl = data.getSite().get(k).getReper().get(i);
                    //Markers
                    if (rl.getType() == 0) {
                        Marker m = new ValueMarker(rl.getCoord1() * 1e-3);
                        m.setStroke(new BasicStroke(1.5F));
                        m.setPaint(Color.ORANGE);
                        //Labels
                        String label = rl.getName();
                        if (label.startsWith("<L>")) {
                            m.setLabelAnchor(RectangleAnchor.TOP_LEFT);
                            m.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
                            label = label.substring(3, label.length());
                        } else {
                            m.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
                            m.setLabelTextAnchor(TextAnchor.TOP_LEFT);
                        }
                        m.setLabelOffsetType(LengthAdjustmentType.EXPAND);
                        m.setLabel(label);
                        m.setLabelFont(new Font("Tahoma", Font.PLAIN, 11));
                        m.setLabelPaint(Color.BLACK);
                        plot.addDomainMarker(m);
                    } else {
                        //Interval
                        Marker m = new IntervalMarker(rl.getCoord1() * 1e-3, rl.getCoord2() * 1e-3);
                        m.setAlpha(0.3F);
                        m.setPaint(Color.ORANGE);
                        plot.addDomainMarker(m);
                        //Labels
                        String label = rl.getName();
                        boolean leftLabel = false;
                        if (label.startsWith("<L>")) {
                            label = label.substring(3, label.length());
                            leftLabel = true;
                        }
                        //Left border
                        m = new ValueMarker(rl.getCoord1() * 1e-3);
                        m.setStroke(new BasicStroke(1.0F));
                        m.setPaint(Color.ORANGE);
                        if (leftLabel) {
                            m.setLabelAnchor(RectangleAnchor.TOP_LEFT);
                            m.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
                            m.setLabelOffsetType(LengthAdjustmentType.EXPAND);
                            m.setLabel(label);
                            m.setLabelFont(new Font("Tahoma", Font.PLAIN, 11));
                            m.setLabelPaint(Color.BLACK);
                        }
                        plot.addDomainMarker(m);
                        //Right border
                        m = new ValueMarker(rl.getCoord2() * 1e-3);
                        m.setStroke(new BasicStroke(1.0F));
                        m.setPaint(Color.ORANGE);
                        if (!leftLabel) {
                            m.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
                            m.setLabelTextAnchor(TextAnchor.TOP_LEFT);
                            m.setLabelOffsetType(LengthAdjustmentType.EXPAND);
                            m.setLabel(label);
                            m.setLabelFont(new Font("Tahoma", Font.PLAIN, 11));
                            m.setLabelPaint(Color.BLACK);
                        }
                        plot.addDomainMarker(m);
                    }
                }
            }
        }
    }

    /**
     * Create or refresh chart dialog.
     * @param currentDialog current chart dialog
     * @param dataset data
     * @param dTitle title of chart dialog
     * @param yLabel name of axis y
     * @param isStepPlot step chart
     * @param isShowLegend show legend
     * @return new chart dialog
     */
    private DlgChart createOrRefresh(final DlgChart currentDialog,
            final XYSeriesCollection dataset, final String dTitle,
            final String yLabel, final boolean isStepPlot, final boolean isShowLegend) {
        DlgChart newDialog;
        if (currentDialog == null) {
            newDialog = new DlgChart(dataset, dTitle, "x, км", yLabel, isStepPlot);
            newDialog.setLocationRelativeTo(parent);
        } else {
            newDialog = currentDialog;
            newDialog.refresh(dataset, isStepPlot);
        }     
        if (isShowLegend) {
            newDialog.getChart().getLegend().setVisible(true);
            newDialog.getChart().setPadding(new RectangleInsets(0, 0, 0, 5));
        } else {
            newDialog.getChart().getLegend().setVisible(false);
            newDialog.getChart().setPadding(new RectangleInsets(0, 0, 0, 38));
        }
        return newDialog;
    }

    /**
     * Create or refresh dialog of chart of pipeline resistance per unit length.
     */
    public void showChartZ() {
        dlgChartZ = createOrRefresh(dlgChartZ, data.createDatasetZ(),
                "Зависимость сопротивления трубопровода от координаты", "Z, мкОм/м", true, data.getParameter().isShowLegend());
        createMarker(data, dlgChartZ.getChart().getXYPlot());
        dlgChartZ.showChart();
    }

    /**
     * Create or refresh dialog of chart of insulation conductivity per unit length.
     */
    public void showChartY() {
        dlgChartY = createOrRefresh(dlgChartY, data.createDatasetY(),
                "Зависимость проводимости изоляции от координаты", "Y, мкСм/м", true, data.getParameter().isShowLegend());
        createMarker(data, dlgChartY.getChart().getXYPlot());
        dlgChartY.showChart();
    }

    /**
     * Create or refresh dialog of chart of external electric field strength.
     */
    public void showChartE() {
        dlgChartE = createOrRefresh(dlgChartE, data.createDatasetE(),
                "Зависимость напряженности внешнего электрического поля от координаты", "E, мкВ/м", true, data.getParameter().isShowLegend());
        createMarker(data, dlgChartE.getChart().getXYPlot());
        dlgChartE.showChart();
    }

    /**
     * Create or refresh dialog of chart of pipeline current.
     */
    public void showChartI() {
        dlgChartI = createOrRefresh(dlgChartI, data.createDatasetI(),
                "Зависимость силы тока в трубопроводе от координаты", "I, А", false, data.getParameter().isShowLegend());
        createMarker(data, dlgChartI.getChart().getXYPlot());
        dlgChartI.showChart();
    }

    /**
     * Create or refresh dialog of chart of pipeline potential.
     */
    public void showChartU() {
        dlgChartU = createOrRefresh(dlgChartU, data.createDatasetU(),
                "Зависимость разности потенциалов между трубопроводом и грунтом от координаты", "U, В", false, data.getParameter().isShowLegend());
        createMarker(data, dlgChartU.getChart().getXYPlot());
        dlgChartU.showChart();
    }

    /**
     * Create or refresh dialog of chart of insulation current per unit length.
     */
    public void showChartD() {
        dlgChartD = createOrRefresh(dlgChartD, data.createDatasetD(),
                "Зависимость силы тока, поступающего через изоляцию, от координаты", "ΔI/Δx, мА/м", false, data.getParameter().isShowLegend());
        createMarker(data, dlgChartD.getChart().getXYPlot());
        dlgChartD.showChart();
    }
}
