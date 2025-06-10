# Apolib: Asisten Informasi Obat Pribadi Andal Anda

Halo! Selamat datang di repositori Apolib. Aplikasi Android ini adalah kawan cerdas dan terpercaya yang dirancang untuk membantu Anda mengakses informasi obat-obatan dengan mudah dan komprehensif. Apolib hadir untuk menjadi asisten pribadi Anda, memberikan pemahaman yang lebih baik tentang berbagai medikasi, membantu Anda mengatur daftar obat favorit, dan senantiasa memberikan informasi terkini seputar dunia farmasi. Baik Anda seorang profesional kesehatan, mahasiswa di bidang medis, atau sekadar individu yang ingin meningkatkan pengetahuan tentang obat-obatan, Apolib hadir untuk menyederhanakan kompleksitas informasi tersebut.

# Mengapa Memilih Apolib?
Di tengah lautan informasi kesehatan yang luas, Apolib menonjol dengan beberapa keunggulan utama:

* Sumber Data yang Luas: Kami menyediakan akses ke basis data obat yang terus diperbarui, mencakup berbagai jenis dan merek obat yang tersedia di pasar.
* Pencarian Cepat dan Akurat: Temukan obat yang Anda cari hanya dengan beberapa ketukan. Fitur pencarian cerdas kami mendukung pencarian berdasarkan nama generik, nama dagang, bahkan komposisi atau indikasi penggunaan.
* Informasi yang Terstruktur: Setiap detail obat disajikan dalam format yang jelas dan mudah dipahami. Mulai dari kandungan aktif, indikasi penggunaan, dosis yang dianjurkan, hingga potensi efek samping dan kontraindikasi, semua ada dalam genggaman Anda.
* Personalisasi dengan Fitur Favorit: Tandai obat-obatan yang sering Anda gunakan atau minati ke dalam daftar favorit pribadi. Ini memungkinkan akses cepat bahkan saat Anda sedang offline.
* Pengalaman Pengguna yang Nyaman: Nikmati kenyamanan visual dalam berbagai kondisi pencahayaan berkat dukungan mode terang dan gelap yang bisa diaktifkan kapan saja.
* Performa Optimal: Semua operasi yang memakan waktu, seperti pencarian data kompleks atau interaksi database, dijalankan di latar belakang (background), menjaga aplikasi tetap responsif dan bebas lag.
# Fitur Unggulan
Apolib dilengkapi dengan serangkaian fitur yang dirancang untuk memperkaya pengalaman Anda dalam mencari dan mengelola informasi obat:

* Jelajahi Basis Data Obat: Telusuri ribuan informasi obat yang terklasifikasi dengan baik.
* Pencarian dan Filter Cerdas: Saring hasil pencarian berdasarkan jenis obat (misalnya, OTC, Obat Resep, Terapi Seluler) dan status verifikasi.
* Manajemen Daftar Obat Favorit: Tambah, hapus, dan kelola obat-obatan yang Anda simpan untuk referensi cepat dan akses offline.
* Profil Pengguna yang Lengkap: Kelola detail akun Anda, termasuk kemampuan untuk menghubungkan akun Google Anda untuk pengalaman yang lebih terintegrasi.
* Antarmuka Pengguna Intuitif: Desain yang bersih dan navigasi yang mudah membuat penggunaan Apolib menjadi pengalaman yang menyenangkan.
* Informasi Obat Mendalam: Dapatkan detail klinis dan pendukung yang komprehensif seperti bahan aktif, indikasi, peringatan, dosis, efek samping, dan banyak lagi.
* Panduan Pengguna Terintegrasi: Butuh bantuan? Akses panduan pengguna kapan saja langsung dari aplikasi untuk memahami semua fitur Apolib.
# Contoh Tampilan Aplikasi
Inilah beberapa cuplikan layar Apolib yang menunjukkan sekilas antarmuka dan fitur-fitur utamanya:

&lt;p align="center">
&lt;img src="[tautan mencurigakan telah dihapus]" alt="Tampilan Layar Utama Apolib" style="width: 30%;">
&lt;img src="[tautan mencurigakan telah dihapus]" alt="Hasil Pencarian Obat" style="width: 30%;">
&lt;img src="[tautan mencurigakan telah dihapus]" alt="Daftar Obat Favorit" style="width: 30%;">
&lt;br>
&lt;em>Layar Utama | Hasil Pencarian | Daftar Favorit&lt;/em>
&lt;/p>

&lt;p align="center">
&lt;img src="[tautan mencurigakan telah dihapus]" alt="Detail Informasi Obat" style="width: 30%;">
&lt;img src="[tautan mencurigakan telah dihapus]" alt="Halaman Profil Pengguna" style="width: 30%;">
&lt;br>
&lt;em>Detail Obat | Halaman Profil&lt;/em>
&lt;/p>


# Teknologi yang Digunakan
Apolib dibangun di atas fondasi teknologi Android modern, memastikan stabilitas, performa, dan kemudahan pengembangan:

* Bahasa Pemrograman: Java
* Android SDK: Menggunakan fitur dan API terbaru dari Android
* UI Framework: Android Jetpack (khususnya ViewPager2, RecyclerView, dan Material Design Components untuk antarmuka yang modern dan responsif)
* Jaringan: Retrofit2 dan OkHttp3 (untuk integrasi dengan API eksternal seperti OpenFDA)
* Asinkronitas: ExecutorService dan Handler (untuk menjalankan operasi yang memakan waktu di background thread agar UI tetap responsif)
* Database Lokal: SQLite (dikelola oleh FavoriteManager dan UserManager untuk penyimpanan data profil pengguna dan obat favorit offline)
* Autentikasi: Firebase Authentication (mendukung login anonim dan integrasi Google Sign-In)
* Database Cloud: Firebase Firestore (untuk sinkronisasi data profil pengguna di cloud)
* Parsing JSON: Gson
* Pemuatan Gambar: (Jika digunakan: Glide atau Picasso untuk pemuatan gambar yang efisien)
# Instalasi dan Setup Proyek
Untuk mendapatkan Apolib dan menjalankannya di lingkungan pengembangan lokal Anda, ikuti langkah-langkah berikut:

