import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FormularioContacto extends JFrame {

    private JTextField tfNombre, tfApellido, tfTelefono;
    private JTextArea taMensaje;
    private JButton btnValidar, btnLimpiar;

    public FormularioContacto() {
        super("Formulario de Contacto");

        // Layout principal
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Margen entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1;
        tfNombre = new JTextField(20);
        add(tfNombre, gbc);

        // Apellido
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Apellido:"), gbc);

        gbc.gridx = 1;
        tfApellido = new JTextField(20);
        add(tfApellido, gbc);

        // Teléfono
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Teléfono:"), gbc);

        gbc.gridx = 1;
        tfTelefono = new JTextField(20);
        add(tfTelefono, gbc);

        // Mensaje (JTextArea con scroll)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTH;
        add(new JLabel("Mensaje:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        taMensaje = new JTextArea(5, 20);
        taMensaje.setLineWrap(true);
        taMensaje.setWrapStyleWord(true);
        JScrollPane scrollMensaje = new JScrollPane(taMensaje);
        add(scrollMensaje, gbc);

        // Botones
        JPanel panelBotones = new JPanel();
        btnValidar = new JButton("Validar");
        btnLimpiar = new JButton("Limpiar");
        panelBotones.add(btnValidar);
        panelBotones.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        add(panelBotones, gbc);

        // Acciones
        btnValidar.addActionListener(e -> validarFormulario());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        // Configuración ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void validarFormulario() {
        String nombre = tfNombre.getText().trim();
        String apellido = tfApellido.getText().trim();
        String telefono = tfTelefono.getText().trim();
        String mensaje = taMensaje.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (apellido.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El apellido es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El telefono es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (mensaje.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El mensaje es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!telefono.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "El teléfono debe contener solo números.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Guardar en archivo
        guardarEnArchivo(nombre, apellido, telefono, mensaje);

        JOptionPane.showMessageDialog(this, "Formulario válido y guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void guardarEnArchivo(String nombre, String apellido, String telefono, String mensaje) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("contactos.txt", true))) {
            writer.write("Nombre: " + nombre);
            writer.newLine();
            writer.write("Apellido: " + apellido);
            writer.newLine();
            writer.write("Teléfono: " + telefono);
            writer.newLine();
            writer.write("Mensaje: " + mensaje);
            writer.newLine();
            writer.write("---------------");
            writer.newLine();
            System.out.println("Datos guardados");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar en archivo: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        tfNombre.setText("");
        tfApellido.setText("");
        tfTelefono.setText("");
        taMensaje.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FormularioContacto::new);
    }
}
