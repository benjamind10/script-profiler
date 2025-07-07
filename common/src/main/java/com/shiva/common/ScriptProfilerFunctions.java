package com.shiva.common;

import org.python.core.PyObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Scripting-facing wrapper class registered under {@code system.profiler.*}.
 * <p>
 * This class serves as the public scripting API exposed to the Ignition environment,
 * delegating all method calls to an internal {@link ScriptProfiler} implementation.
 * It handles necessary type conversions (e.g., Jython {@link PyObject} to Java objects)
 * to ensure seamless integration from user script calls.
 */
public class ScriptProfilerFunctions {

    private final ScriptProfiler delegate;

    /**
     * Constructs a wrapper around the provided {@link ScriptProfiler} implementation.
     *
     * @param delegate the actual profiling logic to delegate to
     */
    public ScriptProfilerFunctions(ScriptProfiler delegate) {
        this.delegate = delegate;
    }

    /**
     * Health check method exposed to scripting as {@code system.profiler.ping()}.
     *
     * @return static confirmation message
     */
    public String ping() {
        return delegate.ping();
    }

    /**
     * Profiles the provided raw script text.
     * <p>
     * Exposed as {@code system.profiler.profileNow(scriptText)}.
     *
     * @param scriptText the script content to profile
     * @return profiling result or summary
     */
    public String profileNow(String scriptText) {
        return delegate.profileNow(scriptText);
    }

    /**
     * Profiles a named script path (e.g. {@code shared.oee.track}).
     * <p>
     * Exposed as {@code system.profiler.profileScript("path.to.script")}.
     *
     * @param scriptPath dot-separated path to the script function
     * @return execution result with timing
     */
    public String profileScript(String scriptPath) {
        return delegate.profileScript(scriptPath);
    }

    /**
     * Profiles a named script path with arguments.
     * Converts incoming {@link PyObject}s into Java types before delegation.
     * <p>
     * Exposed as {@code system.profiler.profileScriptWithArgs("path.to.script", arg1, arg2, ...)}.
     *
     * @param scriptPath dot-separated path to the script function
     * @param args       variable-length argument list passed from Python
     * @return execution result with timing
     */
    public String profileScriptWithArgs(String scriptPath, PyObject... args) {
        List<Object> javaArgs = new ArrayList<>();
        for (PyObject arg : args) {
            javaArgs.add(arg.__tojava__(Object.class));
        }
        return delegate.profileScriptWithArgs(scriptPath, javaArgs);
    }

    /**
     * Retrieves the source code content of a script for preview purposes.
     * <p>
     * Exposed as {@code system.profiler.getScriptContent("path.to.script")}.
     *
     * @param scriptPath dot-separated path to the script function
     * @return the script source code as a string, or an appropriate message if unavailable
     */
    public String getScriptContent(String scriptPath) {
        return delegate.getScriptContent(scriptPath);
    }
}