// Class Main

import java.time.LocalDate;

public class Main{
    public static void main(String[] args){
        // Objek Penumpang
        Penumpang P1 = new Penumpang("P001", "Jeremy", "jeremiooo@gmail.com", "08182736457",
            "Jl. Anggrek No. 12", 30000);
        Penumpang P2 = new Penumpang("P002", "Danny", "ydanny@gmail.com", "08912768435",
            "Jl. Bakti No. 6", 50000);
        Penumpang P3 = new Penumpang("P003", "Elsa", "elsaelsa@gmail.com", "08137244662", 
            "Jl. Tugu No. 28", 50000);
        // Objek Driver
        Driver D1 = new Driver("D001", "Malven", "mamalvel@gmail.com", "08229349215", 
            "100-200-3000");
        Driver D2 = new Driver("D002", "Kevin", "kevinsans@gmail.com", "08229207661", 
            "110-220-3300");
        Driver D3 = new Driver("D003", "Satria", "satriabajahitam@gmail.com", "089393510032", 
            "170-294-3520");
        // Objek Motor Mobil
        Kendaraan K1 = new Motor(D1, "H 3612 IR", "Yamaha NMax");
        Kendaraan K2 = new Mobil(D2, "H 4102 PIC", "Honda Jazz", "Reguler");
        Kendaraan K3 = new Mobil(D3, "H 2656 VIO", "Toyota Innova", "Deluxe");
        try{
            ((Mobil)K3).setTipeMobil("Angkot");
        } catch(IllegalArgumentException e){
            System.out.println("Exception: " + e.getMessage());
        }
        // Objek Perjalanan
        Perjalanan GO1 = null;
        Perjalanan GO2 = null;
        Perjalanan GO3 = null;
        try{
            GO1 = new Perjalanan("GO001", P1, D1, 5, K1);
            GO2 = new Perjalanan("GO002", P2, D2, 8, K2);
            GO3 = new Perjalanan("GO003", P3, D3, 8, K3);
        }
        catch(IllegalArgumentException I){
            System.out.println("Exception: " + I.getMessage());
        }
        // Objek Voucher
        Voucher VO1 = new Voucher("V001", 0.1);
        Voucher VO2 = new Voucher("V002", 0.2);
        Voucher VO3 = new Voucher("V003", 0.3);
        // Objek Pembayaran
        Pembayaran Pay1 = new Cash(P1, LocalDate.now(), "T001", GO1, VO1, K1);
        Pembayaran Pay2 = new Cash(P2, LocalDate.now(), "T002", GO2, VO2, K2);
        Pembayaran Pay3 = new Digital(P3, LocalDate.now(), "T003", GO3, VO3, K3, 1000, "Gopay");

        System.out.println("=====DATA PENUMPANG=====");
        P1.displayInfo();
        System.out.println();
        P2.displayInfo();
        System.out.println();
        P3.displayInfo();
        System.out.println();

        System.out.println("=====DATA DRIVER=====");
        D1.displayInfo();
        System.out.println();
        D2.displayInfo();
        System.out.println();
        D3.displayInfo();
        System.out.println();


        System.out.println("=====DATA KENDARAAN=====");
        K1.printInfo();
        System.out.println();
        K2.printInfo();
        System.out.println();
        K3.printInfo();
        System.out.println();

        System.out.println("=====DATA PERJALANAN=====");
        GO1.printInfo();
        System.out.println();
        GO2.printInfo();
        System.out.println();
        GO3.printInfo();
        System.out.println();

        System.out.println("SIMULASI PERJALANAN");
        try{
            D1.terimaPerjalanan();
            D2.terimaPerjalanan();
            D1.terimaPerjalanan(); 
            System.out.println();
        } catch(DriverNotAvailableException e){
            System.out.println("Exception: " + e.getMessage());
            System.out.println();
        }

        System.out.println("SELESAI PERJALANAN");
        GO1.selesai();
        GO2.selesai();
        D1.updateRating(4.5);
        D2.updateRating(4.0);
        System.out.println();
        
        System.out.println("DATA DRIVER SETELAH PERJALANAN");
        D1.displayInfo();
        System.out.println();
        D2.displayInfo();
        System.out.println();
        
        System.out.println("=====DATA PEMBAYARAN=====");
        try{
            Pay1.printReceipt();
            System.out.println();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }

        try{
            Pay2.printReceipt();
            System.out.println();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }

        try{
            Pay3.printReceipt();
            System.out.println();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}