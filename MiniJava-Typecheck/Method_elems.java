import java.util.*;

public class Method_elems{
    public String name;
    public String return_type;
    public ArrayList<Tuple> param_vars = new ArrayList();
    public ArrayList<String> declared_vars = new ArrayList();
    public HashMap<String, String> all_vars = new HashMap();

    public Method_elems(String n){
        name = n;
    }

    public boolean contains(String a){
        if(param_vars.isEmpty()){
            return false;
        }

        for(Tuple t: param_vars){
            if(t.x.equals(a)){
                return true;
            }
        }
        return false;
    }

    // public boolean contains(String a){
    //     if(param_vars.isEmpty()){
    //         return false;
    //     }

    //     for(Tuple t: param_vars){
    //         if(t.x.equals(a)){
    //             return false;
    //         }
    //     }
    //     return true;
    // }

    public ArrayList<String> get_param_types(){
        ArrayList<String> res = new ArrayList();
        for(Tuple t: param_vars){
            res.add(t.y);
        }
        return res;
    }
}