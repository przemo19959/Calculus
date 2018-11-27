package application.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LateXConverter {
	private Pattern fracSyntax = Pattern.compile(".*\\frac\\{([^}]*)}\\{([^}]*)}.*");
	private Pattern mulSyntax = Pattern.compile(".*(\\d|x)\\(.*");
	private Pattern mulSyntax2 = Pattern.compile(".*(\\d)x.*");

	public String processLateXFormula(String formula) {
		String result = formula;
		Matcher m = null;
		result = fracSyntaxConversion(result, m);
		result = bracketConversion(result);
		result = multiplyConversion(result, m);
		return result;
	}

	// konwersja ...2(... ==> ...2*(... + ...2x(... ==> ...2*x*(...
	private String multiplyConversion(String input, Matcher m) {
		input = exchanger(m, mulSyntax, "(", input);
		input = exchanger(m, mulSyntax2, "x", input);
		input = input.replace("x", "(x)"); // zmienne, dodatkowo otocz nawiasami, aby potem dodaæ 0- i móc obliczyæ wartoœæ
		return input;
	}

	private String exchanger(Matcher m, Pattern pattern, String sign, String input) {
		String tmp = "";
		while ((m = pattern.matcher(input)).matches()) {
			tmp = m.group(1)+ "*"+ sign;
			input = input.replace(m.group(1)+ sign, tmp);
		}
		return input;
	}

	// konwersja ...\frac{...}{...}... ==> ...((...)/(...))...
	private String fracSyntaxConversion(String input, Matcher m) {
		String tmp = "";
		while ((m = fracSyntax.matcher(input)).matches()) {
			tmp = "(("+ m.group(1)+ ")/("+ m.group(2)+ "))";
			input = input.replace("\frac{"+ m.group(1)+ "}{"+ m.group(2)+ "}", tmp);
		}
		return input;
	}

	// konwersja {} ==>()
	private String bracketConversion(String input) {
		return input.replace("{", "(").replace("}", ")");
	}

	// tylko do testów
	public static void main(String[] args) {
		LateXConverter lateXConverter = new LateXConverter();
		lateXConverter.processLateXFormula("4.5x+45x^{2}-2(28+x)");
	}
}
