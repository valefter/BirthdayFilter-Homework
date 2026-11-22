package ro.adrian.reader;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class FileLinesReader {

  public List<String> readLines(String fileName) throws IOException {
    log.info("Attempting to read from file");
    List<String> result = Files.readAllLines(Paths.get(fileName));
    log.info("Successfully read content from file");
    return result;
  }
}
