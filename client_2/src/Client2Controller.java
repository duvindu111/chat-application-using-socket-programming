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
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnSendClient;

    @FXML
    private TextArea mainTxtAreaClient;

    @FXML
    private TextField sendTxtAreaClient;

    String message = "";
    DataInputStream din;
    DataOutputStream dout;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            try {
                Socket s = new Socket("localhost", 3001);
                din = new DataInputStream(s.getInputStream());
                dout = new DataOutputStream(s.getOutputStream());


                while (!message.equals("finish")) {
                    message = din.readUTF();
                    mainTxtAreaClient.appendText("\nServer: " + message);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    void btnSendOnAction(ActionEvent event) throws IOException {
        String typedText = sendTxtAreaClient.getText();
        dout.writeUTF(typedText);
        dout.flush();
        sendTxtAreaClient.setText("");
        mainTxtAreaClient.appendText("\nClient 1: " + typedText);
    }

    @FXML
    void txtFieldClientOnAction(ActionEvent event) throws IOException {
        btnSendOnAction(event);
    }

    @FXML
    void initialize() {
        assert btnSendClient != null : "fx:id=\"btnSendClient\" was not injected: check your FXML file 'client2_form.fxml'.";
        assert mainTxtAreaClient != null : "fx:id=\"mainTxtAreaClient\" was not injected: check your FXML file 'client2_form.fxml'.";
        assert sendTxtAreaClient != null : "fx:id=\"sendTxtAreaClient\" was not injected: check your FXML file 'client2_form.fxml'.";

    }
}

