package application.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import application.integrals.Integral;

public class LateXConverter {
	@SuppressWarnings("unused")
	private FormulaConverter converter;
	private Pattern fracSyntax = Pattern.compile(".*\\\\frac\\{([^}]*)}\\{([^}]*)}.*");
	private Pattern mulSyntax = Pattern.compile(".*(\\d)\\(.*");
	private Pattern mulSyntax2 = Pattern.compile("(.*\\))([^\\+\\-\\*/\\^%\\)])(.*)");
	private Pattern logSyntax = Pattern.compile(".*log_\\{(.*)}\\{(.*)}.*");
	private Pattern integralSyntax = Pattern.compile(".*\\\\int_\\{(.*)}\\^\\{(.*)}\\((.*)\\)dx.*");

	private Integral integralCalc;

	public LateXConverter(FormulaConverter converter) {
		this.converter = converter;
		integralCalc = new Integral(converter).withN(1000);
	}

	public String processLateXFormula(String formula) {
		String result = formula;
		Matcher m = null;
		result = fracSyntaxConversion(result, m);
		result = logSyntaxConversion(result, m);
		result = integralSyntaxConversion(result, m);
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
			input = input.replace("\\frac{" + m.group(1) + "}{" + m.group(2) + "}", tmp);
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

	// konwersja ...\int_{a}^{b} x^2 dx... ==> ...int(a,b,x^2)... zwracana jest ju¿ konkretna wartoœæ ca³ki
	private String integralSyntaxConversion(String input, Matcher m) {
		float tmp = 0;
		while ((m = integralSyntax.matcher(input)).matches()) {
			try {
				tmp = integralCalc.simpsonRuleMethod(Float.valueOf(m.group(1)), Float.valueOf(m.group(2)), processLateXFormula(m.group(3)));
			} catch (NumberFormatException nfe) {
				return "Syntax Error";
			}
			input = input.replace("\\int_{" + m.group(1) + "}^{" + m.group(2) + "}(" + m.group(3) + ")dx", tmp + "");
		}
		return input;
	}

	// konwersja {} ==>()
	private String bracketConversion(String input) {
		return input.replace("{", "(").replace("}", ")");
	}

	// tylko do testów
	public static void main(String[] args) {
		LateXConverter lateXConverter = new LateXConverter(new FormulaConverter());
		lateXConverter.processLateXFormula("{3.5+\frac{5+5}{23}-\frac{4.5-2}{67}}");
	}
}
