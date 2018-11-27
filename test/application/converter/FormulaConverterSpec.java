package application.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FormulaConverterSpec {
	private FormulaConverter converter=new FormulaConverter();

	@Test
	@DisplayName("adding works fine")
	void test1() {
		String input="2+2";
		assertThat(converter.processFormula(input)).isEqualTo("4,00000");
	}
	
	@Test
	@DisplayName("adding many elements works fine")
	void test2() {
		String input="2+22+643+231";
		assertThat(converter.processFormula(input)).isEqualTo("898,00000");
	}
	
	@Test
	@DisplayName("substracting works fine")
	void test3() {
		String input="22-872";
		assertThat(converter.processFormula(input)).isEqualTo("-850,00000");
	}
	
	@Test
	@DisplayName("substracting many elements works fine")
	void test4() {
		String input="24-22-643-231";
		assertThat(converter.processFormula(input)).isEqualTo("-872,00000");
	}
	
	@Test
	@DisplayName("add/substract works fine - in order in which elements are")
	void test5() {
		String input="24+22-643+231";
		assertThat(converter.processFormula(input)).isEqualTo("-366,00000");
	}
	
	@Test
	@DisplayName("multipling works fine")
	void test6() {
		String input="23*7*998";
		assertThat(converter.processFormula(input)).isEqualTo("160678,00000");
	}
	
	@Test
	@DisplayName("add/substract/muliply works fine - in order in which elements are")
	void test7() {
		String input="24*22-643+231";
		assertThat(converter.processFormula(input)).isEqualTo("116,00000");
	}
	
	@Test
	@DisplayName("add/substract/muliply works fine - in random order")
	void test8() {
		String input="24+22*643+231";
		assertThat(converter.processFormula(input)).isEqualTo("14401,00000");
	}
	
	@Test
	@DisplayName("RPN calculation test 1")
	void test9() {
		String input="12 2 3 4 * 10 5 / + * +";
		assertThat(converter.calculateInRPN(input.split(" "))).isEqualTo("40,00000");
		input="15 7 1 1 + - / 3 * 2 1 1 + + -";
		assertThat(converter.calculateInRPN(input.split(" "))).isEqualTo("5,00000");
	}
	
	@Test
	@DisplayName("RPN calculation test 2")
	void test10() {
		String input="2 3 11 + 5 - *";
		assertThat(converter.calculateInRPN(input.split(" "))).isEqualTo("18,00000");
	}
	
	@Test
	@DisplayName("powers works fine - easy test")
	void test11() {
		String input="2^3";
		assertThat(converter.processFormula(input)).isEqualTo("8,00000");
	}
	
	@Test
	@DisplayName("powers works fine - medium test")
	void test12() {
		String input="34+45-2^3";
		assertThat(converter.processFormula(input)).isEqualTo("71,00000");
	}
	
	@Test
	@DisplayName("powers works fine - hard test")
	void test13() {
		String input="34+45-2^(3-2)+456-23^2+45";
		assertThat(converter.processFormula(input)).isEqualTo("49,00000");
	}
	
	@Test
	@DisplayName("modulo works fine - easy test")
	void test14() {
		String input="45%2";
		assertThat(converter.processFormula(input)).isEqualTo("1,00000");
	}
	
	@Test
	@DisplayName("modulo works fine - medium test")
	void test15() {
		String input="45%16+34*15^2";
		assertThat(converter.processFormula(input)).isEqualTo("7663,00000");
	}
	
	@Test
	@DisplayName("general test 1")
	void test16() {
		String input="3+4*2/(1-5)^2";
		assertThat(converter.processFormula(input)).isEqualTo("3,50000");
	}
	
	@Test
	@DisplayName("returns previous result if last token is operator")
	void test17() {
		String input="3+";
		assertThat(converter.processFormula(input)).isEqualTo("Syntax Error");
	}
	
	@Test
	@DisplayName("float number is calculated correctly")
	void test18() {
		String input="2.5+4.5*2.44";
		assertThat(converter.processFormula(input)).isEqualTo("13,48000");
	}
	
	@Test
	@DisplayName("float number is calculated correctly - test2 medium")
	void test19() {
		String input="2.5+4.5*2.44-2^(3.5-13.56)+4.5*5.55";
		assertThat(converter.processFormula(input)).isEqualTo("38,45406");
	}
	
	@Test
	@DisplayName("cos function test")
	void test20() {
		String input="2.5+cos(45)";
		assertThat(converter.processFormula(input)).isEqualTo("3,02532");
	}
	
	@Test
	@DisplayName("cos+sin function test")
	void test21() {
		String input="2.5+cos(45)-sin(123)";
		assertThat(converter.processFormula(input)).isEqualTo("3,48523");
	}
	
	@Test
	@DisplayName("cos+sin+tan function test")
	void test22() {
		String input="2.5+cos(45)-sin(123)/tan(4.5)";
		assertThat(converter.processFormula(input)).isEqualTo("3,12450");
	}
	
	@Test
	@DisplayName("max function test")
	void test23() {
		String input="2.5+max(45,23)";
		assertThat(converter.processFormula(input)).isEqualTo("47,50000");
	}
	
	@Test
	@DisplayName("cos+sin+tan+max function test")
	void test24() {
		String input="2.5+cos(45)-sin(123)/tan(max(45,33))";
		assertThat(converter.processFormula(input)).isEqualTo("3,30925");
	}
	
	@Test
	@DisplayName("neg function test")
	void test25() {
		String input="-4.5-(22)";
		assertThat(converter.processFormula(input)).isEqualTo("-26,50000");
	}
	
	@Test
	@DisplayName("neg function test 2")
	void test26() {
		String input="-4.5-(-22)";
		assertThat(converter.processFormula(input)).isEqualTo("17,50000");
	}
	
	@Test
	@DisplayName("neg function test 3")
	void test27() {
		String input="-4.5-(22-20)";
		assertThat(converter.processFormula(input)).isEqualTo("-6,50000");
	}
	
	@Test
	@DisplayName("neg function test 4")
	void test28() {
		String input="-4.5-(-22+(4-2))";
		assertThat(converter.processFormula(input)).isEqualTo("19,50000");
	}
	
	@Test
	@DisplayName("neg function test 5")
	void test29() {
		String input="-4.5-2*(-22)";
		assertThat(converter.processFormula(input)).isEqualTo("39,50000");
	}
	
	@Test
	@DisplayName("neg function test 6")
	void test30() {
		String input="-4.5-2*(22)";
		assertThat(converter.processFormula(input)).isEqualTo("-48,50000");
	}
	
	@Test
	@DisplayName("neg function test 7")
	void test31() {
		String input="-4.5-2*(-22-10)";
		assertThat(converter.processFormula(input)).isEqualTo("59,50000");
	}
	
	@Test
	@DisplayName("neg function test 8")
	void test32() {
		String input="-4.5-2*(-22-(23+44))";
		assertThat(converter.processFormula(input)).isEqualTo("173,50000");
	}
	
	@Test
	@DisplayName("neg function test 9")
	void test33() {
		String input="-4.5+2*(-22-(23+44))";
		assertThat(converter.processFormula(input)).isEqualTo("-182,50000");
	}
	
	@Test
	@DisplayName("neg function test 10")
	void test34() {
		String input="-4.5+2*(-22-10)";
		assertThat(converter.processFormula(input)).isEqualTo("-68,50000");
	}
	
	@Test
	@DisplayName("neg function test 10")
	void test35() {
		String input="2*(-100)";
		assertThat(converter.processFormula(input)).isEqualTo("-200,00000");
	}
	
	@Test
	@DisplayName("neg function test 11")
	void test36() {
		String input="(-100)";
		assertThat(converter.processFormula(input)).isEqualTo("-100,00000");
	}
}
