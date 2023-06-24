import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Client2Controller implements Initializable {

    @FXML
    private TextArea mainTxtAreaClient;

    @FXML
    private TextField sendTxtAreaClient;

    private Socket clientSocket;
    private DataInputStream din;
    private DataOutputStream dout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            clientSocket = new Socket("localhost", 3001);
            din = new DataInputStream(clientSocket.getInputStream());
            dout = new DataOutputStream(clientSocket.getOutputStream());

            String name = "Client 2";
            dout.writeUTF(name);
            dout.flush();

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
}
