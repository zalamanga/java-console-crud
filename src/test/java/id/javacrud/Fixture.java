package id.javacrud;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;

final class Fixture implements AutoCloseable {
  final String url = "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1";
  final ContactRepository repository =
      new ContactRepository(() -> DriverManager.getConnection(url, "sa", ""));

  Fixture() throws Exception {
    String sql =
        Files.readString(Path.of("database/001-schema.sql"))
            .replace("CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci", "");
    try (Connection connection = DriverManager.getConnection(url, "sa", "");
        Statement statement = connection.createStatement()) {
      statement.execute(sql);
    }
  }

  Contact contact(String name, String email) {
    return new Contact(
        null,
        name,
        "Contoh",
        LocalDate.of(2000, 1, 2),
        "+62 812 3456",
        email,
        "Alamat demo",
        "staff");
  }

  @Override
  public void close() throws Exception {
    try (Connection connection = DriverManager.getConnection(url, "sa", "");
        Statement statement = connection.createStatement()) {
      statement.execute("DROP ALL OBJECTS");
    }
  }
}
