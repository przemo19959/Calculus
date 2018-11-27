package application.converter;

import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

public class ChartConverter {
	private XYChart.Series<Number, Number> dataSeries1;
	private FormulaConverter conv;

	public ChartConverter(Series<Number, Number> dataSeries1, FormulaConverter conv) {
		this.dataSeries1 = dataSeries1;
		this.conv = conv;
	}

	public void processFormula(String formula) {
		if(!formula.equals("Syntax Error")&& !formula.matches(".*(\\+|\\-|\\*|/|%|\\^|\\(|\\,|\\.)")) {
			NumberAxis axis = (NumberAxis) dataSeries1.getChart().getXAxis();
			int xRange = (int) (axis.getUpperBound()- axis.getLowerBound());
			float value = 0;
			if(formula.matches("(\\d+\\,\\d+|\\d+)")) { // jeœli równanie zawiera sta³¹, przyk³adowo y=3
				for(int i = (int) axis.getLowerBound();i< (int) (axis.getUpperBound()+ 1);i += (int) (xRange/ 10.0)) {
					dataSeries1.getData().add(new XYChart.Data<Number, Number>(i, Float.valueOf(formula.replace(",", "."))));
				}
			} else if(formula.contains("x")) { // jeœli równanie zawiera zmienn¹, np. y=2x
				for(float i = (float) axis.getLowerBound();i< (float) axis.getUpperBound();i += (xRange/ 120.0)) {
					try {
						value = Float.valueOf(conv.processFormula(formula.replace("x", (Math.abs(i)< 1e-3) ? 0+ "" : i+ "")).replace(",", "."));
					} catch (NumberFormatException nfe) {
						dataSeries1.getData().clear();
						break;
					}
					dataSeries1.getData().add(new XYChart.Data<Number, Number>(i, value));
				}
			}
		}
	}
}
