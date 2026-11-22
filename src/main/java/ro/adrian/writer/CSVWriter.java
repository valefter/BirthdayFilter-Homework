package ro.adrian.writer;

import lombok.extern.slf4j.Slf4j;
import ro.adrian.model.PersonResult;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CSVWriter {

  private final FileLinesWriter fileLinesWriter;

  public CSVWriter(FileLinesWriter fileLinesWriter) {
    this.fileLinesWriter = fileLinesWriter;
  }

  public void writeResultsToFile(List<PersonResult> persons, String outputFileName)
      throws IOException {
    List<String> result =
        persons.stream()
            .map(personResult -> personResult.getFirstName() + "," + personResult.getLastName())
            .collect(Collectors.toList());

    String header = "firstName,lastName";
    result.addFirst(header);

    fileLinesWriter.writeLines(outputFileName, result);
    log.info("CSV writing completed successfully");
  }
}
