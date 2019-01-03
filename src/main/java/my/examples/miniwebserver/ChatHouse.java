package my.examples.miniwebserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatHouse {
    List<ChatUser> lobby;
    List<ChatRoom> chatRooms;
    private int roomNumber;

    public ChatHouse(){
        lobby = Collections.synchronizedList(new ArrayList<>());
        chatRooms = Collections.synchronizedList(new ArrayList<>());
    }

    public void createRoom(ChatUser chatUser, String title, boolean flag){
        int maxNum = roomNumber++;
        ChatRoom chatRoom = new ChatRoom(chatUser, title, maxNum, flag);
        chatUser.setGrade(1);
        chatRooms.add(chatRoom);
    }
    public List<ChatRoom> getChatRoom(int roomnumber) {
        List<ChatRoom> list = new ArrayList<ChatRoom>();
        for(int i=0; i<chatRooms.size(); i++) {
            ChatRoom chatRoom = chatRooms.get(i);
            if(chatRoom.getRoomNumber() == roomnumber) {
                list.add(chatRoom);
            }
        }
        return list;
    }

    public void setMaster(int roomnumber,String name, int grade) {
        List<ChatRoom> list = getChatRoom(roomnumber);
        List<ChatUser> chatUsers = list.get(0).getChatUsers();
        for(ChatUser user:chatUsers) {
            if(user.getNickname().equals(name)) {
                user.setGrade(grade);
                user.write("방장으로 임명되셨습니다.");
            }
        }
    }

    public void getOut(String nickname, ChatUser chatUser){
        List<ChatRoom> list = getChatRoom(chatUser.getRoomNumber());
        List<ChatUser> chatUsers = list.get(0).getChatUsers();
        for(int j=0; j<chatUsers.size(); j++) {
            ChatUser user = chatUsers.get(j);
            if (user.getNickname().equals(nickname)) {
                user.write("방장에 의해서 강퇴당하셨습니다.");
                user.setStatus(0);
                lobby.add(chatUsers.get(j));
                chatUsers.remove(j);
            }
        }
    }



    // ChatUser를 추가
    public void addChatUser(ChatUser chatUser){
        lobby.add(chatUser);
    }

    // exit
    public void exit(ChatUser chatUser){
        lobby.remove(chatUser);
    }

    public void printLobby(){
        for(ChatUser chatUser : lobby){
            System.out.println(chatUser.getNickname());
        }
    }

    public List<ChatUser> getUser(ChatUser chatUser) {
        for(ChatRoom cr : chatRooms){
            if(cr.existsUser(chatUser)){
                return cr.getChatUsers();
            }
        }
        return new ArrayList<ChatUser>();
    }

    public List<ChatRoom> getChatRooms() {
        return chatRooms;
    }

    public boolean joinRoom(int roomNum, ChatUser chatUser) {
        ChatRoom chatRoom = chatRooms.get(roomNum);
        if(chatRoom.isFlag() == true) {
            chatRoom.addChatUser(chatUser);
            List<ChatUser> chatUsers = chatRoom.getChatUsers();
            for(ChatUser list:chatUsers) {
                if(!list.getNickname().equals(chatUser.getNickname())) {
                    list.write(chatUser.getNickname() + "님이 입장하셨습니다.");
                }
            }
        }
        return chatRoom.isFlag();
    }

    public void outRoom(ChatUser chatUser) {
        List<ChatRoom> list = getChatRoom(chatUser.getRoomNumber());
        list.get(0).remove(chatUser);
        List<ChatUser> chatUsers = list.get(0).getChatUsers();
        for(ChatUser member : chatUsers) {
            if(!member.equals(chatUser.getNickname())) {
                member.write(chatUser.getNickname()+"님이 나가셨습니다.");
            }
        }
    }

    public void secretRoom(ChatUser chatUser, String password) {
        List<ChatRoom> list = getChatRoom(chatUser.getRoomNumber());
        ChatRoom chatRoom = list.get(0);
        chatRoom.setPassword(password);
        chatRoom.setFlag(false);
    }

    public void invite(String inviteName, int roomNumber){
        List<ChatRoom> list = getChatRoom(roomNumber);
        ChatRoom chatRoom = list.get(0);
        for(int i=0; i<lobby.size(); i++) {
            ChatUser member = lobby.get(i);
            if(member.getNickname().equals(inviteName)) {
                member.setStatus(1);
                chatRoom.getChatUsers().add(member);
                lobby.remove(i);
            }
        }
    }
}