package my.examples.miniwebserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatRoom {
    private String title;
    private List<ChatUser> chatUsers;
    private int roomNumber;
    private String password;
    private boolean flag;

    public ChatRoom(ChatUser chatUser, String title, int roomNumber, boolean flag){
        this.title = title;
        this.roomNumber = roomNumber;
        this.flag = flag;
        chatUsers = Collections.synchronizedList(new ArrayList<>());
        chatUsers.add(chatUser);
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public boolean existsUser(ChatUser chatUser){
        return chatUsers.contains(chatUser);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ChatUser> getChatUsers() {
        return chatUsers;
    }

    public void setChatUsers(List<ChatUser> chatUsers) {
        this.chatUsers = chatUsers;
    }

    public void addChatUser(ChatUser chatUser) {
        chatUsers.add(chatUser);
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void remove(ChatUser chatUser) {
        chatUsers.remove(chatUser);
    }
}