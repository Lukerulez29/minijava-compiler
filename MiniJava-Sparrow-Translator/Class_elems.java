import java.util.*;

// methods also need separate class
// need information on method return type
// also need information on parameter names and types of methods
// for methods can just do the classname_method to determine what to call because methods overriden

// calling a field from a method in a super class - just use base mapping because fields overwritten anyways
// can maybe do A-f1, A-f2, B-f1, B-f3
// can assign [this + offset] for fields in second pass
// first pass should just get all fields (and those that are inherited) and put them into the class object
// so that when an object of the class is declared, the mappings to offsets can be done in second pass
// need a way to distinguish A-f1 from B-f1 if we just use an arraylist
// make sure to put superclass fields before subclass and keep same ordering when mapping to offsets
// only variables in vars actually need to be mapped again because everything else would have been
// mapped previously from superclass (methods and stuff would already be created with proper offsets)
// if you are the last variable of your name to appear, then you get remapped

public class Class_elems{
    public String name;
    public String extend_class;
    public HashMap<String, String> vars = new HashMap();
    public HashMap<String, Method_elems> methods = new HashMap();
    public ArrayList<String> subtypes = new ArrayList();

    public ArrayList<String> spar_vars = new ArrayList();
    public ArrayList<String> spar_methods = new ArrayList();

    public Class_elems(String n){
        name = n;
        subtypes.add(n);
        extend_class = null;
    }
}