/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vistas;


import Modelo.Conexion;
import Modelo.MostrarEliminar;
import Modelo.Paciente;
import Modelo.tablapacientes;
import static Vistas.JDeditarpaciente.jtmostrarpacientes;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Usuario
 */
public class JDeliminarPaciente extends javax.swing.JDialog {
DefaultTableModel Modelo = new DefaultTableModel();
    /**
     * Creates new form JDeliminarPaciente
     */
    public JDeliminarPaciente(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        Modelo.addColumn("IdPaciente");
        Modelo.addColumn("Nombre");
        Modelo.addColumn("Apellido");
        Modelo.addColumn("Direccion");
        Modelo.addColumn("Telefono");
        JTdatosPaciente.setModel(Modelo);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSpinner1 = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtApellido = new javax.swing.JTextField();
        btnEliminar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        JTdatosPaciente = new javax.swing.JTable();
        btnBuscarPaciente = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtCd = new javax.swing.JTextField();
        btnEliminarPac2 = new javax.swing.JButton();
        btnVolver = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 51, 255));
        jLabel1.setText("Control de Pacientes");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(137, 11, -1, -1));

        jLabel2.setText("Apellido:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 55, -1, -1));
        getContentPane().add(txtApellido, new org.netbeans.lib.awtextra.AbsoluteConstraints(91, 52, 160, -1));

        btnEliminar.setText("Asignar Eliminacion");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });
        getContentPane().add(btnEliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 327, -1, -1));

        JTdatosPaciente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(JTdatosPaciente);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 85, 358, 224));

        btnBuscarPaciente.setText("Buscar Paciente");
        btnBuscarPaciente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarPacienteActionPerformed(evt);
            }
        });
        getContentPane().add(btnBuscarPaciente, new org.netbeans.lib.awtextra.AbsoluteConstraints(269, 51, -1, -1));

        jLabel3.setText("Codigo del paciente a Eliminar:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 331, -1, -1));
        getContentPane().add(txtCd, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 328, 101, -1));

        btnEliminarPac2.setText("Eliminiar Paciente");
        btnEliminarPac2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarPac2ActionPerformed(evt);
            }
        });
        getContentPane().add(btnEliminarPac2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 368, -1, -1));

        btnVolver.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnVolver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Regresar.jpg"))); // NOI18N
        btnVolver.setText("Volver");
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });
        getContentPane().add(btnVolver, new org.netbeans.lib.awtextra.AbsoluteConstraints(295, 427, 99, -1));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Salud1.jpg"))); // NOI18N
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 430, 450));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarPacienteActionPerformed
        if(txtApellido.getText()=="")
        {
            JOptionPane.showMessageDialog(null, "por favor ingrese el apellido");
        }
        else{
            MostrarEliminar ne = new MostrarEliminar();
        ne.Mostrarpacientes(Modelo, txtApellido.getText());
        }
        
    }//GEN-LAST:event_btnBuscarPacienteActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
         int fila=JTdatosPaciente.getSelectedRow();
         String valor=JTdatosPaciente.getValueAt(fila,0).toString();
         if(fila>=0){
             JDeliminarPaciente.txtCd.setText(JTdatosPaciente.getValueAt(fila, 0).toString());
         }
          
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnEliminarPac2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarPac2ActionPerformed
        Paciente nuevopaciente =new Paciente(null, null, null, null, null,null);
        MostrarEliminar ne = new MostrarEliminar();
        tablapacientes np= new tablapacientes();
        int msj=JOptionPane.showConfirmDialog(null, "¿Esta seguro de eliminar el registro?","Borrado",JOptionPane.YES_NO_OPTION);
                if(msj==JOptionPane.YES_OPTION)
                {
                    nuevopaciente.EliminarDiagnostico(Integer.parseInt(txtCd.getText()));
                    nuevopaciente.EliminarPaciente(Integer.parseInt(txtCd.getText()));
                    ne.Mostrarpacientes(Modelo, txtApellido.getText());
                    np.limpiarTabla(Modelo);
                }
                else if(msj==JOptionPane.NO_OPTION)
                        {
                            JOptionPane.showMessageDialog(null,"Operacion cancelada");
                        }              
    }//GEN-LAST:event_btnEliminarPac2ActionPerformed

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_btnVolverActionPerformed

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
            java.util.logging.Logger.getLogger(JDeliminarPaciente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDeliminarPaciente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDeliminarPaciente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDeliminarPaciente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDeliminarPaciente dialog = new JDeliminarPaciente(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JTable JTdatosPaciente;
    private javax.swing.JButton btnBuscarPaciente;
    public static javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEliminarPac2;
    private javax.swing.JButton btnVolver;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinner1;
    public static javax.swing.JTextField txtApellido;
    public static javax.swing.JTextField txtCd;
    // End of variables declaration//GEN-END:variables
}
