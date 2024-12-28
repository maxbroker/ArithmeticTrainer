package com.example.ArithmeticTrainer;

import android.annotation.SuppressLint;
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
        int[] correctSounds = {R.raw.correct1, R.raw.correct2, R.raw.correct3, R.raw.correct4, R.raw.correct5};
        int[] wrongSounds = {R.raw.wrong1, R.raw.wrong2, R.raw.wrong3, R.raw.wrong4, R.raw.wrong5};

        int[] soundsToPlay = isCorrect ? correctSounds : wrongSounds;

        int randomSound = soundsToPlay[new Random().nextInt(soundsToPlay.length)];

        MediaPlayer mediaPlayer = MediaPlayer.create(this, randomSound);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.release());
    }

    private void playSUSUGIMNSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this,  R.raw.susu);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.release());
    }

    private void displaySUSUImage() {
        ImageView feedbackImage = findViewById(R.id.iv_feedback_image);
        feedbackImage.setVisibility(View.VISIBLE);

        int[] correctImages = {R.drawable.susu};
        Random random = new Random();
        random.nextInt(correctImages.length);
        int randomIndex = 0;


        Glide.with(this)
                .load(correctImages[randomIndex])
                .into(feedbackImage);
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

        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAnswerStr = etAnswer.getText().toString();
                if (!userAnswerStr.isEmpty()) {
                    if (userAnswerStr.equals("1943")) {
                        playSUSUGIMNSound();
                        displaySUSUImage();
                        return;
                    }
                    int userAnswer = Integer.parseInt(userAnswerStr);
                    if (userAnswer == correctAnswer) {
                        score += level;
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

    @SuppressLint("SetTextI18n")
    private void generateQuestion() {
        Random random = new Random();

        if (level >= 5) {
            int taskType = random.nextInt(4);

            if (taskType == 0) {
                generateTriangleQuestion(random);
            } else if (taskType == 1) {
                generateRectangleQuestion(random, true);
            } else if (taskType == 2) {
                generateRectangleQuestion(random, false);
            } else {
                generateTriangleAreaQuestion(random);
            }
        } else if (level >= 3) {
            int taskType = random.nextInt(8);

            if (taskType == 0) {
                generateRectangleQuestion(random, true);
            } else if (taskType == 1 || taskType == 2 || taskType == 3) {
                generateRectangleQuestion(random, false);
            } else {
                generateBasicMathQuestion(random);
            }
        } else {
            int taskType = random.nextInt(8);

            if (taskType == 0) {
                generateRectangleQuestion(random, false);
            } else {
                generateBasicMathQuestion(random);
            }
        }
    }

    private void generateBasicMathQuestion(Random random) {
        int multiplier = 0;

        switch (level) {
            case 1:
                multiplier = 10;
            case 2:
                multiplier = 8;
            case 3:
                multiplier = 7;
            case 4:
                multiplier = 5;
            case 5:
                multiplier = 3;
        }

        int num1 = random.nextInt(level * multiplier) + 1;
        int num2 = random.nextInt(level * multiplier) + 1;


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
                num1 = correctAnswer * num2;
                break;
        }
        tvQuestion.setText("Пример: " + num1 + " " + operator + " " + num2);
    }

    @SuppressLint("SetTextI18n")
    private void generateRectangleQuestion(Random random, boolean isArea) {
        int length = random.nextInt(level * 4) + 1;
        int width = random.nextInt(level * 4) + 1;

        if (isArea) {
            tvQuestion.setText("Найти площадь прямоугольника со сторонами " + length + " и " + width);
            correctAnswer = length * width;
        } else {
            tvQuestion.setText("Найти периметр прямоугольника со сторонами " + length + " и " + width);
            correctAnswer = 2 * (length + width);
        }
    }

    private void generateTriangleAreaQuestion(Random random) {
        int base = random.nextInt(level * 4) + 1;
        int height = random.nextInt(level * 4) + 1;

        tvQuestion.setText("Найти площадь треугольника с основанием " + base + " и высотой " + height);
        correctAnswer = (base * height) / 2;
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

    private void generateTriangleQuestion(Random random) {
        String[] triangleTypes = {"гипотенуза", "катет"};
        String triangleType = triangleTypes[random.nextInt(triangleTypes.length)];

        int a, b, c;

        do {
            a = random.nextInt(level * 4) + 1;
            b = random.nextInt(level * 4) + 1;
            c = (int) Math.sqrt(a * a + b * b);
        } while (c * c != a * a + b * b);

        if ("гипотенуза".equals(triangleType)) {
            tvQuestion.setText("Найти гипотенузу, если катеты: " + a + " и " + b);
            correctAnswer = c;
        } else {
            boolean findA = random.nextBoolean();
            if (findA) {
                tvQuestion.setText("Найти катет a, если катет b = " + b + " и гипотенуза = " + c);
                correctAnswer = a;
            } else {
                tvQuestion.setText("Найти катет b, если катет a = " + a + " и гипотенуза = " + c);
                correctAnswer = b;
            }
        }
    }
}
