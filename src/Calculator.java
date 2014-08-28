import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.plaf.basic.BasicOptionPaneUI.ButtonActionListener;
import javax.xml.soap.Text;


public class Calculator implements ActionListener {
	JFrame frame;
	JPanel panel;
	JPanel opsPanel;

	JButton add;
	JButton sub;
	JButton div;
	JButton mult;
	JButton mod;


	JButton enter;
	JButton clear;
	JButton decimal;

	JTextArea out;
	JPanel digPanel;

	JButton[] digits;

	Queue<Integer> input = new LinkedList<Integer>();

	public Calculator() {
		frame = new JFrame();
		panel = new JPanel();
		frame.getContentPane().add(panel);

		panel.setLayout(new BorderLayout());

		opsPanel = new JPanel();
		panel.add(opsPanel, BorderLayout.EAST);
		opsPanel.setLayout(new BoxLayout(opsPanel, 1));

		add = new JButton("+");
		add.addActionListener(this);
		sub = new JButton("Ð");
		sub.addActionListener(this);
		div = new JButton("/");
		div.addActionListener(this);
		mult = new JButton("*");
		mult.addActionListener(this);
		mod = new JButton("%");
		mod.addActionListener(this);
		decimal = new JButton(".");
		decimal.addActionListener(this);

		opsPanel.add(add);
		opsPanel.add(sub);
		opsPanel.add(div);
		opsPanel.add(mult);
		opsPanel.add(mod);
		opsPanel.add(decimal);


		frame.setVisible(true);

		out = new JTextArea();
		out.setEditable(false);
		out.setSize(300, 20);
		out.setRows(1);
		panel.add(out, BorderLayout.NORTH);

		frame.pack();

		digPanel = new JPanel();
		digPanel.setLayout(new GridLayout(4, 3));
		panel.add(digPanel, BorderLayout.CENTER);
		digits = new JButton[10];
		for(int i=0; i<10; i++) {
			digits[i] = new JButton();
			digits[i].setText("" + i);
			digPanel.add(digits[i]);
			digits[i].addActionListener(this);

		}

		clear = new JButton("C");
		clear.addActionListener(this);
		enter = new JButton("=");
		enter.addActionListener(this);

		digPanel.add(clear);
		digPanel.add(enter);

	}



	public static void main(String[] args) {
		Calculator c = new Calculator();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == add) {
			input.add(-1);
			out.setText("+");
		}
		else if (e.getSource() == sub) {
			input.add(-2);
			out.setText("-");
		}
		else if (e.getSource() == div) {
			input.add(-3);
			out.setText("/");

		}
		else if (e.getSource() == mult) {
			input.add(-4);
			out.setText("*");

		}
		else if (e.getSource() == mod) {
			input.add(-5);
			out.setText("%");

		}
		else if (e.getSource() == clear) {
			clear();
		}
		else if (e.getSource() == enter) {
			out.setText("" + enter());
		}
		else if (e.getSource() == decimal) {
			input.add(-6);
			out.append(".");
		}
		else {
			for(int i=0; i<digits.length; i++){
				if (e.getSource() == digits[i]) {
					if(input.isEmpty()) out.setText("");
					input.add(i);
					out.append("" + i);
				}
			}
		}
	}



	private double enter() {
		//Check for no input or op first
		if(input.isEmpty() || (input.peek() < 0 && input.peek() != -6)) {
			out.setText("0");
			return 0;
		}
		//Check for only one input
		else if (input.size() == 1) {
			if (input.peek() > 0) { 
				out.setText("" + input.peek());
				return input.remove();
			}
			else return 0;
		}
		else {
			boolean hasOp = false;
			boolean hasDec = false;
			int decDeg = 0;
			double num1 = 0;
			double num2 = 0;
			while(hasOp == false) {
				if(hasDec) {
					decDeg++;
					num1 += (double) input.remove() /(Math.pow(10, decDeg));
				}
				else{
					num1 = num1*10;
					num1 += (double) input.remove();
				}
				//If no more input, return the only number entered
				if(input.isEmpty()) return num1;
				//Check if op next
				else if (input.peek() == -6 ) {
					hasDec = true;
					input.remove();
				}
				else if(input.peek() < 0) hasOp = true;
			}
			while(!input.isEmpty()) {				
				//Has op
				int op = input.remove();
				//Check for double ops
				if (input.peek() == -6) hasDec = true;
				else if (input.peek() < 0) {
					error();
					return 0;
				}
				else {
					hasOp = false;
					num2 = 0;
					decDeg = 0;
					hasDec = false;
					
					while(hasOp == false) {
						//If has decimal, increment degree
						if(hasDec) {
							decDeg++;
							num2 += (double) input.remove() /(Math.pow(10, decDeg));
						}
						//If no decimal, increment
						else {
							num2 = num2*10;
							num2 += (double) input.remove();
						}
						if(input.isEmpty()) return calc(num1, num2, op);
						else if(input.peek() == -6){
							if(hasDec) {
								error();
								return 0;
							}
							else {
								hasDec = true;
								decDeg = 0;
							}
						}
						else if(input.peek() < 0){
							hasOp = true;
							num1 = calc(num1, num2, op);
						}
					}
				}
			}
		}
		return 0;
	}



	private double calc(double num1, double num2, int op) {
		switch (op) {
		case -1: return num1 + num2;
		case -2: return num1 - num2;
		case -3: 
			if (num2 == 0) {
				error();
				return 0;
			}
			else {
				return num1 / num2;
			}
		case -4: return num1 * num2;
		case -5: return num1 % num2;
		}
		return 0;
	}



	private void clear() {
		input.clear();
		out.setText("0");
	}

	private void error(){
		out.setText("ERR");
	}
}

