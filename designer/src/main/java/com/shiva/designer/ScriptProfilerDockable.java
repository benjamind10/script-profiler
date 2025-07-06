package com.shiva.designer;

import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
import com.shiva.common.DefaultScriptProfiler;

import java.awt.*;

/**
 * A dockable panel in the Ignition Designer that provides a UI for script profiling.
 * <p>
 * This frame is registered with the Designer's docking manager and contains
 * the {@link ProfilerPanel}, allowing users to execute and inspect script performance.
 */
public class ScriptProfilerDockable extends DockableFrame {

    /**
     * Constructs the Script Profiler dockable frame.
     *
     * @param profiler the DefaultScriptProfiler used to retrieve recent script executions
     */
    public ScriptProfilerDockable(DefaultScriptProfiler profiler) {
        super("script-profiler");            // Unique internal frame key
        setTitle("Script Profiler");         // Title shown in the tab UI
        setFrameIcon(null);                  // Optional icon (can be null)

        // Configure initial docking state and sizing
        getContext().setInitMode(DockContext.STATE_AUTOHIDE);
        setPreferredSize(new Dimension(400, 300));

        // Add the main profiler UI panel
        getContentPane().add(new ProfilerPanel(profiler));
    }
}
