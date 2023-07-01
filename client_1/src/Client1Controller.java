import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.ResourceBundle;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import com.vdurmont.emoji.Emoji;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.json.JSONArray;

import javax.swing.*;


public class Client1Controller implements Initializable  {

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
    private TextField txtUsername;

    @FXML
    private ImageView icnCamera;

    @FXML
    private Text lblName;

    @FXML
    private VBox mainVbox;

    @FXML
    private FlowPane emojiContainer;

    @FXML
    private ScrollPane spaneForFlowPane;

    @FXML
    private ScrollPane sPaneVbox;

    private Socket clientSocket;
    private DataInputStream din;
    private DataOutputStream dout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            clientSocket = new Socket("localhost", 3002);
            //clientSocket = new Socket("192.168.180.199", 5003);
            din = new DataInputStream(clientSocket.getInputStream());
            dout = new DataOutputStream(clientSocket.getOutputStream());

            mainVbox.setPadding(new Insets(20));
            mainVbox.setSpacing(10);
            emojiContainer.setVisible(false);
            spaneForFlowPane.setVisible(false);
            emojiContainer.setPadding(new Insets(10));
            emojiContainer.setHgap(20);
            emojiContainer.setVgap(20);
            displayEmojis();

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
//                            imageView.setFitWidth(256);
//                            imageView.setFitHeight(256);
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
                                    hbox.setPadding(new Insets(5,15,5,15));
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
            Image backgroundImage = new Image("assets/images/back.jpg");
            // Create a BackgroundImage
            BackgroundImage background = new BackgroundImage(backgroundImage,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
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
        } else {
            emojiContainer.setVisible(true);
            spaneForFlowPane.setVisible(true);
        }
    }

    private void displayEmojis() {
        emojiContainer.getChildren().clear();

        // Create a button for each emoji
        String[] emojis = {
                "\uD83D\uDE00", "\uD83D\uDE01", "\uD83D\uDE02", "\uD83D\uDE03", "\uD83D\uDE04",
                "\uD83D\uDE05", "\uD83D\uDE06", "\uD83D\uDE07", "\uD83D\uDE08", "\uD83D\uDE09",
                "\uD83D\uDE0A", "\uD83D\uDE0B", "\uD83D\uDE0C", "\uD83D\uDE0D", "\uD83D\uDE0E",
                "\uD83D\uDE0F", "\uD83D\uDE10", "\uD83D\uDE11", "\uD83D\uDE12", "\uD83D\uDE13",
                "\uD83D\uDE14", "\uD83D\uDE15", "\uD83D\uDE16", "\uD83D\uDE17", "\uD83D\uDE18",
                "\uD83D\uDE19", "\uD83D\uDE1A", "\uD83D\uDE1B", "\uD83D\uDE1C", "\uD83D\uDE1D",
                "\uD83D\uDE1E", "\uD83D\uDE1F", "\uD83D\uDE20", "\uD83D\uDE21", "\uD83D\uDE22",
                "\uD83D\uDE23", "\uD83D\uDE24", "\uD83D\uDE25", "\uD83D\uDE26", "\uD83D\uDE27",
                "\uD83D\uDE28", "\uD83D\uDE29", "\uD83D\uDE2A", "\uD83D\uDE2B", "\uD83D\uDE2C",
                "\uD83D\uDE2D", "\uD83D\uDE2E", "\uD83D\uDE2F", "\uD83D\uDE30", "\uD83D\uDE31",
                "\uD83D\uDE32", "\uD83D\uDE33", "\uD83D\uDE34", "\uD83D\uDE35", "\uD83D\uDE36",
                "\uD83D\uDE37", "\uD83D\uDE38", "\uD83D\uDE39", "\uD83D\uDE3A", "\uD83D\uDE3B",
                "\uD83D\uDE3C", "\uD83D\uDE3D", "\uD83D\uDE3E", "\uD83D\uDE3F", "\uD83D\uDE40",
                "\uD83D\uDE41", "\uD83D\uDE42", "\uD83D\uDE43", "\uD83D\uDE44", "\uD83D\uDE45",
                "\uD83D\uDE46", "\uD83D\uDE47", "\uD83D\uDE48", "\uD83D\uDE49", "\uD83D\uDE4A",
                "\uD83D\uDE4B", "\uD83D\uDE4C", "\uD83D\uDE4D", "\uD83D\uDE4E", "\uD83D\uDE4F"
        };

        for (String emoji : emojis) {
            Label emojiLabel = new Label();
            //emojiLabel.setPrefSize(50, 50);
           // emojiLabel.getStyleClass().add("emoji-button");
            emojiLabel.setText(emoji);
            emojiLabel.setStyle("-fx-font-size: 30");
            emojiLabel.setOnMouseClicked(event -> {
                String unicode = emoji;
                System.out.println("Unicode: " + unicode);
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
                    hbox.setPadding(new Insets(5,15,5,15));
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
                //mainVbox.getChildren().add(imageView);
                HBox hbox = new HBox();
                hbox.setAlignment(Pos.BASELINE_RIGHT);
                hbox.getChildren().add(imageView);
                mainVbox.getChildren().add(hbox);
            });
        }
    }
}
