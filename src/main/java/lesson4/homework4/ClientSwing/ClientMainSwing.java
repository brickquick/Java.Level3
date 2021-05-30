package lesson4.homework4.ClientSwing;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
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

    private volatile FileOutputStream fos;
    private volatile FileInputStream fis;

    private volatile String myNick = "";
    private volatile String myLogin = "";

    public ClientMainSwing() {
        openConnection();
        prepareGUI();
    }

    private void openConnection() {
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
                            myLogin = str.split("\\s")[2];
                            initiateLoginFile(myLogin);
                            break;
                        }
                        writeLoginChatFile(str);
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
                        writeLoginChatFile(strFromServer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                    JOptionPane.showMessageDialog(null, "Отключение от сервера");
                    myNick = "";
                    chatArea.setText("");
                    writeLoginChatFile("Вы были отключены от сервера!");
                    myLogin = "";
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    private void prepareGUI() {
        // Параметры окна////////////////////////////////////////////////////////
        setBounds(600, 300, 700, 400);
        setTitle("Client Local Chat");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Текстовое поле для вывода сообщений///////////////////////////////////
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        // Autoscroll
        DefaultCaret caret = (DefaultCaret) chatArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Нижняя панель с полем для ввода сообщений и кнопкой отправки сообщений
        JPanel bottomPanel1 = new JPanel(new BorderLayout());
        JButton btnSendMsg = new JButton("Отправить >");
        bottomPanel1.add(btnSendMsg, BorderLayout.EAST);
        add(bottomPanel1, BorderLayout.SOUTH);
        msgInputField = new JTextField();
        bottomPanel1.add(msgInputField, BorderLayout.CENTER);
        btnSendMsg.addActionListener(e -> sendMessage());
        msgInputField.addActionListener(e -> {
            sendMessage();
            msgInputField.grabFocus();
        });

        // Верхняя панель с полями логина и пароля и кнопкой аутентификации////////
        topPanel.setLayout(layout);
        add(topPanel, BorderLayout.NORTH);
        updateTopPanel();
        loginInputField.addActionListener(e -> {
            if (!loginInputField.getText().trim().isEmpty()) {
                passInputField.grabFocus();
            }
        });
        passInputField.addActionListener(e -> {
            if (!passInputField.getText().trim().isEmpty() && !loginInputField.getText().trim().isEmpty()) {
                btnAuth.doClick();
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

        setVisible(true);
        //visible///////////////////////////////////////////////
    }

    private void updateTopPanel() {
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

    private void onAuthClick() {
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


    private void closeConnection() {
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

    private void sendMessage() {
        if (!msgInputField.getText().trim().isEmpty()) {
            try {
                out.writeUTF(msgInputField.getText());
                msgInputField.setText("");
                msgInputField.grabFocus();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ошибка отправки сообщения");
            }
        }
    }

    private void initiateLoginFile(String login) {
        chatArea.setText("");
        try {
            File f = new File("src/main/resources/history_" + login + ".txt");
            fos = new FileOutputStream("src/main/resources/history_" + login + ".txt", true);
            if (f.createNewFile()) {
                System.out.println("File created");
            } else {
                System.out.println("File already exists");
                try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/history_" + login + ".txt"))) {
//                    List<String> lines = new LinkedList<>();
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        lines.add(line);
//                    }
//                    if (lines.size() > 100) {
//                        for (int i = 100; i > 0; i--) {
//                            chatArea.append(lines.get(lines.size() - i) + "\n");
//                        }
//                    } else {
//                        for (String ln : lines) {
//                            chatArea.append(ln + "\n");
//                        }
//                    }
                    String[] lines = new String[100];
                    int lastNdx = 0;
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        if (lastNdx == lines.length) {
                            lastNdx = 0;
                        }
                        lines[lastNdx++] = line;
                    }
                    for (int ndx = lastNdx; ndx < lines.length && lines[ndx] != null; ndx++) {
                        chatArea.append(lines[ndx] + "\n");
                    }
                    for (int ndx = 0; ndx < lastNdx && lines[ndx] != null; ndx++) {
                        chatArea.append(lines[ndx] + "\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLoginChatFile(String text) {
        text += "\n";
        chatArea.append(text);
        try {
            fos.write(text.getBytes());
            fos.flush();
//            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
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
