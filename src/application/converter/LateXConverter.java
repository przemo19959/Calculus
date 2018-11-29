package application.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LateXConverter {
	private Pattern fracSyntax = Pattern.compile(".*\\frac\\{([^}]*)}\\{([^}]*)}.*");
	private Pattern mulSyntax = Pattern.compile(".*(\\d)\\(.*");
	private Pattern mulSyntax2 = Pattern.compile("(.*\\))([^\\+\\-\\*/\\^%\\)])(.*)");
	private Pattern logSyntax = Pattern.compile(".*log_\\{(.*)}\\{(.*)}.*");

	public String processLateXFormula(String formula) {
		String result = formula;
		Matcher m = null;
		result = fracSyntaxConversion(result, m);
		result = logSyntaxConversion(result, m);
		result = bracketConversion(result);
		result = multiplyConversion(result, m);
		return result;
	}

	// konwersja ...2(... ==> ...2*(... + ...2x(... ==> ...2*x*(...
	private String multiplyConversion(String input, Matcher m) {
		input = input.replace("x", "(x)");
		input = input.replace("e(x)p", "exp");
		input = input.replace("ma(x)", "max");
		input = exchanger(m, mulSyntax, "(", input);
		input = exchanger2(m, input);
		return input;
	}

	// konwersja wzorca pattern jakiemu jest poddany znak
	private String exchanger(Matcher m, Pattern pattern, String sign, String input) {
		String tmp = "";
		while ((m = pattern.matcher(input)).matches()) {
			tmp = m.group(1) + "*" + sign;
			input = input.replace(m.group(1) + sign, tmp);
		}
		return input;
	}

	// konwersja ...)... ==> ...)*... wed³ug wzorca mulSyntax2
	private String exchanger2(Matcher m, String input) {
		String tmp = "";
		while ((m = mulSyntax2.matcher(input)).matches()) {
			tmp = m.group(1) + "*" + m.group(2) + m.group(3);
			input = input.replace(m.group(1) + m.group(2) + m.group(3), tmp);
		}
		return input;
	}

	// konwersja ...\frac{...}{...}... ==> ...((...)/(...))...
	private String fracSyntaxConversion(String input, Matcher m) {
		String tmp = "";
		while ((m = fracSyntax.matcher(input)).matches()) {
			tmp = "((" + m.group(1) + ")/(" + m.group(2) + "))";
			input = input.replace("\frac{" + m.group(1) + "}{" + m.group(2) + "}", tmp);
		}
		return input;
	}

	// konwersja ...log_{...}{...}... ==> ...log(...,...)...
	private String logSyntaxConversion(String input, Matcher m) {
		String tmp = "";
		while ((m = logSyntax.matcher(input)).matches()) {
			tmp = "log(" + m.group(1) + "," + m.group(2) + ")";
			input = input.replace("log_{" + m.group(1) + "}{" + m.group(2) + "}", tmp);
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
		lateXConverter.processLateXFormula("xcosx");
	}
}
