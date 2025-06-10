// File: com/example/apodicty/SqlDatabase/User.java atau com/example/apodicty/model/User.java

package com.example.apodicty.data.sqlitedatabase.database.user; // Atau com.example.apodicty.model;

public class User {
    private String email;
    private String username;
    private String namaLengkap;
    private String tanggalLahir;
    private String instansi;
    private String quotes;

    // Constructor kosong (opsional, tapi berguna untuk beberapa framework)
    public User() {
    }

    // Constructor dengan semua field
    public User(String email, String username, String namaLengkap,
                String tanggalLahir, String instansi, String quotes) {
        this.email = email;
        this.username = username;
        this.namaLengkap = namaLengkap;
        this.tanggalLahir = tanggalLahir;
        this.instansi = instansi;
        this.quotes = quotes;
    }

    // --- Getter Methods ---
    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public String getInstansi() {
        return instansi;
    }

    public String getQuotes() {
        return quotes;
    }

    // --- Setter Methods (jika Anda butuh untuk mengubah nilai setelah objek dibuat) ---
    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public void setTanggalLahir(String tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }

    public void setInstansi(String instansi) {
        this.instansi = instansi;
    }

    public void setQuotes(String quotes) {
        this.quotes = quotes;
    }
}