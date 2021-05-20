package lesson2.homework2.Server;

public interface AuthService {
    void start();
    String getNickByLoginPass(String login, String pass);
    void stop();
}
