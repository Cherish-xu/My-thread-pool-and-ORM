package test;

import pool.ConnectionPool;
import pool.MyConnection;

import java.sql.Connection;

/**
 * @Author: 张天旭
 * @Date: 2020/3/18 17:15
 * @Version 1.0
 */
public class TestThread extends Thread{

    @Override
    public void run() {
        //1.获取一个连接池对象
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        //2.获取连接        连接+状态
        Connection mc = connectionPool.getConnection();
        System.out.println(mc);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("资源用完了");
//        mc.setUsed(false);
        //获取连接
//        Connection conn = mc.getConn();
        //如果当前有多个（大于连接池的初始化连接数量）线程同时执行上面的获取连接操作会发生什么
        // 可能会有多个线程抢同一个连接

    }
}
