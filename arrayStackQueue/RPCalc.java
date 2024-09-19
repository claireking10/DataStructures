package csci2320;

import java.util.Map;
import java.util.Stack;

/**
 * This class represents a reverse Polish calculator.
 */
public class RPCalc {
  /**
   * Evaluate a space separated string in RPC format that can contain variables.
   * @param expr the expression to evaluate.
   * @param variables a map of the variable names to their values.
   * @return the value of that RPC expression.
   */
  public static double eval(String expr, Map<String, Double> variables) {
    Stack<Double> stack = new Stack<>();
    String[] tokens = expr.split("\\s+");
    for (String token:tokens){
      if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")){
        double operand2 = stack.pop();
        double operand1 = stack.pop();
        switch (token){
          case "+":
            stack.push(operand1+operand2);
            break;
          case "-":
            stack.push(operand1-operand2);
            break;
          case "*":
            stack.push(operand1*operand2);
            break;
          case "/":
            if(operand2!=0){
              stack.push(operand1/operand2);
            }
          else {
            throw new ArithmeticException("Division by zero");
          }
          break;
        }
      }
      else if (variables.containsKey(token)){
        stack.push(variables.get(token));
      }
      else{
        try{
          stack.push(Double.parseDouble(token));
        }
        catch(NumberFormatException e){
          throw new IllegalArgumentException("Invalid numeric value: " + token);
        }
      }
    }
    return stack.peek();
  }
}
