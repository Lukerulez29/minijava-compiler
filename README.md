# minijava-compiler
MiniJava to RISC-V Compiler

This project converts MiniJava, a small subset of Java to Sparrow (an intermediate language), and then finally into RISC-V.
The code inside of the folders with "-" such as "Sparrow-RISV-Generator" was developed by me, as well as parts of the src folder where the AST is implemented.

This project was originally part of a class but I implemented more features since completing this class that helped me understand other other compilation strategies.

To run this, add a file to the root directory that contains your MiniJava code. Then, run the command gradle run < name_of_your_file
