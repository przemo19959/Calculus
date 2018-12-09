package application;

import java.util.regex.Pattern;

import org.hamcrest.core.IsInstanceOf;
import org.scilab.forge.jlatexmath.FractionAtom;

import application.converter.ChartConverter;
import application.converter.FormulaConverter;
import application.converter.LateXConverter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class SampleController {
	@FXML
	private TextField formulaField;
	@FXML
	BorderPane mainPane;
	@FXML
	Slider scaleSlider;
	@FXML
	ScrollPane canvasPane;
	private Drawer drawer;
	@FXML
	VBox bigBox;
	@FXML
	Label resultLabel;
	private FormulaConverter converter = new FormulaConverter();
	private LateXConverter converter2 = new LateXConverter(converter);
	private XYChart.Series<Number, Number> dataSeries1 = new Series<>();
	@FXML
	LineChart<Number, Number> chart;

	private ChartConverter converter3 = new ChartConverter(dataSeries1, converter);
	@FXML
	NumberAxis xAxis;
	@FXML
	NumberAxis yAxis;
	private Tooltip info = new Tooltip();
	private Stage stage;
	private Pattern function = Pattern.compile("cos|sin|tan|max|exp|ln|log");
	@FXML Button integralButton;
	@FXML GridPane buttonsGrid;
	@FXML Button fractionButton;

	public void init(Stage stage) {
		this.stage = stage;
	}

	private void updateAxis(int delta) {
		if(xAxis.getUpperBound()< 10)
			delta = (delta< 0) ? -1 : 1;
		xAxis.setUpperBound(xAxis.getUpperBound()+ delta);
		xAxis.setLowerBound(xAxis.getLowerBound()- delta);
		yAxis.setUpperBound(yAxis.getUpperBound()+ delta);
		yAxis.setLowerBound(yAxis.getLowerBound()- delta);
		xAxis.setTickUnit((xAxis.getUpperBound()- xAxis.getLowerBound())/ 10);
		yAxis.setTickUnit((yAxis.getUpperBound()- yAxis.getLowerBound())/ 10);
	}

	@FXML
	private void initialize() {
		drawer = new Drawer(bigBox);
		bigBox.getChildren().add(drawer);
		chart.getData().add(dataSeries1);
		Tooltip.install(dataSeries1.getNode(), info);
		
		addEventHandlers();
		
		//obs³uga pokazywania chmurki ze wspó³rzêdnymi funkcji
		dataSeries1.getNode().setOnMouseMoved(ev -> {
			showPointInfo(ev);
		});

		//obs³uga wy³aczenia chmurki ze wspó³rzêdnymi
		dataSeries1.getNode().setOnMouseExited(val -> {
			if(info.isShowing())
				info.hide();
		});

		chart.setOnScroll(val -> {
			// //Ka¿dy krok kó³eczka to 40 jednostek, w dó³ krêc¹c mamy wartoœæ ujemn¹, a górê wartoœæ dodatni¹
			if(val.getDeltaY()< 0)
				updateAxis(10);
			else
				updateAxis(-10);
			dataSeries1.getData().clear();
			converter3.processFormula(converter2.processLateXFormula(formulaField.getText()));	//aktualizuj, tak, ¿e wykres jest liczony od nowa, gdy zakres x siê zmnienia
		});

		drawer.widthProperty().bind(bigBox.prefWidthProperty());
		drawer.heightProperty().bind(bigBox.prefHeightProperty());

		formulaField.textProperty().addListener((obs, old, newVal) -> {
			drawer.updateFormula(newVal);	//aktualizacja p³ótna ze wzorem LateX
			dataSeries1.getData().clear();
			updateCalculation(newVal);
		});
		
		fractionButton.setOnAction(val->{
			formulaField.setText(formulaField.getText()+"\\frac{1}{1}");
		});
		
		integralButton.setOnAction(val->{
			formulaField.setText(formulaField.getText()+"\\int_{0}^{1}(x)dx");
		});

	}
	
	//wyœwietlanie chmurki ze wspó³rzêdnymi przebeigu funkcji
	private void showPointInfo(MouseEvent ev) {
		if(info.isShowing())
			info.hide();
		Point2D mouseSceneCoords = new Point2D(ev.getSceneX(), ev.getSceneY());
		double x = xAxis.sceneToLocal(mouseSceneCoords).getX();
		double y = yAxis.sceneToLocal(mouseSceneCoords).getY();
		// System.out.println(xAxis.getValueForDisplay(x)+", "+yAxis.getValueForDisplay(y));
		info.setText("("+ String.format("%.2f", xAxis.getValueForDisplay(x))+ ","+ String.format("%.2f", yAxis.getValueForDisplay(y))+ ")");
		info.show(stage);
	}
	
	//przetwórz równanie wpisane do pola tekstowego
	private void updateCalculation(String formulaFieldContent) {
		if(!formulaFieldContent.equals("")) {
			String tmp = converter2.processLateXFormula(formulaFieldContent);	//przerwórz wed³ug konwertera latexa
			String tmp2 = tmp.replaceAll(function.pattern(), "");	//niektóre funkcje posiadaj¹ w sobie znak x, jak np. exp
			if(tmp2.contains("x")) {	//jeœli rzeczywiœcie równanie zawiera zmienn¹ x
				resultLabel.setText(tmp);
				converter3.processFormula(tmp);
			} else {
				tmp2 = converter.processFormula(tmp);
				resultLabel.setText("= "+ tmp2);
				converter3.processFormula(tmp2);
			}
		} else
			resultLabel.setText("");
	}
	
	private void addEventHandlers() {
		Button tmp=null;
		Node nodeTemp=null;
		for(int i=0;i<buttonsGrid.getChildren().size();i++) {
			nodeTemp=buttonsGrid.getChildren().get(i);
			if(nodeTemp instanceof Button) {
				tmp=(Button)nodeTemp;
				String value=tmp.getText();
				numbersButtonsHandlers(tmp, value);
			}
		}
	}
	
	private void numbersButtonsHandlers(Button button, String value) {
		if(value.matches("[\\d\\.\\+\\-\\*/%\\^]")) {
			addToFormulaField(button, value);
		}else if(value.equals("DEL")) {
			button.setOnAction(ev->formulaField.clear());
		}else if(value.equals("OFF")) {
			button.setOnAction(ev->Platform.exit());
		}
	}
	
	private void addToFormulaField(Button button,String item) {
		button.setOnAction(ev->formulaField.setText(formulaField.getText()+item));
	}
}
