package com.shiva.designer;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

public class ScriptProfilerDesignerHook extends AbstractDesignerModuleHook {

    private final LoggerEx log = LogUtil.getLogger(getClass().getSimpleName());

    @Override
    public void startup(DesignerContext context, LicenseState licenseState) {
        log.info("Script Profiler Designer startup. License active: " + licenseState.toString());
        // You can also inspect licenseState.getModuleId() etc.
    }

    @Override
    public void shutdown() {
        log.info("Script Profiler Designer shutdown");
    }
}
