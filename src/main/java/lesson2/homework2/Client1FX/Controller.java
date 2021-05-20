package lesson2.homework2.Client1FX;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextArea chatArea;
    @FXML
    private TextField inputTextField;
    @FXML
    private Button send;
    @FXML
    private Button auth;
    @FXML
    private TextField loginField;
    @FXML
    private TextField passField;

    private String myNick;

    private final String SERVER_ADDR = "localhost";
    private final int SERVER_PORT = 8189;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        start();
    }

//    public void updateTopPanel() {
//        if ((myNick == null || myNick == "") || (socket == null || socket.isClosed())) {
//            topPanel.add(btnAuth, BorderLayout.EAST);
//            btnAuth.setText("Connect to server");
//            btnAuth.setEnabled(true);
//            topPanel.remove(loginInputField);
//            topPanel.remove(passInputField);
//        }
//        if ((myNick == null || myNick == "") && (socket != null && !socket.isClosed())) {
//            btnAuth.setText("Authentication");
//            btnAuth.setEnabled(true);
//            topPanel.add(loginInputField);
//            topPanel.add(passInputField);
//            topPanel.add(btnAuth);
//        }
//        if ((myNick != "") && (socket != null && !socket.isClosed())) {
//            btnAuth.setEnabled(false);
//            topPanel.add(btnAuth, BorderLayout.EAST);
//            btnAuth.setText("Online: " + myNick);
//            topPanel.remove(loginInputField);
//            topPanel.remove(passInputField);
//        }
//    }

//    public void openConnection() {
//        try {
//            socket = new Socket(SERVER_ADDR, SERVER_PORT);
//            in = new DataInputStream(socket.getInputStream());
//            out = new DataOutputStream(socket.getOutputStream());
//
////            updateTopPanel();
//            Thread t = new Thread(() -> {
//                try {
//                    while (socket != null && !socket.isClosed()) {
//                        String str = in.readUTF();
//                        if (str.startsWith("/authok ")) {
//                            myNick = str.split("\\s")[1];
//                            break;
//                        }
//                        chatArea.append(str + "\n");
//                    }
////                    updateTopPanel();
//                    while (socket != null && !socket.isClosed()) {
//                        String strFromServer = in.readUTF().trim();
//                        if (strFromServer.equalsIgnoreCase("/end")) {
//                            closeConnection();
//                            break;
//                        }
//                        chatArea.append(strFromServer + "\n");
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    closeConnection();
//                    JOptionPane.showMessageDialog(null, "Отключение от сервера");
//                    myNick = "";
//                }
//            });
//            t.start();
//        } catch (IOException e) {
////            updateTopPanel();
////            JOptionPane.showMessageDialog(null, "Не удалось подключиться к серверу");
//            System.err.println("Не удалось подключиться к серверу");
//            e.printStackTrace();
//        }
//    }

    public void sendMessage() {
        if (!inputTextField.getText().trim().isEmpty()) {
            chatArea.appendText(inputTextField.getText() + "\n");
            try {
                out.writeUTF(inputTextField.getText().trim());
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputTextField.clear();
            inputTextField.requestFocus();
        } else {
            inputTextField.clear();
            inputTextField.requestFocus();
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
    }

    private void start() {
        try {
//            setAuthorized(false);
            socket = new Socket(SERVER_ADDR, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread t = new Thread(() -> {
                try {
                    while (socket != null && !socket.isClosed()) {
                        String str = in.readUTF();
                        if (str.startsWith("/authok ")) {
//                            setAuthorized(true);
                            myNick = str.split("\\s")[1];
                            break;
                        }
                        chatArea.appendText(str + "\n");
                    }
                    while (socket != null && !socket.isClosed()) {
                            String strFromServer = in.readUTF();
                            if (strFromServer.equalsIgnoreCase("/end")) {
                                closeConnection();
                                break;
                            }
                            chatArea.appendText(strFromServer + "\n");
                        }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //                        setAuthorized(false);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myNick = "";
                }
            });
            t.start();
        } catch (IOException e) {
//            showAlert("Не удалось подключиться к серверу");
            System.out.println("Не удалось подключиться к серверу");
            e.printStackTrace();
        }
    }

    public void onAuthClick() {
        if (socket == null || socket.isClosed()) {
            start();
        }
        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passField.getText());
            loginField.setText("");
            passField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void focusFieldPass() {
        if (!loginField.getText().trim().isEmpty()) {
            passField.requestFocus();
        }
    }

    public void openConnectionFromPassField() {
        if (!passField.getText().trim().isEmpty() && !loginField.getText().trim().isEmpty()) {
            onAuthClick();
        }
    }

}
