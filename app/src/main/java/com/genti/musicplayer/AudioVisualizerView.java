package com.genti.musicplayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom view that displays an audio spectrum visualizer with colorful bars.
 */
public class AudioVisualizerView extends View {

    private static final int BAR_COUNT = 32;
    private static final int[] COLORS = {
            Color.rgb(255, 100, 0),   // Orange
            Color.rgb(255, 200, 0),   // Yellow
            Color.rgb(0, 255, 100),   // Green
            Color.rgb(0, 200, 255),   // Cyan
            Color.rgb(255, 150, 0),   // Orange-Yellow
            Color.rgb(100, 255, 0),   // Light Green
    };

    private Paint paint;
    private Paint dotPaint;
    private float[] magnitudes;
    private float[] peakValues;
    private long[] peakTimestamps;

    public AudioVisualizerView(Context context) {
        super(context);
        init();
    }

    public AudioVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AudioVisualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);

        dotPaint = new Paint();
        dotPaint.setAntiAlias(true);
        dotPaint.setColor(Color.WHITE);

        magnitudes = new float[BAR_COUNT];
        peakValues = new float[BAR_COUNT];
        peakTimestamps = new long[BAR_COUNT];

        // Initialize with random demo values
        for (int i = 0; i < BAR_COUNT; i++) {
            magnitudes[i] = (float) (Math.random() * 0.7 + 0.1);
        }
    }

    public void updateFFT(byte[] fft) {
        if (fft == null || fft.length == 0) return;

        int step = fft.length / (BAR_COUNT * 2);
        if (step < 1) step = 1;

        for (int i = 0; i < BAR_COUNT && (i * step * 2 + 1) < fft.length; i++) {
            int index = i * step * 2;
            float real = fft[index];
            float imaginary = fft[index + 1];
            float magnitude = (float) Math.sqrt(real * real + imaginary * imaginary);
            magnitude = magnitude / 128f; // Normalize
            magnitude = Math.min(magnitude, 1.0f);

            // Smooth transition
            magnitudes[i] = magnitudes[i] * 0.4f + magnitude * 0.6f;

            // Update peak
            long now = System.currentTimeMillis();
            if (magnitudes[i] > peakValues[i]) {
                peakValues[i] = magnitudes[i];
                peakTimestamps[i] = now;
            } else if (now - peakTimestamps[i] > 1000) {
                peakValues[i] = peakValues[i] * 0.95f;
            }
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        if (width == 0 || height == 0) return;

        float barWidth = (float) width / BAR_COUNT * 0.7f;
        float gap = (float) width / BAR_COUNT * 0.3f;
        float totalBarWidth = barWidth + gap;

        for (int i = 0; i < BAR_COUNT; i++) {
            float barHeight = magnitudes[i] * height * 0.9f;
            float left = i * totalBarWidth;
            float top = height - barHeight;
            float right = left + barWidth;
            float bottom = height;

            // Color based on position and height
            int colorIndex = i % COLORS.length;
            paint.setColor(COLORS[colorIndex]);

            canvas.drawRect(left, top, right, bottom, paint);

            // Draw peak dot
            float peakY = height - (peakValues[i] * height * 0.9f) - 4;
            float dotX = left + barWidth / 2;
            canvas.drawCircle(dotX, peakY, 3f, dotPaint);
        }
    }
}
