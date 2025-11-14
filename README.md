# SmartFarm SaaS

SmartFarm SaaS adalah aplikasi Spring Boot yang dirancang untuk mengelola operasi pertanian cerdas sebagai platform Software-as-a-Service (SaaS). Aplikasi ini menyediakan fungsionalitas untuk manajemen penyewa (tenant), memungkinkan berbagai organisasi untuk menggunakan platform secara independen.

## Fitur

- **Manajemen Penyewa**: Operasi CRUD (Create, Read, Update, Delete) untuk mengelola penyewa.
- **Endpoint API**: API RESTful untuk berinteraksi dengan platform.
- **Penanganan Pengecualian Global**: Penanganan kesalahan yang kuat untuk pengalaman pengguna yang lebih baik.
- **Konfigurasi Keamanan**: Pengaturan keamanan dasar untuk akses API.

## Teknologi yang Digunakan

- **Spring Boot**: Framework untuk membangun aplikasi yang kuat dan siap produksi.
- **Spring Data JPA**: Untuk interaksi database dan manajemen repositori.
- **Maven**: Alat manajemen dependensi dan otomatisasi build.
- **PostgreSQL**: Database relasional open-source yang kuat.
- **Lombok**: Untuk mengurangi boilerplate code.

## Memulai

Instruksi ini akan membantu Anda mendapatkan salinan proyek dan menjalankannya di mesin lokal Anda untuk tujuan pengembangan dan pengujian.

### Prasyarat

- Java 21 atau lebih tinggi
- Maven 3.9.x atau lebih tinggi
- PostgreSQL 16 terinstal dan berjalan

### Instalasi

1. **Kloning repositori:**

   ```bash
   git clone https://github.com/koswara-dev/smartfarm.git
   cd smartfarm
   ```

2. **Bangun proyek:**

   ```bash
   mvn clean install
   ```

### Menjalankan Aplikasi

Anda dapat menjalankan aplikasi menggunakan Maven:

```bash
mvn spring-boot:run
```

Aplikasi akan berjalan di `http://localhost:8080` secara default.

## Endpoint API

Aplikasi ini mengekspos beberapa endpoint API RESTful. Anda dapat menggunakan alat seperti Postman atau curl untuk berinteraksi dengannya.

### Manajemen Penyewa

- `POST /api/v1/tenants`: Membuat penyewa baru.
- `GET /api/v1/tenants`: Mendapatkan semua penyewa.
- `GET /api/v1/tenants/{id}`: Mendapatkan penyewa berdasarkan ID.
- `PUT /api/v1/tenants/{id}`: Memperbarui penyewa yang sudah ada.
- `DELETE /api/v1/tenants/{id}`: Menghapus penyewa berdasarkan ID.

## Struktur Proyek

```
.
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── id
│   │   │       └── petani
│   │   │           └── smartfarm
│   │   │               ├── SmartfarmApplication.java
│   │   │               ├── config
│   │   │               │   └── SecurityConfig.java
│   │   │               ├── controller
│   │   │               │   └── TenantController.java
│   │   │               ├── dto
│   │   │               │   ├── ApiResponse.java
│   │   │               │   ├── TenantRequestDTO.java
│   │   │               │   └── TenantResponseDTO.java
│   │   │               ├── exception
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   └── ResourceNotFoundException.java
│   │   │               ├── model
│   │   │               │   └── Tenant.java
│   │   │               ├── repository
│   │   │               │   └── TenantRepository.java
│   │   │               └── service
│   │   │                   └── TenantService.java
│   │   └── resources
│   │       └── application.properties
│   └── test
│       └── java
│           └── id
│               └── petani
│                   └── smartfarm
│                       └── SmartfarmApplicationTests.java
└── README.md
```

## Konfigurasi

File `application.properties` berisi konfigurasi aplikasi.

```properties
# Server Port
server.port=8080

# PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/smartfarmdb
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Pastikan untuk mengganti `your_username` dan `your_password` dengan kredensial database PostgreSQL Anda yang sebenarnya.

## Kontribusi

Kontribusi sangat diterima! Jangan ragu untuk mengirimkan pull request atau membuka issue.

## Lisensi

[Sebutkan lisensi Anda di sini, misal: Lisensi MIT]

## Buy me a coffe

If you like this project and want to support its further development, buy me a coffee!

[![Buy Me a Coffee](https://www.buymeacoffee.com/assets/img/guidelines/download-assets-sm-1.svg)](https://www.buymeacoffee.com/kudajengke404)