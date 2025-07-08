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
 * <p>
 * Contains two tabs:
 * <ul>
 *   <li>Execute: select a script, input args, view live status, summary, preview, and chart.</li>
 *   <li>History: shows a table of recent profiling runs.</li>
 * </ul>
 */
public class ProfilerPanel extends JPanel {

    // === Execution tab components ===
    /** Dropdown of available script paths (editable to add new entries). */
    private final JComboBox<String> scriptCombo = new JComboBox<>();

    /** Text field for comma-separated arguments to pass to the selected script. */
    private final JTextField argsField = new JTextField();

    /** Button to trigger script execution. */
    private final JButton runBtn = new JButton("Run");

    /** Label showing overall success or error status of last run. */
    private final JLabel statusLabel = new JLabel(" ");

    /** Label displaying the duration of the most recent run. */
    private final JLabel lastRunLabel = new JLabel("Last: N/A");

    /** Label displaying the average duration across recent runs. */
    private final JLabel avgLabel = new JLabel("Avg: N/A");

    /** Label displaying the maximum duration across recent runs. */
    private final JLabel maxLabel = new JLabel("Max: N/A");

    /** Read-only area showing a preview (first few lines) of selected script source. */
    private final JTextArea previewArea = new JTextArea(4, 30);

    /** Read-only area showing raw execution output or errors. */
    private final JTextArea outputArea = new JTextArea(6, 30);

    /** Panel that hosts the execution duration bar chart. */
    private ChartPanel chartPanel;

    // === History tab components ===
    /** Table model backing the history table. */
    private final DefaultTableModel historyModel;

    /** Table displaying recent profiling runs (path, args, time, timestamp). */
    private final JTable historyTable;

    // === Profiler backend ===
    /** The DefaultScriptProfiler instance that executes and records profiling runs. */
    private final DefaultScriptProfiler profiler;

    /**
     * Constructs a new ProfilerPanel.
     *
     * @param profiler an initialized DefaultScriptProfiler for running and retrieving history
     */
    public ProfilerPanel(DefaultScriptProfiler profiler) {
        this.profiler = profiler;
        setLayout(new BorderLayout());

        // initialize script dropdown
        scriptCombo.setEditable(true);
        scriptCombo.addItem("shared.helloWorld");

        // enable wrapping for text areas
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);
        previewArea.setEditable(false);
        previewArea.setBackground(new Color(248, 248, 248));
        previewArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        // build tabbed UI
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Execute", createExecutionTab());

        historyModel = new DefaultTableModel(
                new String[]{"Script", "Args", "Duration (ms)", "Timestamp"}, 0
        );
        historyTable = new JTable(historyModel);
        tabs.addTab("History", createHistoryTab());

        add(tabs, BorderLayout.CENTER);

        // update preview on selection change
        scriptCombo.addActionListener(e -> updatePreview());
        scriptCombo.getEditor()
                .getEditorComponent()
                .addPropertyChangeListener("value", e -> updatePreview());

