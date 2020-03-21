package pool;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @Author: 张天旭
 * @Date: 2020/3/18 16:33
 * @Version 1.0
 */
public class ConfigReader {

    // 目的是为了读取文件(硬盘)中的配置信息
    // 需要借助一个类  Properties
    private static Properties properties;
    //让当前这个类加载的时候一次性的将这个文件中的信息读取出来放入缓存中
    private static HashMap<String, String> configMap;
    //当前类加载的时候读取文件存入map集合中
    static {
        try {//初始化对象
            properties = new Properties();
            //初始化集合
            configMap = new HashMap<String, String>();
            //将文件中的全部内容读取出来
            //加载一个流（目前不太理解）
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("configuration.properties");
            properties.load(is);
            //将文件中的所有内容都读取出来
            //遍历将文件中的内容都取出来             类似于map集合的遍历
            //获取所有 key
            Enumeration en = properties.propertyNames();
            //判断有没有多余的元素    就是判断有没有下一个
            while (en.hasMoreElements()) {
                //取出下一个元素
                String key = (String) en.nextElement();
                //通过key取出对应的值
                String value = properties.getProperty(key);
                //将每一次循环得出的key和value存入集合
                configMap.put(key,value);
            }
            //关闭流
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //提供一个从缓存集合内读取数据的方法
    public static String getValue(String key){
        return configMap.get(key);
    }
}
