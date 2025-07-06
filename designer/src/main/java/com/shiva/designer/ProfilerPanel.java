package com.shiva.designer;

import com.shiva.common.DefaultScriptProfiler;
import com.shiva.common.ScriptExecutionResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;

/**
 * A Swing-based panel that displays recent script profiling results,
 * including execution time, arguments, and timestamps.
 */
public class ProfilerPanel extends JPanel {

    private final DefaultTableModel model;
    private final JTable table;
    private final DefaultScriptProfiler profiler;

    /**
     * Constructs the profiler UI panel.
     *
     * @param profiler the instance of DefaultScriptProfiler to pull data from
     */
    public ProfilerPanel(DefaultScriptProfiler profiler) {
        this.profiler = profiler;
        setLayout(new BorderLayout(6, 6));

        model = new DefaultTableModel(new String[]{"Script", "Args", "Duration (ms)", "Timestamp"}, 0);
        table = new JTable(model);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(refreshBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();
    }

    /**
     * Loads and displays recent script execution results.
     */
    private void loadData() {
        model.setRowCount(0); // Clear table

        List<ScriptExecutionResult> results = profiler.getRecentRuns();
        if (results == null || results.isEmpty()) {
            model.addRow(new Object[]{"No Data", "", "", ""});
            return;
        }

        for (ScriptExecutionResult r : results) {
            model.addRow(new Object[]{
                    r.path(),
                    r.args().toString(),
                    String.format("%.2f", r.elapsedMs()),
                    new Date(r.timestamp()).toString()
            });
        }
    }
}
