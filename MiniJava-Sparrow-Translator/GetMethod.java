import java.util.*;

import minijava.syntaxtree.*;
import minijava.visitor.GJVoidDepthFirst;


public class GetMethod extends GJVoidDepthFirst<Method_elems>{
    /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   @Override
   public void visit(FormalParameter n, Method_elems argu) {
      int type = n.f0.f0.which;
      String res;
      if(type == 0){
         res = "int[]";
      }
      else if(type == 1){
         res = "bool";
      }
      else if(type == 2){
         res = "int";
      }
      else{
         res = ((Identifier)n.f0.f0.choice).f0.toString();
      }
      

      if(!(argu.contains(n.f1.f0.toString()))){
        Tuple cur = new Tuple(n.f1.f0.toString(), res);
        argu.param_vars.add(cur);
        argu.all_vars.put(n.f1.f0.toString(), res);
      }
      else{
        err("param already defined");
      }
   }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   @Override
   public void visit(FormalParameterRest n, Method_elems argu) {
      n.f1.accept(this, argu);
   }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   @Override
   public void visit(VarDeclaration n, Method_elems argu) {
      if(argu.contains(n.f1.f0.toString()) || argu.declared_vars.contains(n.f1.f0.toString())){
        err("variable already exists in params or declared vars");
      }

      int type = n.f0.f0.which;
      String res;
      if(type == 0){
         res = "int[]";
      }
      else if(type == 1){
         res = "bool";
      }
      else if(type == 2){
         res = "int";
      }
      else{
         res = ((Identifier)n.f0.f0.choice).f0.toString();
      }

      argu.declared_vars.add(n.f1.f0.toString());
      argu.all_vars.put(n.f1.f0.toString(), res);
   }

   /**
    * f0 -> FormalParameter()
    * f1 -> ( FormalParameterRest() )*
    */
   @Override
   public void visit(FormalParameterList n, Method_elems argu) {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
   }

   public void err(String msg) {
      System.err.println(msg);
      System.out.println("Type error");
      System.exit(1);
   }
}