package com.example.assignment;
import android.content.Context;
import android.media.midi.*;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api= Build.VERSION_CODES.M)
public class MidiNoteGenerator {
    private MidiManager midiManager;
    private MidiReceiver receiver;
    public MidiNoteGenerator(Context ctxt){
        midiManager = (MidiManager) ctxt.getSystemService(Context.MIDI_SERVICE);
    }
}
