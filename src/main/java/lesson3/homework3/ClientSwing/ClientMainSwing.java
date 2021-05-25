package lesson3.homework3.ClientSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientMainSwing extends JFrame {
    private final String SERVER_ADDR = "localhost";
    private final int SERVER_PORT = 8189;

    //Elements of Swing//////////////////////////////////////////////////
    private JTextArea chatArea;
    private JTextField msgInputField;
    private JTextField loginInputField = new JTextField("login1");
    private JTextField passInputField = new JTextField("pass1");
    private JButton btnAuth = new JButton();
    private GridLayout layout = new GridLayout(1, 3, 0, 0);
    private JPanel topPanel = new JPanel(new BorderLayout());

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String myNick = "";

    public ClientMainSwing() {
        openConnection();
        prepareGUI();
    }

    public void openConnection() {
        try {
            socket = new Socket(SERVER_ADDR, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            updateTopPanel();
            Thread t = new Thread(() -> {
                try {
                    while (socket != null && !socket.isClosed()) {
                        String str = in.readUTF();
                        if (str.startsWith("/authok ")) {
                            myNick = str.split("\\s")[1];
                            break;
                        }
                        chatArea.append(str + "\n");
                    }
                    updateTopPanel();
                    while (socket != null && !socket.isClosed()) {
                        String strFromServer = in.readUTF().trim();
                        if (strFromServer.equalsIgnoreCase("/end")) {
                            closeConnection();
                            break;
                        }
                        if (strFromServer.startsWith("/newnickok ")) {
                            myNick = strFromServer.split("\\s")[1];
                            updateTopPanel();
                            continue;
                        }
                        chatArea.append(strFromServer + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                    JOptionPane.showMessageDialog(null, "Отключение от сервера");
                    myNick = "";
                    chatArea.append("Вы были отключены от сервера!" + "\n");
                }
            });
            t.start();
        } catch (IOException e) {
            updateTopPanel();
            JOptionPane.showMessageDialog(null, "Не удалось подключиться к серверу");
            System.err.println("Не удалось подключиться к серверу");
            e.printStackTrace();
        }
    }

    public void updateTopPanel() {
        if ((myNick == null || myNick.equals("")) || (socket == null || socket.isClosed())) {
            topPanel.add(btnAuth, BorderLayout.EAST);
            btnAuth.setText("Connect to server");
            btnAuth.setEnabled(true);
            topPanel.remove(loginInputField);
            topPanel.remove(passInputField);
        }
        if ((myNick == null || myNick.equals("")) && (socket != null && !socket.isClosed())) {
            btnAuth.setText("Authentication");
            btnAuth.setEnabled(true);
            topPanel.add(loginInputField);
            topPanel.add(passInputField);
            topPanel.add(btnAuth);
        }
        assert myNick != null;
        if ((!myNick.equals("")) && (socket != null && !socket.isClosed())) {
            btnAuth.setEnabled(false);
            topPanel.add(btnAuth, BorderLayout.EAST);
            btnAuth.setText("Online: " + myNick);
            topPanel.remove(loginInputField);
            topPanel.remove(passInputField);
        }
    }

    public void prepareGUI() {
        // Параметры окна////////////////////////////////////////////////////////
        setBounds(600, 300, 700, 400);
        setTitle("Client Local Chat");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        // Текстовое поле для вывода сообщений///////////////////////////////////
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);


        // Нижняя панель с полем для ввода сообщений и кнопкой отправки сообщений
        JPanel bottomPanel1 = new JPanel(new BorderLayout());
        JButton btnSendMsg = new JButton("Отправить >");
        bottomPanel1.add(btnSendMsg, BorderLayout.EAST);
        add(bottomPanel1, BorderLayout.SOUTH);
        msgInputField = new JTextField();
        bottomPanel1.add(msgInputField, BorderLayout.CENTER);
        btnSendMsg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        msgInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
                msgInputField.grabFocus();
            }
        });


        //Нижняя панель с полями логина и пароля и кнопкой аутентификации////////
        topPanel.setLayout(layout);
        add(topPanel, BorderLayout.NORTH);
        updateTopPanel();
        loginInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!loginInputField.getText().trim().isEmpty() && passInputField.getText().trim().isEmpty()) {
                    passInputField.grabFocus();
                }
                if (!loginInputField.getText().trim().isEmpty() && !passInputField.getText().trim().isEmpty()) {
                    btnAuth.doClick();
                }
            }
        });
        passInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!passInputField.getText().trim().isEmpty() && !loginInputField.getText().trim().isEmpty()) {
                    btnAuth.doClick();
                }
            }
        });
        btnAuth.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (socket == null || socket.isClosed()) {
                    openConnection();
                    return;
                }
                if (socket != null && !socket.isClosed()) {
                    onAuthClick();
                }
            }
        });


        // Настраиваем действие на закрытие окна/////////////////////////////////
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (socket != null && !socket.isClosed()) {
                    //                        out.writeUTF("/end");
                    closeConnection();
                }
            }
        });

        setVisible(true); //visible///////////////////////////////////////////////
    }

    public void onAuthClick() {
        if (socket == null || socket.isClosed()) {
            openConnection();
        }
        if (socket != null && !socket.isClosed()) {
            try {
                out.writeUTF("/auth " + loginInputField.getText() + " " + passInputField.getText());
                loginInputField.setText("");
                passInputField.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void closeConnection() {
        if (socket != null && !socket.isClosed()) {
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
        updateTopPanel();
    }

    public void sendMessage() {
        if (!msgInputField.getText().trim().isEmpty()) {
            try {
//                chatArea.append(msgInputField.getText() + "\n");
                out.writeUTF(msgInputField.getText());
                msgInputField.setText("");
                msgInputField.grabFocus();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ошибка отправки сообщения");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientMainSwing();
            }
        });
    }
}
