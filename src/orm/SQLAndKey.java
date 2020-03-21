package orm;

import java.util.List;

/**
 * @Author: 张天旭
 * @Date: 2020/3/19 17:05
 * @Version 1.0
 */
public class SQLAndKey {

    // 这个类用于存放解析sql后得到的两个结果       newSql 和 keyList
    private StringBuilder newSql;
    private List<String> keyList;
    public SQLAndKey() {
    }
    public SQLAndKey(StringBuilder newSql, List<String> keyList) {
        this.newSql = newSql;
        this.keyList = keyList;
    }
    public String getNewSql() {
        return newSql.toString();
    }
    public List<String> getKeyList() {
        return keyList;
    }

}
