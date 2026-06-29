/* Nama file    : TransportasiService.java
 * Deskripsi    : Demonstrasi MODULARISASI
 *                - Memisahkan tanggung jawab ke modul/class tersendiri
 *                - Single Responsibility Principle (SRP)
 *                - Service layer pattern
 *                - Dependency injection sederhana
 * Konsep       : Modularisasi
 * */

import java.time.LocalDate;
import java.util.List;

// =====================================================================
// TransportasiService adalah MODUL UTAMA yang mengkoordinasikan semua
// modul lain. Ia tidak tahu detail implementasi masing-masing modul,
// hanya memanggil layanan yang disediakan.
//
// PRINSIP MODULARISASI:
//   - Setiap class punya SATU tanggung jawab (Single Responsibility)
//   - Modul berkomunikasi lewat interface/method publik
//   - Perubahan di satu modul tidak merusak modul lain (low coupling)
//   - Setiap modul mudah diuji secara terpisah (high cohesion)
// =====================================================================
public class TransportasiService {

    // =====================================================================
    // DEPENDENCY INJECTION:
    // TransportasiService tidak membuat objek ManajemenData dan Persistensi sendiri.
    // Ia menerima dependensi dari luar (constructor injection).
    // Keuntungan: mudah diganti dengan mock/stub saat testing.
    // =====================================================================
    private final ManajemenData manajemenData;   // Modul: manajemen koleksi data
    private Repository<Pembayaran> repoPembayaran; // Modul: generic repository pembayaran

    public TransportasiService(ManajemenData manajemenData) {
        this.manajemenData = manajemenData;
        this.repoPembayaran = new Repository<>("Pembayaran");
    }

    // =====================================================================
    // MODUL REGISTRASI — mendaftarkan entitas ke sistem
    // Tanggung jawab: validasi dasar + simpan ke koleksi + persistensi
    // =====================================================================
    public void daftarDriver(Driver d) {
        // Validasi berada di modul ini, bukan di ManajemenData atau Persistensi
        if (d == null) {
            System.out.println("[Service] Driver tidak boleh null!");
            return;
        }
        manajemenData.tambahDriver(d);
        System.out.println("[Service] Driver " + d.getNama() + " berhasil didaftarkan.");
    }

    public void daftarPenumpang(Penumpang p) {
        if (p == null) {
            System.out.println("[Service] Penumpang tidak boleh null!");
            return;
        }
        manajemenData.tambahPenumpang(p);
        System.out.println("[Service] Penumpang " + p.getNama() + " berhasil didaftarkan.");
    }

    // =====================================================================
    // MODUL PERJALANAN — memproses pemesanan perjalanan baru
    // Tanggung jawab: cek driver tersedia, buat objek Perjalanan, update status
    // =====================================================================
    public Perjalanan pesanPerjalanan(String idPerjalanan, Penumpang penumpang,
                                      Driver driver, double jarak, Kendaraan kendaraan) {
        // Cek ketersediaan driver (delegasi ke method driver)
        if (!driver.isTersedia()) {
            System.out.println("[Service] Driver " + driver.getNama() + " tidak tersedia!");
            return null;
        }

        Perjalanan perjalanan = null;
        try {
            // Buat objek Perjalanan (bisa lempar IllegalArgumentException)
            perjalanan = new Perjalanan(idPerjalanan, penumpang, driver, jarak, kendaraan);

            // Update status driver → tidak tersedia selama perjalanan
            driver.terimaPerjalanan();

            // Masukkan ke antrian proses
            manajemenData.masukAntrian(perjalanan);

            System.out.println("[Service] Perjalanan " + idPerjalanan + " berhasil dipesan.");
        } catch (IllegalArgumentException e) {
            System.out.println("[Service] Gagal pesan: " + e.getMessage());
        } catch (DriverNotAvailableException e) {
            System.out.println("[Service] Driver tidak bisa menerima: " + e.getMessage());
        }
        return perjalanan;
    }

