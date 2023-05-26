package com.example.pio;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HelloApplication extends Application {

    private Pane mainPane;
    private Scene scene;
    private Image clickerImage;
    private Button imageButton;
    private BorderPane root;
    private Text timerText;
    private int secondsElapsed = 0;

    private Text coins;
    private Text counterText;
    private Integer counterCoins = 0;

    private Text perClick;
    private Text perClickText;
    private Integer counterPerClick = 1;

    private Text perSecond;
    private Text perSecondText;
    private Integer counterPerSecond = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Crypto clicker");

        createScreen();

        setImageButton();

        showUser();

        createCounters();

        setCounters();

        createTexts();

        turnOnTimer();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void createScreen() {
        mainPane = new Pane();
        scene = new Scene(mainPane, 1200, 800); // szerokosc, wysokosc

        Color backgroundColor = Color.SADDLEBROWN;
        double backgroundWidth = scene.getWidth() / 3.0;
        BackgroundFill backgroundFill = new BackgroundFill(backgroundColor, null, null);
        Background background = new Background(backgroundFill);

        root = new BorderPane();
        root.setBackground(background);
        root.setPrefWidth(backgroundWidth);
        root.setPrefHeight(scene.getHeight());

        mainPane.getChildren().add(root);

        Color backgroundColor2 = Color.GREEN;
        double backgroundWidth2 = scene.getWidth() / 3.0;
        BackgroundFill backgroundFill2 = new BackgroundFill(backgroundColor2, null, null);
        Background background2 = new Background(backgroundFill2);

        Pane pane2 = new Pane();
        pane2.setBackground(background2);
        pane2.setPrefWidth(backgroundWidth2);
        pane2.setPrefHeight(scene.getHeight());
        pane2.setLayoutX(backgroundWidth);

        Image gifImage = new Image("btc.gif");

        pane2.setBackground(new Background(new BackgroundImage(gifImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

        mainPane.getChildren().add(pane2);

        Color backgroundColor3 = Color.DARKBLUE;
        double backgroundWidth3 = scene.getWidth() / 3.0;
        BackgroundFill backgroundFill3 = new BackgroundFill(backgroundColor3, null, null);
        Background background3 = new Background(backgroundFill3);

        Pane pane3 = new Pane();
        pane3.setBackground(background3);
        pane3.setPrefWidth(backgroundWidth3);
        pane3.setPrefHeight(scene.getHeight());
        pane3.setLayoutX(backgroundWidth * 2);

        double redRectangleWidth = backgroundWidth3 - 40;
        double redRectangleHeight = 300;
        double redRectangleX = 20;
        double redRectangleY = 20;

        Rectangle redRectangle = new Rectangle(redRectangleX, redRectangleY, redRectangleWidth, redRectangleHeight);
        redRectangle.setFill(Color.RED);
        pane3.getChildren().add(redRectangle);

        // Create text inside red rectangle
        String[] redRectangleTexts = {"Marcin       8", "Mateusz       7", "Patryk     9", "Hubert     9"};
        double textY = redRectangleY + (redRectangleHeight / 7);

        for (int i = 0; i < redRectangleTexts.length; i++) {
            Text text = new Text(redRectangleX + 10, textY, redRectangleTexts[i]);
            text.setFont(Font.font("Arial", FontWeight.BOLD, 40));
            text.setFill(Color.WHITE);
            pane3.getChildren().add(text);

            textY += 80; // Adjust the vertical position for each text
        }

        // Create rectangles
        double rectangleWidth = backgroundWidth3 - 40;
        double rectangleHeight = (scene.getHeight() - 450) / 4;
        double yPosition = 400;

        String[] rectangleTexts = {"XmarcinD 1", "DARIUSZ KOWALINSKI 2", "ta krew od razu 3", "Babcia 4"};

        for (int i = 0; i < 4; i++) {
            Rectangle rectangle = new Rectangle(20, yPosition, rectangleWidth, rectangleHeight);
            rectangle.setFill(Color.WHITE);
            pane3.getChildren().add(rectangle);

            Text text = new Text(rectangleTexts[i]);
            text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            text.setFill(Color.BLACK);
            text.setLayoutX(30);
            text.setLayoutY(yPosition + (rectangleHeight / 2) + 5);
            pane3.getChildren().add(text);

            yPosition += rectangleHeight + 10;
        }

        mainPane.getChildren().add(pane3);
    }

    public void setImageButton() {
        // Tworzenie klikalnego obrazka
        clickerImage = new Image("Coin.png");
        ImageView clickerImageView = new ImageView(clickerImage);
        clickerImageView.setFitWidth(200);
        clickerImageView.setFitHeight(200);

        imageButton = new Button();
        imageButton.setGraphic(clickerImageView);
        imageButton.setLayoutX(100);
        imageButton.setLayoutY(300);
        imageButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        ScaleTransition scaleInTransition = new ScaleTransition(Duration.millis(100), imageButton);
        scaleInTransition.setFromX(1.0);
        scaleInTransition.setFromY(1.0);
        scaleInTransition.setToX(1.05);
        scaleInTransition.setToY(1.05);

        ScaleTransition scaleOutTransition = new ScaleTransition(Duration.millis(100), imageButton);
        scaleOutTransition.setFromX(1.05);
        scaleOutTransition.setFromY(1.05);
        scaleOutTransition.setToX(1.0);
        scaleOutTransition.setToY(1.0);

        imageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                counterCoins++;
                counterText.setText(counterCoins.toString());
                perClickText.setText(counterPerClick.toString());
                perSecondText.setText(counterPerSecond.toString());
                scaleInTransition.setOnFinished(e -> scaleOutTransition.play());
                scaleInTransition.play();
            }
        });

        Pane pane = new Pane();
        pane.getChildren().add(imageButton);

        root.setCenter(pane);
    }

    public void createCounters() {
        counterText = new Text("0");
        counterText.setLayoutX(195);
        counterText.setLayoutY(220);
        counterText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        root.getChildren().add(counterText);

        perClickText = new Text("1");
        perClickText.setLayoutX(195);
        perClickText.setLayoutY(610);
        perClickText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        root.getChildren().add(perClickText);

        perSecondText = new Text("0");
        perSecondText.setLayoutX(195);
        perSecondText.setLayoutY(700);
        perSecondText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        root.getChildren().add(perSecondText);
    }

    public void createTexts() {
        coins = new Text("MY CURRENT SCORE:");
        coins.setLayoutX(40);
        coins.setLayoutY(200);
        coins.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        root.getChildren().add(coins);

        perClick = new Text("Score per click:");
        perClick.setLayoutX(75);
        perClick.setLayoutY(570);
        perClick.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        root.getChildren().add(perClick);

        perSecond = new Text("Score per second:");
        perSecond.setLayoutX(75);
        perSecond.setLayoutY(655);
        perSecond.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        root.getChildren().add(perSecond);

        timerText = new Text("Time in seconds: 0");
        timerText.setLayoutX(20);
        timerText.setLayoutY(50);
        timerText.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 20));
    }

    public void showUser() {
        Text highlightedText = new Text("Mateusz");
        highlightedText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        highlightedText.setFill(Color.FUCHSIA);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.PURPLE);
        dropShadow.setRadius(10);
        dropShadow.setSpread(0.6);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(0);

        highlightedText.setEffect(dropShadow);

        highlightedText.setLayoutX(150);
        highlightedText.setLayoutY(120);

        mainPane.getChildren().add(highlightedText);
    }

    public void setCounters() {
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(imageButton);
        stackPane.getChildren().add(counterText);
        stackPane.getChildren().add(perClickText);
        stackPane.getChildren().add(perSecondText);

        stackPane.setAlignment(Pos.CENTER);

        StackPane.setMargin(counterText, new Insets(-340, 0, 0, 0));
        StackPane.setMargin(perClickText, new Insets(400, 0, 0, 0));
        StackPane.setMargin(perSecondText, new Insets(570, 0, 0, 0));

        root.setCenter(stackPane);
    }

    public void turnOnTimer() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    secondsElapsed++;
                    timerText.setText("Time in seconds: " + secondsElapsed);
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        root.getChildren().add(timerText);
    }
}