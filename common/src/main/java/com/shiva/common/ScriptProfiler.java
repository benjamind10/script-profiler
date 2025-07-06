package com.shiva.common;

import java.util.List;

/**
 * Interface defining the contract for script profiling utilities.
 * <p>
 * Implementations are expected to execute project scripts, either by source or path,
 * optionally timing and reporting their execution duration and output.
 */
public interface ScriptProfiler {

    /**
     * Performs a basic connectivity check.
     *
     * @return a simple message confirming the profiler is responsive
     */
    String ping();

    /**
     * Profiles a script given as raw source code.
     * This method is useful for evaluating and benchmarking inline scripts.
     *
     * @param scriptText the body of the script to profile
     * @return profiling result or summary of the input script
     */
    String profileNow(String scriptText);

    /**
     * Profiles a script specified by its dot-separated path (e.g. {@code shared.utils.myFunc}).
     * The script is executed with no arguments.
     *
     * @param scriptPath the qualified path to the script function
     * @return the result of executing the script, including execution time
     */
    String profileScript(String scriptPath);

    /**
     * Profiles a script specified by its dot-separated path, executed with the provided arguments.
     *
     * @param scriptPath the qualified path to the script function
     * @param args       a list of arguments to pass to the script function
     * @return the result of executing the script, including execution time and returned value
     */
    String profileScriptWithArgs(String scriptPath, List<Object> args);
}
