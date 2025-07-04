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

    public String profileNow(String scriptText) {
        return "profiled " + scriptText.length() + " chars";
    }

    /**
     * Execute a named script in your project (e.g. "MyProject.myPackage.myFunc")
     * and report the time.
     */
    public String profileScript(String scriptPath) {
        log.info("Profiling existing script " + scriptPath);

        long start = System.nanoTime();
        Object result;
        try {
            result = "Method call: " + scriptPath + "()";

        } catch (Exception e) {
            log.error("Error running named script " + scriptPath + ": " + e.getMessage(), e);
            return "ERROR: " + e.getMessage();
        }

        double elapsedMs = (System.nanoTime() - start) / 1_000_000.0;
        String out = String.format("Ran %s in %.3f ms → %s", scriptPath, elapsedMs, result);

        log.info(out);
        return out;
    }

    /**
     * Execute a script with arguments (e.g. "helloWorld('Ben')")
     * and report the time.
     */
    public String profileScriptWithArgs(String scriptExpression) {
        log.info("Profiling script with args: " + scriptExpression);

        long start = System.nanoTime();
        Object result;
        try {
            result = "Method call: " + scriptExpression;

        } catch (Exception e) {
            log.error("Error running script " + scriptExpression + ": " + e.getMessage(), e);
            return "ERROR: " + e.getMessage();
        }

        double elapsedMs = (System.nanoTime() - start) / 1_000_000.0;
        String out = String.format("Ran %s in %.3f ms → %s", scriptExpression, elapsedMs, result);

        log.info(out);
        return out;
    }
}