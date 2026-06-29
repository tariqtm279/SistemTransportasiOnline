/* Nama file    : Driver.java
 * Deskripsi    : program class Driver
 * Pembuat      : Arsy Thariq Munawar
 * NIM          : 24060124120009
 * Tanggal      : 27 Maret 2026
 * */

public class Driver extends Person{
    /* ATRIBUT */
    private String noSIM;
    private boolean tersedia;
    private double rating;
    private int ratingCount; // untuk menghitung jumlah rating yang diterima

    /* METHOD */
    /* KONSTRUKTOR */
    public Driver(String id, String nama, String email, String noHp, String noSIM) {
        super(id, nama, email, noHp);
        this.noSIM     = noSIM;
        this.tersedia  = true;
        this.rating    = 0.0;
        this.ratingCount = 0;
    }

    public Driver(){
        super();
        this.noSIM = "";
        this.tersedia = true;
        this.rating = 0.0;
        this.ratingCount = 0;
    }

    /* SELEKTOR */
    public String getNoSIM(){
        return noSIM;
    }

    public double getRating(){ 
        return rating; 
    }

    public int getRatingCount(){
        return ratingCount;
    }

    /* MUTATOR */
    public void setTersedia(boolean status){
        this.tersedia = status;
    }

    public boolean isTersedia(){
        return tersedia;
    }

    /* METHOD LAINNYA */
    // mengupdate rating driver
    public void updateRating(double skorBaru) {
        assert skorBaru >= 1 && skorBaru <= 5 : "Rating harus antara 1 dan 5";
        ratingCount++;
        rating = ((rating * (ratingCount - 1)) + skorBaru) / ratingCount;
    }

    public void terimaPerjalanan() throws DriverNotAvailableException {
        if (!tersedia) {
            throw new DriverNotAvailableException("Driver " + nama + " sedang tidak tersedia!");
        }
        tersedia = false;
        System.out.println("Driver " + nama + " menerima pesanan.");
    }

    public void selesaiPerjalanan() {
        tersedia = true;
        System.out.println("Driver " + nama + " sudah selesai bertugas.");
    }

    // Override displayInfo() dari Person
    @Override
    public void displayInfo() {
        System.out.println("ID      : " + id);
        System.out.println("Driver  : " + nama);
        System.out.println("Email   : " + email);
        System.out.println("No. HP  : " + noHp);
        System.out.println("No. SIM : " + noSIM);
        if (rating == 0.0) {
            System.out.println("Rating : N/A");
        } else {
            System.out.println("Rating : " + rating);
        }
        if (tersedia) {
            System.out.println("Status : Tersedia");
        } else {
            System.out.println("Status : Sedang Bertugas");
        }
    }


}