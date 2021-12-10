// TO DO: add your implementation and JavaDocs.

import java.util.Scanner;
import java.io.File;
import java.io.IOException;

/**
 * Simulates JVM.
 * @author Aarshad Mahi
 */
public class Interpreter {

	// We will test your implementation by running "java Interpreter [inputFile]" and checking your output
	// which should be one or more integer values separated by a single space (the outcome(s) of the "print" instruction)
	// (see the "Testing" section in the project description document)

	// Below are hints that you might want to follow

	// Phase1:
	// create an attribure of type Stack<Integer>
	/**
	 * type Stack holds values as stack.
	 */
	private Stack<Integer> stack;

	// Phase2:
	// create an attribute of type HashMap<Integer, Integer> to map the index of a variable to its value
	// create an attribute of type HashMap<Integer, Node<Instruction>> to map the offset of an instruction to its corresponding node in the LList
	/**
	 * Holds the values for Instruction.
	 */
	private HashMap<Integer, Integer> indexMap;

	/**
	 * Holds the value for instrction's node.
	 */
	private HashMap<Integer, Node<Instruction>> instructMap;

	/**
	 * instruction pointer global.
	 */
	private Node<Instruction> pointer;

	/**
	 * List of instructions.
	 */
	private LList<Instruction> temp;

	// public static LList<Instruction> readFile(String filename) throws IOException
	//
	// parse the instructions in fileName and store them in a LList<Instruction> (each as a Node<Instruction>) 
	// return the resulting LList<Instruction>
	// In the list, the instructions must follow the same order as in the input file
	//
	// (Note: Do not use "dummy nodes" since this is Java and not C)

	/**
	 * returns a LList of instruction from input file.
	 * @param filename filename
	 * @return List
	 * @throws IOException if file does not exist
	 */
	public static LList<Instruction> readFile(String filename) throws IOException {
		LList<Instruction> instructions = new LList<>();
		Scanner scan = new Scanner(new File(filename));

		while (scan.hasNextLine()){
			String line = scan.nextLine();
			instructions.insertLast(new Node<Instruction>(new Instruction(line)));
		}
		return instructions;
	}
	
	// public void evaluateInstructions(LList<Instruction> list)
	//
	// traverse and evaluate the list of instructions 
	//
	// Hints: 
	// depending on the instruction at hand you might need to
	// a) push a value on the stack 
	// b) fetch the value from HashMap<Integer, Integer> then push it on the stack
	// c) pop a value off the stack and store it in HashMap<Integer, Integer>
	// d) to pop the stack and store the value in HashMap<Integer, Integer>
	// e) etc.
	// 
	// HashMap<Integer, Node<Instruction>> is only needed when evaluating the following instructions:
	// goto location, if_icmpeq location, if_icmpne location, if_icmpge location, if_icmpgt location, 
	// if_icmple location, if_icmplt location, ifne location

	/**
	 * Evaluates and simulates JVM.
	 * @param list list of instruction
	 */
	public void evaluateInstructions(LList<Instruction> list) {
		pointer = list.getFirst();
		this.temp = list;

		if(stack == null){
			stack = new Stack<>();
		}

		if(instructMap == null){
			instantiateInstructionMap(list);
		}

		if(indexMap == null){
			indexMap = new HashMap<>();
		}

		while (pointer!=null){
			String opcode = getOpcode(pointer.getValue());

			switch (opcode){
				case "iconst":
					iconst(pointer.getValue());
					break;
				case "bipush":
					biPush(pointer.getValue());
					break;
				case "istore":
					istore(pointer.getValue());
					break;
				case "iload":
					iload(pointer.getValue());
					break;
				case "if_icmple":
					if(if_icmple(pointer.getValue())){continue;}else {break;}
				case "if_icmpgt":
					if(if_icmpgt(pointer.getValue())){continue;}else {break;}
				case "if_icmpge":
					if (if_icmpge(pointer.getValue())) { continue;} else{break;}
				case "if_icmplt":
					if (if_icmplt(pointer.getValue())) { continue;} else{break;}
				case "if_icmpeq":
					if (if_icmpeq(pointer.getValue())) { continue;} else{break;}
				case "if_icmpne":
					if (if_icmpne(pointer.getValue())) { continue;} else{break;}
				case "ifne":
					if (ifne(pointer.getValue())) { continue;} else{break;}
				case "goto":
					goTo(pointer.getValue());
					continue;
				case "iinc":
					iinc(pointer.getValue());
					break;
				case "iadd":
					iadd();
					break;
				case "imul":
					imult();
					break;
				case "idiv":
					idiv();
					break;
				case "isub":
					isub();
					break;
				case "irem":
					irem();
					break;
				case "print":
					System.out.print("Output: " + stack.pop() + "\n");;
					break;
				case "return":
					System.exit(0);
					break;
			}
			pointer = pointer.getNext();
		}
	}

