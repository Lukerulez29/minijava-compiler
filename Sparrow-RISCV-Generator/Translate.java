import java.util.*;

import sparrowv.*;

import sparrowv.visitor.*;


public class Translate implements Visitor{
  String cur_func;
  ArrayList<String> res = new ArrayList();
  HashMap<String, Integer> stack_size = new HashMap();
  HashMap<String, HashMap<String, Integer>> offset = new HashMap();
  int jmp_counter = 0;


   /*   List<FunctionDecl> funDecls; */
   @Override
   public void visit(Program n){
      res.add(".equiv @sbrk, 9");
      res.add(".equiv @print_string, 4");
      res.add(".equiv @print_char, 11");
      res.add(".equiv @print_int, 1");
      res.add(".equiv @exit, 10");
      res.add(".equiv @exit2, 17");
      res.add(".text");
      res.add(".globl main");
      res.add("jal Main");
      res.add("li a0, @exit");
      res.add("ecall");
      for(int i = 0; i < n.funDecls.size(); i++){
         n.funDecls.get(i).accept(this);
      }
      res.add(".globl print");
      res.add("print:");
      res.add("mv a1, a0");
      res.add("li a0, @print_int");
      res.add("ecall");
      res.add("li a1, 10");
      res.add("li a0, @print_char");
      res.add("ecall");
      res.add("jr ra");
      res.add(".globl error");
      res.add("error:");
      res.add("mv a1, a0");
      res.add("li a0, @print_string");
      res.add("ecall");
      res.add("li a1, 10");
      res.add("li a0, @print_char");
      res.add("ecall");
      res.add("li a0, @exit");
      res.add("ecall");
      res.add("abort_17:");
      res.add("j abort_17");
      res.add(".globl alloc");
      res.add("alloc:");
      res.add("mv a1, a0");
      res.add("li a0, @sbrk");
      res.add("ecall");
      res.add("jr ra");
      res.add(".data");
      res.add(".globl msg_nullptr");
      res.add("msg_nullptr:");
      res.add(".asciiz \"null pointer\"");
      res.add(".align 2");
      res.add(".globl msg_array_oob");
      res.add("msg_array_oob:");
      res.add(".asciiz \"array index out of bounds\"");
      res.add(".align 2");
   }

   /*   Program parent;
   *   FunctionName functionName;
   *   List<Identifier> formalParameters;
   *   Block block; */
   @Override
   public void visit(FunctionDecl n){
      cur_func = n.functionName.toString();
      if(n.functionName.toString().equals("Main") || n.functionName.toString().equals("main")){
        res.add(".globl Main");
        res.add("Main:");
      }
      else{
        res.add(".globl " + n.functionName.toString());
        res.add(n.functionName.toString() + ":");
      }

      res.add("sw fp, -8(sp)");
      res.add("mv fp, sp");

      res.add("li t6, " + stack_size.get(cur_func) * 4);
      res.add("sub sp, sp, t6");
      res.add("sw ra, -4(fp)");

      n.block.accept(this);
   }

   /*   FunctionDecl parent;
   *   List<Instruction> instructions;
   *   Identifier return_id; */
  @Override
  public void visit(Block n){
    for(int i = 0; i < n.instructions.size(); i++){
      n.instructions.get(i).accept(this);
    }

    res.add("lw a0, " + offset.get(cur_func).get(n.return_id.toString()) + "(fp)");
    res.add("lw ra, -4(fp)");
    res.add("lw fp, -8(fp)");
    res.add("addi sp, sp, " + 4 * stack_size.get(cur_func));
    res.add("jr ra");
  }

  /*   Label label; */
  @Override
  public void visit(LabelInstr n){
    res.add(cur_func + n.label.toString() + ":");
  }

  /*   Register lhs;
   *   int rhs; */
  @Override
  public void visit(Move_Reg_Integer n){
    res.add("li " + n.lhs.toString() + ", " + n.rhs);
  }

