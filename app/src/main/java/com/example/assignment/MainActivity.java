package com.example.assignment;


import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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


public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
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
            drawerLayout.closeDrawer(GravityCompat.START);  // Close drawer after selection
            return true;
        });
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
        final int[] selectedScale = new int[1];
        final int[] selectedAccidental = new int[1];
        final String[] selectedMode = new String[1];
        final String[] selectedComplexity = new String[1];
        final String[] selectedInstrument= new String[1];
        scales.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch(i){
                    case 0  :
                        selectedScale[0]= 0;
                        break;
                    case 1:
                        selectedScale[0]= 2;
                        break;
                    case 2:
                        selectedScale[0]= 4;
                        break;
                    case 3:
                        selectedScale[0] =5;
                        break;
                    case 4:
                        selectedScale[0] = 7;
                        break;
                    case 5:
                        selectedScale[0]= -3;
                        break;
                    case 6:
                        selectedScale[0] = -1;
                        break;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        accidental.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        selectedAccidental[0] = 0;
                        break;
                    case 1:
                        selectedAccidental[0] = -1;
                        break;
                    case 2:
                        selectedAccidental[0] = 1;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}