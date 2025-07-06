package com.shiva.gateway;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.shiva.common.DefaultScriptProfiler;
import com.shiva.common.ScriptProfilerFunctions;

public class ScriptProfilerHook extends AbstractGatewayModuleHook {

    private GatewayContext context;
    private final LoggerEx log = LogUtil.getLogger(getClass().getSimpleName());

    @Override
    public void setup(GatewayContext gatewayContext) {
        this.context = gatewayContext;
        log.info("Script Profiler: setup()");
    }

    @Override
    public void startup(LicenseState licenseState) {
        log.info("Script Profiler: startup()");
    }

    @Override
    public void shutdown() {
        log.info("Script Profiler: shutdown()");
    }

    /**
     * Register system.profiler scripting functions on the Gateway.
     */
    @Override
    public void initializeScriptManager(ScriptManager manager) {
        log.info("Registering system.profiler in Gateway scope");

        DefaultScriptProfiler profiler = new DefaultScriptProfiler(manager);
        ScriptProfilerFunctions functions = new ScriptProfilerFunctions(profiler);

        manager.addScriptModule(
                "system.profiler",
                functions,
                new PropertiesFileDocProvider()
        );
    }
}
