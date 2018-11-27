package application.integrals;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import application.converter.FormulaConverter;

class IntegralSpec {
	private FormulaConverter conv=new FormulaConverter();
	private Integral integral=new Integral(conv).withN(100);

	@Test
	@DisplayName("integral return 0, when incorrect formula is given")
	void test1() {
		String formula="2x+";
		Assertions.assertThat(integral.rectangleMethod(0, 3, formula)).isEqualTo(0);
	}
	
	@Test
	@DisplayName("integral works fine test 1")
	void test2() {
		String formula="2x";
		Assertions.assertThat(integral.rectangleMethod(0, 3, formula)).isEqualTo(9);
	}

}
