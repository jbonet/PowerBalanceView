package xyz.jbonet.powerbalancesample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import xyz.jbonet.powerbalanceview.PowerBalanceView;

public class MainActivity extends AppCompatActivity {

    private PowerBalanceView powerBalanceView;
    private PowerBalanceView powerBalanceView2;
    private ActionRunner actionRunner;
    private Thread runner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        powerBalanceView = (PowerBalanceView) findViewById(R.id.powerBalanceView);
        Button button = (Button) findViewById(R.id.startStopButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionRunner.shouldRun) {
                    actionRunner.shouldRun = false;
                    try {
                        runner.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    actionRunner.shouldRun = true;
                    runner = new Thread(actionRunner);
                    runner.start();
                }
            }
        });

        powerBalanceView2 = (PowerBalanceView) findViewById(R.id.powerBalanceView2);
        powerBalanceView2.setMaxValue(50);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                powerBalanceView2.setValue(50 - progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar.setProgress(50);
        PowerBalanceView powerBalanceView4 = (PowerBalanceView) findViewById(R.id.powerBalanceView4);
        powerBalanceView4.setMaxValue(50);
        powerBalanceView4.setValue(25);
    }

    @Override
    protected void onPause() {
        super.onPause();
        actionRunner.shouldRun = false;
        try {
            runner.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        actionRunner = new ActionRunner(powerBalanceView);
        runner = new Thread(actionRunner);
        runner.start();
    }

    private static class ActionRunner implements Runnable {
        private final PowerBalanceView powerBalanceView;
        boolean shouldRun = true;

        ActionRunner(PowerBalanceView powerBalanceView) {
            this.powerBalanceView = powerBalanceView;
        }

        @Override
        public void run() {
            double n = 0;
            while (shouldRun) {
                final double nFinal = n;
                powerBalanceView.post(new Runnable() {
                    @Override
                    public void run() {
                        powerBalanceView.setValue(Math.sin(nFinal));
                    }
                });
                try {
                    n += 0.125;
                    Thread.sleep(62);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
