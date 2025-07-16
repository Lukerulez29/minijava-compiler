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


public class J2S{
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
                ArrayList<String> spar_vars_set = classes.get(key).spar_vars;
                ArrayList<String> spar_methods_set = classes.get(key).spar_methods;
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
                    spar_vars_set.addAll(0, classes.get(parent).spar_vars);
                    spar_methods_set.addAll(0, classes.get(parent).spar_methods);
                    parent = classes.get(parent).extend_class;
                }
                // make method_set the new .methods of the class
                classes.get(key).methods = method_set;
                classes.get(key).subtypes = class_set;
                classes.get(key).spar_vars = spar_vars_set;
                classes.get(key).spar_methods = spar_methods_set;
                // System.out.println(key);
                // System.out.println(spar_vars_set);
            }

            HashMap<String, HashMap<String, Integer>> spar_methods = new HashMap();
            Iterator<String> it1 = keySet.iterator();
            while(it1.hasNext()){
                String key = it1.next();
                HashMap<String, Method_elems> cur_method = classes.get(key).methods;
                Set<String> method_keys = cur_method.keySet();
                Iterator<String> method_it = method_keys.iterator();

                ArrayList<String> cur_spar_methods = classes.get(key).spar_methods;
                
                int i = 0;
                HashMap<String, Integer> method_offset = new HashMap();
                for(String m : cur_spar_methods){
                    if(!method_offset.containsKey(m)){
                        method_offset.put(m, i);
                        i += 4;
                    }
                }

                // int i = 0;
                // HashMap<String, Integer> method_offset = new HashMap();
                // while(method_it.hasNext()){
                //     String method_key = method_it.next();
                //     if(!method_offset.containsKey(cur_method.get(method_key).name)){
                //         method_offset.put(cur_method.get(method_key).name, i);
                //         i += 4;
                //     }
                // }

                spar_methods.put(classes.get(key).name, method_offset);
            }
            

            TypeVisit second_pass = new TypeVisit(classes);
            second_pass.spar_methods = spar_methods;
            second_pass.visit(root, null);
            
            ArrayList<String> output = second_pass.funcs;

            for(String i : output){
                System.out.println(i);
            }

            // System.out.println("<EOF>");
        }
        catch (ParseException | IOException e){
            System.out.println("Type error");
        }

        
    }
}