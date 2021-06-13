package lesson6.homework6.Server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {
    private MyServer myServer;
    private volatile Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private static final int SOCKET_TIMEOUT = 20000; //120000

    private static final Logger LOGGER = LogManager.getLogger(ClientHandler.class);

    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer myServer, Socket socket, int order) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "UNKNOWN-" + order;
//            System.out.println("Подключился " + name);
            LOGGER.info("Подключился " + name);
            socket.setSoTimeout(SOCKET_TIMEOUT);
            ExecutorService service = Executors.newFixedThreadPool(1);
            service.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        authentication();
                        readMessages();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        closeConnection();
                    }
                }
            });
            service.shutdown();
        } catch (IOException e) {
            LOGGER.error("Проблемы при создании обработчика клиента");
            throw new RuntimeException("Проблемы при создании обработчика клиента");
        }
    }

    private void authentication() throws IOException {
        while (true) {
            try {
                String str = in.readUTF();
                if (str.startsWith("/auth")) {
                    String[] parts = str.split("\\s");
                    String nick;
                    String login;
                    if (parts.length <= 2) {
                        sendMsg("Server: Недостаточно данных для аутентификации");
                    } else {
                        nick = myServer.getAuthService().getNickByLoginPass(parts[1], parts[2]);
                        login = parts[1];
                        if (nick != null) {
                            if (!myServer.isAccountBusy(nick)) {
                                sendMsg("/authok " + nick + " " + login);
                                String log = name;
//                            System.out.print(name);
                                name = nick;
                                myServer.subscribe(this);
                                socket.setSoTimeout(0);
//                            System.out.println(" аутентифицирован под ником: " + name);
                                log += " аутентифицирован под ником: " + name;
                                LOGGER.info(log);
                                myServer.broadcastMsg(name + " зашел в чат");
                                LOGGER.info(name + " зашел в чат");
                                return;
                            } else {
                                sendMsg("Учетная запись уже используется");
                            }
                        } else {
                            sendMsg("Неверные логин/пароль");
                        }
                    }
                }
            } catch (SocketTimeoutException e) {
                if (socket.getSoTimeout() > 10000) {
                    sendMsg("Server: ЕСЛИ ВЫ НЕ АВТОРИЗУЕТЕСЬ, ТО БУДЕТЕ ОТКЛЮЧЕНЫ ЧЕРЕЗ 10 СЕКУНД!");
                } else {
                    throw new SocketTimeoutException();
                }
                socket.setSoTimeout(10000);
            }
        }
    }

    private void readMessages() throws IOException {
        while (true) {
            String strFromClient = in.readUTF().trim();
//            System.out.println("от " + name + ": " + strFromClient);
            LOGGER.info("от " + name + ": " + strFromClient);
            if (strFromClient.startsWith("/")) {
                String[] tokens = strFromClient.split("\\s");
                if (strFromClient.equals("/end")) {
                    break;
                }
                if (strFromClient.startsWith("/w ")) {
                    String nick = tokens[1];
                    String msg = strFromClient.substring(4 + nick.length());
                    myServer.sendMsgToClient(this, nick, msg);
                    continue;
                }
                if (strFromClient.equals("/clients")) {
                    myServer.readClientsList(this);
                    continue;
                }
                if (strFromClient.startsWith("/newnick ")) {
                    if (tokens.length < 3) {
                        sendMsg("Недостаточно данных для смены ника");
                    } else {
                        String oldNick = tokens[1];
                        String newNick = tokens[2];
                        if (oldNick.equals(name)) {
                            if (!myServer.isAccountBusy(newNick) && !myServer.getAuthService().isNickBusy(newNick)) {
                                myServer.unsubscribe(this);
                                myServer.getAuthService().changeNick(oldNick, newNick);
                                name = newNick;
                                myServer.subscribe(this);
                                sendMsg("/newnickok " + name);
                                myServer.broadcastMsg(oldNick + " сменил ник на " + newNick);
                                LOGGER.info(oldNick + " сменил ник на " + newNick);
                            } else {
                                sendMsg("Аккаунт с таким ником уже существует");
                            }
                            continue;
                        }
                    }
                }
                if (strFromClient.equals("/showtable")) {
                    myServer.getAuthService().showTable();
                    continue;
                }
                if (strFromClient.equals("/spam100")) {
                    for (int i = 0; i <= 100; i++) {
                        sendMsg("Server:spam " + i);
                    }
                    continue;
                }
                if (strFromClient.equals("/help")) {
                    sendMsg("Chat помощь:\n/clients - получить список ников подключенных пользователей\n" +
                            "/w <ник> <сообщение> - послать личное сообщение\n" +
                            "/newnick <текущий ник> <новый ник> - сменить ник на новый");
                } else {
                    sendMsg("Введите /help, чтобы получить список основных команд.");
                }
                continue;
            }
            myServer.broadcastMsg(name + ": " + strFromClient);
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        if (myServer.isAccountBusy(name)) {
            myServer.unsubscribe(this);
            myServer.broadcastMsg(name + " вышел из чата");
        }
        LOGGER.info(name + " вышел из чата");
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
