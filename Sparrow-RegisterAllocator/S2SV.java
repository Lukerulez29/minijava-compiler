import java.io.InputStream;
import java.util.*;

import IR.SparrowParser;
import IR.visitor.SparrowConstructor;
import IR.syntaxtree.*;
// import IR.visitor.*;

// import IR.syntaxtree.*;

public class S2SV {
    public static void main(String [] args) throws Exception {
        // InputStream in = System.in;
        // new SparrowParser(in);
        Program prog = new SparrowParser(System.in).Program();
        // System.err.println(program.toString());

        SparVisit first_pass = new SparVisit();
        first_pass.visit(prog);

        HashMap<String, HashMap<String, String>> alloc_table = new HashMap();
        ArrayList<String> registers = new ArrayList<>(Arrays.asList("s1","s2","s3","s4","s5","s6","s7","s8","s9","s10","s11","t0","t2","t3","t4","t5"));
        ArrayList<String> s_reg = new ArrayList<>(Arrays.asList("s1","s2","s3","s4","s5","s6","s7","s8","s9","s10","s11"));
        ArrayList<String> t_reg = new ArrayList<>(Arrays.asList("t0","t2","t3","t4","t5"));
        int mx = registers.size();

        for(String fun : first_pass.liveness.keySet()){
            // if(!first_pass.contains_call.contains(fun)){
            //     registers = new ArrayList<>(Arrays.asList("t0","t2","t3","t4","t5","s1","s2","s3","s4","s5","s6","s7","s8","s9","s10","s11"));
            // }
            Deque<String> reg_pool = new ArrayDeque<>(registers);
            ArrayList<Threeple> init = new ArrayList();
            ArrayList<Threeple> active = new ArrayList();
            HashMap<String, String> matching = new HashMap();

            for(String key : first_pass.liveness.get(fun).keySet()){
                Threeple cur = new Threeple(key, first_pass.liveness.get(fun).get(key).def, first_pass.liveness.get(fun).get(key).use);
                init.add(cur);
            }
            Collections.sort(init, Comparator.comparingInt(t -> t.def));

            for(int i = 0; i < init.size(); i++) {
                if(init.get(i).use == -1){
                    matching.put(init.get(i).var, "a2");
                    continue;
                }
                Collections.sort(active, Comparator.comparingInt(t -> t.use));
                for(int j = 0; j < active.size(); j++){
                    if(active.get(j).use < init.get(i).def){
                        String cur_reg = matching.get(active.get(j).var);
                        active.remove(j);
                        if(s_reg.contains(cur_reg)){
                            reg_pool.addFirst(cur_reg);
                        }
                        else if(t_reg.contains(cur_reg)){
                            reg_pool.addLast(cur_reg);
                        }
                    }
                }

                if(active.size() >= mx){
                    Threeple spill = active.get(active.size()-1);
                    if(spill.use > init.get(i).use){
                        matching.put(init.get(i).var, matching.get(spill.var));
                        matching.put(spill.var, spill.var);
                        active.remove(active.size()-1);
                        active.add(init.get(i));
                    }
                    else{
                        matching.put(init.get(i).var, init.get(i).var);
                    }
                }
                else{
                    boolean in_call = false;
                    // System.out.println(first_pass.calls.get(fun));
                    for(int l : first_pass.calls.get(fun)){
                        if(init.get(i).def < l && init.get(i).use > l){
                            in_call = true;
                            break;
                        }
                    }
                    if(!in_call){
                        matching.put(init.get(i).var, reg_pool.getLast());
                        reg_pool.removeLast();
                        active.add(init.get(i));
                    }
                    else{
                        matching.put(init.get(i).var, reg_pool.getFirst());
                        reg_pool.removeFirst();
                        active.add(init.get(i));
                    }
                }
            }
            // System.out.print(fun + "  ");
            // System.out.println(matching);
            alloc_table.put(fun, matching);
        }


        // for(String a: first_pass.liveness.get("List_Print").keySet()){
        //     Tuple cur = first_pass.liveness.get("List_Print").get(a);
        //     System.out.println(a + " -> " + cur.def + " " + cur.use);
        // }
        Translate second_pass = new Translate();
        second_pass.alloc_table = alloc_table;
        second_pass.liveness = first_pass.liveness;
        second_pass.visit(prog);
        // System.out.println(alloc_table);

        for(String i : second_pass.res){
            System.out.println(i);
        }


    }
}