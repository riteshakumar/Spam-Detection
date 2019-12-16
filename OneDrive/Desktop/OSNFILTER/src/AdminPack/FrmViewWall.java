/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmViewWall.java
 *
 * Created on Dec 6, 2011, 12:57:07 PM
 */
package AdminPack;

import MyPack.TestimonialInfo;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

/**
 *
 * @author sagar
 */
public class FrmViewWall extends javax.swing.JFrame implements MouseListener {

    Vector<TestimonialInfo> allTestimonials = new Vector<TestimonialInfo>();
    Vector<PnlWall> wallVector;
    Statement stmt;
    TestimonialInfo ti;
    int selectedPanel = -1;
    /**
     * Creates new form FrmViewWall
     */
    MainForm parent;

    public FrmViewWall(MainForm parent) {
        initComponents();
        Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(sd.width / 2 - this.getWidth() / 2, sd.height / 2 - this.getHeight() / 2);
        viewTest();
        fillPanels();
        this.parent = parent;
    }

    void fillPanels() {
        jScrollPane1.getViewport().removeAll();
        jPanelHolder.removeAll();
        jPanelHolder.setLayout(new GridLayout(allTestimonials.size(), 1));
        wallVector = new Vector<PnlWall>();
        for (int i = 0; i < allTestimonials.size(); i++) {
            PnlWall p = new PnlWall();
            p.fillData(allTestimonials.elementAt(i));
            p.addMouseListener(this);
            wallVector.addElement(p);
            jPanelHolder.add(p);
        }
        jScrollPane1.getViewport().add(jPanelHolder);
    }

    void viewTest() {
        parent.initDatabase();
        try {
            allTestimonials = new Vector<TestimonialInfo>();
            stmt = parent.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String ssql = "select * from testimonial";
            System.out.println(ssql);
            ResultSet rs = stmt.executeQuery(ssql);
            while (rs.next()) {
                ti = new TestimonialInfo();
                ti.tid = rs.getInt("tid");
                ti.mdate = rs.getString("mdate");
                ti.text = rs.getString("text");
                ti.time = rs.getString("mtime");
                allTestimonials.addElement(ti);
            }
            System.out.println("Query Executed");
        } catch (Exception e) {
            System.out.println("Error Fetching data : " + e);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        for (int i = 0; i < wallVector.size(); i++) {
            if (e.getSource().equals(wallVector.elementAt(i))) {
                wallVector.elementAt(i).setSelected(true);
                selectedPanel = i;
            } else {
                wallVector.elementAt(i).setSelected(false);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        ///throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
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
        jButton3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelHolder = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        new JavaLib.LoadForm();
        jPanel1.setBackground(new java.awt.Color(156, 166, 166));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel1.setAutoscrolls(true);

        new JavaLib.LoadForm();
        jLabel1.setBackground(new java.awt.Color(75, 96, 96));
        jLabel1.setFont(new java.awt.Font("Calibri", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("V I E W   C O M M E N T S");
        jLabel1.setOpaque(true);

        jButton3.setBackground(new java.awt.Color(204, 204, 204));
        jButton3.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/osn spam detection/Back. (2).png"))); // NOI18N
        jButton3.setBorder(null);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        new JavaLib.LoadForm();
        jPanelHolder.setBackground(new java.awt.Color(224, 230, 230));

        javax.swing.GroupLayout jPanelHolderLayout = new javax.swing.GroupLayout(jPanelHolder);
        jPanelHolder.setLayout(jPanelHolderLayout);
        jPanelHolderLayout.setHorizontalGroup(
            jPanelHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 728, Short.MAX_VALUE)
        );
        jPanelHolderLayout.setVerticalGroup(
            jPanelHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 442, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanelHolder);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(497, 497, 497)
                        .addComponent(jButton3)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        parent.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelHolder;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}