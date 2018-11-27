package application.converter;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LateXConverterSpec {
	private LateXConverter conv=new LateXConverter();

	@Test
	@DisplayName("bracket replacement works fine")
	void test1() {
		String input="{3.5+5}";
		assertThat(conv.processLateXFormula(input)).isEqualTo("(3.5+5)");
	}
	
	@Test
	@DisplayName("bracket replacement works fine")
	void test2() {
		String input="{3.5+\frac{5+5}{23}-\frac{4.5-2}{67}}";
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

}
