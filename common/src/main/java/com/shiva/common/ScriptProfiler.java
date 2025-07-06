package com.shiva.common;

import java.util.List;

public interface ScriptProfiler {
    String ping();
    String profileNow(String scriptText);
    String profileScript(String scriptPath);
    String profileScriptWithArgs(String scriptPath, List<Object> args);
}
