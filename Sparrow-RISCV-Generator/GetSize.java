import java.util.*;

import sparrowv.*;

import sparrowv.visitor.*;


public class GetSize implements Visitor{
    String cur_func;
    int counter = -12;
    HashMap<String, HashMap<String, Integer>> offset = new HashMap();
    HashMap<String, Integer> stack_size = new HashMap();
    HashMap<String, ArrayList<String>> seen = new HashMap();
    ArrayList<String> registers = new ArrayList<>(Arrays.asList("a2","a3","a4","a5","a6","a7","s1","s2","s3","s4","s5","s6","s7","s8","s9","s10","s11","t0","t1","t2","t3","t4","t5"));

   /*   List<FunctionDecl> funDecls; */
   @Override
   public void visit(Program n){
      for(int i = 0; i < n.funDecls.size(); i++){
         n.funDecls.get(i).accept(this);
      }
   }

   /*   Program parent;
   *   FunctionName functionName;
   *   List<Identifier> formalParameters;
   *   Block block; */
   @Override
   public void visit(FunctionDecl n){
      cur_func = n.functionName.name;
      stack_size.put(cur_func, 2);
      ArrayList<String> tmp = new ArrayList();
      seen.put(cur_func, tmp);

      HashMap<String, Integer> tmp1 = new HashMap();
      offset.put(cur_func, tmp1);

      int tmp_count = 0;
      for(int i = 0; i < n.formalParameters.size(); i++){
         seen.get(cur_func).add(n.formalParameters.get(i).toString());
         offset.get(cur_func).put(n.formalParameters.get(i).toString(), tmp_count);
         tmp_count += 4;
      }

      n.block.accept(this);
      counter = -12;
   }

   /*   FunctionDecl parent;
   *   List<Instruction> instructions;
   *   Identifier return_id; */
   @Override
   public void visit(Block n){
      for(int i = 0; i < n.instructions.size(); i++){
         n.instructions.get(i).accept(this);
      }
      if(!seen.get(cur_func).contains(n.return_id.toString())){
         stack_size.put(cur_func, stack_size.get(cur_func) + 1);
         seen.get(cur_func).add(n.return_id.toString());
         offset.get(cur_func).put(n.return_id.toString(), counter);
         counter = counter - 4;
      }
   }

   /*   Label label; */
   @Override
   public void visit(LabelInstr n){
      
   }

   /*   Register lhs;
   *   int rhs; */
   @Override
   public void visit(Move_Reg_Integer n){
      
   }

   /*   Register lhs;
   *   FunctionName rhs; */
   @Override
   public void visit(Move_Reg_FuncName n){
      
   }

   /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
   @Override
   public void visit(Add n){

   }

   /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
   @Override
   public void visit(Subtract n){

   }

   /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
   @Override
   public void visit(Multiply n){

   }

   /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
   @Override
   public void visit(LessThan n){

   }

   /*   Register lhs;
   *   Register base;
   *   int offset; */
   @Override
   public void visit(Load n){

   }

   /*   Register base;
   *   int offset;
   *   Register rhs; */
   @Override
   public void visit(Store n){
      
   }

   /*   Register lhs;
   *   Register rhs; */
   @Override
   public void visit(Move_Reg_Reg n){
      
   }

   /*   Identifier lhs;
   *   Register rhs; */
   @Override
   public void visit(Move_Id_Reg n){
      if(!seen.get(cur_func).contains(n.lhs.toString())){
         stack_size.put(cur_func, stack_size.get(cur_func) + 1);
         seen.get(cur_func).add(n.lhs.toString());
         offset.get(cur_func).put(n.lhs.toString(), counter);
         counter = counter - 4;
      }
   }

   /*   Register lhs;
   *   Identifier rhs; */
   @Override
   public void visit(Move_Reg_Id n){

   }

   /*   Register lhs;
   *   Register size; */
   @Override
   public void visit(Alloc n){

   }

   /*   Register content; */
   @Override
   public void visit(Print n){

   }

   /*   String msg; */
   @Override
   public void visit(ErrorMessage n){
      
   }

   /*   Label label; */
   @Override
   public void visit(Goto n){
      
   }

   /*   Register condition;
   *   Label label; */
   @Override
   public void visit(IfGoto n){

   }

   /*   Register lhs;
   *   Register callee;
   *   List<Identifier> args; */
   @Override
   public void visit(Call n){

   }
}