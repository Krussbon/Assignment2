package com.example.assignment;


import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

//The mainactivity is the first activity that appears when one opens the app.
public class MainActivity extends AppCompatActivity {
    //The Drawer Layout and navView are used in order to set a navigation menu that can be opened using a hamburger icon
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    //OnCreate methods start when the activity is started
    //this is the letter name of the root note
    final int[] selectedScale = new int[1];
    //this is the accidental fo the root note
    final int[] selectedAccidental = new int[1];
    //this represents the mode of the scale
    final String[] selectedMode = new String[1];
    //this defined the complexity of the chords
    //the more "complex" the chords are, the more notes
    //they have
    final String[] selectedComplexity = new String[1];
    //this defined the musical instrument the chords are played in
    final String[] selectedInstrument= new String[1];
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //Edge to edge is enabled for the image to take up all of the space
        EdgeToEdge.enable(this);
        //This sets the main content view to the activity_main.xml file
        //found in the layouts folder
        setContentView(R.layout.activity_main);
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

        //This sets up a listener that is called when the system needs
        //to tell the app about changes in "window insets"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            //the inset for the system bars(The space that shows the battery, time etc)
            //are set
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            //the view gets the padding based on the dimensions of the system bars
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //A toolbar variable is initialised  to display the name of the
        //and the hamburger button
        Toolbar toolbar = findViewById(R.id.toolbar);
        //This line of code sets the toolbar as the action bar
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_bar);
        //Object.requireNonNull is a safety check in order to make sure that the toolbar
        //is not set to null. if there is no toolbar variable, it will throw a NullPointerException
        // as opposed to crashing
        Objects.requireNonNull(
                //this retrieves the action bar, which as previously explained
                //was the toolbar
                getSupportActionBar()
                //when the boolean value is set to true, it displays a selected
                //icon( back arrow <- by default)
        ).setDisplayHomeAsUpEnabled(true);
        //replaces the default <- icon of the HomeAsUpIndicator with the
        //hambruger icon
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger_menu);
        //This attaches a listener to the NavigationView.
        //Whenever a user clicks on an item in the drawer view,
        //the listener will be invoked
        navView.setNavigationItemSelectedListener(item->{
            //Declares a Intent variable and initialises it to
            //null.
            Intent intent = null;
            int id = item.getItemId();
            //this conditional statement chechs which menu item was
            //clicked based on its ID
            if(id==R.id.nav_main){

                intent = new Intent(this, MainActivity.class);
            }else if(id==R.id.pads){
                intent = new Intent(this, PadsPage.class);
            }

            if (intent != null) {

                intent.putExtra("OFFSET",selectedScale[0]+selectedAccidental[0]);
                intent.putExtra("MODE",selectedMode[0]);
                intent.putExtra("COMPL",selectedComplexity[0]);
                intent.putExtra("INSTR",selectedInstrument[0]);
                startActivity(intent);
            }
            drawerLayout.closeDrawer(GravityCompat.START);  // Close drawer after selection
            return true;
        });
        //A Spinner is set for the drop down menus in the main page
        //An arrayadapter is a bridge between the data and the spinner ui element.
        // the sting array is stored in the res/values/strings.xml file
        //The adapter is then connected to the spinner ui and the spinner noow
        //displays the strings from the R.array
        //This process is done for all
        //Root
        //Accidental
        //Mode
        //Complexity
        //Instrument
        Spinner scales = findViewById(R.id.spinnerScale);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.root, android.R.layout.simple_spinner_dropdown_item);
        scales.setAdapter(adapter);
        Spinner accidental = findViewById(R.id.spinnerAccidental);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.accidental,android.R.layout.simple_spinner_dropdown_item);
        accidental.setAdapter(adapter2);
        Spinner modeSp = findViewById(R.id.spinnerMode);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,R.array.mode,android.R.layout.simple_spinner_dropdown_item);
        modeSp.setAdapter(adapter3);
        Spinner compSp = findViewById(R.id.spinnerComplexity);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,R.array.complexity,android.R.layout.simple_spinner_dropdown_item);
        compSp.setAdapter(adapter4);
        Spinner instSp = findViewById(R.id.spinnerInstrument);
        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(this,R.array.instrument,android.R.layout.simple_spinner_dropdown_item);
        instSp.setAdapter(adapter5);


        //selects the root note
        scales.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // The goal here is to convert the selected item's position (i) into a numerical value
                // that represents a musical 'root note'.
                // As mentioned, 'root' is an integer representing how many semitones away it is from C.
                // (C=0, C#=1, D=2, D#=3, E=4, F=5, F#=6, G=7, G#=8, A=9, A#=10, B=11)
                // Note: Negative semitones imply moving downwards from C, e.g., B is -1 from C (or 11 semitones up).

                switch(i){
                    case 0  :
                        //this corresponds to C
                        selectedScale[0]= 0;//Set the root note value to 0 semitones
                        break;
                    case 1:
                        //this corresponds to D
                        selectedScale[0]= 2;//Set the root note value to 2 semitones
                        break;
                    case 2:
                        //this corresponds to E
                        selectedScale[0]= 4;//Set the root note value to 4 semitones
                        break;
                    case 3:
                        //this corresponds to F
                        selectedScale[0] =5;//Set the root note value to 5 semitones
                        break;
                    case 4:
                        //this corresponds to G
                        selectedScale[0] = 7;//Set the root note value to G semitones
                        break;
                    case 5:
                        //this corresponds to A
                        selectedScale[0]= -3;//Set the root note value to -3 semitones
                        break;
                    case 6:
                        //this corresponds to B
                        selectedScale[0] = -1;//Set the root note value to -1 semitones
                        break;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //Defines the offset on the accidental
        //the purpose of the code is to convert the user's selection of accidental
        //into a numerical value that represents a sharp, flat, or natural

        accidental.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        //this is a natural
                        selectedAccidental[0] = 0;
                        break;
                    case 1:
                        //this is a flat
                        selectedAccidental[0] = -1;
                        break;
                    case 2:
                        //this is a sharp
                        selectedAccidental[0] = 1;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //in this section, the user decides the mode of the scale i.e. major or minor
        //each mode has its own combination of tones(a tone is two semitones)
        //and semitones
        modeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        selectedMode[0] = "Major";
                        break;
                    case 1:
                        selectedMode[0] = "Minor";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //in this section, the user decides what level of complexity the chords have
        compSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        selectedComplexity[0] = "Triads";
                        break;
                    case 1:
                        selectedComplexity[0] = "7ths";
                        break;
                    case 2:
                        selectedComplexity[0] = "Exts";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //In this section of the code, the user chooses the instrument the chords are
        //"played on"
        instSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        selectedInstrument[0] = "Piano";
                        break;
                    case 1:
                        selectedInstrument[0] = "Rhodes";
                        break;
                    case 2:
                        selectedInstrument[0] = "Guitar";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //This button lets the user go to the PadPage activity
        View button = findViewById(R.id.button);
        if (button != null) {
            button.setOnClickListener(view -> {
                try {
                    Intent intent = new Intent(MainActivity.this, PadsPage.class);
                    intent.putExtra("OFFSET",selectedScale[0]+selectedAccidental[0]);
                    intent.putExtra("MODE",selectedMode[0]);
                    intent.putExtra("COMPL",selectedComplexity[0]);
                    intent.putExtra("INSTR",selectedInstrument[0]);

                    startActivity(intent);

                }catch (Exception e){
                    Toast.makeText(this, "Failed to open Page", Toast.LENGTH_SHORT).show();
                    Log.e("Nav","  activity",e);
                }

            });
        }else{
            Log.e("MainActivity","Button not found");
        }



    }


    //This method is called by the android system whenever an item in the Toolbar is selected.
    //the item parameter represents the MenuItem that was selected by the user.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //If the selected id is the standard home button then the
        //navigation drawer is open
        //Gravity.Compat.START means that the drawer
        //comes in from the "start" (the left side) of the screen.

        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            //returning true prevents the event
            // from bing passed to other handlers
            return true;
        }
        //if the selected item was not the "home"
        //then pass the event to the superclass;s implementation.
        //This ensures that other menu items one might have ("Settings or share abilities") are handled correctly
        return super.onOptionsItemSelected(item);
    }


}