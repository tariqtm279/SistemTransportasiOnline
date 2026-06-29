/* Nama file    : Kendaraan.java 
 * Deskripsi    : program superclass Kendaraan
 * Pembuat      : Yuma Hazza Yuditama
 * NIM          : 24060124120035
 * Tanggal      : 26 Maret 2026
 * */

public class Kendaraan{
    /********** ATRIBUT **********/
    private Driver driver;
    protected String platNomor;
    protected String jenis;
    protected String model;
    protected int kapasitas;
    protected int tarifKendaraan;

    /********** METHOD **********/
    /* KONSTRUKTOR */
    public Kendaraan(Driver driver, String PlatNomor, String Jenis, String Model, int Kapasitas){
        this.driver = driver;
        this.platNomor = PlatNomor;
        this.jenis = Jenis;
        this.model = Model;
        this.kapasitas = Kapasitas;
    }

    public Kendaraan(){
        driver = null;
        platNomor = "";
        jenis = "";
        model = "";
        kapasitas = 0;
    }


    /* SELEKTOR */
    public String getPlatNomor(){
        return platNomor;
    }

    public String getJenis(){
        return jenis;
    }

    public String getModel(){
        return model;
    }

    public int getKapasitas(){
        return kapasitas;
    }

    public int getTarifKendaraan(){
        return tarifKendaraan;
    }

    /* MUTATOR */
    public void setPlatNomor(String PlatNomor){
        this.platNomor = PlatNomor;
    }

    public void setJenis(String Jenis){
        this.jenis = Jenis;
    }

    public void setModel(String Model){
        this.model = Model;
    }

    public void setKapasitas(int Kapasitas){
        this.kapasitas = Kapasitas;
    }

    public void setTarifKendaraan(int tarif){
        tarifKendaraan = tarif;
    }

    /* METHOD LAINNYA */
    public void infoTipeMobil(){}
    
    public void printInfo(){
        System.out.println("Plat Nomor      : " + platNomor);
        System.out.println("Jenis Kendaraan : " + jenis);
        System.out.println("Model           : " + model);
        infoTipeMobil();
        System.out.println("Kapasitas       : " + kapasitas);
        System.out.println("Tarif Kendaraan : " + tarifKendaraan);
    }
}