package com.example.assignment;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.midi.MidiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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


public class PadsPage extends AppCompatActivity implements ChordPadFragment.OnChordPlayListener{
    private SoundPool soundPool;
    private MidiManager midiManager;
    private int soundId = 0;
    private boolean isLoaded = false;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private MusicBrainzApiService apiService;

    public TextView factTextView;
    int [][] majorProgressions = {
            {1,4,5,6},
            {1,6,2,5},
            {1,3,6,4}

    };
    int [][] minorProgressions = {
            {1,7,6,5},
            {1,3,4,5}

    };
    private int scale[] = {0,2,4,5,7,9,11,12,14,16,17,19,21};
        Bundle extras;
//    private int chord0[] = {0,4,7};
//    private int chord1[] ={0,5,9};
//    private int chord2[]={2,7,11};
//    private int chord3[]={0,4,9};
    private int offset =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        extras = getIntent().getExtras();
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.pads_page);
        offset = extras.getInt("OFFSET",0);
        int selected[];
        if (extras.getString("MODE","Major").equals("Minor")){
            scale = new int[]{0, 2, 3, 5, 7, 8, 10,12,14,15,17,19,20};
            selected = minorProgressions[(int)(Math.random()*minorProgressions.length)];
        }else {
            selected = majorProgressions[(int)(Math.random()*majorProgressions.length)];
        }
        factTextView = findViewById(R.id.factView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Retrofit rf = new Retrofit.Builder()
                .baseUrl("https://musicbrainz.org/")
                        .addConverterFactory(GsonConverterFactory.create())
                                .build();
        apiService = rf.create(MusicBrainzApiService.class);
        fetchRandomArtistsFact();
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_bar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger_menu);
        navView.setNavigationItemSelectedListener(item->{
            Intent intent = null;
            int id = item.getItemId();
            if(id==R.id.nav_main){

                intent = new Intent(this, MainActivity.class);
            }else if(id==R.id.pads){
                intent = new Intent(this, PadsPage.class);
            }

            if (intent != null) {
                startActivity(intent);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        int[][] selectedChords = new int[4][];
        for(int i = 0; i<4; i++){
            selectedChords[i] = getChord(selected[i] - 1,(selected[i]-1==5));
        }
        midiManager=(MidiManager) getSystemService(Context.MIDI_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(7)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }else{
            soundPool = new SoundPool(7, AudioManager.STREAM_MUSIC,0);
        }
        try{
            switch(extras.getString("INSTR")) {
                case "Piano":
                soundId = soundPool.load(this, R.raw.piano, 1);
                break;
                case "Rhodes":
                    soundId = soundPool.load(this, R.raw.rhode, 1);
                    break;
                case "Guitar":
                    soundId = soundPool.load(this, R.raw.guitar, 1);
                    break;
            }
        }catch (Exception e){
            Log.e("SoundPool","Sound loading failed",e);
        }
        soundPool.setOnLoadCompleteListener((sp,sampleId,status)->
        {
            if (status==0){
                isLoaded=true;
            }
        });
        ChordPadFragment fragment0 =ChordPadFragment.newInstance(0,selectedChords);
        ChordPadFragment fragment1 =ChordPadFragment.newInstance(1,selectedChords);
        ChordPadFragment fragment2 =ChordPadFragment.newInstance(2,selectedChords);
        ChordPadFragment fragment3 =ChordPadFragment.newInstance(3,selectedChords);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.chord0, fragment0);
        ft.replace(R.id.chord1, fragment1);
        ft.replace(R.id.chord2, fragment2);
        ft.replace(R.id.chord3, fragment3);

        ft.commit();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

    }
    private void fetchRandomArtistsFact(){
        MusicBrainzApiService apiService = MusicBrainzClient.getClient();
        Call<ArtistResponse> call = apiService.searchArtist("type:person and tag:rock",
                "json",
                100);
        call.enqueue(new Callback<ArtistResponse>() {
            @Override
            public void onResponse(Call<ArtistResponse> call, Response<ArtistResponse> response) {
                if (response.isSuccessful()){
                    List<Artist> artists = response.body().getArtists();
                    if(artists != null && !artists.isEmpty()){
                            Artist randomArtist = artists.get((int)(Math.random()*artists.size()));
                            displayArtistFact(randomArtist);
                    }else{
                        factTextView.setText("No artists found");
                    }
                }else{
                    factTextView.setText("Failed to load facts");
                }
            }

            @Override
            public void onFailure(Call<ArtistResponse> call, Throwable t) {
                factTextView.setText("Error loading fact: " + t.getMessage());
            }
        });
    }
    private void displayArtistFact(Artist artist) {
        String fact = "Did you know about" + artist.getName() + "?\n";


        if (artist.getDisambiguation() != null) {
            fact += artist.getDisambiguation();
        }

        if (artist.getTags() != null && !artist.getTags().isEmpty()) {
            fact+= "Genres: ";
            for (Tag tag : artist.getTags()) {
                fact+=tag.getName()+", ";
            }
            fact = fact.substring(0, fact.length() - 2);
        }

        factTextView.setText(fact);
    }
    public void chord(int[] notes){
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);



        try {
            for (int note :notes) {
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
    public int[] getChord(int rootIndex,boolean altered){
        
        int root = scale[rootIndex % 7];
        int third = scale[(rootIndex + 2) % 7];
        int fifth = scale[(rootIndex+4) % 7];
        ArrayList<Integer> notes = new ArrayList<>();
        notes.add(root);
        notes.add(third);
        notes.add(fifth);
        if (Objects.requireNonNull(extras.getString("COMPL","Triads").equals("7ths"))){
            int seventh = scale[(rootIndex+6) % 7];
            notes.add(seventh);
        }
        if (Objects.requireNonNull(extras.getString("COMPL","Triads").equals("Exts"))){
            int seventh = scale[(rootIndex+6) % 7];
            int ninth = scale[(rootIndex+8) % 7];

            int thirteenth = scale[(rootIndex+12) % 7];

            int degrees[] = {seventh,ninth,thirteenth};
            for (int degree : degrees){
                if ((int)(Math.random()*2)==1){
                    notes.add(degree);
                }
            }
            if (altered){
                int flat9th = ninth-1;
                int sharp9th = ninth+1;
                int flat13th = thirteenth-1;


                    switch ((int)(Math.random()*3)) {
                        case 0:
                            notes.add(flat9th);
                            break;
                        case 1:
                            notes.add(sharp9th);
                            break;
                        case 2:
                            notes.add(flat13th);
                            break;
                    }

            }
        }
        return notes.stream().mapToInt(Integer::intValue).toArray();

    }



    @Override
    public void onPlayChord(int[][] selectedChords, int index) {
        chord(selectedChords[index]);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}