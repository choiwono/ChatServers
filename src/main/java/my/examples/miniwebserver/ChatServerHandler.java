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
                        chatUser.write("방에 입장하셨습니다.");
                        chatUser.setStatus(1);
                        //inRoom = true;
                    } else if(message.indexOf("/list") == 0) {
                        for(ChatRoom cr : chatRooms){
                            chatUser.write(cr.getRoomNumber()+ " : " + cr.getTitle());
                        }
                    } else if(message.indexOf("/quit") == 0){
                        inRoom = false;
                    } else if(message.indexOf("/join") == 0){
                        String strRoomNum = message.substring(message.indexOf(" ") +1);

                        int roomNum = Integer.parseInt(strRoomNum);
                        boolean result = chatHouse.joinRoom(roomNum, chatUser);

                        //String ms = String.valueOf(result);
                        //chatUser.write(ms);
                        if(result == true) {
                            chatUser.setRoomNumber(roomNum);
                            chatUser.write(roomNum+"번방에 입장하셨습니다.");
                            chatHouse.exit(chatUser);
                            chatUser.setStatus(1);
                        } else {
                            chatUser.write("비밀방입니다.");
                            chatUser.setStatus(0);
                        }

                    } else if(message.indexOf("/help") == 0) {
                        chatUser.write("1. 채팅방 생성 : /create 방제목");
                        chatUser.write("2. 목록 확인 : /list");
                        chatUser.write("3. 채팅방 참여 : /join 방번호");
                        chatUser.setStatus(0);
                    }
                } else if(chatUser.getStatus() == 1) { // 방안에 있을 경우
                    List<ChatUser> chatUsers = chatHouse.getUser(chatUser);
                    List<ChatRoom> chatRooms = chatHouse.getChatRooms();
                    if (message.indexOf("/member") == 0) {
                        for (int i = 0; i < chatUsers.size(); i++) {
                            String grade;
                            ChatUser user = chatUsers.get(i);
                            if(user.getGrade() == 1){
                                grade = "방장";
                            }else{
                                grade = "일반";
                            }
                            chatUser.write("["+grade+"]"+chatUsers.get(i).getNickname());
                        }
                    } else if (message.indexOf("/out") == 0) {
                        chatUser.write("방에서 나가셨습니다.");
                        chatHouse.addChatUser(chatUser);
                        chatHouse.outRoom(chatUser);
                        chatUser.setStatus(0);

                    } else if (message.indexOf("/password") == 0) {
                        String password = message.substring(message.indexOf(" ") + 1);
                        chatHouse.secretRoom(chatUser, password);
                        chatUser.write("비밀방이 설정되셨습니다.");

                    } else if (message.indexOf("/master") == 0) {
                        String name = message.substring(message.indexOf(" ") + 1);
                        int roomNum = chatUser.getRoomNumber();
                        int grade = 1;
                        chatHouse.setMaster(roomNum, name, grade);
                        chatUser.setGrade(0);

                    } else if(message.indexOf("/getout") == 0) {
                        if (chatUser.getGrade() == 1) {
                            String memName = message.substring(message.indexOf(" ") + 1);
                            chatHouse.getOut(memName, chatUser);
                            chatUser.write("["+memName+"]"+"님을 강퇴 하셨습니다.");
                        } else {
                            chatUser.write("권한이 없습니다.");
                        }
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