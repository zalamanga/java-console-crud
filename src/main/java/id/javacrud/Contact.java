package id.javacrud;

import java.time.LocalDate;

public record Contact(
    Long id,
    String name,
    String gender,
    LocalDate birthDate,
    String phone,
    String email,
    String address,
    String role) {
  public Contact {
    name = Validation.text(name, 1, 100, "Nama");
    gender = Validation.text(gender, 1, 20, "Gender");
    phone = Validation.phone(phone);
    email = Validation.email(email);
    address = Validation.text(address, 1, 300, "Alamat");
    role = Validation.text(role, 1, 30, "Role");
    if (birthDate == null || birthDate.isAfter(LocalDate.now()) || birthDate.getYear() < 1900)
      throw new IllegalArgumentException("Tanggal lahir harus antara 1900-01-01 dan hari ini.");
  }
}
