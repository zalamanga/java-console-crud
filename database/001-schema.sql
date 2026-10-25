-- Schema baru untuk latihan; tidak berisi data pengguna asli.
CREATE TABLE tbl_user (
  id_user BIGINT AUTO_INCREMENT PRIMARY KEY,
  nama VARCHAR(100) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  gender VARCHAR(20) NOT NULL,
  dob DATE NOT NULL,
  phone_number VARCHAR(25) NOT NULL,
  email VARCHAR(254) NOT NULL UNIQUE,
  alamat VARCHAR(300) NOT NULL,
  role VARCHAR(30) NOT NULL
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
