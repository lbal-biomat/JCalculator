package myCalc;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class Calc extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	JFrame frame;
	JTextField textField;
	JButton[] arrButtons = new JButton[23];
    Container content = getContentPane();
    String str = "";

    public void init() {
    	
        textField = new JTextField();
        textField.setBounds(80,80, 510, 50);
        textField.setFont(new Font(textField.getFont().getName(), Font.BOLD, 16));

        content.add(textField);

        content.setBackground(Color.DARK_GRAY);
        content.setLayout(null); 
    	
    	String[] opStrings = new String[] {"7","8","9","/","←","C","4","5","6","*",
    			"(",")","1","2","3","-","^","√","0",".","%","+","="};
    	for (int i = 0; i < opStrings.length; i++) {
    		arrButtons[i] = new JButton(opStrings[i]);
    		arrButtons[i].setBounds((i*90)%540 + 80, (i/6)*90 + 170, 60, 60);
    		arrButtons[i].addActionListener(this);
    		arrButtons[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
    		content.add(arrButtons[i]);
    	}
    	arrButtons[22].setBounds(440,440,150,60); // = button has different size
    	arrButtons[22].setBackground(Color.decode("#138a07"));
    	setSize(new Dimension(670, 650));
        setTitle("Calc");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
    }

	@Override
	public void actionPerformed(ActionEvent event) {
		String evString = event.getActionCommand();
		
		if (evString == "=" && str != "") {
			try {
				double res = calculate();
				String resString = Double.toString(res);
				textField.setText(resString);
				str = "";
				
			} catch (RuntimeException e) {
		        str = "";
				textField.setText(str);
			} 
		}
		else if (evString == "C") {
	        str = "";
			textField.setText(str);
		}
		else if (evString == "←") {
			if (str.length() > 0) {
				System.out.println(str);
				str = str.substring(0, str.length() - 1);
				textField.setText(str);
			}
		}
		else {
			str += evString;
			textField.setText(str);
		}
	}
    
    
    public double calculate() {
        return new Object() {
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
                if (eat('+')) return +parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus
                
                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }
                
                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
                
                return x;
            }
        }.parse();
    }
	
    public static void main(String[] args) {
        Calc myApp = new Calc();
        myApp.init();
        
    }
	
}
