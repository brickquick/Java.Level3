package lesson2.homework2.ClientFX;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public VBox mainBox;
    @FXML
    private TextArea chatArea;
    //Bottom panel
    @FXML
    private TextField inputTextField;
    @FXML
    private Button send;
    //Top panel
    @FXML
    public HBox topPanel;
    @FXML
    private Button authBtn;
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
        openConnection();
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
                            break;
                        }
                        chatArea.appendText(str + "\n");
                    }
                    updateTopPanel();
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
                    closeConnection();
                    System.out.println("Отключение от сервера!");
                    myNick = "";
                    chatArea.appendText("Вы были отключены от сервера!" + "\n");
                }
            });
            t.start();
        } catch (IOException e) {
            updateTopPanel();
            System.err.println("Не удалось подключиться к серверу");
            e.printStackTrace();
            showAlert("Не удалось подключиться к серверу!", e.toString());
        }
    }

    private void showAlert(String msg, String err) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(msg);
        alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(err)));
        alert.showAndWait();
    }

    public void updateTopPanel() {
        if ((myNick == null || myNick == "") || (socket == null || socket.isClosed())) {
            authBtn.setText("Connect to server");
            passField.setVisible(false);
            loginField.setVisible(false);
            passField.setMaxWidth(0);
            loginField.setMaxWidth(0);
            authBtn.setMinWidth(topPanel.getWidth());
            authBtn.setDisable(false);
        }
        if ((myNick == null || myNick == "") && (socket != null && !socket.isClosed())) {
            authBtn.setText("Authentication");
            passField.setVisible(true);
            loginField.setVisible(true);
            passField.setMaxWidth(Double.MAX_VALUE);
            loginField.setMaxWidth(Double.MAX_VALUE);
            authBtn.setMinWidth(0);
            authBtn.setDisable(false);
        }
        if ((myNick != "" && myNick != null) && (socket != null && !socket.isClosed())) {
            authBtn.setText("Online: " + myNick);
            passField.setVisible(false);
            loginField.setVisible(false);
            passField.setMaxWidth(0);
            loginField.setMaxWidth(0);
            authBtn.setMinWidth(topPanel.getWidth());
            authBtn.setDisable(true);
        }
    }

    public void sendMessage() {
        if (!inputTextField.getText().trim().isEmpty()) {
            try {
                out.writeUTF(inputTextField.getText().trim());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Ошибка отправки сообщения");
                showAlert("Ошибка отправки сообщения!", e.toString());
//                JOptionPane.showMessageDialog(null, "Ошибка отправки сообщения");
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
        updateTopPanel();
    }

    public void onAuthClick() {
        if (socket == null || socket.isClosed()) {
            openConnection();
        } else {
            if (socket != null && !socket.isClosed()) {
                try {
                    out.writeUTF("/auth " + loginField.getText() + " " + passField.getText());
                    loginField.setText("");
                    passField.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
