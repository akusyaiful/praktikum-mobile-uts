package com.uts.pemesanantiket;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import android.app.Activity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends Activity {

    // Komponen input
    private EditText etNama, etJumlah, etPembayaran;
    private RadioGroup rgKategori;

    // Komponen tombol dan hasil
    private Button btnHitung, btnReset;
    private TextView tvHasil;

    // Konstanta biaya admin
    private final long BIAYA_ADMIN = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Menghubungkan komponen Java dengan XML
        etNama = findViewById(R.id.etNama);
        etJumlah = findViewById(R.id.etJumlah);
        etPembayaran = findViewById(R.id.etPembayaran);

        rgKategori = findViewById(R.id.rgKategori);

        btnHitung = findViewById(R.id.btnHitung);
        btnReset = findViewById(R.id.btnReset);

        tvHasil = findViewById(R.id.tvHasil);

        // Event tombol hitung
        btnHitung.setOnClickListener(v -> hitungPembayaran());

        // Event tombol reset
        btnReset.setOnClickListener(v -> resetForm());
    }

    // Method untuk menghitung pembayaran
    private void hitungPembayaran() {

        try {
            // Mengambil input pengguna
            String nama = etNama.getText().toString().trim();
            String jumlahInput = etJumlah.getText().toString().trim();
            String pembayaranInput = etPembayaran.getText().toString().trim();

            // Validasi nama
            if (nama.isEmpty()) {
                etNama.setError("Nama wajib diisi!");
                etNama.requestFocus();
                return;
            }

            // Validasi kategori
            int kategoriTerpilih = rgKategori.getCheckedRadioButtonId();

            if (kategoriTerpilih == -1) {
                tvHasil.setText("Silakan pilih kategori tiket terlebih dahulu.");
                return;
            }

            // Validasi jumlah tiket
            if (jumlahInput.isEmpty()) {
                etJumlah.setError("Jumlah tiket wajib diisi!");
                etJumlah.requestFocus();
                return;
            }

            int jumlahTiket = Integer.parseInt(jumlahInput);

            if (jumlahTiket < 1) {
                etJumlah.setError("Jumlah tiket minimal 1!");
                etJumlah.requestFocus();
                return;
            }

            // Menentukan kategori dan harga tiket
            String kategori;
            long hargaTiket;

            if (kategoriTerpilih == R.id.rbInternal) {
                kategori = "Mahasiswa Internal";
                hargaTiket = 50000;

            } else if (kategoriTerpilih == R.id.rbEksternal) {
                kategori = "Mahasiswa Eksternal";
                hargaTiket = 75000;

            } else if (kategoriTerpilih == R.id.rbDosen) {
                kategori = "Dosen/Tenaga Kependidikan";
                hargaTiket = 100000;

            } else {
                kategori = "Umum";
                hargaTiket = 150000;
            }

            // Menghitung subtotal
            long subtotal = hargaTiket * jumlahTiket;

            // Menentukan diskon berdasarkan jumlah tiket
            int persentaseDiskon;

            if (jumlahTiket >= 10) {
                persentaseDiskon = 15;

            } else if (jumlahTiket >= 5) {
                persentaseDiskon = 10;

            } else if (jumlahTiket >= 3) {
                persentaseDiskon = 5;

            } else {
                persentaseDiskon = 0;
            }

            // Menghitung nominal diskon
            long diskon = subtotal * persentaseDiskon / 100;

            // Menghitung total pembayaran
            long total = subtotal - diskon + BIAYA_ADMIN;

            // Validasi pembayaran
            if (pembayaranInput.isEmpty()) {
                etPembayaran.setError("Pembayaran wajib diisi!");
                etPembayaran.requestFocus();
                return;
            }

            long pembayaran = Long.parseLong(pembayaranInput);

            if (pembayaran < 0) {
                etPembayaran.setError("Pembayaran tidak boleh negatif!");
                etPembayaran.requestFocus();
                return;
            }

            // Menghitung kembalian atau kekurangan
            long selisih = pembayaran - total;

            // Menampilkan ringkasan transaksi
            StringBuilder hasil = new StringBuilder();

            hasil.append("===== RINGKASAN PEMESANAN =====\n\n");
            hasil.append("Nama: ").append(nama).append("\n");
            hasil.append("Kategori: ").append(kategori).append("\n");
            hasil.append("Harga Tiket: ").append(formatRupiah(hargaTiket)).append("\n");
            hasil.append("Jumlah Tiket: ").append(jumlahTiket).append("\n\n");

            hasil.append("Subtotal: ").append(formatRupiah(subtotal)).append("\n");
            hasil.append("Diskon (").append(persentaseDiskon).append("%): ")
                    .append(formatRupiah(diskon)).append("\n");
            hasil.append("Biaya Admin: ").append(formatRupiah(BIAYA_ADMIN)).append("\n");

            hasil.append("--------------------------------\n");
            hasil.append("TOTAL: ").append(formatRupiah(total)).append("\n\n");

            hasil.append("Pembayaran: ").append(formatRupiah(pembayaran)).append("\n");

            if (pembayaran >= total) {

                hasil.append("Kembalian: ").append(formatRupiah(selisih)).append("\n\n");
                hasil.append("STATUS: TRANSAKSI BERHASIL");

            } else {

                hasil.append("Kekurangan: ").append(formatRupiah(Math.abs(selisih))).append("\n\n");
                hasil.append("STATUS: TRANSAKSI TIDAK BERHASIL");
            }

            tvHasil.setText(hasil.toString());

        } catch (NumberFormatException e) {

            tvHasil.setText("Input angka tidak valid. Periksa kembali jumlah tiket dan pembayaran.");

        } catch (Exception e) {

            tvHasil.setText("Terjadi kesalahan: " + e.getMessage());
        }
    }

    // Method untuk mengubah angka menjadi format Rupiah
    private String formatRupiah(long angka) {

        DecimalFormatSymbols simbol = new DecimalFormatSymbols(
                new Locale("id", "ID")
        );

        simbol.setGroupingSeparator('.');

        DecimalFormat format = new DecimalFormat("#,##0", simbol);

        return "Rp" + format.format(angka);
    }

    // Method untuk mereset formulir
    private void resetForm() {

        etNama.setText("");
        etJumlah.setText("");
        etPembayaran.setText("");

        rgKategori.clearCheck();

        tvHasil.setText("Hasil transaksi akan ditampilkan di sini.");

        etNama.setError(null);
        etJumlah.setError(null);
        etPembayaran.setError(null);
    }
}
