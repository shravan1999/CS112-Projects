package apps;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
	//delims are space,tab,mutiply,add,subtract,divide,parenthesis,bracket
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
public void buildSymbols() {
    	
	//removes spaces and tabs from string
		String exprNoSpace = "";
		for(int i = 0; i < this.expr.length(); i++) {
			
			//if current character is not a space or tab, add it to the new string
			if(!this.expr.substring(i, i+1).equals(" ") && !this.expr.substring(i, i+1).equals("\t")) {
				exprNoSpace += this.expr.substring(i, i+1);
			}
		}
	
    	StringTokenizer strTok = new StringTokenizer(exprNoSpace, delims, false);
    	this.scalars = new ArrayList<ScalarSymbol>();
    	this.arrays = new ArrayList<ArraySymbol>();
    	
    	int prevTokenIndex = 0;
    	
    	while(strTok.hasMoreTokens()) {
    		
    		String token = strTok.nextToken();
    		//uses a prevTokenIndex because otherwise it does the first index of, and we 
    		//want to start looking from the index of the last token
    		int tokenIndex = exprNoSpace.indexOf(token, prevTokenIndex);
    		
	    	//if a character after the token exists, so if the next character is a [
	    	if(exprNoSpace.length() > tokenIndex + token.length() && 
	    			exprNoSpace.substring(tokenIndex + token.length() , 
	    					tokenIndex + token.length() + 1).equals("[")){
	    			
	    		ArraySymbol arraySym = new ArraySymbol(token);
    			
	   			if(this.arrays.size() == 0 || this.arrays.contains(arraySym) == false) {
	   				this.arrays.add(arraySym);
	   			}
	   			
	    	}
    		
    		//if token is a scalar, so no [ after, and no numbers 
    		else if(Expression.hasNumber(token) == false) {
    			
    			ScalarSymbol scalarSym = new ScalarSymbol(token);
    			
    			if(this.scalars.size() == 0 || this.scalars.contains(scalarSym) == false) {
    				this.scalars.add(scalarSym);
    			}
    			
    		}
    		
	    	prevTokenIndex = tokenIndex + token.length();
    		
    	}
    	
    	
    	
    }
    
    private static boolean hasNumber(String str) {
    	for(int i = 0; i < str.length(); i++) {
    		char c = str.charAt(i);
    		if(Character.isDigit(c) == true) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
  
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    public float evaluate() {
    	
    	//removes spaces and tabs from string
    	String exprNoSpace = "";
    	for(int i = 0; i < this.expr.length(); i++) {
    		
    		//if current character is not a space or tab, add it to the new string
    		if(!this.expr.substring(i, i+1).equals(" ") && !this.expr.substring(i, i+1).equals("\t")) {
    			exprNoSpace += this.expr.substring(i, i+1);
    		}
    	}
    	
    	float result = recEvaluate(exprNoSpace);
    	if(result == -0)
    		result *= -1;
    	
    	return result;
    	
    	
    }
    
    
    public static boolean isFloat(String s) {
    	
    	try {
    		
    		Float.parseFloat(s);
    		return true;
    		
    	} catch (Exception e) { 
    		return false;
    	}
    }
    
    private float recEvaluate(String expr) {
    	
    	//we are making new numstack and operator stack because each recursion does not care about the stacks
    	// for the stuff inside parenthesis or bracket, since that stuff will already be evaluated when it reaches here
    	Stack<String> numStack = new Stack<String>();
    	Stack<String> operatorsStack = new Stack<String>();
    	
    	StringTokenizer strTok = new StringTokenizer(expr, delims, true);
    
    	
    	String currentTok = strTok.nextToken();
    	
    	
    	//this index is useful in the findclosingbracket method
    	int index = 0;
    	
    	//loop thru every element
    	while(index < expr.length()) {
    		
    		
    		//makes a scalar symbol to test if the thing is a scalar later on
    		ScalarSymbol testScalarSym = new ScalarSymbol(currentTok);
    		//same with arraysym
    		ArraySymbol testArraySym = new ArraySymbol(currentTok);
    		
    		//if current token is an num
    		if(Expression.isFloat(currentTok)) {
    			
    			
    			numStack.push(currentTok);
    			index += currentTok.length();
    			
    		}
    	
    		
    		//else if the current token is a scalar
    		else if(this.scalars.contains(testScalarSym)) {
    			
    			//iterate thru the scalars array
    			for(int i = 0; i < scalars.size(); i++) {
    				
    				//if find the index of the scalar variable, then add the 
    				//value of the scalar to the stack
    				if(scalars.get(i).name.equals(currentTok)) {
    					numStack.push(scalars.get(i).value + "");
    					index += currentTok.length();
    					break;
    				}
    			}
    		}
    		
    		//else if currenttoken is an array
    		else if(this.arrays.contains(testArraySym)) {
    			int indexClosingBracket = Expression.indexOfClosingBracket(expr, index + currentTok.length());
    			int arrayIndex = (int) this.recEvaluate(expr.substring(index + 1 + currentTok.length(), indexClosingBracket));
    			
    			//finds the correct array by iterating thru
    			int indexOfCorrectArray = 0;   //this is the index of the correct array inside this.arrays
    			for(indexOfCorrectArray = 0; indexOfCorrectArray < this.arrays.size(); indexOfCorrectArray++) {
    				if(this.arrays.get(indexOfCorrectArray).name.equals(currentTok)){
    					numStack.push(this.arrays.get(indexOfCorrectArray).values[arrayIndex] + "");
    					break;
    				}
    			}
    			
    			while(index < indexClosingBracket) {
    				
    				index += currentTok.length();
					
					if(strTok.hasMoreElements()) {
						currentTok = strTok.nextToken();
					}
					
				}
				index += currentTok.length();
    			
    		}
    		
    		
    		//if an operator
    		else if(currentTok.equals("+") || currentTok.equals("-") || currentTok.equals("*") ||
    				currentTok.equals("/")) {
    			
    			if(operatorsStack.isEmpty()) {
    				operatorsStack.push(currentTok);
    			}
    			
    			//if the current operator is of higher precedence, then evaluate the
    			
    			//if the current operator is of equal or lower precedence, then evaluate the
    			//stuff in the stack
    			
    			
    			//if the current operator is of higher precedence than the operator already in the stack 
    			//current operator can only be of higher precedence if current is a * or /
    			//and the prev operator is + or -
    			else if((currentTok.equals("*") || currentTok.equals("/")) 
    					&& (operatorsStack.peek().equals("+") || operatorsStack.peek().equals("-"))) {
    				
    				//if current operator has higher precedence
    				operatorsStack.push(currentTok);
    			}
    			
    			
    			//else, the current operator is of equal or lower precedence
    			//this means we evaluate the stuff in the stack
    			else {
    				
    				//do the operation
    				//
    				if(operatorsStack.isEmpty() == false) {
    					float num1 = Float.parseFloat(numStack.pop());
    					float num2 = Float.parseFloat(numStack.pop());
    					
    					float result = 0;
    					
    					
    					String op = operatorsStack.pop();
    					
    					//we need to see if the number before the current 2 is negative
    					//because otherwise it will treat it as a positive and the subtraction from
    					//way before will not work
    					
    					//if the next operator is a -, change the current num2 to negative 
    					//and then push back in a +
    					if(operatorsStack.isEmpty() == false) {
    						String tempOp = operatorsStack.pop();
    						if(tempOp.equals("-")) {
    							num2 = num2 * -1;
    							operatorsStack.push("+");
    						}
    						
    						else {
    							operatorsStack.push(tempOp);
    						}
    					}
    					
    					
    					if(op.equals("+")) {
    						result = num2 + num1;
    					}
    					else if(op.equals("-")) {
    						result = num2 - num1;
    					}
    					else if(op.equals("*")) {
    						result = num2*num1;
    					}
    					else if(op.equals("/")) {
    						result = num2 / num1;
    					}
    					
    					numStack.push(result + "");
    					operatorsStack.push(currentTok);
    					
    				} 
    				
    			}
    			
    			
    			
    			index += currentTok.length();
    		}
    		
    		//if current token is a opening parenthesis
			else if(currentTok.equals("(")) {
				
				int indexClosingBracket = Expression.indexOfClosingBracket(expr, index);
				//we want to push the result of the entire sub expression onto the num stack so it can be evaluated
				numStack.push(this.recEvaluate(expr.substring(index+1, indexClosingBracket)) +"");
				
				
				while(index < indexClosingBracket) {
					
					index += currentTok.length();
				
					if(strTok.hasMoreElements()) {
					currentTok = strTok.nextToken();
					}
					
				}
				index += currentTok.length();
					
				//set index to the index of closing parenthesis, since we will take the recursive leap
				//to assume that everything inside the parenthesis is evaluated, so now index must be
				//set to the closing parenthesis
				
				
				
			}
    		
			else if(currentTok.equals("\t") || currentTok.equals(" ")) {
				index += currentTok.length();
			}
    		
    		if(strTok.hasMoreTokens() == false) {
    			break;
    		}
    		
    		
    		currentTok = strTok.nextToken();
    		
    	}
    	
    	
    	float ret = 0;
    	
    	
    
    	//gets rid of multiplication stuff at the end of the expression
    	while(operatorsStack.isEmpty() == false && (operatorsStack.peek().equals("*") || operatorsStack.peek().equals("/"))) {
    		float num1 = Float.parseFloat(numStack.pop());
			float num2 = Float.parseFloat(numStack.pop());
			
			String op = operatorsStack.pop();
			
			if(op.equals("*")) {
				ret = num2*num1;
			}
			else if(op.equals("/")) {
				ret = num2 / num1;
			}
			
			numStack.push(ret + "");
    	}
    	
    
    	
    	//reverse stacks so we can evaluate in the right order
    	numStack = Expression.reverseStack(numStack);
    	operatorsStack = Expression.reverseStack(operatorsStack);
    	
    	ret = 0;
    	
    	if(operatorsStack.isEmpty() == false && numStack.isEmpty() == false) {
	    	//repeat while there are still operations to be done in the stack
			while(operatorsStack.isEmpty() == false) {
				
				
				float num1 = Float.parseFloat(numStack.pop());
				float num2 = Float.parseFloat(numStack.pop());
				
				String op = operatorsStack.pop();
				
				
				if(op.equals("+")) {
					ret = num1 + num2;
				}
				else if(op.equals("-")) {
					ret = num1 - num2;
				}
				else if(op.equals("*")) {
					ret = num1*num2;
				}
				else if(op.equals("/")) {
					ret = num1 / num2;
				}
				
				numStack.push(ret + "");
				
			}
    	}
    	
    	else {
    		ret = Float.parseFloat(numStack.pop());
    	}
    	
    	
    	
		return ret;
    	
    	
    	
    }
    
    
    private static Stack<String> reverseStack(Stack<String> stk){
    	Stack<String> ret = new Stack<String>();
    	
    	while(stk.isEmpty() == false) {
    		ret.push(stk.pop());
    	}
    	
    	return ret;
    }
    
   
   
    //finds corresponding closing bracket index
    private static int indexOfClosingBracket(String expr, int indexOpenParen) {
    	
    	Stack<String> stk = new Stack<String>();
    	int i = indexOpenParen;
    	
    	do {
    		
    		String s = expr.substring(i, i+1);
    		
    		if(s.equals("(") || s.equals("[")) {
    			stk.push(s);
    		}
    		
    		else if(s.equals(")") || s.equals("]")) {
    			stk.pop();
    		}
    		
    		
    		if(stk.isEmpty()) {
    			return i;
    		}
    		
    		i++;
    		
    	} while(i < expr.length());
    	
    	
    	return -1;
    }

    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }

}
