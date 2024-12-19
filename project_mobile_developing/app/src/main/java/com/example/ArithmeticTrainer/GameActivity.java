package com.example.ArithmeticTrainer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private int level;
    private int correctAnswer;
    private int score = 0;
    private int record = 0;

    private TextView tvQuestion, tvFeedback, tvScore, tvRecord;
    private EditText etAnswer;
    private SharedPreferences sharedPreferences;
    private void playRandomSound(boolean isCorrect) {
        int[] correctSounds = {R.raw.correct1, R.raw.correct2, R.raw.correct2}; // Список правильных мелодий
        int[] wrongSounds = {R.raw.wrong1, R.raw.wrong3};       // Список грустных мелодий

        int[] soundsToPlay = isCorrect ? correctSounds : wrongSounds;

        int randomSound = soundsToPlay[new Random().nextInt(soundsToPlay.length)];

        MediaPlayer mediaPlayer = MediaPlayer.create(this, randomSound);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.release());
    }

    private void playSUSUGIMNSound() {
        int[] SUSUGIMN = {R.raw.susu};

        int randomSound = SUSUGIMN[new Random().nextInt(SUSUGIMN.length)];

        MediaPlayer mediaPlayer = MediaPlayer.create(this, randomSound);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.release());
    }

    private void displayFeedbackImage(boolean isCorrect) {
        ImageView feedbackImage = findViewById(R.id.iv_feedback_image);
        feedbackImage.setVisibility(View.VISIBLE);

        int[] correctImages = {R.drawable.funny_correct0, R.drawable.funny_correct1, R.drawable.funny_correct2, R.drawable.funny_correct3, R.drawable.funny_correct4, R.drawable.funny_correct5, R.drawable.funny_correct6};
        int[] wrongImages = {R.drawable.funny_wrong0, R.drawable.funny_wrong1, R.drawable.funny_wrong2, R.drawable.funny_wrong3, R.drawable.funny_wrong4, R.drawable.funny_wrong5, R.drawable.funny_wrong6};

        Random random = new Random();
        int randomIndex = random.nextInt(correctImages.length);


        if (isCorrect) {
            Glide.with(this)
                    .load(correctImages[randomIndex])
                    .into(feedbackImage);
        } else {
            Glide.with(this)
                    .load(wrongImages[randomIndex])
                    .into(feedbackImage);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        level = getIntent().getIntExtra("LEVEL", 1);

        tvQuestion = findViewById(R.id.tv_question);
        etAnswer = findViewById(R.id.et_answer);
        Button btnSubmit = findViewById(R.id.btn_submit);
        tvFeedback = findViewById(R.id.tv_feedback);
        tvScore = findViewById(R.id.tv_score);
        tvRecord = findViewById(R.id.tv_record);

        sharedPreferences = getSharedPreferences("ArithmeticTrainerPrefs", MODE_PRIVATE);
        record = sharedPreferences.getInt("record", 0);
        tvRecord.setText("Рекорд: " + record);
        Log.e("Error", "Record: " + String.valueOf(tvRecord));

        generateQuestion();

        ImageButton backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Обработчик кнопки "Проверить"
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAnswerStr = etAnswer.getText().toString();
                if (!userAnswerStr.isEmpty()) {
                    if (userAnswerStr.equals("1488")) {
                        playSUSUGIMNSound();
                        return;
                    }
                    int userAnswer = Integer.parseInt(userAnswerStr);
                    if (userAnswer == correctAnswer) {
                        score++;
                        tvFeedback.setText("Правильно!");
                        playRandomSound(true);
                        displayFeedbackImage(true);
                        updateScore();
                    } else {
                        tvFeedback.setText("Неправильно! Правильный ответ: " + correctAnswer);
                        playRandomSound(false);
                        displayFeedbackImage(false);
                    }
                    etAnswer.getText().clear();
                    generateQuestion();
                } else {
                    Toast.makeText(GameActivity.this, "Введите ответ!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void generateQuestion() {
        Random random = new Random();
        int num1 = random.nextInt(level * 10) + 1;
        int num2 = random.nextInt(level * 10) + 1;
        String operator = getRandomOperator();

        switch (operator) {
            case "+":
                correctAnswer = num1 + num2;
                break;
            case "-":
                correctAnswer = num1 - num2;
                break;
            case "*":
                correctAnswer = num1 * num2;
                break;
            case "/":
                correctAnswer = num1 / num2;
                num1 = correctAnswer * num2; // Убедиться, что деление нацело
                break;
        }
        tvQuestion.setText("Пример: " + num1 + " " + operator + " " + num2);
    }

    private String getRandomOperator() {
        String[] operators = {"+", "-", "*", "/"};
        Random random = new Random();
        return operators[random.nextInt(operators.length)];
    }

    private void updateScore() {
        tvScore.setText("Очки: " + score);

        if (score > record) {
            record = score;
            tvRecord.setText("Рекорд: " + record);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("record", record);
            editor.apply();
        }
    }
}