	/**
	 * stores value of instruction in stack.
	 * @param instruction opcode value
	 */
	private void iconst(Instruction instruction){
		stack.push(getOpcodeVariable(instruction));
	}

	/**
	 * pushes value into indexMap.
	 * @param instruction opcode value
	 */
	private void biPush(Instruction instruction){
		stack.push(getOpcodeVariable(instruction));
	}

	/**
	 * stores value in indexMap.
	 * @param instruction opcode value
	 */
	private void istore(Instruction instruction){
		System.out.println(instruction);
		indexMap.put(getOpcodeVariable(instruction), stack.pop());
	}

	/**
	 * pushes values from indexMap to stack.
	 * @param instruction opcode value
	 */
	private void iload(Instruction instruction){
		stack.push(indexMap.get(getOpcodeVariable(instruction)));
	}

	/**
	 * sets pointer to location of instruction.
	 * @param instruction opcode value
	 */
	private void goTo(Instruction instruction){
		this.pointer = getNode(instruction);
	}

	/**
	 * increments value at instruction param1.
	 * @param instruction opcode value
	 */
	private void iinc (Instruction instruction){
		int key = instruction.getParam1();
		int value = instruction.getParam2();
		int mapVal = indexMap.get(key);
		indexMap.put(key, value+mapVal);
	}

	/**
	 * compares two pops and returns if less than equal to.
	 * @param instruction opcode value
	 * @return true if less than or equal to
	 */
	private boolean if_icmple(Instruction instruction){
		int a = stack.pop();
		int b = stack.pop();
		if (b <= a) {
			goTo(instruction);
			return true;
		}

		return false;
	}

	/**
	 * if b > a returns true.
	 * @param instruction opcode value
	 * @return boolean
	 */
	private boolean if_icmpgt(Instruction instruction){
		int a = stack.pop();
		int b = stack.pop();

		if (b > a) {
			goTo(instruction);
			return true;
		}
		return false;
	}

	/**
	 * if b >= a returns true.
	 * @param instruction opcode value
	 * @return boolean
	 */
	private boolean if_icmpge(Instruction instruction){
		int a = stack.pop();
		int b = stack.pop();

		if (b >= a) {
			goTo(instruction);
			return true;
		}
		return false;
	}

	/**
	 * if b < a returns true.
	 * @param instruction opcode value
	 * @return boolean
	 */
	private boolean if_icmplt(Instruction instruction){
		int a = stack.pop();
		int b = stack.pop();

		if (b < a) {
			goTo(instruction);
			return true;
		}
		return false;
	}

	/**
	 * if b == a.
	 * @param instruction opcode value
	 * @return boolean
	 */
	private boolean if_icmpeq(Instruction instruction){
		int a = stack.pop();
		int b = stack.pop();

		if (b == a) {
			goTo(instruction);
			return true;
		}
		return false;
	}

	/**
	 * if b != a.
	 * @param instruction opcode value
	 * @return boolean
	 */
	private boolean if_icmpne(Instruction instruction){
		int a = stack.pop();
		int b = stack.pop();

		if (b != a) {
			goTo(instruction);
			return true;
		}
		return false;
	}

