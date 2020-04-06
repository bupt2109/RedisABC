package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final String IN = "<- ";
    private static final String OUT = "-> ";

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
    @FXML
    private ComboBox<String> infoBox;
    @FXML
    private TextArea historyData;
    @FXML
    private TabPane mainTab;
    @FXML
    private Tab infoTab;
    @FXML
    private TextArea infoArea;

    @FXML
    private TextField stringGetKey;
    @FXML
    private TextField stringGetValue;
    @FXML
    private TextField stringGetDll;

    private String ip;      //IP
    private int port;       //端口
    private String password;//redis AUTH密码
    private int db;         //数据库0-15
    private String version; //redis version

    private Jedis jedis = null;
    private boolean connected;
    private final String[] INFO_SECTIONS = {
            "All",
            "Server",
            "Clients",
            "Memory",
            "Persistence",
            "Stats",
            "Replication",
            "CPU",
            "Keyspace",
            "Commandstats",
    };

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
        infoBox.setItems(FXCollections.observableArrayList(Arrays.asList(INFO_SECTIONS)));
//        selectBox.setVisibleRowCount(5);
        infoBox.setEditable(false);
    }


    public void connRedis(ActionEvent actionEvent) {
        if(connected){
            closeConn();
        }else{
            openConn();
        }
//        jedis = new Jedis("localhost");
//        JedisPoolConfig pool = new JedisPoolConfig();
    }


    public void selectDB(ActionEvent actionEvent) {
        if (connected) {
            if (selectBox.getValue() != db) {
                db = selectBox.getValue();
                println(OUT + "SELECT:" + db);
                String res = jedis.select(db);
//                info(res);
                println(IN + res);
            }
        } else {
            warn(MSG_NO_CONNECT);
        }
    }

    public void selectInfoTab(Event event) {
        if(connected && infoTab.isSelected()){
            println(OUT + "INFO");
            infoArea.clear();
            infoArea.appendText(jedis.info());
        }
    }

    /**
     * info指令切换section操作
     * @param actionEvent
     */
    public void changeInfoBox(ActionEvent actionEvent) {
        infoArea.clear();
        if(connected) {
            //TODO info返回值解析，可参考https://www.cnblogs.com/wshenjin/p/11431378.html
            String section = infoBox.getValue();
            println(OUT + "INFO " + section);
            infoArea.appendText(jedis.info(section));
        }
    }

    public void stringGetAction(ActionEvent actionEvent) {
        if(connected){
            String key = stringGetKey.getText();
            if(key == null || key.isEmpty()){
                warn("请输入查询值");
            }else{
                println(OUT + "GET "+key);
                String res = jedis.get(key);
                println(IN + res);
                if(res != null){
                    stringGetValue.setText(res);
                }
            }
        }
    }

    public void openConn(){
        ip = ipText.getText();
        port = Integer.parseInt(portText.getText());
        password = passwordText.getText();
        jedis = new Jedis(ip, port);
        println(OUT + "建立连接"+ip+":"+port);
        try{
            jedis.connect();
        }catch (Exception ex){
            error("建立连接失败:" + ex.getMessage());
            return;
        }
        //密码效验
        if (password != null && password.length() > 0) {
            try{
                println(OUT + "AUTH:"+password);
                String pwRes = jedis.auth(password);
                println(IN + pwRes);
            }catch (Exception ex){
                error("密码效验失败:" + ex.getMessage());
                jedis.close();
                return;
            }
        }
        //测试PING
        String pingResult = null;
        try{
            println(OUT + "PING");
            pingResult = jedis.ping();
        }catch (Exception ex){
            error("PING测试失败:" + ex.getMessage());
            jedis.close();
            return;
        }
        if (pingResult.equals("PONG")) {
            println(IN + pingResult);
            connected = true;
            connBth.setText("断开连接");
            info("连接redis成功！");
        } else {
            error("PING测试失败:" + pingResult);
            jedis.close();
        }
    }

    public void closeConn(){
        if(connected){
            jedis.close();
            connected = false;
            connBth.setText("连接");
            println(OUT + "关闭Redis连接");
        }else{
            warn(MSG_NO_CONNECT);
        }
    }


    private void print(String msg){
        historyData.appendText(msg);
    }

    private void println(String msg){
        print(msg + "\n");
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