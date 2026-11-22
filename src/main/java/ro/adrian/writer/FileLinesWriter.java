package ro.adrian.writer;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class FileLinesWriter {

  public void writeLines(String outputFileName, List<String> result) throws IOException {
    log.info("Attempting to write on file");
    Files.write(Paths.get(outputFileName), result);
    log.info("Successfully wrote on file");
  }
}
