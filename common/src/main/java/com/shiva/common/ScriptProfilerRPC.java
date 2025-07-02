package com.shiva.common;

import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

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
}
