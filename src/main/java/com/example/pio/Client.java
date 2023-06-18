package com.example.pio;

import com.example.pio.upgrade.perclick.MyCursor;
import com.example.pio.upgrade.persecond.Bitcoin;
import com.example.pio.upgrade.persecond.DogeCoin;
import com.example.pio.upgrade.persecond.Ethereum;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Optional;

public class Client extends Application {

    private static final String IP_HOST = "localhost";

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

    private Text coins;
    private Text counterText;
    private Double currentCoins = 0D;
    private Double allCoins = 0D;

    private Text perClick;
    private Text perClickText;

    private Text perSecond;
    private Text perSecondText;
    private Double counterPerSecond = 0D;

    private final MyCursor myCursor;
    private final Bitcoin bitcoin;
    private final Ethereum ethereum;
    private final DogeCoin dogeCoin;
    private String nickname;

    private Rectangle[] rectangles;
    private Text[] nicknames;
    private Text[] usersCoins;
    private Text[] userUpgrades;
    private Text highlightedText;

    public Client() {
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
            nickname = getUserNickname();
            highlightedText = new Text(String.valueOf(nickname));

            createScreen(primaryStage);
            setImageButton();
            createCounters();
            setCounters();
            createTexts();
            turnOnTimer();
            createRectangles();

            primaryStage.setScene(scene);
            primaryStage.show();

            Thread socketThread = new Thread(() -> {
                try {
                    connectAndSendData(nickname);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            socketThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getUserNickname() {
        TextInputDialog dialog = createNicknameDialog();

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String userInput = dialog.getEditor().getText();
                if (userInput.length() < 2) {

                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("ERROR");
                    errorAlert.setContentText("Not enough characters");
                    errorAlert.showAndWait();
                    dialog.getEditor().clear();
                    return null;
                }
                if (userInput.length() >= 10) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("ERROR");
                    errorAlert.setContentText("Too many characters");
                    errorAlert.showAndWait();
                    dialog.getEditor().clear();
                    return null;
                }
                return userInput;
            }
            return null;
        });

        Optional<String> result = Optional.empty();
        while (!result.isPresent() || result.get().length() < 2 || result.get().length() > 15) {
            result = dialog.showAndWait();
        }

        return result.orElse("");
    }

