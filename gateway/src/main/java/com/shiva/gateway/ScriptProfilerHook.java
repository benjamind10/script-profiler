package com.shiva.gateway;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.web.models.ConfigCategory;
import com.inductiveautomation.ignition.gateway.web.models.DefaultConfigTab;
import com.inductiveautomation.ignition.gateway.web.models.IConfigTab;
import com.shiva.common.DefaultScriptProfiler;
import com.shiva.common.ScriptProfilerFunctions;

import java.util.Collections;
import java.util.List;

/**
 * ScriptProfilerHook is the main entry point for the Script Profiler module on the Gateway side.
 *
 * <p>This class is responsible for registering the Gateway Config UI elements, exposing
 * scripting functions (under {@code system.profiler.*}), and managing lifecycle hooks
 * such as setup, startup, and shutdown.</p>
 *
 * <p>The config UI includes a "Script Profiler" section under the Gateway Config page,
 * with an "Overview" tab. The tab label supports i18n via {@code resources.properties}.</p>
 *
 * <p>During {@link #setup(GatewayContext)}, we also register our i18n bundle
 * so labels like tab titles are rendered correctly.</p>
 */
public class ScriptProfilerHook extends AbstractGatewayModuleHook {

    private final LoggerEx log = LogUtil.getLogger(getClass().getSimpleName());

    /** Top-level category that appears under Gateway Config → Script Profiler */
    private static final ConfigCategory CFG_CAT = new ConfigCategory(
            "scriptProfiler",       // internal ID used in URLs
            "Script Profiler",      // fallback label if i18n fails
            800                     // menu sort order (0–1000)
    );

    /** Defines the "Overview" tab inside our config category. */
    private static final IConfigTab OVERVIEW_TAB = DefaultConfigTab.builder()
            .category(CFG_CAT)
            .name("overview")                          // appears in URL path
            .i18n("ScriptProfiler.config.overview")    // resolved from resources.properties
            .page(ScriptProfilerConfigPage.class)      // Wicket panel backing the tab
            .terms("script", "profiling", "overview")  // optional search terms for filter box
            .build();

    @Override
    public List<ConfigCategory> getConfigCategories() {
        return Collections.singletonList(CFG_CAT);
    }

    @Override
    public List<? extends IConfigTab> getConfigPanels() {
        return Collections.singletonList(OVERVIEW_TAB);
    }

    /**
     * Called during Gateway startup to register scripting APIs.
     * Here we expose the {@code system.profiler} namespace for project scripts.
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

    /**
     * Called once early in Gateway startup. We use this hook to register
     * our i18n bundle so text labels in the config UI can be localized.
     */
    @Override
    public void setup(GatewayContext ctx) {
        log.info("Script Profiler Gateway setup");

        // Register the bundle used for i18n keys like ScriptProfiler.config.overview
        BundleUtil.get().addBundle("ScriptProfiler", getClass(), "resources");
    }

    /**
     * Called after the Gateway finishes startup. No-op for now,
     * but a good place to log readiness or initialize long-running services.
     */
    @Override
    public void startup(LicenseState licenseState) {
        log.info("Script Profiler Gateway startup");
    }

    /**
     * Called during module shutdown or undeploy.
     * We use this to unregister our i18n bundle.
     */
    @Override
    public void shutdown() {
        log.info("Script Profiler Gateway shutdown");

        // Remove i18n bundle to clean up
        BundleUtil.get().removeBundle("ScriptProfiler");
    }
}
