import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import com.vdurmont.emoji.Emoji;
import org.json.JSONArray;


public class Client1Controller implements Initializable {

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
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            });
            emojiContainer.getChildren().add(emojiLabel);
        }
    }

    @FXML
    void btnSendOnAction(ActionEvent event) {
        String message = sendTxtAreaClient.getText();

        if (!message.isEmpty()) {
            try {
                Label label = new Label("You: " + message + "\n");
                Platform.runLater(() -> {
                    mainVbox.getChildren().add(label);
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
//            imageView.setFitWidth(256);
//            imageView.setFitHeight(256);
            Label label = new Label("you: ");
            Platform.runLater(() -> {
                mainVbox.getChildren().add(label);
            });
            Platform.runLater(() -> {
                mainVbox.getChildren().add(imageView);
            });
        }
    }


}
