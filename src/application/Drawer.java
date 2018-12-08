package application;

import org.jfree.fx.FXGraphics2D;
import org.scilab.forge.jlatexmath.Box;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.VBox;

public class Drawer extends Canvas{
	private FXGraphics2D g2;
	private Box box;
	private TeXFormula formula;
	private TeXIcon icon;

	public Drawer(VBox container) {
		widthProperty().bind(container.widthProperty());
		setHeight(100);
		g2 = new FXGraphics2D(getGraphicsContext2D());
		g2.scale(20, 20);
		updateFormula("x=?");
	}
	
	public void updateFormula(String formulaString) {
		formula=new TeXFormula(formulaString);
		icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
		box = icon.getBox();
		draw();
	}

	private void draw() {
		getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());	//wyczyœæ canvas
		box.draw(g2, 1, 5);
	}	
}
