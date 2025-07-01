package com.shiva.gateway;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

public class ScriptProfilerHook extends AbstractGatewayModuleHook {

    private final LoggerEx log = LogUtil.getLogger(getClass().getSimpleName());

    /**
     * Called when the module is first set up (before startup).
     */
    @Override
    public void setup(GatewayContext context) {
        log.info("Script Profiler gateway setup");
    }

    @Override
    public void startup(LicenseState licenseState) {
        log.info("Script Profiler gateway startup");
    }


    /**
     * Called when the module is being shut down.
     */
    @Override
    public void shutdown() {
        log.info("Script Profiler gateway shutdown");
    }
}
