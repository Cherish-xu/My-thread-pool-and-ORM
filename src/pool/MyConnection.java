package pool;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @Author: 张天旭
 * @Date: 2020/3/15 21:44
 * @Version 1.0
 *
 * 自己定义的类
 * 累的目的是为了将一个真实的类和一个状态绑定在一起
 * 类中有两个属性
 * 一个是真实的连接
 * 一个是状态
 */
public class MyConnection extends AbstractConnection{

    private Connection conn;
    //false表示空闲状态   true表示正在占用
    private boolean used = false;

    private static String drive = ConfigReader.getValue("driver");
    private static String url = ConfigReader.getValue("url");
    private static String username = ConfigReader.getValue("username");
    private static String password = ConfigReader.getValue("password");

    //静态块只需要执行一次    加载驱动反射的类是一样的
    static {
        try {
            Class.forName(drive);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //非静态块每次都要执行，
    { 
        try {
            conn = DriverManager.getConnection(url,username,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //添加get和set方法

    public Connection getConn() {
        return conn;
    }

    //boolean类型的get方法在命名通常叫做is..方法
    public boolean isUsed() {
        return used;
    }

    //这个set方法是需要的，因为要更改连接的状态
    public void setUsed(boolean used) {
        this.used = used;
    }



    //=============================以下都是实现接口的方法(如果觉得多，可以使用缺省适配器模式)===================================

    @Override
    public void close() throws SQLException {
        this.setUsed(false);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        PreparedStatement pstat = this.conn.prepareStatement(sql);
        return pstat;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return null;
    }

}
