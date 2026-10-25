package id.javacrud;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {
  @Test
  void rejectsInvalidIdsAndPhones() {
    for (String id : new String[] {"0", "-1", "1 OR 1=1", "9999999999999999999999"})
      assertThrows(IllegalArgumentException.class, () -> Validation.id(id));
    assertEquals(42, Validation.id("42"));
    assertThrows(IllegalArgumentException.class, () -> Validation.phone("0812;DELETE"));
    for (String phone : new String[] {"-----", ".....", "+() ."})
      assertThrows(IllegalArgumentException.class, () -> Validation.phone(phone));
    assertEquals("+62 (812) 345-678", Validation.phone("+62 (812) 345-678"));
  }

  @Test
  void contactValidatesBoundsDatesAndEmail() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Contact(
                null,
                " ",
                "Contoh",
                LocalDate.of(2000, 1, 1),
                "081234",
                "demo@example.test",
                "demo",
                "staff"));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Contact(
                null,
                "Demo",
                "Contoh",
                LocalDate.now().plusDays(1),
                "081234",
                "demo@example.test",
                "demo",
                "staff"));
    assertThrows(IllegalArgumentException.class, () -> Validation.date("2023-02-29"));
    assertThrows(IllegalArgumentException.class, () -> Validation.email("bukan-email"));
    assertThrows(
        IllegalArgumentException.class, () -> Validation.text("\u001b[31m", 1, 20, "Nama"));
  }

  @Test
  void invalidPasswordsAreRejectedBeforeHashing() {
    for (String password : new String[] {"pendek", " ".repeat(12), "x".repeat(129)})
      assertThrows(IllegalArgumentException.class, () -> Passwords.hash(password));
  }
}
