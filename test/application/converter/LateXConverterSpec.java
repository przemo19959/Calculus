package application.converter;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import application.integrals.Integral;

class LateXConverterSpec {
	private FormulaConverter conv1=new FormulaConverter();
	private LateXConverter conv=new LateXConverter(conv1);
	private Integral mockIntegral=new Integral(conv1);

	@Test
	@DisplayName("bracket replacement works fine")
	void test1() {
		String input="{3.5+5}";
		assertThat(conv.processLateXFormula(input)).isEqualTo("(3.5+5)");
	}
	
	@Test
	@DisplayName("bracket replacement works fine")
	void test2() {
		String input="{3.5+\\frac{5+5}{23}-\\frac{4.5-2}{67}}";
		assertThat(conv.processLateXFormula(input)).isEqualTo("(3.5+((5+5)/(23))-((4.5-2)/(67)))");
	}
	
	@Test
	@DisplayName("multiply with bracket works fine")
	void test3() {
		String input="{3.5+4.5(34+45)-2^{2(2*6)}}";
		assertThat(conv.processLateXFormula(input)).isEqualTo("(3.5+4.5*(34+45)-2^(2*(2*6)))");
	}
	
	@Test
	@DisplayName("multiply with variable x works fine")
	void test4() {
		String input="4.5x+45x^{2}-2(28+x)";
		assertThat(conv.processLateXFormula(input)).isEqualTo("4.5*(x)+45*(x)^(2)-2*(28+(x))");
	}
	
	@Test
	@DisplayName("multiply with variable x works fine test 2")
	void test5() {
		String input="2x";
		assertThat(conv.processLateXFormula(input)).isEqualTo("2*(x)");
	}
	
	@Test
	@DisplayName("multiply with variable x works fine test 3")
	void test6() {
		String input="xcosx";
		assertThat(conv.processLateXFormula(input)).isEqualTo("(x)*cos(x)");
	}
	
	@Test
	@DisplayName("conversion works fine with exp function")
	void test7() {
		String input="xcosx+exp(23)";
		assertThat(conv.processLateXFormula(input)).isEqualTo("(x)*cos(x)+exp(23)");
	}
	
	@Test
	@DisplayName("conversion works fine with log function")
	void test8() {
		String input="log_{2}{2+x}";
		assertThat(conv.processLateXFormula(input)).isEqualTo("log(2,2+(x))");
	}
	
	@Test
	@DisplayName("conversion works fine with log function test 2")
	void test9() {
		String input="45x+log_{2}{2+x}+xcosx";
		assertThat(conv.processLateXFormula(input)).isEqualTo("45*(x)+log(2,2+(x))+(x)*cos(x)");
	}
	
	@Test
	@DisplayName("conversion works fine with integral test 1")
	void test10() {
		String integral="\\int_{2}^{70}(x^2)dx";
		float value=Float.valueOf(conv.processLateXFormula(integral));
		assertThat(mockIntegral.floatEqualWithError(value,114330.66f,0.01f));	//sprawdzenie czy ca³ka jest dobrze policzona
		integral="23x+"+integral+"+x";
		assertThat(conv.processLateXFormula(integral)).isEqualTo("23*(x)+"+value+"+(x)");	//sprawdzenie czy dla ca³ego równania mamy dobry wynik
	}

}
