import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnSendAdmin;

    @FXML
    private TextArea mainTxtAreaAdmin;

    @FXML
    private TextField sendTxtAreaAdmin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void btnSendOnAction(ActionEvent event) {

    }

    @FXML
    void txtFieldServerOnAction(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert btnSendAdmin != null : "fx:id=\"btnSendAdmin\" was not injected: check your FXML file 'server_form.fxml'.";
        assert mainTxtAreaAdmin != null : "fx:id=\"mainTxtAreaAdmin\" was not injected: check your FXML file 'server_form.fxml'.";
        assert sendTxtAreaAdmin != null : "fx:id=\"sendTxtAreaAdmin\" was not injected: check your FXML file 'server_form.fxml'.";

    }
}
