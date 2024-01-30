package lk.Ijse.ChatApp.Controller;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class ChatRoomFormController {
    public TextField txtMessage;
    public VBox vBox;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String name;

    private final String[] emojis = {
            "\uD83D\uDE00", // 😀
            "\uD83D\uDE01", // 😁
            "\uD83D\uDE02", // 😂
            "\uD83D\uDE03", // 🤣
            "\uD83D\uDE04", // 😄
            "\uD83D\uDE05", // 😅
            "\uD83D\uDE06", // 😆
            "\uD83D\uDE07", // 😇
            "\uD83D\uDE08", // 😈
            "\uD83D\uDE09", // 😉
            "\uD83D\uDE0A", // 😊
            "\uD83D\uDE0B", // 😋
            "\uD83D\uDE0C", // 😌
            "\uD83D\uDE0D", // 😍
            "\uD83D\uDE0E", // 😎
            "\uD83D\uDE0F", // 😏
            "\uD83D\uDE10", // 😐
            "\uD83D\uDE11", // 😑
            "\uD83D\uDE12", // 😒
            "\uD83D\uDE13"  // 😓
    };

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
                                label.setStyle("-fx-background-color:#4CAF50;-fx-font-size:18;-fx-text-fill:black");
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
            label.setStyle("-fx-background-color:rgb(37, 150, 190);-fx-font-size:18;-fx-text-fill:black");
            HBox hBox = new HBox(label);
            hBox.setStyle("-fx-padding:20;");
            hBox.setAlignment(Pos.CENTER_RIGHT);  // Align to the right for the user's messages
            vBox.getChildren().add(hBox);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void ImagSendOnAction(ActionEvent actionEvent) {

    }

    public void EmojiSendOnAction(ActionEvent actionEvent) {
        ChoiceDialog<String> emojiDialog = new ChoiceDialog<>(null, emojis);
        emojiDialog.setTitle("Choose Emoji");
        emojiDialog.setHeaderText(null);
        emojiDialog.setContentText("Select an Emoji:");

        Optional<String> result = emojiDialog.showAndWait();
        result.ifPresent(emoji -> {
            String currentMessage = txtMessage.getText();
            txtMessage.setText(currentMessage + " " + emoji);
        });
    }
}