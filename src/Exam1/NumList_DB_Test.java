package Exam1;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

class NumList {     // 전화번호 데이터 클래스
    private String name;        // 이름
    private String number;      // 전화번호
    private String address;     // 주소

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

class Sql {     // db 연동 및 작업 클래스
    private static Connection ct;
    private static PreparedStatement pt;

    Sql() throws SQLException {
        ct = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbtest", "root", "1234");
    }

    void insert(NumList n) {        // 입력
        try {
            pt = ct.prepareStatement("insert into phone values(?,?,?);");
            pt.setString(1, n.getName());
            pt.setString(2, n.getNumber());
            pt.setString(3, n.getAddress());
            pt.executeUpdate();
            System.out.println("추가 완료");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void find(String name) throws SQLException {        // 찾기
        String sql = "select * from phone where name = '" + name + "';";
        pt = ct.prepareStatement(sql);
        ResultSet rs = pt.executeQuery();
        if (rs.next()) {
            String name1 = rs.getString("name");
            String number = rs.getString("phoneNumber");
            String address = rs.getString("address");
            System.out.println("이름 : " + name1 + ", 전화번호 : " + number + ", 주소 : " + address);
            System.out.println();
        } else {
            System.out.println("찾는 목록이 없습니다.");
        }

    }

    void delete(String name) throws SQLException {      // 삭제
        String sql = "delete from phone where name = '" + name + "';";
        pt = ct.prepareStatement(sql);
        if (pt.executeUpdate() == 1) {
            System.out.println("삭제완료");
        } else {
            System.out.println("삭제할 목록이 없습니다.");
        }
    }

    void print() throws SQLException {      // 전체 출력
        String sql = "select * from phone;";
        pt = ct.prepareStatement(sql);
        ResultSet rs = pt.executeQuery();
        while (rs.next()) {
            String name1 = rs.getString("name");
            String number = rs.getString("phoneNumber");
            String address = rs.getString("address");
            System.out.println("이름 : " + name1 + ", 전화번호 : " + number + ", 주소 : " + address);
            System.out.println();
        }
    }

    ResultSet overlapcheck(String name) throws SQLException {       // 입력시 중복체크 잡기위한 클래스
        String sql = "select * from phone where name = '" + name + "';";
        pt = ct.prepareStatement(sql);
        return pt.executeQuery();
    }
}


class Input {
    Scanner sc = new Scanner(System.in);

    int first() {       // 첫 시작
        System.out.println("==전화번호부==");
        int first = 0;
        while (first < 1 || first > 5) {
            try {
                System.out.println("1)입력 2)검색 3)삭제 4)출력 5)종료");
                System.out.print("입력 = ");
                first = sc.nextInt();
            }
            catch (InputMismatchException e) {
                System.out.println("숫자 입력 해주세요.");
                sc.next();
            }
        }

        return first;
    }

    NumList insert_data(Sql s) throws SQLException {        // 데이터 입력 클래스
        NumList n = new NumList();
        System.out.println("==추가==");
        System.out.print("이름(8자리까지!) = ");
        n.setName(sc.next());
        while (n.getName().length() > 8 || n.getName().isEmpty()        // 이름 양식 및 중복체크 확인
                || s.overlapcheck(n.getName()).next()) {
            if (n.getName().length() > 8 || n.getName().isEmpty()) {
                System.out.println("이름 양식이 잘못되었습니다.");
            }
            else {
                System.out.println("이름이 중복되었습니다.");
            }
            System.out.print("이름(8자리까지!) = ");
            n.setName(sc.next());
        }

        System.out.print("전화번호(45자리 까지) = ");
        n.setNumber(sc.next());
        while (n.getNumber().length() > 45 || n.getNumber().isEmpty()) {        // 전화번호 양식 확인
            System.out.println("전화번호 양식이 맞지 않습니다.");
            System.out.print("전화번호(45자리) = ");
            n.setNumber(sc.next());
        }
        sc.nextLine();
        System.out.print("주소(45자리까지) = ");
        n.setAddress(sc.nextLine());
        while (n.getAddress().length() > 45) {          // 주소 양식 확인
            System.out.println("주소입력이 잘못되었습니다.");
            System.out.print("주소(45자리까지) = ");
            n.setAddress(sc.nextLine());
        }

        return n;
    }

    String selname() {      // 삭제 및 검색에서 찾을 이름 메소드
        System.out.print("찾을실 이름을 입력해주세요. = ");
        return sc.next();
    }
}

public class NumList_DB_Test {
    public static void main(String[] args) throws SQLException {
        Input input = new Input();
        Sql sql = new Sql();


        while (true) {
            int first = input.first();
            if (first == 1) {
                sql.insert(input.insert_data(sql));
            } else if (first == 2) {
                sql.find(input.selname());
            } else if (first == 3) {
                sql.delete(input.selname());
            } else if (first == 4) {
                sql.print();
            } else {
                System.out.println("종료합니다.");
                break;
            }
        }
    }
}
