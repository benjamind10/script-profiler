package com.shiva.common;

import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

import java.util.Collections;

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
     * Execute a named script in your project (e.g. "MyProject.myPackage.myFunc")
     * and report the time.
     */
    public String profileScript(String scriptPath) {
        // fix the logger call to use simple concatenation
        log.info("Profiling existing script " + scriptPath);

        long start = System.nanoTime();
        Object result;
        try {
            // wrap your function call in a tiny bit of code
            String code = String.format("return %s()", scriptPath);
            result = scriptManager;
        } catch (Exception e) {
            log.error("Error running named script " + scriptPath + ": " + e.getMessage(), e);
            return "ERROR: " + e.getMessage();
        }

        double elapsedMs = (System.nanoTime() - start) / 1_000_000.0;
        String out = String.format("Ran %s in %.3f ms → %s", scriptPath, elapsedMs, result);

        // this one is just a simple single-arg info
        log.info(out);
        return out;
    }
}
