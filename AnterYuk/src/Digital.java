/* Nama file    : Digital.java
 * Deskripsi    : program class Digital
 * Pembuat      : Shalom Kurniawan
 * NIM          : 24060124120033
 * Tanggal      : 27 Maret 2026
 * */

import java.time.LocalDate;

public class Digital extends Pembayaran{
    /* ATRIBUT */
    private double biayaAdmin;
    private String provider;

    /* METHOD */
    /* KONSTRUKTOR */
    public Digital(){
        biayaAdmin = 0;
        provider = "";
    }

    //assertion
    public Digital(Penumpang customer, LocalDate waktu, String kode, Perjalanan jalan, Voucher tiket, Kendaraan vehicle, double admin, String provider){
        super(customer, waktu, kode, jalan, tiket, vehicle);
        assert (admin >= 0) : "Biaya admin tidak boleh negatif!";
        biayaAdmin = admin;
        this.provider = provider;
    }

    /* SELEKTOR */
    public double getBiayaAdmin(){
        return biayaAdmin;
    }
    

    public String getProvider(){
        return provider;
    }

    /* MUTATOR */
    public void setProvider(String provider){
        this.provider = provider;
    }

    //assertion
    public void setBiayaAdmin(double admin){
        assert (admin > 0) : "Biaya admin tidak mungkin negatif";
        biayaAdmin = admin;
    }

    /* METHOD LAINNYA */
    // method untuk menghitung tarif final setelah memakai voucher
    @Override
    public int getTarifFinal(){
        int temp = getTarif() + (int)getBiayaAdmin();
        if(!getVoucher().getSudahDipakai()){
            temp -= (int)(temp * getVoucher().getDiskon());
        }

        return temp;
    }

    // method untuk menampilkan struk atau bukti pembayaran
    @Override
    public void printReceipt() throws Exception{
        super.printReceipt();
        System.out.println("Biaya admin             : Rp " + getBiayaAdmin());
        System.out.println("Provider                : " + getProvider());
    }


}