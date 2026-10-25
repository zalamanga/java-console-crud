package id.javacrud;

import java.time.LocalDate;
import java.util.Locale;

public final class Validation {
  private Validation() {}

  public static String text(String value, int min, int max, String label) {
    String clean = value == null ? "" : value.strip();
    if (clean.length() < min
        || clean.length() > max
        || clean.chars().anyMatch(Character::isISOControl))
      throw new IllegalArgumentException(
          label + " harus berisi " + min + " sampai " + max + " karakter tanpa karakter kontrol.");
    return clean;
  }

  public static String phone(String value) {
    String clean = text(value, 5, 25, "Telepon");
    if (!clean.matches("[+0-9() .-]+") || clean.chars().noneMatch(Character::isDigit))
      throw new IllegalArgumentException(
          "Nomor telepon harus memuat angka; spasi, +, -, titik, dan kurung diperbolehkan.");
    return clean;
  }

  public static String email(String value) {
    String clean = text(value, 3, 254, "Email").toLowerCase(Locale.ROOT);
    if (!clean.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
      throw new IllegalArgumentException("Format email tidak valid.");
    return clean;
  }

  public static long id(String value) {
    try {
      long id = Long.parseLong(value);
      if (id > 0) return id;
    } catch (NumberFormatException ignored) {
    }
    throw new IllegalArgumentException("ID harus berupa bilangan bulat positif.");
  }

  public static LocalDate date(String value) {
    try {
      return LocalDate.parse(value);
    } catch (RuntimeException error) {
      throw new IllegalArgumentException("Gunakan tanggal valid dalam format YYYY-MM-DD.");
    }
  }

  public static void password(String value) {
    if (value == null || value.length() < 12 || value.length() > 128 || value.isBlank())
      throw new IllegalArgumentException("Password contoh harus berisi 12 sampai 128 karakter.");
  }
}
