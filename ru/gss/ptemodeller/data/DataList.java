/*
 * Pipeline Telluric Effect Modeller
 */
package ru.gss.ptemodeller.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.swing.JTextArea;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * List of sites.
 * @version 1.1.0 09.04.2020
 * @author Sergey Guskov
 */
public class DataList {

    /**
     * Chart parameters.
     */
    private ParameterChart parameter;
    /**
     * List of sites.
     */
    private ArrayList<Site> site;
    /**
     * Index of current site.
     */
    private int siteIndex;

    /**
     * Constructor.
     */
    public DataList() {
        parameter = new ParameterChart();
        site = new ArrayList<Site>();
        siteIndex = -1;
    }

    /**
     * Save text area to file.
     * @param file file
     * @param jta text area
     * @throws java.io.IOException exception
     */
    public void saveTextAreaToFile(final File file, final JTextArea jta) throws IOException {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileOutputStream(file), true);
            out.print(jta.getText());
        } finally {
            out.close();
        }
    }

    /**
     * Count of sites.
     * @return count of sites
     */
    public int getSiteCount() {
        return site.size();
    }

    /**
     * Maximum count of sites.
     * @return maximum count of sites
     */
    public int getSiteCountMax() {
        return 9;
    }

    /**
     * Create dataset for chart of pipeline resistance per unit length.
     * @return dataset
     */
    public XYSeriesCollection createDatasetZ() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (int i = 0; i < site.size(); i++) {
            XYSeries series = new XYSeries(i + 1);
            series.add(site.get(i).getCoord1() * 1e-3, site.get(i).getZ() * 1e6);
            series.add(site.get(i).getCoord2() * 1e-3, site.get(i).getZ() * 1e6);
            dataset.addSeries(series);
        }
        return dataset;
    }

     /**
     * Create dataset for chart of insulation conductivity per unit length.
     * @return dataset
     */
    public XYSeriesCollection createDatasetY() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (int i = 0; i < site.size(); i++) {
            XYSeries series = new XYSeries(i + 1);
            series.add(site.get(i).getCoord1() * 1e-3, site.get(i).getY() * 1e6);
            series.add(site.get(i).getCoord2() * 1e-3, site.get(i).getY() * 1e6);
            dataset.addSeries(series);
        }
        return dataset;
    }

     /**
     * Create dataset for chart of external electric field strength.
     * @return dataset
     */
    public XYSeriesCollection createDatasetE() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (int i = 0; i < site.size(); i++) {
            XYSeries series = new XYSeries(i + 1);
            series.add(site.get(i).getCoord1() * 1e-3, site.get(i).getE() * 1e6);
            series.add(site.get(i).getCoord2() * 1e-3, site.get(i).getE() * 1e6);
            dataset.addSeries(series);
        }
        return dataset;
    }

    /**
     * Create dataset for chart pipeline current.
     * @return dataset
     */
    public XYSeriesCollection createDatasetI() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (int i = 0; i < site.size(); i++) {
            XYSeries series = new XYSeries(i + 1);
            for (int j = 0; j < site.get(i).getData().size(); j++) {
                series.add(site.get(i).getData().get(j).getCoordinate() * 1e-3, site.get(i).getData().get(j).getCurrent());
            }
            dataset.addSeries(series);
        }
        return dataset;
    }

    /**
     * Create dataset for chart pipeline potential.
     * @return dataset
     */
    public XYSeriesCollection createDatasetU() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (int i = 0; i < site.size(); i++) {
            XYSeries series = new XYSeries(i + 1);
            for (int j = 0; j < site.get(i).getData().size(); j++) {
                series.add(site.get(i).getData().get(j).getCoordinate() * 1e-3, site.get(i).getData().get(j).getPotential());
            }
            dataset.addSeries(series);
        }
        return dataset;
    }

    /**
     * Create dataset for chart of insulation current per unit length
     * @return dataset
     */
    public XYSeriesCollection createDatasetD() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (int i = 0; i < site.size(); i++) {
            XYSeries series = new XYSeries(i + 1);
            for (int j = 0; j < site.get(i).getData().size(); j++) {
                series.add(site.get(i).getData().get(j).getCoordinate() * 1e-3, site.get(i).getData().get(j).getCurrentDelta() * 1e3);
            }
            dataset.addSeries(series);
        }
        return dataset;
    }

    /**
     * Chart parameters.
     * @return chart parameters
     */
    public ParameterChart getParameter() {
        return parameter;
    }

    /**
     * List of sites.
     * @return list of sites
     */
    public ArrayList<Site> getSite() {
        return site;
    }

    /**
     * Current site.
     * @return current site
     */
    public Site getCurrentSite() {
        return site.get(siteIndex);
    }

    /**
     * Index of current site.
     * @return index of current site
     */
    public int getSiteIndex() {
        return siteIndex;
    }

    /**
     * Index of current site.
     * @param aSiteIndex index of current site
     */
    public void setSiteIndex(final int aSiteIndex) {
        siteIndex = aSiteIndex;
    }
}
