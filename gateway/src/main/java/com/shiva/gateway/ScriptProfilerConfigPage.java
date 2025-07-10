package com.shiva.gateway;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.markup.html.basic.Label;
import com.inductiveautomation.ignition.gateway.web.components.ConfigPanel;

/**
 * Configuration page placeholder for the Script Profiler Gateway module.
 * <p>
 * This panel appears under Config → Script Profiler → Overview.
 */
public class ScriptProfilerConfigPage extends ConfigPanel {

    /**
     * Constructs the config page panel.
     *
     * @param id Wicket component id
     */
    public ScriptProfilerConfigPage(String id) {
        super(id);
        // Simple placeholder; swap out for React mount point later
        add(new Label("placeholder", "🚧 Script Profiler Config coming soon!"));
    }

    /**
     * Defines where this page appears in the Gateway config navigation:
     * the category key "scriptProfiler" and tab key "overview".
     *
     * @return a Pair of (category, tab) matching the IConfigTab setup
     */
    @Override
    public Pair<String, String> getMenuLocation() {
        return Pair.of("scriptProfiler", "overview");
    }
}
