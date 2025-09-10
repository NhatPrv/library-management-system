package server;

import common.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private static final String URL  = "jdbc:mysql://localhost:3306/librarydb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh";
    private static final String USER = "root";
    private static final String PASS = ""; 

    public BookDAO() {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch (ClassNotFoundException ignored) {}
    }

    public List<Book> findAll(){
        String sql="SELECT id,title,author,year FROM books ORDER BY id";
        List<Book> list=new ArrayList<>();
        try(Connection c=DriverManager.getConnection(URL,USER,PASS);
            Statement st=c.createStatement();
            ResultSet rs=st.executeQuery(sql)){
            while(rs.next()){
                list.add(new Book(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4)));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return list;
    }

    public Book insert(Book b){
        String sql="INSERT INTO books(title,author,year) VALUES(?,?,?)";
        try(Connection c=DriverManager.getConnection(URL,USER,PASS);
            PreparedStatement ps=c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,b.getTitle());
            ps.setString(2,b.getAuthor());
            ps.setInt(3,b.getYear());
            ps.executeUpdate();
            try(ResultSet k=ps.getGeneratedKeys()){
                if(k.next()) b.setId(k.getInt(1));
            }
            return b;
        } catch(SQLException e){ e.printStackTrace(); return null; }
    }

    public boolean update(Book b){
        String sql="UPDATE books SET title=?, author=?, year=? WHERE id=?";
        try(Connection c=DriverManager.getConnection(URL,USER,PASS);
            PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,b.getTitle());
            ps.setString(2,b.getAuthor());
            ps.setInt(3,b.getYear());
            ps.setInt(4,b.getId());
            return ps.executeUpdate()>0;
        } catch(SQLException e){ e.printStackTrace(); return false; }
    }

    public boolean delete(int id){
        String sql="DELETE FROM books WHERE id=?";
        try(Connection c=DriverManager.getConnection(URL,USER,PASS);
            PreparedStatement ps=c.prepareStatement(sql)){
            ps.setInt(1,id);
            return ps.executeUpdate()>0;
        } catch(SQLException e){ e.printStackTrace(); return false; }
    }
}
