/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package examen2parcialaxel;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

/**
 *
 * @author Sairi
 */
public class DiccionarioCliente extends javax.swing.JFrame {
    
    DataOutputStream flujoSalida;
    DataInputStream flujoEntrada;
    Socket cliente;
    DefaultListModel dlm;
    HashMap<String,String> dict;
    public static ManejadorMulticast mult=null;
    
    public DiccionarioCliente() throws UnknownHostException, IOException {
        if(mult==null){
            mult=new ManejadorMulticast();
        }
        initComponents(); 
        listar();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<String>();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jList1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jList1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jLabel1.setText("Definición");

        jButton2.setText("Insertar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel2.setText("jLabel2");
        jScrollPane3.setViewportView(jLabel2);

        jButton3.setText("Eliminar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jTextField1.setText("jTextField1");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        jLabel3.setText("Nueva Palabra");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3))
                        .addGap(50, 50, 50)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(129, 129, 129))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            insertar();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jList1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jList1KeyPressed
        // TODO add your handling code here:
        
        if(evt.getKeyCode()==127){//La tecla oprimida es Supr
            int selectedIndex = jList1.getSelectedIndex();
            if(selectedIndex >=0)
                try {
                    borrar();
            } catch (IOException ex) {
                Logger.getLogger(DiccionarioCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
    }//GEN-LAST:event_jList1KeyPressed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
            // TODO add your handling code here:
        try{
            borrar();
        }catch(Exception e){
        }
        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        int selectedIndex = jList1.getSelectedIndex();
        if(selectedIndex >=0)
            mostrarDefinicion();
            
       
        
    }//GEN-LAST:event_jList1MouseClicked

    private void mostrarDefinicion() {
        jLabel2.setText(dict.get((String) dlm.get(jList1.getSelectedIndex())));
        
        
    }
    
    public void insertar() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream(3000*4);
        DataOutputStream dos2 = new DataOutputStream(baos);  
        
            cliente = mult.conectar();
            flujoSalida = new DataOutputStream(cliente.getOutputStream());
            flujoEntrada = new DataInputStream(cliente.getInputStream());

        dos2.writeInt(0);
        
        dos2.writeInt(jTextField1.getText().length());
        dos2.writeInt(jTextArea1.getText().length());
        dos2.write(jTextField1.getText().getBytes());
        dos2.write(jTextArea1.getText().getBytes());
        flujoSalida.write(baos.toByteArray());
        flujoSalida.flush();
        jTextField1.setText("");
        jTextArea1.setText("");
        int answer = flujoEntrada.readInt();
        System.out.println(answer);
        cliente.close();
        listar();
    }
    
    public void listar() throws IOException{  
            
        
            cliente = mult.conectar();
            flujoSalida = new DataOutputStream(cliente.getOutputStream());
            flujoEntrada = new DataInputStream(cliente.getInputStream());

            dlm = new DefaultListModel();
            jList1.setModel(dlm);
            dict=new HashMap<>();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(3000*4);
            DataOutputStream flujoAux = new DataOutputStream(baos);
            flujoAux.writeInt(2);
            
            flujoSalida.write(baos.toByteArray());
            flujoSalida.flush();
            int number_palabras = flujoEntrada.readInt();
            byte buffer[] = new byte[300];
            System.out.println("Leyendo "+number_palabras + "palabras");
            
            for(int i=0; i<number_palabras; i++){
                int n = flujoEntrada.readInt();
                flujoEntrada.read(buffer, 0, n);
                String palabra = new String(buffer, 0, n);
                dlm.addElement(palabra);
                n = flujoEntrada.readInt();
                flujoEntrada.read(buffer, 0, n);
                String significado = new String(buffer, 0, n);
                
                dict.put(palabra, significado);
            }
            
            cliente.close();
    }
    
    public void borrar() throws IOException{ 
        
    
            cliente = mult.conectar();
            flujoSalida = new DataOutputStream(cliente.getOutputStream());
            flujoEntrada = new DataInputStream(cliente.getInputStream());

        
            String palabra = (String) dlm.get(jList1.getSelectedIndex());
            System.out.println("\nEnviando operacion "+palabra.length());
            ByteArrayOutputStream baos = new ByteArrayOutputStream(3000*4);
            DataOutputStream dos2 = new DataOutputStream(baos); 
            
            
            dos2.writeInt(1);
            dos2.writeInt(palabra.length());
            dos2.write(palabra.getBytes());
            flujoSalida.write(baos.toByteArray());
            flujoSalida.flush();
            int answer = flujoEntrada.readInt();
            System.out.println("\nRespuesta "+answer);
            cliente.close();
            listar();
    }
    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
