package com.example.ninja.ninjaeditors;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import NinjaEditor.NinjaEditor;


public class MainActivity extends AppCompatActivity {

    //----------------------------------------------------------------------------------------------------
    static final int SETTINGS_ACTIVITY_CODE = 1;

    //----------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_demo) {
            NinjaEditor editor = (NinjaEditor)findViewById(R.id.NinjaEditor);
            try {
                InputStream fin = getResources().openRawResource(R.raw.demo);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fin));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);
                    buffer.append('\n');
                }
                editor.setText(buffer);
                fin.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        } else if (id == R.id.action_load) {
            NinjaEditor editor = (NinjaEditor)findViewById(R.id.NinjaEditor);
            try {
                FileInputStream fin = super.openFileInput("JavaCode.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fin));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);
                    buffer.append('\n');
                }
                editor.setText(buffer);
                fin.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        } else if (id == R.id.action_save) {
            NinjaEditor editor = (NinjaEditor) findViewById(R.id.NinjaEditor);
            try {
                FileOutputStream fout = super.openFileOutput("JavaCode.txt", Activity.MODE_PRIVATE);
                PrintStream ps = new PrintStream(fout);
                ps.print(editor.getText().toString());
                ps.close();
                fout.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        } else if (id == R.id.action_settings) {
            NinjaEditor editor = (NinjaEditor)findViewById(R.id.NinjaEditor);
            Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
            settings.putExtras(editor.getColors());
            startActivityForResult(settings, SETTINGS_ACTIVITY_CODE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SETTINGS_ACTIVITY_CODE:
                NinjaEditor editor = (NinjaEditor)findViewById(R.id.NinjaEditor);
                editor.setColors(data.getExtras());
                break;
            default:
                break;
        }
    }
}
