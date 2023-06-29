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
            din = new DataInputStream(clientSocket.getInputStream());
            dout = new DataOutputStream(clientSocket.getOutputStream());

            mainVbox.setPadding(new Insets(20));
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
                            Label label = new Label(message);
                            Platform.runLater(() -> {
                                mainVbox.getChildren().add(label);
                            });
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
                "\uD83D\uDE00", // Grinning Face
                "\uD83D\uDE0D", // Smiling Face with Heart-Eyes
                "\uD83D\uDE01", // Grinning Face with Smiling Eyes
                "\uD83D\uDE2D", // Loudly Crying Face
                "\uD83D\uDE10" // Neutral Face
        };

        for (String emoji : emojis) {
            Label emojiLabel = new Label();
            //emojiLabel.setPrefSize(50, 50);
            emojiLabel.getStyleClass().add("emoji-button");
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
                    hbox.setAlignment(Pos.BASELINE_RIGHT);
                    Label label = new Label(message + "\n");
                    hbox.getChildren().add(label);
                    mainVbox.getChildren().add(hbox);
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
