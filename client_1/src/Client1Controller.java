import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class Client1Controller implements Initializable {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void btnSendOnAction(ActionEvent event) {

    }

    @FXML
    void txtFieldClientOnAction(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert btnSendClient != null : "fx:id=\"btnSendClient\" was not injected: check your FXML file 'client1_form.fxml'.";
        assert mainTxtAreaClient != null : "fx:id=\"mainTxtAreaClient\" was not injected: check your FXML file 'client1_form.fxml'.";
        assert sendTxtAreaClient != null : "fx:id=\"sendTxtAreaClient\" was not injected: check your FXML file 'client1_form.fxml'.";

    }
}
