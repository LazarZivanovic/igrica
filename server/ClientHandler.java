package server;

import shared.Message;
import shared.NetworkSerializer;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private final Socket socket;
    private NetworkSerializer serializer;
    private String playerName;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try{
            serializer = new NetworkSerializer(socket);
            while (true){
                Message msg = serializer.receive();
                if (msg.getType()==Message.Type.JOIN){
                    playerName = msg.getName();
                    System.out.println("[ SERVER ] "+ playerName +" is connected!");
                }else if (msg.getType() == Message.Type.CLICK){
                    //broadcast to everyone
                }

            }
        }catch (Exception e){
            System.out.println("[ SERVER ] Player "+ playerName + " is disconnected!");
        }finally {
            try{
                if (socket!=null)socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
