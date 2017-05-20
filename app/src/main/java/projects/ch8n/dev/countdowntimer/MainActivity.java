package projects.ch8n.dev.countdowntimer;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    //flags and constans
    private long timeCountInMilliSeconds = 1 * 60 * 1000;
    private CountDownTimer countDownTimer;


    //extra classes
    private static class TimerStatus {
        private static final int STARTED = 188;
        private static final int STOPPED = 187;
    }

    //views
    @BindView(R.id.progressBarCircle)
    ProgressBar progressBarCircle;

    @BindView(R.id.editTextMinute)
    EditText editTextMinute;

    @BindView(R.id.textViewTime)
    TextView textViewTime;

    @BindView(R.id.buttonReset)
    Button buttonReset;

    @BindView(R.id.buttonStartStop)
    Button buttonStartStop;

    //Objects and variables
    int status = TimerStatus.STOPPED;
    ObjectAnimator smoothAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        buttonReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        buttonStartStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startStop();
            }
        });

    }


    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }

    private void startStop() {

        if (status == TimerStatus.STOPPED) {

            // call to initialize the timer values
            setTimerValues();
            // call to initialize the progress bar values
            setProgressBarValues();
            // showing the reset icon
            buttonReset.setEnabled(true);
            // making edit text not editable
            editTextMinute.setEnabled(false);
            // changing the timer status to started
            status = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

        } else {

            // hiding the reset icon
            buttonReset.setEnabled(false);
            // making edit text editable
            editTextMinute.setEnabled(true);
            // changing the timer status to stopped
            status = TimerStatus.STOPPED;
            stopCountDownTimer();

        }

    }

    private void startCountDownTimer() {

        smoothAnimation = ObjectAnimator.ofInt(progressBarCircle, "progress", progressBarCircle.getProgress(), progressBarCircle.getMax());
        smoothAnimation.setDuration(500);
        smoothAnimation.setInterpolator(new AccelerateInterpolator());

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 10) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.e("getMax", "" + progressBarCircle.getMax());
                Log.e("getprogres", "" + progressBarCircle.getProgress());
                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
                progressBarCircle.setProgress((int) (timeCountInMilliSeconds / 10 - millisUntilFinished / 10));
            }

            @Override
            public void onFinish() {

                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                setProgressBarValues();
                // hiding the reset icon
                buttonReset.setEnabled(false);
                // making edit text editable
                editTextMinute.setEnabled(true);
                // changing the timer status to stopped
                status = TimerStatus.STOPPED;
                smoothAnimation.end();
            }

        }.start();
        smoothAnimation.start();
        countDownTimer.start();
    }

    private void stopCountDownTimer() {
        countDownTimer.cancel();
        smoothAnimation.end();
    }

    private void setProgressBarValues() {
        progressBarCircle.setMax((int) (timeCountInMilliSeconds / 10));
        progressBarCircle.setProgress((int) (timeCountInMilliSeconds / 10));
        Log.e("progres", "" + (timeCountInMilliSeconds / 10));
    }

    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;
    }

    private void setTimerValues() {
        int time = 0;
        if (!editTextMinute.getText().toString().isEmpty()) {
            // fetching value from edit text and type cast to integer
            time = Integer.parseInt(editTextMinute.getText().toString().trim());
        } else {
            // toast message to fill edit text
            Toast.makeText(getApplicationContext(), "please enter time in minutes", Toast.LENGTH_LONG).show();
        }
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = time * 60 * 1000;
    }


}
