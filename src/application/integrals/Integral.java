package application.integrals;

import application.converter.FormulaConverter;

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
				System.out.println(height+", "+argument);
			} catch (NumberFormatException nfe) {
				return 0;
			}
			result += rectangleWidth * height;
		}
		return result;
	}
}
