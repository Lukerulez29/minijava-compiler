import java.util.*;

import IR.SparrowParser;
import IR.syntaxtree.*;

import java.io.InputStream;

import IR.SparrowParser;
import IR.visitor.SparrowVConstructor;
import IR.syntaxtree.Node;
import IR.registers.Registers;

import sparrowv.Program;

public class SV2V {
    public static void main(String[] args) throws Exception {
        Registers.SetRiscVregs();
        InputStream in = System.in;
        new SparrowParser(in);
        Node root = SparrowParser.Program();
        SparrowVConstructor constructor = new SparrowVConstructor();
        root.accept(constructor);
        Program program = constructor.getProgram();
        
        GetSize first_pass = new GetSize();
        first_pass.visit(program);
        // System.out.println(first_pass.stack_size);
        // System.out.println(first_pass.offset);
        Translate second_pass = new Translate();
        second_pass.offset = first_pass.offset;
        second_pass.stack_size = first_pass.stack_size;
        second_pass.visit(program);

        for(String s : second_pass.res){
            System.out.println(s);
        }
    }
}