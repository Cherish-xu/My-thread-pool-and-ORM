package test;

import pool.ConnectionPool;
import pool.MyConnection;

import java.sql.*;

/**
 * @Author: 张天旭
 * @Date: 2020/3/15 20:52
 * @Version 1.0
 */
public class TestMain {

    public static void main(String[] args) throws SQLException {

        // 0.配置文件
        // 1.创建一个连接池
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        // 2. 获取连接
        Connection conn = connectionPool.getConnection();
        // 3.状态参数
        PreparedStatement pstat = conn.prepareStatement("");
        // 4.执行操作
        ResultSet rs = pstat.executeQuery();
        // 5.关闭
        //真正关闭
        rs.close();
        //真正的关闭
        pstat.close();
        //没有真正的关闭，
        conn.close();






//        //原来的方式也是多态
//        try {
//            String drive = "com.mysql.cj.jdbc.Driver";
//            String url = "jdbc:mysql://localhost:3306/lianxi?useSSL=false&serverTimezone=CST";
//            String username = "zhangtianxu";
//            String password = "1211";
//            String sql = "select * from emp";
//            Class.forName(drive);
//            //3.获取连接
//            Connection conn = DriverManager.getConnection(url,username,password);
//            Class clazz = conn.getClass();
//            System.out.println(clazz.getName());
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }


        //模拟六个人同时并发获取五个连接
//        TestThread tt1 = new TestThread();
//        tt1.start();
//        TestThread tt2 = new TestThread();
//        tt2.start();
//        TestThread tt3 = new TestThread();
//        tt3.start();
//        TestThread tt4 = new TestThread();
//        tt4.start();
//        TestThread tt5 = new TestThread();
//        tt5.start();
//        TestThread tt6 = new TestThread();
//        tt6.start();


        //优化之前的写法（线程池单例、将配置文件写入文件）
//        //1.导包--需要
//        //2.加载驱动--不需要
//        //3.获取连接
//        //先从连接池获取一个连接池对象
//        ConnectionPool connectionPool = new ConnectionPool();
//        //调用连接池的getMC方法获取一个类型为MyConnection的连接（包含一个连接和一个状态）
//        MyConnection myConnection = connectionPool.getMC();
//        //获取这个MyConnection中的连接
//        Connection conn = myConnection.getConn();
//        //4.创建状态参数
//        PreparedStatement pstat = conn.prepareStatement("select * from emp");
//        //5.执行操作
//        ResultSet rs = pstat.executeQuery();
//        //6.关闭
//        rs.close();
//        pstat.close();
//        //在这里使用之后不能使用close方法直接将连接关闭，而是应该更改他的状态将他还给连接池
//        // 释放
//        myConnection.setUsed(false);


        //经典的jdbc六部曲            原始的方法
//        try {
//            //JDBC六部曲
//            //1.导包
//            //2.加载驱动
//            String drive = "com.mysql.cj.jdbc.Driver";
//            String url = "jdbc:mysql://localhost:3306/lianxi?useSSL=false&serverTimezone=CST";
//            String username = "zhangtianxu";
//            String password = "1211";
//            String sql = "select * from emp";
//            Class.forName(drive);
//            //3.获取连接
//            //测试获取连接这一行需要多少时间
//            long t1 = System.currentTimeMillis();
//            Connection conn = DriverManager.getConnection(url,username,password);
//            long t2 = System.currentTimeMillis();
//            //4.获取状态参数
//            PreparedStatement pstat = conn.prepareStatement(sql);
//            //5.执行操作
//            //写操作(增删改)   int  executeUpdate();
//            // 读操作(查询)    ResultSet  executeQuery;
//            ResultSet rs = pstat.executeQuery();
//            while (rs.next()){
//                System.out.println(rs.getInt("empno")+"--"+rs.getString("ename")+"--"+rs.getString("job")+"--"+rs.getFloat("sal"));
//            }
//            long t3 = System.currentTimeMillis();
//            System.out.println(t2-t1);
//            System.out.println(t3-t2);
//            //6.关闭
//            rs.close();
//            pstat.close();
//            conn.close();
//        } catch (ClassNotFoundException | SQLException e) {
//            e.printStackTrace();
//        }
    }
}
