/*
 * Pipeline Telluric Effect Modeller
 */
package ru.gss.ptemodeller.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Site.
 * @version 1.1.0 09.04.2020
 * @author Sergey Guskov
 */
public class Site {

    /**
     * Name.
     */
    private String name;
    /**
     * Name of file.
     */
    private String fileName;
    /**
     * Start coordinate of site, m.
     */
    private double coord1;
    /**
     * End coordinate of site, m.
     */
    private double coord2;
    /**
     * Count of elementary sites.
     */
    private int valueCount;
    /**
     * Pipeline resistance per unit length, Ohm/m.
     */
    private double z;
    /**
     * Insulation conductivity per unit length, Sm/m.
     */
    private double y;
    /**
     * External electric field strength, V/m.
     */
    private double e;
    /**
     * List of repers.
     */
    private ArrayList<Reper> reper;
    /**
     * Result of calculation.
     */
    private ArrayList<DataLine> data;
    /**
     * Count of parse exeptions.
     */
    private int parseExceptionCount;
    /**
     * Show repers on charts.
     */
    private boolean showReper;

    /**
     * Constructor.
     */
    public Site() {
        name = "";
        fileName = "";
        coord1 = 0.0;
        coord2 = 1000.0 * 1e3;
        valueCount = 100;
        z = 2.2 * 1e-6;
        y = 44.6 * 1e-6;
        e = 20 * 1e-6;
        reper = new ArrayList<Reper>();
        data = new ArrayList<DataLine>();
        parseExceptionCount = 0;
        showReper = true;
    }

