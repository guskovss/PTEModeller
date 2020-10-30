/*
 * Pipeline Telluric Effect Modeller
 */
package ru.gss.ptemodeller.calculation;

import java.util.Locale;
import javax.swing.table.AbstractTableModel;
import ru.gss.ptemodeller.data.DataList;
import ru.gss.ptemodeller.data.Site;

/**
 * Model of sites table.
 * @version 1.1.0 03.04.2020
 * @author Sergey Guskov
 */
public class SiteTableModel extends AbstractTableModel {

    /**
     * Data.
     */
    private DataList data;
    /**
     * Headers of table columns.
     */
    private String[] colNames = {"x, км", "Z, мкОм/м", "Y, мкСм/м", "E, мкВ/м"};
 
    /**
     * Constructor.
     * @param aData data
     */
    public SiteTableModel(final DataList aData) {
        data = aData;
    }

    /**
     * Header of table column.
     * @param column index of table column
     * @return header of table column
     */
    @Override
    public String getColumnName(final int column) {
        return colNames[column];
    }

    /**
     * Count of table column.
     * @return count of table column
     */
    public int getColumnCount() {
        return 4;
    }

    /**
     * Count of table row.
     * @return count of table row
     */
    public int getRowCount() {
        return data.getSite().size();
    }

    /**
     * Class of table column.
     * @param columnIndex index of table column
     * @return class of table column
     */
    @Override
    public Class < ? > getColumnClass(final int columnIndex) {
        return String.class;
    }

    /**
     * Convertation number to string.
     * @param value number
     * @return string representation of number
     */
    private String convertToString(final Double value) {
        if (value == null) {
            return "";
        }
        return String.format(Locale.US, "%.2f", value);
    }

    /**
     * Value of table cell.
     * @param rowIndex index of table row
     * @param columnIndex index of table column
     * @return value of table cell
     */
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        Site o = data.getSite().get(rowIndex);
        switch (columnIndex) {
            case 0:
                return convertToString(o.getCoord1() * 1e-3) + " - " +
                        convertToString(o.getCoord2() * 1e-3);
            case 1:
                return convertToString(o.getZ() * 1e6);
            case 2:
                return convertToString(o.getY() * 1e6);
            case 3:
                return convertToString(o.getE() * 1e6);
            default:
                return null;
        }
    }
}
