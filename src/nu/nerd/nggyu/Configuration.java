package nu.nerd.nggyu;

import java.util.ArrayList;

import org.bukkit.configuration.ConfigurationSection;

// ----------------------------------------------------------------------------
/**
 * Configuration wrapper.
 */
public class Configuration {
    // ------------------------------------------------------------------------
    /**
     * If true, log configuration loading.
     */
    public boolean DEBUG_CONFIG;

    /**
     * If true, log the note played in the event handler.
     */
    public boolean DEBUG_EVENT;

    /**
     * If true, this plugin is enabled.
     */
    public boolean ENABLED;

    /**
     * All known tunes.
     */
    protected ArrayList<Tune> TUNES = new ArrayList<Tune>();

    // ------------------------------------------------------------------------
    /**
     * Reload the configuration file.
     */
    public void reload() {
        NGGYU.PLUGIN.reloadConfig();

        DEBUG_CONFIG = NGGYU.PLUGIN.getConfig().getBoolean("debug.config");
        DEBUG_EVENT = NGGYU.PLUGIN.getConfig().getBoolean("debug.event");
        ENABLED = NGGYU.PLUGIN.getConfig().getBoolean("enabled");
        ConfigurationSection tunes = NGGYU.PLUGIN.getConfig().getConfigurationSection("tunes");
        TUNES.clear();
        for (String key : tunes.getKeys(false)) {
            TUNES.add(new Tune(tunes.getConfigurationSection(key)));
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Save the configuration file.
     */
    public void save() {
        NGGYU.PLUGIN.getConfig().set("enabled", ENABLED);
        NGGYU.PLUGIN.saveConfig();
    }
} // class Configuration