package myCalc;

import java.math.BigDecimal;
import java.math.MathContext;      

public class Calculator {
    
	public String str = "";
	
	public String toPrecision(double d, int digits) {
	    String s = String.format("%."+((digits>0)?digits:16)+"g",d).replace("e+0","e+").replace("e-0","e-");
	    return s;
	}
	
    public double eval(boolean radians) {
    	//TODO: fix precision issue
        double d = new Object() {
            int pos = -1, ch;
            
            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }
            
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }
            
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }
            
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }
            
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }
            
            double parseFactor() {
                if (eat('+')) {
                	return +parseFactor(); // unary plus
                }
                if (eat('-')) {
                	return -parseFactor(); // unary minus
                }
                if (eat('√')) {
                	return Math.sqrt(parseFactor()); //square root
                }
                double x;
                int startPos = this.pos;
                if (eat('π')) {
                	x = Math.PI;
                }
                else if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions0.0
                    while (ch >= 'a' && ch <= 'z') {
                    	nextChar();
                    }
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) {
                        	throw new RuntimeException("Missing ')' after argument to " + func);
                        }
                    } else {
                        x = parseFactor();
                    }
                    
                    if (func.equals("sqrt")) {
                    	x = Math.sqrt(x);
                    }
                    else if (radians) {
                    	if (func.equals("sin")) {
                        	x = Math.sin(x);
                        }
                        else if (func.equals("cos")) {
                        	x = Math.cos(x);
                        }
                        else if (func.equals("tan")) {
                        	x = Math.tan(x);
                        }
                    }
                    else if (!radians) {
                    	if (func.equals("sin")) {
                        	x = Math.sin(Math.toRadians(x));
                        }
                        else if (func.equals("cos")) {
                        	x = Math.cos(Math.toRadians(x));
                        }
                        else if (func.equals("tan")) {
                        	x = Math.tan(Math.toRadians(x));
                        }
                    }
                    else {
                    	throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }
                
                if (eat('%')) {
                	x = x % parseFactor(); //modulo
                }
                if (eat('^')) {
                	x = Math.pow(x, parseFactor()); // exponentiation     
                }
                
                return x;
            }
        }.parse();
        double res = new BigDecimal(d, MathContext.DECIMAL32).doubleValue();
        if (Math.abs(res) < 1E-15) {
        	res = 0;
        }
        return res;
    }
}
