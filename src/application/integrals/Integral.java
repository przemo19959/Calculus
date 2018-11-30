package application.integrals;

import application.converter.FormulaConverter;
import application.converter.LateXConverter;

public class Integral {
	private int n;
	private float rectangleWidth;
	private FormulaConverter conv;
	
	public Integral(FormulaConverter conv) {
		this.conv = conv;
	}

	public Integral withN(int n) {
		this.n=n;
		return this;
	}

	// integration algorithm -> rectangle method, form a to b
	public float rectangleMethod(float a, float b, String formula) {
//		formula=conv2.processLateXFormula(formula);
		rectangleWidth = (b - a) / n;
		float result = 0;
		float argument = 0;
		float height = 0;
		for (int i = 0; i <= n; i++) {
			argument = a + (i - 1) * rectangleWidth;
			try {
				height = Float.valueOf(
						conv.processFormula(formula.replace("x", (Math.abs(argument) < 1e-3) ? 0 + "" : argument + ""))
								.replace(",", "."));
			} catch (NumberFormatException nfe) {
				return 0;
			}
			result += rectangleWidth * height;
		}
		return result;
	}
	
	public float simpsonRuleMethod(float a, float b, String formula) {
		rectangleWidth = (b - a) / n;
		float result=0;
		float value=0;
		float argument = 0;
		for(int i=0;i<=n;i++) {
			argument = a + i* rectangleWidth;
			try {
				value = Float.valueOf(
						conv.processFormula(formula.replace("x", (Math.abs(argument) < 1e-3) ? 0 + "" : argument + ""))
								.replace(",", "."));
//				System.out.println(value);
			} catch (NumberFormatException nfe) {
				return 0;
			}
			if(i==0 && i==n)
				result+=value;
			else if(i%2==0)
				result+=2*value;
			else
				result+=4*value;
				
		}
//		System.out.println("Result: "+result);
		return result*(rectangleWidth/3f);
	}
	
	public boolean floatEqualWithError(float a, float b, float error) {
		if(Math.abs(a-b)<error)
			return true;
		return false;
	}
	
	public static void main(String[] args) {
		FormulaConverter conv = new FormulaConverter();
		LateXConverter conv2=new LateXConverter(conv);
		Integral integral = new Integral(conv).withN(1000);
		System.out.println(integral.simpsonRuleMethod(-10, 3, conv2.processLateXFormula("2x")));
	}
}
