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


public class MainActivity extends AppCompatActivity {

    private GravityView gravityView;
    private SeekBar radiusSeekBar;
    private EditText radiusET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gravityView = (GravityView) findViewById(R.id.gravityView);
        radiusSeekBar = (SeekBar) findViewById(R.id.radius_seekBar);
        radiusET = (EditText) findViewById(R.id.radius_editText);

        gravityView.requestFocus();
        setupSeekBar();
        setupRadiusET();
    }

    private void setupSeekBar() {
        // Set the max value
        /*if (gravityView.getWidth() >= gravityView.getHeight()) {
            radiusSeekBar.setMax(gravityView.getHeight() / 2);
        } else {
            radiusSeekBar.setMax(gravityView.getWidth() / 2);
        }*/ // TODO why doesn't this work

        // Set the listener
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    gravityView.setRadius(progress);
                    radiusET.setText("" + progress);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {/*ignored*/}

            public void onStopTrackingTouch(SeekBar seekBar) {/*ignored*/}
        });
    }

    private void setupRadiusET() {
        radiusET.setText("" + radiusSeekBar.getProgress());
        radiusET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int radius;
                try {
                    radius = Integer.parseInt(radiusET.getText().toString());
                } catch (NumberFormatException e) {
                    radius = 0;
                }

                if (radius > radiusSeekBar.getMax()) {
                    radiusSeekBar.setProgress(radiusSeekBar.getMax());
                    gravityView.setRadius(radius);
                } else {
                    radiusSeekBar.setProgress(radius);
                    gravityView.setRadius(radius);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {/*ignored*/}

            public void afterTextChanged(Editable s) {/*ignored*/}
        });
    }

    public void onClickPlayPause(View view) {
        if (gravityView.isRunning()) {
            gravityView.stop();
            ((ImageButton) view).setImageResource(R.mipmap.ic_play_arrow_grey600_36dp);
        } else {
            gravityView.begin();
            ((ImageButton) view).setImageResource(R.mipmap.ic_pause_grey600_36dp);
        }
    }

    @Override
    protected void onPause() {
        gravityView.stop();
        ((ImageButton) findViewById(R.id.playPauseButton)).setImageResource(R.mipmap.ic_play_arrow_grey600_36dp);
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
}
