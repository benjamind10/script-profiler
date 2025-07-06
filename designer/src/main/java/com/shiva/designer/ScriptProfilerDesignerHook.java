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
    private ScriptManager scriptManager;

    /**
     * Registers custom scripting functions under {@code system.profiler}.
     * <p>
     * This method is called automatically during Designer startup.
     *
     * @param manager the scripting manager provided by the Designer
     */
    @Override
    public void initializeScriptManager(ScriptManager manager) {
        log.info("Registering scripting API under system.profiler");

        // Store for use in UI layer
        this.scriptManager = manager;

        // Register functions and doc hints
        DefaultScriptProfiler profiler = new DefaultScriptProfiler(manager);
        ScriptProfilerFunctions functions = new ScriptProfilerFunctions(profiler);
        manager.addScriptModule(
                "system.profiler",
                functions,
                new PropertiesFileDocProvider()
        );
    }

    /**
     * Called when the Designer is fully initialized and licensing is verified.
     * <p>
     * Responsible for installing UI components such as dockable panels.
     *
     * @param context      the current Designer context
     * @param licenseState the current module license state
     */
    @Override
    public void startup(DesignerContext context, LicenseState licenseState) {
        log.info("Script Profiler Designer startup");

        // Safely modify UI components on Swing thread
        SwingUtilities.invokeLater(() -> {
            ScriptProfilerDockable dockable = new ScriptProfilerDockable(scriptManager);
            IgnitionDockingManager dockingManager =
                    (IgnitionDockingManager) context.getDockingManager();
            dockingManager.addFrame(dockable);
        });
    }

    /**
     * Called when the Designer is shutting down or the module is being unloaded.
     * Used to perform any necessary cleanup.
     */
    @Override
    public void shutdown() {
        log.info("Script Profiler Designer shutdown");
    }
}
