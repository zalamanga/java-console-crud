package id.javacrud;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public final class Main {
  private Main() {}

  public static void main(String[] args) {
    try {
      new ConsoleApp(
              new InputStreamReader(System.in, StandardCharsets.UTF_8),
              new OutputStreamWriter(System.out, StandardCharsets.UTF_8),
              new ContactRepository(Database.environment()))
          .run();
    } catch (IllegalArgumentException error) {
      System.err.println(error.getMessage());
      System.exit(2);
    } catch (java.io.IOException error) {
      System.err.println("Input/output tidak tersedia.");
      System.exit(3);
    }
  }
}
