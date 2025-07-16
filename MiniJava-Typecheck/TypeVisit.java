import java.util.*;

import minijava.syntaxtree.*;
import minijava.visitor.GJNoArguDepthFirst;

import java.io.IOException;

import org.ietf.jgss.MessageProp;

public class TypeVisit extends GJNoArguDepthFirst<String>{
    //for var declarations need to check if the var is in the method parameters list
    //if it is in the list then that is an error
    //variables are only declared in classes, method parameters, and in methods
    //so keep track of all variables separately based on those declared in class, method params, methods
    //if we can not find variable declaration in method, move up to params, and then move up to class
    //every time we enter a new method, reset the global method variable and environment variable
    //every time we enter a new class reset everything
    HashMap<String, Class_elems> all_classes = new HashMap();
    HashMap<String, String> env = new HashMap();
    HashMap<String, String> class_vars = new HashMap();
    String cur_class;
    String cur_method;
    public Stack<Method_elems> cur_methods = new Stack();

    public TypeVisit(HashMap<String, Class_elems> c){
        all_classes = c;
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
   @Override
   public String visit(MainClass n) {
      String _ret=null;
      class_vars = all_classes.get(n.f1.f0.toString()).vars;
      cur_class = n.f1.f0.toString();
      n.f14.accept(this);
      n.f15.accept(this);
      class_vars.clear();
      return _ret;
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
   @Override
   public String visit(ClassDeclaration n) {
      String _ret=null;
      class_vars = all_classes.get(n.f1.f0.toString()).vars;
      cur_class = n.f1.f0.toString();
      n.f3.accept(this);
      n.f4.accept(this);
      class_vars.clear();
      return _ret;
   }

      /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
   @Override
   public String visit(ClassExtendsDeclaration n) {
      String _ret=null;
      class_vars = all_classes.get(n.f1.f0.toString()).vars;
      cur_class = n.f1.f0.toString();
      n.f5.accept(this);
      n.f6.accept(this);
      class_vars.clear();
      return _ret;
   }

      /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   @Override
   public String visit(VarDeclaration n){
      String _ret=null;
      int type = n.f0.f0.which;
      String res;
      if(type == 3){
         res = ((Identifier)n.f0.f0.choice).f0.toString();
         if(!all_classes.containsKey(res)){
            err("type not found");
         }
      }
      return _ret;
   }

      /**
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
   @Override
   public String visit(MethodDeclaration n) {
      String _ret=null;
      int type = n.f1.f0.which;
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
         res = ((Identifier)n.f1.f0.choice).f0.toString();
         if(!all_classes.containsKey(res)){
            err("type not found");
         }
      }
      cur_method = n.f2.f0.toString();
      n.f4.accept(this);
      n.f7.accept(this);
      env = all_classes.get(cur_class).methods.get(n.f2.f0.toString()).all_vars;
    //   if(cur_class.equals("MyVisitor")){
    //     System.out.println(all_classes.get(cur_class).methods.get(n.f2.f0.toString()).name);
    //     for(Tuple a :all_classes.get(cur_class).methods.get(n.f2.f0.toString()).param_vars){
    //         System.out.println(a.x + a.y);
    //     }
    //   }
      n.f8.accept(this);
      String ret_type = n.f10.accept(this);
      //check ret_type is in res' supertypes
      if(!(ret_type.equals(res) || (all_classes.containsKey(ret_type) && all_classes.get(ret_type).subtypes.contains(res)))){
        err("return type does not match");
      }
      env.clear();
      return _ret;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   @Override
   public String visit(FormalParameter n) {
      String _ret=null;
      int type = n.f0.f0.which;
      String res;
      if(type == 3){
         res = ((Identifier)n.f0.f0.choice).f0.toString();
         if(!all_classes.containsKey(res)){
            err("type does not exist");
         }
      }
      return _ret;
   }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   @Override
   public String visit(FormalParameterRest n) {
      String _ret=null;
      n.f1.accept(this);
      return _ret;
   }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   @Override
   public String visit(ArrayType n) {
      return "int[]";
   }

   /**
    * f0 -> "boolean"
    */
   @Override
   public String visit(BooleanType n) {
      return "bool";
   }

   /**
    * f0 -> "int"
    */
   @Override
   public String visit(IntegerType n) {
      return "int";
   }
   
   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   @Override
   public String visit(AssignmentStatement n) {
      String _ret=null;
      String RHS = n.f2.accept(this);
      if(env.containsKey(n.f0.f0.toString())){
        // might need to check for existence of all_classes.get
        if(!(RHS.equals(env.get(n.f0.f0.toString()))) && !(all_classes.get(RHS) != null && all_classes.get(RHS).subtypes.contains(env.get(n.f0.f0.toString())))){
            err("LHS and RHS do not match");
        }
      }
      else if(class_vars.containsKey(n.f0.f0.toString())){
        if(!(RHS.equals(class_vars.get(n.f0.f0.toString()))) && !(all_classes.get(RHS) != null && all_classes.get(RHS).subtypes.contains(class_vars.get(n.f0.f0.toString())))){
            err("LHS and RHS do not match");
        }
      }
      else{
        err("no variable found");
      }
      return _ret;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
   @Override
   public String visit(ArrayAssignmentStatement n) {
      String _ret=null;
      if(env.containsKey(n.f0.f0.toString())){
        if(!"int[]".equals(env.get(n.f0.f0.toString())) || !"int".equals(n.f2.accept(this)) || !"int".equals(n.f5.accept(this))){
            err("int[] form wrong");
        }
      }
      else if(class_vars.containsKey(n.f0.f0.toString())){
        if(!"int[]".equals(class_vars.get(n.f0.f0.toString())) || !"int".equals(n.f2.accept(this)) || !"int".equals(n.f5.accept(this))){
            err("int[] form wrong");
        }
      }
      else{
        err("int[] var not found");
      }
      return _ret;
   }

   /**
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
   @Override
   public String visit(IfStatement n) {
      String _ret=null;
      if(!n.f2.accept(this).equals("bool")){
        err("if statement not boolean");
      }
      n.f4.accept(this);
      n.f6.accept(this);
      return _ret;
   }

   /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
   @Override
   public String visit(WhileStatement n) {
      String _ret=null;
      if(!n.f2.accept(this).equals("bool")){
        err("while statement not boolean");
      }
      n.f4.accept(this);
      return _ret;
   }

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
   @Override
   public String visit(PrintStatement n) {
      if(!n.f2.accept(this).equals("int")){
        err("print non int");
      }
      return null;
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
   public String visit(Expression n) {
      return n.f0.accept(this);
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    */
   @Override
   public String visit(AndExpression n) {
      if(!n.f0.accept(this).equals("bool") || !n.f2.accept(this).equals("bool")){
        err("&& not comparing bools");
      }
      return "bool";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   @Override
   public String visit(CompareExpression n) {
      if(!n.f0.accept(this).equals("int") || !n.f2.accept(this).equals("int")){
        err("< not comparing ints");
      }
      return "bool";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   @Override
   public String visit(PlusExpression n) {
      if(!n.f0.accept(this).equals("int") || !n.f2.accept(this).equals("int")){
        err("adding two non ints");
      }
      return "int";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   @Override
   public String visit(MinusExpression n) {
      if(!n.f0.accept(this).equals("int") || !n.f2.accept(this).equals("int")){
        err("subtracting two non ints");
      }
      return "int";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   @Override
   public String visit(TimesExpression n) {
      if(!n.f0.accept(this).equals("int") || !n.f2.accept(this).equals("int")){
        err("multiplying two non ints");
      }
      return "int";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   @Override
   public String visit(ArrayLookup n) {
      if(!n.f0.accept(this).equals("int[]") || !n.f2.accept(this).equals("int")){
        err("int[] access wrong");
      }
      return "int";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   @Override
   public String visit(ArrayLength n) {
      if(!n.f0.accept(this).equals("int[]")){
        err(".length not on int[]");
      }
      return "int";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
   @Override
   public String visit(MessageSend n) {
      String LHS = n.f0.accept(this);
      if(all_classes.containsKey(LHS)){
        //need to check that expression list is the same as in the parameters for methods of the class type
        if(all_classes.get(LHS).methods.containsKey(n.f2.f0.toString())){
            cur_methods.push(all_classes.get(LHS).methods.get(n.f2.f0.toString()));
            n.f4.accept(this);
            cur_methods.pop();
        }
        else{
            err("method not declared");
        }
      }
      else{
        err("LHS not a class type");
      }
      return all_classes.get(LHS).methods.get(n.f2.f0.toString()).return_type;
   }

   /**
    * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
   @Override
   public String visit(ExpressionList n) {
      Method_elems cur_method = cur_methods.peek();
      if(n == null){
        if(cur_method.param_vars.isEmpty()){
            return null;
        }
        else{
            err("params do not match 1");
        }
      }
      else{
        String first_in = n.f0.accept(this);
        if(!(cur_method.param_vars.get(0).y.equals(first_in) || all_classes.get(first_in).subtypes.contains(cur_method.param_vars.get(0).y))){
            err("params do not match 2");
        }
        if(cur_method.param_vars.size() != n.f1.size() + 1){
            err("params do not match 3");
        }

        for(int i = 1; i < n.f1.size(); i++){
            if(!(cur_method.param_vars.get(i).y.equals(n.f1.elementAt(i-1).accept(this)) || all_classes.get(n.f1.elementAt(i-1).accept(this)).subtypes.contains(cur_method.param_vars.get(i).y))){
                err("params do not match 4");
            }
        }
      }
      return null;
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   @Override
   public String visit(ExpressionRest n) {
      return n.f1.accept(this);
   }

   /**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | NotExpression()
    *       | BracketExpression()
    */
   @Override
   public String visit(PrimaryExpression n) {
      return n.f0.accept(this);
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   @Override
   public String visit(IntegerLiteral n) {
      return "int";
   }

   /**
    * f0 -> "true"
    */
   @Override
   public String visit(TrueLiteral n) {
      return "bool";
   }

   /**
    * f0 -> "false"
    */
   @Override
   public String visit(FalseLiteral n) {
      return "bool";
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   @Override
   public String visit(Identifier n) {
      String _ret=null;
      String id = n.f0.toString();
      if(all_classes.containsKey(id)){
        return id;
      }
      if(env.containsKey(id)){
        _ret = env.get(id);
      }
      else if(class_vars.containsKey(id)){
        _ret = class_vars.get(id);
      }
      else{
        // System.out.println(cur_class);
        // System.out.println(cur_method);
        // System.out.println(id);
        err("no variable found1");
      }
      return _ret;
   }

   /**
    * f0 -> "this"
    */
   @Override
   public String visit(ThisExpression n) {
      if(cur_class == null){
        err("need to be in class");
      }
      return cur_class;
   }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   @Override
   public String visit(ArrayAllocationExpression n) {
      if(!n.f3.accept(this).equals("int")){
        err("need to access index using int");
      }
      return "int[]";
   }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
   @Override
   public String visit(AllocationExpression n) {
      if(!all_classes.containsKey(n.f1.f0.toString())){
        err("class does not exist for new");
      }
      return n.f1.f0.toString();
   }

   /**
    * f0 -> "!"
    * f1 -> Expression()
    */
   @Override
   public String visit(NotExpression n) {
      if(!n.f1.accept(this).equals("bool")){
        err("! expression needs bool");
      }
      return "bool";
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   @Override
   public String visit(BracketExpression n) {
      return n.f1.accept(this);
   }

   public void err(String msg) {
      System.err.println(msg);
      System.out.println("Type error");
      System.exit(1);
   }
}