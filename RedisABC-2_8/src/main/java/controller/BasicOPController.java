package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * BasicOPController
 *
 * @author yu
 * @version v1.0
 * @Description
 * @Date 2020/4/1
 */
public class BasicOPController implements Initializable {

    private static final String MSG_NO_CONNECT = "未连接Redis服务器";

    @FXML
    private TextField ipText;
    @FXML
    private TextField portText;
    @FXML
    private TextField passwordText;
    @FXML
    private Button connBth;
    @FXML
    private ComboBox<Integer> selectBox;

    private String ip;
    private int port;
    private String password;
    private int db;

    private Jedis jedis = null;
    private boolean connected;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Integer> dbs = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            dbs.add(i);
        }
        selectBox.setItems(FXCollections.observableArrayList(dbs));
        selectBox.setVisibleRowCount(5);
        selectBox.setEditable(false);
        db = 0;
    }


    public void connRedis(ActionEvent actionEvent) {
        ip = ipText.getText();
        port = Integer.parseInt(portText.getText());
        password = passwordText.getText();
        jedis = new Jedis(ip, port);
        if (password != null && password.length() > 0) {
            jedis.auth(password);
        }
        String res = jedis.ping();
        if (res.equals("PONG")) {
            connected = true;
            connBth.setText("断开连接");
            info(res);
        } else {
            error("连接失败");
        }

//        jedis = new Jedis("localhost");
//        JedisPoolConfig pool = new JedisPoolConfig();
    }


    public void selectDB(ActionEvent actionEvent) {
        if (connected) {
            if (selectBox.getValue() != db) {
                db = selectBox.getValue();
                String res = jedis.select(db);
                info(res);
            }
        } else {
            warn(MSG_NO_CONNECT);
        }
    }


    private void info(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.titleProperty().set("信息");
        alert.headerTextProperty().set(msg);
        alert.showAndWait();
    }

    private void warn(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.titleProperty().set("警告");
        alert.headerTextProperty().set(msg);
        alert.showAndWait();
    }

    private void error(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.titleProperty().set("错误");
        alert.headerTextProperty().set(msg);
        alert.showAndWait();
    }
}