        // initial preview load
        updatePreview();
    }

    /**
     * Builds and returns the "Execute" tab panel.
     * @return the execution tab UI
     */
    private JPanel createExecutionTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));

        // script selector row
        JPanel inputRow = new JPanel(new BorderLayout(4, 4));
        inputRow.add(new JLabel("Script:"), BorderLayout.WEST);
        inputRow.add(scriptCombo, BorderLayout.CENTER);
        inputRow.add(runBtn, BorderLayout.EAST);

        // args row
        JPanel argsRow = new JPanel(new BorderLayout(4, 4));
        argsRow.add(new JLabel("Args (comma-separated):"), BorderLayout.WEST);
        argsRow.add(argsField, BorderLayout.CENTER);

        // status label styling
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // summary grid
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        summaryPanel.add(lastRunLabel);
        summaryPanel.add(avgLabel);
        summaryPanel.add(maxLabel);

        // empty chart placeholder
        chartPanel = new ChartPanel(createEmptyDatasetChart());
        chartPanel.setPreferredSize(new Dimension(400, 150));

        // north section assembly
        JPanel northSection = new JPanel(new BorderLayout(4, 4));
        northSection.add(inputRow, BorderLayout.NORTH);
        northSection.add(argsRow, BorderLayout.CENTER);
        northSection.add(statusLabel, BorderLayout.SOUTH);

        JPanel topWrapper = new JPanel(new BorderLayout(4, 4));
        topWrapper.add(northSection, BorderLayout.NORTH);
        topWrapper.add(summaryPanel, BorderLayout.CENTER);
        topWrapper.add(chartPanel, BorderLayout.SOUTH);

        // preview and output split
        JPanel centerSection = new JPanel(new BorderLayout(4, 4));
        JPanel previewPanel = new JPanel(new BorderLayout(2, 2));
        previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        previewPanel.add(new JScrollPane(previewArea), BorderLayout.CENTER);

        JPanel outputPanel = new JPanel(new BorderLayout(2, 2));
        outputPanel.setBorder(BorderFactory.createTitledBorder("Execution Output"));
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                previewPanel,
                outputPanel);
        splitPane.setResizeWeight(0.3);
        splitPane.setOneTouchExpandable(true);
        centerSection.add(splitPane, BorderLayout.CENTER);

        panel.add(topWrapper, BorderLayout.NORTH);
        panel.add(centerSection, BorderLayout.CENTER);

        // wire run button
        runBtn.addActionListener(this::onRun);
        return panel;
    }

    /**
     * Builds and returns the "History" tab panel.
     * @return the history tab UI
     */
    private JPanel createHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadHistory());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(refreshBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Updates the script preview based on the selected script path.
     */
    private void updatePreview() {
        String scriptPath = getSelectedScriptPath();
        if (scriptPath == null || scriptPath.isEmpty()) {
            previewArea.setText("No script selected");
            return;
        }
        try {
            String content = profiler.getScriptContent(scriptPath);
            previewArea.setText(content != null ? content : "(no content)");
        } catch (Exception e) {
            previewArea.setText("Error loading script: " + e.getMessage());
        }
    }

    /**
     * Retrieves the currently selected or entered script path.
     * @return trimmed script path string
     */
    private String getSelectedScriptPath() {
        Object item = scriptCombo.getEditor().getItem();
        return item == null ? null : item.toString().trim();
    }

    /**
     * Handles the Run button action: executes the script, updates UI components.
     */
    private void onRun(ActionEvent e) {
        String path = getSelectedScriptPath();
        if (path == null || path.isEmpty()) {
            outputArea.setText("ERROR: No script selected");
            updateStatus(false);
            return;
        }
        // add new custom entry
        if (((DefaultComboBoxModel<String>) scriptCombo.getModel())
                .getIndexOf(path) < 0) {
            scriptCombo.addItem(path);
        }
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
        } catch (Exception ex) {
            result = "ERROR: " + ex.getMessage();
        }
        outputArea.setText(result);
        boolean ok = !result.startsWith("ERROR:");
        updateStatus(ok);
        updateSummary();
        updateChart();
    }

    /**
     * Creates an empty bar chart with no data.
     * @return chart instance
     */
    private JFreeChart createEmptyDatasetChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart(
                "Last Runs", "Run #", "Duration (ms)",
                dataset, PlotOrientation.VERTICAL,
                false, true, false
        );
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(200,50,50));
        return chart;
    }

    /**
     * Updates the chart with the durations of the last up to 5 runs.
     */
    private void updateChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<ScriptExecutionResult> runs = profiler.getRecentRuns();
        int start = Math.max(0, runs.size() - 5);
        for (int i = start; i < runs.size(); i++) {
            ScriptExecutionResult r = runs.get(i);
            dataset.addValue(r.elapsedMs(), "Duration", "#" + (i - start + 1));
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Last " + Math.min(5, runs.size()) + " Runs",
                "Run #", "Duration (ms)",
                dataset, PlotOrientation.VERTICAL,
                false, true, false
        );
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(200,50,50));
        chartPanel.setChart(chart);
    }

    /**
     * Updates the status label text and color based on success.
     */
    private void updateStatus(boolean success) {
        if (success) {
            statusLabel.setText("SUCCESS");
            statusLabel.setForeground(new Color(0,128,0));
        } else {
            statusLabel.setText("ERROR");
            statusLabel.setForeground(Color.RED);
        }
    }

    /**
     * Recalculates and updates summary labels (last/avg/max durations).
     */
    private void updateSummary() {
        List<ScriptExecutionResult> runs = profiler.getRecentRuns();
        if (runs.isEmpty()) {
            lastRunLabel.setText("Last: N/A");
            avgLabel.setText("Avg: N/A");
            maxLabel.setText("Max: N/A");
            return;
        }
        double last = runs.get(runs.size()-1).elapsedMs();
        lastRunLabel.setText(String.format("Last: %.2f ms", last));
        double sum=0, max=0;
        for (ScriptExecutionResult r : runs) { double t=r.elapsedMs(); sum+=t; max=Math.max(max,t);}
        avgLabel.setText(String.format("Avg: %.2f ms", sum/runs.size()));
        maxLabel.setText(String.format("Max: %.2f ms", max));
    }

    /**
     * Reloads profiling history into the table.
     */
    private void loadHistory() {
        historyModel.setRowCount(0);
        for (ScriptExecutionResult r : profiler.getRecentRuns()) {
            historyModel.addRow(new Object[]{
                    r.path(), r.args().toString(),
                    String.format("%.2f", r.elapsedMs()),
                    new Date(r.timestamp()).toString()
            });
        }
    }
}
