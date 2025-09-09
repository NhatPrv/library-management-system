package common;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class BookTableModel extends AbstractTableModel {
    private final String[] cols = {"ID","Title","Author","Year"};
    private List<Book> data = new ArrayList<>();

    public void setData(List<Book> list){ this.data = list; fireTableDataChanged(); }
    public Book getAt(int row){ return (row>=0 && row<data.size()) ? data.get(row) : null; }

    @Override public int getRowCount(){ return data.size(); }
    @Override public int getColumnCount(){ return cols.length; }
    @Override public String getColumnName(int c){ return cols[c]; }
    @Override public Object getValueAt(int r,int c){
        Book b=data.get(r);
        return switch(c){
            case 0 -> b.getId();
            case 1 -> b.getTitle();
            case 2 -> b.getAuthor();
            case 3 -> b.getYear();
            default -> "";
        };
    }
}
