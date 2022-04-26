package myCalc;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;


public class Window extends JFrame implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;
	JFrame frame;
	JTextField textField;
	JButton[] arrButtons;
    Container content = getContentPane();
    ButtonGroup bg;    
    JRadioButton rad, deg;
    boolean radians;
    Calculator calc;
    public Window(Calculator calc) {
		this.calc = calc;
	}
    
    public void initRadioButtons() {
    	bg = new ButtonGroup();
    
    	rad = new JRadioButton("Radians", true);
    	rad.setBounds(80,160,100,40);
    	rad.addKeyListener(this);
    	rad.setBackground(null);
    	rad.setForeground(Color.white);
    	bg.add(rad);
    	content.add(rad);
    	
    	deg = new JRadioButton("Degrees");
    	deg.setBounds(200,160,100,40);
    	deg.addKeyListener(this);
    	deg.setBackground(null);
    	deg.setForeground(Color.white);
    	bg.add(deg);
    	content.add(deg);
    }
    
    public void initTextField() {
    	textField = new JTextField();
        textField.setBounds(80,80, 600, 50);
        textField.setFont(new Font(textField.getFont().getName(), Font.BOLD, 16));
        textField.addKeyListener(this);
        content.add(textField);
    }
    
    public void initButtons() {
    	arrButtons = new JButton[27];
    	String[] opStrings = new String[] {"π","7","8","9","/","←","C","sin","4","5","6","*",
    			"(",")","cos","1","2","3","-","^","√","tan","0",".","%","+","="};
    	for (int i = 0; i < opStrings.length; i++) {
    		arrButtons[i] = new JButton(opStrings[i]);
    		arrButtons[i].setBounds((i*90)%630 + 80, (i/7)*90 + 220, 60, 60);
    		arrButtons[i].addActionListener(this);
    		arrButtons[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
    		content.add(arrButtons[i]);
    	}
    	arrButtons[opStrings.length-1].setBounds(530,490,150,60); // = button has different size
    	arrButtons[opStrings.length-1].setBackground(Color.decode("#138a07"));
	}

    public void init() {   
    	initRadioButtons();
    	initTextField();
        initButtons();
        content.setBackground(Color.DARK_GRAY);
        content.setLayout(null); 
    	setSize(new Dimension(760, 650));
        setTitle("JCalculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
    }


	@Override
	public void keyTyped(KeyEvent keyEvent) { //this is working
	}
	
	@Override
	public void keyPressed(KeyEvent keyEvent) {
	}

	@Override
	public void keyReleased(KeyEvent keyEvent) {
		if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
			calculate();
		}		
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String evString = event.getActionCommand();
		
		if (evString == "=") {
			calculate();				
		}
		else if (evString == "C") {
	        calc.str = "";
			textField.setText(calc.str);
		}
		else if (evString == "←") {
			if (calc.str.length() > 0) {
				System.out.println(calc.str);
				calc.str = calc.str.substring(0, calc.str.length() - 1);
				textField.setText(calc.str);
			}
		}
		else {
			calc.str = textField.getText();
			calc.str += evString;
			textField.setText(calc.str);
		}
	}
	
	public void calculate() {
		try {
			calc.str = textField.getText();
			radians = true;

			if (deg.isSelected()) {
				radians = false;
			}
			double res = calc.eval(radians);
			String resString = Double.toString(res);
			textField.setText(resString);
			calc.str = "";
		}
		catch (RuntimeException e) {
			calc.str = "";
			textField.setText(calc.str);
		}
	}
	
    public static void main(String[] args) {
    	Calculator myCalculator = new Calculator();
        Window myApp = new Window(myCalculator);
        myApp.init();
    }
}
