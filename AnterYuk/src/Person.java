/* Nama file    : Person.java
 * Deskripsi    : program class Person
 * Pembuat      : Arsy Thariq Munawar
 * NIM          : 24060124120009
 * Tanggal      : 27 Maret 2026
 * */

public abstract class Person{
    /* ATRIBUT */
    protected String id;
    protected String nama;
    protected String email;
    protected String noHp;

    /* METHOD */
    /* KONSTRUKTOR */
    public Person(String id, String nama, String email, String noHp) {
        this.id    = id;
        this.nama  = nama;
        this.email = email;
        this.noHp  = noHp;
    }

    public Person(){
        id = "";
        nama = "";
        email = "";
        noHp = "";
    }

    /* SELEKTOR */
    public String getId(){ 
        return id; 
    }

    public String getNama(){ 
        return nama; 
    }

    public String getEmail(){ 
        return email; 
    }

    public String getNoHp(){ 
        return noHp; 
    }

    /* MUTATOR */
    //boleh ganti Username aplikasi
    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    /* METHOD LAINNYA */
    // abstract class untuk menampilkan info person
    public abstract void displayInfo();




    
}