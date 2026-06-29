/* Nama file    : Pembayaran.java
 * Deskripsi    : program class Pembayaran
 * Pembuat      : Shalom Kurniawan
 * NIM          : 24060124120033
 * Tanggal      : 27 Maret 2026
 * */

import java.time.LocalDate;
// import java.util.ArrayList;

public abstract class Pembayaran{
    /* ATRIBUT */
    protected int nominal_uang;
    protected LocalDate tanggal_transaksi;
    protected String kode_transaksi;
    private Perjalanan detailPerjalanan;
    private Voucher voucher;
    private Kendaraan vehicle;
    private Penumpang customer;

    /* METHOD */
    /* KONSTRUKTOR */
    //konstruktor yang memenuhi overloading
    public Pembayaran(){
        this.nominal_uang = customer.getUang();
        this.tanggal_transaksi = LocalDate.now();
        this.kode_transaksi = "";
        this.detailPerjalanan = new Perjalanan();
        this.voucher = new Voucher();
        this.vehicle = new Kendaraan();
        this.customer = new Penumpang();
    }

    public Pembayaran(Penumpang customer, LocalDate tanggal_transaksi, String kode, Perjalanan jalan, Voucher voucher, Kendaraan vehicle){
        this.nominal_uang = customer.getUang();
        this.tanggal_transaksi = tanggal_transaksi;
        this.kode_transaksi = kode;
        this.detailPerjalanan = jalan;
        this.voucher = voucher;
        this.vehicle = vehicle;
        this.customer = customer;
    }

    /* SELEKTOR */
    public Penumpang getCustomer(){
        return customer;
    }

    public void setCustomer(Penumpang cust){
        this.customer = cust;
    }

    public int getNominalUang(){
        return getCustomer().getUang();
    }

    //tidak boleh ada set nominal uang

    public LocalDate getTanggalTransaksi(){
        return tanggal_transaksi;
    }

    public String getKodeTransaksi(){
        return kode_transaksi;
    }

    /* MUTATOR */
    public void setTanggalTransaksi(LocalDate tanggal){
        tanggal_transaksi = tanggal;
    }


    public void setKodeTransaksi(String kode){
        kode_transaksi = kode;
    }

    //tidak boleh ada set nya untuk atribut dari relasi antar class, sudah di konstruktor dengan parameter
    /* SELEKTOR UNTUK RELASI ANTAR CLASS */
    public Voucher getVoucher(){
        return voucher;
    }

    public Perjalanan getDetailPerjalanan(){
        return detailPerjalanan;
    }

    public Kendaraan getVehicle(){
        return vehicle;
    }

    public int getTarif(){
        return detailPerjalanan.hitungTarif();
    }

    /* METHOD LAINNYA */
    public abstract int getTarifFinal();

    public String proses_bayar() throws Exception{
        if(getNominalUang() < getTarifFinal()){
            throw new Exception("Gagal, biaya tidak mencukupi!");
        }

        if(getVoucher() != null && !getVoucher().getSudahDipakai()){
            getVoucher().setSudahDipakai(true);
        }

        return "Pembayaran Sukses";
    }

    public void printReceipt() throws Exception{
        System.out.println("Status Pembayaran       : " + proses_bayar());
        System.out.println("ID Perjalanan           : " + detailPerjalanan.getIDPerjalanan());
        System.out.println("Nominal Pembayaran      : " + getNominalUang());
        System.out.println("Total tarif             : " + getTarifFinal());
        System.out.println("Tanggal Transaksi       : " + getTanggalTransaksi());
        System.out.println("Kode Unik Transaksi     : " + getKodeTransaksi());
        System.out.println("Voucher                 : " + getVoucher().getKodeVoucher() + ", diskon: " + getVoucher().getDiskon() * 100 + "%");
        System.out.println("Jarak perjalanan        : " + getDetailPerjalanan().getJarak() + "km");
        System.out.println("Kendaraan yang dipakai  : " + getVehicle().getJenis());
    }
}
