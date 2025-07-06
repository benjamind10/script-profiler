package com.shiva.designer;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.IgnitionDockingManager;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.shiva.common.DefaultScriptProfiler;
import com.shiva.common.ScriptProfilerFunctions;

import javax.swing.SwingUtilities;

/**
 * Module hook class for the Ignition Designer scope.
 * <p>
 * Responsible for setting up the scripting API and UI elements
 * (such as dockable panels) when the module is loaded into the Designer.
 */
public class ScriptProfilerDesignerHook extends AbstractDesignerModuleHook {

    private final LoggerEx log = LogUtil.getLogger(getClass().getSimpleName());
    private DesignerContext context;
    private DefaultScriptProfiler profiler;

    @Override
    public void initializeScriptManager(ScriptManager manager) {
        log.info("Registering scripting API under system.profiler");

        // Create profiler
        this.profiler = new DefaultScriptProfiler(manager);

        // Register functions
        ScriptProfilerFunctions functions = new ScriptProfilerFunctions(profiler);
        manager.addScriptModule(
                "system.profiler",
                functions,
                new PropertiesFileDocProvider()
        );

        // Add dockable UI panel now that profiler is initialized
        SwingUtilities.invokeLater(() -> {
            ScriptProfilerDockable dockable = new ScriptProfilerDockable(profiler);
            IgnitionDockingManager dockingManager =
                    (IgnitionDockingManager) context.getDockingManager();
            dockingManager.addFrame(dockable);
        });
    }

    @Override
    public void startup(DesignerContext context, LicenseState licenseState) {
        this.context = context; // Needed for dockingManager in initializeScriptManager
        log.info("Script Profiler Designer startup");
    }

    @Override
    public void shutdown() {
        log.info("Script Profiler Designer shutdown");
    }
}
