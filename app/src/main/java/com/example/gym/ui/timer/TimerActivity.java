package com.example.gym.ui.timer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.gym.R;

public class TimerActivity extends Fragment {

    private EditText minutesInput, secondsInput;
    private TextView tvTimer;

    private TextView tvTitle;
    private Button btnStartStopTimer, btnResetTimer, btnSwitchMode;
    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private boolean isCountdownMode = true;
    private long timeLeftInMillis = 0;
    private long timeMilis = 0;
    private long startTime = 0;
    private long elapsedTime = 0;

    private Handler handler = new Handler();

    public TimerActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_timer, container, false);

        minutesInput = view.findViewById(R.id.minutes_input);
        secondsInput = view.findViewById(R.id.seconds_input);
        tvTimer = view.findViewById(R.id.tv_timer);
        btnStartStopTimer = view.findViewById(R.id.btn_start_stop_timer);
        btnResetTimer = view.findViewById(R.id.btn_reset_timer);
        btnSwitchMode = view.findViewById(R.id.btn_switch_mode);
        tvTitle = view.findViewById(R.id.tv_title);


        btnStartStopTimer.setOnClickListener(v -> {
            if (isRunning) {
                if (isCountdownMode) stopTimer();
                else stopChronometer();
            } else {
                if (isCountdownMode) startTimer();
                else startChronometer();
            }
        });

        btnResetTimer.setOnClickListener(v -> resetTimer());

        btnSwitchMode.setOnClickListener(v -> switchMode());

        return view;
    }

    private void startTimer() {
        String minutesStr = minutesInput.getText().toString();
        String secondsStr = secondsInput.getText().toString();

        if (minutesStr.isEmpty() && secondsStr.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, ingresa minutos y/o segundos", Toast.LENGTH_SHORT).show();
            return;
        }

        int minutes = minutesStr.isEmpty() ? 0 : Integer.parseInt(minutesStr);
        int seconds = secondsStr.isEmpty() ? 0 : Integer.parseInt(secondsStr);

        if (minutes == 0 && seconds == 0) {
            Toast.makeText(getContext(), "El tiempo no puede ser 0", Toast.LENGTH_SHORT).show();
            return;
        }

        timeMilis = (minutes * 60 + seconds) * 1000;
        timeLeftInMillis = timeMilis;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 10) {
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            public void onFinish() {
                isRunning = false;
                tvTimer.setText("00:00.00");
                btnStartStopTimer.setText("Iniciar");
                Toast.makeText(getContext(), "¡Finalizado!", Toast.LENGTH_SHORT).show();
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
        if (isCountdownMode) {
            stopTimer();
            timeLeftInMillis = timeMilis;
            updateTimerText();
        } else {
            stopChronometer();
            elapsedTime = 0;
            startTime = 0;
            tvTimer.setText("00:00.00");
        }
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

    private final Runnable updateChronometer = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                long currentTime = SystemClock.elapsedRealtime();
                elapsedTime = currentTime - startTime;
                tvTimer.setText(formatTimeWithMillis(elapsedTime));
                handler.postDelayed(this, 10);
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
            tvTitle.setText("Temporizador");
        } else {
            tvTimer.setText("00:00.00");
            minutesInput.setVisibility(View.GONE);
            secondsInput.setVisibility(View.GONE);
            btnSwitchMode.setText("Cambiar a Cuenta Atrás");
            btnStartStopTimer.setText("Iniciar");
            tvTitle.setText("Cronómetro");
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
