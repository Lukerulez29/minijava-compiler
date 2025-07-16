import java.util.*;


import minijava.syntaxtree.*;
import minijava.visitor.GJDepthFirst;

public class TypeVisit extends GJDepthFirst<String, ArrayList>{
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
    HashMap<String, Integer> spar_fields = new HashMap();
    //accessed by .get(type).get(method_name)
    HashMap<String, HashMap<String, Integer>> spar_methods = new HashMap();
    String cur_class;
    String cur_method;
    public Stack<Method_elems> cur_methods = new Stack();
    ArrayList<String> cur_spar_vars = new ArrayList();
    Set<String> restricted = Set.of("a2", "a3", "a4", "a5", "a6", "a7", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "t0", "t1", "t2", "t3", "t4", "t5");

    int counter = 0;

   //  String last_result;

    public TypeVisit(HashMap<String, Class_elems> c){
        all_classes = c;
    }

    ArrayList<String> funcs = new ArrayList();

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
   public String visit(MainClass n, ArrayList argu) {
      String _ret=null;
      ArrayList<String> instrs = new ArrayList();

      cur_class = n.f1.f0.toString();
      cur_method = "main";
      class_vars = all_classes.get(n.f1.f0.toString()).vars;
      env = all_classes.get(cur_class).methods.get("main").all_vars;
      instrs.add("func main()");

      n.f14.accept(this, instrs);
      n.f15.accept(this, instrs);

      instrs.add("ret_v0 = 0");
      instrs.add("return ret_v0");

      // instrs.add("nullpmain:");
      // instrs.add("error(\"null pointer\")");

      // instrs.add("arrayoutmain:");
      // instrs.add("error(\"array out of bounds\")");

      for(String i: instrs){
         funcs.add(i);
      }

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
   public String visit(ClassDeclaration n, ArrayList argu) {
      String _ret=null;
      class_vars = all_classes.get(n.f1.f0.toString()).vars;
      cur_class = n.f1.f0.toString();
      cur_spar_vars = all_classes.get(n.f1.f0.toString()).spar_vars;

      for(int i = 0; i < cur_spar_vars.size(); i++){
         spar_fields.put(cur_spar_vars.get(i), i * 4 + 4);
      }

      n.f4.accept(this, argu);
      spar_fields.clear();
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
   public String visit(ClassExtendsDeclaration n, ArrayList argu) {
      String _ret=null;
      class_vars = all_classes.get(n.f1.f0.toString()).vars;
      cur_class = n.f1.f0.toString();
      cur_spar_vars = all_classes.get(n.f1.f0.toString()).spar_vars;

      for(int i = 0; i < cur_spar_vars.size(); i++){
         spar_fields.put(cur_spar_vars.get(i), i * 4 + 4);
      }

      n.f6.accept(this, argu);
      spar_fields.clear();
      return _ret;
   }

      /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   // @Override
   // public String visit(VarDeclaration n){
   //    String _ret=null;
   //    int type = n.f0.f0.which;
   //    String res;
   //    if(type == 3){
   //       res = ((Identifier)n.f0.f0.choice).f0.toString();
   //       if(!all_classes.containsKey(res)){
   //          err("type not found");
   //       }
   //    }
   //    return _ret;
   // }

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
   public String visit(MethodDeclaration n, ArrayList argu) {
      String _ret=null;
      ArrayList<String> instrs = new ArrayList();
      env = all_classes.get(cur_class).methods.get(n.f2.f0.toString()).all_vars;
      cur_method = n.f2.f0.toString();

      ArrayList<String> str_params = all_classes.get(cur_class).methods.get(n.f2.f0.toString()).get_param_names();
      String params = "this";
      for(String s: str_params){
         if(restricted.contains(s)){
            params += " v" + s;
         }
         else{
            params += " " + s;
         }
      }

      instrs.add("func " + cur_class + "_" + n.f2.f0.toString() + "(" + params + ")");

      n.f8.accept(this, instrs);
      String expr_res = n.f10.accept(this, instrs);
      instrs.add("return " + expr_res);

      // instrs.add("nullp:" + n.f2.f0.toString());
      // instrs.add("error(\"null pointer\")");

      // instrs.add("arrayout:" + n.f2.f0.toString());
      // instrs.add("error(\"array out of bounds\")");

      for(String i: instrs){
         funcs.add(i);
      }

      env.clear();
      return _ret;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   // @Override
   // public String visit(FormalParameter n) {
   //    String _ret=null;
   //    int type = n.f0.f0.which;
   //    String res;
   //    if(type == 3){
   //       res = ((Identifier)n.f0.f0.choice).f0.toString();
   //       if(!all_classes.containsKey(res)){
   //          err("type does not exist");
   //       }
   //    }
   //    return _ret;
   // }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   // @Override
   // public String visit(FormalParameterRest n) {
   //    String _ret=null;
   //    n.f1.accept(this);
   //    return _ret;
   // }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   // @Override
   // public String visit(ArrayType n) {
   //    return "int[]";
   // }

   /**
    * f0 -> "boolean"
    */
   // @Override
   // public String visit(BooleanType n) {
   //    return "bool";
   // }

   /**
    * f0 -> "int"
    */
   // @Override
   // public String visit(IntegerType n) {
   //    return "int";
   // }
   
   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   @Override
   public String visit(AssignmentStatement n, ArrayList argu) {
      String _ret=null;

      String RHS = n.f2.accept(this, argu);

      if(env.containsKey(n.f0.f0.toString())){
         if(restricted.contains(n.f0.f0.toString())){
            argu.add("v" + n.f0.f0.toString() + " = " + RHS);
         }
         else{
            argu.add(n.f0.f0.toString() + " = " + RHS);
         }
      }
      else if(class_vars.containsKey(n.f0.f0.toString())){
         argu.add("[this+" + spar_fields.get(n.f0.f0.toString()) + "]" + " = " + RHS);
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
   // can be [a + (idx * 4) + 4]
   // [[this + (c * 4) + 4] + (idx * 4) + 4]
   @Override
   public String visit(ArrayAssignmentStatement n, ArrayList argu) {
      String _ret=null;
      
      String idx = n.f2.accept(this, argu);
      String RHS = n.f5.accept(this, argu);

      if(env.containsKey(n.f0.f0.toString())){
         String new_id;
         if(restricted.contains(n.f0.f0.toString())){
            new_id = "v" + n.f0.f0.toString();
         }
         else{
            new_id = n.f0.f0.toString();
         }
         argu.add("v" + counter + " = 4");
         String tmp1 = "v" + counter;
         counter = counter + 1;

         argu.add("v" + counter + " = " + idx + " * " + tmp1);
         String tmp2 = "v" + counter;
         counter = counter + 1;

         argu.add("v" + counter + " = " + new_id + " + " + tmp2);
         String tmp3 = "v" + counter;
         counter = counter + 1;

         argu.add("v" + counter + " = [" + new_id + " + 0]");
         String tmp4 = "v" + counter;
         counter = counter + 1;

         argu.add("v" + counter + " = " + idx + " < " + tmp4);
         String tmp5 = "v" + counter;
         counter = counter + 1;

         argu.add("v" + counter + " = 0");
         String tmp6 = "v" + counter;
         counter = counter + 1;

         argu.add("v" + counter + " = " + idx + " < " + tmp6);
         String tmp7 = "v" + counter;
         counter = counter + 1;

         argu.add("if0 " + tmp7 + " goto " + tmp7 + "End");
         argu.add("error(\"array index out of bounds\")");
         argu.add(tmp7 + "End:");

         argu.add("if0 " + tmp5 + " goto " + tmp5 + "Error");
         argu.add("goto " + tmp5 + "End");
         argu.add(tmp5 + "Error:");
         argu.add("error(\"array index out of bounds\")");
         argu.add(tmp5 + "End:");

         argu.add("[" + tmp3 + " + 4] = " + RHS);
      }
      else if(class_vars.containsKey(n.f0.f0.toString())){
         argu.add("v" + counter + " = 4");
         String tmp1 = "v" + counter;
         counter = counter + 1;

         argu.add("v" + counter + " = " + spar_fields.get(n.f0.f0.toString()));
         String tmp2 = "v" + counter;
         counter = counter + 1;

         argu.add("v" + counter + " = this + " + tmp2);
         String tmp4 = "v" + counter;
         counter = counter + 1;

         argu.add("v" + counter + " = [" + tmp4 + " + 0]");
         String tmp5 = "v" + counter;
         counter = counter + 1;

         argu.add("v" + counter + " = " + idx + " * " + tmp1);
         String tmp6 = "v" + counter;
         counter = counter + 1;

         argu.add("v" + counter + " = " + tmp5 + " + " + tmp6);
         String tmp7 = "v" + counter;
         counter = counter + 1;

         argu.add("v" + counter + " = [" + tmp5 + " + 0]");
         String tmp8 = "v" + counter;
         counter = counter + 1;

         argu.add("v" + counter + " = " + idx + " < " + tmp8);
         String tmp9 = "v" + counter;
         counter = counter + 1;

         argu.add("v" + counter + " = 0");
         String tmp10 = "v" + counter;
         counter = counter + 1;

         argu.add("v" + counter + " = " + idx + " < " + tmp10);
         String tmp11 = "v" + counter;
         counter = counter + 1;

         argu.add("if0 " + tmp11 + " goto " + tmp11 + "End");
         argu.add("error(\"array index out of bounds\")");
         argu.add(tmp11 + "End:");

         argu.add("if0 " + tmp9 + " goto " + tmp9 + "Error");
         argu.add("goto " + tmp9 + "End");
         argu.add(tmp9 + "Error:");
         argu.add("error(\"array index out of bounds\")");
         argu.add(tmp9 + "End:");

         argu.add("[" + tmp7 + " + 4] = " + RHS);
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
   public String visit(IfStatement n, ArrayList argu) {
      String _ret=null;
      String cond = n.f2.accept(this, argu);
      argu.add("if0 " + cond + " goto " + cond + "Else");
      n.f4.accept(this, argu);
      argu.add("goto " + cond + "End");

      argu.add(cond + "Else:");
      n.f6.accept(this, argu);
      argu.add(cond + "End:");
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
   public String visit(WhileStatement n, ArrayList argu) {
      String _ret=null;
      argu.add("v" + counter + "Start:");
      String tmp = "v" + counter;
      counter = counter + 1;

      String cond = n.f2.accept(this, argu);
      argu.add("if0 " + cond + " goto " + tmp + "End");
      n.f4.accept(this, argu);
      argu.add("goto " + tmp + "Start");
      argu.add(tmp + "End:");
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
   public String visit(PrintStatement n, ArrayList argu) {
      String out = n.f2.accept(this, argu);
      argu.add("print(" + out + ")");
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
   public String visit(Expression n, ArrayList argu) {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    */
   @Override
   public String visit(AndExpression n, ArrayList argu) {
      String LHS = n.f0.accept(this, argu);

      argu.add("if0 " + LHS + " goto " + LHS + "b1");
      String RHS = n.f2.accept(this, argu);
      argu.add("v" + counter + " = " + LHS + " * " + RHS);
      argu.add("goto " + LHS + "b2");
      argu.add(LHS + "b1:");
      argu.add("v" + counter + " = 0");
      argu.add(LHS + "b2:");

      String tmp = "v" + counter;
      counter = counter + 1;

      return tmp;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   @Override
   public String visit(CompareExpression n, ArrayList argu) {
      String LHS = n.f0.accept(this, argu);
      String RHS = n.f2.accept(this, argu);
      argu.add("v" + counter + " = " + LHS + " < " + RHS);
      String tmp = "v" + counter;
      counter = counter + 1;
      return tmp;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   @Override
   public String visit(PlusExpression n, ArrayList argu) {
      String LHS = n.f0.accept(this, argu);
      String RHS = n.f2.accept(this, argu);
      argu.add("v" + counter + " = " + LHS + " + " + RHS);
      String tmp = "v" + counter;
      counter = counter + 1;
      return tmp;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   @Override
   public String visit(MinusExpression n, ArrayList argu) {
      String LHS = n.f0.accept(this, argu);
      String RHS = n.f2.accept(this, argu);
      argu.add("v" + counter + " = " + LHS + " - " + RHS);
      String tmp = "v" + counter;
      counter = counter + 1;
      return tmp;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   @Override
   public String visit(TimesExpression n, ArrayList argu) {
      String LHS = n.f0.accept(this, argu);
      String RHS = n.f2.accept(this, argu);
      argu.add("v" + counter + " = " + LHS + " * " + RHS);
      String tmp = "v" + counter;
      counter = counter + 1;
      return tmp;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   @Override
   public String visit(ArrayLookup n, ArrayList argu) {
      String arr = n.f0.accept(this, argu);
      String idx = n.f2.accept(this, argu);

      argu.add("v" + counter + " = 4");
      String tmp1 = "v" + counter;
      counter = counter + 1;

      argu.add("v" + counter + " = " + tmp1 + " * " + idx);
      String tmp2 = "v" + counter;
      counter = counter + 1;

      argu.add("v" + counter + " = " + arr + " + " + tmp2);
      String tmp3 = "v" + counter;
      counter = counter + 1;

      argu.add("v" + counter + " = [" + arr + " + 0]");
      String tmp4 = "v" + counter;
      counter = counter + 1;

      argu.add("v" + counter + " = " + idx + " < " + tmp4);
      String tmp5 = "v" + counter;
      counter = counter + 1;

      argu.add("v" + counter + " = 0");
      String tmp6 = "v" + counter;
      counter = counter + 1;

      argu.add("v" + counter + " = " + idx + " < " + tmp6);
      String tmp7 = "v" + counter;
      counter = counter + 1;

      argu.add("if0 " + tmp7 + " goto " + tmp7 + "End");
      argu.add("error(\"array index out of bounds\")");
      argu.add(tmp7 + "End:");

      argu.add("if0 " + tmp5 + " goto " + tmp5 + "Error");
      argu.add("goto " + tmp5 + "End");
      argu.add(tmp5 + "Error:");
      argu.add("error(\"array index out of bounds\")");
      argu.add(tmp5 + "End:");

      argu.add("v" + counter + " = [" + tmp3 + " + 4]");
      String tmp8 = "v" + counter;
      counter = counter + 1;

      return tmp8;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   @Override
   public String visit(ArrayLength n, ArrayList argu) {
      String arr = n.f0.accept(this, argu);
      argu.add("v" + counter + " = " + "[" + arr + " + 0]");
      String tmp = "v" + counter;
      counter = counter + 1;
      return tmp;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
   //for spar_methods we need to get the type of LHS, not the variable that is storing LHS
   //maybe use another visitor?
   //need to set spar_methods in J2S
   @Override
   public String visit(MessageSend n, ArrayList argu) {
      String LHS = n.f0.accept(this, argu);
      String params = n.f4.accept(this, argu);

      argu.add("v" + counter + " = " +  LHS);
      String tmp = "v" + counter;
      counter = counter + 1;

      argu.add("if0 " + tmp + " goto " + tmp + "Error");
      argu.add("goto " + tmp + "End");
      argu.add(tmp + "Error:");
      argu.add("error(\"null pointer\")");
      argu.add(tmp + "End:");

      if(params == null){
         params = "";
      }

      argu.add("v" + counter + " = [" + LHS + " + 0]");
      String tmp1 = "v" + counter;
      counter = counter + 1;

      String LHS_type = n.f0.accept(new PrimaryVisit(cur_class, env, class_vars, all_classes), null);
      // if(!spar_methods.containsKey(LHS_type)){
      //    System.out.println(n.f2.f0.toString());
      // }
      argu.add("v" + counter + " = [" + tmp1 + " + " + spar_methods.get(LHS_type).get(n.f2.f0.toString()) + "]");
      String tmp2 = "v" + counter;
      counter = counter + 1;

      argu.add("v" + counter + " = call " + tmp2 + "(" + LHS + " " + params + ")");
      String tmp3 = "v" + counter;
      counter = counter + 1;

      return tmp3;
   }

   /**
    * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
   @Override
   public String visit(ExpressionList n, ArrayList argu) {
      String first_param = n.f0.accept(this, argu);
      String res = first_param;

      for(int i = 0; i < n.f1.size(); i++){
         String cur_param = n.f1.elementAt(i).accept(this, argu);
         res = res + " ";
         res = res + cur_param;
      }
      return res;
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   @Override
   public String visit(ExpressionRest n, ArrayList argu) {
      return n.f1.accept(this, argu);
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
   public String visit(PrimaryExpression n, ArrayList argu) {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   @Override
   public String visit(IntegerLiteral n, ArrayList argu) {
      argu.add("v" + counter + " = " + n.f0.toString());
      String tmp1 = "v" + counter;
      counter = counter + 1;
      return tmp1;
   }

   /**
    * f0 -> "true"
    */
   @Override
   public String visit(TrueLiteral n, ArrayList argu) {
      argu.add("v" + counter + " = 1");
      String tmp1 = "v" + counter;
      counter = counter + 1;
      return tmp1;
   }

   /**
    * f0 -> "false"
    */
   @Override
   public String visit(FalseLiteral n, ArrayList argu) {
      argu.add("v" + counter + " = 0");
      String tmp1 = "v" + counter;
      counter = counter + 1;
      return tmp1;
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   @Override
   public String visit(Identifier n, ArrayList argu) {
      String _ret=null;
      String id = n.f0.toString();
      if(env.containsKey(id)){
         if(restricted.contains(id)){
            _ret = "v" + id;
         }
         else{
            _ret = id;
         }
      }
      else if(class_vars.containsKey(id)){
         argu.add("v" + counter + " = " + "[this + " + spar_fields.get(id) + "]");
         String tmp1 = "v" + counter;
         counter = counter + 1;
        _ret = tmp1;
      }
      return _ret;
   }

   /**
    * f0 -> "this"
    */
   @Override
   public String visit(ThisExpression n, ArrayList argu) {
      return "this";
   }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   @Override
   public String visit(ArrayAllocationExpression n, ArrayList argu) {
      String size = n.f3.accept(this, argu);

      argu.add("v" + counter + " = 4");
      String tmp1 = "v" + counter;
      counter = counter + 1;

      argu.add("v" + counter + " = " + tmp1 + " * " + size);
      String tmp2 = "v" + counter;
      counter = counter + 1;

      argu.add("v" + counter + " = " + tmp1 + " + " + tmp2);
      String tmp3 = "v" + counter;
      counter = counter + 1;

      argu.add("v" + counter + " = alloc(" + tmp3 + ")");
      String tmp4 = "v" + counter;
      counter = counter + 1;

      argu.add("[" + tmp4 + " + 0] = " + size);
      return tmp4;
   }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
   @Override
   public String visit(AllocationExpression n, ArrayList argu) {
      // System.out.println(all_classes.get("List").spar_vars);
      int field_size = all_classes.get(n.f1.f0.toString()).spar_vars.size() * 4 + 4;
      int method_size = all_classes.get(n.f1.f0.toString()).spar_methods.size() * 4 + 4;

      argu.add("v" + counter + " = " + field_size);
      String tmp1 = "v" + counter;
      counter = counter + 1;

      argu.add("v" + counter + " = alloc(" + tmp1 + ")");
      String tmp2 = "v" + counter;
      counter = counter + 1;

      argu.add("v" + counter + " = " + method_size);
      String tmp3 = "v" + counter;
      counter = counter + 1;

      argu.add("v" + counter + " = alloc(" + tmp3 + ")");
      String tmp4 = "v" + counter;
      counter = counter + 1;

      HashMap<String, Method_elems> method_set = all_classes.get(n.f1.f0.toString()).methods;
      for(String key : method_set.keySet()){
         argu.add("v" + counter + " = @" + method_set.get(key).from_class + "_" + method_set.get(key).name);
         String mtmp = "v" + counter;
         counter = counter + 1;

         argu.add("[" + tmp4 + " + " + spar_methods.get(n.f1.f0.toString()).get(method_set.get(key).name) + "] = " + mtmp);
         argu.add("[" + tmp2 + " + 0] = " + tmp4);
      }

      return tmp2;
   }

   /**
    * f0 -> "!"
    * f1 -> Expression()
    */
   @Override
   public String visit(NotExpression n, ArrayList argu) {
      String expr = n.f1.accept(this, argu);

      argu.add("v" + counter + " = 1");
      String tmp1 = "v" + counter;
      counter = counter + 1;

      argu.add("v" + counter + " = " + tmp1 + " - " + expr);
      String tmp2 = "v" + counter;
      counter = counter + 1;

      return tmp2;
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   @Override
   public String visit(BracketExpression n, ArrayList argu) {
      return n.f1.accept(this, argu);
   }

   public void err(String msg) {
      System.err.println(msg);
      System.out.println("Type error");
      System.exit(1);
   }
}