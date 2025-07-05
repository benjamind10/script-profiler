package com.shiva.designer;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.shiva.common.DefaultScriptProfiler;
import com.shiva.common.ScriptProfilerFunctions;

public class ScriptProfilerDesignerHook extends AbstractDesignerModuleHook {

    private DesignerContext context;
    private final LoggerEx log = LogUtil.getLogger(getClass().getSimpleName());

    /**
     * Called when the Designer has finished licensing checks.
     */
    @Override
    public void startup(DesignerContext context, LicenseState licenseState) {
        this.context = context;
        log.info("Script Profiler Designer startup");
    }

    /**
     * Hook into the Designer's scripting engine. Any subsequent
     * calls to system.profiler.xxx() will be dispatched to your implementation.
     */
    @Override
    public void initializeScriptManager(ScriptManager manager) {
        log.info("Registering scripting API under system.profiler");

        DefaultScriptProfiler profiler = new DefaultScriptProfiler(manager);
        ScriptProfilerFunctions functions = new ScriptProfilerFunctions(profiler);

        manager.addScriptModule(
                "system.profiler",
                functions,
                new PropertiesFileDocProvider()
        );
    }

    /**
     * Called when the Designer is shutting down or the module is unloaded.
     */
    @Override
    public void shutdown() {
        log.info("Script Profiler Designer shutdown");
    }
}
