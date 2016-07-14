NGGYU
=====
Never Gonna Give You Up.

NGGYU is a Bukkit plugin that replaces noteblock notes within defined volumes
with volume-specific tunes.  For the tune to play, players must click on the
noteblocks repeatedly or power them with a redstone source, e.g. a lever.


Commands
--------
 * `/nggyu reload` - Reload the configuration.
 * `/giveyouup` - Toggle operation of the plugin (on or off). When disabled,
   noteblock notes are not modified at all.


Configuration
-------------
 * `enabled` - If true, noteblock notes are replaced in defined volumes.
 * `debug.config` - If true configuration settings and tunes are logged when
   loaded.
 * `debug.event` - If true, replacement notes or played noteblock notes are
   logged when played.
 * `tunes` - A map where the top level key identifies a distinct tune and
   volume where that tune is in effect.

There can be any number of tunes, each of which has its own volume-of-effect.
The format of a tune is as follows:

 * `tunes.<tune-name>.location` - Location of the centre of the volume-of-effect
   in Bukkit `ConfigurationSerializable` format.
 * `tunes.<tune-name>.radius` - The radius of the spherical volume-of-effect.
 * `tunes.<tune-name>.rest` - The silent period, in seconds, between repetitions
   of the tune.
 * `tunes.<tune-name>.notes` - A list of strings defining the notes.

Each note string consists of four fields, separated by spaces.

 * `<time>` - The time offset, in seconds, since the start of the tune. The
   format does not preclude playing multiple notes at the same time (chords),
   but it is not currently implemented.
 * `<instrument>` - An org.bukkit.Instrument enum value; one of BASS_DRUM,
   BASS_GUITAR, PIANO, SNARE_DRUM or STICKS.
 * `<octave>` - The octave number from 0 to 2, with higher values corresponding
   to higher pitch.
 * `<tone>` - A capital letter A through G, with an optional sharp (#) or flat
   (b) suffix, e.g. A, C#, Db.


Permissions
-----------
 * `nggyu.admin` - Permission to run `/nggyu reload`.
 * `nggyu.enable` - Permission to run `/giveyouup`.
