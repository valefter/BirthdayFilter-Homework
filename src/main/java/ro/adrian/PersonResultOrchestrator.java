package ro.adrian;

import lombok.extern.slf4j.Slf4j;
import ro.adrian.exceptions.InvalidMonthException;
import ro.adrian.model.PersonResult;
import ro.adrian.reader.CSVReader;
import ro.adrian.writer.CSVWriter;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class PersonResultOrchestrator {

  private final CSVReader csvReader;
  private final CSVWriter csvWriter;

  public PersonResultOrchestrator(CSVReader csvReader, CSVWriter csvWriter) {
    this.csvReader = csvReader;
    this.csvWriter = csvWriter;
  }

  public void processBirthdays(String inputFileName, int month, String outputFileName)
      throws IOException {
    log.info("Attempting to process birthdays");
    List<PersonResult> persons = filterByBirthMonthAndSortAlphabetically(month, inputFileName);
    csvWriter.writeResultsToFile(persons, outputFileName);
    log.info("Successfully processed birthdays");
  }

  private List<PersonResult> filterByBirthMonthAndSortAlphabetically(
      int month, String inputFileName) throws IOException {
    validateMonth(month);
    List<PersonResult> persons = csvReader.readCsv(inputFileName);

    return persons.stream()
        .filter(person -> person.getDateOfBirth().getMonthValue() == month)
        .sorted(
            Comparator.comparing(PersonResult::getLastName)
                .thenComparing(PersonResult::getFirstName))
        .toList();
  }

  private void validateMonth(int month) {
    if (month < 1 || month > 12) {
      throw new InvalidMonthException("The month must be a number between 1 and 12");
    }
  }
}
