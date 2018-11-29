package application.converter;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormulaConverter {
	public static final Pattern numberToRight = Pattern.compile("(\\d+\\.\\d+|\\d+).*?"); // w takiej kolejnoœci, aby
																							// liczby float i int by³y
																							// poprawnie rozpoznawane
	public static final Pattern numberToLeft = Pattern.compile(".*?(\\d+\\.\\d+|\\d+)");
	public static final Pattern operatorToRight = Pattern.compile("(\\+|\\-|\\*|/|%|\\^).*?");
	public static final Pattern operatorToLeft = Pattern.compile(".*?(\\+|\\-|\\*|/|%|\\^)");
	public static final Pattern functionToRight = Pattern.compile("(cos|sin|tan|max|exp|ln|log).*?");
	public static final Pattern functionToLeft = Pattern.compile(".*?(cos|sin|tan|max|exp|ln|log)");

	private Hashtable<String, Integer> priorities = new Hashtable<>();

	public FormulaConverter() {
		priorities.put("(", 0);

		priorities.put("+", 1);
		priorities.put("-", 1);
		priorities.put(")", 1);

		priorities.put("*", 2);
		priorities.put("/", 2);
		priorities.put("%", 2);

		priorities.put("^", 3);
	}

	public String prepareFormula(String stringFormula) {
		if (stringFormula.startsWith("-")) {
			stringFormula = "0" + stringFormula;
		}
		stringFormula = stringFormula.replace("-(-", "+(");
		stringFormula = stringFormula.replace("*(-", "*(0-");
		stringFormula = stringFormula.replace("(-", "(0-");
		return stringFormula;
	}

	// shunting-yard implementation for infix expression
	public String[] classicToRPN(String stringFormula) {
		if (!stringFormula.matches(".*(\\d|\\))"))
			return new String[] {};
		Queue<String> output = new LinkedList<>();
		Deque<String> stack = new ArrayDeque<>();
		Matcher m;
		while (stringFormula.length() > 0) { // dopóki nie przeczytano ca³ego wyra¿enia
			if ((m = numberToRight.matcher(stringFormula)).matches()) { // jeœli liczba
				output.add(m.group(1)); // dodaj liczbê wraz ze znakiem
				stringFormula = stringFormula.substring(m.group(1).length());
			} else if ((m = functionToRight.matcher(stringFormula)).matches()) { // jeœli funkcja
				stack.push(m.group(1));
				stringFormula = stringFormula.substring(m.group(1).length());
			} else if (stringFormula.startsWith(",")) { // jeœli przecinek, oddzielaj¹cy argumenty funkcji
				while (!stack.isEmpty() && !stack.peek().equals("("))
					output.add(stack.pop());
				stringFormula = stringFormula.substring(1);
			} else if ((m = operatorToRight.matcher(stringFormula)).matches()) { // jeœli operator
				while (!stack.isEmpty() && priorities.containsKey(stack.peek())
						&& priorities.get(stack.peek()) >= priorities.get(m.group(1)) && !m.group(1).equals("("))
					output.add(stack.pop());
				stack.push(m.group(1));
				stringFormula = stringFormula.substring(1);
			} else if (stringFormula.startsWith("(")) {
				stack.push("(");
				stringFormula = stringFormula.substring(1);
			} else if (stringFormula.startsWith(")")) {
				while (!stack.peek().equals("("))
					output.add(stack.pop());
				stack.pop();
				if (!stack.isEmpty() && stack.peek().matches(functionToRight.pattern()))
					output.add(stack.pop());
				stringFormula = stringFormula.substring(1);
			} else
				return new String[] {};
		}
		while (!stack.isEmpty())
			output.add(stack.pop()); // zdejmij pozosta³e operatory

		String[] result = new String[output.size()];
		return output.toArray(result);
	}

	public String calculateInRPN(String[] RPNString) {
		if (RPNString.length == 0)
			return "Syntax Error";
		float result = 0;
		Deque<Float> numbers = new ArrayDeque<>(); // stos liczb
		for (int i = 0; i < RPNString.length; i++) { // dla wszystkich symboli
			try {
				if (RPNString[i].matches(numberToRight.pattern()))
					numbers.push(Float.valueOf(RPNString[i])); // liczba na stos
				else if (RPNString[i].matches(operatorToRight.pattern())) {
					result = numbers.pop(); // pobierz operand A
					switch (RPNString[i]) { // wykonaj operacjê B operator A
					case "+":
						result = numbers.pop() + result;
						break;
					case "-":
						result = numbers.pop() - result;
						break;
					case "*":
						result = numbers.pop() * result;
						break;
					case "/":
						result = numbers.pop() / result;
						break;
					case "^":
						result = (float) Math.pow(numbers.pop(), result);
						break;
					case "%":
						result = numbers.pop() % result;
						break;
					}
					numbers.push(result); // wynik dzia³ania na stos
				} else if (RPNString[i].matches(functionToRight.pattern())) {
					switch (RPNString[i]) {
					case "cos":
						result = (float) Math.cos(numbers.pop());
						break;
					case "sin":
						result = (float) Math.sin(numbers.pop());
						break;
					case "tan":
						result = (float) Math.tan(numbers.pop());
						break;
					case "max":
						result = Math.max(numbers.pop(), numbers.pop());
						break;
					case "exp":
						result = (float) Math.exp(numbers.pop());
						break;
					case "ln":
						result = (float) Math.log(numbers.pop());
						break;
					case "log":
						result = log(numbers.pop(),numbers.pop());
						break;
					}
					numbers.push(result); // wynik dzia³ania na stos
				}
			} catch (NoSuchElementException nsee) {
				return "Syntax Error";
			}
		}
		return String.format("%.5f", numbers.pop());
	}
	
	//funkcja pomocnicza, do liczenia logab
	private float log(float value, float base ) {
		return (float)(Math.log10(value)/Math.log10(base));
	}

	public String processFormula(String formulaString) {
		return calculateInRPN(classicToRPN(prepareFormula(formulaString)));
	}

	// //na potrzeby testów z debuggerem
	public static void main(String[] args) {
		FormulaConverter conv = new FormulaConverter();
		System.out.println(conv.processFormula("2+log(2,2)"));
	}
}
