package my.examples.miniwebserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ChatUser {
    private String nickname;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private int roomNumber;
    private int grade;
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public ChatUser(Socket socket){
        this.socket = socket;
        try{
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        }catch(Exception ex){
            throw new RuntimeException("ChatUser생성시 오류");
        }
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void close(){
        try{ in.close();} catch (Exception ignore){}
        try{ out.close();} catch (Exception ignore){}
        try{ socket.close();} catch (Exception ignore){}
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Socket getSocket() {
        return socket;
    }

    public DataInputStream getIn() {
        return in;
    }

    public void setIn(DataInputStream in) {
        this.in = in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void setOut(DataOutputStream out) {
        this.out = out;
    }

    public void write(String msg){
        try {
            out.writeUTF(msg);
            out.flush();
        }catch(Exception ex){
            throw new RuntimeException("메시지 전송시 오류");
        }
    }

    public String read(){
        try{
            return in.readUTF();
        }catch(Exception ex){
            throw new RuntimeException("메시지 읽을 때 오류");
        }
    }
}