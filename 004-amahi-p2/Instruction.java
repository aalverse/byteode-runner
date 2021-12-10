// TO DO: JavaDocs

import java.util.*;

public class Instruction
{
	private int offset;
	private String opcode;
	private ArrayList<Integer> parameters;  

	public Instruction(String s) {  

		s = s.trim();
		parameters = new ArrayList<Integer>();

		String[] tokens = s.split("[:|,|\\s]+");
		int count = 1;
		for (String token : tokens) {
			String item = token.trim();
		    if (item.length() == 0) {
		    	System.out.println("blank item");
				continue;
		    }
		    if (count == 1) {
				offset = Integer.valueOf(item);
		    } else if (count == 2) {
				opcode = item;
		    } else if (count == 3) {			
				parameters.add(Integer.valueOf(item));  
		    } else if (count == 4) {
				parameters.add(Integer.valueOf(item));  
		    } else {
				throw new RuntimeException("Illegal format: " + item);
		    }
		    count++;
		}
		
	}

	public String toString() {
		String s = offset + ": " + opcode + " ";
		if (parameters != null) {
			for (int param : parameters) {
				s += param + " ";
			}
		}
		return s;
	}

	public int getOffset() { return offset; }
	
	public String getOpcode() { return opcode; }

	public int getNumParameters() { return parameters.size(); }
	
	public int getParam1() { 
		if (getNumParameters() < 1) {
			throw new RuntimeException("instruction takes zero parameters");
		}
		return parameters.get(0); 
	}

	public int getParam2() { 
		if (getNumParameters() < 2) {
			throw new RuntimeException("instruction takes zero or one parameters");
		}
		return parameters.get(1); 
	}

	public static void main(String args[]) {

		Instruction ins = new Instruction("0: iconst_2");
		if ( (ins.getOffset() == 0) && 
				(ins.getOpcode().equals("iconst_2")) && 
					(ins.getNumParameters() == 0)) {
			System.out.println("Yay1");
		}

		ins = new Instruction("21 : bipush         6");
		if ( (ins.getOffset() == 21) && 
				(ins.getOpcode().equals("bipush")) && 
					(ins.getNumParameters() == 1) &&
						(ins.getParam1() == 6) ){
			System.out.println("Yay2");
		}

		ins = new Instruction("40:iinc 4, 1");
		if ( (ins.getOffset() == 40) && 
				(ins.getOpcode().equals("iinc")) && 
					(ins.getNumParameters() == 2) &&
						(ins.getParam1() == 4) && (ins.getParam2() == 1)) {
			System.out.println("Yay3");
		}
	}
}