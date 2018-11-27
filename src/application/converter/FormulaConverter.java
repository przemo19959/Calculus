package application.converter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormulaConverter {
	private Pattern number = Pattern.compile("(\\d+\\.\\d+|\\d+).*?"); // w takiej kolejnoœci, aby liczby float i int by³y poprawnie rozpoznawane
	private Pattern operator = Pattern.compile("(\\+|\\-|\\*|/|%|\\^).*?");
	private Pattern function = Pattern.compile("(cos|sin|tan|max|exp).*?");

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
		if(stringFormula.startsWith("-")) {
			stringFormula = "0"+ stringFormula;
		}
		stringFormula = stringFormula.replace("-(-", "+(");
		stringFormula = stringFormula.replace("*(-", "*(0-");
		stringFormula = stringFormula.replace("(-", "(0-");
		return stringFormula;
	}

	// shunting-yard implementation for infix expression
	public String[] classicToRPN(String stringFormula) {
		if(!stringFormula.matches(".*(\\d|\\))"))
			return new String[]{};
		Queue<String> output = new LinkedList<>();
		Deque<String> stack = new ArrayDeque<>();
		Matcher m;
		while (stringFormula.length()> 0) { // dopóki nie przeczytano ca³ego wyra¿enia
			if((m = number.matcher(stringFormula)).matches()) { // jeœli liczba
				output.add(m.group(1)); // dodaj liczbê wraz ze znakiem
				stringFormula = stringFormula.substring(m.group(1).length());
			} else if((m = function.matcher(stringFormula)).matches()) { // jeœli funkcja
				stack.push(m.group(1));
				stringFormula = stringFormula.substring(m.group(1).length());
			} else if(stringFormula.startsWith(",")) { // jeœli przecinek, oddzielaj¹cy argumenty funkcji
				while (!stack.isEmpty()&& !stack.peek().equals("("))
					output.add(stack.pop());
				stringFormula = stringFormula.substring(1);
			} else if((m = operator.matcher(stringFormula)).matches()) { // jeœli operator
				while (!stack.isEmpty()&& priorities.get(stack.peek())>= priorities.get(m.group(1))&& !m.group(1).equals("("))
					output.add(stack.pop());
				stack.push(m.group(1));
				stringFormula = stringFormula.substring(1);
			} else if(stringFormula.startsWith("(")) {
				stack.push("(");
				stringFormula = stringFormula.substring(1);
			} else if(stringFormula.startsWith(")")) {
				while (!stack.peek().equals("("))
					output.add(stack.pop());
				stack.pop();
				if(!stack.isEmpty()&& stack.peek().matches(function.pattern()))
					output.add(stack.pop());
				stringFormula = stringFormula.substring(1);
			} else
				return new String[]{};
		}
		while (!stack.isEmpty())
			output.add(stack.pop()); // zdejmij pozosta³e operatory
		
		String[] result = new String[output.size()];
		return output.toArray(result);
	}

	public String calculateInRPN(String[] RPNString) {
		if(RPNString.length== 0)
			return "Syntax Error";
		float result = 0;
		Deque<Float> numbers = new ArrayDeque<>(); // stos liczb
		for(int i = 0;i< RPNString.length;i++) { // dla wszystkich symboli
			if(RPNString[i].matches(number.pattern()))
				numbers.push(Float.valueOf(RPNString[i])); // liczba na stos
			else if(RPNString[i].matches(operator.pattern())) {
				result = numbers.pop(); // pobierz operand A
				try {
					switch (RPNString[i]) { // wykonaj operacjê B operator A
						case "+" :
							result = numbers.pop()+ result;
							break;
						case "-" :
							result = numbers.pop()- result;
							break;
						case "*" :
							result = numbers.pop()* result;
							break;
						case "/" :
							result = numbers.pop()/ result;
							break;
						case "^" :
							result = (float) Math.pow(numbers.pop(), result);
							break;
						case "%" :
							result = numbers.pop()% result;
							break;
					}
				} catch (NoSuchElementException nsee) {
					return "Syntax Error";
				}
				numbers.push(result); // wynik dzia³ania na stos
			} else if(RPNString[i].matches(function.pattern())) {
				switch (RPNString[i]) {
					case "cos" :
						result = (float) Math.cos(numbers.pop());
						break;
					case "sin" :
						result = (float) Math.sin(numbers.pop());
						break;
					case "tan" :
						result = (float) Math.tan(numbers.pop());
						break;
					case "max" :
						result = Math.max(numbers.pop(), numbers.pop());
						break;
					case "exp" :
						result = (float) Math.exp(numbers.pop());
						break;
				}
				numbers.push(result); // wynik dzia³ania na stos
			}
		}
		return String.format("%.5f", numbers.pop());
	}

	public String processFormula(String formulaString) {
		return calculateInRPN(classicToRPN(prepareFormula(formulaString)));
	}

	// //na potrzeby testów z debuggerem
	public static void main(String[] args) {
		FormulaConverter conv = new FormulaConverter();
		System.out.println(conv.processFormula("-4.5-2*(-22)"));
	}
}
