/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package uts;

import koneksi.koneksi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Statement;
/**
 *
 * @author user
 */
public class Transaksi extends javax.swing.JFrame {

    /**
     * Creates new form home
     */
    
    private DefaultTableModel model;
    private String username; // Variabel untuk menyimpan username
    private String role; // Variabel untuk menyimpan role
    
    private double totalKeseluruhan = 0;
    private double totalDiskon = 0;
    
    public Transaksi(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents(); 
        
     if (username == null || username.isEmpty() || role == null || role.isEmpty()) {
            lblUsername.setVisible(false); // Sembunyikan lblUsername jika username dan role kosong
        } else {
            lblUsername.setText(username);
        }
      
        
    // Mengisi txtTanggal dengan tanggal saat ini
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String currentDate = dateFormat.format(new Date());
    txtTanggal.setText(currentDate);

    // Mengisi txtWaktu dengan waktu saat ini
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    String currentTime = timeFormat.format(new Date());
    txtWaktu.setText(currentTime);
        
        // Periksa apakah username dan role null atau kosong
        if (username == null || username.isEmpty() || role == null || role.isEmpty()) {
            JOptionPane.showMessageDialog(this, "User  session is not valid.");
            dispose(); // Tutup form jika session tidak valid
            return;
        }
        model = new DefaultTableModel();
        jTable1.setModel(model);
        
        // Menambahkan kolom ke model tabel
        model.addColumn("Nama Produk");
        model.addColumn("Harga");
        model.addColumn("Jumlah");
        model.addColumn("Total");
    
        clearFields();
        
    }
     // Metode untuk memformat angka menjadi format rupiah
    private String formatRupiah(double amount) {
        Locale localeID = new Locale("id", "ID");
        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
        return format.format(amount);
    }
    
