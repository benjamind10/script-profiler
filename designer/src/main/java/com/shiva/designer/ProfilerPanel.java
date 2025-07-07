package com.shiva.designer;

import com.shiva.common.DefaultScriptProfiler;
import com.shiva.common.ScriptExecutionResult;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Swing panel for executing project scripts and viewing profiling history.
 * Contains two tabs: one for ad-hoc execution (with status, summary, args input, and bar chart),
 * and one for viewing history.
 */
public class ProfilerPanel extends JPanel {

    // Execution tab components
    private final JTextField pathField = new JTextField("shared.helloWorld");
    private final JTextField argsField = new JTextField();
    private final JButton runBtn = new JButton("Run");
    private final JLabel statusLabel = new JLabel(" ");
    private final JLabel lastRunLabel = new JLabel("Last: N/A");
    private final JLabel avgLabel = new JLabel("Avg: N/A");
    private final JLabel maxLabel = new JLabel("Max: N/A");
    private final JTextArea outputArea = new JTextArea(6, 30);
    private ChartPanel chartPanel;

    // History tab components
    private final DefaultTableModel historyModel;
    private final JTable historyTable;

    // Profiler backend
    private final DefaultScriptProfiler profiler;

    /**
     * Constructs the profiler panel, initializing tabs and UI components.
     *
     * @param profiler The script profiler instance used for execution and history data
     */
    public ProfilerPanel(DefaultScriptProfiler profiler) {
        this.profiler = profiler;
        setLayout(new BorderLayout());

        // enable text wrapping for output area
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Execute", createExecutionTab());

        historyModel = new DefaultTableModel(new String[]{"Script", "Args", "Duration (ms)", "Timestamp"}, 0);
        historyTable = new JTable(historyModel);
        tabs.addTab("History", createHistoryTab());

        add(tabs, BorderLayout.CENTER);
    }

    /** Builds the execution tab with input, args, status, summary, bar chart, and output. */
    private JPanel createExecutionTab() {
        JPanel panel = new JPanel(new BorderLayout(6,6));

        // Script path row
        JPanel inputRow = new JPanel(new BorderLayout(4,4));
        inputRow.add(new JLabel("Script Path:"), BorderLayout.WEST);
        inputRow.add(pathField, BorderLayout.CENTER);
        inputRow.add(runBtn, BorderLayout.EAST);

        // Arguments row
        JPanel argsRow = new JPanel(new BorderLayout(4,4));
        argsRow.add(new JLabel("Args (comma-separated):"), BorderLayout.WEST);
        argsRow.add(argsField, BorderLayout.CENTER);

        // Status indicator
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Summary grid
        JPanel summaryPanel = new JPanel(new GridLayout(1,3,10,0));
        summaryPanel.add(lastRunLabel);
        summaryPanel.add(avgLabel);
        summaryPanel.add(maxLabel);

        // Chart placeholder
        chartPanel = new ChartPanel(createEmptyDatasetChart());
        chartPanel.setPreferredSize(new Dimension(400, 150));

        // Assemble north section
        JPanel northSection = new JPanel(new BorderLayout(4,4));
        northSection.add(inputRow, BorderLayout.NORTH);
        northSection.add(argsRow, BorderLayout.CENTER);
        northSection.add(statusLabel, BorderLayout.SOUTH);

        // Wrap summary and chart below status
        JPanel topWrapper = new JPanel(new BorderLayout(4,4));
        topWrapper.add(northSection, BorderLayout.NORTH);
        topWrapper.add(summaryPanel, BorderLayout.CENTER);
        topWrapper.add(chartPanel, BorderLayout.SOUTH);

        panel.add(topWrapper, BorderLayout.NORTH);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        runBtn.addActionListener(this::onRun);
        return panel;
    }

    /** Creates an empty bar chart for initialization. */
    private JFreeChart createEmptyDatasetChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart(
                "Last Runs",   // chart title
                "Run #",      // domain axis label
                "Duration (ms)", // range axis label
                dataset,       // data
                PlotOrientation.VERTICAL,
                false,         // include legend
                true,          // tooltips
                false          // URLs
        );
        // ensure bars are colored
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(200, 50, 50));
        return chart;
    }

    /** Updates the bar chart with the last 5 run durations. */
    private void updateChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<ScriptExecutionResult> runs = profiler.getRecentRuns();
        int size = runs.size();
        int start = Math.max(0, size - 5);
        for (int i = start; i < size; i++) {
            ScriptExecutionResult r = runs.get(i);
            String runLabel = "#" + (i - start + 1);
            dataset.addValue(r.elapsedMs(), "Duration", runLabel);
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Last " + Math.min(5, size) + " Runs", // chart title
                "Run #",      // domain axis label
                "Duration (ms)", // range axis label
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(200, 50, 50));
        chartPanel.setChart(chart);
    }

    /** Builds the history tab UI that displays previous script executions. */
    private JPanel createHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout(4,4));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadHistory());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(refreshBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Executes the script with arguments, and updates output, status, summary, chart.
     */
    private void onRun(ActionEvent e) {
        String path = pathField.getText().trim();
        List<Object> args = new ArrayList<>();
        String raw = argsField.getText().trim();
        if (!raw.isEmpty()) {
            Arrays.stream(raw.split(","))
                    .map(String::trim)
                    .forEach(args::add);
        }

        String result;
        try {
            result = profiler.profileScriptWithArgs(path, args);
        }
        catch (Exception ex) {
            result = "ERROR: " + ex.getMessage();
        }

        outputArea.setText(result);
        boolean success = !result.startsWith("ERROR:");
        updateStatus(success);
        updateSummary();
        updateChart();
    }

    /** Updates the status label (green "SUCCESS" or red "ERROR"). */
    private void updateStatus(boolean success) {
        if (success) {
            statusLabel.setText("✓ SUCCESS");
            statusLabel.setForeground(new Color(0,128,0));
        } else {
            statusLabel.setText("✗ ERROR");
            statusLabel.setForeground(Color.RED);
        }
    }

    /** Calculates and updates summary labels from recent runs. */
    private void updateSummary() {
        List<ScriptExecutionResult> runs = profiler.getRecentRuns();
        if (runs.isEmpty()) {
            lastRunLabel.setText("Last: N/A");
            avgLabel.setText("Avg: N/A");
            maxLabel.setText("Max: N/A");
            return;
        }
        double lastMs = runs.get(runs.size()-1).elapsedMs();
        lastRunLabel.setText(String.format("Last: %.2f ms", lastMs));

        double sum = 0, max = 0;
        for (ScriptExecutionResult r : runs) { double ms = r.elapsedMs(); sum += ms; max = Math.max(max, ms); }
        avgLabel.setText(String.format("Avg: %.2f ms", sum / runs.size()));
        maxLabel.setText(String.format("Max: %.2f ms", max));
    }

    /** Loads historic runs into the history table. */
    private void loadHistory() {
        historyModel.setRowCount(0);
        for (ScriptExecutionResult r : profiler.getRecentRuns()) {
            historyModel.addRow(new Object[]{
                    r.path(),
                    r.args().toString(),
                    String.format("%.2f", r.elapsedMs()),
                    new Date(r.timestamp()).toString()
            });
        }
    }
}