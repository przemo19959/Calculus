package application;

import application.converter.ChartConverter;
import application.converter.FormulaConverter;
import application.converter.LateXConverter;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

public class SampleController {
	@FXML
	private TextField formulaField;
	@FXML BorderPane mainPane;
	@FXML Slider scaleSlider;
	@FXML ScrollPane canvasPane;
	private Drawer drawer;
	@FXML VBox bigBox;
	@FXML Label resultLabel;
	private FormulaConverter converter=new FormulaConverter();
	private LateXConverter converter2=new LateXConverter();
	private XYChart.Series<Number, Number> dataSeries1=new Series<>();
	@FXML LineChart<Number,Number> chart;
	
	private ChartConverter converter3=new ChartConverter(dataSeries1,converter);
	@FXML NumberAxis xAxis;
	@FXML NumberAxis yAxis;
	private Tooltip info=new Tooltip();
	private Stage stage;
	
	public void init(Stage stage) {
		this.stage=stage;
	}
	
	private void updateAxis(int delta) {
		if(xAxis.getUpperBound()<10)
			delta=(delta<0)?-1:1;
		xAxis.setUpperBound(xAxis.getUpperBound()+delta);
		xAxis.setLowerBound(xAxis.getLowerBound()-delta);
		yAxis.setUpperBound(yAxis.getUpperBound()+delta);
		yAxis.setLowerBound(yAxis.getLowerBound()-delta);
		xAxis.setTickUnit((xAxis.getUpperBound()-xAxis.getLowerBound())/10);
		yAxis.setTickUnit((yAxis.getUpperBound()-yAxis.getLowerBound())/10);
	}
	
	@FXML
	private void initialize() {
		drawer=new Drawer(bigBox);
		bigBox.getChildren().add(drawer);
		chart.getData().add(dataSeries1);
		Tooltip.install(dataSeries1.getNode(), info);
		
		dataSeries1.getNode().setOnMouseMoved(val->{
			if(info.isShowing())
				info.hide();
			Point2D mouseSceneCoords = new Point2D(val.getSceneX(), val.getSceneY());
		    double x = xAxis.sceneToLocal(mouseSceneCoords).getX();
		    double y = yAxis.sceneToLocal(mouseSceneCoords).getY();
//		    System.out.println(xAxis.getValueForDisplay(x)+", "+yAxis.getValueForDisplay(y));
		    info.setText("y"+resultLabel.getText()+"\n("+String.format("%.2f", xAxis.getValueForDisplay(x))+","+String.format("%.2f", yAxis.getValueForDisplay(y))+")");
		    info.show(stage);
		});
		
		dataSeries1.getNode().setOnMouseExited(val->{
			if(info.isShowing())
				info.hide();
		});
		
		chart.setOnScroll(val->{
//			//Ka¿dy krok kó³eczka to 40 jednostek, w dó³ krêc¹c mamy wartoœæ ujemn¹, a górê wartoœæ dodatni¹
			if(val.getDeltaY()<0) {
				updateAxis(10);
			}else
				updateAxis(-10);
		});
		
		
		
//		canvas.widthProperty().bind(formulaBox.prefWidthProperty()); 
//      canvas.heightProperty().bind(formulaBox.prefHeightProperty()); 
        
        formulaField.textProperty().addListener((obs,old,newVal)->{
        	drawer.updateFormula(newVal);
        	dataSeries1.getData().clear();
        	if(!newVal.equals("") && !newVal.matches(".*[a-z].*")) {
        		String tmp=converter.processFormula(converter2.processLateXFormula(newVal));
        		resultLabel.setText("= "+tmp);
        		converter3.processFormula(tmp);
        	}else if(newVal.matches(".*[a-z].*")){	//jeœli równanie zawiera zmienne a-z
        		resultLabel.setText(newVal);
        		converter3.processFormula(converter2.processLateXFormula(newVal));
        	}else
        		resultLabel.setText("");
        });
//        bigBox.widthProperty().addListener((obs,old,newVal)->{
//        	System.out.println(newVal);
//        });
        
	}
}
