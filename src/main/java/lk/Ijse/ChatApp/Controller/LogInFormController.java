package lk.Ijse.ChatApp.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lk.Ijse.ChatApp.Client.Clienthandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LogInFormController {

    public TextField txtName;
    private ServerSocket serverSocket;

    static String name;

    public void initialize(){

        try {
            serverSocket = new ServerSocket(5000);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    while (!serverSocket.isClosed()){

                        Socket socket = serverSocket.accept();
                        System.out.println("new user connected");
                        Clienthandler clienthandler = new Clienthandler(socket);
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();

    }

    public void ButtonLogInOnAction(ActionEvent actionEvent) throws IOException {
        name = txtName.getText();
        txtName.clear();

        Parent rootNode = FXMLLoader.load(getClass().getResource("/view/ChatRoomForm.fxml"));
        Scene scene = new Scene(rootNode);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
