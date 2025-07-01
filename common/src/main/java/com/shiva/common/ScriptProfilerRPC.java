package com.shiva.common;

public class ScriptProfilerRPC {

    public void ping() {
        System.out.println("Profiler RPC ping received");
    }

    // add real profiling methods here, e.g.:
    public String profileNow(String scriptText) {
        // run your profiler and return results
        return "profiled " + scriptText.length() + " chars";
    }
}
