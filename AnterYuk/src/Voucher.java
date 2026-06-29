/* Nama file    : Voucher.java
 * Deskripsi    : program class Voucher
 * Pembuat      : Misbachul Munir
 * NIM          : 24060124120031
 * Tanggal      : 27 Maret 2026
 * */

public class Voucher {
    /***********ATRIBUT************/
    private String kodeVoucher;
    private double diskon; // contoh: 0.1 = 10%
    private boolean sudahDipakai;

    /***********METHOD************/
    // Konstruktor untuk membuat Voucher dengan parameter kodeVoucher dan diskon
    public Voucher(String kodeVoucher, double diskon) {
        this.kodeVoucher = kodeVoucher;
        this.diskon = diskon;
        this.sudahDipakai = false;
    }

    public Voucher(){
        kodeVoucher = "";
        diskon = 0.0;
        sudahDipakai = false;
    }

    /* SELEKTOR */
    public String getKodeVoucher() {
        return kodeVoucher;
    }
    
    public double getDiskon(){
        return diskon;
    }

    public boolean getSudahDipakai(){
        return sudahDipakai;
    }

    /* MUTATOR */
    public void setKodeVoucher(String kode){
        kodeVoucher = kode;
    }

    public void setDiskon(double diskon){
        this.diskon = diskon;
    }

    public void setSudahDipakai(boolean status){
        sudahDipakai = status;
    }

    /* METHOD LAINNYA */
    // method untuk menggunakan voucher
    public void gunakanVoucher(){
        if(!sudahDipakai){ //sudahDipakai == false
            sudahDipakai = true;
            System.out.println("Voucher " + kodeVoucher + " berhasil digunakan.");
        } else {
            System.out.println("Voucher sudah pernah dipakai!");
        }
    }

    // method untuk mencetak info dari voucher
    public void printInfo(){
        System.out.println("Kode Voucher : " + kodeVoucher);
        System.out.println("Diskon       : " + (diskon * 100) + "%");
        System.out.println("Status       : " + (sudahDipakai ? "Sudah dipakai" : "Belum dipakai"));
    }

    
}