package my.examples.miniwebserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatHouse {
    private List<ChatUser> lobby;
    private List<ChatRoom> chatRooms;
    private int roomNumber;

    public ChatHouse(){
        lobby = new ArrayList<>();
        chatRooms = new ArrayList<>();
    }

    public synchronized void createRoom(ChatUser chatUser, String title, boolean flag){
        int maxNum = roomNumber++;
        ChatRoom chatRoom = new ChatRoom(chatUser, title, maxNum, flag);
        chatUser.setGrade(0);
        chatUser.setStatus(1);
        chatRooms.add(chatRoom);
        lobby.remove(chatUser);
        chatUser.write("방에 입장하셨습니다.");
    }

    public synchronized ChatRoom getChatRoom(int roomnumber) {
        for(int i=0; i<chatRooms.size(); i++) {
            ChatRoom chatRoom = chatRooms.get(i);
            if(chatRoom.getRoomNumber() == roomnumber) {
                return chatRoom;
            }
        }
        return null;
    }

    public synchronized ChatUser getUser(int roomnumber,String name) {
        ChatRoom chatRoom = getChatRoom(roomnumber);
        List<ChatUser> chatUsers = chatRoom.getChatUsers();
        for(ChatUser user:chatUsers) {
            if(user.getNickname().equals(name)) {
                return user;
            }
        }
        return null;
    }

    public synchronized void setMaster(int roomnumber,String name) {
        ChatUser chatUser = getUser(roomnumber,name);
        chatUser.setGrade(0);
        chatUser.write("방장으로 임명되셨습니다.");
    }

    /**
     * 방장인 경우 닉네임을 받아서 강퇴시킨다.
     * @param Strname
     * @param chatUser
     **/

    public synchronized void getOut(String Strname, ChatUser chatUser){
        if(chatUser.getStatus() == 0) { // 방장이 아닌경우
            chatUser.write("권한이 없습니다.");
        } else { // 방장인 경우
            ChatRoom chatRoom = getChatRoom(chatUser.getRoomNumber());
            List<ChatUser> chatUsers = chatRoom.getChatUsers();
            for (int j = 0; j < chatUsers.size(); j++) {
                ChatUser user = chatUsers.get(j);
                if (user.getNickname().equals(Strname)) {
                    user.write("방장에 의해서 강퇴당하셨습니다.");
                    user.setStatus(0);
                    lobby.add(chatUsers.get(j));
                    chatUsers.remove(j);
                }
            }
        }
    }


    /**
     * 사용자가 처음 접속했을 때 로비에 ChatUser를 추가한다.
     * 원래는.......
     * @param chatUser
     */
    public synchronized void addChatUser(ChatUser chatUser){
        lobby.add(chatUser);
    }

    // exit
    public synchronized void exit(ChatUser chatUser){
        lobby.remove(chatUser);
    }

    public synchronized List<ChatUser> getUsers(ChatUser chatUser) {
        for(ChatRoom cr : chatRooms){
            if(cr.existsUser(chatUser)){
                return cr.getChatUsers();
            }
        }
        return new ArrayList<>();
    }

    public synchronized List<ChatRoom> getChatRooms() {
        return chatRooms;
    }

    public synchronized void joinMessage(int roomNumber,ChatUser chatUser) {
        ChatRoom chatRoom = chatRooms.get(roomNumber);
        chatUser.setStatus(1);
        chatUser.setGrade(1);
        chatRoom.addChatUser(chatUser);
        chatUser.setRoomNumber(roomNumber);
        lobby.remove(chatUser);
        chatUser.write(roomNumber+"번방에 입장하셨습니다.");
        List<ChatUser> chatUsers = chatRoom.getChatUsers();
        for(ChatUser list:chatUsers) {
            if(!list.getNickname().equals(chatUser.getNickname())) {
                list.write(chatUser.getNickname() + "님이 입장하셨습니다.");
            }
        }
    }

    public synchronized void joinRoom(int roomNumber, ChatUser chatUser) {
        ChatRoom chatRoom = chatRooms.get(roomNumber);
        if(chatRoom.isFlag() == true) {
            joinMessage(roomNumber,chatUser);
        } else if(chatRoom.isFlag() == false){
            chatUser.write("비밀방입니다.");
            chatUser.write("입장하실려면 비밀번호를 입력해주세요.");
            while(true) {
                String password = chatUser.read();
                if (chatRoom.getPassword().equals(password)) {
                    joinMessage(roomNumber, chatUser);
                    break;
                } else {
                    chatUser.write("비밀번호가 틀렸습니다.");
                    break;
                }
            }
        }
    }

    public synchronized void outRoom(ChatUser chatUser) { // 방나가기 기능
        ChatRoom chatRoom = getChatRoom(chatUser.getRoomNumber());
        chatUser.setStatus(0);
        chatUser.setGrade(1);
        chatRoom.remove(chatUser);
        List<ChatUser> chatUsers = chatRoom.getChatUsers();
        for(ChatUser member : chatUsers) {
            if(!member.equals(chatUser.getNickname())) {
                member.write(chatUser.getNickname()+"님이 나가셨습니다.");
            }
        }
    }

    public synchronized void secretRoom(ChatUser chatUser, String password) {
        ChatRoom chatRoom = getChatRoom(chatUser.getRoomNumber());
        chatRoom.setPassword(password);
        chatRoom.setFlag(false);
        chatUser.write(password);
        chatUser.write(String.valueOf(chatRoom.isFlag()));
    }

    public synchronized void invite(String inviteName, int roomNumber){
        ChatRoom chatRoom = getChatRoom(roomNumber);
        for(int i=0; i<lobby.size(); i++) {
            ChatUser member = lobby.get(i);
            if(member.getNickname().equals(inviteName)) {
                member.setStatus(1);
                chatRoom.getChatUsers().add(member);
                lobby.remove(i);
            }
        }
    }

    public void printHelp(ChatUser chatUser) {
        if(chatUser.getStatus() == 0) {
            chatUser.write("1. 채팅방 생성 : /create 방제목");
            chatUser.write("2. 목록 확인 : /list");
            chatUser.write("3. 채팅방 참여 : /join 방번호");
        } else {
            chatUser.write("1. 채팅방 인원확인 : /member");
            chatUser.write("2. 채팅방 나가기 : /out");
            chatUser.write("3. 채팅방 비밀번호 지정 : /password");
            chatUser.write("4. 방장 넘기기 : /master 닉네임");
            chatUser.write("5. 강퇴 시키기 : /getout 닉네임");
            chatUser.write("6. 로비 인원초대 : /invite 닉네임");
        }
    }

    public void getMembers(List<ChatUser> chatUsers, ChatUser chatUser) {
        for (int i = 0; i < chatUsers.size(); i++) {
            ChatUser user = chatUsers.get(i);
            if(user.getGrade() == 0) {
                chatUser.write("[방장]"+user.getNickname());
            } else if(user.getGrade() == 1){
                chatUser.write("[일반]"+user.getNickname());
            }
        }
    }
}