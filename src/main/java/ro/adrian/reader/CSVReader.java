package ro.adrian.reader;

import lombok.extern.slf4j.Slf4j;
import ro.adrian.PersonResultValidator;
import ro.adrian.model.PersonResult;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CSVReader {

  private final FileLinesReader fileLinesReader;
  private final PersonResultValidator personResultValidator;
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

  public CSVReader(FileLinesReader fileLinesReader, PersonResultValidator personResultValidator) {
    this.fileLinesReader = fileLinesReader;
    this.personResultValidator = personResultValidator;
  }

  public List<PersonResult> readCsv(String fileName) throws IOException {
    List<String> csvLines = fileLinesReader.readLines(fileName);
    List<PersonResult> persons = new ArrayList<>();

    if (csvLines.isEmpty()) {
      return persons;
    }

    csvLines.removeFirst();

    for (String line : csvLines) {
      PersonResult personResult = null;
      try {
        personResult = parseCsvLine(line);
      } catch (ParseException e) {
        log.warn("Skipping invalid CSV line: {}", e.getMessage());
      }
      if (personResult != null) {
        persons.add(personResult);
      }
    }
    log.info("CSV processing completed");
    return persons;
  }

  private PersonResult parseCsvLine(String line) throws ParseException {
    personResultValidator.validate(line);
    String[] lineParts = line.split(",", -1);

    String firstName = lineParts[0];
    String lastName = lineParts[1];
    LocalDate dateOfBirth = LocalDate.parse(lineParts[2], FORMATTER);

    return new PersonResult(firstName, lastName, dateOfBirth);
  }
}
