import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

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

    private Socket clientSocket;
    private DataInputStream din;
    private DataOutputStream dout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            clientSocket = new Socket("localhost", 3001);
            din = new DataInputStream(clientSocket.getInputStream());
            dout = new DataOutputStream(clientSocket.getOutputStream());

            new Thread(() -> {
                try {
                    while (true) {
                        String message = din.readUTF();
                        mainTxtAreaClient.appendText(message + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnSendOnAction(ActionEvent event) {
        String message = sendTxtAreaClient.getText();

        if (!message.isEmpty()) {
            try {
                mainTxtAreaClient.appendText("You: "+ message + "\n");
                dout.writeUTF(message);
                dout.flush();
                sendTxtAreaClient.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            dout.writeUTF(username);
            dout.flush();
        }
    }
}
