package com.shiva.gateway;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.shiva.common.ScriptProfilerRPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptProfilerHook extends AbstractGatewayModuleHook {

    private GatewayContext context;
    private final LoggerEx log = LogUtil.getLogger(getClass().getSimpleName());

    @Override
    public void setup(GatewayContext gatewayContext) {
        this.context = gatewayContext;
        log.info("Script Profiler setup");
    }

    @Override
    public void startup(LicenseState licenseState) {
        log.info("Script Profiler: startup();");
    }

    @Override
    public void shutdown() {
        log.info("Script Profiler: shutdown()");
    }

    /**
     * Hook into Ignition’s scripting engine so that scripts can call:
     *   system.profiler.yourMethod(...)
     */
    @Override
    public void initializeScriptManager(ScriptManager manager) {
        log.info("Registering system.profiler in Designer scope");
        manager.addScriptModule(
                "system.profiler",
                new ScriptProfilerRPC(manager),
                null
        );
    }
}
