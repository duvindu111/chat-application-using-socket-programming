import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ServerController implements Initializable {

    @FXML
    private TextArea mainTxtAreaAdmin;

    @FXML
    private TextField sendTxtAreaAdmin;

    private ServerSocket serverSocket;
    private List<ClientHandler> clients;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        clients = new ArrayList<>();

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(3003);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);
                    System.out.println(clients);
                    clientHandler.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        Platform.runLater(() -> {
            Stage stage = (Stage) mainTxtAreaAdmin.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                event.consume(); // Consume the event to prevent the default close operation

                // Display a confirmation dialog
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Server Connection");
                alert.setHeaderText("Are you sure you want to close the server?");
                alert.setContentText("The connection of the chat application will be lost");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    stage.close(); // Close the window
                }
            });
        });
    }

    @FXML
    void btnSendOnAction(ActionEvent event) {
        String message = sendTxtAreaAdmin.getText();

        if (!message.isEmpty()) {
            broadcastMessagebyAdmin("System", message);
            sendTxtAreaAdmin.clear();
        }
    }

    public void txtFieldServerOnAction(ActionEvent actionEvent) {
        btnSendOnAction(actionEvent);
    }


    private void broadcastMessagebyAdmin(String sender, String message) {
        mainTxtAreaAdmin.appendText(sender + ": " + message + "\n");

        for (ClientHandler client : clients) {
            client.sendMessage(sender, message);
        }
    }

    private void broadcastMessagebyClients(String sender, String message, Socket socket) {
        mainTxtAreaAdmin.appendText(sender + ": " + message + "\n");

        for (ClientHandler client : clients) {
            if (client.clientSocket != socket) {
                client.sendMessage(sender, message);
            }
        }
    }

    private void broadcastImagesbyClients(String username, String path, Socket socket) {
        mainTxtAreaAdmin.appendText(username + " sent an image\n");

        for (ClientHandler client : clients) {
            if (client.clientSocket != socket) {
                client.sendImage(username, path);
            }
        }
    }

    //start of private class to handle each client
    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private DataInputStream din;
        private DataOutputStream dout;

        public ClientHandler(Socket socket) {
            clientSocket = socket;
            try {
                din = new DataInputStream(clientSocket.getInputStream());
                dout = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String name = din.readUTF();
                broadcastMessagebyClients("System", name + " has joined the chat.", clientSocket);

                while (true) {
                    String message = din.readUTF();
                    if (message.equals("pass-qpactk3i5710-xkdwisq@ee358fyndvndla98r478t35-jvvhjfv94r82@")) {
                        break;
                    } else if (message.startsWith("image")) {
                        String username = din.readUTF();
                        String path = din.readUTF();
                        broadcastImagesbyClients(username, path, clientSocket);
                    } else {
                        broadcastMessagebyClients(name, message, clientSocket);
                    }
                }

                clients.remove(this);
                System.out.println(clients);
                clientSocket.close();
                broadcastMessagebyClients("System", name + " has left the chat.", clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String sender, String message) {
            try {
                if (sender.equals("System")) {
                    dout.writeUTF(sender + ": " + message);
                    dout.flush();
                } else {
                    dout.writeUTF(sender + ":\n" + message);
                    dout.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendImage(String username, String path) {
            try {
                dout.writeUTF("image");
                dout.writeUTF(username);
                dout.writeUTF(path);
                dout.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
    //end of the ClientHandler class
}
