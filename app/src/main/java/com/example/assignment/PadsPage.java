package com.example.assignment;

// Import necessary Android and Java classes. These classes provide
// functionalities for UI (TextView, View, MenuItem), app components (Context, Intent, Bundle, AppCompatActivity),
// audio (AudioAttributes, AudioManager, SoundPool), MIDI (MidiManager),
// logging (Log), utility functions (ArrayList, List, Objects),
// UI layout management (Toolbar, DrawerLayout, NavigationView, EdgeToEdge, Insets, ViewCompat, WindowInsetsCompat, GravityCompat),
// fragment management (FragmentTransaction, ChordPadFragment), and
// network operations (Retrofit, Callback, Call, Response, GsonConverterFactory).
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



//It extends AppCompatActivity, providing modern Android
// Activity features and backwareds compatibility with older Android
//versions. Itimplements ChordpPad Fragment.OnChordPlayListener, therefore
//it can receive callbacks from the ChordPadFragment
//when a chord needs to be played.
public class PadsPage extends AppCompatActivity implements ChordPadFragment.OnChordPlayListener{
    //For playing audio clips efficiently.
    private SoundPool soundPool;

    //stores the ID of the loaded instrument sound (e.g. guitar, rhodes)
    private int soundId = 0;


    //A flag to ensure the sound is loaded before attempting to play it
    private boolean isLoaded = false;
    //The main layout that supports the navigation drawer.
    private DrawerLayout drawerLayout;
    //The view that holds the navigation menu items(slide-out menu)
    private NavigationView navView;

    //An instance of the Retrofit service interface for the MusicBrainZ API calls
    private MusicBrainzApiService apiService;

    //A textvieo to display fetched facts from MusicBrainz
    public TextView factTextView;
    //Predefined major progression(represented by numbers  of scale degreea)
    //Example: {1,4,5,6} would be a chord chord progression of I-IV-V-vi
    int [][] majorProgressions = {
            {1,4,5,6},
            {1,6,2,5},
            {1,3,6,4}

    };
    //Defines minor chord Progressions
    int [][] minorProgressions = {
            {1,7,6,5},
            {1,3,4,5}

    };
    //Defines the semitone intervals of the major scale
    //Example in C Major
    // 0=C, 2=D, 4=E, 5=F, 7=G, 9=A, 11=B.
    private int scale[] = {0,2,4,5,7,9,11,12,14,16,17,19,21};
        Bundle extras;
//    private int chord0[] = {0,4,7};
//    private int chord1[] ={0,5,9};
//    private int chord2[]={2,7,11};
//    private int chord3[]={0,4,9};
    //The offset value in semitones is used to transpose the entire chord progression.
    private int offset =0;

    //This method is called when the Activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Retreive any extras (data) passed from the MainActivity
        extras = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        //Edge to edge is enabled,
        EdgeToEdge.enable(this);
        //Set the layout for this activity from the xml file
        setContentView(R.layout.pads_page);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // For Android 11 (API 30) and above
            getWindow().setDecorFitsSystemWindows(false); // Let content draw behind system bars
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                // Hide the status bar
                insetsController.hide(WindowInsets.Type.statusBars());
                insetsController.hide(WindowInsets.Type.navigationBars());

