package my.examples.miniwebserver;

import java.net.Socket;
import java.util.List;

public class ChatServerHandler extends Thread{
    private Socket socket;
    private ChatHouse chatHouse;
    private boolean inRoom;

    public ChatServerHandler(Socket socket, ChatHouse chatHouse) {
        this.socket = socket;
        this.chatHouse = chatHouse;
        inRoom = false;
    }

    @Override
    public void run() {
        ChatUser chatUser = new ChatUser(socket);
        String nickname = chatUser.read();
        chatUser.setNickname(nickname);
        System.out.println("message : " + nickname);

        chatHouse.addChatUser(chatUser);

        try {
            while (true) {
                String message = chatUser.read();
                System.out.println("message : " + message);

                if(chatUser.getStatus() == 0){ // 로비에 있을 경우
                    List<ChatRoom> chatRooms = chatHouse.getChatRooms();

                    if(message.indexOf("/create") == 0) {
                        String title = message.substring(message.indexOf(" ") + 1);
                        boolean flag = true;
                        chatHouse.createRoom(chatUser, title, flag);

                    } else if(message.indexOf("/list") == 0) {
                        for(ChatRoom cr : chatRooms){
                            chatUser.write(cr.getRoomNumber()+ " : " + cr.getTitle());
                        }

                    } else if(message.indexOf("/join") == 0){
                        String strRoomNum = message.substring(message.indexOf(" ") +1);
                        int roomNum = Integer.parseInt(strRoomNum);
                        chatHouse.joinRoom(roomNum, chatUser);

                    } else if(message.indexOf("/help") == 0) {
                        chatHouse.printHelp(chatUser);
                    }

                } else if(chatUser.getStatus() == 1) { // 방안에 있을 경우
                    List<ChatUser> chatUsers = chatHouse.getUsers(chatUser);
                    if (message.indexOf("/member") == 0) {
                        chatHouse.getMembers(chatUsers, chatUser);

                    } else if(message.indexOf("/help") == 0) {
                        chatHouse.printHelp(chatUser);

                    } else if (message.indexOf("/out") == 0) {
                        chatUser.write("방에서 나가셨습니다.");
                        chatHouse.outRoom(chatUser);
                        chatHouse.addChatUser(chatUser);

                    } else if (message.indexOf("/secret") == 0) {
                        String password = message.substring(message.indexOf(" ") + 1);
                        chatHouse.secretRoom(chatUser, password);
                        chatUser.write("비밀방이 설정되셨습니다.");

                    } else if (message.indexOf("/master") == 0) {
                        String name = message.substring(message.indexOf(" ") + 1);
                        chatHouse.setMaster(chatUser.getRoomNumber(), name);
                        chatUser.setGrade(1);
                    } else if(message.indexOf("/getout") == 0) {
                        String strName = message.substring(message.indexOf(" ") + 1);
                        chatHouse.getOut(strName, chatUser);

                    } else if (message.indexOf("/invite")==0){
                        String inviteName = message.substring(message.indexOf(" ")+1);
                        chatHouse.invite(inviteName, chatUser.getRoomNumber());
                        chatUser.write(inviteName+"님을 초대하셨습니다.");

                    } else {
                        for(ChatUser cu : chatUsers){
                          cu.write(chatUser.getNickname() + " : " + message);
                        }
                    }
                }
            }
        } catch(Exception ex) {
            chatHouse.exit(chatUser);
        }
    }
}