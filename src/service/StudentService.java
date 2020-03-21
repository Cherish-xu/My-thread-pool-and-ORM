package service;

import dao.StudentDao;
import domain.Student;
import orm.SqlSession;

import java.util.List;
import java.util.Map;

/**
 * @Author: 张天旭
 * @Date: 2020/3/19 15:16
 * @Version 1.0
 */
public class StudentService {

    // private StudentDao dao = new StudentDao();
    // 利用代理执行
    private StudentDao dao = new SqlSession().getMapper(StudentDao.class);

    //设计一个方法，新增学生（注册）
    public void regist(Map student){
        // 写入数据库    dao层
        dao.insert(student);
    }

    //设计一个方法，删除一个学生信息
    public void delete(Integer sid){
        //需要dao支持
        dao.delete(sid);
    }
//
//    //修改
//    public void upadate(Student student){
//        //需要dao
//        dao.update(student);
//    }
//
    //查询单条信息
    public String selectOne(Integer sid){
        //需要dao
        return dao.selectOne(sid);
    }

    //查询多条信息
    public List<Student> selectList(){
        return dao.selectList();
    }
}
