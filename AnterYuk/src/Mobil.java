/* Nama file    : Mobil.java 
 * Deskripsi    : program class Mobil
 * Pembuat      : Yuma Hazza Yuditama
 * NIM          : 24060124120035
 * Tanggal      : 26 Maret 2026
 * */

public class Mobil extends Kendaraan{
    /********** ATRIBUT **********/
    private String tipeMobil;
    
    /********** METHOD **********/
    /* KONSTRUKTOR */
    public Mobil(Driver driver, String PlatNomor, String Model, String tipe_mobil){
        super(driver, PlatNomor, "Mobil", Model, 0);
        if(tipe_mobil.equals("Reguler")){
            this.tipeMobil = tipe_mobil;
            this.kapasitas = 4;
            this.tarifKendaraan = 5000;
        } else if(tipe_mobil.equals("Deluxe")){
            this.tipeMobil = tipe_mobil;
            this.kapasitas = 6;
            this.tarifKendaraan = 6000;
        }
    }

    /* SELEKTOR */
    public String getTipeMobil(){
        return tipeMobil;
    }

    /* MUTATOR */
    public void setTipeMobil(String tipe_mobil){
        if(tipe_mobil.equals("Reguler") || tipe_mobil.equals("Deluxe")){
            this.tipeMobil = tipe_mobil;
        } else{
            throw new IllegalArgumentException("Tipe mobil tidak valid!");
        }
    }

    /* METHOD LAINNYA */
    @Override
    public void infoTipeMobil(){
        System.out.println("Tipe            : " + tipeMobil);
    }
}