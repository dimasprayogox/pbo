/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package uts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import koneksi.koneksi;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.JOptionPane;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import java.io.File;
import java.text.NumberFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author user
 */
public class LaporanPenjualan extends javax.swing.JFrame {

    /**
     * Creates new form LaporanPenjualan
     */
    private DefaultTableModel model;
    private String username; // Variabel untuk menyimpan username
    private String role; // Variabel untuk menyimpan role
    
    public LaporanPenjualan(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents(); 
        
        // Periksa apakah username dan role null atau kosong
        if (username == null || username.isEmpty() || role == null || role.isEmpty()) {
            JOptionPane.showMessageDialog(this, "User  session is not valid.");
            dispose(); // Tutup form jika session tidak valid
            return;
        }
      
        model = new DefaultTableModel();
        tblTransaksi.setModel(model);
        
        // Menambahkan kolom ke model tabel
        model.addColumn("ID");
        model.addColumn("Kode Barang");
        model.addColumn("Nama Barang");
        model.addColumn("Jumlah");
        model.addColumn("Waktu Transaksi");
        model.addColumn("Dibayar");
        model.addColumn("Kasir");
        
        loadData();
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
        String sql = "SELECT t.id, t.kode_produk, p.nama_produk, t.jumlah, t.tanggal_transaksi, t.total, t.kasir " +
                     "FROM transaksi t JOIN produk p ON t.kode_produk = p.kode_produk"; // Mengambil data produk dan kategori
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            model.addRow(new Object[]{
                resultSet.getInt("id"), // ID produk
                resultSet.getString("kode_produk"),
                resultSet.getString("nama_produk"),
                resultSet.getInt("jumlah"), // Jumlah produk
                resultSet.getDate("tanggal_transaksi"), // Mengambil tanggal sebagai Date
                formatRupiah(resultSet.getDouble("total")), // Format total sebagai Rupiah
                resultSet.getString("kasir") // Kasir sebagai String
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
    }
}
    
private void clearFields() {
    txtCariTanggal.setText("");
    txtCariBulan.setText("");
    txtCariTahun.setText("");
    txtCariPegawai.setText("");
}

private double getHargaBeli(String kodeProduk) {
    double hargaBeli = 0.0;
    try (Connection connection = koneksi.getConnection()) {
        String sql = "SELECT harga_beli FROM produk WHERE kode_produk = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, kodeProduk);
        
        System.out.println("Menjalankan kueri: " + statement.toString()); // Debugging output
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            hargaBeli = resultSet.getDouble("harga_beli");
        } else {
            System.out.println("Tidak ada harga beli ditemukan untuk kode produk: " + kodeProduk); // Debugging output
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error fetching harga beli: " + e.getMessage());
    }
    return hargaBeli;
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtCariTanggal = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        txtCariBulan = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        txtCariTahun = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        txtCariPegawai = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTransaksi = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(54, 48, 98));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("TOKO RAMAYANI");

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Jl. Kenangan No.10 Madiun");

        jButton7.setText("Keluar");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(405, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(237, 237, 237))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(274, 274, 274)))
                .addComponent(jButton7)
                .addGap(42, 42, 42))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(15, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton7)
                .addGap(24, 24, 24))
        );

        jPanel2.setBackground(new java.awt.Color(54, 48, 98));

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Cari Tanggal");

        txtCariTanggal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCariTanggalActionPerformed(evt);
            }
        });

        jButton1.setText("Cari");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Cari");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Cari Bulan");

        jButton3.setText("Cari");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Cari Tahun");

        jButton4.setText("Cari");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Cari Pegawai");

        jButton6.setText("Clear");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton8.setText("Hapus");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtCariBulan, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCariTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(73, 73, 73)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtCariTahun, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCariPegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton8)
                .addGap(44, 44, 44)
                .addComponent(jButton6)
                .addGap(44, 44, 44))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtCariTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1)
                            .addComponent(jLabel5)
                            .addComponent(txtCariTahun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton6)
                            .addComponent(jButton8))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCariBulan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2)
                    .addComponent(jLabel6)
                    .addComponent(txtCariPegawai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(54, 48, 98));

        tblTransaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Waktu Transaksi", "Nama Barang", "Jumlah", "Dibayar", "Kasir"
            }
        ));
        tblTransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTransaksiMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblTransaksi);

        jButton5.setText("Cetak Laporan");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addGap(17, 17, 17))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton5)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        String searchQuery = txtCariTanggal.getText().trim();
    model.setRowCount(0); // Clear existing data

    try (Connection connection = koneksi.getConnection()) {
        // Menambahkan spasi sebelum WHERE
        String sql = "SELECT t.id, t.kode_produk, p.nama_produk, t.jumlah, t.tanggal_transaksi, t.total, t.kasir " +
                     "FROM transaksi t JOIN produk p ON t.kode_produk = p.kode_produk " + // Spasi ditambahkan di sini
                     "WHERE t.tanggal_transaksi LIKE ?"; // Pencarian berdasarkan tanggal transaksi
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, searchQuery); // Menggunakan input langsung untuk pencarian tanggal
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            model.addRow(new Object[]{
                resultSet.getInt("id"), // ID produk
                resultSet.getString("kode_produk"),
                resultSet.getString("nama_produk"),
                resultSet.getInt("jumlah"), // Jumlah produk
                resultSet.getDate("tanggal_transaksi"), // Mengambil tanggal sebagai Date
                resultSet.getDouble("total"), // Total sebagai double
                resultSet.getString("kasir") // Kasir sebagai String
            });
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data yang ditemukan.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error searching : " + e.getMessage());
    }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtCariTanggalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariTanggalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCariTanggalActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
       String monthInput = txtCariBulan.getText().trim(); // JTextField untuk bulan (format: YYYY-MM)
    model.setRowCount(0); // Clear existing data

    // Validasi format input bulan
    if (!monthInput.matches("\\d{4}-(0?[1-9]|1[0-2])")) {
    JOptionPane.showMessageDialog(this, "Format bulan harus YYYY-MM (contoh: 2024-1).");
    return;
}

    String[] parts = monthInput.split("-");
    int year = Integer.parseInt(parts[0]);
    int month = Integer.parseInt(parts[1]);

    try (Connection connection = koneksi.getConnection()) {
        String sql = "SELECT t.id, t.kode_produk, p.nama_produk, t.jumlah, t.tanggal_transaksi, t.total, t.kasir " +
                     "FROM transaksi t JOIN produk p ON t.kode_produk = p.kode_produk " +
                     "WHERE YEAR(t.tanggal_transaksi) = ? AND MONTH(t.tanggal_transaksi) = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, year); // Set tahun
        statement.setInt(2, month); // Set bulan

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            model.addRow(new Object[]{
                resultSet.getInt("id"),
                resultSet.getString("kode_produk"),
                resultSet.getString("nama_produk"),
                resultSet.getInt("jumlah"),
                resultSet.getDate("tanggal_transaksi"),
                resultSet.getDouble("total"),
                resultSet.getString("kasir")
            });
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data yang ditemukan untuk bulan " + monthInput + ".");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error searching by month: " + e.getMessage());
    }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        String yearInput = txtCariTahun.getText().trim(); // JTextField untuk tahun
    model.setRowCount(0); // Clear existing data

    // Validasi input tahun
    if (!yearInput.matches("\\d{4}")) {
        JOptionPane.showMessageDialog(this, "Tahun harus berupa 4 digit (contoh: 2024).");
        return;
    }

    int year = Integer.parseInt(yearInput);

    try (Connection connection = koneksi.getConnection()) {
        String sql = "SELECT t.id, t.kode_produk, p.nama_produk, t.jumlah, t.tanggal_transaksi, t.total, t.kasir " +
                     "FROM transaksi t JOIN produk p ON t.kode_produk = p.kode_produk " +
                     "WHERE YEAR(t.tanggal_transaksi) = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, year); // Set tahun

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            model.addRow(new Object[]{
                resultSet.getInt("id"),
                resultSet.getString("kode_produk"),
                resultSet.getString("nama_produk"),
                resultSet.getInt("jumlah"),
                resultSet.getDate("tanggal_transaksi"),
                resultSet.getDouble("total"),
                resultSet.getString("kasir")
            });
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data yang ditemukan untuk tahun " + year + ".");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error searching by year: " + e.getMessage());
    }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        home mainMenu = home.getInstance(username, role); // Gunakan instance tunggal
        mainMenu.setMenuEnabled(true, role); // Aktifkan menu sesuai role
        mainMenu.setVisible(true);
        dispose(); 
    }//GEN-LAST:event_jButton7ActionPerformed

    private void tblTransaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTransaksiMouseClicked
        // TODO add your handling code here:
        int selectedRow = tblTransaksi.getSelectedRow();
    }//GEN-LAST:event_tblTransaksiMouseClicked

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
         int selectedRow = tblTransaksi.getSelectedRow();
    if (selectedRow != -1) {
        // Mengambil ID produk dari baris yang dipilih
        int id = (int) model.getValueAt(selectedRow, 0); // ID produk

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection connection = koneksi.getConnection()) {
                // Hapus produk
                String sqlDelete = "DELETE FROM transaksi WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(sqlDelete);
                statement.setInt(1, id);
                statement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Produk berhasil dihapus!");
                loadData(); // Refresh the table
                clearFields(); // Clear input fields
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting product: " + e.getMessage());
            }
        }
    } else {
        JOptionPane.showMessageDialog(this, "Silakan pilih produk yang ingin dihapus.");
    }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        loadData();
        clearFields();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
         String kasirInput = txtCariPegawai.getText().trim(); // JTextField untuk nama pegawai (kasir)
    model.setRowCount(0); // Clear existing data

    // Validasi input kasir
    if (kasirInput.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nama pegawai tidak boleh kosong.");
        return;
    }

    try (Connection connection = koneksi.getConnection()) {
        String sql = "SELECT t.id, t.kode_produk, p.nama_produk, t.jumlah, t.tanggal_transaksi, t.total, t.kasir " +
                     "FROM transaksi t JOIN produk p ON t.kode_produk = p.kode_produk " +
                     "WHERE t.kasir LIKE ?"; // Menggunakan LIKE untuk pencarian

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, "%" + kasirInput + "%"); // Menambahkan wildcard untuk pencarian

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            model.addRow(new Object[]{
                resultSet.getInt("id"),
                resultSet.getString("kode_produk"),
                resultSet.getString("nama_produk"),
                resultSet.getInt("jumlah"),
                resultSet.getDate("tanggal_transaksi"),
                resultSet.getDouble("total"),
                resultSet.getString("kasir")
            });
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data yang ditemukan untuk pegawai: " + kasirInput + ".");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error searching by kasir: " + e.getMessage());
    }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
       if (model.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Tidak ada data untuk dicetak.");
        return;
    }

    // Menggunakan JFileChooser untuk memilih lokasi dan nama file
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Simpan Laporan Transaksi");
    fileChooser.setSelectedFile(new File("Laporan_Transaksi.pdf")); // Nama file default

    // Menampilkan dialog untuk memilih file
    int userSelection = fileChooser.showSaveDialog(this);
    if (userSelection != JFileChooser.APPROVE_OPTION) {
        return; // Jika pengguna membatalkan, keluar dari metode
    }

    File fileToSave = fileChooser.getSelectedFile();
    
    // Membuat dokumen PDF
    Document document = new Document();
    try {
        // Menentukan lokasi penyimpanan PDF
        PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
        document.open();

        Paragraph namaPerusahaan = new Paragraph("TOKO RAMAYANI", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        Paragraph storeAddress = new Paragraph("Jl. Kenangan No.1 \n Madiun\n\n", FontFactory.getFont(FontFactory.HELVETICA, 12));
        Paragraph garis2 = new Paragraph("==========================================================================");
        namaPerusahaan.setAlignment(Element.ALIGN_CENTER);
        storeAddress.setAlignment(Element.ALIGN_CENTER);
        garis2.setAlignment(Element.ALIGN_CENTER);
        document.add(namaPerusahaan);
        document.add(storeAddress);
        document.add(garis2);
        
        // Menambahkan tanggal laporan
        document.add(new Paragraph("Tanggal Laporan: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        document.add(new Paragraph(" "));
        
        // Menambahkan judul dengan alignment tengah
        Paragraph title = new Paragraph("Laporan Transaksi", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        
        // Menambahkan garis pemanis
        document.add(garis2);
        document.add(new Paragraph(" "));
        
        // Membuat tabel untuk laporan
        PdfPTable table = new PdfPTable(7); // 7 kolom sesuai dengan data
        table.setWidthPercentage(100);
        
        // Menambahkan header tabel
        table.addCell("ID");
        table.addCell("Kode Produk");
        table.addCell("Nama Produk");
        table.addCell("Jumlah");
        table.addCell("Tanggal Transaksi");
        table.addCell("Total");
        table.addCell("Kasir");

        double subtotal = 0.0; // Inisialisasi subtotal
        double totalKeuntungan = 0.0; // Inisialisasi total keuntungan

        // Mengisi tabel dengan data dari model
        for (int i = 0; i < model.getRowCount(); i++) {
            String kodeProduk = model.getValueAt(i, 1).toString(); // Kode Produk
            int jumlah = Integer.parseInt(model.getValueAt(i, 3).toString()); // Jumlah
            
            // Ambil total dan bersihkan formatnya
            String totalString = model.getValueAt(i, 5).toString(); // Total dalam format string
            double total = Double.parseDouble(totalString.replace("Rp", "").replace(".", "").replace(",", ".").trim()); // Bersihkan dan konversi

            // Ambil harga beli dari tabel produk berdasarkan kode produk
            double hargaBeli = getHargaBeli(kodeProduk); // Fungsi untuk mendapatkan harga beli

            // Hitung subtotal dan keuntungan
            subtotal += total;
            double hargaPerUnit = total / jumlah; // Harga per unit
            totalKeuntungan += (hargaPerUnit - hargaBeli) * jumlah; // Keuntungan

            // Menambahkan data ke tabel
            table.addCell(model.getValueAt(i, 0).toString()); // ID
            table.addCell(kodeProduk); // Kode Produk
            table.addCell(model.getValueAt(i, 2).toString()); // Nama Produk
            table.addCell(String.valueOf(jumlah)); // Jumlah
            table.addCell(model.getValueAt(i, 4).toString()); // Tanggal Transaksi
            table.addCell(formatRupiah(total)); // Format total sebagai Rupiah
            table.addCell(model.getValueAt(i, 6).toString()); // Kasir
        }

        // Menambahkan tabel ke dokumen
        document.add(table);
        document.add(garis2);
        
        // Menambahkan subtotal dan keuntungan ke dokumen
        document.add(new Paragraph(" "));
        Paragraph stotal = new Paragraph("Pendapatan : " + formatRupiah(subtotal)); // Format subtotal sebagai Rupiah
        Paragraph keuntungan = new Paragraph("Total Keuntungan: " + formatRupiah(totalKeuntungan)); // Format total keuntungan sebagai Rupiah
        stotal.setAlignment(Element.ALIGN_RIGHT);
        keuntungan.setAlignment(Element.ALIGN_RIGHT);
        document.add(stotal);
        document.add(keuntungan);
        document.add(new Paragraph(" "));
        document.add(garis2);
        
        // Menambahkan footer atau catatan
        document.add(new Paragraph(" "));
        
        Paragraph footer = new Paragraph("Catatan: Laporan ini dihasilkan secara otomatis.");
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
        
        // Menambahkan garis pemanis di akhir
        document.add(garis2);
        
        document.close();

        // Menampilkan pesan sukses
        JOptionPane.showMessageDialog(this, "Laporan berhasil dibuat: " + fileToSave.getAbsolutePath());
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error creating PDF: " + e.getMessage());
    } finally {
        if (document.isOpen()) {
            document.close();
        }
    }
    }//GEN-LAST:event_jButton5ActionPerformed

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
            java.util.logging.Logger.getLogger(LaporanPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LaporanPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LaporanPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LaporanPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        String username = UserSession.getInstance().getUsername(); // Ambil username dari UserSession
        String role = UserSession.getInstance().getRole(); // Ambil role dari UserSession

        // Cek apakah username dan role valid
        if (username == null || role == null) {
            JOptionPane.showMessageDialog(null, "User  session is not valid.");
            return; // Keluar jika session tidak valid
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LaporanPenjualan(username, role).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblTransaksi;
    private javax.swing.JTextField txtCariBulan;
    private javax.swing.JTextField txtCariPegawai;
    private javax.swing.JTextField txtCariTahun;
    private javax.swing.JTextField txtCariTanggal;
    // End of variables declaration//GEN-END:variables
}