    /**
     * Parse integer value.
     * @param s string representation of integer value
     * @return integer value or null
     */
    private Integer parseInteger(final String s) {
        if (s.trim().isEmpty()) {
            return null;
        }
        if (s.equals("-")) {
            return null;
        }
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException ex) {
            parseExceptionCount++;
            return null;
        }
    }

    /**
     * Parse double value.
     * @param s string representation of double value
     * @return double value or null
     */
    private Double parseDouble(final String s) {
        if (s.trim().isEmpty()) {
            return null;
        }
        if (s.equals("-")) {
            return null;
        }
        try {
            String ss = s.replaceAll(",", ".");
            return Double.valueOf(ss);
        } catch (NumberFormatException ex) {
            parseExceptionCount++;
            return null;
        }
    }

    /**
     * Convert integer value to string.
     * @param value integer value
     * @return string representation of value
     */
    private String convertToString(final Integer value) {
        if (value == null) {
            return " ";
        }
        return String.valueOf(value);
    }

    /**
     * Convert double value to string.
     * @param value double value
     * @return string representation of value
     */
    private String convertToString(final Double value) {
        if (value == null) {
            return " ";
        }
        return String.format(Locale.US, "%.2f", value);
    }

    /**
     * Convert double value to string.
     * @param value double value
     * @param precision count of symbols after separator
     * @return string representation of value
     */
    private String convertToString(final Double value, final int precision) {
        if (value == null) {
            return " ";
        }
        return String.format(Locale.US, "%." + precision + "f", value);
    }

     /**
     * Load data from file.
     * @param file file
     * @throws java.io.IOException exception
     */
    public void loadDataFromFile(final File file) throws IOException {
        BufferedReader reader = null;
        try {
            //Read all lines from file
            reader = new BufferedReader(new FileReader(file));
            ArrayList<String> strings = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                strings.add(line);
            }
            data.clear();
            reper.clear();
            parseExceptionCount = 0;
            fileName = file.getAbsolutePath();
            //Parse data
            int i1 = 0, i2 = 0, i3 = 0, i4 = 0, n1, n2, n3, n4, n5;
            for (int i = 0; i < strings.size(); i++) {
                if (strings.get(i).startsWith("Parameter")) {
                    i1 = i + 1;
                }
                if (strings.get(i).startsWith("Data")) {
                    i2 = i + 1;
                }
                if (strings.get(i).startsWith("Result")) {
                    i3 = i + 1;
                }
                if (strings.get(i).startsWith("Reper")) {
                    i4 = i + 1;
                }
            }
            n1 = i2 - i1 - 1;
            if (i1 > 0) {
                String[] s = strings.get(i1).split("\t");
                int columnCount = s.length;
                if (columnCount > 0) {
                    setName(s[0].trim());
                }
            }
            n2 = i3 - i2 - 1;
            if (i2 > 0) {
                if (i2 > 0) {
                    String[] s = strings.get(i2).split("\t");
                    int columnCount = s.length;
                    if (columnCount > 0) {
                        setCoord1(parseDouble(s[0]) * 1e3);
                    }
                    if (columnCount > 1) {
                       setCoord2(parseDouble(s[1]) * 1e3);
                    }
                    if (columnCount > 2) {
                        setZ(parseDouble(s[2]) * 1e-6);
                    }
                    if (columnCount > 3) {
                        setY(parseDouble(s[3]) * 1e-6);
                    }
                    if (columnCount > 4) {
                        setE(parseDouble(s[4]) * 1e-6);
                    }
                    if (columnCount > 5) {
                        setValueCount(parseInteger(s[5]));
                    }
                }
            }
            n3 = i4 - i3 - 1;
            if (i3 > 0) {
                for (int i = i3; i < i3 + n3; i++) {
                    DataLine o = new DataLine();
                    String[] s = strings.get(i).split("\t");
                    int columnCount = s.length;
                    if (columnCount > 0) {
                        o.setCoordinate(parseDouble(s[0]) * 1e3);
                    }
                    if (columnCount > 1) {
                        o.setCurrent(parseDouble(s[1]));
                    }
                    if (columnCount > 2) {
                        o.setPotential(parseDouble(s[2]));
                    }
                    if (columnCount > 3) {
                        o.setCurrentDelta(parseDouble(s[3]));
                    }
                    getData().add(o);
                }
            }
            n4 = strings.size() - i4;
            if (i4 > 0) {
                for (int i = i4; i < i4 + n4; i++) {
                    Reper o = new Reper();
                    String[] s = strings.get(i).split("\t");
                    int columnCount = s.length;
                    if (columnCount > 0) {
                        o.setType(parseInteger(s[0].trim()));
                    }
                    if (columnCount > 1) {
                        o.setCoord1(parseDouble(s[1]) * 1e3);
                    }
                    if (columnCount > 2) {
                        o.setCoord2(parseDouble(s[2]) * 1e3);
                    }
                    if (columnCount > 3) {
                        o.setName(s[3].trim());
                    }
                    reper.add(o);
                }
            }
        } finally {
            reader.close();
        }
    }

    /**
     * Save data to file.
     * @param file file
     * @throws java.io.IOException exception
     */
    public void saveDataToFile(final File file) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            String line;
            //Save parameters
            writer.write("Parameter");
            writer.newLine();
            line = getName();
            writer.write(line);
            writer.newLine();
            //Save data
            writer.write("Data");
            writer.newLine();
            line = convertToString(getCoord1() * 1e-3) + "\t" +
                    convertToString(getCoord2() * 1e-3) + "\t" +
                    convertToString(getZ() * 1e6) + "\t" +
                    convertToString(getY() * 1e6) + "\t" +
                    convertToString(getE() * 1e6) + "\t" +
                    convertToString(getValueCount());
            writer.write(line);
            writer.newLine();
            //Save results
            writer.write("Result");
            writer.newLine();
            for (int i = 0; i < data.size(); i++) {
                DataLine dl = data.get(i);
                line = convertToString(dl.getCoordinate() * 1e-3, 4) + "\t" +
                        convertToString(dl.getCurrent(), 4) + "\t" +
                        convertToString(dl.getPotential(), 4) + "\t" +
                        convertToString(dl.getCurrentDelta(), 4);
                writer.write(line);
                writer.newLine();
            }
            //Save repers
            writer.write("Reper");
            writer.newLine();
            for (int i = 0; i < reper.size(); i++) {
                Reper o = reper.get(i);
                line = convertToString(o.getType()) + "\t" +
                        convertToString(o.getCoord1() * 1e-3) + "\t" +
                        convertToString(o.getCoord2() * 1e-3) + "\t" +
                        o.getName();
                writer.write(line);
                writer.newLine();
            }
            fileName = file.getAbsolutePath();
        } finally {
            writer.close();
        }
    }

     /**
     * Current and potential calculation.
     */
    public void calculate() {
        double l = coord2 - coord1;
        double dx = l / valueCount;
        double g = Math.sqrt(z * y);
        double c1 = e / z * (Math.exp(-g * l) - 1) / (Math.exp(g * l) - Math.exp(-g * l));
        double c2 = e / z * (1 - Math.exp(g * l)) / (Math.exp(g * l) - Math.exp(-g * l));
        data.clear();
        for (int i = 0; i < valueCount + 1; i++) {
            DataLine dl = new DataLine();
            double x = i * dx;
            dl.setCoordinate(x + coord1);
            double c = c1 * Math.exp(g * x) + c2 * Math.exp(-g * x) + e / z;
            double p = -Math.sqrt(z / y) * (c1 * Math.exp(g * x) - c2 * Math.exp(-g * x));
            dl.setCurrent(c);
            dl.setPotential(p);
            dl.setCurrentDelta(-p * y);
            data.add(dl);
        }
    }

    /**
     * Name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Name.
     * @param aName name
     */
    public void setName(final String aName) {
        name = aName;
    }

    /**
     * Name of file.
     * @return name of file
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Name of file.
     * @param aFileName name of file
     */
    public void setFileName(final String aFileName) {
        fileName = aFileName;
    }

    /**
     * Start coordinate of site.
     * @return start coordinate of site
     */
    public double getCoord1() {
        return coord1;
    }

    /**
     * Start coordinate of site.
     * @param aCoord1 start coordinate of site
     */
    public void setCoord1(final double aCoord1) {
        coord1 = aCoord1;
    }

    /**
     * End coordinate of site.
     * @return end coordinate of site
     */
    public double getCoord2() {
        return coord2;
    }

    /**
     * End coordinate of site.
     * @param aCoord2 end coordinate of site
     */
    public void setCoord2(final double aCoord2) {
        coord2 = aCoord2;
    }

    /**
     * Count of elementary sites.
     * @return count of elementary sites
     */
    public int getValueCount() {
        return valueCount;
    }

    /**
     * Count of elementary sites.
     * @param aValueCount count of elementary sites
     */
    public void setValueCount(final int aValueCount) {
        valueCount = aValueCount;
    }

    /**
     * Pipeline resistance per unit length.
     * @return pipeline resistance per unit length
     */
    public double getZ() {
        return z;
    }

    /**
     * Pipeline resistance per unit length.
     * @param aZ pipeline resistance per unit length
     */
    public void setZ(final double aZ) {
        z = aZ;
    }

    /**
     * Insulation conductivity per unit length.
     * @return insulation conductivity per unit length
     */
    public double getY() {
        return y;
    }

    /**
     * Insulation conductivity per unit length.
     * @param aY insulation conductivity per unit length
     */
    public void setY(final double aY) {
        y = aY;
    }

   /**
     * External electric field strength.
     * @return external electric field strength
     */
    public double getE() {
        return e;
    }

   /**
     * External electric field strength.
     * @param aE external electric field strength
     */
    public void setE(final double aE) {
        e = aE;
    }

    /**
     * Show repers on charts.
     * @return show repers on charts
     */
    public boolean isShowReper() {
        return showReper;
    }

    /**
     * Show repers on charts.
     * @param aShowReper show repers on charts
     */
    public void setShowReper(final boolean aShowReper) {
        showReper = aShowReper;
    }

    /**
     * Count of parse exeptions.
     * @return count of parse exeptions
     */
    public int getParseExceptionCount() {
        return parseExceptionCount;
    }

    /**
     * List of repers.
     * @return list of repers
     */
    public ArrayList<Reper> getReper() {
        return reper;
    }

    /**
     * Result of calculation.
     * @return result of calculation
     */
    public ArrayList<DataLine> getData() {
        return data;
    }
}
