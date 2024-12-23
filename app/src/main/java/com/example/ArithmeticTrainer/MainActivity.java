package com.example.ArithmeticTrainer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinnerLevel = findViewById(R.id.spinner_level);
        Button btnStartGame = findViewById(R.id.btn_start_game);

        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedLevel = spinnerLevel.getSelectedItemPosition() + 1;
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("LEVEL", selectedLevel);
                startActivity(intent);
            }
        });
    }
}
