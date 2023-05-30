package com.example.pio;

import com.example.pio.upgrade.perclick.MyCursor;
import com.example.pio.upgrade.persecond.Bitcoin;
import com.example.pio.upgrade.persecond.DogeCoin;
import com.example.pio.upgrade.persecond.Ethereum;
import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Random;

public class HelloApplication extends Application {

    private Pane mainPane;
    private Scene scene;
    private Image clickerImage;
    private Button imageButton;
    private Button firstUpgrade;
    private Button secondUpgrade;
    private Button thirdUpgrade;
    private Button fourthUpgrade;
    private BorderPane root;
    private Text timerText;
    private long secondsElapsed = 0;
    private boolean isAnimationRunning = false;

    private Text coins;
    private Text counterText;
    private Double counterCoins = 0D;

    private Text perClick;
    private Text perClickText;
    private Double counterPerClick = 1D;

    private Text perSecond;
    private Text perSecondText;
    private Double counterPerSecond = 0D;

    private Text cursor;
    private Text cursorText;
    private Double counterCursor = 0D;

    private final MyCursor myCursor;
    private final Bitcoin bitcoin;
    private final Ethereum ethereum;
    private final DogeCoin dogeCoin;

    public HelloApplication() {
        this.bitcoin = new Bitcoin();
        this.myCursor = new MyCursor();
        this.ethereum = new Ethereum();
        this.dogeCoin = new DogeCoin();
    }

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

        Color backgroundColor = Color.rgb(56,36,36);
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

        Color backgroundColor3 = Color.rgb(26, 99, 136, 1.0);
        double backgroundWidth3 = scene.getWidth() / 3.0;
        BackgroundFill backgroundFill3 = new BackgroundFill(backgroundColor3, null, null);
        Background background3 = new Background(backgroundFill3);

        Pane pane3 = new Pane();
        pane3.setBackground(background3);
        pane3.setPrefWidth(backgroundWidth3);
        pane3.setPrefHeight(scene.getHeight());
        pane3.setLayoutX(backgroundWidth * 2);

        mainPane.getChildren().add(pane3);

