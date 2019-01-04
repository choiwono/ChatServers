package my.examples.miniwebserver;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * 채팅 서버
 */
public class ChatServer implements Runnable{
    private int port;
    private ChatHouse chatHouse;

    /**
     * 채팅서버 생성자
     *
     * 내부적으로 채팅과 관련된 자료구조 객체인 ChatHouse를 초기화한다.
     * @param port 포트
     */
    public ChatServer(int port){
        this.port = port;
        chatHouse = new ChatHouse();
    }

    /**
     * 클라이언트를 기다린다.
     * 클라이언트가 접속하면 ChatServerHandler에게 통신을 맡긴다.
     */
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