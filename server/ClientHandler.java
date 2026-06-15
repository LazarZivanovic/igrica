package server;

import shared.Message;
import shared.NetworkSerializer;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private final Socket socket;
    private final GameState gameState;
    private final Broadcaster broadcaster;
    private NetworkSerializer serializer;
    private  String playerName;

    public ClientHandler(Socket socket, GameState gameState, Broadcaster broadcaster){
        this.socket = socket;
        this.gameState = gameState;
        this.broadcaster = broadcaster;
    }

    @Override
    public void run(){
        try{
            serializer = new NetworkSerializer(socket);
            while (true){
                Message msg = serializer.receive();
                if (msg.getType()==Message.Type.JOIN){
                    playerName = msg.getName();
                    gameState.addPlayer(playerName);
                    broadcaster.addClient(this);
                    System.out.println("[ SERVER ] "+ playerName +" is connected!");
                }else if (msg.getType() == Message.Type.CLICK){
                    gameState.paintPixel(msg.getRow(), msg.getCol(), playerName);
                    broadcaster.broadcast(new Message(
                            Message.Type.CLICK,
                            gameState.getBoardCopy(),
                            gameState.getScores()
                    ));
                }

            }
        }catch (Exception e){
            System.out.println("[ SERVER ] Player "+ playerName + " is disconnected!");
        }finally {
            try{
                broadcaster.removeClient(this);
                if (socket!=null)socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public void sendMessage(Message msg) {
        try {
            serializer.send(msg);
        } catch (IOException e) {
            System.out.println("[ SERVER ] Greška pri slanju: " + e.getMessage());
        }
    }
}
