package ro.adrian.writer;

import org.junit.jupiter.api.Test;
import ro.adrian.model.PersonResult;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CSVWriterTest {

  @Test
  public void testWriteResultsToFileHappyPath() throws IOException {
    // given
    FileLinesWriter fileLinesWriter = mock(FileLinesWriter.class);
    CSVWriter csvWriter = new CSVWriter(fileLinesWriter);

    List<String> linesToBeWritten =
        List.of("firstName,lastName", "Alex,Ionescu", "Andrei,Ionescu", "Mihai,Stan");

    List<PersonResult> personsToWrite =
        List.of(
            new PersonResult("Alex", "Ionescu", LocalDate.of(2001, 3, 12)),
            new PersonResult("Andrei", "Ionescu", LocalDate.of(2005, 3, 23)),
            new PersonResult("Mihai", "Stan", LocalDate.of(2010, 3, 1)));

    // when
    csvWriter.writeResultsToFile(personsToWrite, "outputFile");

    // then
    verify(fileLinesWriter).writeLines("outputFile", linesToBeWritten);
  }

  @Test
  public void testWriteResultsToFileWhenNoPersonsFound() throws IOException {
    // given
    FileLinesWriter fileLinesWriter = mock(FileLinesWriter.class);
    CSVWriter csvWriter = new CSVWriter(fileLinesWriter);

    List<String> linesToBeWritten = List.of("firstName,lastName");

    List<PersonResult> personsToWrite = List.of();

    // when
    csvWriter.writeResultsToFile(personsToWrite, "outputFile");

    // then
    verify(fileLinesWriter).writeLines("outputFile", linesToBeWritten);
  }

  @Test
  public void testWriteResultsToFileWhenFileCannotBeWritten() throws IOException {
    // given
    FileLinesWriter fileLinesWriter = mock(FileLinesWriter.class);
    CSVWriter csvWriter = new CSVWriter(fileLinesWriter);

    List<String> linesToBeWritten = List.of("firstName,lastName");

    List<PersonResult> personsToWrite = List.of();

    doThrow(new IOException("Cannot write to file"))
        .when(fileLinesWriter)
        .writeLines("outputFile", linesToBeWritten);

    // when + then
    assertThatThrownBy(() -> csvWriter.writeResultsToFile(personsToWrite, "outputFile"))
        .isInstanceOf(IOException.class)
        .hasMessage("Cannot write to file");
  }
}
