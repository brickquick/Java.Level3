package lesson2.homework2.Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaseAuthService implements AuthService {
    private class Entry {
        private String login;
        private String pass;
        private String nick;
        private int id;

        public Entry(String login, String pass, String nick) {
            this.id = id;
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }

    private static Connection c;
    private static Statement stmt;

    private static final String CON_STR = "jdbc:sqlite:lol.db";
    private static BaseAuthService instance = null;

    private List<Entry> entries;

//    public static synchronized BaseAuthService getInstance() throws SQLException {
//        if (instance == null)
//            instance = new BaseAuthService();
//        return instance;
//    }

    public BaseAuthService() throws SQLException{

        entries = new ArrayList<>();

        start();
        try {
            createTable();
        } catch (SQLException e) {
            System.out.println("Таблица уже существует");
        }
        addEntry();
        entries = getAllEntries();
        System.out.println(entries.size());
        showTable();

//        entries.add(new Entry("login1", "pass1", "nick1"));
//        entries.add(new Entry("login2", "pass2", "nick2"));
//        entries.add(new Entry("login3", "pass3", "nick3"));
    }

    @Override
    public void changeNick(String oldNick, String newNick) {
        try {
            c.setAutoCommit(false);
            System.out.println("Opened database adasd123123123444");
            stmt = c.createStatement();
            String sql = "UPDATE entries set nick = '" + newNick + "' where nick = '" + oldNick + "' ;";
            stmt.executeUpdate(sql);
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showTable() {
        try {
            c.setAutoCommit(false);
            System.out.println("Opened database successfully123");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM entries;" );

            while ( rs.next() ) {
                int id = rs.getInt("id");
                String  login = rs.getString("login");
                String pass  = rs.getString("pass");
                String  nick = rs.getString("nick");

                System.out.println( "id = " + id );
                System.out.println( "login = " + login );
                System.out.println( "pass = " + pass );
                System.out.println( "nick = " + nick );
                System.out.println();
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Operation done successfully");
    }

    private static void createTable() throws SQLException {
            stmt = c.createStatement();
            String sql = "CREATE TABLE entries " +
                    "(id       INT PRIMARY KEY NOT NULL," +
                    " login    CHAR(50)        NOT NULL, " +
                    " pass     CHAR(50)        NOT NULL, " +
                    " nick     CHAR(50)        NOT NULL)";
            stmt.executeUpdate(sql);
            System.out.println("Table created successfully");
    }

    public List<Entry> getAllEntries() {
        try {
            ResultSet resultSet = stmt.executeQuery("SELECT login, pass, nick FROM entries");
            while (resultSet.next()) {
                entries.add(new Entry(
                        resultSet.getString("login"),
                        resultSet.getString("pass"),
                        resultSet.getString("nick")));
            }
            return entries;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void addEntry() {
        try {
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = "INSERT INTO entries (ID,login,pass,nick) VALUES (1, 'login1', 'pass1', 'nick1');";
            stmt.executeUpdate(sql);;

            sql = "INSERT INTO entries (ID,login,pass,nick) VALUES (2, 'login2', 'pass2', 'nick2');";
            stmt.executeUpdate(sql);;

            sql = "INSERT INTO entries (ID,login,pass,nick) VALUES (3, 'login3', 'pass3', 'nick3');";
            stmt.executeUpdate(sql);
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getNickByLoginPass(String login, String pass) {
        for (Entry o : entries) {
            if (o.login.equals(login) && o.pass.equals(pass)) {
                return o.nick;
            }
        }
        return null;
    }

    @Override
    public void start() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(CON_STR);
            System.out.println("Opened database successfully");
        } catch (SQLException | ClassNotFoundException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            stmt.close();
            c.close();
            System.out.println("Сервис аутентификации остановлен");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
