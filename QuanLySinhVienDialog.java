/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhom5.dialog;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import nhom5.model.GiaoVien;
import nhom5.model.SinhVien;
import nhom5.sql.GiaoVienService;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import nhom5.gui.LoginFrm;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Giang Phan
 */
public class QuanLySinhVienDialog extends javax.swing.JFrame {
    DefaultTableModel dtm=null;
    private GiaoVien giaoVien;
    private String maMh;
    GiaoVienService giaoVienService=new GiaoVienService();
    /**
     * Creates new form QuanLySinhVienDialog
     */
    public QuanLySinhVienDialog(){
    }
    public QuanLySinhVienDialog(GiaoVien gv,String maMH) {
        this.maMh=maMH;
        this.giaoVien=gv;
        initComponents();
        addControls();
        loadDataTable();
        LoginFrm.FrameDragListener frameDragListener = new LoginFrm.FrameDragListener(this);
        this.addMouseListener(frameDragListener);
        this.addMouseMotionListener(frameDragListener);
    }
    
    private static void Export(DefaultTableModel tableModel) {

    JFileChooser save = new JFileChooser();
    save.setDialogTitle("Save as...");
    save.setFileFilter(new FileNameExtensionFilter( "xlsx", "xlsm"));
    int choose = save.showSaveDialog(null);

    if(choose == JFileChooser.APPROVE_OPTION) {
        XSSFWorkbook export = new XSSFWorkbook();
        XSSFSheet sheet1 = export.createSheet("new file");
        try{
            //TableModel tableModel = tble.getModel();
            //DefaultTableModel xxx=(DefaultTableModel) tble.getModel();
            //xxx.getC
            for(int i=0; i<tableModel.getRowCount(); i++) {
                XSSFRow newRow = sheet1.createRow(i);
                for(int j=0; j<tableModel.getColumnCount(); j++) {
                    XSSFCell newCell = newRow.createCell((short) j);
                    if(i==0){
                        XSSFCellStyle style = export.createCellStyle();
                        style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
                        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        //style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
                        style.setBorderBottom(BorderStyle.THIN);
                        style.setBorderTop(BorderStyle.THIN);
                        style.setBorderLeft(BorderStyle.THIN);
                        style.setBorderRight(BorderStyle.THIN);
                        newCell.setCellStyle(style);
                        newCell.setCellValue(tableModel.getColumnName(j));
                    } else {
                        XSSFCellStyle style = export.createCellStyle();
                        style.setBorderBottom(BorderStyle.THIN);
                        style.setBorderTop(BorderStyle.THIN);
                        style.setBorderLeft(BorderStyle.THIN);
                        style.setBorderRight(BorderStyle.THIN);
                        newCell.setCellStyle(style);
                    newCell.setCellValue(tableModel.getValueAt(i, j).toString());
                    }
                }
            }

            FileOutputStream otp = new FileOutputStream(save.getSelectedFile()+".xlsx");
            BufferedOutputStream bos = new BufferedOutputStream(otp);
            export.write(bos);
            bos.close();
            otp.close();

            JOptionPane.showMessageDialog(null, "Xuất file thành công !");
        }catch(Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
}
    private void loadDataTable(){
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        ArrayList<SinhVien>dsSV=giaoVienService.dsChiTietSVTheoMa(giaoVien.getMaGV(), maMh);
        for(int i=tblQuanLySV.getRowCount()-1; i>=0; i--){
            dtm.removeRow(i);
        }
        for(SinhVien sv:dsSV){
            Vector<Object>vec=new Vector<>();
            vec.add(sv.getMaSV());
            vec.add(sv.getTenSV());
            vec.add(sv.getGioiTinh());
            vec.add(sv.getQueQuan());
            vec.add(sdf.format(sv.getNgaySinh()));
            dtm.addRow(vec);
        }
    }
    public static class FrameDragListener extends MouseAdapter {

        private final JFrame frame;
        private Point mouseDownCompCoords = null;

        public FrameDragListener(JFrame frame) {
            this.frame = frame;
        }

        public void mouseReleased(MouseEvent e) {
            mouseDownCompCoords = null;
        }

        public void mousePressed(MouseEvent e) {
            mouseDownCompCoords = e.getPoint();
        }

        public void mouseDragged(MouseEvent e) {
            Point currCoords = e.getLocationOnScreen();
            frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
        }
    }
    private void addControls(){
        dtm=(DefaultTableModel) tblQuanLySV.getModel();
        dtm.addColumn("Mã sv");
        dtm.addColumn("Tên sv");
        dtm.addColumn("Giới tính");
        dtm.addColumn("Quê quán");
        dtm.addColumn("Ngày sinh");
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
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblQuanLySV = new javax.swing.JTable();
        btnDong = new javax.swing.JButton();
        btnXuatFile = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(44, 44, 84));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Quản lý sinh viên");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("X");
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("-");
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(26, 26, 26)
                .addComponent(jLabel2)
                .addGap(22, 22, 22))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(52, 172, 224));

        tblQuanLySV.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblQuanLySV.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblQuanLySV.setRowHeight(30);
        jScrollPane1.setViewportView(tblQuanLySV);

        btnDong.setBackground(new java.awt.Color(255, 118, 117));
        btnDong.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnDong.setForeground(new java.awt.Color(255, 255, 204));
        btnDong.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nhom5/images/back32.png"))); // NOI18N
        btnDong.setText("Đóng");
        btnDong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDongActionPerformed(evt);
            }
        });

        btnXuatFile.setBackground(new java.awt.Color(108, 92, 231));
        btnXuatFile.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnXuatFile.setForeground(new java.awt.Color(255, 255, 255));
        btnXuatFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nhom5/images/excel32.png"))); // NOI18N
        btnXuatFile.setText("Xuất file");
        btnXuatFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXuatFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnXuatFile)
                        .addGap(18, 18, 18)
                        .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 798, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(92, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXuatFile, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel2MouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        this.setState(JFrame.ICONIFIED);
    }//GEN-LAST:event_jLabel3MouseClicked

    private void btnDongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDongActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnDongActionPerformed

    private void btnXuatFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuatFileActionPerformed
        Export(dtm);
    }//GEN-LAST:event_btnXuatFileActionPerformed
        
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
            java.util.logging.Logger.getLogger(QuanLySinhVienDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QuanLySinhVienDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QuanLySinhVienDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QuanLySinhVienDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new QuanLySinhVienDialog().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDong;
    private javax.swing.JButton btnXuatFile;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblQuanLySV;
    // End of variables declaration//GEN-END:variables
}
