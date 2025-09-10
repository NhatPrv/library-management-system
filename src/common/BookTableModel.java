package common;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class BookTableModel extends AbstractTableModel {
    private final String[] cols = {"ID","Title","Author","Year"};
    private final List<Book> data = new ArrayList<>();

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

    public Book getAt(int row){ return (row>=0 && row<data.size()) ? data.get(row) : null; }

    public void setAll(List<Book> list){
        data.clear();
        if(list!=null) data.addAll(list);
        fireTableDataChanged();
    }

    public void upsert(Book b){
        int idx=-1;
        for (int i=0;i<data.size();i++) if (data.get(i).getId()==b.getId()) { idx=i; break; }
        if (idx==-1) { data.add(b); fireTableRowsInserted(data.size()-1, data.size()-1); }
        else { data.set(idx,b); fireTableRowsUpdated(idx, idx); }
    }

    public void removeById(int id){
        int idx=-1;
        for (int i=0;i<data.size();i++) if (data.get(i).getId()==id) { idx=i; break; }
        if (idx!=-1) { data.remove(idx); fireTableRowsDeleted(idx, idx); }
    }
}
