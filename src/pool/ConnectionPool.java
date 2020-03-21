package pool;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 张天旭
 * @Date: 2020/3/15 22:07
 * @Version 1.0
 *
 * 这个类就是连接池
 * 应该有一个属性--一个集合，集合中存放了很多个MyConnection  每一个MyConnection有一个连接和当前的状态
 * 这个类的主要功能就是管理这些连接
 * 获取连接和连接状态的变化
 */
public class ConnectionPool {

    //将连接池设计成单例模式
    // 1.私有构造方法
    private ConnectionPool() {

    }
    // 2.在当前类中自己创建一个对象
    // 类中的成员：属性（）可以 方法（不可以，方法每一次调用都执行）  构造方法（不可以，上面已经私有）    块（不可以，没有返回值，创建的对象拿不出来）
    // 写一个私有的静态的当前类对象
    //懒汉式  先不加载对象
    private volatile static ConnectionPool connectionPool;
//    // 饿汉式     私有：不可以被访问    静态：只创建一次（若非静态则会进入死循环无限的调用）
//    private static ConnectionPool connectionPool = new ConnectionPool();
    // 3.设计一个方法，用来返回当前对象
    //静态：调用方法是还没有对象，静态是为了可以通过类名直接调用
    // 可能会产生线程安全的问题 --- 加锁
    public static ConnectionPool getInstance(){
        // 第一遍判断---如果为空将其锁定
        if (connectionPool == null){
            synchronized (ConnectionPool.class) {
                // 第二次判断   如果将其锁定后还为空   则证明为空可以new对象    如果不为空则证明第一次判断是发生了线程并发，你判断的同时有另外的也在判断
                if (connectionPool == null) {
                    connectionPool = new ConnectionPool();
                }
            }
        }
        return connectionPool;
    }
//    //饿汉式
//    public static ConnectionPool getInstance(){
//        return connectionPool;
//    }





    //有一个集合 （大池子）里面装着很多的连接
    //这个泛型为什么是MyConnection 而不是Connection？       因为MyConnection中还有一个状态的属性
    private List<Connection> poolList = new ArrayList<>();
    //初始化时 先向块中存放十个连接
    //表示这个块在初始化是创建的连接个数
    private int minConnectionCount = Integer.parseInt(ConfigReader.getValue("minConnectionCount"));

    //需要给池子里面先存放一些连接
    //在list集合有了之后这个池子里就可以有东西了，所以用了块（构造方法也可以）
    {
        for (int i = 0;i < minConnectionCount;i++){
            poolList.add(new MyConnection());
        }
    }

    //设计一个方法    是给用户拿去使用的   返回一个连接
    public synchronized Connection getMC(){
        Connection result = null;
        //从连接池中找寻一个可用连接返回
        for (int i = 0;i < poolList.size();i++){
            MyConnection mc = (MyConnection) poolList.get(i);
            //如果获取的这个MyConnection的Used属性是false，则证明找到了一个可用的连接
            if (!mc.isUsed()) {
                //将这个连接占有，讲他的状态改为true
                mc.setUsed(true);
                result = mc;
                break;
            }
        }
        return result;
    }


    //设计一个方法    添加一个排队等待的机制
    public Connection getConnection() {
        //直接调用上面的获取连接的方法
        Connection mc = this.getMC();
        //记录等待的时间，调用一次睡100毫秒，当count超过50（超过五秒）的时候不再等待
        int count = 0;
        //如果获取的为空，mc=null
        while (mc == null && count < Integer.parseInt(ConfigReader.getValue("waitTime"))*10) {
            //就在调用一次
            mc = this.getMC();
            //每次调用后让线程睡一会，给CPU一个缓冲的时间
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
        }
        //跳出循环有两种可能     1.找到了   2.超时了
        // 如果等了五秒了还没获取到连接
        if (mc == null){
            //自定义异常
        }
        //如果没有抛出异常就返回
        return mc;
    }
}
