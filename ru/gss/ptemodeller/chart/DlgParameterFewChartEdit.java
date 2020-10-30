/*
 * Pipeline Telluric Effect Modeller
 */
package ru.gss.ptemodeller.chart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.text.DefaultFormatterFactory;
import org.jdesktop.application.Action;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import ru.gss.ptemodeller.commons.DlgDirEdit;
import ru.gss.ptemodeller.commons.NoLocaleNumberFormatter;

/**
 * Dialog for edit of chart parameters.
 * @version 1.1.0 06.04.2020
 * @author Sergey Guskov
 */
public class DlgParameterFewChartEdit extends DlgDirEdit < JFreeChart > {

    /**
     * Index of current axis.
     */
    private int currentAxis;
    /**
     * List of axes.
     */
    private ArrayList<ParameterAxis> axes;

    /**
     * Constructor.
     */
    public DlgParameterFewChartEdit() {
        super();
        initComponents();
        jftfX1.setFormatterFactory(new DefaultFormatterFactory(new NoLocaleNumberFormatter(2)));
        jftfX2.setFormatterFactory(new DefaultFormatterFactory(new NoLocaleNumberFormatter(2)));
        jftfX3.setFormatterFactory(new DefaultFormatterFactory(new NoLocaleNumberFormatter(2)));
        jftfY1.setFormatterFactory(new DefaultFormatterFactory(new NoLocaleNumberFormatter(2)));
        jftfY2.setFormatterFactory(new DefaultFormatterFactory(new NoLocaleNumberFormatter(2)));
        jftfY3.setFormatterFactory(new DefaultFormatterFactory(new NoLocaleNumberFormatter(2)));
        axes = new ArrayList<ParameterAxis>();
        jcbRangeAxis.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                getData(currentAxis);
                currentAxis = jcbRangeAxis.getSelectedIndex();
                setData(currentAxis);
            }
        });
    }

    /**
     * Setter editing object.
     * @param aTempObj editing object
     */
    @Override
    public void setTempObj(final JFreeChart aTempObj) {
        putTempObj(aTempObj);
        CombinedDomainXYPlot plot = (CombinedDomainXYPlot) getTempObj().getPlot();
        axes.clear();
        currentAxis = 0;
        String[] items = new String[plot.getSubplots().size()];
        for (int i = 0; i < plot.getSubplots().size(); i++) {
            XYPlot subplot = (XYPlot) plot.getSubplots().get(i);
            ParameterAxis axis = new ParameterAxis();
            axis.setMin(subplot.getRangeAxis().getRange().getLowerBound());
            axis.setMax(subplot.getRangeAxis().getRange().getUpperBound());
            axis.setStep(((NumberAxis) subplot.getRangeAxis()).getTickUnit().getSize());
            axis.setAutoRange(subplot.getRangeAxis().isAutoRange());
            axis.setAutoStep(subplot.getRangeAxis().isAutoTickUnitSelection());
            axes.add(axis);
            items[i] = subplot.getRangeAxis().getLabel(); //"Диаграмма " + (i + 1);
        }
        jcbRangeAxis.setModel(new DefaultComboBoxModel(items));
        jftfX1.setValue(plot.getDomainAxis().getRange().getLowerBound());
        jftfX2.setValue(plot.getDomainAxis().getRange().getUpperBound());
        jftfX3.setValue(((NumberAxis) getTempObj().getXYPlot().getDomainAxis()).getTickUnit().getSize());
        jchbX1.setSelected(getTempObj().getXYPlot().getDomainAxis().isAutoRange());
        jchbX3.setSelected(getTempObj().getXYPlot().getDomainAxis().isAutoTickUnitSelection());
        setData(jcbRangeAxis.getSelectedIndex());  
        getRootPane().setDefaultButton(jbtnOk);
    }

    /**
     * Setting data of selected axis.
     * @param i index of axis
     */
    private void setData(final int i) {
        jftfY1.setValue(axes.get(i).getMin());
        jftfY2.setValue(axes.get(i).getMax());
        jftfY3.setValue(axes.get(i).getStep());
        jchbY1.setSelected(axes.get(i).isAutoRange());
        jchbY3.setSelected(axes.get(i).isAutoStep());
    }

    /**
     * Getting data of selected axis.
     * @param i index of axis
     */
    private void getData(final int i) {
        axes.get(i).setMin(getDoubleFromFormattedTextField(jftfY1));
        axes.get(i).setMax(getDoubleFromFormattedTextField(jftfY2));
        axes.get(i).setStep(getDoubleFromFormattedTextField(jftfY3));
        axes.get(i).setAutoRange(jchbY1.isSelected());
        axes.get(i).setAutoStep(jchbY3.isSelected());
    }

    /**
     * Init new object.
     * @return new object
     */
    @Override
    public JFreeChart createTempObj() {
        return null;
    }

    /**
     * Action for Cancel button.
     */
    @Action
    public void acCancel() {
        setChangeObj(false);
    }

    /**
     * Action for OK button.
     */
    @Action
    public void acOk() {
        CombinedDomainXYPlot plot = (CombinedDomainXYPlot) getTempObj().getPlot();
        plot.getDomainAxis().setRange(getDoubleFromFormattedTextField(jftfX1), getDoubleFromFormattedTextField(jftfX2));
        ((NumberAxis) plot.getDomainAxis()).setTickUnit(new NumberTickUnit(getDoubleFromFormattedTextField(jftfX3)));
        plot.getDomainAxis().setAutoRange(jchbX1.isSelected());
        plot.getDomainAxis().setAutoTickUnitSelection(jchbX3.isSelected());
        getData(jcbRangeAxis.getSelectedIndex());
        for (int i = 0; i < plot.getSubplots().size(); i++) {
            XYPlot subplot = (XYPlot) plot.getSubplots().get(i);
            subplot.getRangeAxis().setRange(axes.get(i).getMin(), axes.get(i).getMax());
            ((NumberAxis) subplot.getRangeAxis()).setTickUnit(new NumberTickUnit(axes.get(i).getStep()));
            subplot.getRangeAxis().setAutoRange(axes.get(i).isAutoRange());
            subplot.getRangeAxis().setAutoTickUnitSelection(axes.get(i).isAutoStep());
        }
        setChangeObj(true);
    }

    //CHECKSTYLE:OFF
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbtnOk = new javax.swing.JButton();
        jbtnCancel = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jlbX1 = new javax.swing.JLabel();
        jftfX1 = new javax.swing.JFormattedTextField();
        jftfX2 = new javax.swing.JFormattedTextField();
        jftfX3 = new javax.swing.JFormattedTextField();
        jlbX3 = new javax.swing.JLabel();
        jchbX3 = new javax.swing.JCheckBox();
        jchbX1 = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jlbY1 = new javax.swing.JLabel();
        jftfY1 = new javax.swing.JFormattedTextField();
        jftfY2 = new javax.swing.JFormattedTextField();
        jftfY3 = new javax.swing.JFormattedTextField();
        jlbY3 = new javax.swing.JLabel();
        jchbY1 = new javax.swing.JCheckBox();
        jchbY3 = new javax.swing.JCheckBox();
        jcbRangeAxis = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.gss.ptemodeller.PTEModellerApp.class).getContext().getResourceMap(DlgParameterFewChartEdit.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ru.gss.ptemodeller.PTEModellerApp.class).getContext().getActionMap(DlgParameterFewChartEdit.class, this);
        jbtnOk.setAction(actionMap.get("acOk")); // NOI18N
        jbtnOk.setName("jbtnOk"); // NOI18N

        jbtnCancel.setAction(actionMap.get("acCancel")); // NOI18N
        jbtnCancel.setName("jbtnCancel"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        jlbX1.setText(resourceMap.getString("jlbX1.text")); // NOI18N
        jlbX1.setName("jlbX1"); // NOI18N

        jftfX1.setName("jftfX1"); // NOI18N
        jftfX1.setPreferredSize(new java.awt.Dimension(50, 20));

        jftfX2.setName("jftfX2"); // NOI18N
        jftfX2.setPreferredSize(new java.awt.Dimension(50, 20));

        jftfX3.setName("jftfX3"); // NOI18N
        jftfX3.setPreferredSize(new java.awt.Dimension(50, 20));

        jlbX3.setText(resourceMap.getString("jlbX3.text")); // NOI18N
        jlbX3.setName("jlbX3"); // NOI18N

        jchbX3.setText(resourceMap.getString("jchbX3.text")); // NOI18N
        jchbX3.setName("jchbX3"); // NOI18N

        jchbX1.setText(resourceMap.getString("jchbX1.text")); // NOI18N
        jchbX1.setName("jchbX1"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jlbX1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jchbX1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jlbX3, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jchbX3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jftfX1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jftfX2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jftfX3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbX1)
                    .addComponent(jftfX1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jftfX2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jchbX1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbX3)
                    .addComponent(jchbX3)
                    .addComponent(jftfX3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        jlbY1.setText(resourceMap.getString("jlbY1.text")); // NOI18N
        jlbY1.setName("jlbY1"); // NOI18N

        jftfY1.setName("jftfY1"); // NOI18N
        jftfY1.setPreferredSize(new java.awt.Dimension(50, 20));

        jftfY2.setName("jftfY2"); // NOI18N
        jftfY2.setPreferredSize(new java.awt.Dimension(50, 20));

        jftfY3.setName("jftfY3"); // NOI18N
        jftfY3.setPreferredSize(new java.awt.Dimension(50, 20));

        jlbY3.setText(resourceMap.getString("jlbY3.text")); // NOI18N
        jlbY3.setName("jlbY3"); // NOI18N

        jchbY1.setText(resourceMap.getString("jchbY1.text")); // NOI18N
        jchbY1.setName("jchbY1"); // NOI18N

        jchbY3.setText(resourceMap.getString("jchbY3.text")); // NOI18N
        jchbY3.setName("jchbY3"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbY1)
                    .addComponent(jlbY3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jchbY1)
                    .addComponent(jchbY3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jftfY1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jftfY2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jftfY3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbY1)
                    .addComponent(jftfY1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jftfY2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jchbY1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbY3)
                    .addComponent(jftfY3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jchbY3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jcbRangeAxis.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbRangeAxis.setName("jcbRangeAxis"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jcbRangeAxis, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(157, 157, 157)
                        .addComponent(jbtnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnOk)
                    .addComponent(jbtnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcbRangeAxis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnOk;
    private javax.swing.JComboBox jcbRangeAxis;
    private javax.swing.JCheckBox jchbX1;
    private javax.swing.JCheckBox jchbX3;
    private javax.swing.JCheckBox jchbY1;
    private javax.swing.JCheckBox jchbY3;
    private javax.swing.JFormattedTextField jftfX1;
    private javax.swing.JFormattedTextField jftfX2;
    private javax.swing.JFormattedTextField jftfX3;
    private javax.swing.JFormattedTextField jftfY1;
    private javax.swing.JFormattedTextField jftfY2;
    private javax.swing.JFormattedTextField jftfY3;
    private javax.swing.JLabel jlbX1;
    private javax.swing.JLabel jlbX3;
    private javax.swing.JLabel jlbY1;
    private javax.swing.JLabel jlbY3;
    // End of variables declaration//GEN-END:variables
    //CHECKSTYLE:ON
}
