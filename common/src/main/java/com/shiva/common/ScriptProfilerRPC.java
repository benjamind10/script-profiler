package com.shiva.common;

import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import org.python.core.PyObject;
import org.python.core.PyStringMap;

public class ScriptProfilerRPC {
    private final LoggerEx log = LogUtil.getLogger(getClass().getSimpleName());
    private final ScriptManager scriptManager;

    public ScriptProfilerRPC(ScriptManager scriptManager) {
        this.scriptManager = scriptManager;
    }

    /**
     * Simple health check.
     */
    public String ping() {
        log.info("Ping received from client.");
        return "Script Profiler is alive";
    }

    // add real profiling methods here, e.g.:
    public String profileNow(String scriptText) {
        // run your profiler and return results
        return "profiled " + scriptText.length() + " chars";
    }

    /**
     * Execute a named script in your project (e.g. "shared.helloWorld")
     * and report the time.
     */
    public String profileScript(String scriptPath) {
        log.info("Profiling existing script " + scriptPath);

        long startNanos = System.nanoTime();
        Object result;
        try {
            // Split the path on dots (e.g., shared.oee.counters.logCounters)
            String[] parts = scriptPath.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Script path must have at least one module and one function");
            }

            // Import the root module (e.g., "shared")
            String rootModule = parts[0];
            PyStringMap locals = new PyStringMap();
            scriptManager.runCode("import " + rootModule, locals, "<import:" + rootModule + ">");

            // Recursively walk the parts to get the final function
            PyObject current = locals.__getitem__(rootModule);
            for (int i = 1; i < parts.length; i++) {
                current = current.__getattr__(parts[i]);
            }

            // Call the final function
            PyObject raw = scriptManager.runFunction(current);
            result = raw.__tojava__(Object.class);
        }
        catch (Exception e) {
            log.error("Error running script '" + scriptPath + "': " + e.getMessage(), e);
            return "ERROR: " + e.getMessage();
        }

        double elapsedMs = (System.nanoTime() - startNanos) / 1_000_000.0;
        String out = String.format("Ran %s in %.3f ms → %s", scriptPath, elapsedMs, result);

        log.info(out);
        return out;
    }
}
