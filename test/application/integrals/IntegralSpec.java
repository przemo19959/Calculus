package application.integrals;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import application.converter.FormulaConverter;
import application.converter.LateXConverter;

class IntegralSpec {
	private FormulaConverter conv = new FormulaConverter();
	private LateXConverter conv2=new LateXConverter();
	private Integral integral = new Integral(conv).withN(1000);
	private String formula;
	private float result;
	
	@Test
	@DisplayName("integral return 0, when incorrect formula is given")
	void test1() {
		formula = conv2.processLateXFormula("2x+");
		Assertions.assertThat(integral.rectangleMethod(0, 3, formula)).isEqualTo(0);
	}

	@Test
	@DisplayName("integral works fine (rectangle method) test 1")
	void test2() {
		formula = conv2.processLateXFormula("2x");
		result=integral.rectangleMethod(0, 3, formula);
//		System.out.println(result);
		Assertions.assertThat(integral.floatEqualWithError(result, 9, 0.01f)).isEqualTo(true);
	}
	
	@Test
	@DisplayName("integral works fine (rectangle method) test 2")
	void test3() {
		formula = conv2.processLateXFormula("2x");
		result=integral.rectangleMethod(-10, 3, formula);	//dla przedzia³u równym 13 wymaganych jest a¿ 10000 prostok¹tów, aby osi¹gn¹æ b³¹d mniejszy ni¿ 0.01
//		System.out.println(result);
		Assertions.assertThat(integral.floatEqualWithError(result, -91, 0.5f)).isEqualTo(true);
	}
	
	@Test
	@DisplayName("integral works fine (rectangle method) test 3")
	void test4() {
		formula = conv2.processLateXFormula("1/(x+4)");
		result=integral.rectangleMethod(0, 2, formula);	//dla przedzia³u równym 13 wymaganych jest a¿ 10000 prostok¹tów, aby osi¹gn¹æ b³¹d mniejszy ni¿ 0.01
//		System.out.println(result);
		Assertions.assertThat(integral.floatEqualWithError(result, 0.405f, 0.01f)).isEqualTo(true);
	}
	
	@Test
	@DisplayName("integral works fine (simpson rule) test 1")
	void test5() {
		formula = conv2.processLateXFormula("2x");
		result=integral.simpsonRuleMethod(0, 3, formula);
//		System.out.println(result);
		Assertions.assertThat(integral.floatEqualWithError(result, 9, 0.01f)).isEqualTo(true);
	}
	
	@Test
	@DisplayName("integral works fine (simpson rule) test 2")
	void test6() {
		formula = conv2.processLateXFormula("2x");
		result=integral.simpsonRuleMethod(-10, 3, formula);		//dla funkcji 2x, metoda ta zapewnia ponad 7 razy lepsz¹ dok³adnoœæ dla tej samej iloœci n
//		System.out.println(result);
		Assertions.assertThat(integral.floatEqualWithError(result, -91, 0.07f)).isEqualTo(true);
	}

}
