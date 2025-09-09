package common;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    public enum Type { INIT, LIST, ADD, UPDATE, DELETE, ERROR }

    private Type type;
    private Book book;          // dùng cho ADD/UPDATE/DELETE
    private List<Book> books;   // dùng cho INIT/LIST
    private String error;

    public static Message list(List<Book> books){
        Message m=new Message(); m.type=Type.LIST; m.books=books; return m;
    }
    public static Message init(List<Book> books){
        Message m=new Message(); m.type=Type.INIT; m.books=books; return m;
    }
    public static Message add(Book b){ Message m=new Message(); m.type=Type.ADD; m.book=b; return m; }
    public static Message update(Book b){ Message m=new Message(); m.type=Type.UPDATE; m.book=b; return m; }
    public static Message delete(Book b){ Message m=new Message(); m.type=Type.DELETE; m.book=b; return m; }
    public static Message error(String e){ Message m=new Message(); m.type=Type.ERROR; m.error=e; return m; }

    public Type getType(){ return type; }
    public Book getBook(){ return book; }
    public List<Book> getBooks(){ return books; }
    public String getError(){ return error; }
}
