/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package examen2parcialaxel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JOptionPane;

/**
 *
 * @author Sairi
 */
public class ManejadorMulticast {
    public static final String MCAST_ADDR = "230.0.0.1"; //dir clase D valida, grupo al que nos vamos a unir
    public static final int MCAST_PORT = 9013; //puerto multicast
    public static final int DGRAM_BUF_LEN = 512; //tamaño del buffer
    //public static final int TIMEOUT = 1; // tiempo para desconectar

    // Que tan rapido envio packets
    public static final int EXPIRE_SPEED = 100;
    public static final int PING_SPEED = 1000;
    //public static final int TTL = PING_SPEED / EXPIRE_SPEED; // Exacto
    public static final int TTL = 12; // +2 Buffer

    public static MulticastSocket socket;
    public static InetAddress group = null;

    public static ArrayList<String> servidores = new ArrayList<String>();
    public static ArrayList<Integer> srvttl = new ArrayList<Integer>();
    
    Receiver recv = new Receiver();
    Expirer exp = new Expirer();
    
    public ManejadorMulticast(){
        try{
            
            Thread th_recv = new Thread(recv);
            Thread th_exp = new Thread(exp);
            th_recv.start();
            th_exp.start();
            System.out.println("Deteniendo descubrimiento. Intentando conexion.");
            /**/
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Socket conectar(){
            exp.setStop(true);
            recv.setStop(true);
            Socket s=null;
            int i=0;
        while (true) {
            if(i==servidores.size())
                i=0;
            String[] data = (servidores.get(i)).split(":");
            System.out.println(data[1]);
            Integer port = Integer.valueOf(data[1]);
            
            try{
                s = new Socket(data[0].substring(1),port);
                BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String answer = input.readLine();
                System.out.println(answer);
            }catch(Exception e){
                i++;
                continue;
            }
            break;

        }
             // Muestro mensjae con lo que me envia el servidor, solo para probar
            return s;
    }
    static class Expirer implements Runnable{
        private Boolean stop = false;

        public void setStop(Boolean stop){
            this.stop = stop;
        }

        public void run() {
            while(!stop){
                try{
                // Remover ttl poco a poco y checar si algun servidor expiro
                    int i = 0;
                    for (Iterator<Integer> iterator = srvttl.iterator(); iterator.hasNext();) {
                        Integer value = iterator.next();
                        if(value > 0) {
                            srvttl.set(i,value-1);
                        } else if(value == 0) {
                            System.out.println("Servidor "+servidores.get(i)+" expiro... #"+servidores.size()); // TODO: Mostrar IP
                            iterator.remove();
                            servidores.remove(i);
                        }
                        i++;
                    }
                    Thread.sleep(EXPIRE_SPEED);
                }catch(Exception ev){
                    ev.printStackTrace();
                }
            }
        }  
    }

    static class Receiver implements Runnable{
        private Boolean stop = false;
        
        public void setStop(Boolean stop){
            this.stop = stop;
        }

        public void run(){
            try {
                group = InetAddress.getByName(MCAST_ADDR);
                socket = new MulticastSocket(MCAST_PORT); //socket tipo multicast
                socket.joinGroup(group); //se une al grupo
                while(!stop){
                    byte[] buf = new byte[DGRAM_BUF_LEN];//crea arreglo de bytes 
                    DatagramPacket recv = new DatagramPacket(buf,buf.length);//crea el datagram packet a recibir
                    socket.receive(recv);// ya se tiene el datagram packet
                    //System.out.println("Host remoto: "+recv.getAddress()); 
                    //System.out.println("Puerto: "+ recv.getPort());
                    byte [] data = recv.getData(); //aqui no se entienden los datos
                    String port_str = new String(data);
                    Integer port_num = Integer.parseInt(port_str.trim());
                    String address = recv.getAddress()+":"+port_num;
                    if(!servidores.contains(address)){
                        addNewServer(address);
                        System.out.println("Nuevo servidor encontrado: "+recv.getAddress()+":"+port_num);
                    }else{
                        srvttl.set(servidores.indexOf(address),TTL);
                    }
                }
            }catch (UnknownHostException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(2);
            }
        }
    }

    static void displayGUI() {
        // TODO: Agregar interfaz
        System.out.println("No UI");
    }

    static public void addNewServer(String address){
        servidores.add(address);
        int index = servidores.indexOf(address);
        srvttl.add(index,TTL);
    }
}
