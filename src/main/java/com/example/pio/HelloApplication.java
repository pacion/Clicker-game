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
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.*;

import static java.lang.System.in;

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
    private String nickname;

    private Rectangle[] rectangles;
    private Text[] nicknames;
    private Text[] usersCoins;

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
        try {
            primaryStage.setTitle("Crypto clicker");
            primaryStage.setResizable(false);

            createScreen(primaryStage);

            setImageButton();

            createCounters();

            setCounters();

            createTexts();

            turnOnTimer();

            createRectangles();

            primaryStage.setScene(scene);
            primaryStage.show();
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nickname");
            dialog.setHeaderText(null);
            dialog.setContentText("Please enter your nickname:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                nickname = result.get();
            }

            final String finalNickname = nickname;

            showUser();

            Thread socketThread = new Thread(() -> {
                try {
                    Socket socket = new Socket("172.20.10.2", 8080);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                    int i = 0;
                    while (true) {
                        writer.println(counterCoins + ":" + nickname + ":" + socket.getLocalAddress().getHostAddress());

                        if (i == 1000000)
                            break;
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        updateLeaderboard(reader.readLine());
                    }

                    socket.close();
                    reader.close();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            socketThread.start();
        }
        catch(Exception e) {

        }

    }

    private static Map<String, Integer> parseMap(String mapString) {
        Map<String, Integer> map = new HashMap<>();

        String cleanedString = mapString.replace("{", "").replace("}", "").trim();

        if (!cleanedString.isEmpty()) {
            String[] pairs = cleanedString.split(",");

            for (String pair : pairs) {
                String[] keyValue = pair.trim().split("=");

                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    int value = Integer.parseInt(keyValue[1].trim());
                    map.put(key, value);
                }
            }
        }

        return map;
    }

    public void createScreen(Stage primaryStage) {
        mainPane = new Pane();
        scene = new Scene(mainPane, 1200, 800);

        Color backgroundColor = Color.rgb(71,25,171);
        double backgroundWidth = scene.getWidth() / 3.0;
        BackgroundFill backgroundFill = new BackgroundFill(backgroundColor, null, null);
        Background background = new Background(backgroundFill);

        root = new BorderPane();
        root.setBackground(background);
        root.setPrefWidth(backgroundWidth);
        root.setPrefHeight(scene.getHeight());

        mainPane.getChildren().add(root);

        Color backgroundColor2 = Color.LAVENDER;
        double backgroundWidth2 = scene.getWidth() / 3.0;
        BackgroundFill backgroundFill2 = new BackgroundFill(backgroundColor2, null, null);
        Background background2 = new Background(backgroundFill2);

        Pane pane2 = new Pane();
        pane2.setBackground(background2);
        pane2.setPrefWidth(backgroundWidth2);
        pane2.setPrefHeight(scene.getHeight());
        pane2.setLayoutX(backgroundWidth);

        Image gifImage = new Image("btc.gif");

        pane2.setBackground(new Background(new BackgroundImage(gifImage, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

        mainPane.getChildren().add(pane2);

        Color backgroundColor3 = Color.GOLDENROD;
        double backgroundWidth3 = scene.getWidth() / 3.0;
        BackgroundFill backgroundFill3 = new BackgroundFill(backgroundColor3, null, null);
        Background background3 = new Background(backgroundFill3);

        Pane pane3 = new Pane();
        pane3.setBackground(background3);
        pane3.setPrefWidth(backgroundWidth3);
        pane3.setPrefHeight(scene.getHeight());
        pane3.setLayoutX(backgroundWidth * 2);

        mainPane.getChildren().add(pane3);

        createRectangles();

        createUpgrades();
    }

    private void createRectangles() {
        rectangles = new Rectangle[4];
        rectangles[0] = createRectangle(Color.GOLD, 30);
        rectangles[1] = createRectangle(Color.SILVER, 110);
        rectangles[2] = createRectangle(Color.BROWN, 190);
        rectangles[3] = createRectangle(Color.TAN, 270);

        mainPane.getChildren().addAll(rectangles);
        mainPane.getChildren().addAll(nicknames);
        mainPane.getChildren().addAll(usersCoins);
    }

    private Label createColoredLabel(String text, Color backgroundColor) {
        Label label = new Label(text);
        label.setBackground(new Background(new BackgroundFill(backgroundColor, null, null)));
        label.setPadding(new Insets(5));
        return label;
    }

    private Rectangle createRectangle(Color color, double y) {
        nicknames = new Text[4];
        usersCoins = new Text[4];

        int offset = 80;

        for(int i = 0; i < 4; i++){
            nicknames[i] = new Text();
            nicknames[i].setText("-----");
            nicknames[i].setFont(Font.font("Arial", FontWeight.THIN, 24));
            nicknames[i].setFill(Color.rgb(140, 20, 199));
            nicknames[i].setX(850);
            nicknames[i].setY(70 + offset * i);
            usersCoins[i] = new Text();
            usersCoins[i].setText("0");
            usersCoins[i].setFont(Font.font("Arial", FontWeight.EXTRA_LIGHT, 24));
            usersCoins[i].setFill(Color.rgb(89, 180, 79));
            usersCoins[i].setX(1025);
            usersCoins[i].setY(70 + offset * i);
        }

        Rectangle rectangle = new Rectangle();
        rectangle.setFill(color);
        rectangle.setHeight(60);
        rectangle.setWidth(350);
        rectangle.setX(830);
        rectangle.setY(y);

        return rectangle;
    }

    public void setImageButton() {
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
                counterCoins += myCursor.getCoinsPerClick();
                upgrade();
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
        dropShadow.setRadius(20);
        dropShadow.setSpread(0.8);
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
        Text highlightedText = new Text(String.valueOf(nickname));
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

    public void upgrade() {
        var df = new DecimalFormat("#.##");
        //counterText.setText(String.valueOf(counterCoins));
        counterText.setText(df.format(counterCoins.doubleValue()));
        //perClickText.setText(String.valueOf(myCursor.getCoinsPerClick()));
        perSecondText.setText(df.format(counterPerSecond.doubleValue()));
        perClickText.setText(df.format(myCursor.getCoinsPerClick().doubleValue()));
       // perSecondText.setText(df.format(String.valueOf(counterPerSecond)));
    }

    public void turnOnTimer() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    upgrade();
                    counterCoins += dogeCoin.getCoinsPerSecond();
                    counterCoins += bitcoin.getCoinsPerSecond();
                    counterCoins += ethereum.getCoinsPerSecond();
                    secondsElapsed++;
                    timerText.setText("Time in seconds: " + secondsElapsed);
                }));

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

        Text cursorValue = new Text(String.valueOf(myCursor.getPrice()));
        cursorValue.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        cursorValue.setFill(Color.BLACK);

        vbox.getChildren().addAll(buttonTextCursor, cursorValue);
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setPadding(new Insets(0, 0, 0, 10));

        firstUpgrade.setGraphic(vbox);

        firstUpgrade.setLayoutX(830);
        firstUpgrade.setLayoutY(400);

        firstUpgrade.setOnMousePressed(event -> firstUpgrade.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 10;"));

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
                upgrade();
                cursorValue.setText(String.valueOf(myCursor.getPrice()));
            }
        });

        secondUpgrade = new Button();
        secondUpgrade.setPrefWidth(350);
        secondUpgrade.setPrefHeight(80);

        secondUpgrade.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 10;");

        Text buttonTextDogeCoin = new Text("Dogecoin");
        buttonTextDogeCoin.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        buttonTextDogeCoin.setFill(Color.BLACK);

        Text dogeCoinValue = new Text(String.valueOf(dogeCoin.getPrice()));
        dogeCoinValue.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        dogeCoinValue.setFill(Color.BLACK);

        VBox vbox2 = new VBox(5);
        vbox2.getChildren().addAll(buttonTextDogeCoin, dogeCoinValue);
        vbox2.setAlignment(Pos.CENTER_LEFT);
        vbox2.setPadding(new Insets(0, 0, 0, 10));

        secondUpgrade.setGraphic(vbox2);

        secondUpgrade.setLayoutX(830);
        secondUpgrade.setLayoutY(500);

        secondUpgrade.setOnMousePressed(event -> secondUpgrade.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 10;"));

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

                counterPerSecond = ethereum.getCoinsPerSecond() + bitcoin.getCoinsPerSecond() + dogeCoin.getCoinsPerSecond();
                upgrade();
                dogeCoinValue.setText(String.valueOf(dogeCoin.getPrice()));
            }
        });

        thirdUpgrade = new Button();
        thirdUpgrade.setPrefWidth(350);
        thirdUpgrade.setPrefHeight(80);

        thirdUpgrade.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 14;");

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
                counterPerSecond = ethereum.getCoinsPerSecond() + bitcoin.getCoinsPerSecond() + dogeCoin.getCoinsPerSecond();
                upgrade();
                etherumValue.setText(String.valueOf(ethereum.getPrice()));
            }
        });

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
                counterPerSecond = ethereum.getCoinsPerSecond() + bitcoin.getCoinsPerSecond() + dogeCoin.getCoinsPerSecond();
                upgrade();
                bitcoinValue.setText(String.valueOf(bitcoin.getPrice()));
            }
        });

        mainPane.getChildren().add(firstUpgrade);
        mainPane.getChildren().add(secondUpgrade);
        mainPane.getChildren().add(thirdUpgrade);
        mainPane.getChildren().add(fourthUpgrade);
    }

    private void updateLeaderboard(String message) {
        if (message != null) {
            String[] players = message.split("/");
            DecimalFormat df = new DecimalFormat("#.##");

            for (int i = 0; i < players.length; i++) {
                String[] playerStat = players[i].split(":");
                String nickname = playerStat[0];
                String coins = playerStat[1];

                nicknames[i].setText(nickname);

                double score = Double.parseDouble(coins);
                String formattedScore = df.format(score);
                usersCoins[i].setText(formattedScore);
                usersCoins[i].setTextAlignment(TextAlignment.RIGHT);
            }
        }
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


}