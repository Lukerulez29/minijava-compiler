import java.util.*;

import IR.visitor.*;

import IR.syntaxtree.*;



public class SparVisit implements Visitor{
    int line = 0;
    String cur_func;
    HashMap<String, HashMap<String, Tuple>> liveness = new HashMap();
    HashMap<String, HashMap<String, Integer>> labels = new HashMap();
    HashMap<String, Integer> num_args = new HashMap();
    HashMap<String, ArrayList<Integer>> calls = new HashMap();
    Set<String> contains_call = new HashSet<>();
    // HashMap<String, HashMap<String, Integer>> gotos = new HashMap();

    /**
    * f0 -> ( FunctionDeclaration() )*
    * f1 -> <EOF>
    */
   @Override
   public void visit(IR.syntaxtree.Program n) {
      for(int i = 0; i < n.f0.size(); i++){
        n.f0.elementAt(i).accept(this);
      }
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
      ArrayList<Integer> tmp = new ArrayList();
      calls.put(cur_func, tmp);
      HashMap<String, Tuple> vars = new HashMap();
      for(int i = 0; i < n.f3.size(); i++){
        vars.put(((IR.syntaxtree.Identifier)n.f3.elementAt(i)).f0.toString(), new Tuple(line, -1));
      }
      num_args.put(cur_func, n.f3.size());
      liveness.put(cur_func, vars);
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
      for(int i = 0; i < n.f0.size(); i++){
        n.f0.elementAt(i).accept(this);
      }
      liveness.get(cur_func).get(n.f2.f0.toString()).use = line;
      line = line + 1;
   }

