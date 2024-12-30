/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uts;
public class Product {
    private String kodeProduk;
    private String namaProduk;
    private double hargaJual;

    public Product(String kodeProduk, String namaProduk, double hargaJual) {
        this.kodeProduk = kodeProduk;
        this.namaProduk = namaProduk;
        this.hargaJual = hargaJual;
    }

    public String getKodeProduk() {
        return kodeProduk;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public double getHargaJual() {
        return hargaJual;
    }
}