                getWindow().setDecorFitsSystemWindows(false);
                //Make sure the status and system bars appear on swipe from edge
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                // Or if you want them to be entirely hidden unless you explicitly show them
                // insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_TOUCH);
            }
        }
        //Get the OFFSET value from the extras, default to 0 if not found.
        offset = extras.getInt("OFFSET",0);
        //declare an array to hold the chosen chord progression
        int selected[];
        //check if the intent has passed the "Minor" string as extra
        if (extras.getString("MODE","Major").equals("Minor")){
            //If the mode is set to minor, then set the scale array to represent the
            //minor scale in semitones
            // 0=C, 2=D, 3=Eb, 5=F, 7=G, 8=Ab, 10=Bb.
            scale = new int[]{0, 2, 3, 5, 7, 8, 10,12,14,15,17,19};
            //Randomly select one of the minor chord progressions
            selected = minorProgressions[(int)(Math.random()*minorProgressions.length)];
        }else {
            //Otherwise (i.e. if the user picked major mode), randomly selectone of the major chord progressions
            selected = majorProgressions[(int)(Math.random()*majorProgressions.length)];
        }
        //Get a reference to the TextView that will display facts.
        factTextView = findViewById(R.id.factView);
        //Get a reference to the Toolbar defined in the layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        //Initialise Retrofir for notwork requests to MusicBrainzAPI
        Retrofit rf = new Retrofit.Builder()
                //Set the base URL for the API
                .baseUrl("https://musicbrainz.org/")
                        //Add a converter to automatically parse Json responses
                        //into java onjects
                        .addConverterFactory(GsonConverterFactory.create())
                                .build();
        //Create an instance of the API service interface.
        apiService = rf.create(MusicBrainzApiService.class);
        //Fetch a random artist from MusicBrainz API
        fetchRandomArtistsFact();

        //Sets the Toolbar as the Activity's Action Bar
        setSupportActionBar(toolbar);
        //Get a reference to the Drawer Layout
        drawerLayout = findViewById(R.id.drawer_layout);
        //Get a reference to the NavigationView
        navView = findViewById(R.id.nav_bar);

        //Ensure the Action Bar exists, then enable the Home Button (Default to the <- button)
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //Replace the icon of the home button with the hamburger icon
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger_menu);

        //Set a listener for when an item in the navigation drawer is selected.
        navView.setNavigationItemSelectedListener(item->{
            Intent intent = null;//Declare an Intent to navigate to a new Activity.
            int id = item.getItemId();//Get the ID of the selected menu item.
            if(id==R.id.nav_main){

                intent = new Intent(this, MainActivity.class);//Create for the MainActivity.
                //If the selected item is "pads"
            }else if(id==R.id.pads){

                intent = new Intent(this, PadsPage.class); //create a new intent for PadsPage.
            }
            //if an intent was created. (i.e. if a navigation item was selected).
            if (intent != null) {
                startActivity(intent);//Start the new Activity.
            }
            //Close the navigation drawer after an item is selected.
            drawerLayout.closeDrawer(GravityCompat.START);
            //Indicate that the event was handled
            return true;
        });
        //Initialise an array to hold the four specific chords for the pads.
        int[][] selectedChords = new int[4][];
        //Loop through the chosen progression (e.g., I-IV-V-vi)
        for(int i = 0; i<4; i++){
            //Generate each chord using the getChord method
            //selected [i]-1 converts a 1-based number to a 0-based array index
            //The second argument checks if it's  the 5th degree
            //as this might trigger altered extensions in getChord for dominant 7th
            selectedChords[i] = getChord(selected[i] - 1,(selected[i]==5));
        }
            //Initialised SoundPool based on Android version for compatibility
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //For lollipop or newer: use SoundPool.Builder for better control
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC) //Specify audio Type
                    .setUsage(AudioAttributes.USAGE_GAME) //Specify Usage (game is used for low_latency sounds)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(7)//Allow up to 7 sounds to play simultaneously.
                    .setAudioAttributes(audioAttributes) // Apply audio attributes.
                    .build();
        }else{
            //For older Android Versions: use the deprecated Sountpool constructor
            soundPool = new SoundPool(7, AudioManager.STREAM_MUSIC,0);
        }
        //Load the selected instrument sound into SoundPool
        try{
            //Get instrument name from the extras found in the intent.
            switch(extras.getString("INSTR")) {
                case "Piano"://if instrument is piano.
                soundId = soundPool.load(this, R.raw.piano, 1);//This code loads the piano sample.
                    //Sound by Le Amigo
                    //https://samplefocus.com/samples/one-key-shot-grand-piano-keys-dezz
                break;
                case "Rhodes":// If instrument is Rhodes
                    soundId = soundPool.load(this, R.raw.rhode, 1);//This code loads the rhodes sample
                    //sound by freesound_community
                    break;
                case "Guitar":// If instrument is Guitar
                    soundId = soundPool.load(this, R.raw.guitar, 1);//This code loads the guitar sample
                    //sound by Gabo Fernández
                    break;
            }
        }catch (Exception e){
            //Log errors if the sound loading fails
            Log.e("SoundPool","Sound loading failed",e);
        }
        //Set a listener that triggers when a sound has finished loading into soundpool
        soundPool.setOnLoadCompleteListener((sp,sampleId,status)->
        {
            if (status==0){ //status 0 means successfuly loaded
                isLoaded=true;//the is_loaded flag is set to true
            }
        });
        //This code creates four instances of the ChordPadFragment, wach representing a chord pad.
        //Pass their respective index and the array of the generated chords.
        ChordPadFragment fragment0 =ChordPadFragment.newInstance(0,selectedChords);
        ChordPadFragment fragment1 =ChordPadFragment.newInstance(1,selectedChords);
        ChordPadFragment fragment2 =ChordPadFragment.newInstance(2,selectedChords);
        ChordPadFragment fragment3 =ChordPadFragment.newInstance(3,selectedChords);
        //Begin a Fragment Transaction to add these fragments to the Activity's Layout.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //Replace placeholders in the layout(R.id.chord, etc.) with the created fragments
        ft.replace(R.id.chord0, fragment0);
        ft.replace(R.id.chord1, fragment1);
        ft.replace(R.id.chord2, fragment2);
        ft.replace(R.id.chord3, fragment3);
        //Commit the transaction to apply the changes

        ft.commit();

        //Set a listener to handle window insets
        //This ensures your content doesn't get hidden behind them.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_page), (v, insets) -> {
            //Get the insets for system bars
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            //Apply padding to the main content to match the system bar sizes.
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            //Return the insets to indicate they have been consumed
            return insets;

        });

    }
    //This method fetches a random artist fact from the MusicBrainz API
    private void fetchRandomArtistsFact(){
        //This code  gets retrofit API service.
        MusicBrainzApiService apiService = MusicBrainzClient.getClient();
        //Create an API call to search for artists of type "person" with tag rock
        Call<ArtistResponse> call = apiService.searchArtist("type:person",
                "json",
                100);
        //Enqueue the call to execute it asynchronously
        call.enqueue(new Callback<ArtistResponse>() {
            //Called when the API call successfully returns a response
            @Override
            public void onResponse(Call<ArtistResponse> call, Response<ArtistResponse> response) {
                //Check if the HTTP response was successful
                if (response.isSuccessful()){
                    //If the connection is successful then get a list of artists.
                    List<Artist> artists = response.body().getArtists();
                    //If artist lisst is not null and not empty
                    if(artists != null && !artists.isEmpty()){
                        //Select a random artist from the list.
                            Artist randomArtist = artists.get((int)(Math.random()*artists.size()));
                            //Display the fact about the selected artist
                            displayArtistFact(randomArtist);
                    }else{
                        //If no artists were found , update the TextView.
                        factTextView.setText("No artists found");
                    }
                }else{
                    //If response was not successful, update the TExtView with a Failure message.
                    factTextView.setText("Failed to load facts");
                }
            }
            //If api call fails
            @Override
            public void onFailure(Call<ArtistResponse> call, Throwable t) {
                //The app sets the text to an error messsage.
                factTextView.setText("Error loading fact: " + t.getMessage());
            }
        });
    }
    //Formats and displays an artist's fact in the TextView.
    private void displayArtistFact(Artist artist) {
        String fact = "Did you know about " + artist.getName() + "?\n\n";//Start with artist

//if the artist has a disambiguation, it is also added to the fact
        if (artist.getDisambiguation() != null) {
            fact += artist.getDisambiguation();
        }
//5 of its genres are listed
        if (artist.getTags() != null && !artist.getTags().isEmpty()) {
            fact+= "Genres: ";


            for (int i = 0; i < 5; i++){
                fact+= artist.getTags().get(i).getName()+", ";
            }
            //this removes the trailing "," from the last tag
            fact = fact.substring(0, fact.length() - 2);
        }
//Set the final fact text to the TextView
        factTextView.setText(fact);
    }
    //This plays the musical chord
    //the parameter int[] notes is an array of "notes" represented as integers
    public void chord(int[] notes){
        //Get the AudioManager system service
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);



        try {
            //for every integer in the array
            for (int note :notes) {
                //Play the sound of the sample,
                // pitched up by exactly (float) Math.pow(2,  (note+offset) / 12.0)
                //This formula transposes the base sound by (note+offset semitones)
                //where note is the specific note in the chord and the offset is the
                //root note
                //This is because playback speed is directly proportional with the
                //frequency .
                soundPool.play(soundId, 1.0f, 1.0f, 1, 0, (float) Math.pow(2,  (note+offset) / 12.0));

            }

        }catch(Exception e){
            Log.e("SoundPool","Playback Error",e);
        }
    }
    @Override
    protected void onDestroy(){

        if (soundPool != null){
            soundPool.release();
            soundPool=null;
        }
        super.onDestroy();
    }
    //This function generates a chord based on a root index and whether it is altered
    public int[] getChord(int rootIndex,boolean altered){
        rootIndex %=7;
        //Calculate the root, third and fifth of the chord using the single array and modulo 7 to wrap around.
        int root = scale[rootIndex]; //root
        int third = scale[(rootIndex + 2) % 7]; // third (which is two scale degrees above root)
        int fifth = scale[(rootIndex+4) % 7];// fifth (which is four scale degrees above root)
        //An arraylist can dynamically add notes to the chord
        ArrayList<Integer> notes = new ArrayList<>();
        notes.add(root);
        notes.add(third);
        notes.add(fifth);
        //checks if the user selected 7ths complexity in the intent
        if (Objects.requireNonNull(extras.getString("COMPL","Triads").equals("7ths"))){
            int seventh = scale[(rootIndex+6) % 7];//the seventh is 6 scale degrees up from the root
            notes.add(seventh);//add the seventh note
        }
        //Checks if the "Exts" otption is selected
        if (Objects.requireNonNull(extras.getString("COMPL","Triads").equals("Exts"))){
            int seventh = scale[(rootIndex+6) % 7];//the seventh is 6 scale degrees up from the root
            int ninth = scale[(rootIndex+8) % 7];//the seventh is 8 scale degrees up from the root

            int thirteenth = scale[(rootIndex+12) % 7];//the thirteenth is 12 scale degrees up from the root

            int degrees[] = {seventh,ninth,thirteenth};
            for (int degree : degrees){ //Randomly add some extentions(7th,9th,13th) with 66.6% probablility
                if ((int)(Math.random()*3)>=1){
                    notes.add(degree);
                }
            }
            //If the 'altered' flag is true
            //randomly add altered extentions
            if (altered){
                int flat9th = ninth-1;//Calculate flat 9th
                int sharp9th = ninth+1;//Calculate sharp 9th
                int flat13th = thirteenth-1;//Calculate flat 13th

                    //Randomly chose one altered extention to add
                    switch ((int)(Math.random()*3)) {
                        case 0:
                            notes.add(flat9th);//Add flat 9th
                            break;
                        case 1:
                            notes.add(sharp9th);//Add sharp 9th
                            break;
                        case 2:
                            notes.add(flat13th);//Add flat 13th
                            break;
                    }

            }
        }
        //Convert the arraylist of integer notes to a primitive int array and return it
        return notes.stream().mapToInt(Integer::intValue).toArray();

    }


//This is triggered by a ChordPadFragment when it's tapped.
    @Override
    public void onPlayChord(int[][] selectedChords, int index) {
        //Call the local chord() methods to play the specific chord corresponding to the tapped pad
        chord(selectedChords[index]);

    }
    //This method is called when an item in the Toolbar is selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Check if the "Home" icon is selected.
        if (item.getItemId() == android.R.id.home) {
            //open the navigation drawer from the start edge.
            drawerLayout.openDrawer(GravityCompat.START);
            //Indicate that the event has been handled.
            return true;
        }
        //If it is any other menu item, it is then handled by the superclass
        return super.onOptionsItemSelected(item);
    }
}