1. Kloning Repositori:
  Buka terminal atau Git Bash Anda dan kloning repositori ini:
  Bash
  git clone https://github.com/nama-pengguna-anda/Apolib.git
  (Ganti nama-pengguna-anda dengan username GitHub Anda.)

2. Buka di Android Studio:
   * Luncurkan Android Studio.
   * Pilih Open an existing Android Studio project dari layar selamat datang.
   * Arahkan ke direktori Apolib yang baru saja Anda kloning dan klik OK.
Sinkronkan Proyek Gradle:

Android Studio akan secara otomatis mencoba menyinkronkan proyek Gradle Anda. Pastikan koneksi internet aktif untuk mengunduh semua dependensi yang diperlukan. Jika sinkronisasi tidak dimulai otomatis, klik File > Sync Project with Gradle Files.
Siapkan Firebase (Penting!):
Apolib sangat bergantung pada Firebase untuk autentikasi dan sinkronisasi data profil.

Kunjungi Firebase Console dan buat proyek baru.
Tambahkan aplikasi Android ke proyek Firebase Anda. Ikuti petunjuk di Firebase Console untuk mendaftarkan aplikasi Anda.
Unduh file konfigurasi google-services.json yang disediakan oleh Firebase.
Letakkan file google-services.json ini di dalam direktori app/ proyek Android Studio Anda.
Di Firebase Console, aktifkan layanan berikut:
Firebase Authentication (aktifkan metode Anonymous dan Google Sign-In).
Cloud Firestore.
Konfigurasi Google Sign-In (Opsional namun Direkomendasikan):

Pastikan default_web_client_id di file app/src/main/res/values/strings.xml Anda cocok dengan ID klien web yang disediakan oleh proyek Firebase Anda.
Bangun dan Jalankan Aplikasi:

Hubungkan perangkat Android fisik Anda (pastikan mode USB debugging aktif) atau luncurkan emulator Android melalui AVD Manager.
Klik tombol Run 'app' (ikon segitiga hijau) di bilah alat Android Studio.
Panduan Penggunaan Singkat
Apolib dirancang agar intuitif dan mudah digunakan. Berikut adalah gambaran singkat untuk memulai:

Navigasi: Gunakan Bilah Navigasi Bawah untuk beralih antar bagian utama aplikasi: Beranda, Cari, Favorit, dan Profil.
Beranda: Menampilkan informasi obat umum dan statistik terkini.
Cari: Temukan obat spesifik menggunakan bilah pencarian dan filter yang tersedia. Hasil akan diperbarui secara real-time.
Favorit: Kelola daftar obat favorit Anda yang telah Anda simpan. Ketuk item untuk melihat detail lengkap obat; ketuk ikon simpan/favorit di halaman detail untuk menghapus dari favorit.
Profil: Lihat status akun Anda, kelola detail profil Anda, dan akses Panduan Pengguna lengkap.
Pengalih Tema: Beralih antara mode terang dan gelap langsung dari bagian Profil untuk kenyamanan visual.
Kontribusi
Kami sangat menghargai setiap bentuk kontribusi untuk pengembangan Apolib! Jika Anda memiliki ide-ide baru, menemukan bug, atau ingin berkontribusi kode, jangan ragu untuk melakukannya.

Kami menerapkan Semantic Commits dalam proyek ini untuk menjaga riwayat commit yang bersih dan mudah dipahami. Pesan commit Anda harus mengikuti format: <tipe>[opsional(lingkup)]: <deskripsi>.
Contoh: fix(auth): Mengatasi isu autentikasi anonim atau feat(detail): Menambahkan bagian interaksi obat.

Silakan ikuti alur kontribusi standar GitHub:

Fork repositori ini ke akun GitHub Anda.
Kloning fork Anda ke mesin lokal.
Buat cabang (branch) baru untuk fitur atau perbaikan yang ingin Anda kerjakan (git checkout -b fitur/nama-fitur-anda).
Lakukan perubahan pada kode Anda.
Tulis pesan commit semantik yang jelas dan ringkas.
Push cabang Anda ke repositori fork Anda (git push origin fitur/nama-fitur-anda).
Ajukan Pull Request dari cabang Anda ke cabang main (atau master) di repositori Apolib utama. Sertakan deskripsi yang jelas tentang perubahan Anda.
Tim pengembang Apolib akan meninjau pull request Anda dan memberikan feedback jika diperlukan sebelum perubahan Anda digabungkan.

Lisensi
Proyek ini dilisensikan di bawah Lisensi MIT. Anda dapat melihat detail lengkap lisensi pada file LICENSE yang tersedia di root repositori ini.

Kontak
Jika Anda memiliki pertanyaan, saran, atau ingin berdiskusi lebih lanjut tentang Apolib, jangan ragu untuk menghubungi kami:

Username GitHub: your-github-username
Email: your.email@example.com
Terima kasih telah tertarik dengan Apolib! Kami berharap aplikasi ini dapat memberikan manfaat yang besar bagi Anda.
