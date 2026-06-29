/* Nama file    : MainLengkap.java
 * Deskripsi    : Program utama yang mendemonstrasikan semua konsep:
 *                1. Polimorfisme
 *                2. Generik
 *                3. Koleksi
 *                4. Persistensi
 *                5. Modularisasi
 * */
//yang ditambahkan : Laporan,manajemenData,Persistensi,Repository,transportasiService
import java.util.List;

public class MainLengkap {
    public static void main(String[] args) {

        separator("INISIALISASI DATA");

        // ---- Data Dasar ----
        Driver D1 = new Driver("D001", "Malven",  "malven@mail.com",  "081234", "100-200-300");
        Driver D2 = new Driver("D002", "Kevin",   "kevin@mail.com",   "082345", "110-220-330");
        Driver D3 = new Driver("D003", "Satria",  "satria@mail.com",  "083456", "120-240-360");

        Penumpang P1 = new Penumpang("P001", "Jeremy", "jeremy@mail.com", "081111", "Jl. A No.1", 80000);
        Penumpang P2 = new Penumpang("P002", "Danny",  "danny@mail.com",  "082222", "Jl. B No.2", 60000);
        Penumpang P3 = new Penumpang("P003", "Elsa",   "elsa@mail.com",   "083333", "Jl. C No.3", 50000);

        Kendaraan K1 = new Motor(D1, "H 3612 IR", "Yamaha NMax");
        Kendaraan K2 = new Mobil(D2, "H 4102 PIC", "Honda Jazz", "Reguler");
        Kendaraan K3 = new Mobil(D3, "H 2656 VIO", "Toyota Innova", "Deluxe");

        Voucher V1 = new Voucher("V001", 0.1);
        Voucher V2 = new Voucher("V002", 0.2);
        Voucher V3 = new Voucher("V003", 0.3);

        // ================================================================
        // BAGIAN 1: MODULARISASI
        //   Semua operasi dilakukan lewat TransportasiService.
        //   TransportasiService mengoordinasikan ManajemenData, Persistensi, dll.
        // ================================================================
        separator("1. MODULARISASI — Service Layer");

        ManajemenData db = new ManajemenData();
        TransportasiService service = new TransportasiService(db);

        // Registrasi lewat service (bukan langsung ke koleksi)
        service.daftarDriver(D1);
        service.daftarDriver(D2);
        service.daftarDriver(D3);
        service.daftarPenumpang(P1);
        service.daftarPenumpang(P2);
        service.daftarPenumpang(P3);

        // ================================================================
        // BAGIAN 2: KOLEKSI
        //   ManajemenData menggunakan ArrayList, HashMap, LinkedList, TreeMap.
        // ================================================================
        separator("2. KOLEKSI — ArrayList, HashMap, LinkedList, TreeMap");

        // ArrayList: cari driver yang tersedia
        List<Driver> tersedia = db.driverTersedia();
        System.out.println("Driver tersedia: " + tersedia.size() + " orang");

        // ArrayList sort by nama
        db.sortPenumpangByNama();
        System.out.println("Penumpang (sorted by nama):");
        for (Penumpang p : db.getDaftarPenumpang()) {
            System.out.println("  - " + p.getNama());
        }

        // LinkedList sebagai antrian: pesan perjalanan
        Perjalanan GO1 = service.pesanPerjalanan("GO001", P1, D1, 5.0, K1);
        Perjalanan GO2 = service.pesanPerjalanan("GO002", P2, D2, 8.0, K2);
        Perjalanan GO3 = service.pesanPerjalanan("GO003", P3, D3, 12.0, K3);
        System.out.println("Antrian perjalanan: " + db.sizeAntrian() + " item");

        // Proses dari antrian (FIFO)
        db.prosesAntrian();
        db.prosesAntrian();

        // ================================================================
        // BAGIAN 3: POLIMORFISME
        //   Laporan.tampilkanTarifFinal() menerima Pembayaran (parent),
        //   tapi memanggil getTarifFinal() milik Cash atau Digital.
        // ================================================================
        separator("3. POLIMORFISME — Runtime & Compile-time");

        // Pembayaran
        Pembayaran Pay1 = service.prosesPembayaranCash(P1, GO1, V1, K1);
        Pembayaran Pay2 = service.prosesPembayaranCash(P2, GO2, V2, K2);
        Pembayaran Pay3 = service.prosesPembayaranDigital(P3, GO3, V3, K3, 1500, "GoPay");

        // Polimorfisme runtime: method yang dipanggil tergantung tipe asli objek
        System.out.println("\nPolimorfisme runtime (getTarifFinal):");
        Laporan.tampilkanTarifFinal(Pay1); // Cash.getTarifFinal()
        Laporan.tampilkanTarifFinal(Pay2); // Cash.getTarifFinal()
        Laporan.tampilkanTarifFinal(Pay3); // Digital.getTarifFinal()

        // Polimorfisme compile-time: method overloading
        System.out.println("\nPolimorfisme compile-time (overloading cetak):");
        Laporan.cetak("Laporan Singkat");
        Laporan.cetak("Laporan Panjang", 40);
        Laporan.cetak("Info", "Sistem berjalan normal");

        // Polimorfisme via interface Pajak
        System.out.println("\nPolimorfisme via interface Pajak:");
        Laporan.tampilkanPajak(GO1);
        Laporan.tampilkanPajak(GO2);
        Laporan.tampilkanPajak(GO3);

        // Downcasting
        System.out.println("\nDowncasting untuk akses method spesifik:");
        Laporan.detailPembayaran(Pay1);
        Laporan.detailPembayaran(Pay3);

        // ================================================================
        // BAGIAN 4: GENERIK
        //   Repository<T> bekerja untuk tipe apa pun.
        //   Pasangan<A,B> menyimpan dua tipe berbeda.
        // ================================================================
        separator("4. GENERIK — Repository<T> dan Pasangan<A,B>");

        // Repository generik untuk Driver
        Repository<Driver> repoDriver = new Repository<>("Driver");
        repoDriver.tambah(D1);
        repoDriver.tambah(D2);
        repoDriver.tambah(D3);

        // Repository generik untuk Kendaraan
        Repository<Kendaraan> repoKendaraan = new Repository<>("Kendaraan");
        repoKendaraan.tambah(K1);
        repoKendaraan.tambah(K2);
        repoKendaraan.tambah(K3);

        repoDriver.tampilSemua();
        repoKendaraan.tampilSemua();

        // Generic method dengan bounded type parameter
        System.out.println("\nBounded Generic Method (tampilkanNama<U extends Person>):");
        Repository.tampilkanNama(db.getDaftarDriver());

        // Generic method cari() dengan Predicate (lambda)
        System.out.println("\nGeneric cari() dengan Predicate:");
        List<Driver> driverTersediaList = repoDriver.cari(d -> d.isTersedia());
        System.out.println("Driver tersedia: " + driverTersediaList.size() + " orang");

        // Generic Pair
        System.out.println("\nGeneric Pasangan<Driver, Kendaraan>:");
        Pasangan<Driver, Kendaraan> pasangan1 = new Pasangan<>(D1, K1);
        Pasangan<Driver, Kendaraan> pasangan2 = new Pasangan<>(D2, K2);
        System.out.println("  " + pasangan1.getPertama().getNama() + " ↔ " + pasangan1.getKedua().getModel());
        System.out.println("  " + pasangan2.getPertama().getNama() + " ↔ " + pasangan2.getKedua().getModel());

        // ================================================================
        // BAGIAN 5: PERSISTENSI
        //   Simpan data ke file, hapus dari memori, muat kembali dari file.
        // ================================================================
        separator("5. PERSISTENSI — File I/O");

        // Rating driver dulu agar ada data
        service.beriRating(D1, 4.5);
        service.beriRating(D2, 3.8);
        service.beriRating(D3, 4.9);
        db.tampilLeaderboard();

        // Simpan ke file
        System.out.println();
        service.simpanSemuaData();

        // Muat ulang dari file (simulasi restart aplikasi)
        System.out.println("\nSimulasi restart — muat dari file:");
        ManajemenData dbBaru = new ManajemenData();
        TransportasiService serviceBaru = new TransportasiService(dbBaru);
        serviceBaru.muatSemuaData();
        System.out.println("Driver dimuat: " + dbBaru.getDaftarDriver().size());
        System.out.println("Penumpang dimuat: " + dbBaru.getDaftarPenumpang().size());

        // Tampilkan riwayat pembayaran dari file log
        Persistensi.tampilRiwayatPembayaran();

        // ================================================================
        // LAPORAN AKHIR (koordinasi semua modul)
        // ================================================================
        service.tampilLaporanLengkap();

        // Bersihkan file setelah demo
        Persistensi.hapusSemuaFile();
    }

    // Helper method untuk separator visual
    private static void separator(String judul) {
        System.out.println("\n" + "=".repeat(55));
        System.out.println("  " + judul);
        System.out.println("=".repeat(55));
    }
}
