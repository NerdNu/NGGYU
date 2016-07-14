package nu.nerd.nggyu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.plugin.java.JavaPlugin;

// --------------------------------------------------------------------------
/**
 * Plugin class.
 */
public class NGGYU extends JavaPlugin implements Listener {
    /**
     * Singleton-like reference to this plugin.
     */
    public static NGGYU PLUGIN;

    /**
     * Configuration instance.
     */
    public static Configuration CONFIG = new Configuration();

    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {
        PLUGIN = this;

        saveDefaultConfig();
        CONFIG.reload();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    // ------------------------------------------------------------------------
    /**
     * When a note plays in the area of interest, either suppress or modify it
     * to hold the desired tune.
     */
    @EventHandler(ignoreCancelled = true)
    public void onNotePlay(NotePlayEvent event) {
        if (_debugStartTime == 0) {
            _debugStartTime = System.currentTimeMillis();
        }

        if (CONFIG.ENABLED) {
            Location loc = event.getBlock().getLocation();
            for (Tune tune : CONFIG.TUNES) {
                if (tune.isActive(loc)) {
                    Step step = tune.replace(event);
                    if (CONFIG.DEBUG_EVENT && step != null) {
                        getLogger().info("Played: " + step);
                    }
                }
            }
        } else {
            if (CONFIG.DEBUG_EVENT) {
                double elapsed = 0.001 * (System.currentTimeMillis() - _debugStartTime);
                double quantised = (int) (elapsed * 16) / 16.0;
                getLogger().info("Played: " +
                                 quantised + " " +
                                 event.getInstrument() + " " +
                                 event.getNote().getOctave() + " " +
                                 event.getNote().getTone() +
                                 (event.getNote().isSharped() ? "#" : ""));
            }
        }
    }

    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender,
     *      org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(getName())) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                CONFIG.reload();
                sender.sendMessage(ChatColor.GOLD + getName() + " configuration reloaded.");
                return true;
            }
        } else if (command.getName().equalsIgnoreCase("giveyouup")) {
            CONFIG.ENABLED = !CONFIG.ENABLED;
            CONFIG.save();
            sender.sendMessage(ChatColor.GOLD + getName() + (CONFIG.ENABLED ? " enabled." : " disabled."));
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Invalid command syntax.");
        return true;
    }

    // ------------------------------------------------------------------------
    /**
     * Time of the first note event, used for debugging tunes.
     */
    protected long _debugStartTime;
} // class NGGYU