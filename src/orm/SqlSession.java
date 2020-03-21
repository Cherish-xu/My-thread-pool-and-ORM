package orm;

import org.omg.CORBA.ValueMember;
import orm.annotation.Delete;
import orm.annotation.Insert;
import orm.annotation.Select;
import orm.annotation.Updata;
import pool.ConnectionPool;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/**
 * @Author: 张天旭
 * @Date: 2020/3/20 10:00
 * @Version 1.0
 */
@SuppressWarnings("all")
public class SqlSession {
    // 存放一个handle属性 用于处理SQL 参数 返回值等
    private Handler handle = new Handler();



    //增删改---方法类似    将其封装为一个方法
    //查询单条数据，查询多条数据类似   封装为一个方法
    //可以处理任何一个表
    //参数：1.sql语句    2.字段的值（语句中的？）

    public void update(String sql,Object obj){
        try {
            // 0.解析sql语句
            SQLAndKey sqlAndKey = handle.parseSQL(sql);
            // 1.获取连接
            Connection conn = ConnectionPool.getInstance().getConnection();
            // 2.获取状态参数
            PreparedStatement pstat = conn.prepareStatement(sqlAndKey.getNewSql());
            // 3.将sql语句与？拼接在一起      原生的pstat.setString(1,"sid")
            if (obj != null) {
                handle.handleParameter(pstat, obj, sqlAndKey.getKeyList());
            }
            // 4.执行executeUpdate()
            pstat.executeUpdate();
            // 5.关闭
            pstat.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(String sql,Object obj){
        this.update(sql,obj);
    }
    public void delete(String sql,Object obj){
        this.update(sql,obj);
    }

    // 没有参数的情况下
    public void update(String sql){
        this.update(sql,null);
    }
    public void delete(String sql){
        this.delete(sql,null);
    }
    public void insert(String sql){
        this.insert(sql,null);
    }

    //==========================================================================================
    // 设计一个方法，用于处理任何一个表格的单条数据查询
    // 参数:1.sql语句   2.sql上的？    3.返回值类型，查询的结果放在什么类型的对象里
    public <T>T selectOne(String sql, Object obj,Class resultType) {
        return (T)this.selectList(sql,obj,resultType).get(0);
    }
    public <T>T selectOne(String sql,Class resultType){
        return this.selectOne(sql,null,resultType);
    }

    // 查询多条
    public <T> List<T> selectList(String sql, Object obj, Class resultType) {
        List<T> result = new ArrayList<>();
        try {
            // 1.解析sql
            SQLAndKey sqlAndKey = handle.parseSQL(sql);
            // 2.创建连接
            Connection conn = ConnectionPool.getInstance().getConnection();
            // 3.状态参数
            PreparedStatement pstat = conn.prepareStatement(sqlAndKey.getNewSql());
            // 4.将sql和提供的obj参数拼接完整
            if (obj != null){
                handle.handleParameter(pstat,obj,sqlAndKey.getKeyList());
            }
            // 5.执行操作
            ResultSet rs = pstat.executeQuery();
            // **6.将结果集中的数据拆分出来，重新存入一个容器中（domain，map，单个数据）
            while (rs.next()){
                // 将结果集的值取出来存入一个容器
                // 将新的容器返回
                result.add((T)handle.handleResult(rs,resultType));
            }
            // 7.关闭
            rs.close();
            pstat.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    public <T> List<T> selectList(String sql,Class resultType){
        return this.selectList(sql,null,resultType);
    }


    //=============================================================================================
    // 根据传入的类型创建一个代理对象
    // 参数是一个接口，返回值是一个对象
    public <T> T getMapper(Class clazz) {
//        // 想要创建一个代理对象，需要三个条件
//        // 1.需要一个类加载器 将clazz加载进来
//        ClassLoader loader = clazz.getClassLoader();
//        // 2.需要一个数组 代理的接口是谁
//        Class[] interfaces = new Class[]{clazz};
//        // 3.需要一个InvocationHandler   明确代理要做的是那件事（增删改中的哪一个）
//        InvocationHandler h = new ProxyHandlerImpl();
//        // 返回值是代理的对象
//        Object obj = Proxy.newProxyInstance(loader,interfaces,h);
//        return (T) obj;


        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 这个是代理对象执行的方法

                // 1.获取方法上面的注解
                Annotation an = method.getAnnotations()[0];
                // 分析注解是什么类型
                // 2.根据这个注解是什么类型分析
                Class type = an.annotationType();
                // 3.获取注解中的sql'语句
                Method valueMethod = type.getDeclaredMethod("value");
                // 4.执行注解的方法执行sql
                String sql = (String) valueMethod.invoke(an);
                // 5.分析参数（args）
                // dao中方法的参数就是args数组
                // 判断数组中的参数是否为空（目前参数只能是没有，或者是一个 即String float int map domain）
                Object param = args == null ? null : args[0];
                // 6.根据注解的类型判断执行那种操作
                if (type == Insert.class){
                    SqlSession.this.insert(sql,param);
                }else if (type == Updata.class){
                    SqlSession.this.update(sql,param);
                }else if (type == Delete.class){
                    SqlSession.this.delete(sql,param);
                }else if (type == Select.class){
                    // 1.获取方法的返回值类型，来替代原来的resultType
                    Class methodReturnTypeClass = method.getReturnType();
                    if (methodReturnTypeClass == List.class){
                        // 如果是个集合代表多条查询
                        // 多条查询需要获取List集合中的泛型
                        // 获取返回值的具体类型
                        Type returnType = method.getGenericReturnType();
                        ParameterizedType realReturnType = (ParameterizedType) returnType;
                        // 继续反射
                        // 获取所有的泛型
                        Type[] patternTypes = realReturnType.getActualTypeArguments();
                        // 我们只需要第一个
                        Type patternType = patternTypes[0];
                        // 将这个泛型还原为原来的class
                        Class realPatternType = (Class) patternType;
                        // 执行查询多条操作
                        return SqlSession.this.selectList(sql,param,realPatternType);
                    }else {
                         // 否则代表单条查询
                        return SqlSession.this.selectOne(sql,param,methodReturnTypeClass);

                    }
                }
                return null;
            }
        });
    }



//    // 一个私有内部类，是InvocationHandler的具体实现类
//    private class ProxyHandlerImpl implements InvocationHandler{
//        @Override
//        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//
//        }
//    }

}
