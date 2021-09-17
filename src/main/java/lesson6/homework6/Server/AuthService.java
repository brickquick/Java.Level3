package lesson6.homework6.Server;

public interface AuthService {
    void showTable();
    void changeNick(String oldNick, String newNick);
    boolean isNickBusy(String newNick);
    String getNickByLoginPass(String login, String pass);
    void start();
    void stop();
}
