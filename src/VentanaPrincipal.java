import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> crearYMostrarGUI());
    }

    private static void crearYMostrarGUI() {
        JFrame ventana = new JFrame("Ventana GUI");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(500, 300);

        // Crear el panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Crear etiquetas con nombres diferentes
        JLabel etiquetaCentro = new JLabel("Hola Mundo GUI con Swing", SwingConstants.CENTER);
        etiquetaCentro.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel etiquetaAbajo = new JLabel("Esta es mi primera ventana", SwingConstants.CENTER);
        etiquetaAbajo.setFont(new Font("Times New Roman", Font.BOLD, 16));

        JLabel etiquetaArriba = new JLabel("Esta es mi segunda ventana", SwingConstants.CENTER);
        etiquetaArriba.setFont(new Font("Times New Roman", Font.BOLD, 16));

        // Añadir etiquetas al panel principal
        panelPrincipal.add(etiquetaArriba, BorderLayout.NORTH);
        panelPrincipal.add(etiquetaCentro, BorderLayout.CENTER);
        panelPrincipal.add(etiquetaAbajo, BorderLayout.SOUTH);

        // Crear un panel para los botones
        JPanel panelBotones = new JPanel(new FlowLayout());

        // Botón 1
        JButton boton1 = new JButton("Mostrar mensaje");
        boton1.addActionListener(e -> JOptionPane.showMessageDialog(null, "¡Botón 1 presionado!"));
        panelBotones.add(boton1);

        // Botón 2 - cambia texto del centro
        JButton boton2 = new JButton("Cambiar texto centro");
        boton2.addActionListener(e -> etiquetaCentro.setText("¡Texto del centro cambiado!"));
        panelBotones.add(boton2);

        // Botón 3 - cambia texto de abajo
        JButton boton3 = new JButton("Cambiar texto abajo");
        boton3.addActionListener(e -> etiquetaAbajo.setText("¡Texto de abajo cambiado!"));
        panelBotones.add(boton3);

        // Añadir panel principal y panel de botones a la ventana
        ventana.getContentPane().add(panelPrincipal, BorderLayout.CENTER);
        ventana.getContentPane().add(panelBotones, BorderLayout.SOUTH);

        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}