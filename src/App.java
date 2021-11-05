import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;

public class App {
    static JFrame ventanaChat = null;
    static JButton enviar = null;
    static JTextArea areaChat = null;
    static JTextField mensaje = null;
    static JPanel contenedorChat = null;
    static JPanel contenedorBoton = null;
    static JScrollPane scroll = null;
    static ServerSocket servidor = null;
    static Socket socket = null;
    static BufferedReader lector = null;
    static PrintWriter escritor = null;

    public static void main(String[] args) throws Exception {
        new App();
        new Cliente();
    }

    public App() {
        interfazGrafica();
    }

    public static void interfazGrafica() {
        ventanaChat = new JFrame("servidor");
        enviar = new JButton("Enviar");
        mensaje = new JTextField(4);
        areaChat = new JTextArea(10, 12);
        areaChat.setEditable(false);
        scroll = new JScrollPane(areaChat);
        contenedorChat = new JPanel();
        contenedorChat.setLayout(new GridLayout(1, 1));
        contenedorChat.add(scroll);
        contenedorBoton = new JPanel();
        contenedorBoton.setLayout(new GridLayout(1, 2));
        contenedorBoton.add(mensaje);
        contenedorBoton.add(enviar);
        ventanaChat.setLayout(new BorderLayout());
        ventanaChat.add(contenedorChat, BorderLayout.NORTH);
        ventanaChat.add(contenedorBoton, BorderLayout.SOUTH);
        ventanaChat.setVisible(true);
        ventanaChat.setSize(300, 220);
        ventanaChat.setResizable(false);
        ventanaChat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Ejecutar acciones conn un thread
        Thread principal = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    servidor = new ServerSocket(8069);
                    while (true) {
                        socket = servidor.accept();
                        leer();
                        escribir();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        principal.start();
    }

    public static void leer() {
        Thread leerHilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while (true) {
                        String mensajeRecibido = lector.readLine();
                        if (!mensajeRecibido.equals("")) {
                            areaChat.append("Cliente: " + mensajeRecibido + "\n");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        leerHilo.start();
    }

    public static void escribir() {
        // para poder obtener la salida

        Thread ecribirHilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    escritor = new PrintWriter(socket.getOutputStream(), true);
                    enviar.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String enviar = mensaje.getText();
                            areaChat.append("Servidor: " + enviar + "\n");
                            escritor.println(enviar);
                            mensaje.setText("");
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ecribirHilo.start();
    }

}
