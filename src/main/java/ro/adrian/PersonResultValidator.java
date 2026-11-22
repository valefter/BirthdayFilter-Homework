package ro.adrian;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PersonResultValidator {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

  public void validate(String line) throws ParseException {
    String[] lineParts = line.split(",", -1);
    if (lineParts.length != 3) {
      throw new ParseException("The line must contain 3 elements", 0);
    }

    String firstName = lineParts[0];
    if (firstName.isBlank()) {
      throw new ParseException("First name must be provided", 0);
    }

    String lastName = lineParts[1];
    if (lastName.isBlank()) {
      throw new ParseException("Last name must be provided", 0);
    }

    String birthDate = lineParts[2];
    if (birthDate.isBlank()) {
      throw new ParseException("Date of birth must be provided", 0);
    }

    try {
      LocalDate.parse(birthDate, FORMATTER);
    } catch (DateTimeParseException e) {
      throw new ParseException("Provide a valid date of birth", 0);
    }
  }
}
