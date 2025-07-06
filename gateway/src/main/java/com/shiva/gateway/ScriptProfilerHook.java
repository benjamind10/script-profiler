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

/**
 * Gateway hook for the Script Profiler module.
 * <p>
 * This class registers scripting functions on the Gateway and manages lifecycle events
 * such as setup, startup, and shutdown.
 */
public class ScriptProfilerHook extends AbstractGatewayModuleHook {

    private GatewayContext context;
    private final LoggerEx log = LogUtil.getLogger(getClass().getSimpleName());

    /**
     * Called when the module is loaded during Gateway initialization.
     *
     * @param gatewayContext the context for interacting with the Gateway
     */
    @Override
    public void setup(GatewayContext gatewayContext) {
        this.context = gatewayContext;
        log.info("Script Profiler: setup()");
    }

    /**
     * Called once the Gateway has completed startup and licensing is verified.
     *
     * @param licenseState the current license state of the module
     */
    @Override
    public void startup(LicenseState licenseState) {
        log.info("Script Profiler Designer startup");
    }

    /**
     * Called when the module is being shut down or unloaded.
     */
    @Override
    public void shutdown() {
        log.info("Script Profiler: shutdown()");
    }

    /**
     * Registers the {@code system.profiler} scripting functions within the Gateway scope.
     * This allows scripting access to profiling features from Gateway-scoped scripts.
     *
     * @param manager the script manager used to register scripting functions
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
