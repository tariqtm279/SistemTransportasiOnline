/* Nama file    : Penumpang.java
 * Deskripsi    : program class Penumpang
 * Pembuat      : Arsy Thariq Munawar
 * NIM          : 24060124120009
 * Tanggal      : 27 Maret 2026
 * */

public class Penumpang extends Person{
    /* ATRIBUT */
    private String alamat;
    private int uang;

    /* METHOD */
    /* KONSTRUKTOR */
    public Penumpang(String id, String nama, String email, String noHp, String alamat, int uang) {
        super(id, nama, email, noHp);
        this.alamat = alamat;
        this.uang = uang;
    }

    public Penumpang(){
        super();
        this.alamat = "";
        this.uang = 0;
    }

    /* MUTATOR */
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public void setUang(int duit){
        this.uang = duit;
    }

    /* SELEKTOR */
    public String getAlamat(){ 
        return alamat; 
    }

    public int getUang(){
        return uang;
    }

    // Override displayInfo() dari Person
    @Override
    public void displayInfo() {
        System.out.println("ID        : " + id);
        System.out.println("Penumpang : " + nama);
        System.out.println("Email     : " + email);
        System.out.println("No. HP    : " + noHp);
        System.out.println("Alamat    : " + alamat);
        System.out.println("Uang bayar: " + uang);
    }
}
