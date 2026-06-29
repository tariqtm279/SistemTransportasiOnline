/* Nama file    : Cash.java
 * Deskripsi    : program class Cash
 * Pembuat      : Shalom Kurniawan
 * NIM          : 24060124120033
 * Tanggal      : 27 Maret 2026
 * */

import java.time.LocalDate;

public class Cash extends Pembayaran{
    /* ATRIBUT */
    
    /* METHOD */
    /* KONSTRUKTOR */
    public Cash(){
        super();
    }
    
    public Cash(Penumpang customer, LocalDate waktu, String kode, Perjalanan jalan, Voucher tiket, Kendaraan vehicle){
        super(customer, waktu, kode, jalan, tiket, vehicle);
    }

    /* SELEKTOR */
    
    /* MUTATOR */

    /* METHOD LAINNYA */
    // method untuk menghitung tarif final setelah voucher dipakai
    @Override
    public int getTarifFinal(){
        int temp = getTarif();
        if(!getVoucher().getSudahDipakai()){
            temp -= (int)(temp * getVoucher().getDiskon());
        }

        return temp;
    }

}