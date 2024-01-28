package lk.Ijse.ChatApp.Controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Clienthandler {
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String userName;
    private String sender;

    public static ArrayList<Clienthandler> clients = new ArrayList<>();

    public Clienthandler(Socket socket) {

        try {
            this.socket = socket;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            userName = dataInputStream.readUTF();
            clients.add(this);

            listenMessage();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listenMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (socket.isConnected()){
                    try {

                        String message = dataInputStream.readUTF();
                        String[] name = message.split(" : "); // X : hi
                        sender = name[0];

                        broadcastMessage(message);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }

            }
        }).start();
    }

    private void broadcastMessage(String message) {
        for (Clienthandler client: clients) {

            try {
                if(!client.userName.equals(sender)) {

                    client.dataOutputStream.writeUTF(message);
                    client.dataOutputStream.flush();

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
