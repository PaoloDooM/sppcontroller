package com.paolodoom.sppcontroller.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

public class MidiService {

    private static final int UNLIMITED = -1;

    enum COMMANDS {
        noteOn(144),
        noteOff(128),
        /*PARAMS*/controlChange(176),
        /*BANKS*/programChange(192);

        private final int code;

        COMMANDS(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    private static final String[] sm_astrKeyNames = {"C", "C#", "D", "D#",
        "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    public static void entrypoint() throws Exception {
        String device = getMidiOutDriverList().get(3);
        System.out.println("Selected device :" + device);
        //sendMidiMessage(device, COMMANDS.noteOn.getCode(), 1, sm_astrKeyNames[0], 1, 50);
        //sendMidiMessage(device, COMMANDS.noteOff.getCode(), 1, sm_astrKeyNames[0], 1, 50);
        if(allowsReceivers(getMidiDevice(device, "OUT"))){
            sendMidiMessage(device, COMMANDS.programChange.getCode(), 4, 1, 1);
        }else{
            System.out.println("Dont allow Receivers");
        }
    }

    public static List<String> getMidiOutDriverList() {
        Info[] infoArray = MidiSystem.getMidiDeviceInfo();
        List<String> deviceList = new ArrayList<String>();
        for (Info info : infoArray) {
            try {
                MidiDevice device = MidiSystem.getMidiDevice(info);
                int recMax = device.getMaxReceivers();
                int trsMax = device.getMaxTransmitters();
                if (trsMax == 0 && (recMax == UNLIMITED || recMax > 0)) {
                    System.out.println("Adding device :" + info.getName());
                    deviceList.add(info.getName());
                }
            } catch (MidiUnavailableException ex) {
                System.out.println("Skip device :" + info.getName());
            }
        }
        return deviceList;
    }

    public static List<String> getMidiInDriverList() {
        Info[] infoArray = MidiSystem.getMidiDeviceInfo();
        List<String> deviceList = new ArrayList<String>();
        for (Info info : infoArray) {
            try {
                MidiDevice device = MidiSystem.getMidiDevice(info);
                int trsMax = device.getMaxTransmitters();
                if (trsMax == UNLIMITED || trsMax > 0) {
                    System.out.println("Adding device :" + info.getName());
                    deviceList.add(info.getName());
                }
            } catch (MidiUnavailableException ex) {
                System.out.println("Skip device :" + info.getName());
            }
        }
        return deviceList;
    }

    /**
     * Sends a midi message
     *
     * @param midiDeviceName
     * @param command
     * @param channel
     * @param controlNo
     * @param value
     * @throws InvalidMidiDataException
     * @throws MidiUnavailableException
     */
    public static void sendMidiMessage(String midiDeviceName, int command,
            int channel, int controlNo, int value)
            throws InvalidMidiDataException, MidiUnavailableException {
        ShortMessage message = new ShortMessage();

        message.setMessage(command, channel - 1, controlNo, value);
        MidiDevice device = getMidiDevice(midiDeviceName, "OUT");
        device.open();
        long timeStamp = device.getMicrosecondPosition();
        device.getReceiver().send(message, timeStamp);
        device.close();
    }

    /**
     * Sends a midi message
     *
     * @param midiDeviceName
     * @param command
     * @param channel
     * @param note
     * @param octave
     * @param velocity
     * @throws InvalidMidiDataException
     * @throws MidiUnavailableException
     */
    public static void sendMidiMessage(String midiDeviceName, int command,
            int channel, String note, int octave, int velocity)
            throws InvalidMidiDataException, MidiUnavailableException {
        sendMidiMessage(midiDeviceName, command, channel,
                getKeyNumber(note, octave), velocity);
    }

    /**
     * Gets a midi device by name
     *
     * @param midiDeviceName The name of the midi device
     * @param direction
     * @return The midi device
     * @throws MidiUnavailableException If the midi device can not be found
     */
    public static MidiDevice getMidiDevice(String midiDeviceName,
            String direction) throws MidiUnavailableException {

        MidiDevice.Info[] midiInfos;
        MidiDevice device = null;

        if (midiDeviceName != null) {

            midiInfos = MidiSystem.getMidiDeviceInfo();

            for (int i = 0; i < midiInfos.length; i++) {
                if (midiInfos[i].getName().equals(midiDeviceName)) {
                    device = MidiSystem.getMidiDevice(midiInfos[i]);

                    if (getDirectionOfMidiDevice(device).equals(direction)) {
                        return device;
                    }
                }
            }

        }
        throw new MidiUnavailableException();
    }

    /**
     * Gets the byte value for a key name and a octave
     *
     * @param note The key name
     * @param octave The octave
     * @return The byte value, -1 if no key was found
     */
    private static int getKeyNumber(String note, int octave) {

        int nOctave = (octave + 2) * 12;

        int nNote = -1;
        for (int i = 0; i < sm_astrKeyNames.length; i++) {
            if (sm_astrKeyNames[i].equals(note)) {
                nNote = i;
                break;
            }
        }

        if (nNote == -1) {
            return -1;
        }

        return nOctave + nNote;
    }

    /**
     * Retrieve a MidiDevice.Info for a given name.
     *
     * This method tries to return a MidiDevice.Info whose name matches the
     * passed name. If no matching MidiDevice.Info is found, null is returned.
     * If bForOutput is true, then only output devices are searched, otherwise
     * only input devices.
     *
     * @param strDeviceName the name of the device for which an info object
     * should be retrieved.
     * @param bForOutput If true, only output devices are considered. If false,
     * only input devices are considered.
     * @return A MidiDevice.Info object matching the passed device name or null
     * if none could be found.
     */
    public static MidiDevice.Info getMidiDeviceInfo(String strDeviceName,
            boolean bForOutput) {

        MidiDevice.Info[] aInfos = MidiSystem.getMidiDeviceInfo();

        for (int i = 0; i < aInfos.length; i++) {

            if (aInfos[i].getName().equals(strDeviceName)) {

                try {
                    MidiDevice device = MidiSystem.getMidiDevice(aInfos[i]);
                    boolean bAllowsInput = (device.getMaxTransmitters() != 0);
                    boolean bAllowsOutput = (device.getMaxReceivers() != 0);
                    if ((bAllowsOutput && bForOutput)
                            || (bAllowsInput && !bForOutput)) {
                        return aInfos[i];
                    }

                } catch (MidiUnavailableException e) {
                    // TODO:
                }
            }
        }
        return null;
    }

    /**
     * Returns the allowed direction of the given midi device
     *
     * @param device The midi device
     * @return "IN" for inbound devices, "OUT" for outbound devices, else <NULL>
     */
    public static String getDirectionOfMidiDevice(MidiDevice device) {

        if (device.getMaxTransmitters() != 0) {
            return "IN";
        }

        if (device.getMaxReceivers() != 0) {
            return "OUT";
        }
        return null;
    }
    
        public static boolean allowsReceivers(MidiDevice.Info deviceInfo)
            throws MidiUnavailableException {
        return allowsReceivers(getMidiDevice(deviceInfo));
    }

    public static boolean allowsReceivers(MidiDevice device) {
        return probe(device.getMaxReceivers())
                || probe(device.getReceivers());
    }

    public static MidiDevice getMidiDevice(MidiDevice.Info deviceInfo)
            throws MidiUnavailableException {
        try {
            return MidiSystem.getMidiDevice(deviceInfo);
        } catch (IllegalArgumentException e) {
            // the device does not exist anymore - 
            // this seems to be a bug because MidiUnavailableException should
            // be thrown instead
            MidiUnavailableException m = new MidiUnavailableException();
            m.initCause(e);
            throw m;
        }
    }

    private static boolean probe(Collection<?> receiverTransmitterList) {
        return !receiverTransmitterList.isEmpty();
    }

    private static boolean probe(int receivertransmitter) {
        return receivertransmitter == -1 || receivertransmitter > 0;
    }
    
    /**
     * holds the state if a SysexMessage is completely displayed or
     * shorten to one hexdump line. (Complete Sysex Message)
     */
    private static boolean CSMstate = false;
    
    /**
     * Send a <code>MidiMessage</code>. Whole message is passed to lower MIDI driver.
     * @param rcv MIDI Receiver
     * @param msg MIDI Message
     * @throws MidiUnavailableException
     * @throws InvalidMidiDataException
     */
    public static void send(Receiver rcv, MidiMessage msg)
            throws MidiUnavailableException, InvalidMidiDataException {
        rcv.send(msg, -1);
        System.out.println("XMIT: " + msg.toString());
    }
    
    /**
     * Send a <code>MidiMessage</code>. A Sysex Message is divided into
     * several Sysex Messages whose size is <code>bufSize</code>.
     * 
     * @param rcv
     *            MIDI Receiver
     * @param msg
     *            MIDI Message
     * @param bufSize
     *            MIDI message size. If zero, whole MIDI message is passed to
     *            lower MIDI driver.
     * @param delay
     *            delay (msec) after every MIDI message transfer.
     * @throws MidiUnavailableException
     * @throws InvalidMidiDataException
     */
    public static void send(Receiver rcv, MidiMessage msg, int bufSize,
            int delay) throws MidiUnavailableException,
            InvalidMidiDataException {
        int size = msg.getLength();

        if (bufSize == 0 || size <= bufSize) {
            rcv.send(msg, -1);
            System.out.println("XMIT: " + msg.toString());
        } else {
            // divide large System Exclusive Message into multiple
            // small messages.
            byte[] sysex = msg.getMessage();
            byte[] tmpArray = new byte[bufSize + 1];
            for (int i = 0; size > 0; i += bufSize, size -= bufSize) {
                int s = Math.min(size, bufSize);

                if (i == 0) {
                    System.arraycopy(sysex, i, tmpArray, 0, s);
                    ((SysexMessage) msg).setMessage(tmpArray, s);
                } else {
                    tmpArray[0] = (byte) SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE;
                    System.arraycopy(sysex, i, tmpArray, 1, s);
                    ((SysexMessage) msg).setMessage(tmpArray, ++s);
                }
                rcv.send(msg, -1);
                System.out.println("XMIT: " + msg.toString());
                try {
                    Thread.sleep(delay);
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
    }
    
    /**
     * Return a <code>String</code> of the name of status byte of a
     * <code>MidiMessage</code>.
     *
     * @param m a <code>MidiMessage</code> value
     * @return a <code>String</code> value
     * @exception InvalidMidiDataException if an error occurs
     */
    public static String statusString(MidiMessage m)
            throws InvalidMidiDataException {
        int c = m.getStatus();
        switch (c < 0xf0 ? c & 0xf0 : c) {
        case 0x80:
            return "Note Off";
        case 0x90:
            return "Note On";
        case 0xa0:
            return "Poly Pressure";
        case 0xb0:
            return "Control Change";
        case 0xc0:
            return "Program Change";
        case 0xd0:
            return "Channel Pressure";
        case 0xe0:
            return "Pitch Bend";
        case 0xf0:
            return "System Exclusive";
        case 0xf1:
            return "MIDI Time Code";
        case 0xf2:
            return "Song Position Pointer";
        case 0xf3:
            return "Song Select";
        case 0xf4:
            return "Undefined";
        case 0xf5:
            return "Undefined";
        case 0xf6:
            return "Tune Request";
            //case 0xf7: return "End of System Exclusive";
        case 0xf7:
            return "Special System Exclusive";
        case 0xf8:
            return "Timing Clock";
        case 0xf9:
            return "Undefined";
        case 0xfa:
            return "Start";
        case 0xfb:
            return "Continue";
        case 0xfc:
            return "Stop";
        case 0xfd:
            return "Undefined";
        case 0xfe:
            return "Active Sensing";
        case 0xff:
            return "System Reset";
        default:
            throw new InvalidMidiDataException();
        }
    }
}
