import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
                serverSocket = new ServerSocket(3001);

                while (true) {
                    System.out.println("before accept");
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("after accept");
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);
                    clientHandler.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    void btnSendOnAction(ActionEvent event) {
        String message = sendTxtAreaAdmin.getText();

        if (!message.isEmpty()) {
            broadcastMessagebyAdmin("Admin", message);
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
            if(client.clientSocket!=socket) {
                client.sendMessage(sender, message);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////
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
                broadcastMessagebyClients("System", name + " has joined the chat.",clientSocket);

                while (true) {
                    String message = din.readUTF();
                    System.out.println("message by: "+ clientSocket);
                    if (message.equals("finish")) {
                        break;
                    }
                    broadcastMessagebyClients(name, message,clientSocket);
                }

                clients.remove(this);
                clientSocket.close();
                broadcastMessagebyClients("System", name + " has left the chat.",clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String sender, String message) {
            try {
                dout.writeUTF(sender + ": " + message);
                dout.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////
}
