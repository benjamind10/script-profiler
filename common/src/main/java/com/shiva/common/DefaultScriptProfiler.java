package com.shiva.common;

import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyStringMap;

import java.util.List;

public class DefaultScriptProfiler implements ScriptProfiler {
    private final LoggerEx log = LogUtil.getLogger(getClass().getSimpleName());
    private final ScriptManager scriptManager;

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

        long startNanos = System.nanoTime();
        Object result;
        try {
            String[] parts = scriptPath.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Script path must have at least one module and one function");
            }

            String rootModule = parts[0];
            PyStringMap locals = new PyStringMap();
            scriptManager.runCode("import " + rootModule, locals, "<import:" + rootModule + ">");

            PyObject current = locals.__getitem__(rootModule);
            for (int i = 1; i < parts.length; i++) {
                current = current.__getattr__(parts[i]);
            }

            PyObject[] pyArgs = args.stream()
                    .map(Py::java2py)
                    .toArray(PyObject[]::new);

            PyObject raw = scriptManager.runFunction(current, pyArgs);
            result = raw.__tojava__(Object.class);
        } catch (Exception e) {
            log.error("Error running script '" + scriptPath + "' with args: " + e.getMessage(), e);
            return "ERROR: " + e.getMessage();
        }

        double elapsedMs = (System.nanoTime() - startNanos) / 1_000_000.0;
        String out = String.format("Ran %s(%s) in %.3f ms → %s",
                scriptPath,
                args.toString(),
                elapsedMs,
                result
        );

        log.info(out);
        return out;
    }
}
