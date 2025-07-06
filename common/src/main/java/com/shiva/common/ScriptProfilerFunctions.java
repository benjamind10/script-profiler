package com.shiva.common;

import org.python.core.PyObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is registered under system.profiler.*
 * It delegates to the real ScriptProfiler implementation (e.g. DefaultScriptProfiler)
 */
public class ScriptProfilerFunctions {

    private final ScriptProfiler delegate;

    public ScriptProfilerFunctions(ScriptProfiler delegate) {
        this.delegate = delegate;
    }

    public String ping() {
        return delegate.ping();
    }

    public String profileNow(String scriptText) {
        return delegate.profileNow(scriptText);
    }

    public String profileScript(String scriptPath) {
        return delegate.profileScript(scriptPath);
    }

    public String profileScriptWithArgs(String scriptPath, PyObject... args) {
        List<Object> javaArgs = new ArrayList<>();
        for (PyObject arg : args) {
            javaArgs.add(arg.__tojava__(Object.class));
        }
        return delegate.profileScriptWithArgs(scriptPath, javaArgs);
    }
}
