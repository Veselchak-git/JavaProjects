package MusicApp;

import java.util.ArrayList;

import javax.sound.midi.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;


public class MusicApp implements Serializable {
    JPanel mainPanel;
    ArrayList<JCheckBox> checkboxList;
    Sequencer sequencer;
    Sequence sequence;
    Track track;
    JFrame frame;

    String[] musicalToolsNames = {
        "Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap",
        "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap",
        "Low-mid Tom", "High Agogo", "Open Hi Conga"
    };
    int[] musicalTools = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};
    public class StartListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            buildTrackAndStart();
        }
    }

    public class StopListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            sequencer.stop();
        }
    }

    public class UpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));
        }
    }

    public class DownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 0.97));
        }
    }

    public class SendListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            boolean[] checkboxState = new boolean[256];
            
            for(int i = 0; i < 256; i++) {
                JCheckBox checkbox = (JCheckBox) checkboxList.get(i);
                if (checkbox.isSelected()) {
                    checkboxState[i] = true;
                }
            }
            JFileChooser fileSave = new JFileChooser();
            fileSave.showSaveDialog(frame);

            try {
                FileOutputStream fileStream = new FileOutputStream(fileSave.getSelectedFile());
                ObjectOutputStream os = new ObjectOutputStream(fileStream);
                os.writeObject(checkboxState);
                os.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public class ReadInListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            boolean[] checkboxState = null;

            JFileChooser fileOpen = new JFileChooser();
            fileOpen.showOpenDialog(frame);
            
            try {
                FileInputStream fileIn = new FileInputStream(fileOpen.getSelectedFile());
                ObjectInputStream is = new ObjectInputStream(fileIn);
                checkboxState = (boolean[]) is.readObject();
                is.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            for(int i = 0; i < 256; i++) {
                JCheckBox checkbox = (JCheckBox) checkboxList.get(i);
                if (checkboxState[i]) {
                    checkbox.setSelected(true);
                }
                else {
                    checkbox.setSelected(false);
                }
            }
            sequencer.stop();
            buildTrackAndStart();
        }
    }

    public void setUpMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public MidiEvent makeEvent(int command, int channel, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(command, channel, one, two);
            event = new MidiEvent(message, tick);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return event;
    }

    public void makeTracks(int[] list) {
        for (int i = 0; i < 16; i++) {
            int key = list[i];

            if (key != 0) {
                track.add(makeEvent(144, 9, key, 100, i));
                track.add(makeEvent(128, 9, key, 100, i+1));
            }
        }
    }

    public void buildTrackAndStart() {
        int[] trackList = null;

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (int i = 0; i < 16; i++) {
            trackList = new int[16];
            int key = musicalTools[i];

            for (int j = 0; j < 16; j++) {
                JCheckBox checkbox = (JCheckBox) checkboxList.get(j + (16*i));
                if (checkbox.isSelected()) {
                    trackList[j] = key;
                }
                else {
                    trackList[j] = 0;
                }
            }
            makeTracks(trackList);
            track.add(makeEvent(176, 1, 127, 0, 16));
        }
        track.add(makeEvent(192, 9, 1, 0, 15));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void buildGUI() {
        frame = new JFrame("MusicApp");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        checkboxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Start");
        start.addActionListener(new StartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new StopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(new UpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(new DownTempoListener());
        buttonBox.add(downTempo); 

        JButton sendIt = new JButton("Save");
        sendIt.addActionListener(new SendListener());
        buttonBox.add(sendIt);

        JButton restore = new JButton("Open");
        restore.addActionListener(new ReadInListener());
        buttonBox.add(restore);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++) {
            nameBox.add(new Label(musicalToolsNames[i]));
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        frame.getContentPane().add(background);

        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        for (int i = 0; i < 256; i++) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setSelected(false);
            checkboxList.add(checkBox);
            mainPanel.add(checkBox);
        }

        setUpMidi();

        frame.setBounds(50, 50, 300, 300);
        frame.pack();
        frame.setVisible(true);
    }
}
