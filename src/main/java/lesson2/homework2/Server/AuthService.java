package lesson2.homework2.Server;

import java.sql.SQLException;

public interface AuthService {
    void start() throws ClassNotFoundException, SQLException;
    String getNickByLoginPass(String login, String pass);
    void stop();
}
