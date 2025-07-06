package com.shiva.designer;

import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;

import javax.swing.*;
import java.awt.*;

public class ScriptProfilerDockable extends DockableFrame {

    public ScriptProfilerDockable() {
        super("script-profiler");

        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Hello World from Script Profiler", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(label, BorderLayout.CENTER);

        this.getContentPane().add(panel);
        this.getContext().setInitMode(DockContext.STATE_AUTOHIDE);
        this.setPreferredSize(new Dimension(300, 200));
    }
}
