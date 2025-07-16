import java.util.*;

// methods also need separate class
// need information on method return type
// also need information on parameter names and types of methods

public class Class_elems{
    public String name;
    public String extend_class;
    public HashMap<String, String> vars = new HashMap();
    public HashMap<String, Method_elems> methods = new HashMap();
    public ArrayList<String> subtypes = new ArrayList();

    public Class_elems(String n){
        name = n;
        subtypes.add(n);
        extend_class = null;
    }
}