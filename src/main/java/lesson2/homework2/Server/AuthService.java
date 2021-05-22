package lesson2.homework2.Server;

import java.sql.SQLException;

public interface AuthService {
    void showTable();
    void changeNick(String oldNick, String newNick);
    boolean isNickBusy(String newNick);
    String getNickByLoginPass(String login, String pass);
    void start();
    void stop();
}
