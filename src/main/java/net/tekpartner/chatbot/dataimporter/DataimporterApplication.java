package net.tekpartner.chatbot.dataimporter;

import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataimporterApplication {

  public static void main(String[] args) {
    SpringApplication.run(DataimporterApplication.class, args);

    ImporterUtil importerUtil = new ImporterUtil();
    try {
      importerUtil.readFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
