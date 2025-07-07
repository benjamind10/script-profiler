package com.shiva.common;

import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyStringMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of the {@link ScriptProfiler} interface.
 * <p>
 * This class provides functionality to execute and time user-created project scripts
 * within the Ignition scripting environment. It explicitly blocks system scripts
 * (those starting with "system.") to restrict profiling to user-defined code.
 */
public class DefaultScriptProfiler implements ScriptProfiler {

    private static final int MAX_HISTORY = 100;
    private final LoggerEx log = LogUtil.getLogger(getClass().getSimpleName());
    private final ScriptManager scriptManager;
    private final List<ScriptExecutionResult> recentRuns = new ArrayList<>();

    /**
     * Constructs a new DefaultScriptProfiler.
     *
     * @param scriptManager the ScriptManager used to execute project scripts
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
     * Profiles the execution of the given script text. Currently returns length.
     *
     * @param scriptText the raw script content to profile
     * @return summary of profiling (character count)
     */
    @Override
    public String profileNow(String scriptText) {
        return "profiled " + scriptText.length() + " chars";
    }

    /**
     * Profiles a named script with no arguments.
     *
     * @param scriptPath dot-separated path to the script (e.g. shared.utils.myFunc)
     * @return execution summary including timing
     */
    @Override
    public String profileScript(String scriptPath) {
        return profileScriptWithArgs(scriptPath, List.of());
    }

    /**
     * Profiles a named script with the provided arguments.
     * Blocks any paths starting with "system.".
     *
     * @param scriptPath the dot-separated path to the script function
     * @param args       arguments to pass to the script
     * @return execution summary including timing and result, or error message
     */
    @Override
    public String profileScriptWithArgs(String scriptPath, List<Object> args) {
        log.info("Profiling script with args: " + scriptPath + ", args=" + args);

        long startNanos = System.nanoTime();
        Object result;

        try {
            // Block system scripts
            if (scriptPath.startsWith("system.")) {
                throw new IllegalArgumentException(
                        "Profiling of system.* scripts is not permitted.");
            }

            String[] parts = scriptPath.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException(
                        "Script path must contain at least module and function");
            }

            String rootModule = parts[0];
            PyStringMap locals = new PyStringMap();

            // Import root module
            scriptManager.runCode(
                    "import " + rootModule,
                    locals,
                    "<import:" + rootModule + ">"
            );

            // Resolve function via nested attributes
            PyObject current = locals.__getitem__(rootModule);
            for (int i = 1; i < parts.length; i++) {
                current = current.__getattr__(parts[i]);
            }

            // Convert Java args to PyObjects
            PyObject[] pyArgs = args.stream()
                    .map(Py::java2py)
                    .toArray(PyObject[]::new);

            // Execute the function
            PyObject raw = scriptManager.runFunction(current, pyArgs);
            result = raw.__tojava__(Object.class);
        }
        catch (Exception e) {
            log.error("Error running script '" + scriptPath + "' with args: "
                    + e.getMessage(), e);
            result = "ERROR: " + e.getMessage();
        }

        double elapsedMs = (System.nanoTime() - startNanos) / 1_000_000.0;
        long timestamp = System.currentTimeMillis();

        ScriptExecutionResult run = new ScriptExecutionResult(
                scriptPath, args, elapsedMs, timestamp
        );

        synchronized (recentRuns) {
            recentRuns.add(run);
            if (recentRuns.size() > MAX_HISTORY) {
                recentRuns.remove(0);
            }
        }

        String out = String.format(
                "Ran %s(%s) in %.3f ms → %s",
                scriptPath, args, elapsedMs, result
        );
        log.info(out);
        return out;
    }

    /**
     * Returns an immutable snapshot of recent profiling runs.
     *
     * @return list of ScriptExecutionResult
     */
    public List<ScriptExecutionResult> getRecentRuns() {
        synchronized (recentRuns) {
            return List.copyOf(recentRuns);
        }
    }
}
