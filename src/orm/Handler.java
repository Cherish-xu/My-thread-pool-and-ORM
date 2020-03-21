package orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: 张天旭
 * @Date: 2020/3/20 10:02
 * @Version 1.0
 */
public class Handler {

    // 只负责处理SQL
    // 处理SQL上的？信息
    // 处理不同的值 map Object


    //设计一个方法，这个方法负责解析sql语句
    // 负责第0步
    SQLAndKey parseSQL(String sql) {
        // sql: insert into student values(#{sid},#{sname},#{ssex},#{sage},#{class});
        // 1.将所有的#{xxx}取出来  作为key---以后跟domain或者map中的key对应
        // 2.将现在的sql替换为带？的形式
        // 目的是存放所有解析出来的key
        List<String> keyList = new ArrayList<>();
        StringBuilder newSql = new StringBuilder();

        // 开始解析
        while (true) {
            // 找到#{出现的位置
            // 找左边的
            int left = sql.indexOf("#{");
            // 找右边的
            int right = sql.indexOf("}");
            // 都不为-1，说明找到了
            if (left != -1 && right != -1 && left < right) {
                // 将原来sql的#{之前的先存起来 ----- insert into student values(
                newSql.append(sql.substring(0, left));
                // 拼接一个?
                newSql.append("?");
                // 将key取出来
                String key = sql.substring(left + 2, right);
                keyList.add(key);
            } else {
                // 如果进入到了else中，则说明没有成对的了
                newSql.append(sql.substring(right + 1));
                break;
            }
            // 将sql的右半部分保留之后继续循环
            sql = sql.substring(right + 1);
        }
        // 将解析完的结果存入一个对象
//        System.out.println(newSql);
//        System.out.println(keyList);
        return new SQLAndKey(newSql, keyList);
    }

    // 设计一个方法    负责分析Object类型    将sql和Object中的值拼接完整
    // 负责第3步
    // 参数：1.需要一个pstat对象   2.需要真正的值（Object）  3.需要分析后得到的key的集合
    void handleParameter(PreparedStatement pstat, Object obj, List keyList) throws SQLException {
        // 反射获取Object是什么
        Class clazz = obj.getClass();
        // 判断类型 基本类型
        if (clazz == int.class || clazz == Integer.class) {
            pstat.setInt(1, (Integer) obj);
        } else if (clazz == double.class || clazz == Double.class) {
            pstat.setDouble(1, (Double) obj);
        } else if (clazz == float.class || clazz == Float.class) {
            pstat.setFloat(1, (Float) obj);
        } else if (clazz == String.class) {
            pstat.setString(1, (String) obj);
        } else if (clazz.isArray()) {
            // 如果是数组   就先不操作
        } else {
            //不是基础类型了（基本类型+包装类+String）
            if (obj instanceof Map) {
                // 判断是不是map类型  如果是，需要将map中的key遍历，寻找key对应的value 将value赋值到SQL上
                this.setMap(pstat, obj, keyList);
            } else {
                // 如果你不是map类型则认为是domain或是异常
                // 如果是domain需要将key遍历，反射去找属性，获取属性的值，然后拼接到sql上
                this.setObject(pstat, obj, keyList);
            }
        }
    }

    // 设计一个方法，负责解析当Object是Map时的赋值
    private void setMap(PreparedStatement pstat, Object obj, List keyList) throws SQLException {
        Map map = (Map) obj;
        for (int i = 0; i < keyList.size(); i++) {
            // 这个key是  #{sid}类型的
            String key = (String) keyList.get(i);
            // 传递的值
            Object value = (Object) map.get(key);
            pstat.setObject(i + 1, value);
        }
    }

    // 设计一个方法，负责解析当Object是domain时的解析
    private void setObject(PreparedStatement pstat, Object obj, List keyList) throws SQLException {
        //需要将keyList中解析出来的key遍历 反射去domain对象中找属性 获取属性value 赋值到SQL上拼接
        try {
            //获取obj对应的class
            Class clazz = obj.getClass();
            //遍历keyList
            for (int i = 0; i < keyList.size(); i++) {
                //获取key
                String key = (String) keyList.get(i);
                //通过key反射找到domain中对应的属性     (数据库列名  属性名不一致)
                Field field = clazz.getDeclaredField(key);
                String fieldName = field.getName();
                //拼接属性对应的get方法名
                String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                //反射找到属性对应的get方法
                Method getMethod = clazz.getMethod(getMethodName);
                //执行get方法获取属性值
                Object value = getMethod.invoke(obj);
                //让pstat将数据拼接完整
                pstat.setObject(i + 1, value);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    //========================================================================================
    // 设计一个方法，分析给定的Class是什么类型的，确定返回值类型
    // 之后进行组装:将结果集的值拆分，填充到新的容器中
    // 参数:
    Object handleResult(ResultSet rs, Class resultType) throws SQLException {
        // 先创建一个用于存放的容器
        Object result = null;
        if (resultType == int.class || resultType == Integer.class) {
            return rs.getInt(1);
        } else if (resultType == float.class || resultType == Float.class) {
            return rs.getFloat(1);
        } else if (resultType == String.class) {
            return rs.getString(1);
        } else if (resultType == double.class || resultType == Double.class) {
            return rs.getDouble(1);
        } else {
            try {
                result = resultType.newInstance();
                // 走到这个分支就是引用类型的，不是Map就是domain
                if (resultType == HashMap.class) {
                    // 需要创建一个map对象  将rs的值存入对象中
                    // 需要将结果集的信息一个一个取出  存入map
                    result = this.getMap(rs, result);
                } else {
                    // 是个domain
                    // 需要将结果集的信息一个一个取出  存入domain
                    this.getObject(rs,result);
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // 设计一个方法，用于给map赋值  将结果集的值拆分出来放入map集合中
    private Map getMap(ResultSet rs,Object result) throws SQLException {
        // 获取结果集中的全部信息  存入map集合中    key value
        // 获取结果集中全部的信息，包括了列名和值
        ResultSetMetaData metaData = rs.getMetaData();
        // 遍历结果集中全部的列名
        // 列是从1开始的
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            // 获取一个列名
            String columnName = metaData.getColumnName(i);
            // 取得列对应的值
            Object value = rs.getObject(columnName);
            // 存入map集合
            ((Map)result).put(columnName,value);
        }
        return (Map) result;
    }

    // 设计一个方法，用于给domain赋值   将结果集的信息去吃来放在domain中
    private void getObject(ResultSet rs, Object result) throws SQLException {
        try {
            // 创建一个对象对应的类
            Class clazz = result.getClass();
            // 获取结果集的全部信息
            ResultSetMetaData metaData = rs.getMetaData();
            // 讲结果集的信息遍历
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                // 获取每一列的名字
                String colunmName = metaData.getColumnName(i);
                // 获取结果集的值
                Object value = rs.getObject(colunmName);
                // 将值存入domain的属性里
                // 通过列名字找属性
                // 要求列名字和属性一致
                Field field = clazz.getDeclaredField(colunmName);
                // 获取属性的名字
                String fieldName = field.getName();
                // 根据属性名字拼接处对应的set方法
                String setMethodName = "set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
                // 找到set方法
                Method setMethod = clazz.getMethod(setMethodName, field.getType());
                setMethod.invoke(result,value);
            }
        }catch (NoSuchFieldException | NoSuchMethodException e){
        e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
