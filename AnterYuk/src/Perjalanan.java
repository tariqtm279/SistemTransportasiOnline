/* Nama file    : Perjalanan.java
 * Deskripsi    : program class Perjalanan
 * Pembuat      : Misbachul Munir
 * NIM          : 24060124120031
 * Tanggal      : 27 Maret 2026
 * */

public class Perjalanan implements Pajak{
    /***********ATRIBUT************/
    protected String idPerjalanan;
    protected double jarak;
    // protected int tarifKendaraan; //tarif dasar; motor sekian, mobil sekian
    protected static int hargaPerkm = 5000; // asumsi harga konstan
    protected Kendaraan kendaraan;
    protected Driver driver;
    protected Penumpang penumpang;

    /***********METHOD************/
    /* KONSTRUKTOR */
    public Perjalanan(String idPerjalanan, Penumpang penumpang, Driver driver, double jarak, Kendaraan kendaraan) throws IllegalArgumentException {
        if(jarak <= 0.0){
            throw new IllegalArgumentException("Jarak harus lebih dari 0!");
        }
        this.jarak = jarak;
        this.idPerjalanan = idPerjalanan;
        this.penumpang = penumpang;
        this.driver = driver;
        this.kendaraan = kendaraan;
    }

    public Perjalanan(){
        idPerjalanan = "";
        jarak = 0.0;
    }

    /* MUTATOR */
    public void setJarak(double jarak) throws IllegalArgumentException{
        if(jarak <= 0.0){
            throw new IllegalArgumentException("Jarak harus lebih dari 0!");
        }
        this.jarak = jarak;
    }

    public void setIDPerjalanan(String id){
        idPerjalanan = id;
    }

    /* SELEKTOR */
    // mengambil info jarak
    public double getJarak(){
        return jarak;
    }

    public int getHargaPerkm(){
        return hargaPerkm;
    }

    // mengambil info idperjalanan
    public String getIDPerjalanan(){
        return idPerjalanan;
    }

    /* METHOD LAINNYA */
    @Override
    public double hitungPajak(){
        return (getJarak() * getHargaPerkm() + kendaraan.getTarifKendaraan()) * 0.1;
    }

    // Menghitung tarifKendaraan dari perjalanan dengan asumsi setiap jarak dalam satu kilometer di hargai dengan 2000
    public int hitungTarif() {
        // assertion
        assert getJarak() > 0 : "Jarak tidak valid!";
        int tarif = (int)(hitungPajak() + (hitungPajak() * 10));

        return tarif;
    }

    public void selesai() {
        driver.selesaiPerjalanan();
        System.out.println("Perjalanan " + idPerjalanan + " selesai.");
    }

    // method menampilkan info perjalanan
    public void printInfo() {
        System.out.println("ID Perjalanan   : " + getIDPerjalanan());
        System.out.println("Nama Penumpang  : " + penumpang.getNama());
        System.out.println("Nama Driver     : " + driver.getNama());
        System.out.println("Kendaraan       : " + kendaraan.getJenis() + " - " + kendaraan.getModel());
        if(kendaraan instanceof Mobil){
            Mobil mobil = (Mobil) kendaraan;
            System.out.println("Tipe            : " + mobil.getTipeMobil());
        }
        System.out.println("Jarak           : " + getJarak() + " km");
    }

}