    // =====================================================================
    // MODUL PEMBAYARAN — memproses pembayaran setelah perjalanan
    // Tanggung jawab: buat objek Pembayaran, simpan, catat riwayat
    // =====================================================================
    public Pembayaran prosesPembayaranCash(Penumpang penumpang, Perjalanan perjalanan, Voucher voucher, Kendaraan kendaraan) {
        String kode = "T-" + System.currentTimeMillis();
        Cash bayar = new Cash(penumpang, LocalDate.now(), kode, perjalanan, voucher, kendaraan);
    
        // VALIDASI SALDO DI SINI (LOGIKA CONTROLLER)
        if (penumpang.getUang() < bayar.getTarifFinal()) {
            return null; // Mengembalikan null tanda saldo tidak cukup
        }
    
        // MANIPULASI DATA DI SINI
        penumpang.setUang(penumpang.getUang() - bayar.getTarifFinal());
        voucher.setSudahDipakai(true);
        perjalanan.selesai();
    
        repoPembayaran.tambah(bayar);
        manajemenData.tambahPembayaran(bayar);
        Persistensi.catatRiwayatPembayaran(bayar);
        return bayar;
    }

    public Pembayaran prosesPembayaranDigital(Penumpang penumpang, Perjalanan perjalanan, Voucher voucher, Kendaraan kendaraan, double admin, String provider) {
        String kode = "T-" + System.currentTimeMillis();
        Digital bayar = new Digital(penumpang, LocalDate.now(), kode, perjalanan, voucher, kendaraan, admin, provider);
    
        // VALIDASI SALDO DI SINI (LOGIKA CONTROLLER)
        if (penumpang.getUang() < bayar.getTarifFinal()) {
            return null; // Mengembalikan null tanda saldo tidak cukup
        }
    
        // MANIPULASI DATA DI SINI
        penumpang.setUang(penumpang.getUang() - bayar.getTarifFinal());
        voucher.setSudahDipakai(true);
        perjalanan.selesai();
    
        repoPembayaran.tambah(bayar);
        manajemenData.tambahPembayaran(bayar);
        Persistensi.catatRiwayatPembayaran(bayar);
        return bayar;
    }

    // =====================================================================
    // MODUL RATING — memberi rating setelah perjalanan selesai
    // Tanggung jawab: validasi nilai, update driver, update leaderboard
    // =====================================================================
    public void beriRating(Driver driver, double rating) {
        if (rating < 1 || rating > 5) {
            System.out.println("[Service] Rating harus antara 1 dan 5!");
            return;
        }
        driver.updateRating(rating);
        manajemenData.updateLeaderboard(driver);
        System.out.println("[Service] Rating " + rating + " diberikan ke " + driver.getNama());
    }

    // =====================================================================
    // MODUL PERSISTENSI — simpan & muat semua data
    // Tanggung jawab: koordinasi penyimpanan ke file (delegasi ke Persistensi)
    // =====================================================================
    public void simpanSemuaData() {
        System.out.println("[Service] Menyimpan semua data...");
        Persistensi.simpanDriver(manajemenData.getDaftarDriver());
        Persistensi.simpanPenumpang(manajemenData.getDaftarPenumpang());
        System.out.println("[Service] Semua data tersimpan.");
    }

    public void muatSemuaData() {
        System.out.println("[Service] Memuat data dari file...");
        List<Driver> drivers = Persistensi.muatDriver();
        List<Penumpang> penumpangs = Persistensi.muatPenumpang();
        for (Driver d : drivers) manajemenData.tambahDriver(d);
        for (Penumpang p : penumpangs) manajemenData.tambahPenumpang(p);
        System.out.println("[Service] Data berhasil dimuat.");
    }

    // =====================================================================
    // MODUL LAPORAN — menghasilkan berbagai laporan
    // Tanggung jawab: agregasi & tampilan data, tidak menyentuh logika bisnis
    // =====================================================================
    public void tampilLaporanLengkap() {
        System.out.println("\n========== LAPORAN SISTEM TRANSPORTASI ==========");

        // Delegasi ke modul ManajemenData
        manajemenData.demoIterator();
        System.out.println();
        manajemenData.tampilSemuaPembayaran();
        System.out.println();
        manajemenData.tampilLeaderboard();

        // Delegasi ke modul Persistensi
        System.out.println();
        Persistensi.tampilRiwayatPembayaran();

        System.out.println("=================================================\n");
    }

    // Getter untuk akses dari luar jika diperlukan
    public ManajemenData getManajemenData() { return manajemenData; }
    public Repository<Pembayaran> getRepoPembayaran() { return repoPembayaran; }

    public List<Driver> getDaftarDriver() {
        return manajemenData.getDaftarDriver();
    }

    public List<Penumpang> getDaftarPenumpang() {
        return manajemenData.getDaftarPenumpang();
    }

}