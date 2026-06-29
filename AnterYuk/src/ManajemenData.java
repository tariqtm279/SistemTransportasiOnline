/* Nama file    : ManajemenData.java
 * Deskripsi    : Demonstrasi KOLEKSI (Collections Framework)
 *                - ArrayList  : daftar driver/penumpang
 *                - HashMap    : mapping kode→pembayaran
 *                - LinkedList : antrian perjalanan
 *                - TreeMap    : rating driver (terurut otomatis)
 *                - Iterator   : traversal manual
 * Konsep       : Koleksi
 * */

import java.util.*;

public class ManajemenData {

    // =====================================================================
    // 1. ArrayList<Driver>
    //    - Ordered, index-based, allows duplicates
    //    - O(1) akses by index, O(n) insert/delete di tengah
    //    - Paling umum dipakai saat urutan penting
    // =====================================================================
    private ArrayList<Driver> daftarDriver = new ArrayList<>();

    // =====================================================================
    // 2. ArrayList<Penumpang>
    //    Sama seperti driver, mudah ditambah/hapus di akhir list
    // =====================================================================
    private ArrayList<Penumpang> daftarPenumpang = new ArrayList<>();

    // =====================================================================
    // 3. HashMap<String, Pembayaran>
    //    - Key-value pairs, akses O(1) rata-rata via hash
    //    - Key: kode transaksi (unik), Value: objek Pembayaran
    //    - Tidak ordered (urutan tidak dijamin)
    // =====================================================================
    private HashMap<String, Pembayaran> mapPembayaran = new HashMap<>();

    // =====================================================================
    // 4. LinkedList<Perjalanan> sebagai QUEUE (antrian)
    //    - Doubly-linked list, efisien insert/delete di awal & akhir
    //    - offer() = masuk antrian, poll() = keluar dari depan antrian
    //    - Cocok untuk simulasi antrian perjalanan
    // =====================================================================
    private LinkedList<Perjalanan> antrianPerjalanan = new LinkedList<>();

    // =====================================================================
    // 5. TreeMap<Double, String>
    //    - Seperti HashMap tapi SORTED berdasarkan key (natural order)
    //    - Cocok untuk leaderboard rating (terurut dari kecil ke besar)
    //    - Key: rating, Value: nama driver
    // =====================================================================
    private TreeMap<Double, String> leaderboardRating = new TreeMap<>(Collections.reverseOrder());

    // -------------------------------------------------------------------
    // METODE OPERASI DRIVER
    // -------------------------------------------------------------------
    public void tambahDriver(Driver d) {
        daftarDriver.add(d);
    }

    public Driver cariDriverById(String id) {
        // Menggunakan enhanced for loop → di balik layar pakai Iterator
        for (Driver d : daftarDriver) {
            if (d.getId().equals(id)) return d;
        }
        return null;
    }

    public List<Driver> driverTersedia() {
        // Collections.unmodifiableList → hasil tidak bisa diubah dari luar
        List<Driver> hasil = new ArrayList<>();
        for (Driver d : daftarDriver) {
            if (d.isTersedia()) hasil.add(d);
        }
        return Collections.unmodifiableList(hasil);
    }

    // -------------------------------------------------------------------
    // METODE OPERASI PENUMPANG
    // -------------------------------------------------------------------
    public void tambahPenumpang(Penumpang p) {
        daftarPenumpang.add(p);
    }

    public void hapusPenumpang(Penumpang p) {
        // remove() menggunakan equals() → hapus pertama yang cocok
        boolean berhasil = daftarPenumpang.remove(p);
        System.out.println("Hapus penumpang " + p.getNama() + ": " + (berhasil ? "sukses" : "gagal"));
    }

    // Sorting menggunakan Comparator (lambda)
    public void sortPenumpangByNama() {
        // Collections.sort dengan Comparator → algoritma TimSort O(n log n)
        Collections.sort(daftarPenumpang, (a, b) -> a.getNama().compareTo(b.getNama()));
    }

    // -------------------------------------------------------------------
    // METODE OPERASI PEMBAYARAN (HashMap)
    // -------------------------------------------------------------------
    public void tambahPembayaran(Pembayaran p) {
        // put(key, value) → jika key sudah ada, value lama ditimpa
        mapPembayaran.put(p.getKodeTransaksi(), p);
    }

    public Pembayaran getPembayaranByKode(String kode) {
        // get() → O(1) average case
        return mapPembayaran.get(kode);
    }

    public void tampilSemuaPembayaran() {
        System.out.println("--- Semua Pembayaran ---");
        // entrySet() → iterasi semua pasangan key-value
        for (Map.Entry<String, Pembayaran> entry : mapPembayaran.entrySet()) {
            System.out.println("  Kode: " + entry.getKey()
                    + " | Tarif Final: Rp " + entry.getValue().getTarifFinal()
                    + " | Tipe: " + entry.getValue().getClass().getSimpleName());
        }
    }

    // -------------------------------------------------------------------
    // METODE OPERASI ANTRIAN (LinkedList/Queue)
    // -------------------------------------------------------------------
    public void masukAntrian(Perjalanan p) {
        antrianPerjalanan.offer(p);  // offer = add ke ekor antrian
        System.out.println("Perjalanan " + p.getIDPerjalanan() + " masuk antrian. Posisi: " + antrianPerjalanan.size());
    }

    public Perjalanan prosesAntrian() {
        // poll() = ambil dari kepala antrian (FIFO), null jika kosong
        Perjalanan p = antrianPerjalanan.poll();
        if (p != null) {
            System.out.println("Memproses perjalanan: " + p.getIDPerjalanan());
        } else {
            System.out.println("Antrian kosong.");
        }
        return p;
    }

    public int sizeAntrian() {
        return antrianPerjalanan.size();
    }

    // -------------------------------------------------------------------
    // METODE OPERASI LEADERBOARD (TreeMap)
    // -------------------------------------------------------------------
    public void updateLeaderboard(Driver d) {
        if (d.getRatingCount() > 0) {
            // TreeMap sorted by key → rating tertinggi tampil pertama (reverseOrder)
            leaderboardRating.put(d.getRating(), d.getNama());
        }
    }

    public void tampilLeaderboard() {
        System.out.println("--- Leaderboard Rating Driver ---");
        int rank = 1;
        // TreeMap sudah terurut → langsung iterate
        for (Map.Entry<Double, String> entry : leaderboardRating.entrySet()) {
            System.out.printf("  #%d %-15s %.2f  * %n", rank++, entry.getValue(), entry.getKey());
        }
    }

    // -------------------------------------------------------------------
    // DEMO ITERATOR MANUAL
    //    Iterator adalah design pattern yang memisahkan traversal dari struktur data.
    //    Java Collection otomatis menyediakan iterator().
    // -------------------------------------------------------------------
    public void demoIterator() {
        System.out.println("--- Iterasi Manual Driver (via Iterator) ---");
        Iterator<Driver> it = daftarDriver.iterator();
        while (it.hasNext()) {                  // hasNext() cek apakah ada elemen lagi
            Driver d = it.next();               // next() ambil elemen berikutnya
            System.out.println("  " + d.getNama() + " | Rating: " +
                    (d.getRatingCount() > 0 ? d.getRating() : "N/A") +
                    " | Tersedia: " + d.isTersedia());
        }
    }

    // Getter untuk keperluan lain
    public ArrayList<Driver> getDaftarDriver() { return daftarDriver; }
    public ArrayList<Penumpang> getDaftarPenumpang() { return daftarPenumpang; }
}
