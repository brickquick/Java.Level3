package lesson2.homework2.Server;

import java.sql.SQLException;

public interface AuthService {
    void start() throws ClassNotFoundException, SQLException;
    void changeNick(String oldNick, String newNick);
    public void showTable();
    String getNickByLoginPass(String login, String pass);
    void stop();
}
