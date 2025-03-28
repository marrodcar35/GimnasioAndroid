package com.example.gym;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TimerActivity extends AppCompatActivity {

    private EditText minutesInput, secondsInput;
    private TextView tvTimer;
    private Button btnStartStopTimer, btnResetTimer, btnSwitchMode;
    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private boolean isCountdownMode = true; // True = Cuenta atrás, False = Cronómetro
    private long timeLeftInMillis = 0;
    private long timeMilis = 0;
    private long startTime = 0;
    private long elapsedTime = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        minutesInput = findViewById(R.id.minutes_input);
        secondsInput = findViewById(R.id.seconds_input);
        tvTimer = findViewById(R.id.tv_timer);
        btnStartStopTimer = findViewById(R.id.btn_start_stop_timer);
        btnResetTimer = findViewById(R.id.btn_reset_timer);
        btnSwitchMode = findViewById(R.id.btn_switch_mode);

        btnStartStopTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isRunning) {
                    if (isCountdownMode) stopTimer();
                    else stopChronometer();
                } else {
                    if (isCountdownMode) startTimer();
                    else startChronometer();
                }
            }
        });

        btnResetTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetTimer();
            }
        });

        btnSwitchMode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchMode();
            }
        });


    }

    private void startTimer() {
        String minutesStr = minutesInput.getText().toString();
        String secondsStr = secondsInput.getText().toString();

        if (minutesStr.isEmpty() && secondsStr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa minutos y/o segundos", Toast.LENGTH_SHORT).show();
            return;
        }

        int minutes = minutesStr.isEmpty() ? 0 : Integer.parseInt(minutesStr);
        int seconds = secondsStr.isEmpty() ? 0 : Integer.parseInt(secondsStr);

        if (minutes == 0 && seconds == 0) {
            Toast.makeText(this, "El tiempo no puede ser 0", Toast.LENGTH_SHORT).show();
            return;
        }

        timeMilis = (minutes * 60 + seconds) * 1000;
        timeLeftInMillis = timeMilis;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 10) { // Se actualiza cada 10ms
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            public void onFinish() {
                isRunning = false;
                tvTimer.setText("00:00.00");
                btnStartStopTimer.setText("Iniciar");
                Toast.makeText(TimerActivity.this, "¡Finalizado!", Toast.LENGTH_SHORT).show();
            }
        }.start();

        isRunning = true;
        btnStartStopTimer.setText("Detener");
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isRunning = false;
        btnStartStopTimer.setText("Iniciar");
    }

    private void resetTimer() {
        stopTimer();
        timeLeftInMillis = (timeMilis > 0) ? timeMilis : 0;
        updateTimerText();
    }


    private void startChronometer() {
        startTime = SystemClock.elapsedRealtime() - elapsedTime;
        handler.post(updateChronometer);
        isRunning = true;
        btnStartStopTimer.setText("Detener");
    }

    private void stopChronometer() {
        handler.removeCallbacks(updateChronometer);
        elapsedTime = SystemClock.elapsedRealtime() - startTime;
        isRunning = false;
        btnStartStopTimer.setText("Iniciar");
    }

    private Runnable updateChronometer = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                long currentTime = SystemClock.elapsedRealtime();
                elapsedTime = currentTime - startTime;
                tvTimer.setText(formatTimeWithMillis(elapsedTime));
                handler.postDelayed(this, 10); // Actualiza cada 10ms
            }
        }
    };

    private void switchMode() {
        if (isRunning) {
            if (isCountdownMode) stopTimer();
            else stopChronometer();
        }

        isCountdownMode = !isCountdownMode;

        if (isCountdownMode) {
            tvTimer.setText("00:00.00");
            minutesInput.setVisibility(View.VISIBLE);
            secondsInput.setVisibility(View.VISIBLE);
            btnSwitchMode.setText("Cambiar a Cronómetro");
            btnStartStopTimer.setText("Iniciar");
        } else {
            tvTimer.setText("00:00.00");
            minutesInput.setVisibility(View.GONE);
            secondsInput.setVisibility(View.GONE);
            btnSwitchMode.setText("Cambiar a Cuenta Atrás");
            btnStartStopTimer.setText("Iniciar");
        }
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        int millis = (int) (timeLeftInMillis % 1000) / 10;
        tvTimer.setText(String.format("%02d:%02d.%02d", minutes, seconds, millis));
    }

    private String formatTimeWithMillis(long elapsedMillis) {
        int minutes = (int) (elapsedMillis / 1000) / 60;
        int seconds = (int) (elapsedMillis / 1000) % 60;
        int millis = (int) (elapsedMillis % 1000) / 10;
        return String.format("%02d:%02d.%02d", minutes, seconds, millis);
    }
}
