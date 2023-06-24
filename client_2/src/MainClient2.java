import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainClient2 extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("client2_form.fxml"));
        primaryStage.setTitle("Client");
        primaryStage.setScene(new Scene(root, 608, 497));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
