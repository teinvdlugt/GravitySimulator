package com.teinproductions.tein.gravitysimulator;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;


public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private GravityView gravityView;
    private SeekBar radiusSeekBar, massSeekBar, densitySeekBar;
    private EditText radiusET, massET, densityET;
    private ImageButton playButton, pauseButton;

    /**
     * The relation between the radius of a newly created Luminary and its mass.
     * The mass is calculated as {@code m = density * r^3}.
     * When the radiusSeekBar value is changed, the massSeekBar and massET are adapted
     * according to the above equation.
     * When the massSeekBar value is changed, the radiusSeekBar will not be updated. Instead,
     * the value of {@code radiusMassRatio} is updated.
     */
    private double density = 10000;

    // TODO: add a variable which takes care of the zoom. Now 1 meter = 1 pixel, that has to be changed.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        gravityView.requestFocus();
        radiusSeekBar.setOnSeekBarChangeListener(this);
        massSeekBar.setOnSeekBarChangeListener(this);
        densitySeekBar.setOnSeekBarChangeListener(this);
        setupETs();
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickPlay();
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickPause();
            }
        });
    }

    private void initViews() {
        gravityView = (GravityView) findViewById(R.id.gravityView);
        radiusSeekBar = (SeekBar) findViewById(R.id.radius_seekBar);
        radiusET = (EditText) findViewById(R.id.radius_editText);
        massSeekBar = (SeekBar) findViewById(R.id.mass_seekBar);
        massET = (EditText) findViewById(R.id.mass_editText);
        densitySeekBar = (SeekBar) findViewById(R.id.density_seekBar);
        densityET = (EditText) findViewById(R.id.density_editText);
        playButton = (ImageButton) findViewById(R.id.playButton);
        pauseButton = (ImageButton) findViewById(R.id.pauseButton);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) return;

        switch (seekBar.getId()) {
            case R.id.radius_seekBar:
                gravityView.setRadius(progress);
                radiusET.setText("" + progress);
                break;
            case R.id.mass_seekBar:
                gravityView.setMass(progress);
                massET.setText("" + progress);
                break;
            case R.id.density_seekBar:
                density = progress;
                densityET.setText("" + progress);
                double mass = density * gravityView.getRadius() * gravityView.getRadius() * gravityView.getRadius();
                massSeekBar.setProgress((int) mass);
                onProgressChanged(massSeekBar, (int) mass, true);
        }
    }

    private void setupETs() {
        radiusET.setText("" + radiusSeekBar.getProgress());
        massET.setText("" + massSeekBar.getProgress());

        radiusET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int radius;
                String input = radiusET.getText().toString();
                if (validInteger(input)) radius = Integer.parseInt(input);
                else radius = 0;

                if (radius > radiusSeekBar.getMax())
                    radiusSeekBar.setProgress(radiusSeekBar.getMax());
                else radiusSeekBar.setProgress(radius);
                gravityView.setRadius(radius);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {/*ignored*/}

            public void afterTextChanged(Editable s) {/*ignored*/}
        });
        massET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int mass;
                String input = massET.getText().toString();
                if (validInteger(input)) mass = Integer.parseInt(input);
                else mass = 0;

                if (mass > massSeekBar.getMax())
                    massSeekBar.setProgress(massSeekBar.getMax());
                else massSeekBar.setProgress(mass);
                gravityView.setMass(mass);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {/*ignored*/}

            public void afterTextChanged(Editable s) {/*ignored*/}
        });
    }

    private void onClickPlay() {
        gravityView.begin();
    }

    private void onClickPause() {
        gravityView.stop();
    }

    @Override
    protected void onPause() {
        gravityView.stop();
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            gravityView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    public void onStartTrackingTouch(SeekBar seekBar) {/*ignored*/}

    public void onStopTrackingTouch(SeekBar seekBar) {/*ignored*/}

    public static boolean validInteger(String toCheck) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(toCheck);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
