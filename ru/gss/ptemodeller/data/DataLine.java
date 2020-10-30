/*
 * Pipeline Telluric Effect Modeller
 */
package ru.gss.ptemodeller.data;

/**
 * Result of calculation.
 * @version 1.1.0 09.04.2020
 * @author Sergey Guskov
 */
public class DataLine {

    /**
     * Coordinate, m.
     */
    private double coordinate;
    /**
     * Pipeline current, A.
     */
    private double current;
    /**
     * Pipeline potential, V.
     */
    private double potential;
    /**
     * Insulation current per unit length, A/m.
     */
    private double currentDelta;
    
    /**
     * Constructor.
     */
    public DataLine() {
        coordinate = 0.0;
    }

    /**
     * Coordinate.
     * @return coordinate
     */
    public double getCoordinate() {
        return coordinate;
    }

    /**
     * Coordinate.
     * @param aCoordinate coordinate
     */
    public void setCoordinate(final double aCoordinate) {
        coordinate = aCoordinate;
    }
    
    /**
     * Pipeline current.
     * @return pipeline current
     */
    public double getCurrent() {
        return current;
    }

    /**
     * Pipeline current.
     * @param aCurrent pipeline current
     */
    public void setCurrent(final double aCurrent) {
        current = aCurrent;
    }

    /**
     * Pipeline potential.
     * @return pipeline potential
     */
    public double getPotential() {
        return potential;
    }

    /**
     * Pipeline potential.
     * @param aPotential pipeline potential
     */
    public void setPotential(final double aPotential) {
        potential = aPotential;
    }

    /**
     * Insulation current per unit length.
     * @return insulation current per unit length
     */
    public double getCurrentDelta() {
        return currentDelta;
    }

    /**
     * Insulation current per unit length.
     * @param aCurrentDelta insulation current per unit length
     */
    public void setCurrentDelta(final double aCurrentDelta) {
        currentDelta = aCurrentDelta;
    }
}
