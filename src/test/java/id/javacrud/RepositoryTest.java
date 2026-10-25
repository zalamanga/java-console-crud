package id.javacrud;

import java.sql.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {
  @Test
  void realCrudAndPasswordNeverReturnedInModel() throws Exception {
    try (Fixture db = new Fixture()) {
      assertTrue(db.repository.search("").isEmpty());
      long id =
          db.repository.add(
              db.contact("Pengguna contoh", "one@example.test"), "password-contoh-2026");
      Contact contact = db.repository.search("").get(0);
      assertEquals(id, contact.id());
      assertTrue(db.repository.updatePhone(id, "+62 999 888"));
      assertEquals("+62 999 888", db.repository.search("").get(0).phone());
      try (Connection c = DriverManager.getConnection(db.url, "sa", "");
          ResultSet r = c.createStatement().executeQuery("SELECT password_hash FROM tbl_user")) {
        assertTrue(r.next());
        assertTrue(r.getString(1).startsWith("pbkdf2-sha256$600000$"));
        assertFalse(r.getString(1).contains("password-contoh"));
      }
      assertTrue(db.repository.delete(id));
      assertTrue(db.repository.search("").isEmpty());
    }
  }

  @Test
  void literalsCannotBecomeSearchOrUpdateSql() throws Exception {
    try (Fixture db = new Fixture()) {
      long id =
          db.repository.add(db.contact("O'Neill", "literal@example.test"), "password-contoh-2026");
      assertEquals(1, db.repository.search("o'neill").size());
      assertEquals(1, db.repository.search("LITERAL").size());
      assertTrue(db.repository.search("' OR 1=1 --").isEmpty());
      assertTrue(db.repository.search("%").isEmpty());
      assertThrows(
          IllegalArgumentException.class,
          () -> db.repository.updatePhone(id, "1'; DROP TABLE tbl_user;--"));
      assertEquals(1, db.repository.search("").size());
    }
  }

  @Test
  void unknownIdsDoNotClaimMutation() throws Exception {
    try (Fixture db = new Fixture()) {
      assertFalse(db.repository.updatePhone(99, "08123456"));
      assertFalse(db.repository.delete(99));
    }
  }

  @Test
  void duplicateEmailFailureDoesNotDamageExistingData() throws Exception {
    try (Fixture db = new Fixture()) {
      db.repository.add(db.contact("Satu", "one@example.test"), "password-contoh-2026");
      assertThrows(
          SQLException.class,
          () -> db.repository.add(db.contact("Dua", "one@example.test"), "password-contoh-2026"));
      assertEquals(1, db.repository.search("").size());
    }
  }
}
