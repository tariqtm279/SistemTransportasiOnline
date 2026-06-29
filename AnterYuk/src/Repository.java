/* Nama file    : Repository.java
 * Deskripsi    : Demonstrasi GENERIK (Generics)
 *                - Generic class: Repository<T>
 *                - Generic method: cari()
 *                - Bounded type parameter: <T extends Person>
 *                - Generic pair: Pasangan<A, B>
 * Konsep       : Generik
 * */

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

// =====================================================================
// GENERIC CLASS — Repository<T>
// T adalah type parameter. Saat dibuat, T diganti tipe konkret.
// Contoh: Repository<Driver>, Repository<Penumpang>
// Keuntungan: SATU class bisa dipakai untuk berbagai tipe data
//             tanpa perlu casting manual → type-safe.
// =====================================================================
public class Repository<T> {
    private List<T> data = new ArrayList<>();  // internal storage
    private String namaRepo;

    public Repository(String nama) {
        this.namaRepo = nama;
    }

    // Menambah item ke repository
    public void tambah(T item) {
        data.add(item);
        System.out.println("[" + namaRepo + "] Ditambahkan: " + item);
    }

    // Menghapus item
    public boolean hapus(T item) {
        return data.remove(item);
    }

    // Mengambil item berdasarkan index
    public T get(int index) {
        return data.get(index);  // tipe T dikembalikan, tidak perlu cast
    }

    // Jumlah item
    public int ukuran() {
        return data.size();
    }

    // Menampilkan semua isi repository
    public void tampilSemua() {
        System.out.println("--- Repository: " + namaRepo + " (" + data.size() + " item) ---");
        for (T item : data) {
            System.out.println("  " + item);
        }
    }

    // =====================================================================
    // GENERIC METHOD — cari()
    // Predicate<T> adalah functional interface → bisa pakai lambda.
    // Metode ini generik, bisa mencari dengan kriteria apa pun.
    // =====================================================================
    public List<T> cari(Predicate<T> kondisi) {
        List<T> hasil = new ArrayList<>();
        for (T item : data) {
            if (kondisi.test(item)) {
                hasil.add(item);
            }
        }
        return hasil;
    }

    // =====================================================================
    // BOUNDED TYPE PARAMETER — <U extends Person>
    // Hanya tipe yang extends Person yang boleh masuk.
    // Ini membatasi T agar punya akses method getNama() dari Person.
    // =====================================================================
    public static <U extends Person> void tampilkanNama(List<U> daftar) {
        System.out.println("Daftar nama:");
        for (U orang : daftar) {
            // Bisa panggil getNama() karena U dijamin extends Person
            System.out.println("  - " + orang.getNama() + " (" + orang.getId() + ")");
        }
    }
}


// =====================================================================
// GENERIC PAIR — Pasangan<A, B>
// Menyimpan dua objek bertipe berbeda dalam satu struktur.
// Berguna untuk pasangan Driver↔Kendaraan, Penumpang↔Perjalanan, dll.
// =====================================================================
class Pasangan<A, B> {
    private A pertama;
    private B kedua;

    public Pasangan(A pertama, B kedua) {
        this.pertama = pertama;
        this.kedua = kedua;
    }

    public A getPertama() { return pertama; }
    public B getKedua()   { return kedua; }

    @Override
    public String toString() {
        return "(" + pertama + " ↔ " + kedua + ")";
    }
}
