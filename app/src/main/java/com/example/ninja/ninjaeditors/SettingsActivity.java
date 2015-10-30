package com.example.ninja.ninjaeditors;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class SettingsActivity extends AppCompatActivity {

    //----------------------------------------------------------------------------------------------------
    private Bundle m_colors;
    private SurfaceView m_colorSurface;
    private Spinner m_itemSpinner;
    private AdapterView.OnItemSelectedListener m_itemListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String item = m_itemSpinner.getSelectedItem().toString();
                    if (m_colors.containsKey(item)) {
                        setColorProgress(m_colors.getInt(item));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            };

    private TextView m_alphaText;
    private SeekBar m_alphaBar;
    private TextView m_redText;
    private SeekBar m_redBar;
    private TextView m_greenText;
    private SeekBar m_greenBar;
    private TextView m_blueText;
    private SeekBar m_blueBar;
    private SeekBar.OnSeekBarChangeListener m_seekBarListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    updateData();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };

    //----------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        m_colors = getIntent().getExtras();
        m_colorSurface = (SurfaceView)findViewById(R.id.colorSurface);
        m_itemSpinner = (Spinner)findViewById(R.id.itemSpinner);
        SpinnerAdapter sa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(m_colors.keySet()));
        m_itemSpinner.setAdapter(sa);
        m_itemSpinner.setSelection(0);
        m_itemSpinner.setOnItemSelectedListener(m_itemListener);

        // Alpha
        m_alphaText = (TextView)findViewById(R.id.alphaText);
        m_alphaBar = (SeekBar)findViewById(R.id.alphaSeek);
        m_alphaBar.setOnSeekBarChangeListener(m_seekBarListener);
        // Red
        m_redText = (TextView)findViewById(R.id.redText);
        m_redBar = (SeekBar)findViewById(R.id.redSeek);
        m_redBar.setOnSeekBarChangeListener(m_seekBarListener);
        // Green
        m_greenText = (TextView)findViewById(R.id.greenText);
        m_greenBar = (SeekBar)findViewById(R.id.greenSeek);
        m_greenBar.setOnSeekBarChangeListener(m_seekBarListener);
        // Blue
        m_blueText = (TextView)findViewById(R.id.blueText);
        m_blueBar = (SeekBar)findViewById(R.id.blueSeek);
        m_blueBar.setOnSeekBarChangeListener(m_seekBarListener);
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------------------------------------------------------
    public void setColorProgress(int color) {
        m_alphaBar.setProgress(Color.alpha(color));
        m_redBar.setProgress(Color.red(color));
        m_greenBar.setProgress(Color.green(color));
        m_blueBar.setProgress(Color.blue(color));
    }

    //----------------------------------------------------------------------------------------------------
    public void updateData() {
        final int alpha = m_alphaBar.getProgress();
        final int red = m_redBar.getProgress();
        final int green = m_greenBar.getProgress();
        final int blue = m_blueBar.getProgress();
        int color = Color.argb(alpha, red, green, blue);

        String item = m_itemSpinner.getSelectedItem().toString();
        if (m_colors.containsKey(item)) {
            // todo: need a backup
            m_colors.putInt(item, color);
        }

        m_colorSurface.setBackgroundColor(color);

        // Update the text for each label with the value of each channel
        m_alphaText.setText(getString(R.string.value_alpha, alpha));
        m_redText.setText(getString(R.string.value_red, red));
        m_greenText.setText(getString(R.string.value_green, green));
        m_blueText.setText(getString(R.string.value_blue, blue));
    }
}