	/**
	 * if the top of the stack !=0.
	 * @param instruction opcode value
	 * @return boolean
	 */
	private boolean ifne(Instruction instruction){
		if (stack.pop()!=0) {
			goTo(instruction);
			return true;
		}
		return false;
	}

	/**
	 * add the top two values from stack.
	 */
	private void iadd(){
		if(stack.getSize()<2){
			System.err.println("Stack has too few values to call iAdd()");
			return;
		}
		stack.push(stack.pop() + stack.pop());
	}

	/**
	 * subtracts top two values from the stack.
	 */
	private void isub(){
		if(stack.getSize()<2){
			System.err.println("Stack has too few values to call iSub()");
			return;
		}

		int a = stack.pop();
		int b = stack.pop();

		stack.push(b-a);
	}

	/**
	 * multiplies top two values from stack.
	 */
	private void imult(){
		if(stack.getSize()<2){
			System.err.println("Stack has too few values to call iMult()");
			return;
		}
		stack.push(stack.pop() * stack.pop());
	}

	/**
	 * divides top two values from stack.
	 */
	private void idiv(){
		if(stack.getSize()<2){
			System.err.println("Stack has too few values to call iDiv()");
			return;
		}

		int a = stack.pop();
		int b = stack.pop();

		stack.push(b/a);
	}

	/**
	 * modulos top two values from stack.
	 */
	private void irem(){
		if(stack.getSize()<2){
			System.err.println("Stack has too few values to call iRem()");
			return;
		}

		int a = stack.pop();
		int b = stack.pop();

		stack.push(b%a);
	}

	/**
	 * instantiates instruction hashmap.
	 * @param list opcode list
	 */
	private void instantiateInstructionMap(LList<Instruction> list){
		Node<Instruction> pointer = list.getFirst();
		if(instructMap == null){instructMap = new HashMap<>(getListSize(list));}

		while(pointer!=null){
			instructMap.put(pointer.getValue().getOffset(), pointer);
			pointer = pointer.getNext();
		}
	}

	/**
	 * Traverses the list and returns the total number of items.
	 * @param list opcode list
	 * @return size of list
	 */
	private int getListSize(LList<Instruction> list){
		Node<Instruction> pointer = list.getFirst();
		int size = 0;
		while(pointer!=null){ size++; pointer = pointer.getNext(); }
		return size;
	}

	/**
	 * parses opcode variable from instruction.
	 * @param instruction opcode value
	 * @return Integer
	 */
	private int getOpcodeVariable(Instruction instruction){
		String code = instruction.getOpcode();
		String[] line = instruction.toString().trim().split(code.contains("if_")? " " : code.contains("_")? "_": " ");
		return Integer.valueOf(line[line.length-1]);
	}

	/**
	 * parses opcode from instruction.
	 * @param instruction opcode value
	 * @return String
	 */
	private String getOpcode(Instruction instruction){
		String code = instruction.getOpcode();
		return instruction.getOpcode().trim().split(code.contains("if_")? " " : code.contains("_")? "_": " ")[0];
	}

	/**
	 * fetches node pointer from instruction hash map.
	 * @param instruction opcode value
	 * @return Node
	 */
	private Node<Instruction> getNode(Instruction instruction){
		return instructMap.get(getOpcodeVariable(instruction));
	}

	/**
	 * Main.
	 * @param args argue with me
	 */
	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("Usage: java Interpreter [filename]");
			System.exit(0);
		}

		try {
			LList<Instruction> input = readFile("src/Inputs_phase2/Input6.txt");
			new Interpreter().evaluateInstructions(input);
		} catch(IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		
		/*
		// to test the readFile() method, do something similar to below
		//src/Inputs_phase1/Input0.txt
		try {
			LList<Instruction> input1 = readFile("./Inputs_part1/Input0.txt");
			if (input1.listToString().equals("0: iconst_1  1: iconst_2  2: iadd  3: print  6: return")) {
				System.out.println("Yay1");
			}else {
				System.out.println("ERRRRR");
			}
		}
		catch(IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		*/

	}
}