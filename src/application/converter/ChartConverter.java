package application.converter;

import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

public class ChartConverter {
	private XYChart.Series<Number, Number> dataSeries1;
	private FormulaConverter conv;
	private NumberAxis xAxis;
	private int xRange;
	private int xLowerBound;
	private int xUpperBound;

	public ChartConverter(Series<Number, Number> dataSeries1, FormulaConverter conv) {
		this.dataSeries1 = dataSeries1;
		this.conv = conv;
	}
	
	private void changeChartSettings() {
		xAxis = (NumberAxis) dataSeries1.getChart().getXAxis();
		xLowerBound=(int)xAxis.getLowerBound();
		xUpperBound=(int)xAxis.getUpperBound();
		xRange = xUpperBound-xLowerBound;
	}

	public void processFormula(String formula) {
		if(!formula.equals("Syntax Error") && formula.matches(".*(\\d|\\)|x)")) {
			changeChartSettings();
			if(formula.matches("(|-)(\\d+\\,\\d+|\\d+)")) { // jeœli równanie zawiera sta³¹, przyk³adowo y=3
				processNumber(formula);
			} else if(formula.contains("x")) { // jeœli równanie zawiera zmienn¹, np. y=2x
				processFunction(formula);
			}
		}
	}
	
	//przetwórz formu³ê, jeœli jej wynik to liczba
	private void processNumber(String formula) {
		for(int i = xLowerBound;i< xUpperBound+1;i += (int) (xRange/ 10.0)) {
			dataSeries1.getData().add(new XYChart.Data<Number, Number>(i, Float.valueOf(formula.replace(",", "."))));
		}
	}
	
	//jeœli formu³a zawiera zmienn¹ x
	private void processFunction(String formula) {
		float argument=0;
		float value=0;
		for(float i = xLowerBound;i< xUpperBound+1;i += (xRange/ 120.0)) {
			argument=(Math.abs(i)< 1e-3) ? 0 :i;
			try {
				value = Float.valueOf(conv.processFormula(formula.replace("x", argument+"")
						.replace("e"+argument+"p", "exp").replace("ma"+argument, "max")).replace(",", "."));
			} catch (NumberFormatException nfe) {
				dataSeries1.getData().clear();
				break;
			}
			dataSeries1.getData().add(new XYChart.Data<Number, Number>(i, value));
		}
	}
}
