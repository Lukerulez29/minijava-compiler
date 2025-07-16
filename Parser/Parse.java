import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Parse{
  public ArrayList<String> tokens = new ArrayList<>();
  public String token;
  public int cur_ptr = 0;

  private void scan(char[] input) throws IOException{
    int cur_state = 0;

    for(int i = 0; i < input.length; i++){
      if(cur_state == 0 && (input[i] == ' ' || input[i] == '\t' || input[i] == '\n')){
        continue;
      }
      if(cur_state == 0){
        if(input[i] == '{'){
          tokens.add("{");
        }
        else if(input[i] == '}'){
          tokens.add("}");
        }
        else if(input[i] == '('){
          tokens.add("(");
        }
        else if(input[i] == ')'){
          tokens.add(")");
        }
        else if(input[i] == ';'){
          tokens.add(";");
        }
        else if(input[i] == '!'){
          tokens.add("!");
        }
        else if(input[i] == 'S'){
          cur_state = 1;
        }
        else if(input[i] == 'i'){
          cur_state = 18;
        }
        else if(input[i] == 'e'){
          cur_state = 19;
        }
        else if(input[i] == 'w'){
          cur_state = 22;
        }
        else if(input[i] == 't'){
          cur_state = 26;
        }
        else if(input[i] == 'f'){
          cur_state = 29;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 1){
        if(input[i] == 'y'){
          cur_state = 2;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 2){
        if(input[i] == 's'){
          cur_state = 3;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 3){
        if(input[i] == 't'){
          cur_state = 4;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 4){
        if(input[i] == 'e'){
          cur_state = 5;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 5){
        if(input[i] == 'm'){
          cur_state = 6;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 6){
        if(input[i] == '.'){
          cur_state = 7;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 7){
        if(input[i] == 'o'){
          cur_state = 8;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 8){
        if(input[i] == 'u'){
          cur_state = 9;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 9){
        if(input[i] == 't'){
          cur_state = 10;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 10){
        if(input[i] == '.'){
          cur_state = 11;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 11){
        if(input[i] == 'p'){
          cur_state = 12;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 12){
        if(input[i] == 'r'){
          cur_state = 13;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 13){
        if(input[i] == 'i'){
          cur_state = 14;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 14){
        if(input[i] == 'n'){
          cur_state = 15;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 15){
        if(input[i] == 't'){
          cur_state = 16;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 16){
        if(input[i] == 'l'){
          cur_state = 17;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 17){
        if(input[i] == 'n'){
          tokens.add("System.out.println");
          cur_state = 0;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 18){
        if(input[i] == 'f'){
          tokens.add("if");
          cur_state = 0;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 19){
        if(input[i] == 'l'){
          cur_state = 20;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 20){
        if(input[i] == 's'){
          cur_state = 21;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 21){
        if(input[i] == 'e'){
          tokens.add("else");
          cur_state = 0;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 22){
        if(input[i] == 'h'){
          cur_state = 23;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 23){
        if(input[i] == 'i'){
          cur_state = 24;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 24){
        if(input[i] == 'l'){
          cur_state = 25;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 25){
        if(input[i] == 'e'){
          tokens.add("while");
          cur_state = 0;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 26){
        if(input[i] == 'r'){
          cur_state = 27;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 27){
        if(input[i] == 'u'){
          cur_state = 28;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 28){
        if(input[i] == 'e'){
          tokens.add("true");
          cur_state = 0;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 29){
        if(input[i] == 'a'){
          cur_state = 30;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 30){
        if(input[i] == 'l'){
          cur_state = 31;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 31){
        if(input[i] == 's'){
          cur_state = 32;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
      else if(cur_state == 32){
        if(input[i] == 'e'){
          tokens.add("false");
          cur_state = 0;
        }
        else{
          throw new IOException("Parse Error");
        }
      }
    }
    tokens.add("EOF");
  }

  private String next_token(){
    int tmp = cur_ptr;
    cur_ptr = cur_ptr + 1;
    return tokens.get(tmp);
  }

  private void eat(String a) throws IOException{
    if(token.equals(a)){
      // System.out.println(token);
      // System.out.println(a);
      if(cur_ptr < tokens.size()){
        token = next_token();
      }
    }
    else{
      // System.out.println(token);
      // System.out.println(a);
      throw new IOException("Parse Error");
    }
  }

  private void goal() throws IOException{
    // for(int i = 0; i < tokens.size(); i++){
    //   System.out.println(tokens.get(i));
    // }
    token = next_token();
    S();
    eat("EOF");
  }

  private void S() throws IOException{
    if(token.equals("{")){
      eat("{");
      L();
      eat("}");
    }
    else if(token.equals("System.out.println")){
      eat("System.out.println");
      eat("(");
      E();
      eat(")");
      eat(";");
    }
    else if(token.equals("if")){
      eat("if");
      eat("(");
      E();
      eat(")");
      S();
      eat("else");
      S();
    }
    else if(token.equals("while")){
      eat("while");
      eat("(");
      E();
      eat(")");
      S();
    }
    else{
      throw new IOException("Parse Error");
    }
  }

  private void L() throws IOException{
    if(token.equals("}")){
    }
    else{
      S();
      L();
    }
  }

  private void E() throws IOException{
    if(token.equals("true")){
      eat("true");
    }
    else if(token.equals("false")){
      eat("false");
    }
    else if(token.equals("!")){
      eat("!");
      E();
    }
    else{
      throw new IOException("Parse Error");
    }
  }



  public static void main(String [] args) {
    Scanner inp = new Scanner(System.in);
    StringBuilder inputBuilder = new StringBuilder();

    while (inp.hasNextLine()) {
      String line = inp.nextLine();
      inputBuilder.append(line).append("\n");
    }
    char[] inputChars = inputBuilder.toString().toCharArray();

    Parse s = new Parse();
    try{
      s.scan(inputChars);
      s.goal();
      System.out.println("Program parsed successfully");
    }catch(IOException e) {
      System.out.println("Parse error");
    }
  }
}