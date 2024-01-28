package lk.Ijse.ChatApp.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatRoomFormController {
    public TextField txtMessage;
    public VBox vBox;

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String name;

    public void initialize(){

        name = LogInFormController.name;

        try {

            socket = new Socket("localhost",5000);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream.writeUTF(name);
            dataOutputStream.flush();

            listenMessage();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void listenMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (socket.isConnected()){

                    try {
                        String message = dataInputStream.readUTF();
                        System.out.println(message);

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Label label = new Label(message);
                                label.setStyle("-fx-background-color:blue;-fx-font-size:18;-fx-text-fill:white");
                                HBox hBox = new HBox(label);
                                hBox.setStyle("-fx-padding:20");
                                vBox.getChildren().add(hBox);
                            }
                        });



                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }

            }
        }).start();
    }


    @FXML
   public void sendOnAction(ActionEvent actionEvent) {
    String message = txtMessage.getText();
    try {
        dataOutputStream.writeUTF(name+" : "+message); // x:hi
        dataOutputStream.flush();

        Label label = new Label(message);
        label.setStyle("-fx-background-color:Red;-fx-font-size:18;-fx-text-fill:white");
        HBox hBox = new HBox(label);
        hBox.setStyle("-fx-padding:20;");
        vBox.getChildren().add(hBox);

    } catch (IOException e) {
        throw new RuntimeException(e);
    }


}
    }

