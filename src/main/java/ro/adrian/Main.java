package ro.adrian;

import lombok.extern.slf4j.Slf4j;
import ro.adrian.reader.CSVReader;
import ro.adrian.reader.FileLinesReader;
import ro.adrian.writer.CSVWriter;
import ro.adrian.writer.FileLinesWriter;

import java.io.IOException;

@Slf4j
public class Main {

  public static void main(String[] args) {
    try {
      run();
    } catch (IOException e) {
      log.error("An error occurred while processing the files", e);
    }
  }

  private static void run() throws IOException {
    FileLinesReader fileLinesReader = new FileLinesReader();
    FileLinesWriter fileLinesWriter = new FileLinesWriter();
    PersonResultValidator personResultValidator = new PersonResultValidator();
    CSVReader csvReader = new CSVReader(fileLinesReader, personResultValidator);
    CSVWriter csvWriter = new CSVWriter(fileLinesWriter);
    PersonResultOrchestrator orchestrator = new PersonResultOrchestrator(csvReader, csvWriter);

    String inputFileName = "src/main/resources/persons.csv";
    String outputFileName = "src/main/resources/processedPersons.csv";

    orchestrator.processBirthdays(inputFileName, 3, outputFileName);
  }
}
