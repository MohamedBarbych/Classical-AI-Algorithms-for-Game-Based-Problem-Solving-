package MenuGastro;
import java.util.TreeMap;
import java.util.Iterator;

public class ST<Key extends Comparable<Key>, Val> implements Iterable<Key> {
    private TreeMap<Key, Val> st;

    public ST() {
        st = new TreeMap<Key, Val>();
    }

    public void put(Key key, Val val) {
        if (val == null) st.remove(key);
        else             st.put(key, val);
    }

    public Val get(Key key)             { return st.get(key);            }
    public Val remove(Key key)          { return st.remove(key);         }
    public boolean contains(Key key)    { return st.containsKey(key);    }
    public int size()                   { return st.size();              }
    public Iterator<Key> iterator()     { return st.keySet().iterator(); }

    // Overridden toString method to display the symbol table contents
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Key key : st.keySet()) {
            sb.append(key).append(" = ").append(st.get(key)).append("\n");
        }
        return sb.toString();
    }
}
