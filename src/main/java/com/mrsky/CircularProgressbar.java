package com.mrsky;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.TextAlignment;

public class CircularProgressbar extends Canvas {
    private final Color progressColor;
    private final Color centerBorderColor;
    private final Color centerColor;
    private final Color fontColor;
    private final double progressWith;
    private final double centerBorderWith;

    public CircularProgressbar(double radius, double progressWith, double centerBorderWith) {
        super(radius, radius);
        this.progressWith = progressWith;
        this.centerBorderWith = centerBorderWith;
        final GraphicsContext gc = getGraphicsContext2D();
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        progressColor = Color.web("#FFAA00");
        centerBorderColor = Color.web("#FF4100");
        centerColor = Color.web("#FFFFFF");
        fontColor = Color.web("#000000");
    }

    public void draw(float percent) {
        percent = Math.max(Math.min(percent, 100), 0);
        percent = Float.parseFloat(String.format("%.1f", percent));
        final GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        gc.setFill(progressColor);
        gc.fillArc(0, 0, getWidth(), getHeight(), 90, -(percent / 100 * 360), ArcType.ROUND);

        gc.setFill(centerBorderColor);
        gc.fillArc(progressWith, progressWith, getWidth() - (progressWith * 2), getHeight() - (progressWith * 2), 0, -360, ArcType.ROUND);

        gc.setFill(centerColor);
        gc.fillArc(progressWith + centerBorderWith, progressWith + centerBorderWith, getWidth() - ((progressWith + centerBorderWith) * 2), getHeight() - ((progressWith + centerBorderWith) * 2), 0, -360, ArcType.ROUND);

        gc.setFill(fontColor);
        gc.fillText(percent + " %", getWidth() / 2, getHeight() / 2);
    }
}
