package com.mrsky;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class App extends Application {
    private static volatile long estimate = -1;
    private static volatile boolean alarmCanceled = false;
    private static final Toolkit toolkit = Toolkit.getDefaultToolkit();

    @Override
    public void start(Stage primaryStage) {
        try {
            final Pane root = new Pane();

            final CircularProgressbar circularProgressbar = new CircularProgressbar(200, 10, 3);
            circularProgressbar.draw(0);
            circularProgressbar.relocate(0, 0);

            final Text text = new Text("Status: NaN");
            text.relocate(210, 120);

            final Button button = new Button("Cancel Alarm");
            button.setStyle("-fx-background-color: #A1E7FF;-fx-border-color: #000000;");
            button.setOnMouseClicked(this::onMouseClicked);
            button.onMousePressedProperty().set(event -> button.setStyle("-fx-background-color: #37C5FF;-fx-border-color: #000000;"));
            button.onMouseReleasedProperty().set(event -> button.setStyle("-fx-background-color: #A1E7FF;-fx-border-color: #000000;"));
            button.relocate(210, 150);

            root.getChildren().addAll(circularProgressbar, text, button);
            root.setStyle("-fx-background-color: #BFECFF");
            Scene scene = new Scene(root, 350, 200);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setOnCloseRequest(event -> {
                System.exit(0);
            });
            primaryStage.setTitle("Inventivetalent Hypixel Skyblock DarkAuction Timer");
            primaryStage.show();
            final Thread updateTimeThread = new Thread(() -> updateTime(text));
            updateTimeThread.start();
            final Thread thread = new Thread(() -> {
                try {
                    boolean alarm = true;
                    long time;
                    while (true) {
                        if (estimate == -1) {
                            Thread.sleep(10000);
                            continue;
                        }
                        time = (estimate - System.currentTimeMillis()) / 1000;
                        if (time < 0) {
                            estimate = -1;
                            continue;
                        }
                        text.setText("Status: " + (time / 60) + " min " + (time % 60) + " sec");
                        if ((time / 60) < 5 && alarm) {
                            alarmCanceled = false;
                            while (!alarmCanceled) {
                                toolkit.beep();
                                Thread.sleep(500);
                            }
                            alarm = false;
                        } else {
                            if (!((time / 60) < 5)) {
                                alarm = true;
                            }
                        }
                        circularProgressbar.draw(time / 3600f * 100);
                        Thread.sleep(500);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void onMouseClicked(MouseEvent mouseEvent) {
        alarmCanceled = true;
    }

    private static synchronized void updateTime(final Text text) {
        try {
            while (true) {
                final String response = WebRequest.request();
                if (response == null) {
                    System.out.println("ERROR: \"WebRequest.request()\" returns NULL");
                    text.setText("Status: failed to get data");
                } else {
                    final JSONObject jsonObject = new JSONObject(response);
                    final boolean success = (boolean) jsonObject.get("success");
                    text.setText("Status: success = " + success);
                    if (success) {
                        //final long queryTime = (long) jsonObject.get("queryTime");
                        estimate = (long) jsonObject.get("estimate");
                    } else {
                        System.out.println("ERROR: \"success\" is false");
                        text.setText("Status: \"success\" is false");
                    }
                }
                Thread.sleep(30000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
