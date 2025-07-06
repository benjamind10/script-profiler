package com.shiva.common;

import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyStringMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of the {@link ScriptProfiler} interface.
 * <p>
 * This class provides functionality to execute and time project scripts
 * within the Ignition scripting environment, with or without arguments.
 */
public class DefaultScriptProfiler implements ScriptProfiler {

    private static final int MAX_HISTORY = 100;

    private final LoggerEx log = LogUtil.getLogger(getClass().getSimpleName());
    private final ScriptManager scriptManager;
    private final List<ScriptExecutionResult> recentRuns = new ArrayList<>();

    /**
     * Constructs a new DefaultScriptProfiler.
     *
     * @param scriptManager the ScriptManager used to execute Ignition project scripts
     */
    public DefaultScriptProfiler(ScriptManager scriptManager) {
        this.scriptManager = scriptManager;
    }

    /**
     * Simple health check to confirm the profiler is responsive.
     *
     * @return a static string indicating the profiler is alive
     */
    @Override
    public String ping() {
        log.info("Ping received from client.");
        return "Script Profiler is alive";
    }

    /**
     * Profiles the execution of the given script text.
     * Currently this implementation simply reports the character count.
     *
     * @param scriptText the script source to profile
     * @return a brief summary of the script content
     */
    @Override
    public String profileNow(String scriptText) {
        return "profiled " + scriptText.length() + " chars";
    }

    /**
     * Profiles the execution of a script at the specified path with no arguments.
     *
     * @param scriptPath the dot-separated path to the script (e.g., shared.utils.myFunc)
     * @return execution summary and timing result
     */
    @Override
    public String profileScript(String scriptPath) {
        return profileScriptWithArgs(scriptPath, List.of());
    }

    /**
     * Profiles the execution of a script at the specified path with the given arguments.
     * Script resolution is performed by importing the root module and walking each nested attribute.
     *
     * @param scriptPath the dot-separated path to the script (e.g., shared.utils.myFunc)
     * @param args       the arguments to pass to the script function
     * @return execution summary including timing and result, or error message if invocation fails
     */
    @Override
    public String profileScriptWithArgs(String scriptPath, List<Object> args) {
        log.info("Profiling script with args: " + scriptPath + ", args=" + args);

        long startNanos = System.nanoTime();
        Object result;

        try {
            String[] parts = scriptPath.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Script path must have at least one module and one function");
            }

            String rootModule = parts[0];
            PyStringMap locals = new PyStringMap();

            // Import the root module into the local namespace
            scriptManager.runCode("import " + rootModule, locals, "<import:" + rootModule + ">");

            // Traverse the path to the final function
            PyObject current = locals.__getitem__(rootModule);
            for (int i = 1; i < parts.length; i++) {
                current = current.__getattr__(parts[i]);
            }

            // Convert arguments to PyObjects
            PyObject[] pyArgs = args.stream()
                    .map(Py::java2py)
                    .toArray(PyObject[]::new);

            // Execute the function
            PyObject raw = scriptManager.runFunction(current, pyArgs);
            result = raw.__tojava__(Object.class);
        } catch (Exception e) {
            log.error("Error running script '" + scriptPath + "' with args: " + e.getMessage(), e);
            result = "ERROR: " + e.getMessage();
        }

        double elapsedMs = (System.nanoTime() - startNanos) / 1_000_000.0;
        long timestamp = System.currentTimeMillis();

        ScriptExecutionResult run = new ScriptExecutionResult(scriptPath, args, elapsedMs, timestamp);

        synchronized (recentRuns) {
            recentRuns.add(run);
            if (recentRuns.size() > MAX_HISTORY) {
                recentRuns.remove(0);
            }
        }

        String out = String.format("Ran %s(%s) in %.3f ms → %s", scriptPath, args, elapsedMs, result);
        log.info(out);
        return out;
    }

    /**
     * Returns a snapshot of the recent script executions.
     *
     * @return list of recent profiling results
     */
    public List<ScriptExecutionResult> getRecentRuns() {
        synchronized (recentRuns) {
            return List.copyOf(recentRuns);
        }
    }
}
