import java.security.interfaces.RSAKey;
import java.util.*;

import IR.visitor.*;

import IR.syntaxtree.*;


public class Translate extends SparrowConstructor{
    int line = 0;
    String cur_func;
    HashMap<String, HashMap<String, String>> alloc_table = new HashMap();
    HashMap<String, HashMap<String, Tuple>> liveness = new HashMap();
    ArrayList<String> registers = new ArrayList<>(Arrays.asList("a2","a3","a4","a5","a6","a7","s1","s2","s3","s4","s5","s6","s7","s8","s9","s10","s11","t0","t1","t2","t3","t4","t5"));
    ArrayList<String> caller_saved = new ArrayList<>(Arrays.asList("a4","a5","a6","a7","t0","t2","t3","t4","t5"));
    ArrayList<String> callee_saved = new ArrayList<>(Arrays.asList("s1","s2","s3","s4","s5","s6","s7","s8","s9","s10","s11"));
    ArrayList<String> res = new ArrayList();
    // HashMap<String, HashMap<String, Integer>> gotos = new HashMap();

    /**
    * f0 -> ( FunctionDeclaration() )*
    * f1 -> <EOF>
    */
   @Override
   public void visit(IR.syntaxtree.Program n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> "func"
    * f1 -> FunctionName()
    * f2 -> "("
    * f3 -> ( Identifier() )*
    * f4 -> ")"
    * f5 -> Block()
    */
   @Override
   public void visit(FunctionDeclaration n) {
      cur_func = n.f1.f0.toString();
      String tmp = "func " + cur_func + "(";
      for(int i = 6; i < n.f3.size(); i++){
        tmp = tmp + ((IR.syntaxtree.Identifier)n.f3.elementAt(i)).f0.toString() + " ";
      }
      tmp = tmp + ")";
      res.add(tmp);
      
      if(!cur_func.equals("Main") && !cur_func.equals("main")){
        Collection<String> values = alloc_table.get(cur_func).values();
        for(String reg : callee_saved){
          if(values.contains(reg)){
            res.add("save_" + reg + " = " + reg);
          }
        }

        for(int i = 0; i < Math.min(6,n.f3.size()); i++){
          int val = i + 2;
          res.add(alloc_table.get(cur_func).get(((IR.syntaxtree.Identifier)n.f3.elementAt(i)).f0.toString()) + " = a" + val);
        }
        for(int i = 6; i < n.f3.size(); i++){
          if(registers.contains(alloc_table.get(cur_func).get(((IR.syntaxtree.Identifier)n.f3.elementAt(i)).f0.toString()))){
            res.add(alloc_table.get(cur_func).get(((IR.syntaxtree.Identifier)n.f3.elementAt(i)).f0.toString()) + " = " + ((IR.syntaxtree.Identifier)n.f3.elementAt(i)).f0.toString());
          }
        }
      }
      
      line = line + 1;

      n.f5.accept(this);
      line = 0;
   }

   /**
    * f0 -> ( Instruction() )*
    * f1 -> "return"
    * f2 -> Identifier()
    */
   @Override
   public void visit(IR.syntaxtree.Block n) {
      n.f0.accept(this);

      if(!cur_func.equals("Main") && !cur_func.equals("main")){
        if(registers.contains(alloc_table.get(cur_func).get(n.f2.f0.toString()))){
          res.add(n.f2.f0.toString() + " = " + alloc_table.get(cur_func).get(n.f2.f0.toString()));
        }
        Collection<String> values = alloc_table.get(cur_func).values();
        for(String reg : callee_saved){
          if(values.contains(reg)){
            res.add(reg + " = save_" + reg);
          }
        }
      }

      res.add("return " + n.f2.f0.toString());
      line = line + 1;
   }

    /**
    * f0 -> Label()
    * f1 -> ":"
    */
   @Override
   public void visit(LabelWithColon n) {
      res.add(n.f0.f0.toString() + ":");
      line = line + 1;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> IntegerLiteral()
    */
   @Override
   public void visit(SetInteger n) {
      if(registers.contains(alloc_table.get(cur_func).get(n.f0.f0.toString()))){
        res.add(alloc_table.get(cur_func).get(n.f0.f0.toString()) + " = " + n.f2.f0.toString());
      }
      else{
        res.add("a2 = " + n.f2.f0.toString());
        res.add(n.f0.f0.toString() + " = a2");
      }
      line = line + 1;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> "@"
    * f3 -> FunctionName()
    */
   @Override
   public void visit(SetFuncName n) {
      if(registers.contains(alloc_table.get(cur_func).get(n.f0.f0.toString()))){
        res.add(alloc_table.get(cur_func).get(n.f0.f0.toString()) + " = @" + n.f3.f0.toString());
      }
      else{
        res.add("a2 = @" + n.f3.f0.toString());
        res.add(n.f0.f0.toString() + " = a2");
      }
      line = line + 1;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Identifier()
    * f3 -> "+"
    * f4 -> Identifier()
    */
   @Override
   public void visit(Add n) {
      String LHS = alloc_table.get(cur_func).get(n.f0.f0.toString());
      String arg1 = alloc_table.get(cur_func).get(n.f2.f0.toString());
      String arg2 = alloc_table.get(cur_func).get(n.f4.f0.toString());
      if(registers.contains(LHS)){
        if(registers.contains(arg1) && registers.contains(arg2)){
          res.add(LHS + " = " + arg1 + " + " + arg2);
        }
        else if(registers.contains(arg1)){
          res.add("a2 = " + arg2);
          res.add(LHS + " = " + arg1 + " + a2");
        }
        else if(registers.contains(arg2)){
          res.add("a2 = " + arg1);
          res.add(LHS + " = a2 + " + arg2);
        }
        else{
          res.add("a2 = " + arg1);
          res.add("a3 = " + arg2);
          res.add(LHS + " = a2 + a3");
        }
      }
      else{
        if(registers.contains(arg1) && registers.contains(arg2)){
          res.add("a2 = " + arg1 + " + " + arg2);
          res.add(LHS + " = a2");
        }
        else if(registers.contains(arg1)){
          res.add("a2 = " + arg2);
          res.add("a2 = " + arg1 + " + a2");
          res.add(LHS + " = a2");
        }
        else if(registers.contains(arg2)){
          res.add("a2 = " + arg1);
          res.add("a2 = a2 + " + arg2);
          res.add(LHS + " = a2");
        }
        else{
          res.add("a2 = " + arg1);
          res.add("a3 = " + arg2);
          res.add("a2 = a2 + a3");
          res.add(LHS + " = a2");
        }
      }
      line = line + 1;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Identifier()
    * f3 -> "-"
    * f4 -> Identifier()
    */
   @Override
   public void visit(Subtract n) {
      String LHS = alloc_table.get(cur_func).get(n.f0.f0.toString());
      String arg1 = alloc_table.get(cur_func).get(n.f2.f0.toString());
      String arg2 = alloc_table.get(cur_func).get(n.f4.f0.toString());
      if(registers.contains(LHS)){
        if(registers.contains(arg1) && registers.contains(arg2)){
          res.add(LHS + " = " + arg1 + " - " + arg2);
        }
        else if(registers.contains(arg1)){
          res.add("a2 = " + arg2);
          res.add(LHS + " = " + arg1 + " - a2");
        }
        else if(registers.contains(arg2)){
          res.add("a2 = " + arg1);
          res.add(LHS + " = a2 - " + arg2);
        }
        else{
          res.add("a2 = " + arg1);
          res.add("a3 = " + arg2);
          res.add(LHS + " = a2 - a3");
        }
      }
      else{
        if(registers.contains(arg1) && registers.contains(arg2)){
          res.add("a2 = " + arg1 + " - " + arg2);
          res.add(LHS + " = a2");
        }
        else if(registers.contains(arg1)){
          res.add("a2 = " + arg2);
          res.add("a2 = " + arg1 + " - a2");
          res.add(LHS + " = a2");
        }
        else if(registers.contains(arg2)){
          res.add("a2 = " + arg1);
          res.add("a2 = a2 - " + arg2);
          res.add(LHS + " = a2");
        }
        else{
          res.add("a2 = " + arg1);
          res.add("a3 = " + arg2);
          res.add("a2 = a2 - a3");
          res.add(LHS + " = a2");
        }
      }
      line = line + 1;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Identifier()
    * f3 -> "*"
    * f4 -> Identifier()
    */
   @Override
   public void visit(Multiply n) {
      String LHS = alloc_table.get(cur_func).get(n.f0.f0.toString());
      String arg1 = alloc_table.get(cur_func).get(n.f2.f0.toString());
      String arg2 = alloc_table.get(cur_func).get(n.f4.f0.toString());
      if(registers.contains(LHS)){
        if(registers.contains(arg1) && registers.contains(arg2)){
          res.add(LHS + " = " + arg1 + " * " + arg2);
        }
        else if(registers.contains(arg1)){
          res.add("a2 = " + arg2);
          res.add(LHS + " = " + arg1 + " * a2");
        }
        else if(registers.contains(arg2)){
          res.add("a2 = " + arg1);
          res.add(LHS + " = " + arg2 + " * a2");
        }
        else{
          res.add("a2 = " + arg1);
          res.add("a3 = " + arg2);
          res.add(LHS + " = a2 * a3");
        }
      }
      else{
        if(registers.contains(arg1) && registers.contains(arg2)){
          res.add("a2 = " + arg1 + " * " + arg2);
          res.add(LHS + " = a2");
        }
        else if(registers.contains(arg1)){
          res.add("a2 = " + arg2);
          res.add("a2 = " + arg1 + " * a2");
          res.add(LHS + " = a2");
        }
        else if(registers.contains(arg2)){
          res.add("a2 = " + arg1);
          res.add("a2 = " + arg2 + " * a2");
          res.add(LHS + " = a2");
        }
        else{
          res.add("a2 = " + arg1);
          res.add("a3 = " + arg2);
          res.add("a2 = a2 * a3");
          res.add(LHS + " = a2");
        }
      }
      line = line + 1;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Identifier()
    * f3 -> "<"
    * f4 -> Identifier()
    */
   @Override
   public void visit(LessThan n) {
      String LHS = alloc_table.get(cur_func).get(n.f0.f0.toString());
      String arg1 = alloc_table.get(cur_func).get(n.f2.f0.toString());
      String arg2 = alloc_table.get(cur_func).get(n.f4.f0.toString());
      if(registers.contains(LHS)){
        if(registers.contains(arg1) && registers.contains(arg2)){
          res.add(LHS + " = " + arg1 + " < " + arg2);
        }
        else if(registers.contains(arg1)){
          res.add("a2 = " + arg2);
          res.add(LHS + " = " + arg1 + " < a2");
        }
        else if(registers.contains(arg2)){
          res.add("a2 = " + arg1);
          res.add(LHS + " = a2 < " + arg2);
        }
        else{
          res.add("a2 = " + arg1);
          res.add("a3 = " + arg2);
          res.add(LHS + " = a2 < a3");
        }
      }
      else{
        if(registers.contains(arg1) && registers.contains(arg2)){
          res.add("a2 = " + arg1 + " < " + arg2);
          res.add(LHS + " = a2");
        }
        else if(registers.contains(arg1)){
          res.add("a2 = " + arg2);
          res.add("a2 = " + arg1 + " < a2");
          res.add(LHS + " = a2");
        }
        else if(registers.contains(arg2)){
          res.add("a2 = " + arg1);
          res.add("a2 = a2 < " + arg2);
          res.add(LHS + " = a2");
        }
        else{
          res.add("a2 = " + arg1);
          res.add("a3 = " + arg2);
          res.add("a2 = a2 < a3");
          res.add(LHS + " = a2");
        }
      }
      line = line + 1;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> "["
    * f3 -> Identifier()
    * f4 -> "+"
    * f5 -> IntegerLiteral()
    * f6 -> "]"
    */
   @Override
   public void visit(Load n) {
      String LHS = alloc_table.get(cur_func).get(n.f0.f0.toString());
      String base = alloc_table.get(cur_func).get(n.f3.f0.toString());
      if(registers.contains(LHS)){
        if(registers.contains(base)){
          res.add(LHS + " = [" + base + " + " + n.f5.f0.toString() + "]");
        }
        else{
          res.add("a2 = " + base);
          res.add(LHS + " = [a2 + " + n.f5.f0.toString() + "]");
        }
      }
      else{
        if(registers.contains(base)){
          res.add("a2 = [" + base + " + " + n.f5.f0.toString() + "]");
          res.add(LHS + " = a2");
        }
        else{
          res.add("a2 = " + base);
          res.add("a2 = [a2 + " + n.f5.f0.toString() + "]");
          res.add(LHS + " = a2");
        }
      }
      line = line + 1;
   }

   /**
    * f0 -> "["
    * f1 -> Identifier()
    * f2 -> "+"
    * f3 -> IntegerLiteral()
    * f4 -> "]"
    * f5 -> "="
    * f6 -> Identifier()
    */
   @Override
   public void visit(Store n) {
      String base = alloc_table.get(cur_func).get(n.f1.f0.toString());
      String RHS = alloc_table.get(cur_func).get(n.f6.f0.toString());
      if(registers.contains(base)){
        if(registers.contains(RHS)){
          res.add("[" + base + " + " + n.f3.f0.toString() + "] = " + RHS);
        }
        else{
          res.add("a2 = " + RHS);
          res.add("[" + base + " + " + n.f3.f0.toString() + "] = a2");
        }
      }
      else{
        if(registers.contains(RHS)){
          res.add("a2 = " + base);
          res.add("[a2 + " + n.f3.f0.toString() + "] = " + RHS);
        }
        else{
          res.add("a2 = " + base);
          res.add("a3 = " + RHS);
          res.add("[a2 + " + n.f3.f0.toString() + "] = a3");
        }
      }
      line = line + 1;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Identifier()
    */
   @Override
   public void visit(Move n) {
      String LHS = alloc_table.get(cur_func).get(n.f0.f0.toString());
      String RHS = alloc_table.get(cur_func).get(n.f2.f0.toString());
      if(!registers.contains(LHS) && !registers.contains(RHS)){
        res.add("a2 = " + RHS);
        res.add(LHS + " = a2");
      }
      else{
        res.add(LHS + " = " + RHS);
      }
      line = line + 1;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> "alloc"
    * f3 -> "("
    * f4 -> Identifier()
    * f5 -> ")"
    */
   @Override
   public void visit(Alloc n) {
      String LHS = alloc_table.get(cur_func).get(n.f0.f0.toString());
      String sz = alloc_table.get(cur_func).get(n.f4.f0.toString());
      if(registers.contains(LHS)){
        if(registers.contains(sz)){
          res.add(LHS + " = alloc(" + sz + ")");
        }
        else{
          res.add("a2 = " + sz);
          res.add(LHS + " = alloc(a2)");
        }
      }
      else{
        if(registers.contains(sz)){
          res.add("a2 = " + LHS);
          res.add("a2 = alloc(" + sz + ")");
          res.add(LHS + " = a2");
        }
        else{
          res.add("a2 = " + sz);
          res.add("a2 = alloc(a2)");
          res.add(LHS + " = a2");
        }
      }
      line = line + 1;
   }

   /**
    * f0 -> "print"
    * f1 -> "("
    * f2 -> Identifier()
    * f3 -> ")"
    */
   @Override
   public void visit(Print n) {
      if(registers.contains(alloc_table.get(cur_func).get(n.f2.f0.toString()))){
        res.add("print(" + alloc_table.get(cur_func).get(n.f2.f0.toString()) + ")");
      }
      else{
        res.add("a2 = " + n.f2.f0.toString());
        res.add("print(a2)");
      }
      line = line + 1;
   }

   /**
    * f0 -> "goto"
    * f1 -> Label()
    */
   @Override
   public void visit(Goto n) {
      res.add("goto " + n.f1.f0.toString());
      line = line + 1;
   }

   /**
    * f0 -> "if0"
    * f1 -> Identifier()
    * f2 -> "goto"
    * f3 -> Label()
    */
   @Override
   public void visit(IfGoto n) {
      if(registers.contains(alloc_table.get(cur_func).get(n.f1.f0.toString()))){
        res.add("if0 " + alloc_table.get(cur_func).get(n.f1.f0.toString()) + " goto " + n.f3.f0.toString());
      }
      else{
        res.add("a2 = " + n.f1.f0.toString());
        res.add("if0 a2 goto " + n.f3.f0.toString());
      }
      line = line + 1;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> "call"
    * f3 -> Identifier()
    * f4 -> "("
    * f5 -> ( Identifier() )*
    * f6 -> ")"
    */
   @Override
   public void visit(Call n) {
      String LHS = alloc_table.get(cur_func).get(n.f0.f0.toString());
      String fun = alloc_table.get(cur_func).get(n.f3.f0.toString());
      String tmp;
      ArrayList<String> available = new ArrayList();
      if(registers.contains(LHS)){
        res.add("t1 = " + fun);
        tmp = "t1 = call t1(";

        for(int i = 6; i < n.f5.size(); i++){
          String arg = ((IR.syntaxtree.Identifier)n.f5.elementAt(i)).f0.toString();
          if(registers.contains(alloc_table.get(cur_func).get(arg))){
            res.add(arg + " = " + alloc_table.get(cur_func).get(arg));
          }
          tmp = tmp + arg + " ";
        }
        tmp = tmp + ")";

        // Collection<String> values = alloc_table.get(cur_func).values();
        // for(String reg : caller_saved){
        //   if(values.contains(reg)){
        //     res.add("save_" + reg + " = " + reg);
        //   }
        // }

        for(String save : alloc_table.get(cur_func).keySet()){
          if(caller_saved.contains(alloc_table.get(cur_func).get(save)) && !alloc_table.get(cur_func).get(save).equals(LHS) && liveness.get(cur_func).get(save).use > line && !available.contains(alloc_table.get(cur_func).get(save))){
            res.add("save_" + alloc_table.get(cur_func).get(save) + " = " + alloc_table.get(cur_func).get(save));
            available.add(alloc_table.get(cur_func).get(save));
          }
        }

        for(int i = 0; i < Math.min(6, n.f5.size()); i++){
          String arg = ((IR.syntaxtree.Identifier)n.f5.elementAt(i)).f0.toString();
          int val = i + 2;
          res.add("a" + val + " = " + alloc_table.get(cur_func).get(arg));
        }

        res.add(tmp);
        res.add(LHS + " = t1");
      }
      else{
        res.add("t1 = " + fun);
        tmp = "t1 = call t1(";

        for(int i = 6; i < n.f5.size(); i++){
          String arg = ((IR.syntaxtree.Identifier)n.f5.elementAt(i)).f0.toString();
          if(registers.contains(alloc_table.get(cur_func).get(arg))){
            res.add(arg + " = " + alloc_table.get(cur_func).get(arg));
          }
          tmp = tmp + arg + " ";
        }
        tmp = tmp + ")";

        for(String save : alloc_table.get(cur_func).keySet()){
          if(caller_saved.contains(alloc_table.get(cur_func).get(save)) && !alloc_table.get(cur_func).get(save).equals(LHS) && liveness.get(cur_func).get(save).use > line && !available.contains(alloc_table.get(cur_func).get(save))){
            res.add("save_" + alloc_table.get(cur_func).get(save) + " = " + alloc_table.get(cur_func).get(save));
            available.add(alloc_table.get(cur_func).get(save));
          }
        }

        // Collection<String> values = alloc_table.get(cur_func).values();
        // for(String reg : caller_saved){
        //   if(values.contains(reg)){
        //     res.add("save_" + reg + " = " + reg);
        //   }
        // }

        for(int i = 0; i < Math.min(6, n.f5.size()); i++){
          String arg = ((IR.syntaxtree.Identifier)n.f5.elementAt(i)).f0.toString();
          int val = i + 2;
          res.add("a" + val + " = " + alloc_table.get(cur_func).get(arg));
        }

        res.add(tmp);
        res.add(LHS + " = t1");
      }
      
      available = new ArrayList();
      for(String save : alloc_table.get(cur_func).keySet()){
        if(caller_saved.contains(alloc_table.get(cur_func).get(save)) && !alloc_table.get(cur_func).get(save).equals(LHS) && liveness.get(cur_func).get(save).use > line && !available.contains(alloc_table.get(cur_func).get(save))){
          res.add(alloc_table.get(cur_func).get(save) + " = save_" + alloc_table.get(cur_func).get(save));
          available.add(alloc_table.get(cur_func).get(save));
        }
      }
      // Collection<String> values = alloc_table.get(cur_func).values();
      // for(String reg : caller_saved){
      //   if(values.contains(reg) && liveness.get(cur_func).get(save).use > line){
      //     res.add("save_" + reg + " = " + reg);
      //   }
      // }
      line = line + 1;
   }

   /**
    * f0 -> "error"
    * f1 -> "("
    * f2 -> StringLiteral()
    * f3 -> ")"
    */
   @Override
   public void visit(ErrorMessage n){
      res.add("error(" + n.f2.f0.toString() + ")");
      line = line + 1;
   }
}