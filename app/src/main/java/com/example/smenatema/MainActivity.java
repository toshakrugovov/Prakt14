package com.example.smenatema;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    SharedPreferences themeSettings;
    SharedPreferences.Editor settingsEditor;
    ImageButton imageTheme;

    boolean isPlayerTurn = true;
    String[] board = new String[9];
    Random random = new Random();
    int playerWins = 0;
    int botWins = 0;
    int draws = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        themeSettings = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        if (!themeSettings.contains("MODE_NIGHT_ON")) {
            settingsEditor = themeSettings.edit();
            settingsEditor.putBoolean("MODE_NIGHT_ON", false);
            settingsEditor.apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            setCurrentTheme();
        }

        setContentView(R.layout.activity_main);
        imageTheme = findViewById(R.id.gg);
        updateImageButton();


        imageTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTheme();
            }
        });

        setupGame();
    }


    private void setupGame() {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        TextView statistics = findViewById(R.id.statistics);


        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            int finalI = i;
            Button button = (Button) gridLayout.getChildAt(i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPlayerTurn && board[finalI] == null) {
                        board[finalI] = "X";
                        button.setText("X");
                        if (checkWin("X")) {
                            playerWins++;
                            updateStatistics(statistics);
                            Toast.makeText(MainActivity.this, "Вы победили!", Toast.LENGTH_SHORT).show();
                            resetBoard();
                        } else if (isBoardFull()) {
                            draws++;
                            updateStatistics(statistics);
                            Toast.makeText(MainActivity.this, "Ничья!", Toast.LENGTH_SHORT).show();
                            resetBoard();
                        } else {
                            isPlayerTurn = false;
                            botMove();
                        }
                        updateStatistics(statistics);
                    }
                }
            });
        }
    }


    private void botMove() {
        int move;
        do {
            move = random.nextInt(9);
        } while (board[move] != null);

        board[move] = "O";
        Button button = (Button) ((GridLayout) findViewById(R.id.gridLayout)).getChildAt(move);
        button.setText("O");

        if (checkWin("O")) {
            botWins++;
            updateStatistics((TextView) findViewById(R.id.statistics));
            Toast.makeText(this, "Бот победил!", Toast.LENGTH_SHORT).show();
            resetBoard();
        } else if (isBoardFull()) {
            draws++;
            updateStatistics((TextView) findViewById(R.id.statistics));
            Toast.makeText(this, "Ничья!", Toast.LENGTH_SHORT).show();
            resetBoard();
        } else {
            isPlayerTurn = true;
        }
    }

    // Проверка победы
    private boolean checkWin(String player) {
        int[][] winPatterns = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };


        for (int[] pattern : winPatterns) {
            if (board[pattern[0]] == player && board[pattern[1]] == player && board[pattern[2]] == player) {
                return true;
            }
        }
        return false;
    }


    private boolean isBoardFull() {
        for (String cell : board) {
            if (cell == null) {
                return false;
            }
        }
        return true;
    }


    private void updateStatistics(TextView statistics) {
        statistics.setText("Побед: " + playerWins + ", Поражений: " + botWins + ", Ничьи: " + draws);
    }


    private void resetBoard() {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            Button button = (Button) gridLayout.getChildAt(i);
            button.setText("");
        }
        board = new String[9];
        isPlayerTurn = true;
    }


    private void toggleTheme() {
        if (themeSettings.getBoolean("MODE_NIGHT_ON", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            settingsEditor = themeSettings.edit();
            settingsEditor.putBoolean("MODE_NIGHT_ON", false);
            settingsEditor.apply();
            Toast.makeText(MainActivity.this, "Темная тема отключена", Toast.LENGTH_SHORT).show();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            settingsEditor = themeSettings.edit();
            settingsEditor.putBoolean("MODE_NIGHT_ON", true);
            settingsEditor.apply();
            Toast.makeText(MainActivity.this, "Темная тема включена", Toast.LENGTH_SHORT).show();
        }
        updateImageButton();
    }


    private void updateImageButton() {
        if (themeSettings.getBoolean("MODE_NIGHT_ON", false)) {
            imageTheme.setImageResource(R.drawable.sunblack);
        } else {
            imageTheme.setImageResource(R.drawable.a);
        }
    }


    private void setCurrentTheme() {
        if (themeSettings.getBoolean("MODE_NIGHT_ON", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