    /**
    * f0 -> Label()
    * f1 -> ":"
    */
   @Override
   public void visit(LabelWithColon n) {
      HashMap<String, Integer> loc;
      if (labels.containsKey(cur_func)) {
        loc = labels.get(cur_func);
      } else {
        loc = new HashMap();
      }
      loc.put(n.f0.f0.toString(), line);
      line = line + 1;
      labels.put(cur_func, loc);
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> IntegerLiteral()
    */
   @Override
   public void visit(SetInteger n) {
      if(!liveness.get(cur_func).containsKey(n.f0.f0.toString())){
        liveness.get(cur_func).put(n.f0.f0.toString(), new Tuple(line, -1));
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
      if(!liveness.get(cur_func).containsKey(n.f0.f0.toString())){
        liveness.get(cur_func).put(n.f0.f0.toString(), new Tuple(line, -1));
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
      if(liveness.get(cur_func).get(n.f2.f0.toString()).use < line){
        liveness.get(cur_func).get(n.f2.f0.toString()).use = line;
      }
      if(liveness.get(cur_func).get(n.f4.f0.toString()).use < line){
        liveness.get(cur_func).get(n.f4.f0.toString()).use = line;
      }
      if(!liveness.get(cur_func).containsKey(n.f0.f0.toString())){
        liveness.get(cur_func).put(n.f0.f0.toString(), new Tuple(line, -1));
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
      if(liveness.get(cur_func).get(n.f2.f0.toString()).use < line){
        liveness.get(cur_func).get(n.f2.f0.toString()).use = line;
      }
      if(liveness.get(cur_func).get(n.f4.f0.toString()).use < line){
        liveness.get(cur_func).get(n.f4.f0.toString()).use = line;
      }
      if(!liveness.get(cur_func).containsKey(n.f0.f0.toString())){
        liveness.get(cur_func).put(n.f0.f0.toString(), new Tuple(line, -1));
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
      if(liveness.get(cur_func).get(n.f2.f0.toString()).use < line){
        liveness.get(cur_func).get(n.f2.f0.toString()).use = line;
      }
      if(liveness.get(cur_func).get(n.f4.f0.toString()).use < line){
        liveness.get(cur_func).get(n.f4.f0.toString()).use = line;
      }
      if(!liveness.get(cur_func).containsKey(n.f0.f0.toString())){
        liveness.get(cur_func).put(n.f0.f0.toString(), new Tuple(line, -1));
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
      if(liveness.get(cur_func).get(n.f2.f0.toString()).use < line){
        liveness.get(cur_func).get(n.f2.f0.toString()).use = line;
      }
      if(liveness.get(cur_func).get(n.f4.f0.toString()).use < line){
        liveness.get(cur_func).get(n.f4.f0.toString()).use = line;
      }
      if(!liveness.get(cur_func).containsKey(n.f0.f0.toString())){
        liveness.get(cur_func).put(n.f0.f0.toString(), new Tuple(line, -1));
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
      if(liveness.get(cur_func).get(n.f3.f0.toString()).use < line){
        liveness.get(cur_func).get(n.f3.f0.toString()).use = line;
      }
      if(!liveness.get(cur_func).containsKey(n.f0.f0.toString())){
        liveness.get(cur_func).put(n.f0.f0.toString(), new Tuple(line, -1));
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
      if(liveness.get(cur_func).get(n.f1.f0.toString()).use < line){
        liveness.get(cur_func).get(n.f1.f0.toString()).use = line;
      }
      if(liveness.get(cur_func).get(n.f6.f0.toString()).use < line){
        liveness.get(cur_func).get(n.f6.f0.toString()).use = line;
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
      if(liveness.get(cur_func).get(n.f2.f0.toString()).use < line){
        liveness.get(cur_func).get(n.f2.f0.toString()).use = line;
      }
      if(!liveness.get(cur_func).containsKey(n.f0.f0.toString())){
        liveness.get(cur_func).put(n.f0.f0.toString(), new Tuple(line, -1));
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
      // System.out.println(n.f4.f0.toString());
      // System.out.println(liveness.get(cur_func));
      // System.out.println(cur_func);
      if(liveness.get(cur_func).get(n.f4.f0.toString()).use < line){
        liveness.get(cur_func).get(n.f4.f0.toString()).use = line;
      }
      if(!liveness.get(cur_func).containsKey(n.f0.f0.toString())){
        liveness.get(cur_func).put(n.f0.f0.toString(), new Tuple(line, -1));
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
      if(liveness.get(cur_func).get(n.f2.f0.toString()).use < line){
        liveness.get(cur_func).get(n.f2.f0.toString()).use = line;
      }
      line = line + 1;
   }

   /**
    * f0 -> "goto"
    * f1 -> Label()
    */
   @Override
   public void visit(Goto n) {
      if(labels.containsKey(cur_func) && labels.get(cur_func).containsKey(n.f1.f0.toString())){
        for(String key : liveness.get(cur_func).keySet()){
          if((liveness.get(cur_func).get(key).def < labels.get(cur_func).get(n.f1.f0.toString())) && (liveness.get(cur_func).get(key).use > labels.get(cur_func).get(n.f1.f0.toString()))){
            liveness.get(cur_func).get(key).use = line;
          }
        }
      }
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
      if(liveness.get(cur_func).get(n.f1.f0.toString()).use < line){
        liveness.get(cur_func).get(n.f1.f0.toString()).use = line;
      }

      if(labels.containsKey(cur_func) && labels.get(cur_func).containsKey(n.f3.f0.toString())){
        for(String key : liveness.get(cur_func).keySet()){
          if((liveness.get(cur_func).get(key).def < labels.get(cur_func).get(n.f3.f0.toString())) && (liveness.get(cur_func).get(key).use > labels.get(cur_func).get(n.f3.f0.toString()))){
            liveness.get(cur_func).get(key).use = line;
          }
        }
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
      contains_call.add(cur_func);
      calls.get(cur_func).add(line);
      if(liveness.get(cur_func).get(n.f3.f0.toString()).use < line){
        liveness.get(cur_func).get(n.f3.f0.toString()).use = line;
      }

      for(int i = 0; i < n.f5.size(); i++){
        liveness.get(cur_func).get(((IR.syntaxtree.Identifier)n.f5.elementAt(i)).f0.toString()).use = line;
      }

      if(!liveness.get(cur_func).containsKey(n.f0.f0.toString())){
        liveness.get(cur_func).put(n.f0.f0.toString(), new Tuple(line, -1));
      }
      line = line + 1;
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   @Override
   public void visit(FunctionName n){

   }

   /**
    * f0 -> <IDENTIFIER>
    */
   @Override
   public void visit(Label n){

   }

   /**
    * f0 -> <IDENTIFIER>
    */
   @Override
   public void visit(Identifier n){

   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   @Override
   public void visit(IntegerLiteral n){

   }

   /**
    * f0 -> <STRINGCONSTANT>
    */
   @Override
   public void visit(StringLiteral n){

   }
   
   @Override
   public void visit(If n){

   }

   @Override
   public void visit(LabeledInstruction n){

   }

   /**
    * f0 -> "error"
    * f1 -> "("
    * f2 -> StringLiteral()
    * f3 -> ")"
    */
   @Override
   public void visit(ErrorMessage n){
     line = line + 1;
   }

   /**
    * f0 -> LabelWithColon()
    *       | SetInteger()
    *       | SetFuncName()
    *       | Add()
    *       | Subtract()
    *       | Multiply()
    *       | LessThan()
    *       | Load()
    *       | Store()
    *       | Move()
    *       | Alloc()
    *       | Print()
    *       | ErrorMessage()
    *       | Goto()
    *       | IfGoto()
    *       | Call()
    */
   @Override
   public void visit(Instruction n){
    n.f0.accept(this);
   }

   @Override
   public void visit(NodeList n){

   }
   @Override
   public void visit(NodeListOptional n){

   }
   @Override
   public void visit(NodeOptional n){

   }
   @Override
   public void visit(NodeSequence n){

   }
   @Override
   public void visit(NodeToken n){

   }
}