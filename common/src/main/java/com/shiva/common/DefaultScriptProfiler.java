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
 * Provides timing of user‐created project scripts only, and maintains a short history.
 */
public class DefaultScriptProfiler implements ScriptProfiler {

    private static final int MAX_HISTORY = 100;

    private final LoggerEx log = LogUtil.getLogger(getClass().getSimpleName());
    private final ScriptManager scriptManager;
    private final List<ScriptExecutionResult> recentRuns = new ArrayList<>();

    /**
     * @param scriptManager the Ignition ScriptManager used to execute project scripts
     */
    public DefaultScriptProfiler(ScriptManager scriptManager) {
        this.scriptManager = scriptManager;
    }

    @Override
    public String ping() {
        log.info("Ping received from client.");
        return "Script Profiler is alive";
    }

    @Override
    public String profileNow(String scriptText) {
        return "profiled " + scriptText.length() + " chars";
    }

    @Override
    public String profileScript(String scriptPath) {
        return profileScriptWithArgs(scriptPath, List.of());
    }

    @Override
    public String profileScriptWithArgs(String scriptPath, List<Object> args) {
        log.info("Profiling script with args: " + scriptPath + ", args=" + args);

        long start = System.nanoTime();
        Object result;

        try {
            if (scriptPath.startsWith("system.")) {
                throw new IllegalArgumentException("Profiling system.* scripts is not permitted.");
            }

            String[] parts = scriptPath.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Path must have at least one module and one function");
            }

            String root = parts[0];
            PyStringMap locals = new PyStringMap();

            // import the root module
            scriptManager.runCode("import " + root, locals, "<import:" + root + ">");

            // walk down to the function
            PyObject current = locals.__getitem__(root);
            for (int i = 1; i < parts.length; i++) {
                current = current.__getattr__(parts[i]);
            }

            // convert Java args → PyObject[]
            PyObject[] pyArgs = args.stream()
                    .map(Py::java2py)
                    .toArray(PyObject[]::new);

            PyObject raw = scriptManager.runFunction(current, pyArgs);
            result = raw.__tojava__(Object.class);
        }
        catch (Exception e) {
            log.error("Error running '" + scriptPath + "': " + e.getMessage(), e);
            result = "ERROR: " + e.getMessage();
        }

        double elapsed = (System.nanoTime() - start) / 1_000_000.0;
        long ts = System.currentTimeMillis();

        // record history
        ScriptExecutionResult run = new ScriptExecutionResult(scriptPath, args, elapsed, ts);
        synchronized (recentRuns) {
            recentRuns.add(run);
            if (recentRuns.size() > MAX_HISTORY) {
                recentRuns.remove(0);
            }
        }

        String out = String.format("Ran %s(%s) in %.3f ms → %s",
                scriptPath, args, elapsed, result);
        log.info(out);
        return out;
    }

    /**
     * @return an immutable list of the recent profiling runs
     */
    public List<ScriptExecutionResult> getRecentRuns() {
        synchronized (recentRuns) {
            return List.copyOf(recentRuns);
        }
    }

    /**
     * Exposes the underlying ScriptManager for introspection or UI use.
     */
    public ScriptManager getScriptManager() {
        return scriptManager;
    }

    /**
     * Retrieves the first few lines of a user script’s source code for preview.
     * Blocks any path starting with "system.".
     *
     * @param scriptPath e.g. "shared.myFunc"
     * @return either the first 3 lines of source, or an explanatory error
     */
    public String getScriptContent(String scriptPath) {
        log.debug("Getting script content for: " + scriptPath);

        if (scriptPath.startsWith("system.")) {
            return "Preview unavailable for system.* scripts.";
        }
        if (scriptPath.isBlank()) {
            return "No script path provided.";
        }

        String[] parts = scriptPath.split("\\.");
        if (parts.length < 2) {
            return "Invalid path; needs module and function (e.g. shared.myFunc)";
        }

        String root = parts[0];
        PyStringMap locals = new PyStringMap();

        try {
            scriptManager.runCode("import " + root, locals, "<import:" + root + ">");
        }
        catch (Exception e) {
            return "Error importing module '" + root + "': " + e.getMessage();
        }

        // drill down
        PyObject current = locals.__getitem__(root);
        StringBuilder full = new StringBuilder(root);
        try {
            for (int i = 1; i < parts.length; i++) {
                current = current.__getattr__(parts[i]);
                full.append(".").append(parts[i]);
            }
        }
        catch (Exception e) {
            return "Error navigating to '" + scriptPath + "': " + e.getMessage();
        }

        // attempt inspect.getsource
        try {
            PyStringMap inspectLocals = new PyStringMap();
            String code =
                    "import inspect\n" +
                            "import " + root + "\n" +
                            "func = " + full + "\n" +
                            "src = None\n" +
                            "try:\n" +
                            "    src = inspect.getsource(func)\n" +
                            "except:\n" +
                            "    pass\n" +
                            "result = src";
            scriptManager.runCode(code, inspectLocals, "<getsource>");
            PyObject srcObj = inspectLocals.__getitem__(Py.newString("result"));
            String src = srcObj == null ? null : srcObj.toString();
            if (src != null) {
                // take first 3 lines
                String[] lines = src.split("\\R", 4);
                StringBuilder preview = new StringBuilder();
                preview.append("=== ").append(scriptPath).append(" ===\n\n");
                for (int i = 0; i < Math.min(3, lines.length); i++) {
                    preview.append(lines[i]).append("\n");
                }
                return preview.toString();
            }
        }
        catch (Exception ignored) {}

        return "Source not available for " + scriptPath;
    }
}
