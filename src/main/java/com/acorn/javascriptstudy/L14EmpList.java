package com.acorn.javascriptstudy;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// 톰캣에서 동적페이지(서블릿)를 생성하기 위한 2가지 약속
// 서블릿으로 정의할 타입을 HttpServlet으로 정의
// 서블릿을 리소스로 등록하기 위해 DD(web.xml)에 주소를 등록 (@WebServlet)
@WebServlet("/L14_emp_list.do")
public class L14EmpList extends HttpServlet {
    // 클라이언트에서 리소스를 요청하는 2가지 방식 (Get : url, POST : 양식제출)
    // doGet 클라이언트(브라우저)가 L14_emp_list.do를 url 호출하면 톰캣이 해당 리소스의 doGet()함수를 실행
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // java.sql.* : JDBC java 제공하는 db 접속 객체들
        // JDBC가 mysql에 접속하기 위해서는 mysql-j-conncector.jar가 필요하다.
        String user = "root";
        String pw = "mysql123";
//        String driver = "com.mysql.cj.jdbc.Driver";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String empListStr = "";
        String empListStr2 = "";
        List<EmpDto> empList = null;
        String sql="select ename from emp where deptno = 20";
        try{ // try catch 오류가 발생해도 시스템이 멈추지 않게 하는 예외처리
            Class.forName("com.mysql.cj.jdbc.Driver"); // 동적로딩의 대상
            // 동적로딩 : 특정 객체(DriverManager)가 작업을 수행할 때 해당 객체를 생성하는 행위 (제어의 역전)
            // 일반적인 객체지향 언어는 객체를 프로그래머가 직접 생성 후 매개변수로 전달

            //DriverManaer가 db에 접속할 때 주소를 보고 필요한 라이브러리를 찾아서 생성후 접속한다 (동적로딩)
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/scott", user,pw); //동적로딩의 대상
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery(); // 질의어를 실행하는 함수
            empList = new ArrayList<EmpDto>();
            while(rs.next()) { // table row : 레코드,튜플,객체
                /* 문자열로 db의 데이터를 받으면 제어할 방법이 없다. 그래서 dto를 정의해서 사용한다.
                 empListStr += rs.getInt("empno");
                empListStr += rs.getString("ename");
                empListStr += rs.getString("job");
                */
                EmpDto emp = new EmpDto();
                emp.setEmpno(rs.getInt("empno"));
                emp.setEname(rs.getString("ename"));
                emp.setJob(rs.getString("job"));
                emp.setSal(rs.getDouble("sal"));
                emp.setComm(rs.getDouble("comm"));
                emp.setMgr(rs.getInt("mgr"));
                emp.setDeptno(rs.getInt("deptno"));
                emp.setHiredate(rs.getDate("hireDate"));
                empList.add(emp);
            }
        }catch(Exception e) { // 부모타입의 예외를 작성할 수록 더 많은 예외 처리를 한다. (Exception은 모든 예외처리 가능)
            e.printStackTrace();
        }
        resp.setContentType("text/html;charset=UTF-8;");;
        PrintWriter out = resp.getWriter(); // resp.getWriter() : 리소스로 반환될 문자열 작성
        // 해당 문자열을 리소스로 반환하면 브라우저가 자동으로 문서의 형식을 html로 인지하는데 문서에 인코딩이 적용되지 않아 한글이 깨짐
        out.println("<h1>Scott.emp list 출력</h1>");
        out.println("<h2>문제1 : 부서번호를 파라미터(?deptno=20)로 보내면 쿼리로 부서번호에 해당하는 사원만 출력하세요.</h2>");
        out.println("<h2>문제2 : dept와 emp를 조인해서 부서이름과 상사이름을 출력하세요.</h2>");
        out.println("<h2><a href = './L14_dept_list.do'>문제3 : 부서 리스트를 출력하는 동적 페이지를 만드세요.</a></h2>");
        empListStr += "<table>";
        empListStr += "<tr><th>사번</th><th>이름</th><th>부서이름</th><th>상사이름</th></tr>";

        for(EmpDto emp : empList){
            empListStr += "<tr>";
            empListStr += "<td>" + emp.getEmpno() + "</td>";
            empListStr += "<td>" + emp.getEname() + "</td>";
            empListStr += "<td>" + emp.getDeptno() + "</td>";
            empListStr += "<td>Dept 조인하세요</td>";
            empListStr += "<td>Emp 조인하세요</td>";
            empListStr += "</tr>";
        }
        empListStr += "<table>";

        out.println(empListStr);

        // java ㅁ누서가 바뀌면 class로 컴파일하고 톰캣에 배포된 webapp로 바꿔야한다. (배포!)
    }
}
