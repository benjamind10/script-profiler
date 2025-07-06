package com.shiva.common;

import java.util.List;

/**
 * Holds the result of a single script execution, including performance data.
 */
public record ScriptExecutionResult(
        String path,
        List<Object> args,
        double elapsedMs,
//        String result,
        long timestamp
) {}
