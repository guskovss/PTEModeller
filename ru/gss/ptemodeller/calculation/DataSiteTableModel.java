/*
 * Pipeline Telluric Effect Modeller
 */
package ru.gss.ptemodeller.calculation;

import java.util.Locale;
import javax.swing.table.AbstractTableModel;
import ru.gss.ptemodeller.data.DataList;
import ru.gss.ptemodeller.data.DataLine;

/**
 * Model of data table.
 * @version 1.1.0 03.04.2020
 * @author Sergey Guskov
 */
public class DataSiteTableModel extends AbstractTableModel {

    /**
     * Data.
     */
    private DataList data;
    /**
     * Headers of table columns.
     */
    private String[] colNames = {"x, км", "I, А", "U, В", "ΔI/Δx, мА/м"};
 
    /**
     * Constructor.
     * @param aData data
     */
    public DataSiteTableModel(final DataList aData) {
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
        if (data.getSiteIndex() < 0) {
            return 0;
        } else {
            return data.getSite().get(data.getSiteIndex()).getData().size();
        }
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
     * @param format count of symbols after separator
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
        DataLine o = data.getSite().get(data.getSiteIndex()).getData().get(rowIndex);
        switch (columnIndex) {
            case 0:
                return convertToString(o.getCoordinate() * 1e-3);
            case 1:
                return convertToString(o.getCurrent());
            case 2:
                return convertToString(o.getPotential());
            case 3:
                return convertToString(o.getCurrentDelta() * 1e3);
            default:
                return null;
        }
    }
}
