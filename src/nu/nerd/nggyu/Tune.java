package nu.nerd.nggyu;

import java.util.ArrayList;

import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.NotePlayEvent;

// ----------------------------------------------------------------------------
/**
 * Describes a tune to be played.
 *
 * Tunes store a list of {@link Step}s describing notes to play and when to play
 * them relative to the start of the tune.
 *
 * The current implementation of {@link Step#replace(NotePlayEvent, double)}
 * replaces the fields in the event with a single new note, meaning that only
 * one note will be played. However, when that method is reimplemented to play
 * the sound itself, we will be able to support chords by setting the same time
 * stamp for several notes.
 */
public class Tune {
    // ------------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param section the section from which the tune is loaded.
     */
    public Tune(ConfigurationSection section) {
        _location = (Location) section.get("location");
        _radius = section.getInt("radius");
        _rest = section.getDouble("rest");

        if (NGGYU.CONFIG.DEBUG_CONFIG) {
            NGGYU.PLUGIN.getLogger().info(_location.getWorld().getName() + ", " +
                                          _location.getBlockX() + ", " +
                                          _location.getBlockY() + ", " +
                                          _location.getBlockZ() + ", " +
                                          "radius " + _radius + ", rest " + _rest);
        }

        for (String entry : section.getStringList("notes")) {
            try {
                String[] parts = entry.split("\\s+");
                double time = Double.parseDouble(parts[0]);
                Instrument instrument = Instrument.valueOf(parts[1]);
                int octave = Integer.parseInt(parts[2]);

                Note note = null;
                if (parts[3].length() >= 1) {
                    Tone tone = Tone.valueOf(parts[3].substring(0, 1));
                    if (parts[3].length() == 1) {
                        note = Note.natural(octave, tone);
                    } else if (parts[3].length() == 2) {
                        if (parts[3].charAt(1) == '#') {
                            note = Note.sharp(octave, tone);
                        } else if (parts[3].charAt(1) == 'b') {
                            note = Note.flat(octave, tone);
                        }
                    }
                }

                if (note != null) {
                    Step step = new Step(time, instrument, note);
                    if (NGGYU.CONFIG.DEBUG_CONFIG) {
                        NGGYU.PLUGIN.getLogger().info(step.toString());
                    }
                    _steps.add(step);
                } else {
                    NGGYU.PLUGIN.getLogger().warning("Invalid note specifier: " + octave + " " + parts[3]);
                }
            } catch (Exception ex) {
                NGGYU.PLUGIN.getLogger().warning("Unable to parse entry in " +
                                                 section.getName() + " \"" + entry + "\" " + ex.getMessage());
            }
        } // for
    } // Tune

    // ------------------------------------------------------------------------
    /**
     * Return true if this Tune affects noteblocks at the specified Location.
     *
     * @param loc the location.
     * @return true if this Tune affects noteblocks at the specified Location.
     */
    public boolean isActive(Location loc) {
        return loc.getWorld().equals(_location.getWorld()) &&
               loc.distance(_location) <= _radius;
    }

    // ------------------------------------------------------------------------
    /**
     * Replace or silence the note played by the specified event with this tune.
     *
     * @param event the event describing a noteblock note.
     * @return the last played {@link Step}.
     */
    public Step replace(NotePlayEvent event) {
        long now = System.currentTimeMillis();
        if (_startTime == 0) {
            _startTime = now;
        }
        double elapsedSeconds = 0.001 * (now - _startTime);

        Step playedStep = null;
        while (_nextStep < _steps.size()) {
            Step step = _steps.get(_nextStep);
            if (!step.replace(event, elapsedSeconds)) {
                break;
            }
            playedStep = step;
            ++_nextStep;
        }

        if (playedStep == null) {
            event.setCancelled(true);
        }

        if (_nextStep >= _steps.size()) {
            _nextStep = 0;
            _startTime = now + (long) (1000 * _rest);
        }
        return playedStep;
    }

    // ------------------------------------------------------------------------
    /**
     * Centre of the volume in which this Tune overrides notblocks.
     */
    protected Location _location;

    /**
     * Radius of the volume in which this Tune overrides notblocks.
     */
    protected int _radius;

    /**
     * Steps to play this Tune.
     */
    protected ArrayList<Step> _steps = new ArrayList<Step>();

    /**
     * The duration, in seconds, of silent pause between repetitions of this
     * tune.
     */
    protected double _rest;

    /**
     * System time when this tune started.
     */
    protected long _startTime;

    /**
     * Index into _steps of next step to play.
     */
    protected int _nextStep;
} // class Tune