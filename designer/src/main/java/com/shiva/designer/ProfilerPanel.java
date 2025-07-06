package com.shiva.designer;

import com.inductiveautomation.ignition.common.script.ScriptManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * A simple Swing-based UI panel for running and profiling project scripts.
 * <p>
 * This panel is intended to be used inside the Ignition Designer's dockable interface.
 * Users can input a dot-separated script path, invoke it via a button, and view the result
 * in a scrollable text area.
 */
public class ProfilerPanel extends JPanel {

    /** Input field for the fully-qualified script path (e.g. shared.utils.testScript) */
    private final JTextField pathField = new JTextField("shared.helloWorld");

    /** Output display area for script results or error messages */
    private final JTextArea outputArea = new JTextArea(8, 30);

    /** Reference to the Designer's script manager, used to execute user scripts */
    private final ScriptManager scriptManager;

    /**
     * Constructs a profiler panel with attached script execution support.
     *
     * @param scriptManager the Ignition script manager for evaluating project scripts
     */
    public ProfilerPanel(ScriptManager scriptManager) {
        this.scriptManager = scriptManager;
        setLayout(new BorderLayout(6, 6));

        // Top bar: label + input + button
        JPanel top = new JPanel(new BorderLayout(4, 4));
        top.add(new JLabel("Script Path:"), BorderLayout.WEST);
        top.add(pathField, BorderLayout.CENTER);
        JButton runBtn = new JButton("Run");
        top.add(runBtn, BorderLayout.EAST);

        // Center: scrollable results
        outputArea.setEditable(false);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        runBtn.addActionListener(this::onRun);
    }

    /**
     * Handles the Run button click.
     * <p>
     * This will eventually use the ScriptManager to execute the user-provided path.
     * Currently placeholder logic.
     *
     * @param e the originating action event
     */
    private void onRun(ActionEvent e) {
        String scriptPath = pathField.getText().trim();
        try {
            Object result = scriptManager; // TODO: Replace with actual script execution
            outputArea.setText(result != null ? result.toString() : "null");
        } catch (Exception ex) {
            outputArea.setText("ERROR: " + ex.getMessage());
        }
    }
}
