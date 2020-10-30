/*
 * Pipeline Telluric Effect Modeller
 */
package ru.gss.ptemodeller.data;

/**
 * Reper.
 * @version 1.1.0 09.04.2020
 * @author Sergey Guskov
 */
public class Reper {

    /**
     * Type (0 - point, 1 - interval).
     */
    private int type;
    /**
     * Start coordinate, m.
     */
    private double coord1;
    /**
     * End coordinate, m.
     */
    private double coord2;
    /**
     * Name.
     */
    private String name;

    /**
     * Constructor.
     */
    public Reper() {
        type = 0;
        name = "";
    }

    /**
     * Type.
     * @return type
     */
    public int getType() {
        return type;
    }

    /**
     * Type.
     * @param aType type
     */
    public void setType(final int aType) {
        type = aType;
    }

    /**
     * Start coordinate.
     * @return start coordinate
     */
    public double getCoord1() {
        return coord1;
    }

    /**
     * Start coordinate.
     * @param aCoord1 start coordinate
     */
    public void setCoord1(final double aCoord1) {
        coord1 = aCoord1;
    }

    /**
     * End coordinate.
     * @return end coordinate
     */
    public double getCoord2() {
        return coord2;
    }

    /**
     * End coordinate.
     * @param aCoord2 end coordinate
     */
    public void setCoord2(final double aCoord2) {
        coord2 = aCoord2;
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
}
