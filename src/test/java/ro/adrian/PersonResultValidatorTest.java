package ro.adrian;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.ParseException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonResultValidatorTest {

  @ParameterizedTest
  @MethodSource("invalidInputs")
  public void testValidateWithInvalidInputs(String input, String expectedErrorMessage) {
    // given
    PersonResultValidator validator = new PersonResultValidator();

    // when + then
    assertThatThrownBy(() -> validator.validate(input))
        .isInstanceOf(ParseException.class)
        .hasMessage(expectedErrorMessage);
  }

  private static Stream<Arguments> invalidInputs() {
    return Stream.of(
        Arguments.of("Mihai,Stan", "The line must contain 3 elements"),
        Arguments.of(",Stan,01-03-2010", "First name must be provided"),
        Arguments.of("  ,Stan,01-03-2010", "First name must be provided"),
        Arguments.of("Mihai,,01-03-2010", "Last name must be provided"),
        Arguments.of("Mihai,  ,01-03-2010", "Last name must be provided"),
        Arguments.of("Mihai,Stan,", "Date of birth must be provided"),
        Arguments.of("Mihai,Stan,  ", "Date of birth must be provided"),
        Arguments.of("Mihai,Stan,01.03.2010", "Provide a valid date of birth"),
        Arguments.of("Mihai,Stan,40-03-2010", "Provide a valid date of birth"));
  }

  @Test
  public void testValidateHappyPath() {
    // given
    PersonResultValidator validator = new PersonResultValidator();
    String input = "Mihai,Stan,01-03-2010";

    // when + then
    assertThatCode(() -> validator.validate(input)).doesNotThrowAnyException();
  }
}
