package ro.adrian.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PersonResult {

  private final String firstName;
  private final String lastName;
  private final LocalDate dateOfBirth;
}