        createUpgrades();
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
                var df = new DecimalFormat("#.##");
                counterCoins += myCursor.getCoinsPerClick();
                counterText.setText(counterCoins.toString());
                counterText.setText(df.format(counterCoins));
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
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.PURPLE);
        dropShadow.setRadius(10);
        dropShadow.setSpread(0.6);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(0);

        counterText = new Text("0.0");
        counterText.setLayoutX(195);
        counterText.setLayoutY(220);
        counterText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        counterText.setFill(Color.FUCHSIA);
        counterText.setEffect(dropShadow);
        counterText.setLayoutX(150);
        counterText.setLayoutY(120);

        root.getChildren().add(counterText);

        perClickText = new Text("1.0");
        perClickText.setLayoutX(195);
        perClickText.setLayoutY(610);
        perClickText.setFill(Color.FUCHSIA);
        perClickText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        perClickText.setEffect(dropShadow);
        perClickText.setLayoutX(150);
        perClickText.setLayoutY(120);

        root.getChildren().add(perClickText);

        perSecondText = new Text("0.0");
        perSecondText.setLayoutX(195);
        perSecondText.setLayoutY(700);
        perSecondText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        perSecondText.setFill(Color.FUCHSIA);
        perSecondText.setEffect(dropShadow);
        perSecondText.setLayoutX(150);
        perSecondText.setLayoutY(120);

        root.getChildren().add(perSecondText);
    }

    public void createTexts() {
        coins = new Text("MY CURRENT SCORE:");
        coins.setLayoutX(40);
        coins.setLayoutY(200);
        coins.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        coins.setFill(Color.WHITE);
        root.getChildren().add(coins);

        perClick = new Text("Score per click:");
        perClick.setLayoutX(75);
        perClick.setLayoutY(570);
        perClick.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        perClick.setFill(Color.WHITE);
        root.getChildren().add(perClick);

        perSecond = new Text("Score per second:");
        perSecond.setLayoutX(75);
        perSecond.setLayoutY(655);
        perSecond.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        perSecond.setFill(Color.WHITE);
        root.getChildren().add(perSecond);

        timerText = new Text("Time in seconds: 0");
        timerText.setLayoutX(20);
        timerText.setLayoutY(50);
        timerText.setFill(Color.WHITE);
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
                    var df = new DecimalFormat("#.##");

                    counterCoins += dogeCoin.getCoinsPerSecond();
                    counterCoins += bitcoin.getCoinsPerSecond();
                    counterCoins += ethereum.getCoinsPerSecond();
                    counterText.setText(counterCoins.toString());
                    counterText.setText(df.format(counterCoins));
                    perClickText.setText(counterPerClick.toString());
                    perSecondText.setText(counterPerSecond.toString());

                    secondsElapsed++;
                    timerText.setText("Time in seconds: " + secondsElapsed);
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        root.getChildren().add(timerText);
    }

    public void createUpgrades() {
        firstUpgrade = new Button();
        firstUpgrade.setPrefWidth(350);
        firstUpgrade.setPrefHeight(80);

        firstUpgrade.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 10;");

        VBox vbox = new VBox(5);

        Text buttonTextCursor = new Text("Cursor");
        buttonTextCursor.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        buttonTextCursor.setFill(Color.BLACK);

        Text cursorValue = new Text("1");
        cursorValue.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        cursorValue.setFill(Color.BLACK);

        vbox.getChildren().addAll(buttonTextCursor, cursorValue);
        vbox.setAlignment(Pos.CENTER_LEFT);
       // vbox.setPadding(new Insets(0, 0, 0, 10));

        firstUpgrade.setGraphic(vbox);

        firstUpgrade.setLayoutX(830);
        firstUpgrade.setLayoutY(400);

        firstUpgrade.setOnMousePressed(event -> firstUpgrade.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 10;"));

        // Zmiana stylu po puszczeniu przycisku
        firstUpgrade.setOnMouseReleased(event -> firstUpgrade.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 10;"));

        firstUpgrade.setOnMouseEntered(event -> {
            firstUpgrade.setCursor(Cursor.HAND);
        });

        firstUpgrade.setOnMouseExited(event -> {
            firstUpgrade.setCursor(Cursor.DEFAULT);
        });


        
        firstUpgrade.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (myCursor.isAvailableToBuy(counterCoins)) {
                    counterCoins -= myCursor.buyCursor();
                } else {
                    playTextAnimation();
                }
                var df = new DecimalFormat("#.##");
                counterText.setText(counterCoins.toString());
                counterText.setText(df.format(counterCoins));
                perClickText.setText(counterPerClick.toString());
                perSecondText.setText(counterPerSecond.toString());
            }
        });

        // drugi

        secondUpgrade = new Button();
        secondUpgrade.setPrefWidth(350);
        secondUpgrade.setPrefHeight(80);

        secondUpgrade.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 10;");

        Text buttonTextDogeCoin = new Text("Dogecoin");
        buttonTextDogeCoin.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        buttonTextDogeCoin.setFill(Color.BLACK);

        Text dogeCoinValue = new Text(String.valueOf(dogeCoin.getPrice()));
        dogeCoinValue.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        dogeCoinValue.setFill(Color.BLACK);

        VBox vbox2 = new VBox(5);
        vbox2.getChildren().addAll(buttonTextDogeCoin, dogeCoinValue);
        vbox2.setAlignment(Pos.CENTER_LEFT);
        vbox2.setPadding(new Insets(0, 0, 0, 10));

        secondUpgrade.setGraphic(vbox2);

        secondUpgrade.setLayoutX(830);
        secondUpgrade.setLayoutY(500);

        secondUpgrade.setOnMousePressed(event -> secondUpgrade.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 10;"));

        // Zmiana stylu po puszczeniu przycisku
        secondUpgrade.setOnMouseReleased(event -> secondUpgrade.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 10;"));

        secondUpgrade.setOnMouseEntered(event -> {
            secondUpgrade.setCursor(Cursor.HAND);
        });

        secondUpgrade.setOnMouseExited(event -> {
            secondUpgrade.setCursor(Cursor.DEFAULT);
        });


        secondUpgrade.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (dogeCoin.isAvailableToBuy(counterCoins)) {
                    counterCoins -= dogeCoin.buyCrypto();
                } else {
                    playTextAnimation();
                }
                var df = new DecimalFormat("#.##");
                counterText.setText(counterCoins.toString());
                counterText.setText(df.format(counterCoins));
                perClickText.setText(counterPerClick.toString());
                perSecondText.setText(counterPerSecond.toString());
            }
        });

        // trzeci

        thirdUpgrade = new Button();
        thirdUpgrade.setPrefWidth(350);
        thirdUpgrade.setPrefHeight(80);

        thirdUpgrade.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 10;");

        Text buttonTextEthereum = new Text("Ethereum");
        buttonTextEthereum.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        buttonTextEthereum.setFill(Color.BLACK);

        Text etherumValue = new Text(String.valueOf(ethereum.getPrice()));
        etherumValue.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        etherumValue.setFill(Color.BLACK);

        VBox vbox3 = new VBox(5);
        vbox3.getChildren().addAll(buttonTextEthereum, etherumValue);
        vbox3.setAlignment(Pos.CENTER_LEFT);
        vbox3.setPadding(new Insets(0, 0, 0, 10));

        thirdUpgrade.setGraphic(vbox3);

        thirdUpgrade.setLayoutX(830);
        thirdUpgrade.setLayoutY(600);

        thirdUpgrade.setOnMousePressed(event -> thirdUpgrade.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 10;"));

        // Zmiana stylu po puszczeniu przycisku
        thirdUpgrade.setOnMouseReleased(event -> thirdUpgrade.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 10;"));

        thirdUpgrade.setOnMouseEntered(event -> {
            thirdUpgrade.setCursor(Cursor.HAND);
        });

        thirdUpgrade.setOnMouseExited(event -> {
            thirdUpgrade.setCursor(Cursor.DEFAULT);
        });

        thirdUpgrade.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (ethereum.isAvailableToBuy(counterCoins)) {
                    counterCoins -= ethereum.buyCrypto();
                } else {
                    playTextAnimation();
                }
                var df = new DecimalFormat("#.##");
                counterText.setText(counterCoins.toString());
                counterText.setText(df.format(counterCoins));

                perClickText.setText(counterPerClick.toString());
                perSecondText.setText(counterPerSecond.toString());
            }
        });

        // czwarty

        fourthUpgrade = new Button();
        fourthUpgrade.setPrefWidth(350);
        fourthUpgrade.setPrefHeight(80);

        fourthUpgrade.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 10;");

        Text buttonTextBitcoin = new Text("Bitcoin");
        buttonTextBitcoin.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        buttonTextBitcoin.setFill(Color.BLACK);

        Text bitcoinValue = new Text(String.valueOf(bitcoin.getPrice()));
        bitcoinValue.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        bitcoinValue.setFill(Color.BLACK);

        VBox vbox4 = new VBox(5);
        vbox4.getChildren().addAll(buttonTextBitcoin, bitcoinValue);
        vbox4.setAlignment(Pos.CENTER_LEFT);
        vbox4.setPadding(new Insets(0, 0, 0, 10));

        fourthUpgrade.setGraphic(vbox4);

        fourthUpgrade.setLayoutX(830);
        fourthUpgrade.setLayoutY(700);

        fourthUpgrade.setOnMousePressed(event -> fourthUpgrade.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 10;"));

        // Zmiana stylu po puszczeniu przycisku
        fourthUpgrade.setOnMouseReleased(event -> fourthUpgrade.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 10;"));

        fourthUpgrade.setOnMouseEntered(event -> {
            fourthUpgrade.setCursor(Cursor.HAND);
        });

        fourthUpgrade.setOnMouseExited(event -> {
            fourthUpgrade.setCursor(Cursor.DEFAULT);
        });

        fourthUpgrade.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (bitcoin.isAvailableToBuy(counterCoins)) {
                    counterCoins -= bitcoin.buyCrypto();
                } else {
                    playTextAnimation();
                }
                var df = new DecimalFormat("#.##");
                counterText.setText(counterCoins.toString());
                counterText.setText(df.format(counterCoins));
                perClickText.setText(counterPerClick.toString());
                perSecondText.setText(counterPerSecond.toString());

            }
        });

        mainPane.getChildren().add(firstUpgrade);
        mainPane.getChildren().add(secondUpgrade);
        mainPane.getChildren().add(thirdUpgrade);
        mainPane.getChildren().add(fourthUpgrade);
    }


    private void playTextAnimation() {
        Text text = new Text("Not enough money");
        text.setFont(Font.font("Arial", 26));

        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), text);
        transition.setFromX(490);
        transition.setToX(490);
        transition.setFromY(770);
        transition.setToY(650);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), text);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);

        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fadeTransition.play();
            }
        });

        transition.play();

        mainPane.getChildren().add(text);
    }

    private void addToScorePerClick() {

    }
}