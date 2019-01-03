package my.examples.miniwebserver;

import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private int port;
    private ChatHouse chatHouse;

    public ChatServer(int port){
        this.port = port;
        chatHouse = new ChatHouse();
    }

    public void run(){
        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(port);
            while(true) {
                Socket socket = serverSocket.accept();
                ChatServerHandler chatServerHandler
                        = new ChatServerHandler(socket, chatHouse);
                chatServerHandler.start();
            }
        }catch (Exception ex){
            System.out.println("오류 발생.");
        }finally {
            try{ serverSocket.close(); }catch(Exception ignore){}
        }
    }
}