package id.javacrud;

import java.io.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConsoleAppTest {
  private String run(Fixture db, String script) throws IOException {
    StringWriter output = new StringWriter();
    new ConsoleApp(new StringReader(script), output, db.repository).run();
    return output.toString();
  }

  @Test
  void menuInvalidInputAndEofRecover() throws Exception {
    try (Fixture db = new Fixture()) {
      String output = run(db, "x\n1\n3\nbukan-id\n");
      assertTrue(output.contains("Pilihan tidak tersedia"));
      assertTrue(output.contains("Tidak ada data"));
      assertTrue(output.contains("ID harus"));
      assertTrue(output.contains("Selesai"));
    }
  }

  @Test
  void completeMenuWorkflowAndCancelledDeletion() throws Exception {
    try (Fixture db = new Fixture()) {
      String script =
          "2\nPengguna Demo\npassword-contoh-2026\nContoh\n2000-01-02\n08123456\ndemo@example.test\nAlamat demo\nstaff\n"
              + "1\n5\nPengguna\n3\n1\n08999999\n4\n1\nBATAL\n4\n1\nHAPUS\n0\n";
      String output = run(db, script);
      for (String expected :
          new String[] {
            "Data tersimpan dengan ID 1",
            "Pengguna Demo",
            "Nomor telepon diperbarui",
            "Penghapusan dibatalkan",
            "Data dihapus",
            "Selesai"
          }) assertTrue(output.contains(expected), expected);
      assertFalse(output.contains("password-contoh-2026"));
      assertFalse(output.contains("pbkdf2-sha256"));
      assertTrue(db.repository.search("").isEmpty());
    }
  }

  @Test
  void eofDuringAddDoesNotWrite() throws Exception {
    try (Fixture db = new Fixture()) {
      assertTrue(run(db, "2\nNama contoh\n").contains("dibatalkan"));
      assertTrue(db.repository.search("").isEmpty());
    }
  }

  @Test
  void databaseErrorDoesNotPrintCredentialsOrSuccess() throws Exception {
    ContactRepository bad =
        new ContactRepository(
            () -> {
              throw new java.sql.SQLException("secret-url-password", "08001");
            });
    StringWriter output = new StringWriter();
    new ConsoleApp(new StringReader("1\n0\n"), output, bad).run();
    assertTrue(output.toString().contains("08001"));
    assertFalse(output.toString().contains("secret-url-password"));
  }
}
