package id.javacrud;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
  private Database() {}

  @FunctionalInterface
  public interface Connections {
    Connection open() throws SQLException;
  }

  public static Connections environment() {
    String url = required("DB_URL"), user = required("DB_USER"), password = required("DB_PASSWORD");
    if (!url.startsWith("jdbc:mysql:"))
      throw new IllegalArgumentException("DB_URL harus berupa URL JDBC MySQL.");
    return () -> DriverManager.getConnection(url, user, password);
  }

  private static String required(String name) {
    String value = System.getenv(name);
    if (value == null || value.isBlank())
      throw new IllegalArgumentException("Environment " + name + " belum diisi.");
    return value;
  }
}