   private void loadData() {
    model.setRowCount(0); // Clear existing data
    try (Connection connection = koneksi.getConnection()) {
        String sql = "SELECT p.nama_produk, p.harga_jual " +
                     "FROM produk p"; // Mengambil data produk
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            String namaProduk = resultSet.getString("nama_produk");
            double hargaJual = resultSet.getDouble("harga_jual");

            // Menambahkan baris ke model tabel dengan format yang diinginkan
            model.addRow(new Object[]{
                namaProduk, // Nama produk
                formatRupiah(hargaJual), // Harga produk
                0, // Jumlah (default 0, bisa diubah saat menambahkan ke tabel)
                formatRupiah(0) // Total (default 0, bisa diubah saat menambahkan ke tabel)
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
    }
}

private void clearFields() {
    txtKodeProduk.setText("");
    txtNamaProduk.setText("");
    txtHarga.setText("");
    txtStok.setText("");
    txtJumlah.setText("");
    txtDiskon.setText("");
    txtBayar.setText("");
    txtKembalian.setText("");
    // Kosongkan jTable1
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    model.setRowCount(0); // Menghapus semua baris dari jTable1
}

private void cetakTransaksi() {
    String tanggal = txtTanggal.getText().trim();
    String waktu = txtWaktu.getText().trim();
    String kasir = lblUsername.getText().trim(); // Mengambil username dari label
    String kodeBarang = txtKodeProduk.getText().trim();
    String total = txtTotal.getText().trim();
    String bayar = txtBayar.getText().trim();
    String kembalian = txtKembalian.getText().trim();

    // Validasi input
    if (tanggal.isEmpty() || total.isEmpty() || bayar.isEmpty() || kembalian.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Semua field harus diisi sebelum mencetak kuitansi.");
        return;
    }

    // Ambil ID terbesar dari database
    int ID = getMaxTransactionId() + 1; // Mengambil ID terbesar dan menambah 1

    // Format bayar menjadi rupiah
    String formattedBayar = formatRupiah(Integer.parseInt(bayar));

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Simpan Kuitansi");
    fileChooser.setSelectedFile(new File("Kuitansi" + ID + "_" + tanggal.replace("-", "") + "_" + waktu.replace(":", "") + ".pdf"));

    int userSelection = fileChooser.showSaveDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToSave = fileChooser.getSelectedFile();

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
            document.open();

            // Menambahkan nama toko dan header
            Paragraph storeName = new Paragraph("TOKO RAMAYANI", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            Paragraph storeAddress = new Paragraph("Jl. Kenangan No.1 \n Madiun\n\n", FontFactory.getFont(FontFactory.HELVETICA, 12));
            Paragraph garis2 = new Paragraph("==========================================================================");
            Paragraph garis1 = new Paragraph("---------------------------------------------------------------------------");
            storeName.setAlignment(Element.ALIGN_CENTER);
            storeAddress.setAlignment(Element.ALIGN_CENTER);
            garis1.setAlignment(Element.ALIGN_CENTER);
            garis2.setAlignment(Element.ALIGN_CENTER);
            document.add(storeName);
            document.add(storeAddress);
            document.add(garis2);

            // Menambahkan informasi kasir dan waktu
            document.add(new Paragraph("No. Struk      : " + ID, FontFactory.getFont(FontFactory.HELVETICA, 12)));
            document.add(new Paragraph("Kasir             : " + kasir, FontFactory.getFont(FontFactory.HELVETICA, 12)));
            document.add(new Paragraph("Tanggal        : " + tanggal, FontFactory.getFont(FontFactory.HELVETICA, 12)));
            document.add(new Paragraph("Waktu           : " + waktu, FontFactory.getFont(FontFactory.HELVETICA, 12)));

            // Garis pemisah
            document.add(garis2);

            // Menambahkan detail transaksi
            Paragraph detailHeader = new Paragraph("### LUNAS ###", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            detailHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(detailHeader);

            PdfPTable table = new PdfPTable(4); // 4 kolom
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Header tabel
            table.addCell("Nama Produk");
            table.addCell("Harga");
            table.addCell("Jumlah");
            table.addCell("Sub Total");

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                table.addCell(model.getValueAt(i, 0).toString());
                table.addCell(model.getValueAt(i, 1).toString());
                table.addCell(model.getValueAt(i, 2).toString());
                table.addCell(model.getValueAt(i, 3).toString());
            }
            document.add(table);
            document.add(garis2);

            // Menambahkan subtotal, total, bayar, kembalian
            document.add(new Paragraph("Total            : " + total, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            document.add(new Paragraph("Bayar           : " + formattedBayar, FontFactory.getFont(FontFactory.HELVETICA, 12)));
            document.add(new Paragraph("Kembali        : " + kembalian, FontFactory.getFont(FontFactory.HELVETICA, 12)));

            // Garis pemisah
            document.add(garis2);
            // Footer
            Paragraph footer = new Paragraph("\nTerima Kasih Atas Kunjungannya", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.ITALIC));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            Paragraph poweredBy = new Paragraph("", FontFactory.getFont(FontFactory.HELVETICA, 10));
            poweredBy.setAlignment(Element.ALIGN_CENTER);
            document.add(poweredBy);

            // Menampilkan pesan sukses
            JOptionPane.showMessageDialog(this, "Kuitansi berhasil dicetak: " + fileToSave.getAbsolutePath());
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mencetak kuitansi: " + e.getMessage());
        } finally {
            document.close();
        }
    } else {
        JOptionPane.showMessageDialog(this, "Penyimpanan kuitansi dibatalkan.");
    }
}

// Method untuk mengambil ID terbesar dari tabel transaksi
private int getMaxTransactionId() {
    int maxId = 0;
    String query = "SELECT MAX(id) AS max_id FROM transaksi"; // Ganti 'transaksi' dengan nama tabel yang sesuai
    try (Connection conn = koneksi.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
        if (rs.next()) {
            maxId = rs.getInt("max_id");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return maxId;
}

// Method untuk memformat angka menjadi format rupiah
private String formatRupiah(int amount) {
    NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    return format.format(amount);
}




    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblUsername = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtBayar = new javax.swing.JTextField();
        txtKembalian = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtTanggal = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtKodeProduk = new javax.swing.JTextField();
        txtHarga = new javax.swing.JTextField();
        txtJumlah = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        txtWaktu = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtNamaProduk = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txtDiskon = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtStok = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(54, 48, 98));

        lblUsername.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblUsername.setForeground(new java.awt.Color(248, 243, 243));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(248, 243, 243));
        jLabel10.setText("Total Rp.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lblUsername)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUsername)
                    .addComponent(jLabel10))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jLabel3.setText("Bayar");

        jLabel4.setText("Kembalian");

        txtBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBayarActionPerformed(evt);
            }
        });

        txtKembalian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKembalianActionPerformed(evt);
            }
        });

        jLabel5.setText("Tanggal");

        txtTanggal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTanggalActionPerformed(evt);
            }
        });

        jLabel6.setText("Kode Barang");

        jLabel7.setText("Harga");

        jLabel8.setText("Jumlah");

        txtKodeProduk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKodeProdukActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama Barang", "Harga", "Jumlah", "Total"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("Tambah");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton5.setText("Bayar");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton7.setText("Edit");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Hapus");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Clear");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(54, 48, 98));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(248, 243, 243));
        jLabel11.setText("TOKO RAMAYANI");

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Jl. Kenangan No. 10 Madiun");

        jButton3.setText("Keluar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Waktu");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(350, 350, 350)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 219, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11)
                        .addGap(191, 191, 191)))
                .addComponent(jLabel2)
                .addGap(12, 12, 12)
                .addComponent(txtWaktu, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jButton3)
                .addGap(16, 16, 16))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addContainerGap(9, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtWaktu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(jButton3))
                .addGap(22, 22, 22))
        );

        jLabel13.setText("Nama Barang");

        jLabel14.setText("Rp.");

        btnSearch.setText("Cari");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        jLabel9.setText("Diskon ");

        jLabel15.setText("%");

        jLabel1.setText("Stok");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel5)
                                            .addComponent(txtKodeProduk))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnSearch)
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel13)
                                                    .addComponent(txtNamaProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel14)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel7)
                                                    .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(23, 23, 23)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(txtStok, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addGap(20, 20, 20))
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel1)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE))))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(35, 35, 35)
                                                .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel8))
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel9)
                                            .addComponent(txtDiskon, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jScrollPane1))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton1)
                                    .addComponent(jButton7)
                                    .addComponent(jButton8)
                                    .addComponent(jButton9)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel4))
                                        .addGap(35, 35, 35)
                                        .addComponent(txtBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jButton5))
                                .addGap(1, 1, 1)))
                        .addGap(14, 14, 14))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtKodeProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSearch)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNamaProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDiskon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel8)
                                .addComponent(jLabel1))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtStok)))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel14)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton8)
                        .addGap(18, 18, 18)
                        .addComponent(jButton7)
                        .addGap(18, 18, 18)
                        .addComponent(jButton9))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButton5)
                .addContainerGap(47, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public Product cariProduk(String kodeProduk) {
        Product product = null;

        try (Connection connection = koneksi.getConnection()) {
            String sql = "SELECT p.nama_produk, p.harga_jual FROM produk p WHERE p.id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, kodeProduk);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String namaProduk = resultSet.getString("nama_produk");
                double hargaJual = resultSet.getDouble("harga_jual");
                product = new Product(kodeProduk, namaProduk, hargaJual);
            } else {
                System.out.println("Produk tidak ditemukan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }
    
    private void txtBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBayarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBayarActionPerformed

    private void txtKembalianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKembalianActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKembalianActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        try {
        // Ambil nilai dari txtTotal dan txtBayar
        String totalTagihanStr = txtTotal.getText()
                                         .replace("Rp. ", "")
                                         .replace("Rp", "")
                                         .replace(".", "")
                                         .replace(",", "");

        // Hapus dua angka terakhir dari totalTagihanStr
    if (totalTagihanStr.length() > 2) {
        totalTagihanStr = totalTagihanStr.substring(0, totalTagihanStr.length() - 2);
    } else {
        totalTagihanStr = "0";
    }

    String uangDibayarStr = txtBayar.getText().replace(".", "").replace(",", "");

    // Validasi input kosong
    if (uangDibayarStr.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Input tidak boleh kosong. Silakan masukkan angka.");
        return;
    }

    // Validasi format angka
    if (!uangDibayarStr.matches("\\d+")) {
        JOptionPane.showMessageDialog(null, "Input hanya boleh angka.");
        return;
    }

    // Konversi string ke double
    double totalTagihan = Double.parseDouble(totalTagihanStr);
    double uangDibayar = Double.parseDouble(uangDibayarStr);

    // Hitung kembalian
    double kembalian = uangDibayar - totalTagihan;

    // Cek apakah uang yang dibayarkan cukup
    if (kembalian >= 0) {
        txtKembalian.setText(formatRupiah(kembalian)); // Format kembalian
        JOptionPane.showMessageDialog(null, "Kembalian: " + formatRupiah(kembalian));

        // Menyimpan transaksi ke database
        String kodeProduk = txtKodeProduk.getText().trim();
        String tanggal = txtTanggal.getText().trim();
        String waktu = txtWaktu.getText().trim();
        String kasir = username; // Ambil username yang sudah ada

        // Hitung total jumlah produk dari jTable1
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        int jumlah = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            String jumlahStr = model.getValueAt(i, 2).toString(); // Ambil nilai sebagai String
            try {
                int jumlahValue = Integer.parseInt(jumlahStr); // Parsing String menjadi Integer
                jumlah += jumlahValue; // Tambahkan ke total jumlah
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Jumlah tidak valid di baris " + (i + 1));
                return; // Keluar jika ada angka yang tidak valid
            }
        }

        // Simpan transaksi ke database
        try (Connection connection = koneksi.getConnection()) {
            String sql = "INSERT INTO transaksi (kode_produk, tanggal_transaksi, waktu, total, kasir, jumlah) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, kodeProduk); // Simpan kode produk
            statement.setString(2, tanggal);   // Simpan tanggal transaksi
            statement.setString(3, waktu);     // Simpan waktu transaksi
            statement.setDouble(4, totalTagihan); // Simpan total tagihan
            statement.setString(5, kasir);     // Simpan nama kasir
            statement.setInt(6, jumlah);       // Simpan total jumlah produk

            statement.executeUpdate(); // Eksekusi pernyataan SQL

            // Mengurangi stok produk di database
            String updateStokSql = "UPDATE produk SET stok = stok - ? WHERE kode_produk = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateStokSql)) {
                updateStatement.setInt(1, jumlah); // Jumlah yang dibeli
                updateStatement.setString(2, kodeProduk); // Kode produk
                updateStatement.executeUpdate(); // Eksekusi pernyataan SQL untuk mengupdate stok
            }

            // Konfirmasi untuk mencetak kuitansi
            int printOption = JOptionPane.showConfirmDialog(this, "Apakah Anda ingin mencetak kuitansi?", "Konfirmasi Cetak", JOptionPane.YES_NO_OPTION);
            if (printOption == JOptionPane.YES_OPTION) {
                // Panggil metode untuk mencetak kuitansi
                cetakTransaksi(); // Ganti dengan pemanggilan metode cetakTransaksi
            }

            // Reset semua field setelah transaksi
            clearFields(); // Panggil metode untuk mengosongkan field

            // Dapatkan tanggal dan waktu saat ini
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Format tanggal
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss"); // Format waktu
            
            // Set tanggal dan waktu ke text field
            txtTanggal.setText(dateFormat.format(now)); // Set tanggal ke txtTanggal
            txtWaktu.setText(timeFormat.format(now)); // Set waktu ke txtWaktu
            
            // Kosongkan txtTotal, txtBayar, txtKembalian
            txtTotal.setText(""); // Kosongkan total
            txtBayar.setText(""); // Kosongkan bayar
            txtKembalian.setText(""); // Kosongkan kembalian
            
            // Kosongkan jTable1
            model.setRowCount(0); // Menghapus semua baris dari jTable1
            
            // Reset total keseluruhan
            totalKeseluruhan = 0; // Reset total keseluruhan

        } catch (SQLException e) {
            e.printStackTrace(); // Print stack trace for debugging
            JOptionPane.showMessageDialog(null, "Gagal menyimpan transaksi: " + e.getMessage());
        }
    } else {
        JOptionPane.showMessageDialog(null, "Uang Anda kurang: " + formatRupiah(-kembalian));
    }
} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(null, "Input tidak valid. Silakan masukkan angka.");
}
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        home mainMenu = home.getInstance(username, role); // Gunakan instance tunggal
        mainMenu.setMenuEnabled(true, role); // Aktifkan menu sesuai role
        mainMenu.setVisible(true);
        dispose(); 
    }//GEN-LAST:event_jButton3ActionPerformed

    private void txtTanggalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTanggalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTanggalActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
         String tanggal = txtTanggal.getText().trim();
    String waktu = txtWaktu.getText().trim();
    String kodeProduk = txtKodeProduk.getText().trim();
    String kasir = username; // Mengambil username yang sudah ada
    int jumlah;

    // Validasi input jumlah
    try {
        jumlah = Integer.parseInt(txtJumlah.getText().trim());
        if (jumlah <= 0) {
            JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0.");
            return;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka.");
        return;
    }

    // Mencari produk berdasarkan kode produk
    Product product = null;
    int stok = 0; // Variabel untuk menyimpan stok produk
    try (Connection connection = koneksi.getConnection()) {
        String sql = "SELECT p.kode_produk, p.nama_produk, p.harga_jual, p.stok " +
                     "FROM produk p " +
                     "WHERE p.kode_produk = ?"; // Pencarian berdasarkan kode produk
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, kodeProduk); // Set kode produk untuk pencarian
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            // Jika produk ditemukan, ambil data
            String namaProduk = resultSet.getString("nama_produk");
            double hargaJual = resultSet.getDouble("harga_jual");
            stok = resultSet.getInt("stok"); // Ambil stok produk
            product = new Product(kodeProduk, namaProduk, hargaJual);

            // Tampilkan informasi produk di field
            txtNamaProduk.setText(namaProduk);
            txtHarga.setText(formatRupiah(hargaJual)); // Format harga sebelum ditampilkan
        } else {
            JOptionPane.showMessageDialog(this, "Produk tidak ditemukan.");
            return;
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error searching product: " + e.getMessage());
        return;
    }

    // Validasi jumlah terhadap stok
    if (jumlah > stok) {
        JOptionPane.showMessageDialog(this, "Jumlah yang dimasukkan melebihi stok yang tersedia (" + stok + ").");
        return;
    }

    // Menghitung total untuk transaksi ini
    double total = product.getHargaJual() * jumlah;

    // Cek apakah ada diskon yang dimasukkan
    String diskonInput = txtDiskon.getText().trim();
    if (!diskonInput.isEmpty()) {
        double diskonPersentase;
        try {
            diskonPersentase = Double.parseDouble(diskonInput);
            if (diskonPersentase < 0 || diskonPersentase > 100) {
                JOptionPane.showMessageDialog(this, "Diskon harus antara 0 dan 100.");
                return;
            }
            // Menghitung diskon
            double diskon = (diskonPersentase / 100) * total; // Diskon berdasarkan total transaksi ini
            total -= diskon; // Mengurangi total dengan diskon
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Diskon harus berupa angka.");
            return;
        }
    }

    // Menambahkan transaksi ke tabel dengan urutan yang benar
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    model.addRow(new Object[]{
        product.getNamaProduk(), // Nama produk
        formatRupiah(product.getHargaJual()), // Harga produk
        jumlah, // Jumlah produk
        formatRupiah(total) // Total setelah diskon
    });

    // Menambahkan total transaksi ke total keseluruhan
    totalKeseluruhan += total;

    // Menampilkan total keseluruhan di txtTotal
    txtTotal.setText(formatRupiah(totalKeseluruhan));
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
         String kodeProduk = txtKodeProduk.getText().trim();
    try (Connection connection = koneksi.getConnection()) {
        // Memperbarui query untuk mengambil stok
        String sql = "SELECT p.kode_produk, p.nama_produk, p.harga_jual, p.stok FROM produk p WHERE p.kode_produk = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, kodeProduk);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String namaProduk = resultSet.getString("nama_produk");
            double hargaJual = resultSet.getDouble("harga_jual");
            int stok = resultSet.getInt("stok"); // Mengambil nilai stok

            // Menampilkan data di field input
            txtNamaProduk.setText(namaProduk);
            txtHarga.setText(formatRupiah(hargaJual)); // Format harga menjadi rupiah
            txtStok.setText(String.valueOf(stok)); // Menampilkan stok di txtStok
            txtJumlah.requestFocus(); // Fokus pada field jumlah
        } else {
            JOptionPane.showMessageDialog(this, "Produk tidak ditemukan.");
            txtNamaProduk.setText("");
            txtHarga.setText("");
            txtStok.setText(""); // Kosongkan txtStok jika produk tidak ditemukan
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error searching product: " + e.getMessage());
    }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
    int selectedRow = jTable1.getSelectedRow();
    if (selectedRow != -1) {
        // Mengonfirmasi penghapusan
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus barang ini dari daftar?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Menghapus baris yang dipilih dari model tabel
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            double totalDihapus = 0;

            // Ambil total dari baris yang akan dihapus
            String totalString = model.getValueAt(selectedRow, 3).toString().replace("Rp", "").replace(".", "").replace(",", ".").trim();
            totalDihapus = Double.parseDouble(totalString);

            // Menghapus baris
            model.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Barang berhasil dihapus dari daftar!");

            // Menghitung total keseluruhan setelah penghapusan
            double totalKeseluruhan = 0; // Reset total keseluruhan
            for (int i = 0; i < model.getRowCount(); i++) {
                String totalRowString = model.getValueAt(i, 3).toString().replace("Rp", "").replace(".", "").replace(",", ".").trim();
                totalKeseluruhan += Double.parseDouble(totalRowString);
            }

            // Menampilkan total keseluruhan di txtTotal
            txtTotal.setText(formatRupiah(totalKeseluruhan));
        }
    } else {
        JOptionPane.showMessageDialog(this, "Silakan pilih barang yang ingin dihapus dari daftar.");
    }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        int selectedRow = jTable1.getSelectedRow();
    if (selectedRow != -1) {
        try {
            // Mengisi field input dengan data dari baris yang dipilih
            txtNamaProduk.setText(model.getValueAt(selectedRow, 0).toString()); // Nama Produk
            txtHarga.setText(model.getValueAt(selectedRow, 1).toString()); // Harga
            txtJumlah.setText(model.getValueAt(selectedRow, 2).toString()); // Jumlah
            
            // Mengambil total dari tabel
            String totalString = model.getValueAt(selectedRow, 3).toString(); // Total
            String hargaAwalString = model.getValueAt(selectedRow, 1).toString(); // Harga Awal
            String jumlahString = txtJumlah.getText(); // Jumlah
            
            // Validasi sebelum konversi
            if (totalString.isEmpty() || hargaAwalString.isEmpty() || jumlahString.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Total, Harga, atau Jumlah tidak boleh kosong.");
                return;
            }

            // Menghapus "Rp" dan mengganti format
            totalString = totalString.replace("Rp", "").replace(".", "").replace(",", ".").trim();
            hargaAwalString = hargaAwalString.replace("Rp", "").replace(".", "").replace(",", ".").trim();
            jumlahString = jumlahString.trim(); // Pastikan jumlah tidak kosong
            
            // Mengonversi string ke double
            double total = Double.parseDouble(totalString); // Total
            double hargaAwal = Double.parseDouble(hargaAwalString); // Harga Awal
            int jumlah = Integer.parseInt(jumlahString); // Jumlah
            
            // Menghitung total harga awal
            double totalHargaAwal = hargaAwal * jumlah; // Total harga awal
            
            // Menghitung diskon
            double diskon = totalHargaAwal - total; // Diskon
            
            // Menghitung persentase diskon sebagai integer
            int persentaseDiskon = (int) ((diskon / totalHargaAwal) * 100); // Persentase diskon
            
            // Jika diskon negatif, set menjadi 0
            if (persentaseDiskon < 0) {
                persentaseDiskon = 0;
            }
            
            // Set diskon dan persentase diskon ke text field
            txtDiskon.setText(String.valueOf((int) diskon)); // Set diskon ke text field sebagai integer
            txtDiskon.setText(String.valueOf(persentaseDiskon)); // Set persentase diskon ke text field sebagai integer
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan dalam format angka: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        int selectedRow = jTable1.getSelectedRow();
    if (selectedRow != -1) {
        // Mengambil data dari field input
        String namaProduk = txtNamaProduk.getText();
        String harga = txtHarga.getText();
        String jumlah = txtJumlah.getText();
        String diskon = txtDiskon.getText(); // Misalkan diskon diambil dari txtDiskon

        // Validasi input (misalnya, pastikan tidak kosong)
        if (namaProduk.isEmpty() || harga.isEmpty() || jumlah.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi.");
            return;
        }

        // Mengonfirmasi pengeditan
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin mengedit barang ini?", "Konfirmasi Edit", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Mengupdate baris yang dipilih di model tabel
            model.setValueAt(namaProduk, selectedRow, 0); // Nama Produk
            model.setValueAt(harga, selectedRow, 1); // Harga
            model.setValueAt(jumlah, selectedRow, 2); // Jumlah

            // Menghitung total baru berdasarkan harga dan jumlah
            double hargaValue = Double.parseDouble(harga.replace("Rp", "").replace(".", "").replace(",", ".").trim());
            int jumlahValue = Integer.parseInt(jumlah);
            double totalValue = hargaValue * jumlahValue;

            // Cek apakah ada diskon yang dimasukkan
            if (!diskon.isEmpty()) {
                double diskonPersentase = Double.parseDouble(diskon);
                if (diskonPersentase < 0 || diskonPersentase > 100) {
                    JOptionPane.showMessageDialog(this, "Diskon harus antara 0 dan 100.");
                    return;
                }
                double diskonValue = (diskonPersentase / 100) * totalValue; // Hitung diskon
                totalValue -= diskonValue; // Kurangi total dengan diskon
            }

            // Set total baru ke tabel
            model.setValueAt(formatRupiah(totalValue), selectedRow, 3); // Total

            // Menghitung total keseluruhan setelah pengeditan
            double totalKeseluruhan = 0; // Reset total keseluruhan
            for (int i = 0; i < model.getRowCount(); i++) {
                String totalRowString = model.getValueAt(i, 3).toString().replace("Rp", "").replace(".", "").replace(",", ".").trim();
                totalKeseluruhan += Double.parseDouble(totalRowString);
            }

            // Menampilkan total keseluruhan di txtTotal
            txtTotal.setText(formatRupiah(totalKeseluruhan));

            JOptionPane.showMessageDialog(this, "Barang berhasil diedit!");
        }
    } else {
        JOptionPane.showMessageDialog(this, "Silakan pilih barang yang ingin diedit.");
    }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
         clearFields();
    
    // Refresh data (jika ada data yang perlu dimuat)
    loadData();
    
    // Dapatkan tanggal dan waktu saat ini
    Date now = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd"); // Format tanggal
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss"); // Format waktu
    
    // Set tanggal dan waktu ke text field
    txtTanggal.setText(dateFormat.format(now)); // Set tanggal ke txtTanggal
    txtWaktu.setText(timeFormat.format(now)); // Set waktu ke txtWaktu
    
    // Kosongkan txtTotal, txtBayar, txtKembalian
    txtTotal.setText(""); // Kosongkan total
    txtBayar.setText(""); // Kosongkan bayar
    txtKembalian.setText(""); // Kosongkan kembalian
    
    // Kosongkan jTable1
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    model.setRowCount(0); // Menghapus semua baris dari jTable1
    
    // Reset total keseluruhan
    totalKeseluruhan = 0; // Reset total keseluruhan
    }//GEN-LAST:event_jButton9ActionPerformed

    private void txtKodeProdukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKodeProdukActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKodeProdukActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Transaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Transaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Transaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Transaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        String username = UserSession.getInstance().getUsername(); // Ambil username dari UserSession
        String role = UserSession.getInstance().getRole(); // Ambil role dari UserSession

        // Cek apakah username dan role valid
        if (username == null || role == null) {
            JOptionPane.showMessageDialog(null, "User  session is not valid.");
            return; // Keluar jika session tidak valid
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Transaksi(username, role).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JTextField txtBayar;
    private javax.swing.JTextField txtDiskon;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtJumlah;
    private javax.swing.JTextField txtKembalian;
    private javax.swing.JTextField txtKodeProduk;
    private javax.swing.JTextField txtNamaProduk;
    private javax.swing.JLabel txtStok;
    private javax.swing.JTextField txtTanggal;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtWaktu;
    // End of variables declaration//GEN-END:variables
}
