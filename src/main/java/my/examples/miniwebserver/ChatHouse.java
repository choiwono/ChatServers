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
    /**
     * 불필요한 리스트사용이 있어 리턴타입과 변수사용을 줄였습니다.
     * List<ChatRoom> list = new getChatRoom(roomnumber);
     * ChatRoom chatRoom = list.get(0);
     * 이부분을
     * ChatRoom chatRoom = getChatRoom(roomnumber)
     * 으로 바꿧습니다
     * @param roomnumber
     * @return
     */
    public ChatRoom getChatRoom(int roomnumber) {
        for(int i=0; i<chatRooms.size(); i++) {
            ChatRoom chatRoom = chatRooms.get(i);
            if(chatRoom.getRoomNumber() == roomnumber) {
                return chatRoom;
            }
        }
        return null;
    }
    /**
     * 위와 동일
     * @param roomnumber
     * @param name
     * @param grade
     */
    public void setMaster(int roomnumber,String name, int grade) {
        ChatRoom chatRoom = getChatRoom(roomnumber);
        List<ChatUser> chatUsers = chatRoom.getChatUsers();
        for(ChatUser user:chatUsers) {
            if(user.getNickname().equals(name)) {
                user.setGrade(grade);
                user.write("방장으로 임명되셨습니다.");
            }
        }
    }
    /**
     * 위와 동일
     * @param nickname
     * @param chatUser
     */
    public void getOut(String nickname, ChatUser chatUser){
        ChatRoom chatRoom = getChatRoom(chatUser.getRoomNumber());
        List<ChatUser> chatUsers = chatRoom.getChatUsers();
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
    public boolean joinPasswordRoom(int roomNum,ChatUser chatUser,String password){
        ChatRoom chatRoom = chatRooms.get(roomNum);
        if(chatRoom.getPassword() == null){
            return joinRoom(roomNum,chatUser);
        }else if(chatRoom.getPassword() == password){
            return joinRoom(roomNum,chatUser);
        }else{
            return false;
        }
    }
    /**
     * 위와 동일
     * @param chatUser
     */
    public void outRoom(ChatUser chatUser) {
        ChatRoom chatRoom = getChatRoom(chatUser.getRoomNumber());
        chatRoom.remove(chatUser);
        List<ChatUser> chatUsers = chatRoom.getChatUsers();
        for(ChatUser member : chatUsers) {
            if(!member.equals(chatUser.getNickname())) {
                member.write(chatUser.getNickname()+"님이 나가셨습니다.");
            }
        }
    }
    public void secretRoom(ChatUser chatUser, String password) {
        ChatRoom chatRoom = getChatRoom(chatUser.getRoomNumber());
        chatRoom.setPassword(password);
    }
    /**
     * 위와 동일
     * @param inviteName
     * @param roomNumber
     */
    public void invite(String inviteName, int roomNumber){
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
}