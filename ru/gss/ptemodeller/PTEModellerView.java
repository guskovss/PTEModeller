/*
 * Pipeline Telluric Effect Modeller
 */
package ru.gss.ptemodeller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import ru.gss.ptemodeller.chart.ChartMaker;
import ru.gss.ptemodeller.calculation.DlgReperEdit;
import ru.gss.ptemodeller.calculation.DlgSiteEdit;
import ru.gss.ptemodeller.calculation.ReperTableModel;
import ru.gss.ptemodeller.calculation.DataSiteTableModel;
import ru.gss.ptemodeller.calculation.SiteTableModel;
import ru.gss.ptemodeller.chart.DlgParameterFewChartEdit;
import ru.gss.ptemodeller.commons.FileChooserFactory;
import ru.gss.ptemodeller.data.DataList;
import ru.gss.ptemodeller.data.Site;

/**
 * The main frame of the application.
 * @version 1.1.0 10.04.2020
 * @author Sergey Guskov
 */
public class PTEModellerView extends FrameView {

    static {
        UIManager.put("JXTable.column.horizontalScroll", "Горизонтальная прокрутка");
        UIManager.put("JXTable.column.packAll", "Упаковка всех столбцов");
        UIManager.put("JXTable.column.packSelected", "Упаковка выбранного столбца");
    }

    /**
     * Constructor.
     * @param app application
     */
    public PTEModellerView(final SingleFrameApplication app) {
        super(app);
        initComponents();

        //Icon
        //org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.gss.ptemodeller.PTEModellerApp.class).getContext().getResourceMap(PTEModellerView.class);
        //getFrame().setIconImage(resourceMap.getImageIcon("mainFrame.icon").getImage());

        //Translate
        UIManager.put("FileChooser.fileNameLabelText", "Имя файла:");
        UIManager.put("FileChooser.lookInLabelText", "Папка:");
        UIManager.put("FileChooser.saveInLabelText", "Папка:");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Тип:");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Фильтр");
        UIManager.put("FileChooser.upFolderToolTipText", "Наверх");
        UIManager.put("FileChooser.homeFolderToolTipText", "Домой");
        UIManager.put("FileChooser.newFolderToolTipText", "Новая папка");
        UIManager.put("FileChooser.listViewButtonToolTipText", "Список");
        UIManager.put("FileChooser.detailsViewButtonToolTipText", "Таблица");
        UIManager.put("FileChooser.saveButtonText", "Сохранить");
        UIManager.put("FileChooser.openButtonText", "Открыть");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.updateButtonText", "Обновить");
        UIManager.put("FileChooser.helpButtonText", "Справка");
        UIManager.put("FileChooser.saveButtonToolTipText", "Сохранить");
        UIManager.put("FileChooser.openButtonToolTipText", "Открыть");
        UIManager.put("FileChooser.cancelButtonToolTipText", "Отмена");
        UIManager.put("FileChooser.updateButtonToolTipText", "Обновить");
        UIManager.put("FileChooser.helpButtonToolTipText", "Справка");
        UIManager.put("FileChooser.openDialogTitleText", "Открыть");
        UIManager.put("FileChooser.saveDialogTitleText", "Сохранить как");
        UIManager.put("ProgressMonitor.progressText", "Загрузка...");
        UIManager.put("OptionPane.cancelButtonText", "Отмена");
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
        UIManager.put("OptionPane.messageDialogTitle", "Внимание");

        //Main objects
        data = new DataList();
        chartMaker = new ChartMaker(this.getComponent(), data);

