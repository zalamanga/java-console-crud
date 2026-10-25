package id.javacrud;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

public final class ConsoleApp {
  private final BufferedReader input;
  private final PrintWriter output;
  private final ContactRepository repository;

  public ConsoleApp(Reader input, Writer output, ContactRepository repository) {
    this.input = new BufferedReader(input);
    this.output = new PrintWriter(output, true);
    this.repository = repository;
  }

  public void run() throws IOException {
    output.println("JAVA CONSOLE CRUD | Data contoh pengguna");
    output.println("Role adalah nilai data; program ini tidak memiliki sistem login.");
    while (true) {
      output.println("\n1 Daftar | 2 Tambah | 3 Ubah telepon | 4 Hapus | 5 Cari | 0 Keluar");
      String option = read("Pilihan: ");
      if (option == null || option.equals("0")) {
        output.println("Selesai.");
        return;
      }
      try {
        switch (option) {
          case "1" -> list("");
          case "2" -> add();
          case "3" -> update();
          case "4" -> delete();
          case "5" -> list(required("Nama atau email: "));
          default -> output.println("Pilihan tidak tersedia. Masukkan 0 sampai 5.");
        }
      } catch (EOFException error) {
        output.println("\nInput berakhir; operasi yang belum lengkap dibatalkan.");
        return;
      } catch (IllegalArgumentException error) {
        output.println("Input ditolak: " + error.getMessage());
      } catch (SQLException error) {
        output.println(
            "Operasi database gagal; perubahan tidak dikonfirmasi. Periksa koneksi, schema, atau email yang sudah terdaftar. Kode: "
                + error.getSQLState());
      }
    }
  }

  private void add() throws IOException, SQLException {
    String name = required("Nama: ");
    output.print("Password contoh (12-128 karakter): ");
    output.flush();
    String password = input.readLine();
    if (password == null) throw new EOFException();
    String gender = required("Gender: ");
    String date = required("Tanggal lahir (YYYY-MM-DD): ");
    String phone = required("Telepon: ");
    String email = required("Email: ");
    String address = required("Alamat: ");
    String role = required("Role (nilai data): ");
    Contact contact =
        new Contact(null, name, gender, Validation.date(date), phone, email, address, role);
    output.println("Data tersimpan dengan ID " + repository.add(contact, password) + ".");
  }

  private void update() throws IOException, SQLException {
    long id = Validation.id(required("ID pengguna: "));
    String phone = required("Telepon baru: ");
    output.println(
        repository.updatePhone(id, phone)
            ? "Nomor telepon diperbarui."
            : "ID tidak ditemukan; tidak ada data diubah.");
  }

  private void delete() throws IOException, SQLException {
    long id = Validation.id(required("ID pengguna yang dihapus: "));
    if (!required("Ketik HAPUS untuk konfirmasi: ").equals("HAPUS")) {
      output.println("Penghapusan dibatalkan.");
      return;
    }
    output.println(
        repository.delete(id) ? "Data dihapus." : "ID tidak ditemukan; tidak ada data dihapus.");
  }

  private void list(String keyword) throws SQLException {
    List<Contact> contacts = repository.search(keyword);
    if (contacts.isEmpty()) {
      output.println("Tidak ada data yang cocok.");
      return;
    }
    for (Contact contact : contacts)
      output.println(
          "\nID "
              + contact.id()
              + " | "
              + contact.name()
              + "\nEmail: "
              + contact.email()
              + "\nGender: "
              + contact.gender()
              + " | Lahir: "
              + contact.birthDate()
              + "\nTelepon: "
              + contact.phone()
              + "\nAlamat: "
              + contact.address()
              + "\nRole: "
              + contact.role());
  }

  private String read(String prompt) throws IOException {
    output.print(prompt);
    output.flush();
    String value = input.readLine();
    return value == null ? null : value.strip();
  }

  private String required(String prompt) throws IOException {
    String value = read(prompt);
    if (value == null) throw new EOFException();
    return value;
  }
}
