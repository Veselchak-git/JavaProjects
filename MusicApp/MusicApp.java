package MusicApp;

import javax.sound.midi.*;

public class MusicApp {
    public void play() {
        try {
            Sequencer player = MidiSystem.getSequencer();
            player.open();

            Sequence sequence = new Sequence(Sequence.PPQ, 4);

            Track track = sequence.createTrack();

            ShortMessage messageOne = new ShortMessage();
            messageOne.setMessage(144, 1, 44, 100);
            MidiEvent noteOn = new MidiEvent(messageOne, 1);
            track.add(noteOn);

            ShortMessage messageTwo = new ShortMessage();
            messageTwo.setMessage(128, 1, 44, 100);
            MidiEvent noteOff = new MidiEvent(messageTwo, 16);
            track.add(noteOff);

            player.setSequence(sequence);
            player.start();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
