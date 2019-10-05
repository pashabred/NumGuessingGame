package com.example.guessinggame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText txtGuess;
    private TextView txtRange;


    private int randNum;

    private int rangeMin = 1;

    private int rangeMax = 100;

    public int generateRandNum(int min, int max) {
        return (int) (Math.random() * (max + 1 - min) + min) ;
    }

    @SuppressLint("SetTextI18n")
    public void checkGuess() {
        String textFromET = txtGuess.getText().toString();
        try {
            if (Integer.parseInt(textFromET) < randNum) {
                Toast.makeText(MainActivity.this, textFromET + " is less than a maiden number. Try again", Toast.LENGTH_LONG).show();
                txtGuess.setText("");
            } else if (Integer.parseInt(textFromET) > randNum) {
                Toast.makeText(MainActivity.this, textFromET + " is over than a maiden number. Try again", Toast.LENGTH_LONG).show();
                txtGuess.setText("");
            } else {
                Toast.makeText(MainActivity.this, "Congratulations! You got that right. Let's play again", Toast.LENGTH_LONG).show();
                storeWinStat();
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Enter the number between " + rangeMin + " and "+ rangeMax, Toast.LENGTH_LONG).show();
        }

    }

    @SuppressLint("SetTextI18n")
    private void newGame(int min, int max) {
        randNum = generateRandNum(min, max);
        txtRange.setText("Enter the number between " + min + " and " + max);
        txtGuess.setText(" ");
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtGuess = findViewById(R.id.editText);
        Button btnGuess = findViewById(R.id.button);
        txtRange = findViewById(R.id.textView2);

        rangeMax = getRangeMax();

        txtRange.setText("Enter the number between " + rangeMin + " and "+ rangeMax);

        randNum = generateRandNum(rangeMin, rangeMax);

        btnGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGuess();
            }
        });

        txtGuess.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                checkGuess();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("InflateParams")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                showSettingsDialog();
                return true;

            case R.id.action_new_game:
                newGame(rangeMin,rangeMax);
                return true;

            case R.id.action_gamestats:
                showStatDialog();
                return true;

            case R.id.action_about:
               showAboutDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }
    void showAboutDialog() {
        AlertDialog aDialog;
        aDialog = new AlertDialog.Builder(MainActivity.this).create();
        aDialog.setTitle("About");
        aDialog.setMessage("Created by pashabred in 2019");
        aDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        aDialog.show();

    }

    void showSettingsDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Settings");
        dialog.setContentView(R.layout.settings_dialog);

        Objects.requireNonNull(dialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.END);

        final TextView dialogTextView = dialog.findViewById(R.id.dialog_textview);
        final SeekBar dialogSeekBar = dialog.findViewById(R.id.dialog_seekbar);
        final Button dialogButton = dialog.findViewById(R.id.dialog_button_save);


        dialogTextView.setText(String.valueOf(rangeMax));
        dialogSeekBar.setProgress(rangeMax);
        dialogSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dialogTextView.setText(String.valueOf(progress));
                storeRange(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rangeMax = getRangeMax();
                newGame(rangeMin,rangeMax);
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    void showStatDialog() {
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
        int gamesWon = shp.getInt("gamesWon", 0);

        final AlertDialog statDialog;

        statDialog = new AlertDialog.Builder(MainActivity.this).create();
        statDialog.setTitle("Statistics");
        statDialog.setMessage("You have guessed " + gamesWon);
        statDialog.setButton(AlertDialog.BUTTON_POSITIVE, "COOl", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                statDialog.dismiss();
            }
        });
        statDialog.show();
    }


    void storeRange(int newRange) {
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = shp.edit();
        ed.putInt("rangeMax", newRange);
        ed.apply();
    }

    int getRangeMax() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        return sharedPreferences.getInt("rangeMax", 100);
    }

    void storeWinStat() {
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
        int gameWon = shp.getInt("gamesWon", 0) + 1;
        SharedPreferences.Editor ed = shp.edit();
        ed.putInt("gamesWon", gameWon);
        ed.apply();
    }
}
