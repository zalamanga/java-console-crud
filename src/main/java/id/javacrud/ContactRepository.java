package id.javacrud;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactRepository {
  private final Database.Connections connections;

  public ContactRepository(Database.Connections connections) {
    this.connections = connections;
  }

  public List<Contact> search(String keyword) throws SQLException {
    String term = keyword.strip().toLowerCase(java.util.Locale.ROOT);
    try (Connection connection = connections.open();
        PreparedStatement query =
            connection.prepareStatement(
                "SELECT id_user,nama,gender,dob,phone_number,email,alamat,role FROM tbl_user WHERE LOCATE(?,LOWER(nama))>0 OR LOCATE(?,LOWER(email))>0 ORDER BY id_user")) {
      query.setString(1, term);
      query.setString(2, term);
      try (ResultSet rows = query.executeQuery()) {
        List<Contact> contacts = new ArrayList<>();
        while (rows.next())
          contacts.add(
              new Contact(
                  rows.getLong("id_user"),
                  rows.getString("nama"),
                  rows.getString("gender"),
                  rows.getDate("dob").toLocalDate(),
                  rows.getString("phone_number"),
                  rows.getString("email"),
                  rows.getString("alamat"),
                  rows.getString("role")));
        return contacts;
      }
    }
  }

  public long add(Contact contact, String password) throws SQLException {
    String hash = Passwords.hash(password);
    try (Connection connection = connections.open();
        PreparedStatement query =
            connection.prepareStatement(
                "INSERT INTO tbl_user(nama,password_hash,gender,dob,phone_number,email,alamat,role) VALUES(?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
      query.setString(1, contact.name());
      query.setString(2, hash);
      query.setString(3, contact.gender());
      query.setDate(4, Date.valueOf(contact.birthDate()));
      query.setString(5, contact.phone());
      query.setString(6, contact.email());
      query.setString(7, contact.address());
      query.setString(8, contact.role());
      query.executeUpdate();
      try (ResultSet keys = query.getGeneratedKeys()) {
        if (keys.next()) return keys.getLong(1);
      }
      throw new SQLException("Database tidak mengembalikan ID baru.");
    }
  }

  public boolean updatePhone(long id, String phone) throws SQLException {
    String valid = Validation.phone(phone);
    try (Connection connection = connections.open();
        PreparedStatement query =
            connection.prepareStatement("UPDATE tbl_user SET phone_number=? WHERE id_user=?")) {
      query.setString(1, valid);
      query.setLong(2, id);
      return query.executeUpdate() > 0;
    }
  }

  public boolean delete(long id) throws SQLException {
    try (Connection connection = connections.open();
        PreparedStatement query =
            connection.prepareStatement("DELETE FROM tbl_user WHERE id_user=?")) {
      query.setLong(1, id);
      return query.executeUpdate() > 0;
    }
  }
}
