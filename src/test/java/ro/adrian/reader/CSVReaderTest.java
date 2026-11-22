package ro.adrian.reader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ro.adrian.PersonResultValidator;
import ro.adrian.model.PersonResult;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CSVReaderTest {

  @Test
  public void testReadCsvHappyPath() throws IOException {
    // given
    FileLinesReader fileLinesReader = mock(FileLinesReader.class);
    PersonResultValidator validator = new PersonResultValidator();
    CSVReader csvReader = new CSVReader(fileLinesReader, validator);

    List<String> mockFile =
        new ArrayList<>(
            List.of(
                "firstName,lastName,dateOfBirth",
                "Ionut,Popescu,14-08-2011",
                "Andrei,Ionescu,23-03-2005"));

    when(fileLinesReader.readLines("mockFile")).thenReturn(mockFile);

    PersonResult person1 = new PersonResult("Ionut", "Popescu", LocalDate.of(2011, 8, 14));
    PersonResult person2 = new PersonResult("Andrei", "Ionescu", LocalDate.of(2005, 3, 23));

    // when
    List<PersonResult> persons = csvReader.readCsv("mockFile");

    // then
    assertThat(persons).hasSize(2).contains(person1, person2);
  }

  @ParameterizedTest
  @MethodSource("emptyCsvFiles")
  public void testReturnEmptyListWhenCSVContainsNoPersons(List<String> mockFile)
      throws IOException {
    // given
    FileLinesReader fileLinesReader = mock(FileLinesReader.class);
    PersonResultValidator validator = new PersonResultValidator();
    CSVReader csvReader = new CSVReader(fileLinesReader, validator);

    when(fileLinesReader.readLines("mockFile")).thenReturn(mockFile);

    // when
    List<PersonResult> persons = csvReader.readCsv("mockFile");

    // then
    assertThat(persons).isEmpty();
  }

  private static Stream<Arguments> emptyCsvFiles() {
    return Stream.of(
        Arguments.of(new ArrayList<>(List.of("firstName,lastName,dateOfBirth"))),
        Arguments.of(new ArrayList<>(List.of())));
  }

  @ParameterizedTest
  @MethodSource("columns")
  public void testSkipRowsWhenCSVHasInvalidNumberOfColumns(List<String> mockFile)
      throws IOException {
    // given
    FileLinesReader fileLinesReader = mock(FileLinesReader.class);
    PersonResultValidator validator = new PersonResultValidator();
    CSVReader csvReader = new CSVReader(fileLinesReader, validator);

    when(fileLinesReader.readLines("mockFile")).thenReturn(mockFile);

    // when
    List<PersonResult> persons = csvReader.readCsv("mockFile");

    // then
    assertThat(persons).isEmpty();
  }

  private static Stream<Arguments> columns() {
    String header = "firstName,lastName,dateOfBirth";

    return Stream.of(
        Arguments.of(new ArrayList<>(List.of(header, ""))),
        Arguments.of(new ArrayList<>(List.of(header, "Alex,Ionescu"))),
        Arguments.of(new ArrayList<>(List.of(header, "Alex,Ionescu,12-03-2001,abcd"))));
  }

  @ParameterizedTest
  @MethodSource("invalidInputs")
  public void testSkipRowWhenCSVContainsInvalidInput(List<String> mockFile) throws IOException {
    // given
    FileLinesReader fileLinesReader = mock(FileLinesReader.class);
    PersonResultValidator validator = new PersonResultValidator();
    CSVReader csvReader = new CSVReader(fileLinesReader, validator);

    when(fileLinesReader.readLines("mockFile")).thenReturn(mockFile);

    // when
    List<PersonResult> persons = csvReader.readCsv("mockFile");

    // then
    assertThat(persons).isEmpty();
  }

  private static Stream<Arguments> invalidInputs() {
    String header = "firstName,lastName,dateOfBirth";

    return Stream.of(
        Arguments.of(new ArrayList<>(List.of(header, ",Ionescu,12-03-2001"))),
        Arguments.of(new ArrayList<>(List.of(header, "Alex,,12-03-2001"))),
        Arguments.of(new ArrayList<>(List.of(header, "Alex,Ionescu,"))),
        Arguments.of(new ArrayList<>(List.of(header, "Alex,Ionescu,abcd"))),
        Arguments.of(new ArrayList<>(List.of(header, "Alex,Ionescu,12.03.2001"))));
  }

  @Test
  public void testContinueReadingCSVAfterInvalidRow() throws IOException {
    // given
    FileLinesReader fileLinesReader = mock(FileLinesReader.class);
    PersonResultValidator validator = new PersonResultValidator();
    CSVReader csvReader = new CSVReader(fileLinesReader, validator);

    List<String> mockFile =
        new ArrayList<>(
            List.of(
                "firstName,lastName,dateOfBirth",
                "Ionut,Popescu,14-08-2011",
                "Andrei,Ionescu,abcd",
                "Alex,Ionescu,12-03-2001"));

    when(fileLinesReader.readLines("mockFile")).thenReturn(mockFile);

    PersonResult person1 = new PersonResult("Ionut", "Popescu", LocalDate.of(2011, 8, 14));
    PersonResult person2 = new PersonResult("Alex", "Ionescu", LocalDate.of(2001, 3, 12));

    // when
    List<PersonResult> persons = csvReader.readCsv("mockFile");

    // then
    assertThat(persons).hasSize(2).contains(person1, person2);
  }

  @Test
  public void testReadCSVWhenFileCannotBeRead() throws IOException {
    // given
    FileLinesReader fileLinesReader = mock(FileLinesReader.class);
    PersonResultValidator validator = new PersonResultValidator();
    CSVReader csvReader = new CSVReader(fileLinesReader, validator);

    when(fileLinesReader.readLines("mockFile")).thenThrow(new IOException("File cannot be read"));

    // when + then
    assertThatThrownBy(() -> csvReader.readCsv("mockFile"))
        .isInstanceOf(IOException.class)
        .hasMessage("File cannot be read");
  }
}
