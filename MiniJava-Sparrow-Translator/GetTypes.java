import java.util.*;

import minijava.syntaxtree.*;
import minijava.visitor.GJVoidDepthFirst;

public class GetTypes extends GJVoidDepthFirst<Class_elems>{
    public HashMap<String, Class_elems> classes = new HashMap();

    //calling a method that is inherited - b.method1() can be a call to a.method1()
    //accessing field from supertype
    //every time you see a field variable it will be represented by [this + offset]
    //should keep track of all field variables and their potential offsets

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
   public void visit(MainClass n, Class_elems argu) {
      Class_elems main_class = new Class_elems(n.f1.f0.toString());
      Method_elems main = new Method_elems("main");
      main.return_type = "void";
      main.from_class = n.f1.f0.toString();

      n.f14.accept(new GetMethod(), main);

      main_class.methods.put("main", main);
      classes.put(n.f1.f0.toString(), main_class);
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
   public void visit(ClassDeclaration n, Class_elems argu) {
      Class_elems cur_class = new Class_elems(n.f1.f0.toString());
      n.f3.accept(this, cur_class);
      n.f4.accept(this, cur_class);
      classes.put(cur_class.name, cur_class);
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
    * Need to make sure to iterate through the list of declared classes to check that extends class exists
    * Also need to add the extends methods to the class extending from it and potentially the methods from
    * the class higher up in the hierarchy also extending, check for overriding
    * Also need to check for duplicate class names
    */
   @Override
   public void visit(ClassExtendsDeclaration n, Class_elems argu) {
      Class_elems cur_class = new Class_elems(n.f1.f0.toString());
      cur_class.extend_class = n.f3.f0.toString();
      n.f5.accept(this, cur_class);
      n.f6.accept(this, cur_class);
      if(!(classes.containsKey(n.f1.f0.toString()))){
         classes.put(cur_class.name, cur_class);
      }
      else{
         err("class already defined");
      }
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   @Override
   public void visit(VarDeclaration n, Class_elems argu) {
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

      if(!(argu.vars.containsKey(n.f1.f0.toString()))){
         argu.vars.put(n.f1.f0.toString(), res);
         argu.spar_vars.add(n.f1.f0.toString());
      }
      else{
         err("class already defined");
      }
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
   public void visit(MethodDeclaration n, Class_elems argu) {
      Method_elems cur_method = new Method_elems(n.f2.f0.toString());
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
      }
      cur_method.return_type = res;
      cur_method.from_class = argu.name;
      
      n.f4.accept(new GetMethod(), cur_method);
      n.f7.accept(new GetMethod(), cur_method);
      
      if(!(argu.methods.containsKey(cur_method.name))){
         argu.methods.put(cur_method.name, cur_method);
         argu.spar_methods.add(cur_method.name);
      }
      else{
         err("can not overload methods");
      }
   }

   public void err(String msg) {
      System.err.println(msg);
      System.out.println("Type error");
      System.exit(1);
   }
    
}