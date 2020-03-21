package test;

import domain.Student;
import service.StudentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: 张天旭
 * @Date: 2020/3/19 15:15
 * @Version 1.0
 */
public class NewTest {



    public static void main(String[] args) {
        // 创建一个业务层
        StudentService service = new StudentService();
        //新增
        //Student student = new Student(10, "张天旭", "男", 18, 3);
        //service.regist(student);
        // 删除
        //service.delete(9);
//        Map<String,Object> student = new HashMap<>();
//        student.put("sid", 9);
//        student.put("sname", "张天旭");
//        student.put("ssex", "男");
//        student.put("sage", 18);
//        student.put("classid", 3);
//        service.regist(student);

//        // 查询单条
//        String student = service.selectOne(1);
//        System.out.println(student);

        // 查询多条
        List<Student> studentList = service.selectList();
        System.out.println(studentList);
    }
}
