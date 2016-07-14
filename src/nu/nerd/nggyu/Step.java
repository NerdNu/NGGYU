package nu.nerd.nggyu;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.event.block.NotePlayEvent;

// ----------------------------------------------------------------------------
/**
 * Represents one step in a {@link Tune}, comprising a Tone an Instrument and a
 * time at which the tone is played.
 */
public class Step {
    // ------------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param time The time, in seconds, relative to the start of the tune, at
     *        which to play this step.
     * @param instrument The Instrument in which to play the Tone.
     * @param note The Note to play.
     */
    public Step(double time, Instrument instrument, Note note) {
        _time = time;
        _instrument = instrument;
        _note = note;
    }

    // ------------------------------------------------------------------------
    /**
     * Perform this step, replacing the note in an event.
     *
     * TODO: Always cancel the event and play with playSound().
     *
     * @param event the event raised when a noteblock plays a note.
     * @param elapsedSeconds the time elapsed relative to the start of the tune,
     *        in seconds.
     * @return true if the note of the event is replaced, or false if it is
     *         silenced.
     */
    public boolean replace(NotePlayEvent event, double elapsedSeconds) {
        if (elapsedSeconds < _time) {
            return false;
        } else {
            event.setInstrument(_instrument);
            event.setNote(_note);
            return true;
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Return the String representation, for debugging, which matches the
     * serialised form.
     *
     * @return the String representation
     */
    @Override
    public String toString() {
        // Recall B# == Cb. API only records isSharped() or not.
        String suffix = _note.isSharped() ? "#" : "";
        return "" + _time + " " + _instrument + " " +
               _note.getOctave() + " " + _note.getTone() + suffix;
    }

    // ------------------------------------------------------------------------
    /**
     * The time, in seconds, relative to the start of the tune, at which to play
     * this step.
     */
    double _time;

    /**
     * The Instrument in which to play the Tone.
     */
    Instrument _instrument;

    /**
     * The Note to play.
     */
    Note _note;
} // class Step