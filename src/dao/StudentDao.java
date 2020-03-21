package dao;

import domain.Student;
import orm.SqlSession;
import orm.annotation.Delete;
import orm.annotation.Insert;
import orm.annotation.Select;
import orm.annotation.Updata;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: 张天旭
 * @Date: 2020/3/19 15:17
 * @Version 1.0
 */
public interface StudentDao {
    // private SqlSession sqlSession = new SqlSession();

    // 封装过的方法
    // 设计一个新增数据的方法
    @Insert("insert into student values(#{sid},#{sname},#{ssex},#{sage},#{classid})")
    public void insert(Map student);
    // 通过sid删除
    @Delete("delete from student where sid = #{sid}")
    public void delete(Integer sid);
    // 修改
    @Updata("update student set sname=#{sname},ssex=#{ssex},sage=#{sage} where sid=#{sid}")
    public void update(Student student);
    // 查询单条
    @Select("select sname from student where sid = #{sid}")
    public String selectOne(Integer sid);
    // 查询多条
    @Select("select * from student")
    public List<Student> selectList();




    //=====================以下是原生的方法========================================
//    // 设计一个新增数据的方法
//    public void insert(Student student){
//        String sql = "insert into student value(?,?,?,?,?)";
//        try {
//            // 传统的流程
//            // 1.导包
//            // 2.加载驱动
//            // 3.获取连接
//            Connection conn = ConnectionPool.getInstance().getConnection();
//            // 4.创建状态参数
//            PreparedStatement pstat = conn.prepareStatement(sql);
//            // 将sql中需要替换的参数赋值
//            pstat.setInt(1, student.getSid());
//            pstat.setString(2, student.getSname());
//            pstat.setString(3, student.getSsex());
//            pstat.setInt(4, student.getSage());
//            pstat.setInt(5,student.getClassid());
//            // 5.执行操作
//            pstat.executeUpdate();
//            // 6.关闭
//            pstat.close();
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //删除数据库中的信息
//    public void delete(Integer sid){
//        String sql = "delete from student where id = ";
//        try {
//            // 传统的流程
//            // 1.导包
//            // 2.加载驱动
//            // 3.获取连接
//            Connection conn = ConnectionPool.getInstance().getConnection();
//            // 4.创建状态参数
//            PreparedStatement pstat = conn.prepareStatement(sql);
//            // 将sql中需要替换的参数赋值
//            pstat.setInt(1, sid);
//            // 5.执行操作
//            pstat.executeUpdate();
//            // 6.关闭
//            pstat.close();
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //修改
//    public void update(Student student){
//        String sql = "update student set sname = ?,ssex = ?,sage = ?,classid = ? where sid = ?";
//        try {
//            // 传统的流程
//            // 1.导包
//            // 2.加载驱动
//            // 3.获取连接
//            Connection conn = ConnectionPool.getInstance().getConnection();
//            // 4.创建状态参数
//            PreparedStatement pstat = conn.prepareStatement(sql);
//            // 将sql中需要替换的参数赋值
//
//            pstat.setString(1, student.getSname());
//            pstat.setString(2, student.getSsex());
//            pstat.setInt(3, student.getSage());
//            pstat.setInt(4,student.getClassid());
//            pstat.setInt(5, student.getSid());
//            // 5.执行操作
//            pstat.executeUpdate();
//            // 6.关闭
//            pstat.close();
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //查询
//    public Student selectOne(Integer sid){
//        Student student = null;
//        String sql = "select * from student where sid = ?";
//        try {
//            // 传统的流程
//            // 1.导包
//            // 2.加载驱动
//            // 3.获取连接
//            Connection conn = ConnectionPool.getInstance().getConnection();
//            // 4.创建状态参数
//            PreparedStatement pstat = conn.prepareStatement(sql);
//            // 将sql中需要替换的参数赋值
//            pstat.setInt(1, sid);
//            // 5.执行操作
//            ResultSet rs = pstat.executeQuery();
//            if (rs.next()){
//                //将结果集的元素拆出来，存进一个对象
//                student = new Student();
//                student.setSid(rs.getInt("sid"));
//                student.setSname(rs.getString("sname"));
//                student.setSsex(rs.getString("ssex"));
//                student.setSage(rs.getInt("sage"));
//                student.setClassid(rs.getInt("classid"));
//            }
//            // 6.关闭
//            pstat.close();
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return student;
//    }
//
//    //查询多条
//    public List<Student> select(){
//        List<Student> studentList = new ArrayList<>();
//        String sql = "select * from student";
//        try {
//            // 传统的流程
//            // 1.导包
//            // 2.加载驱动
//            // 3.获取连接
//            Connection conn = ConnectionPool.getInstance().getConnection();
//            // 4.创建状态参数
//            PreparedStatement pstat = conn.prepareStatement(sql);
//            // 5.执行操作
//            ResultSet rs = pstat.executeQuery();
//            while (rs.next()){
//                //将结果集的元素拆出来，存进一个对象
//                Student student = new Student();
//                student.setSid(rs.getInt("sid"));
//                student.setSname(rs.getString("sname"));
//                student.setSsex(rs.getString("ssex"));
//                student.setSage(rs.getInt("sage"));
//                student.setClassid(rs.getInt("classid"));
//                studentList.add(student);
//            }
//            // 6.关闭
//            pstat.close();
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return studentList;
//    }
}
