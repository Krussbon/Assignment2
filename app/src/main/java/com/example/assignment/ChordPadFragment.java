package com.example.assignment;

//Import necessary Android and Java Classes
import android.content.Context;//Provides access to app-specific resources
import android.graphics.drawable.Drawable; // For working with graphical drawables
import android.os.Bundle;//Used for passing data between activities /fragments.

import androidx.activity.result.ActivityResultLauncher;//Modern API for launching activities and getting results
import androidx.activity.result.contract.ActivityResultContracts;//Standard contracts for common activity results
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;//Adding input stream

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChordPadFragment extends Fragment {
    //ActivityResultLauncher for picking an image from the device's content provider
    private ActivityResultLauncher<String> pickImageLauncher;
    // Stores a reference to the specific pat pad View whose background is currently being
    //changed
    private View currentPadToChange;//this variable tracks which pad to change

    private int pointerCount=0;
    //Threshold for detecting finger movement. If fingers move more than this,
    //it's not considered a tap
    public static double move_threshold = 125;
    //the squared value of the move_threshold used for faster distance calculations
    public static final double MOVE_THRESHOLD_SQUARED = Math.pow(move_threshold,2);
    //Bundle argument keys for passing data to the fragment
    private static final String ARG_CHORD_INDEX = "chord_index";
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    private static final String ARG_SELECTED_CHORDS = "selected_chords";
    //The root View of this fragment, which acts as the interactive chord pad.
    private View padButtonView;
    //Listener to communicate chord play events back to the hosting Activity
    private int chordIndex = 0;
    //Listener that communicates chord play events back to the hosting activity
    private OnChordPlayListener listener;
    //The 2D array of musical notes(integers representing semitone values)
    //for all chords
    private int[][] selectedChords;

    //Flag to indicate if a two-finger tap sequence has begun(i.e., ACTION_POINTER_DOWN)
    private boolean twoFingerTapSequenceStarted=false;


    //multi-touch gesture state variables
    //Time when the first finger touched down
    private long multiTouchStartTime = -1;
    //the X and y coordinates  of the first and second pointer(pointers 0 and 1 respectively)
    private float p0InitialX = -1, p0InitialY = -1;
    private float p1InitialX = -1, p1InitialY = -1;
    private static final int TWO_FINGER_TAP_TIMEOUT = 300;

    //Interface for communicating chord play events to the hosting Activity
    public interface OnChordPlayListener {


        void onPlayChord(int[][] selectedChords, int index);
    }
    //Empty public constructor for Fragment instantiation
    public ChordPadFragment() {}

    /*
     * Factory Method in order to create a new instance of ChordPadFragment with initial arguments
     * The chord index parameter is used for the chord index the pad represents.
     * Chords is the 2D array of all selected chords for the pads.
     * this returns a new instance of ChordPadFragment
     */
    public static ChordPadFragment newInstance(int chordIndex,int[][] chords) {

        ChordPadFragment fragment = new ChordPadFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHORD_INDEX, chordIndex);

        //Flatten the 2D chords array into a 1D array to pass it via Bundle
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
        //Ensure the hosting Activity implements OnChordPlayListener
        if (context instanceof OnChordPlayListener) {
            listener = (OnChordPlayListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChordPlayListener");
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
                super.onCreate(savedInstanceState);


        //Initialise the ActivityResultLauncher for picking images.
        //This lambda expression handles the result of the image picker.
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri->{
            //this is a lambda expression for the Activity Result Callback
                    //this checks if a uri was returned and a pad was designated for change.
                    if(uri!=null && currentPadToChange != null){
                        if(getContext()!=null){
                            try{
                                //Open an input stream form the URI to read the image data.
                                InputStream inputStream = getContext().getContentResolver().openInputStream(uri);

                                Drawable d = Drawable.createFromStream(inputStream,uri.toString());
                                currentPadToChange.setBackground(d);
                                //Close the input stream to release resources.
                                inputStream.close();
                            } catch (IOException e) {
                                //Log eny errors during image loading
                                Log.e("ImagePicker","Error loading image: "+e.getMessage());
                            }
                        }else{
                            //Log if context is null, indicating fragment detachment
                            Log.d("Image Picker", "ImageSelection has been cancelled");
                        }
                    }
                }
                );

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chord_button, container, false);
        padButtonView = rootView;




        return rootView;
    }
//initiates the process of changing the background of a chord pad by launching an image picker
    //param padview The view whose background needs to be changed.
    private void changePadBackground(View padView){
        currentPadToChange = padView;//Store the reference to the pad.

        pickImageLauncher.launch("image/*");//Launch the picker, requesting image content.


    }
    public void initiateBackgroundChange() {
        if (padButtonView != null) { // Use the fragment's own root view
            currentPadToChange = padButtonView; // Set the target for the image picker
            if (pickImageLauncher != null) {
                pickImageLauncher.launch("image/*");
                Log.d("ChordPadFragment", "Initiating background change for pad: " + chordIndex);
            } else {
                Log.e("ChordPadFragment", "pickImageLauncher is null in initiateBackgroundChange");
            }
        } else {
            Log.e("ChordPadFragment", "padButtonView is null in initiateBackgroundChange");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) return;// if there are no arguments, nothing is done

        //Retreive fragment arguments.
        chordIndex = args.getInt(ARG_CHORD_INDEX);
        int[] flat = args.getIntArray(ARG_SELECTED_CHORDS);
        int rows = args.getInt("rows",0);
        int cols = args.getInt("cols",0);

        //REconstruct the 2D selectedChords array from the flattened array
        selectedChords = new int[rows][cols];
        if (flat !=null && rows>0 && cols>0) {
            for (int i = 0; i < rows; i++) {
                System.arraycopy(flat, i * cols, selectedChords[i], 0, cols);
            }
        }

        //Get reference to the Button inside the fragment's layout.
        Button chordButton = view.findViewById(R.id.chord_button);
        //Set the text of the button(e.g. "Chord 1")
        chordButton.setText("Chord " + (chordIndex + 1)); // Just for labeling
        //The OnClick listener is set for single taps on the button
        chordButton.setOnClickListener(v -> {
            //if there is a listener set, notify the PadsPage
            //Activity to play the chord
            if (listener != null) {
                listener.onPlayChord(selectedChords,chordIndex);

            }
        });
        FloatingActionButton fab = view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> {
            initiateBackgroundChange();
        });

    }

    //Setter for updating the selected chords.
    public void setSelectedChords(int[][] selectedChords) {
        this.selectedChords = selectedChords;
    }
    /*
    * This is a helper method to reset all multi-touch gesture state variables to their inital values
    * This ensures that each new touch desture starts from a clean slate.
    */


}
