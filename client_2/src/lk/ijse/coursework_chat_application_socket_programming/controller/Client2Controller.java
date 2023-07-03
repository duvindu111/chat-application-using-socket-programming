package lk.ijse.coursework_chat_application_socket_programming.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class Client2Controller implements Initializable {

    @FXML
    private Button btnJoin;

    @FXML
    private Button btnSendClient;

    @FXML
    private Group grpEnterName;

    @FXML
    private Group grpMessageArea;

    @FXML
    private TextArea mainTxtAreaClient;

    @FXML
    private TextField sendTxtAreaClient;

    @FXML
    private Text lblName;

    @FXML
    private TextField txtUsername;

    @FXML
    private ImageView icnCamera;

    @FXML
    private VBox mainVbox;

    @FXML
    private FlowPane emojiContainer;

    @FXML
    private ScrollPane spaneForFlowPane;

    @FXML
    private FlowPane emojiCategoryPane;

    private Socket clientSocket;
    private DataInputStream din;
    private DataOutputStream dout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            clientSocket = new Socket("localhost", 3003);
            din = new DataInputStream(clientSocket.getInputStream());
            dout = new DataOutputStream(clientSocket.getOutputStream());

            mainVbox.setPadding(new Insets(20));
            mainVbox.setSpacing(10);
            emojiContainer.setVisible(false);
            spaneForFlowPane.setVisible(false);
            emojiCategoryPane.setVisible(false);
            emojiContainer.setPadding(new Insets(10));
            emojiContainer.setHgap(20);
            emojiContainer.setVgap(20);
            emojiCategoryPane.setPadding(new Insets(0, 10, 0, 10));
            emojiCategoryPane.setHgap(20);
            displaySmileyEmojis();
            dispayEmojiCategories();

            new Thread(() -> {
                try {
                    while (true) {
                        String message = din.readUTF();

                        if (message.startsWith("image")) {
                            String sender = din.readUTF();
                            Label senderLabel = new Label(sender + ": ");
                            String path = din.readUTF();
                            System.out.println(path);

                            ImageView imageView = new ImageView(new Image("file:" + path));
                            imageView.setFitWidth(192);
                            imageView.setPreserveRatio(true);
                            //////////////
                            imageView.setOnMouseClicked(event -> {
                                // Open the image when clicked
                                try {
                                    File file = new File(path);
                                    Desktop.getDesktop().open(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            /////////////////////
                            Platform.runLater(() -> {
                                mainVbox.getChildren().add(senderLabel);
                            });
                            Platform.runLater(() -> {
                                mainVbox.getChildren().add(imageView);
                            });

                        } else {
                            if (message.startsWith("System")) {
                                Label label = new Label(message);
                                Platform.runLater(() -> {
                                    mainVbox.getChildren().add(label);
                                });
                            } else {
                                Platform.runLater(() -> {
                                    // Create an HBox for right-aligned content
                                    HBox hbox = new HBox();
                                    hbox.setPadding(new Insets(5, 15, 5, 15));
                                    hbox.setStyle("-fx-background-color: #3390ec; -fx-text-fill: #ffffff;-fx-background-radius: 10");
                                    hbox.setAlignment(Pos.BASELINE_LEFT);
                                    Label label = new Label(message);
                                    label.setTextFill(Color.WHITE);
                                    label.setMaxWidth(300);
                                    label.setWrapText(true);
                                    hbox.getChildren().add(label);
                                    hbox.setMaxWidth(Region.USE_PREF_SIZE);
                                    hbox.setMaxHeight(Region.USE_PREF_SIZE);
                                    hbox.setMinHeight(Region.USE_PREF_SIZE);
                                    hbox.setMinWidth(Region.USE_PREF_SIZE);
                                    StackPane stackPane = new StackPane(hbox);
                                    stackPane.setAlignment(Pos.BASELINE_LEFT);
                                    mainVbox.getChildren().add(stackPane);
                                });
                            }
                        }

                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
            }).start();
        } catch (IOException e) {
            System.out.println(e);
        }

        Platform.runLater(() -> {
            // Load the background image
            Image backgroundImage = new Image("lk/ijse/coursework_chat_application_socket_programming/assets/images/back.jpg");
            // Create a BackgroundImage
            BackgroundImage background = new BackgroundImage(backgroundImage,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.REPEAT,
                    BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
            // Set the background to the VBox
            mainVbox.setBackground(new Background(background));

            Stage stage = (Stage) mainVbox.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                event.consume(); // Consume the event to prevent the default close operation

                // Display a confirmation dialog
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(username);
                alert.setHeaderText("Are you sure you want to leave the chat?");
                alert.setContentText("Your data will be lost if you leave the chat application now");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        dout.writeUTF("pass-qpactk3i5710-xkdwisq@ee358fyndvndla98r478t35-jvvhjfv94r82@");
                        dout.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    stage.close(); // Close the window
                }
            });
        });
    }

    public void emoIconOnAction(MouseEvent mouseEvent) {
        if (emojiContainer.isVisible()) {
            emojiContainer.setVisible(false);
            spaneForFlowPane.setVisible(false);
            emojiCategoryPane.setVisible(false);
        } else {
            emojiContainer.setVisible(true);
            spaneForFlowPane.setVisible(true);
            emojiCategoryPane.setVisible(true);
        }
    }

    private void changeColorOfEmojiCategories() {
        ObservableList<Node> children = emojiCategoryPane.getChildren();

        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            // Process the child element here
            child.setStyle("-fx-text-fill: black; -fx-font-size: 30");
        }
    }

    private void dispayEmojiCategories() {
        String[] emojiCategories = {"\uD83D\uDE06", "\uD83D\uDC2C", "\uD83C\uDF30"};

        for (String emoji : emojiCategories) {
            Label emojiLabel = new Label();
            emojiLabel.setText(emoji);
            emojiLabel.setStyle("-fx-font-size: 30");
            if (emoji.equals("\uD83D\uDE06")) {
                emojiLabel.setStyle("-fx-text-fill: red; -fx-font-size: 30;  -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.75), 5, 0, 0, 2)");
            }
            emojiLabel.setOnMouseClicked(event -> {
                String unicode = emoji;

                if (unicode.equals("\uD83D\uDE06")) {
                    displaySmileyEmojis();
                    changeColorOfEmojiCategories();
                    emojiLabel.setStyle("-fx-text-fill: red; -fx-font-size: 30;  -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.75), 5, 0, 0, 2)");
                } else if (unicode.equals("\uD83D\uDC2C")) {
                    displayAnimalEmojis();
                    changeColorOfEmojiCategories();
                    emojiLabel.setStyle("-fx-text-fill: red; -fx-font-size: 30;  -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.75), 5, 0, 0, 2)");
                } else if (unicode.equals("\uD83C\uDF30")) {
                    displayFoodEmojis();
                    changeColorOfEmojiCategories();
                    emojiLabel.setStyle("-fx-text-fill: red; -fx-font-size: 30;  -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.75), 5, 0, 0, 2)");
                }
            });
            emojiCategoryPane.getChildren().add(emojiLabel);
        }
    }

    private void displayAnimalEmojis() {
        emojiContainer.getChildren().clear();
        // Create a button for each emoji
        String[] emojis = {
                "\uD83D\uDC2C", "\uD83D\uDC2D", "\uD83D\uDC2E", "\uD83D\uDC2F", "\uD83D\uDC30",
                "\uD83D\uDC31", "\uD83D\uDC32", "\uD83D\uDC33", "\uD83D\uDC34", "\uD83D\uDC35",
                "\uD83D\uDC36", "\uD83D\uDC37", "\uD83D\uDC38", "\uD83D\uDC39", "\uD83D\uDC3A",
                "\uD83D\uDC3B", "\uD83D\uDC3C", "\uD83D\uDC3D", "\uD83D\uDC3E", "\uD83D\uDC3F",
                "\uD83D\uDC40", "\uD83D\uDC41", "\uD83D\uDC42", "\uD83D\uDC43", "\uD83D\uDC44",
                "\uD83D\uDC45", "\uD83D\uDC46", "\uD83D\uDC47", "\uD83D\uDC48", "\uD83D\uDC49",
                "\uD83D\uDC4A", "\uD83D\uDC4B", "\uD83D\uDC4C", "\uD83D\uDC4D", "\uD83D\uDC4E",
                "\uD83D\uDC4F", "\uD83D\uDC50", "\uD83D\uDC51", "\uD83D\uDC52", "\uD83D\uDC53",
                "\uD83D\uDC54", "\uD83D\uDC55", "\uD83D\uDC56", "\uD83D\uDC57", "\uD83D\uDC58",
                "\uD83D\uDC59", "\uD83D\uDC5A", "\uD83D\uDC5B", "\uD83D\uDC5C", "\uD83D\uDC5D",
                "\uD83D\uDC5E", "\uD83D\uDC5F", "\uD83D\uDC60", "\uD83D\uDC61", "\uD83D\uDC62",
                "\uD83D\uDC63", "\uD83D\uDC64", "\uD83D\uDC65", "\uD83D\uDC66", "\uD83D\uDC67",
                "\uD83D\uDC68", "\uD83D\uDC69", "\uD83D\uDC6A", "\uD83D\uDC6B", "\uD83D\uDC6C",
                "\uD83D\uDC6D", "\uD83D\uDC6E", "\uD83D\uDC6F", "\uD83D\uDC70", "\uD83D\uDC71",
                "\uD83D\uDC72", "\uD83D\uDC73", "\uD83D\uDC74", "\uD83D\uDC75", "\uD83D\uDC76",
                "\uD83D\uDC77", "\uD83D\uDC78", "\uD83D\uDC79", "\uD83D\uDC7A", "\uD83D\uDC7B",
                "\uD83D\uDC7C", "\uD83D\uDC7D", "\uD83D\uDC7E", "\uD83D\uDC7F"
        };

        for (String emoji : emojis) {
            Label emojiLabel = new Label();
            emojiLabel.setText(emoji);
            emojiLabel.setStyle("-fx-font-size: 30");
            emojiLabel.setOnMouseClicked(event -> {
                String unicode = emoji;
                sendTxtAreaClient.appendText(emoji);
                sendTxtAreaClient.requestFocus();
                sendTxtAreaClient.positionCaret(sendTxtAreaClient.getText().length());
            });
            emojiContainer.getChildren().add(emojiLabel);
        }
    }

    private void displayFoodEmojis() {
        emojiContainer.getChildren().clear();
        // Create a button for each emoji
        String[] emojis = {
                "\uD83C\uDF30", "\uD83C\uDF31", "\uD83C\uDF32", "\uD83C\uDF33", "\uD83C\uDF34",
                "\uD83C\uDF35", "\uD83C\uDF36", "\uD83C\uDF37", "\uD83C\uDF38", "\uD83C\uDF39",
                "\uD83C\uDF3A", "\uD83C\uDF3B", "\uD83C\uDF3C", "\uD83C\uDF3D", "\uD83C\uDF3E",
                "\uD83C\uDF3F", "\uD83C\uDF40", "\uD83C\uDF41", "\uD83C\uDF42", "\uD83C\uDF43",
                "\uD83C\uDF44", "\uD83C\uDF45", "\uD83C\uDF46", "\uD83C\uDF47", "\uD83C\uDF48",
                "\uD83C\uDF49", "\uD83C\uDF4A", "\uD83C\uDF4B", "\uD83C\uDF4C", "\uD83C\uDF4D",
                "\uD83C\uDF4E", "\uD83C\uDF4F", "\uD83C\uDF50", "\uD83C\uDF51", "\uD83C\uDF52",
                "\uD83C\uDF53", "\uD83C\uDF54", "\uD83C\uDF55", "\uD83C\uDF56", "\uD83C\uDF57",
                "\uD83C\uDF58", "\uD83C\uDF59", "\uD83C\uDF5A", "\uD83C\uDF5B", "\uD83C\uDF5C",
                "\uD83C\uDF5D", "\uD83C\uDF5E", "\uD83C\uDF5F", "\uD83C\uDF60", "\uD83C\uDF61",
                "\uD83C\uDF62", "\uD83C\uDF63", "\uD83C\uDF64", "\uD83C\uDF65", "\uD83C\uDF66",
                "\uD83C\uDF67", "\uD83C\uDF68", "\uD83C\uDF69", "\uD83C\uDF6A", "\uD83C\uDF6B",
                "\uD83C\uDF6C", "\uD83C\uDF6D", "\uD83C\uDF6E", "\uD83C\uDF6F", "\uD83C\uDF70",
                "\uD83C\uDF71", "\uD83C\uDF72", "\uD83C\uDF73", "\uD83C\uDF74", "\uD83C\uDF75",
                "\uD83C\uDF76", "\uD83C\uDF77", "\uD83C\uDF78", "\uD83C\uDF79", "\uD83C\uDF7A",
                "\uD83C\uDF7B", "\uD83C\uDF7C", "\uD83C\uDF7D", "\uD83C\uDF7E", "\uD83C\uDF7F"

        };

        for (String emoji : emojis) {
            Label emojiLabel = new Label();
            emojiLabel.setText(emoji);
            emojiLabel.setStyle("-fx-font-size: 30");
            emojiLabel.setOnMouseClicked(event -> {
                String unicode = emoji;
                sendTxtAreaClient.appendText(emoji);
                sendTxtAreaClient.requestFocus();
                sendTxtAreaClient.positionCaret(sendTxtAreaClient.getText().length());
            });
            emojiContainer.getChildren().add(emojiLabel);
        }
    }

    private void displaySmileyEmojis() {
        emojiContainer.getChildren().clear();
        // Create a button for each emoji
        String[] emojis = {
                "\u263A", "\uD83D\uDE00", "\uD83D\uDE01", "\uD83D\uDE02", "\uD83D\uDE03",
                "\uD83D\uDE04", "\uD83D\uDE05", "\uD83D\uDE06", "\uD83D\uDE07", "\uD83D\uDE08",
                "\uD83D\uDE09", "\uD83D\uDE0A", "\uD83D\uDE0B", "\uD83D\uDE0C", "\uD83D\uDE0D",
                "\uD83D\uDE0E", "\uD83D\uDE0F", "\uD83D\uDE10", "\uD83D\uDE11", "\uD83D\uDE12",
                "\uD83D\uDE13", "\uD83D\uDE14", "\uD83D\uDE15", "\uD83D\uDE16", "\uD83D\uDE17",
                "\uD83D\uDE18", "\uD83D\uDE19", "\uD83D\uDE1A", "\uD83D\uDE1B", "\uD83D\uDE1C",
                "\uD83D\uDE1D", "\uD83D\uDE1E", "\uD83D\uDE1F", "\uD83D\uDE20", "\uD83D\uDE21",
                "\uD83D\uDE22", "\uD83D\uDE23", "\uD83D\uDE24", "\uD83D\uDE25", "\uD83D\uDE26",
                "\uD83D\uDE27", "\uD83D\uDE28", "\uD83D\uDE29", "\uD83D\uDE2A", "\uD83D\uDE2B",
                "\uD83D\uDE2C", "\uD83D\uDE2D", "\uD83D\uDE2E", "\uD83D\uDE2F", "\uD83D\uDE30",
                "\uD83D\uDE31", "\uD83D\uDE32", "\uD83D\uDE33", "\uD83D\uDE34", "\uD83D\uDE35",
                "\uD83D\uDE36", "\uD83D\uDE37", "\uD83D\uDE38", "\uD83D\uDE39", "\uD83D\uDE3A",
                "\uD83D\uDE3B", "\uD83D\uDE3C", "\uD83D\uDE3D", "\uD83D\uDE3E", "\uD83D\uDE3F",
                "\uD83D\uDE40", "\uD83D\uDE41", "\uD83D\uDE42", "\uD83D\uDE43", "\uD83D\uDE44",
                "\uD83D\uDE45", "\uD83D\uDE46", "\uD83D\uDE47", "\uD83D\uDE48", "\uD83D\uDE49",
                "\uD83D\uDE4A", "\uD83D\uDE4B", "\uD83D\uDE4C", "\uD83D\uDE4D", "\uD83D\uDE4E",
                "\uD83D\uDE4F"
        };

        for (String emoji : emojis) {
            Label emojiLabel = new Label();
            emojiLabel.setText(emoji);
            emojiLabel.setStyle("-fx-font-size: 30");
            emojiLabel.setOnMouseClicked(event -> {
                String unicode = emoji;
                sendTxtAreaClient.appendText(emoji);
                sendTxtAreaClient.requestFocus();
                sendTxtAreaClient.positionCaret(sendTxtAreaClient.getText().length());
            });
            emojiContainer.getChildren().add(emojiLabel);
        }
    }

    @FXML
    void btnSendOnAction(ActionEvent event) {
        String message = sendTxtAreaClient.getText();

        if (!message.isEmpty()) {
            try {
                Platform.runLater(() -> {
                    // Create an HBox for right-aligned content
                    HBox hbox = new HBox();
                    hbox.setPadding(new Insets(5, 15, 5, 15));
                    hbox.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: black;-fx-background-radius: 10");
                    hbox.setAlignment(Pos.BASELINE_RIGHT);
                    Label label = new Label(message + "\n");
                    label.setMaxWidth(300);
                    label.setWrapText(true);
                    hbox.getChildren().add(label);
                    hbox.setMaxWidth(Region.USE_PREF_SIZE);
                    hbox.setMaxHeight(Region.USE_PREF_SIZE);
                    hbox.setMinWidth(Region.USE_PREF_SIZE);
                    hbox.setMinHeight(Region.USE_PREF_SIZE);
                    StackPane stackPane = new StackPane(hbox);
                    stackPane.setAlignment(Pos.BASELINE_RIGHT);
                    mainVbox.getChildren().add(stackPane);
                });
                dout.writeUTF(message);
                dout.flush();
                sendTxtAreaClient.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        emojiContainer.setVisible(false);
        spaneForFlowPane.setVisible(false);
        emojiCategoryPane.setVisible(false);
    }

    public void txtFieldClientOnAction(ActionEvent actionEvent) {
        btnSendOnAction(actionEvent);
    }

    public void txtUsernameOnAction(ActionEvent actionEvent) throws IOException {
        btnJoinOnAction(actionEvent);
    }

    String username;

    public void btnJoinOnAction(ActionEvent actionEvent) throws IOException {
        username = txtUsername.getText();

        if (!username.isEmpty()) {
            grpEnterName.setVisible(false);
            grpMessageArea.setVisible(true);
            lblName.setText(username);
            dout.writeUTF(username);
            dout.flush();
        }
    }

    String imagePath;

    public void icnCameraOnMouseClicked(MouseEvent mouseEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) icnCamera.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();
            System.out.println("Selected image path: " + imagePath);
            dout.writeUTF("image");
            dout.writeUTF(username);
            dout.writeUTF(imagePath);
            dout.flush();

            ImageView imageView = new ImageView(new Image("file:" + imagePath));
            imageView.setFitWidth(192);
            imageView.setPreserveRatio(true);
            Platform.runLater(() -> {
                HBox hbox = new HBox();
                hbox.setAlignment(Pos.BASELINE_RIGHT);
                hbox.getChildren().add(imageView);
                mainVbox.getChildren().add(hbox);
            });
        }
    }
}