    private TextInputDialog createNicknameDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nickname");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter your nickname:");
        return dialog;
    }

    private void connectAndSendData(String nickname) throws IOException {
        try (Socket socket = createSocket(IP_HOST, 8080);
             BufferedReader reader = createReader(socket);
             PrintWriter writer = createWriter(socket)) {

            int i = 0;
            while (true) {
                sendData(writer, nickname, socket);

                if (i == 1000000) {
                    break;
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                var line = reader.readLine();

                if(secondsElapsed == 60 * 10) {
                    endGame(line);
                }

                updateLeaderboard(line);
            }
        }
    }

    private void endGame(String line) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Time ended");
            alert.setHeaderText(null);

            String[] players = line.split("/");
            String[] playerStat = players[0].split(":");
            String nick = playerStat[0];
            String nickCoins = playerStat[1];

            alert.setContentText("Winner: " + nick + " with " + nickCoins + " coins " + "\n" + "Your coins: " + allCoins);

            alert.showAndWait();
        });
        System.exit(0);
    }

    private Socket createSocket(String host, int port) throws IOException {
        return new Socket(host, port);
    }

    private BufferedReader createReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private PrintWriter createWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }

    private void sendData(PrintWriter writer, String nickname, Socket socket) {
        writer.println(allCoins + ":" + nickname + ":" + socket.getLocalAddress().getHostAddress());
    }

    public void createScreen(Stage primaryStage) {
        double sceneWidth = 1200;
        double sceneHeight = 800;

        mainPane = new Pane();
        scene = new Scene(mainPane, sceneWidth, sceneHeight);

        createLeftPane(sceneWidth, sceneHeight);
        createCenterPane(sceneWidth, sceneHeight);
        createRightPane(sceneWidth, sceneHeight);

        createRectangles();
        createUpgrades();
        createHelpButton();

        configurePrimaryStage(primaryStage);

        primaryStage.show();
    }

    private void createLeftPane(double sceneWidth, double sceneHeight) {
        Pane leftPane = createPaneWithBackground(sceneWidth / 3.0, sceneHeight, 0, Color.rgb(71, 25, 171));
        root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.rgb(71, 25, 171), null, null)));
        root.setPrefWidth(sceneWidth / 3.0);
        root.setPrefHeight(sceneHeight);
        mainPane.getChildren().add(root);
    }

    private void createCenterPane(double sceneWidth, double sceneHeight) {
        Pane centerPane = createPaneWithBackground(sceneWidth / 3.0, sceneHeight, sceneWidth / 3.0, Color.LAVENDER);
        Image gifImage = new Image("btc.gif");
        centerPane.setBackground(new Background(new BackgroundImage(gifImage, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
        mainPane.getChildren().add(centerPane);
    }

    private void createRightPane(double sceneWidth, double sceneHeight) {
        Pane rightPane = createPaneWithBackground(sceneWidth / 3.0, sceneHeight, sceneWidth / 3.0 * 2, Color.GOLDENROD);
        mainPane.getChildren().add(rightPane);
    }

    private Pane createPaneWithBackground(double width, double height, double translateX, Paint fill) {
        Pane pane = new Pane();
        pane.setPrefWidth(width);
        pane.setPrefHeight(height);
        pane.setTranslateX(translateX);
        pane.setBackground(new Background(new BackgroundFill(fill, null, null)));
        return pane;
    }

    private void configurePrimaryStage(Stage primaryStage) {
        primaryStage.setTitle("Crypto clicker");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
    }

    private void createHelpButton() {
        Rectangle clickableDiv = createClickableDiv();
        StackPane.setAlignment(clickableDiv, Pos.BOTTOM_LEFT);
        StackPane.setMargin(clickableDiv, new Insets(0, 20, 20, 0));
        clickableDiv.setCursor(Cursor.HAND);
        StackPane rootPane = new StackPane();
        rootPane.setLayoutX(1143);
        rootPane.setLayoutY(10);
        rootPane.getChildren().add(clickableDiv);
        mainPane.getChildren().add(rootPane);

        clickableDiv.setOnMouseClicked(event -> showPopup());
    }

    private Rectangle createClickableDiv() {
        Rectangle div = new Rectangle(40, 40);
        Image image = new Image("help.png");

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(div.getWidth());
        imageView.setFitHeight(div.getHeight());

        PixelReader pixelReader = image.getPixelReader();
        WritableImage transparentImage = new WritableImage(pixelReader, (int) image.getWidth(), (int) image.getHeight());
        PixelWriter pixelWriter = transparentImage.getPixelWriter();

        for (int y = 0; y < transparentImage.getHeight(); y++) {
            for (int x = 0; x < transparentImage.getWidth(); x++) {
                Color color = pixelReader.getColor(x, y);
                if (color.getOpacity() > 0) {
                    pixelWriter.setColor(x, y, color);
                }
            }
        }

        div.setFill(new ImagePattern(transparentImage));

        return div;
    }

    private void showPopup() {
        Stage popupStage = createPopupStage();
        VBox popupContent = createPopupContent();
        Scene popupScene = createPopupScene(popupContent);

        configurePopupStage(popupStage, popupScene);
        popupStage.setTitle("Help");

        popupStage.showAndWait();
    }

    private Stage createPopupStage() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(mainPane.getScene().getWindow());
        return popupStage;
    }

    private VBox createPopupContent() {
        VBox popupContent = new VBox(10);
        popupContent.setPadding(new Insets(20));

        Label messageLabel = createMessageLabel();
        Button okButton = createOkButton();

        popupContent.getChildren().addAll(messageLabel, okButton);
        popupContent.setAlignment(Pos.CENTER);
        popupContent.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));

        return popupContent;
    }

    private Label createMessageLabel() {
        Label messageLabel = new Label("Witamy w Crypto Cicker!\n\n" +
                "W tej grze twoim celem jest zebranie jak największej ilości monet.\n" +
                "Możesz to robić, klikając na obraz żetonu lub po zebraniu odpowiedniej ilości monet, możesz kupić ulepszenia, które pozwolą Ci zbierać monety automatycznie co sekundę.\n" +
                "Zbieraj monety, pokonuj swoich znajomych i dotrzyj na szczyt rankingu.\n" +
                "Powodzenia!");

        messageLabel.setWrapText(true);
        messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        return messageLabel;
    }

    private Button createOkButton() {
        Button okButton = new Button("OK");
        okButton.setPrefHeight(45);
        okButton.setPrefWidth(75);
        okButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
        okButton.setCursor(Cursor.HAND);
        okButton.setOnAction(event -> ((Stage) okButton.getScene().getWindow()).close());
        return okButton;
    }

    private Scene createPopupScene(VBox popupContent) {
        return new Scene(popupContent, 500, 300);
    }

    private void configurePopupStage(Stage popupStage, Scene popupScene) {
        popupStage.setScene(popupScene);
    }

    private void createRectangles() {
        rectangles = new Rectangle[4];
        rectangles[0] = createRectangle(Color.GOLD, 60);
        rectangles[1] = createRectangle(Color.SILVER, 130);
        rectangles[2] = createRectangle(Color.BROWN, 200);
        rectangles[3] = createRectangle(Color.TAN, 270);

        mainPane.getChildren().addAll(rectangles);
        mainPane.getChildren().addAll(nicknames);
        mainPane.getChildren().addAll(usersCoins);
    }

    private Rectangle createRectangle(Color color, double y) {
        nicknames = new Text[4];
        usersCoins = new Text[4];

        int offset = 70;

        for (int i = 0; i < 4; i++) {
            nicknames[i] = new Text();
            nicknames[i].setText("-----");
            nicknames[i].setFont(Font.font("Arial", FontWeight.THIN, 24));
            nicknames[i].setFill(Color.rgb(140, 20, 199));
            nicknames[i].setX(850);
            nicknames[i].setY(100 + offset * i);
            usersCoins[i] = new Text();
            usersCoins[i].setText("0");
            usersCoins[i].setFont(Font.font("Arial", FontWeight.EXTRA_LIGHT, 24));
            usersCoins[i].setFill(Color.rgb(89, 180, 79));
            usersCoins[i].setX(1035);
            usersCoins[i].setY(100 + offset * i);
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

        imageButton = createButton();
        imageButton.setGraphic(clickerImageView);
        imageButton.setOnAction(event -> {
            currentCoins += myCursor.getCoinsPerClick();
            allCoins += myCursor.getCoinsPerClick();
            bumpCoinStats();
        });

        Pane pane = new Pane();
        pane.getChildren().add(imageButton);

        root.setCenter(pane);
    }

    private Button createButton() {
        Button button = new Button();
        button.setLayoutX(100);
        button.setLayoutY(300);
        button.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        ScaleTransition scaleInTransition = createScaleTransition(button, 1.0, 1.05, 100);
        ScaleTransition scaleOutTransition = createScaleTransition(button, 1.05, 1.0, 100);

        button.setOnMouseEntered(event -> scaleInTransition.play());
        button.setOnMouseExited(event -> scaleOutTransition.play());

        return button;
    }

    private ScaleTransition createScaleTransition(Node node, double fromValue, double toValue, int duration) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(duration), node);
        scaleTransition.setFromX(fromValue);
        scaleTransition.setFromY(fromValue);
        scaleTransition.setToX(toValue);
        scaleTransition.setToY(toValue);
        return scaleTransition;
    }

    public void createCounters() {
        Font font = Font.font("Arial", FontWeight.BOLD, 30);
        DropShadow dropShadow = createDropShadow();

        counterText = createText("0.0", font, Color.FUCHSIA, dropShadow, 195, 220);
        root.getChildren().add(counterText);

        perClickText = createText("1.0", font, Color.FUCHSIA, dropShadow, 195, 610);
        root.getChildren().add(perClickText);

        perSecondText = createText("0.0", font, Color.FUCHSIA, dropShadow, 195, 700);
        root.getChildren().add(perSecondText);

        highlightedText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        highlightedText.setFill(Color.FUCHSIA);

        root.getChildren().add(highlightedText);
    }

    private DropShadow createDropShadow() {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.PURPLE);
        dropShadow.setRadius(20);
        dropShadow.setSpread(0.8);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(0);
        return dropShadow;
    }

    private Text createText(String text, Font font, Color color, Effect effect, double layoutX, double layoutY) {
        Text textNode = new Text(text);
        textNode.setFont(font);
        textNode.setFill(color);
        textNode.setEffect(effect);
        textNode.setLayoutX(layoutX);
        textNode.setLayoutY(layoutY);
        return textNode;
    }

    public void createTexts() {
        Font font = Font.font("Arial", FontWeight.BOLD, 30);
        coins = new Text("MY CURRENT SCORE:");
        coins.setLayoutX(40);
        coins.setLayoutY(190);
        coins.setFont(font);
        coins.setFill(Color.WHITE);
        root.getChildren().add(coins);

        perClick = new Text("Score per click:");
        perClick.setLayoutX(75);
        perClick.setLayoutY(540);
        perClick.setFont(font);
        perClick.setFill(Color.WHITE);
        root.getChildren().add(perClick);

        perSecond = new Text("Score per second:");
        perSecond.setLayoutX(75);
        perSecond.setLayoutY(625);
        perSecond.setFont(font);
        perSecond.setFill(Color.WHITE);
        root.getChildren().add(perSecond);

        timerText = new Text("Time in seconds: 0");
        timerText.setLayoutX(20);
        timerText.setLayoutY(30);
        timerText.setFill(Color.WHITE);
        timerText.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 20));
    }

    public void showUser() {
        highlightedText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        highlightedText.setFill(Color.FUCHSIA);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.PURPLE);
        dropShadow.setRadius(10);
        dropShadow.setSpread(0.6);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(0);

        highlightedText.setEffect(dropShadow);

        highlightedText.setLayoutX(160);
        highlightedText.setLayoutY(120);

        mainPane.getChildren().add(highlightedText);
    }

    public void setCounters() {

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(imageButton);
        stackPane.getChildren().add(counterText);
        stackPane.getChildren().add(perClickText);
        stackPane.getChildren().add(perSecondText);
        stackPane.getChildren().add(highlightedText);

        stackPane.setAlignment(Pos.CENTER);

        StackPane.setMargin(imageButton, new Insets(-50, 0, 0, 0));
        StackPane.setMargin(counterText, new Insets(-350, 0, 0, 0));
        StackPane.setMargin(perClickText, new Insets(350, 0, 0, 0));
        StackPane.setMargin(perSecondText, new Insets(520, 0, 0, 0)); //570 original
        StackPane.setMargin(highlightedText, new Insets(-550, 0, 0, 0));

        root.setCenter(stackPane);
    }

    public void upgrade() {
        DecimalFormat df = new DecimalFormat("#.##");
        Double coinsPerSecond = dogeCoin.getCoinsPerSecond() + bitcoin.getCoinsPerSecond() + ethereum.getCoinsPerSecond();
        counterText.setText(df.format(currentCoins.doubleValue()));
        perSecondText.setText(df.format(coinsPerSecond.doubleValue()));
        perClickText.setText(df.format(myCursor.getCoinsPerClick().doubleValue()));
    }

    public void turnOnTimer() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    upgrade();
                    Double coinsToAdd = dogeCoin.getCoinsPerSecond() + bitcoin.getCoinsPerSecond() + ethereum.getCoinsPerSecond();
                    currentCoins += coinsToAdd;
                    allCoins += coinsToAdd;
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

        doSomethingWithButton(firstUpgrade);

        firstUpgrade.setOnAction(event -> {
            if (myCursor.isAvailableToBuy(currentCoins)) {
                currentCoins -= myCursor.buyCursor();
                cursorValue.setText(String.valueOf(myCursor.getPrice()));
            } else {
                playTextAnimation();
            }
            bumpCoinStats();
        });

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

        doSomethingWithButton(secondUpgrade);

        secondUpgrade.setOnAction(event -> {
            if (dogeCoin.isAvailableToBuy(currentCoins)) {
                currentCoins -= dogeCoin.buyCrypto();
                dogeCoinValue.setText(String.valueOf(dogeCoin.getPrice()));
            } else {
                playTextAnimation();
            }
            bumpCoinStats();
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

        doSomethingWithButton(thirdUpgrade);

        thirdUpgrade.setOnAction(event -> {
            if (ethereum.isAvailableToBuy(currentCoins)) {
                currentCoins -= ethereum.buyCrypto();
                etherumValue.setText(String.valueOf(ethereum.getPrice()));
            } else {
                playTextAnimation();
            }
            bumpCoinStats();
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

        doSomethingWithButton(fourthUpgrade);

        fourthUpgrade.setOnAction(event -> {
            if (bitcoin.isAvailableToBuy(currentCoins)) {
                currentCoins -= bitcoin.buyCrypto();
                bitcoinValue.setText(String.valueOf(bitcoin.getPrice()));
            } else {
                playTextAnimation();
            }
            bumpCoinStats();
        });

        mainPane.getChildren().add(firstUpgrade);
        mainPane.getChildren().add(secondUpgrade);
        mainPane.getChildren().add(thirdUpgrade);
        mainPane.getChildren().add(fourthUpgrade);
        createUpgradeAmountText();
    }

    private void bumpCoinStats() {
        DecimalFormat df = new DecimalFormat("#.##");
        counterText.setText(df.format(currentCoins.doubleValue()));
        counterPerSecond = dogeCoin.getCoinsPerSecond() + ethereum.getCoinsPerSecond() + bitcoin.getCoinsPerSecond();
        perSecondText.setText(df.format(counterPerSecond.doubleValue()));
        perClickText.setText(df.format(myCursor.getCoinsPerClick().doubleValue()));
        Long cursorAmount = myCursor.getAmount();
        Long dogeCoinAmount = dogeCoin.getAmount();
        Long ethereumAmount = ethereum.getAmount();
        Long bitcoinAmount = bitcoin.getAmount();
        userUpgrades[0].setText(df.format(cursorAmount.longValue()));
        userUpgrades[1].setText(df.format(dogeCoinAmount.longValue()));
        userUpgrades[2].setText(df.format(ethereumAmount.longValue()));
        userUpgrades[3].setText(df.format(bitcoinAmount.longValue()));
    }

    private void createUpgradeAmountText() {
        userUpgrades = new Text[4];

        int offset = 100;

        for (int i = 0; i < 4; ++i) {
            userUpgrades[i] = new Text();
            userUpgrades[i].setText("0");
            userUpgrades[i].setFont(Font.font("Arial", FontWeight.EXTRA_LIGHT, 24));
            userUpgrades[i].setFill(Color.rgb(89, 180, 79));
            userUpgrades[i].setX(1125);
            userUpgrades[i].setY(450 + offset * i);
        }

        mainPane.getChildren().addAll(userUpgrades[0], userUpgrades[1], userUpgrades[2], userUpgrades[3]);
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

    private void doSomethingWithButton(Button firstUpgrade) {
        firstUpgrade.setOnMousePressed(event -> firstUpgrade.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 10;"));

        firstUpgrade.setOnMouseReleased(event -> firstUpgrade.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: white; -fx-alignment: baseline-left; -fx-padding: 0 0 0 10;"));

        firstUpgrade.setOnMouseEntered(event -> firstUpgrade.setCursor(Cursor.HAND));

        firstUpgrade.setOnMouseExited(event -> firstUpgrade.setCursor(Cursor.DEFAULT));
    }

    private void playTextAnimation() {
        Text text = new Text("Not enough money");
        text.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        text.setFill(Color.RED);

        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), text);
        transition.setFromX(490);
        transition.setToX(490);
        transition.setFromY(770);
        transition.setToY(650);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), text);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);

        transition.setOnFinished(event -> fadeTransition.play());

        transition.play();

        mainPane.getChildren().add(text);
    }
}