package com.shiva.designer;

import com.shiva.common.DefaultScriptProfiler;
import com.shiva.common.ScriptExecutionResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.List;

/**
 * Swing panel for executing project scripts and viewing profiling history.
 * Contains two tabs: one for ad-hoc execution, one for viewing recent results.
 */
public class ProfilerPanel extends JPanel {

    private final JTextField pathField = new JTextField("shared.helloWorld");
    private final JTextArea outputArea = new JTextArea(6, 30);
    private final DefaultTableModel historyModel;
    private final JTable historyTable;
    private final DefaultScriptProfiler profiler;

    /**
     * Constructs the profiler panel, initializing tabs and UI components.
     *
     * @param profiler The script profiler instance used for execution and historical data
     */
    public ProfilerPanel(DefaultScriptProfiler profiler) {
        this.profiler = profiler;
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

        // Setup Execution tab
        tabs.addTab("Execute", createExecutionTab());

        // Setup History tab
        historyModel = new DefaultTableModel(new String[]{"Script", "Args", "Duration (ms)", "Timestamp"}, 0);
        historyTable = new JTable(historyModel);
        tabs.addTab("History", createHistoryTab());

        add(tabs, BorderLayout.CENTER);
    }

    /**
     * Builds the execution tab UI for script entry and manual invocation.
     */
    private JPanel createExecutionTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));

        JPanel top = new JPanel(new BorderLayout(4, 4));
        top.add(new JLabel("Script Path:"), BorderLayout.WEST);
        top.add(pathField, BorderLayout.CENTER);
        JButton runBtn = new JButton("Run");
        top.add(runBtn, BorderLayout.EAST);

        outputArea.setEditable(false);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        runBtn.addActionListener(this::onRun);
        return panel;
    }

    /**
     * Builds the history tab UI that displays previous script executions.
     */
    private JPanel createHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(refreshBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        return panel;
    }

    /**
     * Executes the script path entered in the input field and displays the result.
     */
    private void onRun(ActionEvent e) {
        String path = pathField.getText().trim();
        try {
            String result = profiler.profileScript(path);
            outputArea.setText(result);
        } catch (Exception ex) {
            outputArea.setText("ERROR: " + ex.getMessage());
        }
    }

    /**
     * Loads recent profiling results into the table.
     */
    private void loadData() {
        historyModel.setRowCount(0); // Clear table

        List<ScriptExecutionResult> results = profiler.getRecentRuns();
        for (ScriptExecutionResult r : results) {
            historyModel.addRow(new Object[]{
                    r.path(),
                    r.args().toString(),
                    String.format("%.2f", r.elapsedMs()),
                    new Date(r.timestamp()).toString()
            });
        }
    }
}
