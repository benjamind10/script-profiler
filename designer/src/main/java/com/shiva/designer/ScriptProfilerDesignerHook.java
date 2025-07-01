package com.shiva.designer;

import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.common.licensing.LicenseState;

public class ScriptProfilerDesignerHook extends AbstractDesignerModuleHook {

    private final LoggerEx log = LogUtil.getLogger(getClass().getSimpleName());

    /**
     * Called once licensing has been checked in the Designer.
     */
    @Override
    public void startup(DesignerContext context, LicenseState licenseState) throws Exception {
        log.info("Script Profiler designer startup (license = {})", (Throwable) licenseState);
    }

    /**
     * Called when the Designer is closing or the module is unloaded.
     */
    @Override
    public void shutdown() {
        log.info("Script Profiler designer shutdown");
    }
}
