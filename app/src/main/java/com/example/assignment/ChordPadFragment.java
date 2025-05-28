package com.example.assignment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ChordPadFragment extends Fragment {

    public static double move_threshold = 50;
    public static final double MOVE_THRESHOLD_SQUARED = Math.pow(move_threshold,2);
    private static final String ARG_CHORD_INDEX = "chord_index";
    private static final String ARG_SELECTED_CHORDS = "selected_chords";
    private View padButtonView;
    private int chordIndex = 0;
    private OnChordPlayListener listener;
    private int[][] selectedChords;
    private View currentPadToChange;
    private static final int PICK_IMAGE_REQUEST = 1;



    public interface OnChordPlayListener {


        void onPlayChord(int[][] selectedChords, int index);
    }

    public ChordPadFragment() {}

    public static ChordPadFragment newInstance(int chordIndex,int[][] chords) {
        ChordPadFragment fragment = new ChordPadFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHORD_INDEX, chordIndex);
        int flat[] = new int[chords.length * chords[0].length];
        int cols = chords[0].length;
        for (int i = 0; i < chords.length; i++) {
            System.arraycopy(chords[i], 0, flat, i * cols, cols);
        }
        args.putIntArray(ARG_SELECTED_CHORDS, flat);
        args.putInt("rows", chords.length);
        args.putInt("cols", cols);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnChordPlayListener) {
            listener = (OnChordPlayListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChordPlayListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chord_button, container, false);
        padButtonView=rootView;
        if(padButtonView != null){
            padButtonView.setOnTouchListener(new View.OnTouchListener(){
                private static final int TAP_TIMEOUT = 200;


                private long firstTouchTime = -1;
                private float firstTouchX = -1;
                private float firstTouchY = -1;


                @Override
                public boolean onTouch(View v, MotionEvent event){
                    int action = event.getActionMasked();
                    int pointerCount = event.getPointerCount();
                    switch (action){
                        case MotionEvent.ACTION_DOWN:
                            if (pointerCount==1){
                                firstTouchTime = System.currentTimeMillis();
                                firstTouchX = event.getX();
                                firstTouchY = event.getY(); // Corrected in previous answer
                            }else{
                                firstTouchTime = -1;
                            }
                            break; // <-- Add this break

                        case MotionEvent.ACTION_POINTER_DOWN:
                            if(pointerCount==2) {
                                long currentTime = System.currentTimeMillis();
                                if (firstTouchTime != -1 && (currentTime - firstTouchTime <= TAP_TIMEOUT) && // Corrected in previous answer
                                        (Math.pow(event.getX(0)-firstTouchX,2)+Math.pow(event.getY(0)-firstTouchY,2)<MOVE_THRESHOLD_SQUARED) && // Corrected in previous answer
                                        (Math.pow(event.getX(1)-firstTouchX,2)+Math.pow(event.getY(1)-firstTouchY,2)<MOVE_THRESHOLD_SQUARED)) { // Corrected in previous answer
                                    changePadBackground(v); // Use the new method name
                                    firstTouchTime = -1;
                                    return true; // Consume the event!
                                }else{
                                    firstTouchTime = currentTime;
                                    firstTouchX=event.getX(0);
                                    firstTouchY=event.getY(0);
                                }
                            }else{
                                firstTouchTime=-1;
                            }
                            break; // <--- THIS IS THE MISSING BREAK! Add this here.

                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_POINTER_UP:
                            if (pointerCount < 2){
                                firstTouchTime= -1;
                            }
                            break;

                        case MotionEvent.ACTION_MOVE: // You had this case, which is good
                            // Optional: If fingers move too far, cancel tap detection
                            if (firstTouchTime != -1) {
                                float dx0 = event.getX(0) - firstTouchX;
                                float dy0 = event.getY(0) - firstTouchY;
                                if ((dx0 * dx0 + dy0 * dy0) > MOVE_THRESHOLD_SQUARED) {
                                    firstTouchTime = -1;
                                }
                            }
                            break;
                    }
                    return false;
                }
            });
        }
        return rootView;
    }

    private void changePadBackground(View padView){
        currentPadToChange = padView;

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args == null) return;
        chordIndex = args.getInt(ARG_CHORD_INDEX);
        int[] flat = args.getIntArray(ARG_SELECTED_CHORDS);
        int rows = args.getInt("rows",0);
        int cols = args.getInt("cols",0);
        selectedChords = new int[rows][cols];
        if (flat !=null && rows>0 && cols>0) {
            for (int i = 0; i < rows; i++) {
                System.arraycopy(flat, i * cols, selectedChords[i], 0, cols);
            }
        }

        Button chordButton = view.findViewById(R.id.chord_button);
        chordButton.setText("Chord " + (chordIndex + 1)); // Just for labeling
        chordButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlayChord(selectedChords,chordIndex);
            }
        });
    }
    public void setSelectedChords(int[][] selectedChords) {
        this.selectedChords = selectedChords;
    }
}
