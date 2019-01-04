package my.examples.miniwebserver;

/**
 * 실행방법
 *
 * java my.examples.miniwebserver port번호
 */
public class ChatServerMain {
    public static void main(String[] args) {
        int port = 9999;
        if(args.length > 1){ // 프로그램 아규먼트
            try {
                port = Integer.parseInt(args[0]);
            }catch(NumberFormatException nfe){
                System.out.println("사용법 : java my.examples.miniwebserver port번호");
                System.out.println("port번호는 정수값이어야합니다.");
                return;
            }
        }

        ChatServer chatServer = new ChatServer(port);
        new Thread(chatServer).start();
    }
}