        //Settings of site table
        tmSite = new SiteTableModel(data);
        jtSite.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jtSite.addHighlighter(HighlighterFactory.createSimpleStriping());
        jtSite.setModel(tmSite);
        jtSite.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(final ListSelectionEvent e) {
                int k = jtSite.getSelectedRow();
                if (k > -1) {
                    setSelectSite(true);
                    data.setSiteIndex(jtSite.convertRowIndexToModel(k));
                    jtbtnShowReper.setSelected(data.getCurrentSite().isShowReper());
                } else {
                    setSelectSite(false);
                    data.setSiteIndex(-1);
                }
                tmDataSite.fireTableDataChanged();
            }
        });
        jtSite.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if ((e.getClickCount() == 2) && (e.getButton() == 1) && (jtSite.getSelectedRow() >= 0)) {
                    acEditSite();
                }
            }
        });

        //Settings of site data table
        tmDataSite = new DataSiteTableModel(data);
        jtDataSite.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jtDataSite.addHighlighter(HighlighterFactory.createSimpleStriping());
        jtDataSite.setModel(tmDataSite);

        //Settings of reper table
        tmReper = new ReperTableModel(data);
        jtReper.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jtReper.addHighlighter(HighlighterFactory.createSimpleStriping());
        jtReper.setModel(tmReper);
        jtReper.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(final ListSelectionEvent e) {
                if (e.getFirstIndex() >= 0) {
                    setSelectReper(true);
                } else {
                    setSelectReper(false);
                }
            }
        });
        jtReper.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if ((e.getClickCount() == 2) && (e.getButton() == 1) && (jtReper.getSelectedRow() >= 0)) {
                    acEditReper();
                }
            }
        });

        //Buttons for chart visualisation
        jtbtnChart1.setSelected(data.getParameter().isShowChartZ());
        jtbtnChart2.setSelected(data.getParameter().isShowChartY());
        jtbtnChart3.setSelected(data.getParameter().isShowChartE());
        jtbtnChart4.setSelected(data.getParameter().isShowChartI());
        jtbtnChart5.setSelected(data.getParameter().isShowChartU());
        jtbtnChart6.setSelected(data.getParameter().isShowChartD());

        chartPanel = new ChartPanel(ChartMaker.createChart(data));
        chartPanel.setPopupMenu(jpmChart);
        chartPanel.setMouseWheelEnabled(true);
        jpChart.add(chartPanel);
    }

    /**
     * Save log to file.
     */
    @Action
    public void acSaveLogToFile() {
        JFileChooser chooser = FileChooserFactory.getChooser(3);
        if (chooser.showSaveDialog(this.getFrame()) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                data.saveTextAreaToFile(f, jtaLog);
                addToLog("Запись сообщений в файл " + f.getAbsolutePath());
            } catch (IOException ex) {
                showErrorMessage(ex);
            }
        }
    }

    /**
     * Text message.
     * @param s message
     */
    private void addToLog(final String s) {
        jtaLog.append(s + "\n");
    }

    /**
     * Existing data.
     */
    private boolean existData = false;

    /**
     * Existing data.
     * @return existing data
     */
    public boolean isExistData() {
        return existData;
    }

    /**
     * Existing data.
     * @param b existing data
     */
    public void setExistData(final boolean b) {
        boolean old = isExistData();
        existData = b;
        firePropertyChange("existData", old, isExistData());
    }

    /**
     * Select row in site table.
     */
    private boolean selectSite = false;

    /**
     * Select row in site table.
     * @return select row in site table
     */
    public boolean isSelectSite() {
        return selectSite;
    }

    /**
     * Select row in site table.
     * @param b select row in site table
     */
    public void setSelectSite(final boolean b) {
        boolean old = isSelectSite();
        selectSite = b;
        firePropertyChange("selectSite", old, isSelectSite());
    }

    /**
     * Select row in reper table.
     */
    private boolean selectReper = false;

    /**
     * Select row in reper table.
     * @return select row in reper table
     */
    public boolean isSelectReper() {
        return selectReper;
    }

    /**
     * Select row in reper table.
     * @param b select row in reper table
     */
    public void setSelectReper(final boolean b) {
        boolean old = isSelectReper();
        selectReper = b;
        firePropertyChange("selectReper", old, isSelectReper());
    }
    
    /**
     * Message of number parse exeptions.
     * @param n count of exeptions
     */
    private void showParseExceptionMessage(final int n) {
        JOptionPane.showMessageDialog(this.getFrame(),
                "Количество ошибок при распознавании чисел - " + n,
                "Внимание", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Message of maximum site count.
     */
    private void showSiteCountMessage() {
        JOptionPane.showMessageDialog(this.getFrame(),
                "Максимальное количество одновременно открытых участков - " + data.getSiteCountMax() +
                ".\nДля добавления нового участка необходимо удалить один из имеющихся.",
                "Внимание", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Message of exeption.
     * @param ex exeption
     */
    public void showErrorMessage(final Exception ex) {
        JOptionPane.showMessageDialog(
                PTEModellerApp.getApplication().getMainFrame(), ex,
                "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Chart save.
     */
    @Action
    public void acChartSaveAs() {
        JFileChooser ch = FileChooserFactory.getChooser(5);
        if (ch.showSaveDialog(this.getFrame()) == JFileChooser.APPROVE_OPTION) {
            File f = ch.getSelectedFile();
            try {
                if (chartPanel.getChart().getPlot() instanceof CombinedDomainXYPlot) {
                    ChartUtilities.saveChartAsPNG(f, chartPanel.getChart(), 1200, 620);
                } else {
                    ChartUtilities.saveChartAsPNG(f, chartPanel.getChart(), 1200, 320);
                }
            } catch (IOException ex) {
                showErrorMessage(ex);
            }
        }
    }

    /**
     * Chart parameters.
     */
    @Action
    public void acChartParameter() {
        DlgParameterFewChartEdit d = new DlgParameterFewChartEdit();
        d.setTempObj(chartPanel.getChart());
        d.setLocationRelativeTo(this.getFrame());
        d.setVisible(true);
    }

    /**
     * Refresh all table.
     */
    private void refreshAllTable() {
        tmDataSite.fireTableDataChanged();
        tmReper.fireTableDataChanged();
    }
    
    /**
     * Chart repaint.
     */
    @Action(enabledProperty = "existData")
    public void acPlot() {
        chartPanel.setChart(ChartMaker.createChart(data));
    }
    
    /**
     * Check status of show chart buttons.
     * @return true, if chart is selected
     */
    private boolean isShowChartSelected() {
        boolean b = false;
        if (jtbtnChart1.isSelected()) {
            b = true;
        }
        if (jtbtnChart2.isSelected()) {
            b = true;
        }
        if (jtbtnChart3.isSelected()) {
            b = true;
        }
        if (jtbtnChart4.isSelected()) {
            b = true;
        }
        if (jtbtnChart5.isSelected()) {
            b = true;
        }
        if (jtbtnChart6.isSelected()) {
            b = true;
        }
        return b;
    }

    /**
     * Show charts.
     */
    @Action(enabledProperty = "existData")
    public void acShowChart() {
        if (!isShowChartSelected()) {
            jtbtnChart1.setSelected(data.getParameter().isShowChartZ());
            jtbtnChart2.setSelected(data.getParameter().isShowChartY());
            jtbtnChart3.setSelected(data.getParameter().isShowChartE());
            jtbtnChart4.setSelected(data.getParameter().isShowChartI());
            jtbtnChart5.setSelected(data.getParameter().isShowChartU());
            jtbtnChart6.setSelected(data.getParameter().isShowChartD());
        } else {
            data.getParameter().setShowChartZ(jtbtnChart1.isSelected());
            data.getParameter().setShowChartY(jtbtnChart2.isSelected());
            data.getParameter().setShowChartE(jtbtnChart3.isSelected());
            data.getParameter().setShowChartI(jtbtnChart4.isSelected());
            data.getParameter().setShowChartU(jtbtnChart5.isSelected());
            data.getParameter().setShowChartD(jtbtnChart6.isSelected());
            acPlot();
        }
    }

    /**
     * Show legend.
     */
    @Action(enabledProperty = "existData")
    public void acShowLegend() {
        data.getParameter().setShowLegend(jtbtnShowLegend.isSelected());
        acPlot();
    }

    /**
     * Chart of pipeline resistance per unit length.
     */
    @Action(enabledProperty = "existData")
    public void acChartZ() {
        chartMaker.showChartZ();
    }

    /**
     * Chart of insulation conductivity per unit length.
     */
    @Action(enabledProperty = "existData")
    public void acChartY() {
        chartMaker.showChartY();
    }

    /**
     * Chart of external electric field strength.
     */
    @Action(enabledProperty = "existData")
    public void acChartE() {
        chartMaker.showChartE();
    }

     /**
     * Chart of pipeline current.
     */
    @Action(enabledProperty = "existData")
    public void acChartI() {
        chartMaker.showChartI();
    }

    /**
     * Chart of pipeline potential.
     */
    @Action(enabledProperty = "existData")
    public void acChartU() {
        chartMaker.showChartU();
    }

    /**
     * Chart of insulation current per unit length.
     */
    @Action(enabledProperty = "existData")
    public void acChartD() {
        chartMaker.showChartD();
    }

    /**
     * Open site from file.
     */
    @Action
    public void acOpenSite() {
        if (data.getSiteCount() == data.getSiteCountMax()) {
            showSiteCountMessage();
            return;
        }
        JFileChooser chooser = FileChooserFactory.getChooser(4);
        if (chooser.showOpenDialog(this.getFrame()) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            Site sc = new Site();
            try {
                sc.loadDataFromFile(f);
                data.getSite().add(sc);
                int i = data.getSite().size() - 1;
                tmSite.fireTableRowsInserted(i, i);
                int j = jtSite.convertRowIndexToView(i);
                jtSite.setRowSelectionInterval(j, j);
                jtSite.scrollRectToVisible(jtSite.getCellRect(j, 0, true));
                addToLog("Открыт файл с данными " + f.getAbsolutePath());
            } catch (FileNotFoundException ex) {
                showErrorMessage(ex);
            } catch (IOException ex) {
                showErrorMessage(ex);
            }
            if (sc.getParseExceptionCount() > 0) {
                showParseExceptionMessage(sc.getParseExceptionCount());
                return;
            }
            refreshAllTable();
            chartPanel.setChart(ChartMaker.createChart(data));
            setExistData(true);
        }
    }

    /**
     * Save site to file.
     */
    @Action(enabledProperty = "selectSite")
    public void acSaveSite() {
        JFileChooser chooser = FileChooserFactory.getChooser(4);
        if (chooser.showSaveDialog(this.getFrame()) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                int i = jtSite.convertRowIndexToModel(jtSite.getSelectedRow());
                data.getSite().get(i).saveDataToFile(f);
                addToLog("Данные сохранены в файл " + f.getAbsolutePath());
            } catch (IOException ex) {
                showErrorMessage(ex);
            }
        }
    }

    /**
     * Add site.
     */
    @Action
    public void acAddSite() {
        if (data.getSiteCount() == data.getSiteCountMax()) {
            showSiteCountMessage();
            return;
        }
        DlgSiteEdit d = new DlgSiteEdit();
        d.setTempObj(d.createTempObj());
        d.setLocationRelativeTo(this.getFrame());
        d.setVisible(true);
        if (d.isChangeObj()) {
            data.getSite().add(d.getTempObj());
            int i = data.getSite().size() - 1;
            tmSite.fireTableRowsInserted(i, i);
            int j = jtSite.convertRowIndexToView(i);
            jtSite.setRowSelectionInterval(j, j);
            jtSite.scrollRectToVisible(jtSite.getCellRect(j, 0, true));
            setExistData(true);
            acPlot();
        }
    }

    /**
     * Edit site.
     */
    @Action(enabledProperty = "selectSite")
    public void acEditSite() {
        int i = jtSite.convertRowIndexToModel(jtSite.getSelectedRow());
        DlgSiteEdit d = new DlgSiteEdit();
        d.setTempObj(data.getSite().get(i));
        d.setLocationRelativeTo(this.getFrame());
        d.setVisible(true);
        if (d.isChangeObj()) {
            tmSite.fireTableRowsUpdated(i, i);
            jtSite.getRowSorter().allRowsChanged();
            acPlot();
        }
    }

    /**
     * Delete site.
     */
    @Action(enabledProperty = "selectSite")
    public void acDelSite() {
        int i = jtSite.convertRowIndexToModel(jtSite.getSelectedRow());
        data.getSite().remove(i);
        tmSite.fireTableRowsDeleted(i, i);
        setSelectSite(false);
        if (data.getSite().isEmpty()) {
            setExistData(false);
            jtbtnShowLegend.setSelected(false);
            data.getParameter().setShowLegend(false);
        }
        acPlot();
    }
    
    /**
     * Calculate.
     */
    @Action(enabledProperty = "selectSite")
    public void acCalculate() {
        int i = jtSite.convertRowIndexToModel(jtSite.getSelectedRow());
        data.getSite().get(i).calculate();
        tmDataSite.fireTableDataChanged();
        acPlot();
    }
    
    /**
     * Add reper.
     */
    @Action(enabledProperty = "selectSite")
    public void acAddReper() {
        DlgReperEdit d = new DlgReperEdit();
        d.setTempObj(d.createTempObj());
        d.setLocationRelativeTo(this.getFrame());
        d.setVisible(true);
        if (d.isChangeObj()) {
            data.getSite().get(data.getSiteIndex()).getReper().add(d.getTempObj());
            int i = data.getSite().get(data.getSiteIndex()).getReper().size() - 1;
            tmReper.fireTableRowsInserted(i, i);
            int j = jtReper.convertRowIndexToView(i);
            jtReper.setRowSelectionInterval(j, j);
            jtReper.scrollRectToVisible(jtReper.getCellRect(j, 0, true));
        }
    }

    /**
     * Edit reper.
     */
    @Action(enabledProperty = "selectReper")
    public void acEditReper() {
        int i = jtReper.convertRowIndexToModel(jtReper.getSelectedRow());
        DlgReperEdit d = new DlgReperEdit();
        d.setTempObj(data.getSite().get(data.getSiteIndex()).getReper().get(i));
        d.setLocationRelativeTo(this.getFrame());
        d.setVisible(true);
        if (d.isChangeObj()) {
            tmReper.fireTableRowsUpdated(i, i);
            jtReper.getRowSorter().allRowsChanged();
        }
    }

    /**
     * Delete reper.
     */
    @Action(enabledProperty = "selectReper")
    public void acDelReper() {
        int i = jtReper.convertRowIndexToModel(jtReper.getSelectedRow());
        data.getSite().get(data.getSiteIndex()).getReper().remove(i);
        tmReper.fireTableRowsDeleted(i, i);
        setSelectReper(false);
    }

    /**
     * Show repers.
     */
    @Action(enabledProperty = "selectSite")
    public void acShowReper() {
        data.getCurrentSite().setShowReper(jtbtnShowReper.isSelected());
    }

    /**
     * Action for About button.
     */
    @Action
    public void acShowAboutBox() {
        if (aboutBox == null) {
            aboutBox = new PTEModellerAboutBox();
        }
        aboutBox.setLocationRelativeTo(this.getFrame());
        aboutBox.setVisible(true);
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

        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jToolBar4 = new javax.swing.JToolBar();
        jbtnCalculate = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtDataSite = new org.jdesktop.swingx.JXTable();
        jPanel8 = new javax.swing.JPanel();
        jToolBar5 = new javax.swing.JToolBar();
        jbtnAddReper = new javax.swing.JButton();
        jbtnEditReper = new javax.swing.JButton();
        jbtnDelReper = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        jtbtnShowReper = new javax.swing.JToggleButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jtReper = new org.jdesktop.swingx.JXTable();
        jPanel6 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        jbtnOpenSite = new javax.swing.JButton();
        jbtnSaveSite = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        jbtnAddSite = new javax.swing.JButton();
        jbtnEditSite = new javax.swing.JButton();
        jbtnDelSite = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtSite = new org.jdesktop.swingx.JXTable();
        jPanel5 = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        jbtnSaveLog = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtaLog = new javax.swing.JTextArea();
        jToolBar1 = new javax.swing.JToolBar();
        jbtnRefresh = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jtbtnChart1 = new javax.swing.JToggleButton();
        jtbtnChart2 = new javax.swing.JToggleButton();
        jtbtnChart3 = new javax.swing.JToggleButton();
        jtbtnChart4 = new javax.swing.JToggleButton();
        jtbtnChart5 = new javax.swing.JToggleButton();
        jtbtnChart6 = new javax.swing.JToggleButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jtbtnShowLegend = new javax.swing.JToggleButton();
        jPanel3 = new javax.swing.JPanel();
        jpChart = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu jmFile = new javax.swing.JMenu();
        jmiOpenSite = new javax.swing.JMenuItem();
        jmiSaveSite = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jmiAddSite = new javax.swing.JMenuItem();
        jmiEditSite = new javax.swing.JMenuItem();
        jmiDelSite = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        jmiCalculate = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        jmiExit = new javax.swing.JMenuItem();
        jmChart = new javax.swing.JMenu();
        jmiChartZ = new javax.swing.JMenuItem();
        jmiChartY = new javax.swing.JMenuItem();
        jmiChartE = new javax.swing.JMenuItem();
        jmiChartI = new javax.swing.JMenuItem();
        jmiChartU = new javax.swing.JMenuItem();
        jmiChartD = new javax.swing.JMenuItem();
        javax.swing.JMenu jmHelp = new javax.swing.JMenu();
        javax.swing.JMenuItem jmiAbout = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        statusMessageLabel = new javax.swing.JLabel();
        jpmChart = new javax.swing.JPopupMenu();
        jmiChartSave = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        jmiChartParameters = new javax.swing.JMenuItem();

        mainPanel.setName("mainPanel"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(350);
        jSplitPane1.setDividerSize(3);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(350, 210));
        jPanel2.setRequestFocusEnabled(false);

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(290, 0));
        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N

        jSplitPane2.setBorder(null);
        jSplitPane2.setDividerLocation(250);
        jSplitPane2.setDividerSize(3);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setLastDividerLocation(200);
        jSplitPane2.setMinimumSize(new java.awt.Dimension(200, 226));
        jSplitPane2.setName("jSplitPane2"); // NOI18N

        jTabbedPane2.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane2.setMinimumSize(new java.awt.Dimension(0, 180));
        jTabbedPane2.setName("jTabbedPane2"); // NOI18N

        jPanel7.setName("jPanel7"); // NOI18N

        jToolBar4.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 0));
        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);
        jToolBar4.setMaximumSize(new java.awt.Dimension(314, 36));
        jToolBar4.setMinimumSize(new java.awt.Dimension(228, 36));
        jToolBar4.setName("jToolBar4"); // NOI18N
        jToolBar4.setPreferredSize(new java.awt.Dimension(100, 36));

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ru.gss.ptemodeller.PTEModellerApp.class).getContext().getActionMap(PTEModellerView.class, this);
        jbtnCalculate.setAction(actionMap.get("acCalculate")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.gss.ptemodeller.PTEModellerApp.class).getContext().getResourceMap(PTEModellerView.class);
        jbtnCalculate.setIcon(resourceMap.getIcon("jbtnCalculate.icon")); // NOI18N
        jbtnCalculate.setDisabledIcon(resourceMap.getIcon("jbtnCalculate.disabledIcon")); // NOI18N
        jbtnCalculate.setFocusable(false);
        jbtnCalculate.setHideActionText(true);
        jbtnCalculate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtnCalculate.setName("jbtnCalculate"); // NOI18N
        jbtnCalculate.setRolloverIcon(resourceMap.getIcon("jbtnCalculate.rolloverIcon")); // NOI18N
        jbtnCalculate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar4.add(jbtnCalculate);

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jtDataSite.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jtDataSite.setColumnControlVisible(true);
        jtDataSite.setName("jtDataSite"); // NOI18N
        jtDataSite.setSortable(false);
        jScrollPane3.setViewportView(jtDataSite);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar4, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab(resourceMap.getString("jPanel7.TabConstraints.tabTitle"), jPanel7); // NOI18N

        jPanel8.setName("jPanel8"); // NOI18N

        jToolBar5.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 0));
        jToolBar5.setFloatable(false);
        jToolBar5.setRollover(true);
        jToolBar5.setMaximumSize(new java.awt.Dimension(314, 36));
        jToolBar5.setMinimumSize(new java.awt.Dimension(228, 36));
        jToolBar5.setName("jToolBar5"); // NOI18N
        jToolBar5.setPreferredSize(new java.awt.Dimension(100, 36));

        jbtnAddReper.setAction(actionMap.get("acAddReper")); // NOI18N
        jbtnAddReper.setIcon(resourceMap.getIcon("jbtnAddReper.icon")); // NOI18N
        jbtnAddReper.setDisabledIcon(resourceMap.getIcon("jbtnAddReper.disabledIcon")); // NOI18N
        jbtnAddReper.setFocusable(false);
        jbtnAddReper.setHideActionText(true);
        jbtnAddReper.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtnAddReper.setName("jbtnAddReper"); // NOI18N
        jbtnAddReper.setRolloverIcon(resourceMap.getIcon("jbtnAddReper.rolloverIcon")); // NOI18N
        jbtnAddReper.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar5.add(jbtnAddReper);

        jbtnEditReper.setAction(actionMap.get("acEditReper")); // NOI18N
        jbtnEditReper.setIcon(resourceMap.getIcon("jbtnEditReper.icon")); // NOI18N
        jbtnEditReper.setDisabledIcon(resourceMap.getIcon("jbtnEditReper.disabledIcon")); // NOI18N
        jbtnEditReper.setFocusable(false);
        jbtnEditReper.setHideActionText(true);
        jbtnEditReper.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtnEditReper.setName("jbtnEditReper"); // NOI18N
        jbtnEditReper.setRolloverIcon(resourceMap.getIcon("jbtnEditReper.rolloverIcon")); // NOI18N
        jbtnEditReper.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar5.add(jbtnEditReper);

        jbtnDelReper.setAction(actionMap.get("acDelReper")); // NOI18N
        jbtnDelReper.setIcon(resourceMap.getIcon("jbtnDelReper.icon")); // NOI18N
        jbtnDelReper.setDisabledIcon(resourceMap.getIcon("jbtnDelReper.disabledIcon")); // NOI18N
        jbtnDelReper.setFocusable(false);
        jbtnDelReper.setHideActionText(true);
        jbtnDelReper.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtnDelReper.setName("jbtnDelReper"); // NOI18N
        jbtnDelReper.setRolloverIcon(resourceMap.getIcon("jbtnDelReper.rolloverIcon")); // NOI18N
        jbtnDelReper.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar5.add(jbtnDelReper);

        jSeparator8.setName("jSeparator8"); // NOI18N
        jToolBar5.add(jSeparator8);

        jtbtnShowReper.setAction(actionMap.get("acShowReper")); // NOI18N
        jtbtnShowReper.setIcon(resourceMap.getIcon("jtbtnShowReper.icon")); // NOI18N
        jtbtnShowReper.setDisabledIcon(resourceMap.getIcon("jtbtnShowReper.disabledIcon")); // NOI18N
        jtbtnShowReper.setFocusable(false);
        jtbtnShowReper.setHideActionText(true);
        jtbtnShowReper.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbtnShowReper.setName("jtbtnShowReper"); // NOI18N
        jtbtnShowReper.setRolloverIcon(resourceMap.getIcon("jtbtnShowReper.rolloverIcon")); // NOI18N
        jtbtnShowReper.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar5.add(jtbtnShowReper);

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        jtReper.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jtReper.setColumnControlVisible(true);
        jtReper.setName("jtReper"); // NOI18N
        jtReper.setSortable(false);
        jScrollPane4.setViewportView(jtReper);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar5, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jToolBar5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab(resourceMap.getString("jPanel8.TabConstraints.tabTitle"), jPanel8); // NOI18N

        jSplitPane2.setRightComponent(jTabbedPane2);

        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jPanel6.setMinimumSize(new java.awt.Dimension(0, 160));
        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setPreferredSize(new java.awt.Dimension(250, 128));

        jToolBar2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 0));
        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setMaximumSize(new java.awt.Dimension(314, 36));
        jToolBar2.setMinimumSize(new java.awt.Dimension(228, 36));
        jToolBar2.setName("jToolBar2"); // NOI18N
        jToolBar2.setPreferredSize(new java.awt.Dimension(100, 36));

        jbtnOpenSite.setAction(actionMap.get("acOpenSite")); // NOI18N
        jbtnOpenSite.setIcon(resourceMap.getIcon("jbtnOpenSite.icon")); // NOI18N
        jbtnOpenSite.setDisabledIcon(resourceMap.getIcon("jbtnOpenSite.disabledIcon")); // NOI18N
        jbtnOpenSite.setFocusable(false);
        jbtnOpenSite.setHideActionText(true);
        jbtnOpenSite.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtnOpenSite.setName("jbtnOpenSite"); // NOI18N
        jbtnOpenSite.setRolloverIcon(resourceMap.getIcon("jbtnOpenSite.rolloverIcon")); // NOI18N
        jbtnOpenSite.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(jbtnOpenSite);

        jbtnSaveSite.setAction(actionMap.get("acSaveSite")); // NOI18N
        jbtnSaveSite.setIcon(resourceMap.getIcon("jbtnSaveSite.icon")); // NOI18N
        jbtnSaveSite.setDisabledIcon(resourceMap.getIcon("jbtnSaveSite.disabledIcon")); // NOI18N
        jbtnSaveSite.setFocusable(false);
        jbtnSaveSite.setHideActionText(true);
        jbtnSaveSite.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtnSaveSite.setName("jbtnSaveSite"); // NOI18N
        jbtnSaveSite.setRolloverIcon(resourceMap.getIcon("jbtnSaveSite.rolloverIcon")); // NOI18N
        jbtnSaveSite.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(jbtnSaveSite);

        jSeparator7.setName("jSeparator7"); // NOI18N
        jToolBar2.add(jSeparator7);

        jbtnAddSite.setAction(actionMap.get("acAddSite")); // NOI18N
        jbtnAddSite.setIcon(resourceMap.getIcon("jbtnAddSite.icon")); // NOI18N
        jbtnAddSite.setDisabledIcon(resourceMap.getIcon("jbtnAddSite.disabledIcon")); // NOI18N
        jbtnAddSite.setFocusable(false);
        jbtnAddSite.setHideActionText(true);
        jbtnAddSite.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtnAddSite.setName("jbtnAddSite"); // NOI18N
        jbtnAddSite.setRolloverIcon(resourceMap.getIcon("jbtnAddSite.rolloverIcon")); // NOI18N
        jbtnAddSite.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(jbtnAddSite);

        jbtnEditSite.setAction(actionMap.get("acEditSite")); // NOI18N
        jbtnEditSite.setIcon(resourceMap.getIcon("jbtnEditSite.icon")); // NOI18N
        jbtnEditSite.setDisabledIcon(resourceMap.getIcon("jbtnEditSite.disabledIcon")); // NOI18N
        jbtnEditSite.setFocusable(false);
        jbtnEditSite.setHideActionText(true);
        jbtnEditSite.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtnEditSite.setName("jbtnEditSite"); // NOI18N
        jbtnEditSite.setRolloverIcon(resourceMap.getIcon("jbtnEditSite.rolloverIcon")); // NOI18N
        jbtnEditSite.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(jbtnEditSite);

        jbtnDelSite.setAction(actionMap.get("acDelSite")); // NOI18N
        jbtnDelSite.setIcon(resourceMap.getIcon("jbtnDelSite.icon")); // NOI18N
        jbtnDelSite.setDisabledIcon(resourceMap.getIcon("jbtnDelSite.disabledIcon")); // NOI18N
        jbtnDelSite.setFocusable(false);
        jbtnDelSite.setHideActionText(true);
        jbtnDelSite.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtnDelSite.setName("jbtnDelSite"); // NOI18N
        jbtnDelSite.setRolloverIcon(resourceMap.getIcon("jbtnDelSite.rolloverIcon")); // NOI18N
        jbtnDelSite.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(jbtnDelSite);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jtSite.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jtSite.setColumnControlVisible(true);
        jtSite.setName("jtSite"); // NOI18N
        jtSite.setSortable(false);
        jScrollPane2.setViewportView(jtSite);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE))
        );

        jSplitPane2.setLeftComponent(jPanel6);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jPanel5.setName("jPanel5"); // NOI18N

        jToolBar3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 0));
        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.setMaximumSize(new java.awt.Dimension(314, 36));
        jToolBar3.setMinimumSize(new java.awt.Dimension(228, 36));
        jToolBar3.setName("jToolBar3"); // NOI18N
        jToolBar3.setPreferredSize(new java.awt.Dimension(100, 36));

        jbtnSaveLog.setAction(actionMap.get("acSaveLogToFile")); // NOI18N
        jbtnSaveLog.setIcon(resourceMap.getIcon("jbtnSaveLog.icon")); // NOI18N
        jbtnSaveLog.setDisabledIcon(resourceMap.getIcon("jbtnSaveLog.disabledIcon")); // NOI18N
        jbtnSaveLog.setFocusable(false);
        jbtnSaveLog.setHideActionText(true);
        jbtnSaveLog.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtnSaveLog.setName("jbtnSaveLog"); // NOI18N
        jbtnSaveLog.setRolloverIcon(resourceMap.getIcon("jbtnSaveLog.rolloverIcon")); // NOI18N
        jbtnSaveLog.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar3.add(jbtnSaveLog);

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jtaLog.setColumns(20);
        jtaLog.setEditable(false);
        jtaLog.setFont(resourceMap.getFont("jtaLog.font")); // NOI18N
        jtaLog.setRows(5);
        jtaLog.setWrapStyleWord(true);
        jtaLog.setName("jtaLog"); // NOI18N
        jScrollPane1.setViewportView(jtaLog);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

        jToolBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 10));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setMaximumSize(new java.awt.Dimension(314, 41));
        jToolBar1.setMinimumSize(new java.awt.Dimension(228, 41));
        jToolBar1.setName("jToolBar1"); // NOI18N
        jToolBar1.setPreferredSize(new java.awt.Dimension(100, 41));

        jbtnRefresh.setAction(actionMap.get("acPlot")); // NOI18N
        jbtnRefresh.setIcon(resourceMap.getIcon("jbtnRefresh.icon")); // NOI18N
        jbtnRefresh.setDisabledIcon(resourceMap.getIcon("jbtnRefresh.disabledIcon")); // NOI18N
        jbtnRefresh.setFocusable(false);
        jbtnRefresh.setHideActionText(true);
        jbtnRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtnRefresh.setName("jbtnRefresh"); // NOI18N
        jbtnRefresh.setRolloverIcon(resourceMap.getIcon("jbtnRefresh.rolloverIcon")); // NOI18N
        jbtnRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jbtnRefresh);

        jSeparator5.setName("jSeparator5"); // NOI18N
        jToolBar1.add(jSeparator5);

        jtbtnChart1.setAction(actionMap.get("acShowChart")); // NOI18N
        jtbtnChart1.setIcon(resourceMap.getIcon("jtbtnChart1.icon")); // NOI18N
        jtbtnChart1.setToolTipText(resourceMap.getString("jtbtnChart1.toolTipText")); // NOI18N
        jtbtnChart1.setDisabledIcon(resourceMap.getIcon("jtbtnChart1.disabledIcon")); // NOI18N
        jtbtnChart1.setFocusable(false);
        jtbtnChart1.setHideActionText(true);
        jtbtnChart1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbtnChart1.setName("jtbtnChart1"); // NOI18N
        jtbtnChart1.setRolloverIcon(resourceMap.getIcon("jtbtnChart1.rolloverIcon")); // NOI18N
        jtbtnChart1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jtbtnChart1);

        jtbtnChart2.setAction(actionMap.get("acShowChart")); // NOI18N
        jtbtnChart2.setIcon(resourceMap.getIcon("jtbtnChart2.icon")); // NOI18N
        jtbtnChart2.setToolTipText(resourceMap.getString("jtbtnChart2.toolTipText")); // NOI18N
        jtbtnChart2.setDisabledIcon(resourceMap.getIcon("jtbtnChart2.disabledIcon")); // NOI18N
        jtbtnChart2.setFocusable(false);
        jtbtnChart2.setHideActionText(true);
        jtbtnChart2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbtnChart2.setName("jtbtnChart2"); // NOI18N
        jtbtnChart2.setRolloverIcon(resourceMap.getIcon("jtbtnChart2.rolloverIcon")); // NOI18N
        jtbtnChart2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jtbtnChart2);

        jtbtnChart3.setAction(actionMap.get("acShowChart")); // NOI18N
        jtbtnChart3.setIcon(resourceMap.getIcon("jtbtnChart3.icon")); // NOI18N
        jtbtnChart3.setToolTipText(resourceMap.getString("jtbtnChart3.toolTipText")); // NOI18N
        jtbtnChart3.setDisabledIcon(resourceMap.getIcon("jtbtnChart3.disabledIcon")); // NOI18N
        jtbtnChart3.setFocusable(false);
        jtbtnChart3.setHideActionText(true);
        jtbtnChart3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbtnChart3.setName("jtbtnChart3"); // NOI18N
        jtbtnChart3.setRolloverIcon(resourceMap.getIcon("jtbtnChart3.rolloverIcon")); // NOI18N
        jtbtnChart3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jtbtnChart3);

        jtbtnChart4.setAction(actionMap.get("acShowChart")); // NOI18N
        jtbtnChart4.setIcon(resourceMap.getIcon("jtbtnChart4.icon")); // NOI18N
        jtbtnChart4.setToolTipText(resourceMap.getString("jtbtnChart4.toolTipText")); // NOI18N
        jtbtnChart4.setDisabledIcon(resourceMap.getIcon("jtbtnChart4.disabledIcon")); // NOI18N
        jtbtnChart4.setFocusable(false);
        jtbtnChart4.setHideActionText(true);
        jtbtnChart4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbtnChart4.setName("jtbtnChart4"); // NOI18N
        jtbtnChart4.setRolloverIcon(resourceMap.getIcon("jtbtnChart4.rolloverIcon")); // NOI18N
        jtbtnChart4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jtbtnChart4);

        jtbtnChart5.setAction(actionMap.get("acShowChart")); // NOI18N
        jtbtnChart5.setIcon(resourceMap.getIcon("jtbtnChart5.icon")); // NOI18N
        jtbtnChart5.setToolTipText(resourceMap.getString("jtbtnChart5.toolTipText")); // NOI18N
        jtbtnChart5.setDisabledIcon(resourceMap.getIcon("jtbtnChart5.disabledIcon")); // NOI18N
        jtbtnChart5.setFocusable(false);
        jtbtnChart5.setHideActionText(true);
        jtbtnChart5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbtnChart5.setName("jtbtnChart5"); // NOI18N
        jtbtnChart5.setRolloverIcon(resourceMap.getIcon("jtbtnChart5.rolloverIcon")); // NOI18N
        jtbtnChart5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jtbtnChart5);

        jtbtnChart6.setAction(actionMap.get("acShowChart")); // NOI18N
        jtbtnChart6.setIcon(resourceMap.getIcon("jtbtnChart6.icon")); // NOI18N
        jtbtnChart6.setToolTipText(resourceMap.getString("jtbtnChart6.toolTipText")); // NOI18N
        jtbtnChart6.setDisabledIcon(resourceMap.getIcon("jtbtnChart6.disabledIcon")); // NOI18N
        jtbtnChart6.setFocusable(false);
        jtbtnChart6.setHideActionText(true);
        jtbtnChart6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbtnChart6.setName("jtbtnChart6"); // NOI18N
        jtbtnChart6.setRolloverIcon(resourceMap.getIcon("jtbtnChart6.rolloverIcon")); // NOI18N
        jtbtnChart6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jtbtnChart6);

        jSeparator6.setName("jSeparator6"); // NOI18N
        jToolBar1.add(jSeparator6);

        jtbtnShowLegend.setAction(actionMap.get("acShowLegend")); // NOI18N
        jtbtnShowLegend.setIcon(resourceMap.getIcon("jtbtnShowLegend.icon")); // NOI18N
        jtbtnShowLegend.setDisabledIcon(resourceMap.getIcon("jtbtnShowLegend.disabledIcon")); // NOI18N
        jtbtnShowLegend.setFocusable(false);
        jtbtnShowLegend.setHideActionText(true);
        jtbtnShowLegend.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbtnShowLegend.setName("jtbtnShowLegend"); // NOI18N
        jtbtnShowLegend.setRolloverIcon(resourceMap.getIcon("jtbtnShowLegend.rolloverIcon")); // NOI18N
        jtbtnShowLegend.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jtbtnShowLegend);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(jPanel2);

        jPanel3.setMinimumSize(new java.awt.Dimension(400, 290));
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(762, 290));

        jpChart.setName("jpChart"); // NOI18N
        jpChart.setLayout(new javax.swing.BoxLayout(jpChart, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpChart, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpChart, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(jPanel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 903, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        menuBar.setName("menuBar"); // NOI18N

        jmFile.setText(resourceMap.getString("jmFile.text")); // NOI18N
        jmFile.setName("jmFile"); // NOI18N

        jmiOpenSite.setAction(actionMap.get("acOpenSite")); // NOI18N
        jmiOpenSite.setName("jmiOpenSite"); // NOI18N
        jmFile.add(jmiOpenSite);

        jmiSaveSite.setAction(actionMap.get("acSaveSite")); // NOI18N
        jmiSaveSite.setName("jmiSaveSite"); // NOI18N
        jmFile.add(jmiSaveSite);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jmFile.add(jSeparator1);

        jmiAddSite.setAction(actionMap.get("acAddSite")); // NOI18N
        jmiAddSite.setText(resourceMap.getString("jmiAddSite.text")); // NOI18N
        jmiAddSite.setToolTipText(resourceMap.getString("jmiAddSite.toolTipText")); // NOI18N
        jmiAddSite.setName("jmiAddSite"); // NOI18N
        jmFile.add(jmiAddSite);

        jmiEditSite.setAction(actionMap.get("acEditSite")); // NOI18N
        jmiEditSite.setText(resourceMap.getString("jmiEditSite.text")); // NOI18N
        jmiEditSite.setToolTipText(resourceMap.getString("jmiEditSite.toolTipText")); // NOI18N
        jmiEditSite.setName("jmiEditSite"); // NOI18N
        jmFile.add(jmiEditSite);

        jmiDelSite.setAction(actionMap.get("acDelSite")); // NOI18N
        jmiDelSite.setText(resourceMap.getString("jmiDelSite.text")); // NOI18N
        jmiDelSite.setToolTipText(resourceMap.getString("jmiDelSite.toolTipText")); // NOI18N
        jmiDelSite.setName("jmiDelSite"); // NOI18N
        jmFile.add(jmiDelSite);

        jSeparator2.setName("jSeparator2"); // NOI18N
        jmFile.add(jSeparator2);

        jmiCalculate.setAction(actionMap.get("acCalculate")); // NOI18N
        jmiCalculate.setText(resourceMap.getString("jmiCalculate.text")); // NOI18N
        jmiCalculate.setToolTipText(resourceMap.getString("jmiCalculate.toolTipText")); // NOI18N
        jmiCalculate.setName("jmiCalculate"); // NOI18N
        jmFile.add(jmiCalculate);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jmFile.add(jSeparator3);

        jmiExit.setAction(actionMap.get("quit")); // NOI18N
        jmiExit.setName("jmiExit"); // NOI18N
        jmFile.add(jmiExit);

        menuBar.add(jmFile);

        jmChart.setText(resourceMap.getString("jmChart.text")); // NOI18N
        jmChart.setName("jmChart"); // NOI18N

        jmiChartZ.setAction(actionMap.get("acChartZ")); // NOI18N
        jmiChartZ.setName("jmiChartZ"); // NOI18N
        jmChart.add(jmiChartZ);

        jmiChartY.setAction(actionMap.get("acChartY")); // NOI18N
        jmiChartY.setName("jmiChartY"); // NOI18N
        jmChart.add(jmiChartY);

        jmiChartE.setAction(actionMap.get("acChartE")); // NOI18N
        jmiChartE.setName("jmiChartE"); // NOI18N
        jmChart.add(jmiChartE);

        jmiChartI.setAction(actionMap.get("acChartI")); // NOI18N
        jmiChartI.setName("jmiChartI"); // NOI18N
        jmChart.add(jmiChartI);

        jmiChartU.setAction(actionMap.get("acChartU")); // NOI18N
        jmiChartU.setText(resourceMap.getString("jmiChartU.text")); // NOI18N
        jmiChartU.setToolTipText(resourceMap.getString("jmiChartU.toolTipText")); // NOI18N
        jmiChartU.setName("jmiChartU"); // NOI18N
        jmChart.add(jmiChartU);

        jmiChartD.setAction(actionMap.get("acChartD")); // NOI18N
        jmiChartD.setText(resourceMap.getString("jmiChartD.text")); // NOI18N
        jmiChartD.setToolTipText(resourceMap.getString("jmiChartD.toolTipText")); // NOI18N
        jmiChartD.setName("jmiChartD"); // NOI18N
        jmChart.add(jmiChartD);

        menuBar.add(jmChart);

        jmHelp.setAction(actionMap.get("showAboutBox")); // NOI18N
        jmHelp.setText(resourceMap.getString("jmHelp.text")); // NOI18N
        jmHelp.setName("jmHelp"); // NOI18N

        jmiAbout.setAction(actionMap.get("acShowAboutBox")); // NOI18N
        jmiAbout.setName("jmiAbout"); // NOI18N
        jmHelp.add(jmiAbout);

        menuBar.add(jmHelp);

        statusPanel.setMaximumSize(new java.awt.Dimension(32767, 20));
        statusPanel.setName("statusPanel"); // NOI18N

        statusMessageLabel.setMinimumSize(new java.awt.Dimension(20, 20));
        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusMessageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(616, Short.MAX_VALUE))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusMessageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
        );

        jpmChart.setName("jpmChart"); // NOI18N

        jmiChartSave.setAction(actionMap.get("acChartSaveAs")); // NOI18N
        jmiChartSave.setName("jmiChartSave"); // NOI18N
        jpmChart.add(jmiChartSave);

        jSeparator4.setName("jSeparator4"); // NOI18N
        jpmChart.add(jSeparator4);

        jmiChartParameters.setAction(actionMap.get("acChartParameter")); // NOI18N
        jmiChartParameters.setName("jmiChartParameters"); // NOI18N
        jpmChart.add(jmiChartParameters);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar5;
    private javax.swing.JButton jbtnAddReper;
    private javax.swing.JButton jbtnAddSite;
    private javax.swing.JButton jbtnCalculate;
    private javax.swing.JButton jbtnDelReper;
    private javax.swing.JButton jbtnDelSite;
    private javax.swing.JButton jbtnEditReper;
    private javax.swing.JButton jbtnEditSite;
    private javax.swing.JButton jbtnOpenSite;
    private javax.swing.JButton jbtnRefresh;
    private javax.swing.JButton jbtnSaveLog;
    private javax.swing.JButton jbtnSaveSite;
    private javax.swing.JMenu jmChart;
    private javax.swing.JMenuItem jmiAddSite;
    private javax.swing.JMenuItem jmiCalculate;
    private javax.swing.JMenuItem jmiChartD;
    private javax.swing.JMenuItem jmiChartE;
    private javax.swing.JMenuItem jmiChartI;
    private javax.swing.JMenuItem jmiChartParameters;
    private javax.swing.JMenuItem jmiChartSave;
    private javax.swing.JMenuItem jmiChartU;
    private javax.swing.JMenuItem jmiChartY;
    private javax.swing.JMenuItem jmiChartZ;
    private javax.swing.JMenuItem jmiDelSite;
    private javax.swing.JMenuItem jmiEditSite;
    private javax.swing.JMenuItem jmiExit;
    private javax.swing.JMenuItem jmiOpenSite;
    private javax.swing.JMenuItem jmiSaveSite;
    private javax.swing.JPanel jpChart;
    private javax.swing.JPopupMenu jpmChart;
    private org.jdesktop.swingx.JXTable jtDataSite;
    private org.jdesktop.swingx.JXTable jtReper;
    private org.jdesktop.swingx.JXTable jtSite;
    private javax.swing.JTextArea jtaLog;
    private javax.swing.JToggleButton jtbtnChart1;
    private javax.swing.JToggleButton jtbtnChart2;
    private javax.swing.JToggleButton jtbtnChart3;
    private javax.swing.JToggleButton jtbtnChart4;
    private javax.swing.JToggleButton jtbtnChart5;
    private javax.swing.JToggleButton jtbtnChart6;
    private javax.swing.JToggleButton jtbtnShowLegend;
    private javax.swing.JToggleButton jtbtnShowReper;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
    private JDialog aboutBox;
    private DataSiteTableModel tmDataSite;
    private SiteTableModel tmSite;
    private ReperTableModel tmReper;
    private DataList data;
    private ChartMaker chartMaker;
    private ChartPanel chartPanel;
    //CHECKSTYLE:ON
}
