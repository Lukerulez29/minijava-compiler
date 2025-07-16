import java.util.*;


import minijava.syntaxtree.*;
import minijava.visitor.GJDepthFirst;

public class PrimaryVisit extends GJDepthFirst<String, ArrayList>{
    public String cur_class;
    HashMap<String, String> env = new HashMap();
    HashMap<String, String> class_vars = new HashMap();
    HashMap<String, Class_elems> all_classes = new HashMap();

    public PrimaryVisit(String cl, HashMap<String, String> e, HashMap<String, String> c, HashMap<String, Class_elems> ac){
        cur_class = cl;
        env = e;
        class_vars = c;
        all_classes = ac;
    }

    @Override
   public String visit(PrimaryExpression n, ArrayList argu) {
      return n.f0.accept(this, argu);
   }

    @Override
   public String visit(Identifier n, ArrayList argu) {
      String _ret=null;
      String id = n.f0.toString();
      if(env.containsKey(id)){
        _ret = env.get(id);
      }
      else if(class_vars.containsKey(id)){
        _ret = class_vars.get(id);
      }
      return _ret;
   }

   @Override
   public String visit(ThisExpression n, ArrayList argu) {
      return cur_class;
   }

   @Override
   public String visit(AllocationExpression n, ArrayList argu) {
      return n.f1.f0.toString();
   }

   @Override
   public String visit(BracketExpression n, ArrayList argu) {
      return n.f1.accept(this, argu);
   }

   @Override
   public String visit(MessageSend n, ArrayList argu) {
      String LHS = n.f0.accept(this, argu);
      return all_classes.get(LHS).methods.get(n.f2.f0.toString()).return_type;
   }

   /**
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | PrimaryExpression()
    */
   @Override
   public String visit(Expression n, ArrayList argu) {
      return n.f0.accept(this, argu);
   }

}