import java.io.InputStream;
import java.util.*;
import java.io.IOException;

import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.syntaxtree.*;

// class needs to store extended class name
// also need methods 
// also need variables declared at beginning
// can store these required elements for a class in a struct/class
// can them store a list of all found classes so they can be type checked


public class Typecheck{
    // public Stack<HashMap<String, String>> env= new Stack<>();
    public static void main(String[] args) throws IOException{
        try{
            InputStream in = System.in;
            new MiniJavaParser(in);
            Goal root = MiniJavaParser.Goal();
            GetTypes types = new GetTypes();
            types.visit(root, null);
            
            HashMap<String, Class_elems> classes = types.classes;

            Set<String> keySet = classes.keySet();
            Iterator<String> it = keySet.iterator();

            while(it.hasNext()){
                String key = it.next();
                ArrayList<String> class_set = new ArrayList();
                // System.out.println(key);
                class_set.add(key);
                HashMap<String, Method_elems> method_set = classes.get(key).methods;
                HashMap<String, String> vars_set = classes.get(key).vars;
                String parent = classes.get(key).extend_class;

                while(parent != null){
                    if(class_set.contains(parent) || !classes.containsKey(parent)){
                        throw new IOException("issue with extends");
                    }
                    class_set.add(parent);
                    //iterate through all methods and check if method names/parameters are same
                    Set<String> method_keys = classes.get(parent).methods.keySet();
                    Iterator<String> method_it = method_keys.iterator();

                    while(method_it.hasNext()){
                        //method_key is a value that is an individual method name of parent class
                        String method_key = method_it.next();
                        //wrong second part only gets list of method_elems
                        if(method_set.containsKey(method_key) && (!method_set.get(method_key).get_param_types().equals(classes.get(parent).methods.get(method_key).get_param_types()) || !method_set.get(method_key).return_type.equals(classes.get(parent).methods.get(method_key).return_type))){
                            throw new IOException("method overloading");
                        }
                        //add method to method_set
                        if(!method_set.containsKey(classes.get(parent).methods.get(method_key).name)){
                            method_set.put(classes.get(parent).methods.get(method_key).name, classes.get(parent).methods.get(method_key));
                        }
                    }
                    
                    //need to go through all class variables and add variables that are not of the same
                    //name as another field variable to the list of variables for the class
                    Set<String> vars_keys = classes.get(parent).vars.keySet();
                    Iterator<String> vars_it = vars_keys.iterator();
                    while(vars_it.hasNext()){
                        String vars_key = vars_it.next();
                        if(!vars_set.containsKey(vars_key)){
                            vars_set.put(vars_key, classes.get(parent).vars.get(vars_key));
                        }
                    }
                    parent = classes.get(parent).extend_class;
                }
                // make method_set the new .methods of the class
                classes.get(key).methods = method_set;
                classes.get(key).subtypes = class_set;
            }

            TypeVisit second_pass = new TypeVisit(classes);
            second_pass.visit(root);
            System.out.println("Program type checked successfully");

        }
        catch (ParseException | IOException e){
            System.out.println("Type error");
        }

        
    }
}