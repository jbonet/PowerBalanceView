package xyz.jbonet.powerbalanceview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jbonet on 22/05/2017.
 */

public class PowerBalanceView extends View {

    // Values used in onDraw()
    private Paint paint = new Paint();
    private double value = 0;
    private float percent = 0;
    private double maxValue = 0;

    // Axis params
    private int axisColor = Color.BLACK;
    private int axisSize = 8;

    // Background and progress bar params;
    private int backgroundColor = Color.WHITE;
    private int barColor = Color.GREEN;

    // Steps params
    private int stepBarColor = Color.BLACK;
    private int stepBarSize = 2;
    private int steps = 15;

    // Gradients
    private boolean gradientEnabled = false;

    public PowerBalanceView(Context context) {
        super(context);
    }

    public PowerBalanceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet);
    }

    public PowerBalanceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.PowerBalanceView,
                0, 0);

        try {
            steps = a.getInteger(R.styleable.PowerBalanceView_steps, 15);
            barColor = a.getColor(R.styleable.PowerBalanceView_barColor, Color.GREEN);
            stepBarColor = a.getColor(R.styleable.PowerBalanceView_stepBarColor, Color.BLACK);
            stepBarSize = a.getInteger(R.styleable.PowerBalanceView_stepBarSize, 2);
            backgroundColor = a.getColor(R.styleable.PowerBalanceView_backgroundColor, Color.WHITE);
            axisSize = a.getInteger(R.styleable.PowerBalanceView_axisSize, 8);
            axisColor = a.getColor(R.styleable.PowerBalanceView_axisColor, Color.BLACK);
            gradientEnabled = a.getBoolean(R.styleable.PowerBalanceView_gradient, false);

            steps = checkValue(steps);
            stepBarSize = checkValue(stepBarSize);
            axisSize = checkValue(axisSize);
        } finally {
            a.recycle();
        }
    }

    private int checkValue(int value) {
        if (value % 2 == 0) return value;
        else return value - 1;
    }

    public void setValue(double value) {
        this.value = value;
        if (Math.abs(value) > Math.abs(maxValue)) maxValue = Math.abs(value);
        percent = (float) Math.abs(value) / (float) maxValue;
        this.invalidate();
    }

    public void setValues(double first, double second) {
        setValue(first - second);
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = Math.abs(maxValue);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(backgroundColor);
        canvas.drawPaint(paint);
        paint.setAntiAlias(true);
        int canvasW = getWidth();
        int canvasH = getHeight();
        int axisWidth = axisSize;
        int left = canvasW / 2 - (axisWidth / 2);
        int top = 0;
        int right = canvasW / 2 + (axisWidth / 2);
        paint.setColor(axisColor);
        canvas.drawRect(left, top, right, canvasH, paint);
        paint.setColor(stepBarColor);


        // Draw Step Bars
        float stepSize = (canvasW / 2 - axisWidth / 2) / (float) (steps / 2);
        for (int i = 0; i < steps / 2; i++) {
            canvas.drawRect(i * stepSize, 0, (i * stepSize + (stepBarSize)), canvasH, paint);

            float padding = canvasW / 2 + axisWidth / 2 - stepBarSize;
            int j = i + 1;
            canvas.drawRect(padding + j * stepSize, 0, padding + (j * stepSize + (stepBarSize)), canvasH, paint);
        }

        // Draw Progress Bar
        paint.setColor(barColor);
        if (value > 0) {
            if (percent > 0.75f && isGradientEnabled()) {
                float greenLength = left - (canvasW / 2) * 0.75f;
                canvas.drawRect(greenLength, 0, right - axisWidth, canvasH, paint);
                paint.setColor(Color.YELLOW);
                canvas.drawRect(left - (canvasW / 2) * percent, 0, greenLength, canvasH, paint);
                if (percent > 0.9f) {
                    paint.setColor(Color.RED);
                    canvas.drawRect(left - (canvasW / 2) * percent, 0, left - (canvasW / 2) * 0.9f, canvasH, paint);
                }
            } else {
                canvas.drawRect(left - (canvasW / 2) * percent, 0, right - axisWidth, canvasH, paint);
            }
        } else {
            int left2 = canvasW / 2 + axisWidth / 2;
            if (percent > 0.75f && isGradientEnabled()) {
                canvas.drawRect(left2, 0, left2 + (canvasW / 2) * 0.75f, canvasH, paint);
                paint.setColor(Color.YELLOW);
                canvas.drawRect(left2 + (canvasW / 2) * 0.75f , 0, left2 + (canvasW / 2) *  percent, canvasH, paint);
                if (percent > 0.9f) {
                    paint.setColor(Color.RED);
                    canvas.drawRect(left2 + (canvasW / 2) * 0.9f , 0, left2 + (canvasW / 2) * percent, canvasH, paint);
                }
            } else {
                canvas.drawRect(left2, 0, left2 + (canvasW / 2) * percent, canvasH, paint);
            }
        }
    }

    private LinearGradient getGradient(float percent) {

        return null;
    }

    private boolean isGradientEnabled() {
        return gradientEnabled;
    }
}
