# Java Console CRUD

Latihan **Java 17 dan JDBC/MySQL** untuk menambah dan membaca data pengguna, mencari nama/email, mengubah nomor telepon, serta menghapus data dengan konfirmasi. Program memakai satu menu terminal, prepared statement, validasi input, dan koneksi database dari environment.

Ini latihan CRUD dengan data contoh. **Tidak ada sistem login**; kolom role hanya nilai data. Password contoh disimpan sebagai hash dan tidak ditampilkan pada daftar. Operator program memiliki hak sesuai akun database yang dipakainya.

## Jalankan dengan Docker

Siapkan Docker Desktop dengan Linux containers. Di PowerShell:

```powershell
git clone https://github.com/zalamanga/java-console-crud.git
cd java-console-crud
Copy-Item .env.example .env
```

Buka `.env`, ganti kedua password contoh dengan nilai lokal yang berbeda, lalu jalankan:

```powershell
docker compose build app
docker compose run --rm app
```

MySQL dimulai otomatis dan ditunggu sampai sehat. Database baru kosong; pilih **2 Tambah** untuk membuat data contoh. Tidak ada port database yang dibuka ke Windows. Di Linux/macOS gunakan `cp .env.example .env`.

| Menu | Tindakan |
| --- | --- |
| 1 Daftar | Tampilkan seluruh data; password/hash tidak ikut ditampilkan. |
| 2 Tambah | Isi nama, password contoh, gender, tanggal lahir, telepon, email, alamat, dan role. ID dibuat database. |
| 3 Ubah telepon | Masukkan ID dan nomor telepon baru. Field lain tetap. |
| 4 Hapus | Masukkan ID, kemudian ketik `HAPUS`; jawaban lain membatalkan. |
| 5 Cari | Cari sebagian nama/email; `%` dan `_` merupakan teks biasa. |
| 0 Keluar | Akhiri program. Database tetap berjalan dan data tersimpan pada volume. |

Gunakan tanggal `YYYY-MM-DD`, email contoh seperti `demo@example.test`, serta password contoh 12 sampai 128 karakter. Input password pada menu terminal ini terlihat saat diketik; gunakan data latihan. Nama/alamat dapat memuat spasi. Input tidak valid kembali ke menu tanpa mengklaim perubahan berhasil. Akhir input di tengah form membatalkan form yang belum lengkap.

```powershell
docker compose logs --tail=60 db
docker compose down
```

`down` mempertahankan volume database. Schema hanya dijalankan ketika volume baru dibuat. Mengganti password `.env` tidak otomatis mengganti password akun MySQL yang sudah terbentuk.

## Build tanpa Docker

Gunakan JDK 17 dan Maven 3.9:

```powershell
mvn verify
```

Hasilnya `target/java-console-crud.jar`, sudah berisi driver MySQL. Tes memakai H2 dalam memori, bukan database asal. Siapkan MySQL baru dengan `database/001-schema.sql`, lalu atur environment pada terminal yang akan menjalankan aplikasi:

```powershell
$env:DB_URL = 'jdbc:mysql://localhost:3306/console_crud_demo?connectionTimeZone=UTC'
$env:DB_USER = 'console_crud'
$env:DB_PASSWORD = 'isi-password-database-demo-lokal'
java -jar target/java-console-crud.jar
```

`.env` dibaca Docker Compose; Java mandiri memakai environment proses. Tidak ada migrasi otomatis database lama. Gunakan schema baru yang terpisah agar percobaan tidak mengubah data asli.

Program membaca input UTF-8. Di terminal Windows lama, jalankan `chcp 65001` sebelum menjalankan JAR agar nama yang memakai aksara non-ASCII terbaca dengan benar.

## Susunan kode

```text
src/main/java/id/javacrud/
  Main.java                 Entry point dan environment
  ConsoleApp.java           Menu dan input/output terminal
  Contact.java              Data pengguna tanpa field password
  ContactRepository.java    Query JDBC berparameter dan penutupan resource
  Validation.java           Validasi input
  Passwords.java            PBKDF2 dengan salt acak
src/test/java/id/javacrud/   Tes repository, menu dan kegagalan input/database
database/001-schema.sql     Schema kosong untuk data latihan
```

Versi asal memiliki query update yang tidak valid, query gabungan input, password pada output daftar, dan beberapa koneksi yang tidak ditutup. Perbaikannya mempertahankan cakupan latihan tanpa menambahkan klaim sistem autentikasi atau aplikasi apotek lengkap. Lihat [NOTICE.md](NOTICE.md) untuk atribusi **midha** dan asal sumber.
