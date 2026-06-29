/* Nama file    : Laporan.java
 * Deskripsi    : Demonstrasi POLIMORFISME
 *                - Method overriding: getTarifFinal() di Cash dan Digital
 *                - Method overloading: cetak() dengan parameter berbeda
 *                - Upcasting & Downcasting
 * Konsep       : Polimorfisme
 * */

public class Laporan {

    // =====================================================================
    // 1. POLIMORFISME via UPCASTING
    //    Objek Cash/Digital disimpan sebagai tipe parent Pembayaran.
    //    Saat dipanggil, JVM menentukan method mana yang dieksekusi
    //    berdasarkan tipe objek ASLI (dynamic dispatch / late binding).
    // =====================================================================
    public static void tampilkanTarifFinal(Pembayaran p) {
        // Meskipun parameter bertipe Pembayaran (abstract),
        // yang dipanggil adalah getTarifFinal() milik Cash atau Digital
        // sesuai objek yang dikirim → ini POLIMORFISME runtime
        System.out.println("Tarif final [" + p.getKodeTransaksi() + "] : Rp " + p.getTarifFinal());
    }

    // =====================================================================
    // 2. POLIMORFISME via METHOD OVERLOADING (compile-time polymorphism)
    //    Method cetak() dipanggil dengan parameter berbeda.
    //    Compiler memilih versi yang tepat berdasarkan argumen.
    // =====================================================================
    public static void cetak(String judul) {
        System.out.println("===== " + judul + " =====");
    }

    public static void cetak(String judul, int lebar) {
        String garis = "=".repeat(lebar);
        System.out.println(garis);
        System.out.println("  " + judul);
        System.out.println(garis);
    }

    public static void cetak(String judul, String isi) {
        System.out.println("[" + judul + "] " + isi);
    }

    // =====================================================================
    // 3. POLIMORFISME via INTERFACE
    //    Perjalanan mengimplementasikan interface Pajak.
    //    Variabel bertipe Pajak bisa menampung objek Perjalanan.
    // =====================================================================
    public static void tampilkanPajak(Pajak objekBerkena) {
        // Semua objek yang implement Pajak bisa masuk sini
        System.out.println("Pajak (PPN 10%): Rp " + (int) objekBerkena.hitungPajak());
    }

    // =====================================================================
    // 4. DOWNCASTING — mengecek tipe asli dan cast kembali ke subclass
    // =====================================================================
    public static void detailPembayaran(Pembayaran p) {
        System.out.println("Kode: " + p.getKodeTransaksi());

        if (p instanceof Digital) {
            // Downcast ke Digital agar bisa akses getProvider() dan getBiayaAdmin()
            Digital d = (Digital) p;
            System.out.println("  → Tipe: Digital | Provider: " + d.getProvider()
                    + " | Admin: Rp " + (int) d.getBiayaAdmin());
        } else if (p instanceof Cash) {
            System.out.println("  → Tipe: Cash (tanpa biaya admin)");
        }
    }
}
