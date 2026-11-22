package ro.adrian;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ro.adrian.exceptions.InvalidMonthException;
import ro.adrian.model.PersonResult;
import ro.adrian.reader.CSVReader;
import ro.adrian.writer.CSVWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PersonResultOrchestratorTest {

  @Test
  public void testFilterByBirthMonthHappyPath() throws IOException {
    // given
    CSVReader csvReader = mock(CSVReader.class);
    CSVWriter csvWriter = mock(CSVWriter.class);
    PersonResultOrchestrator orchestrator = new PersonResultOrchestrator(csvReader, csvWriter);

    List<PersonResult> inputPersons =
        List.of(
            new PersonResult("Maria", "Georgescu", LocalDate.of(2002, 8, 7)),
            new PersonResult("Ionut", "Popescu", LocalDate.of(2011, 8, 14)),
            new PersonResult("Elena", "Dumitru", LocalDate.of(1999, 11, 19)));

    List<PersonResult> expectedResult =
        List.of(
            new PersonResult("Maria", "Georgescu", LocalDate.of(2002, 8, 7)),
            new PersonResult("Ionut", "Popescu", LocalDate.of(2011, 8, 14)));

    when(csvReader.readCsv("inputFile")).thenReturn(inputPersons);

    // when
    orchestrator.processBirthdays("inputFile", 8, "outputFile");

    // then
    verify(csvReader).readCsv("inputFile");
    verify(csvWriter).writeResultsToFile(expectedResult, "outputFile");
  }

  @Test
  public void testSortingAlphabeticallyHappyPath() throws IOException {
    // given
    CSVReader csvReader = mock(CSVReader.class);
    CSVWriter csvWriter = mock(CSVWriter.class);
    PersonResultOrchestrator orchestrator = new PersonResultOrchestrator(csvReader, csvWriter);

    List<PersonResult> inputPersons =
        List.of(
            new PersonResult("Andrei", "Ionescu", LocalDate.of(2005, 3, 23)),
            new PersonResult("Mihai", "Stan", LocalDate.of(2010, 3, 1)),
            new PersonResult("Alex", "Ionescu", LocalDate.of(2001, 3, 12)));

    List<PersonResult> expectedResult =
        List.of(
            new PersonResult("Alex", "Ionescu", LocalDate.of(2001, 3, 12)),
            new PersonResult("Andrei", "Ionescu", LocalDate.of(2005, 3, 23)),
            new PersonResult("Mihai", "Stan", LocalDate.of(2010, 3, 1)));

    when(csvReader.readCsv("inputFile")).thenReturn(inputPersons);

    // when
    orchestrator.processBirthdays("inputFile", 3, "outputFile");

    // then
    verify(csvReader).readCsv("inputFile");
    verify(csvWriter).writeResultsToFile(expectedResult, "outputFile");
  }

  @Test
  public void testProcessBirthdaysHappyPath() throws IOException {
    // given
    CSVReader csvReader = mock(CSVReader.class);
    CSVWriter csvWriter = mock(CSVWriter.class);
    PersonResultOrchestrator orchestrator = new PersonResultOrchestrator(csvReader, csvWriter);

    List<PersonResult> inputPersons =
        List.of(
            new PersonResult("Ionut", "Popescu", LocalDate.of(2011, 8, 14)),
            new PersonResult("Andrei", "Ionescu", LocalDate.of(2005, 3, 23)),
            new PersonResult("Maria", "Georgescu", LocalDate.of(2002, 7, 8)),
            new PersonResult("Elena", "Dumitru", LocalDate.of(1999, 11, 19)),
            new PersonResult("Mihai", "Stan", LocalDate.of(2010, 3, 1)),
            new PersonResult("Alex", "Ionescu", LocalDate.of(2001, 3, 12)));

    List<PersonResult> expectedResult =
        List.of(
            new PersonResult("Alex", "Ionescu", LocalDate.of(2001, 3, 12)),
            new PersonResult("Andrei", "Ionescu", LocalDate.of(2005, 3, 23)),
            new PersonResult("Mihai", "Stan", LocalDate.of(2010, 3, 1)));

    when(csvReader.readCsv("inputFile")).thenReturn(inputPersons);

    // when
    orchestrator.processBirthdays("inputFile", 3, "outputFile");

    // then
    verify(csvReader).readCsv("inputFile");
    verify(csvWriter).writeResultsToFile(expectedResult, "outputFile");
  }

  @ParameterizedTest
  @MethodSource("invalidMonths")
  public void testValidateMonthWithInvalidInput(int month) {
    // given
    CSVReader csvReader = mock(CSVReader.class);
    CSVWriter csvWriter = mock(CSVWriter.class);
    PersonResultOrchestrator orchestrator = new PersonResultOrchestrator(csvReader, csvWriter);

    // when + then
    assertThatThrownBy(() -> orchestrator.processBirthdays("inputFile", month, "outputFile"))
        .isInstanceOf(InvalidMonthException.class)
        .hasMessage("The month must be a number between 1 and 12");

    verifyNoInteractions(csvReader, csvWriter);
  }

  private static Stream<Arguments> invalidMonths() {
    return Stream.of(Arguments.of(0), Arguments.of(-2), Arguments.of(13));
  }

  @Test
  public void testProcessBirthdaysWhenFileCannotBeRead() throws IOException {
    // given
    CSVReader csvReader = mock(CSVReader.class);
    CSVWriter csvWriter = mock(CSVWriter.class);
    PersonResultOrchestrator orchestrator = new PersonResultOrchestrator(csvReader, csvWriter);

    when(csvReader.readCsv("inputFile")).thenThrow(new IOException("File cannot be read"));

    // when + then
    assertThatThrownBy(() -> orchestrator.processBirthdays("inputFile", 1, "outputFile"))
        .isInstanceOf(IOException.class)
        .hasMessage("File cannot be read");
    verifyNoInteractions(csvWriter);
  }
}
