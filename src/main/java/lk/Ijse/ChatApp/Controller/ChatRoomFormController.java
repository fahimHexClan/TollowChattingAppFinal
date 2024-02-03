package lk.Ijse.ChatApp.Controller;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Optional;

public class ChatRoomFormController {
    @FXML
    private AnchorPane MainAnchorpane;
    public TextField txtMessage;
    public VBox vBox;
    public Label LabelTxt;
    public ScrollPane MessageScrollPane;
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
            LabelTxt.setText(name);

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

                        String type = dataInputStream.readUTF();
                        if (type.equals("Message")) {
                            System.out.println("sms");
                            String sms = dataInputStream.readUTF();
                            setTxt(sms);
                        }
                        if (type.equals("image")) {
                            //imge ek ganna

                            String size = dataInputStream.readUTF();
                            System.out.println(size+"ssssss");
                            byte[] blob = new byte[Integer.parseInt(size)];
                            dataInputStream.readFully(blob);
                            setImg(blob);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }

            }
        }).start();
    }

    private void setImg(byte[] blob) {
        Platform.runLater(new Runnable() {
            public void run() {
                Image image = new Image(new ByteArrayInputStream(blob));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                // Create a new HBox to hold the image
                HBox hBox = new HBox(imageView);
                hBox.setStyle("-fx-padding:20;");

                vBox.getChildren().add(hBox);  // Assuming vBox is your target VBox
                MessageScrollPane.setVvalue(1.0);
            }
        });

    }

    public void setTxt(String message){
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
    }
    @FXML
    public void sendOnAction(ActionEvent actionEvent) {
        String message = txtMessage.getText();
        try {
            dataOutputStream.writeUTF("Message");
            dataOutputStream.writeUTF(name+" : "+message); // x:hi
            dataOutputStream.flush();

            Label label = new Label(message);
            label.setStyle("-fx-background-color:rgb(37, 150, 190);-fx-font-size:18;-fx-text-fill:black");
            HBox hBox = new HBox(label);
            hBox.setStyle("-fx-padding:20;");
            hBox.setAlignment(Pos.CENTER_RIGHT);  // Align to the right for the user's messages
            vBox.getChildren().add(hBox);
            txtMessage.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void ImagSendOnAction(ActionEvent actionEvent) {


        FileChooser chooser = new FileChooser();
        File file =chooser.showOpenDialog(MainAnchorpane.getScene().getWindow());
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            if (fileInputStream!=null) {
                Image image = new Image(fileInputStream);

                byte[] blob = imagenToByte(image);
                String path = file.getPath();
                sendImg(blob);
                System.out.println(path);
                setMyImg(image);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setMyImg(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
            // Create a new HBox to hold the image
        HBox hBox = new HBox(imageView);
        hBox.setStyle("-fx-padding:20;");
        hBox.setAlignment(Pos.CENTER_RIGHT);


        vBox.getChildren().add(hBox);  // Assuming vBox is your target VBox
        MessageScrollPane.setVvalue(1.0);
    }




    private void sendImg(byte[] blob) {
        try {
            dataOutputStream.writeUTF("image");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(blob.length+"");
            dataOutputStream.flush();
            dataOutputStream.write(blob);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private static byte[] imagenToByte(Image image) {
        BufferedImage bufferimage = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferimage, "jpg", output );
            ImageIO.write(bufferimage, "png", output );
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte [] data = output.toByteArray();
        return data;
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