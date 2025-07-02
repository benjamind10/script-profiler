package com.shiva.designer;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.shiva.common.ScriptProfilerRPC;

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
     * Hook into the Designer's scripting engine.  Any subsequent
     * calls to system.profiler.xxx() will be dispatched to your RPC.
     */
    @Override
    public void initializeScriptManager(ScriptManager manager) {
        log.info("Registering scripting API under system.profiler");
        manager.addScriptModule(
                "system.profiler",
                // give your RPC the same manager the Designer is using
                new ScriptProfilerRPC(manager),
                /* no doc-provider properties file */ null
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
