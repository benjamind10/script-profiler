package com.shiva.gateway;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.shiva.common.ScriptProfilerRPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptProfilerHook extends AbstractGatewayModuleHook {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ScriptProfilerRPC profiler = new ScriptProfilerRPC();

    @Override
    public void setup(GatewayContext context) {
        log.info("Script Profiler: setup()");
    }

    @Override
    public void startup(LicenseState licenseState) {
        log.info("Script Profiler: startup(); license = {}", licenseState);
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
        super.initializeScriptManager(manager);
        manager.addScriptModule(
                "system.profiler",
                profiler
//                new PropertiesFileDocProvider()  // if you have a .properties doc file, otherwise omit
        );
        log.info("Script Profiler: registered scripting API under system.profiler");
    }

    /**
     * Clients can invoke RPC on this module via the same RPC object.
     */
    @Override
    public Object getRPCHandler(ClientReqSession session, String projectName) {
        return profiler;
    }
}
