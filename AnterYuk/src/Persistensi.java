/* Nama file    : Persistensi.java
 * Deskripsi    : Demonstrasi PERSISTENSI (File I/O)
 *                - Menulis data ke file teks (.txt)
 *                - Membaca data dari file teks
 *                - Serialisasi manual (object → string)
 *                - Deserialisasi manual (string → object)
 *                - try-with-resources (AutoCloseable)
 * Konsep       : Persistensi
 * */

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Persistensi {

    // Path file tempat data disimpan
    private static final String FILE_DRIVER    = "data_driver.txt";
    private static final String FILE_PENUMPANG = "data_penumpang.txt";
    private static final String FILE_RIWAYAT   = "riwayat_pembayaran.txt";

    // Delimiter kolom antar field dalam satu baris
    private static final String SEP = "|";

    // =====================================================================
    // SIMPAN DRIVER ke file
    // Format baris: id|nama|email|noHp|noSIM|rating|ratingCount|tersedia
    //
    // try-with-resources → BufferedWriter otomatis close() meski ada exception.
    // FileWriter(path, true) → append mode (tidak menimpa file lama).
    // FileWriter(path, false) → overwrite mode.
    // =====================================================================
    public static void simpanDriver(List<Driver> daftarDriver) {
        // try-with-resources: resource dideklarasikan di dalam try(...)
        // Java otomatis panggil close() setelah blok selesai
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_DRIVER, false))) {
            // Header komentar di file
            bw.write("# FORMAT: id|nama|email|noHp|noSIM|rating|ratingCount|tersedia");
            bw.newLine();

            for (Driver d : daftarDriver) {
                // Serialisasi manual: objek → satu baris teks
                String baris = d.getId() + SEP
                        + d.getNama() + SEP
                        + d.getEmail() + SEP
                        + d.getNoHp() + SEP
                        + d.getNoSIM() + SEP
                        + d.getRating() + SEP
                        + d.getRatingCount() + SEP
                        + d.isTersedia();
                bw.write(baris);
                bw.newLine();   // newline agar setiap driver satu baris
            }
            System.out.println("[Persistensi] " + daftarDriver.size() + " driver berhasil disimpan ke " + FILE_DRIVER);
        } catch (IOException e) {
            System.err.println("[Persistensi] Gagal menyimpan driver: " + e.getMessage());
        }
    }

    // =====================================================================
    // MUAT DRIVER dari file
    // Membaca baris per baris, parsing setiap baris → objek Driver
    // =====================================================================
    public static List<Driver> muatDriver() {
        List<Driver> hasil = new ArrayList<>();
        File file = new File(FILE_DRIVER);

        if (!file.exists()) {
            System.out.println("[Persistensi] File " + FILE_DRIVER + " belum ada.");
            return hasil;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String baris;
            // readLine() → null jika sudah EOF
            while ((baris = br.readLine()) != null) {
                if (baris.startsWith("#") || baris.isBlank()) continue; // skip komentar & baris kosong

                // Deserialisasi: string → array field → objek Driver
                String[] field = baris.split("\\" + SEP);  // split by "|" (escape karena regex)

                if (field.length < 8) continue; // skip baris tidak lengkap

                Driver d = new Driver(field[0], field[1], field[2], field[3], field[4]);
                // Restore rating menggunakan reflection-like manual update
                double rating = Double.parseDouble(field[5]);
                int ratingCount = Integer.parseInt(field[6]);
                boolean tersedia = Boolean.parseBoolean(field[7]);

                // Simulasi restore rating (karena updateRating() mengakumulasi)
                for (int i = 0; i < ratingCount; i++) {
                    d.updateRating(rating); // approximate restore
                }
                d.setTersedia(tersedia);

                hasil.add(d);
            }
            System.out.println("[Persistensi] " + hasil.size() + " driver dimuat dari " + FILE_DRIVER);
        } catch (IOException | NumberFormatException e) {
            System.err.println("[Persistensi] Gagal memuat driver: " + e.getMessage());
        }
        return hasil;
    }

    // =====================================================================
    // SIMPAN PENUMPANG ke file
    // Format: id|nama|email|noHp|alamat|uang
    // =====================================================================
    public static void simpanPenumpang(List<Penumpang> daftarPenumpang) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PENUMPANG, false))) {
            bw.write("# FORMAT: id|nama|email|noHp|alamat|uang");
            bw.newLine();
            for (Penumpang p : daftarPenumpang) {
                String baris = p.getId() + SEP + p.getNama() + SEP
                        + p.getEmail() + SEP + p.getNoHp() + SEP
                        + p.getAlamat() + SEP + p.getUang();
                bw.write(baris);
                bw.newLine();
            }
            System.out.println("[Persistensi] " + daftarPenumpang.size() + " penumpang disimpan ke " + FILE_PENUMPANG);
        } catch (IOException e) {
            System.err.println("[Persistensi] Gagal menyimpan penumpang: " + e.getMessage());
        }
    }

    // =====================================================================
    // MUAT PENUMPANG dari file
    // =====================================================================
    public static List<Penumpang> muatPenumpang() {
        List<Penumpang> hasil = new ArrayList<>();
        File file = new File(FILE_PENUMPANG);
        if (!file.exists()) { System.out.println("[Persistensi] File belum ada."); return hasil; }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String baris;
            while ((baris = br.readLine()) != null) {
                if (baris.startsWith("#") || baris.isBlank()) continue;
                String[] f = baris.split("\\" + SEP);
                if (f.length < 6) continue;
                Penumpang p = new Penumpang(f[0], f[1], f[2], f[3], f[4], Integer.parseInt(f[5]));
                hasil.add(p);
            }
            System.out.println("[Persistensi] " + hasil.size() + " penumpang dimuat dari " + FILE_PENUMPANG);
        } catch (IOException | NumberFormatException e) {
            System.err.println("[Persistensi] Gagal memuat penumpang: " + e.getMessage());
        }
        return hasil;
    }

    // =====================================================================
    // APPEND RIWAYAT PEMBAYARAN ke file log
    // append mode (true) → tidak menimpa data lama, hanya menambah
    // Cocok untuk log/history yang terus bertambah
    // =====================================================================
    public static void catatRiwayatPembayaran(Pembayaran p) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_RIWAYAT, true))) {
            String log = p.getKodeTransaksi() + SEP
                    + p.getCustomer().getNama() + SEP
                    + p.getTanggalTransaksi() + SEP
                    + p.getTarifFinal() + SEP
                    + p.getClass().getSimpleName(); // Cash atau Digital
            bw.write(log);
            bw.newLine();
            System.out.println("[Persistensi] Riwayat pembayaran " + p.getKodeTransaksi() + " dicatat.");
        } catch (IOException e) {
            System.err.println("[Persistensi] Gagal mencatat riwayat: " + e.getMessage());
        }
    }

    // =====================================================================
    // BACA RIWAYAT PEMBAYARAN dan tampilkan
    // Menggunakan java.nio.file.Files untuk pembacaan sekaligus (modern API)
    // =====================================================================
    public static void tampilRiwayatPembayaran() {
        Path path = Paths.get(FILE_RIWAYAT);
        if (!Files.exists(path)) {
            System.out.println("[Persistensi] Belum ada riwayat.");
            return;
        }
        try {
            // Files.readAllLines() → baca seluruh file sekaligus ke List<String>
            // Cocok untuk file kecil; untuk file besar gunakan BufferedReader
            List<String> baris = Files.readAllLines(path);
            System.out.println("--- Riwayat Pembayaran ---");
            for (String b : baris) {
                if (b.isBlank()) continue;
                String[] f = b.split("\\" + SEP);
                System.out.printf("  [%s] %s | Tgl: %s | Rp %s | %s%n",
                        f[0], f[1], f[2], f[3], f[4]);
            }
        } catch (IOException e) {
            System.err.println("[Persistensi] Gagal membaca riwayat: " + e.getMessage());
        }
    }

    // =====================================================================
    // HAPUS FILE (utility)
    // =====================================================================
    public static void hapusSemuaFile() {
        for (String nama : new String[]{FILE_DRIVER, FILE_PENUMPANG, FILE_RIWAYAT}) {
            try {
                Files.deleteIfExists(Paths.get(nama));
            } catch (IOException e) {
                System.err.println("Gagal hapus " + nama);
            }
        }
        System.out.println("[Persistensi] Semua file data dihapus.");
    }
}