  /*   Register lhs;
   *   FunctionName rhs; */
  @Override
  public void visit(Move_Reg_FuncName n){
    res.add("la " + n.lhs.toString() + ", " + n.rhs.toString());
  }

  /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
  @Override
  public void visit(Add n){
    res.add("add " + n.lhs.toString() + ", " + n.arg1.toString() + ", " + n.arg2.toString());
  }

  /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
  @Override
  public void visit(Subtract n){
    res.add("sub " + n.lhs.toString() + ", " + n.arg1.toString() + ", " + n.arg2.toString());
  }

  /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
  @Override
  public void visit(Multiply n){
    res.add("mul " + n.lhs.toString() + ", " + n.arg1.toString() + ", " + n.arg2.toString());
  }

  /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
  @Override
  public void visit(LessThan n){
    res.add("slt " + n.lhs.toString() + ", " + n.arg1.toString() + ", " + n.arg2.toString());
  }

  /*   Register lhs;
   *   Register base;
   *   int offset; */
  @Override
  public void visit(Load n){
    res.add("lw " + n.lhs.toString() + ", " + n.offset + "(" + n.base.toString() + ")");
  }

  /*   Register base;
   *   int offset;
   *   Register rhs; */
  @Override
  public void visit(Store n){
    res.add("sw " + n.rhs.toString() + ", " + n.offset + "(" + n.base.toString() + ")");
  }

  /*   Register lhs;
   *   Register rhs; */
  @Override
  public void visit(Move_Reg_Reg n){
    res.add("mv " + n.lhs.toString() + ", " + n.rhs.toString());
  }

  /*   Identifier lhs;
   *   Register rhs; */
  @Override
  public void visit(Move_Id_Reg n){
    res.add("sw " + n.rhs.toString() + ", " + offset.get(cur_func).get(n.lhs.toString()) + "(fp)");
  }

  /*   Register lhs;
   *   Identifier rhs; */
  @Override
  public void visit(Move_Reg_Id n){
    res.add("lw " + n.lhs.toString() + ", " + offset.get(cur_func).get(n.rhs.toString()) + "(fp)");
  }

  /*   Register lhs;
   *   Register size; */
  @Override
  public void visit(Alloc n){
    res.add("mv a0, " + n.size.toString());
    res.add("jal alloc");
    res.add("mv " + n.lhs.toString() + ", a0");
  }

  /*   Register content; */
  @Override
  public void visit(Print n){
    res.add("mv a0, " + n.content.toString());
    res.add("jal print");
  }

  /*   String msg; */
  @Override
  public void visit(ErrorMessage n){
    if(n.msg.contains("null pointer")){
      res.add("la a0, msg_nullptr");
      res.add("jal error");
    }
    else{
      res.add("la a0, msg_array_oob");
      res.add("jal error");
    }
  }

  /*   Label label; */
  @Override
  public void visit(Goto n){
    res.add("jal " + cur_func + n.label.toString());
  }

  /*   Register condition;
   *   Label label; */
  @Override
  public void visit(IfGoto n){
    res.add("bnez " + n.condition.toString() + ", " + cur_func + n.label.toString() + "_no_long_jump" + jmp_counter);
    res.add("jal " + cur_func + n.label.toString());
    res.add(cur_func + n.label.toString() + "_no_long_jump" + jmp_counter + ":");
    jmp_counter += 1;
  }

  /*   Register lhs;
   *   Register callee;
   *   List<Identifier> args; */
  @Override
  public void visit(Call n){
    res.add("li t6, " + 4 * n.args.size());
    res.add("sub sp, sp, t6");
    int tmp_count = 0;
    for(int i = 0; i < n.args.size(); i++){
      res.add("lw t6, " + offset.get(cur_func).get(n.args.get(i).toString()) + "(fp)");
      res.add("sw t6, " + tmp_count + "(sp)");
      tmp_count += 4;
    }

    res.add("jalr " + n.callee.toString());
    res.add("addi sp, sp, " + 4 * n.args.size());
    res.add("mv " + n.lhs.toString() + ", a0");
  }
   
}