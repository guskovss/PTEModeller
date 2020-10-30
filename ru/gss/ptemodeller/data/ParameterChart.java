/*
 * Pipeline Telluric Effect Modeller
 */
package ru.gss.ptemodeller.data;

/**
 * Chart show parameters.
 * @version 1.1.0 09.04.2020
 * @author Sergey Guskov
 */
public class ParameterChart {
    
    /**
     * Show chart of pipeline resistance per unit length.
     */
    private boolean showChartZ;
    /**
     * Show chart of insulation conductivity per unit length.
     */
    private boolean showChartY;
    /**
     * Show chart of external electric field strength.
     */
    private boolean showChartE;
    /**
     * Show chart of pipeline current.
     */
    private boolean showChartI;
    /**
     * Show chart of pipeline potential.
     */
    private boolean showChartU;
    /**
     * Show chart of insulation current per unit length.
     */
    private boolean showChartD;
    /**
     * Show legend.
     */
    private boolean showLegend;
    
    /**
     * Constructor.
     */
    public ParameterChart() {
        showChartZ = false;
        showChartY = false;
        showChartE = true;
        showChartI = true;
        showChartU = true;
        showChartD = false;
        showLegend = false;
    }

    /**
     * Show chart of pipeline resistance per unit length.
     * @return show chart of pipeline resistance per unit length
     */
    public boolean isShowChartZ() {
        return showChartZ;
    }

    /**
     * Show chart of pipeline resistance per unit length.
     * @param aShowChartZ show chart of pipeline resistance per unit length
     */
    public void setShowChartZ(final boolean aShowChartZ) {
        showChartZ = aShowChartZ;
    }

    /**
     * Show chart of insulation conductivity per unit length.
     * @return show chart of insulation conductivity per unit length
     */
    public boolean isShowChartY() {
        return showChartY;
    }

    /**
     * Show chart of insulation conductivity per unit length.
     * @param aShowChartY show chart of insulation conductivity per unit length
     */
    public void setShowChartY(final boolean aShowChartY) {
        showChartY = aShowChartY;
    }

    /**
     * Show chart of external electric field strength.
     * @return show chart of external electric field strength
     */
    public boolean isShowChartE() {
        return showChartE;
    }

    /**
     * Show chart of external electric field strength.
     * @param aShowChartE show chart of external electric field strength
     */
    public void setShowChartE(final boolean aShowChartE) {
        showChartE = aShowChartE;
    }

    /**
     * Show chart of pipeline current.
     * @return show chart of pipeline current
     */
    public boolean isShowChartI() {
        return showChartI;
    }

    /**
     * Show chart of pipeline current.
     * @param aShowChartI show chart of pipeline current
     */
    public void setShowChartI(final boolean aShowChartI) {
        showChartI = aShowChartI;
    }

    /**
     * Show chart of pipeline potential.
     * @return show chart of pipeline potential
     */
    public boolean isShowChartU() {
        return showChartU;
    }

    /**
     * Show chart of pipeline potential.
     * @param aShowChartU show chart of pipeline potential
     */
    public void setShowChartU(final boolean aShowChartU) {
        showChartU = aShowChartU;
    }

    /**
     * Show chart of insulation current.
     * @return show chart of insulation current
     */
    public boolean isShowChartD() {
        return showChartD;
    }

    /**
     * Show chart of insulation current.
     * @param aShowChartD show chart of insulation current
     */
    public void setShowChartD(final boolean aShowChartD) {
        showChartD = aShowChartD;
    }

    /**
     * Show legend.
     * @return show legend
     */
    public boolean isShowLegend() {
        return showLegend;
    }

    /**
     * Show legend.
     * @param aShowLegend show legend
     */
    public void setShowLegend(final boolean aShowLegend) {
        showLegend = aShowLegend;
    }
}
