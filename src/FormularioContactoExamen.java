import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormularioContactoExamen extends JFrame {

    private JTextField tfNombre, tfApellido, tfTelefono, tfDni, tfDireccion, tfEmail;

    private JButton btnValidar, btnLimpiar;

    public FormularioContactoExamen() {
        super("Formulario de Contacto");
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0; // Variable para controlar la fila actual

        // --- Campos de Formulario ---

        // Nombre (Fila 0)
        gbc.gridx = 0;
        gbc.gridy = fila;
        add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        tfNombre = new JTextField(20);
        add(tfNombre, gbc);
        fila++;

        // Apellidos (Fila 1)
        gbc.gridx = 0;
        gbc.gridy = fila;
        add(new JLabel("Apellidos:"), gbc);
        gbc.gridx = 1;
        tfApellido = new JTextField(20);
        add(tfApellido, gbc);
        fila++;

        // Teléfono (Fila 2)
        gbc.gridx = 0;
        gbc.gridy = fila;
        add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        tfTelefono = new JTextField(20);
        add(tfTelefono, gbc);
        fila++;

        // DNI (Fila 3)
        gbc.gridx = 0;
        gbc.gridy = fila;
        add(new JLabel("DNI:"), gbc);
        gbc.gridx = 1;
        tfDni = new JTextField(20);
        add(tfDni, gbc);
        fila++;

        // Dirección (Fila 4)
        gbc.gridx = 0;
        gbc.gridy = fila;
        add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1;
        tfDireccion = new JTextField(20);
        add(tfDireccion, gbc);
        fila++;

        // Email (Fila 5)
        gbc.gridx = 0;
        gbc.gridy = fila;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        tfEmail = new JTextField(20);
        add(tfEmail, gbc);
        fila++;


        // --- Botones ---
        JPanel panelBotones = new JPanel();
        btnValidar = new JButton("Validar y Guardar");
        btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnValidar);
        panelBotones.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        add(panelBotones, gbc);

        // --- Acciones ---
        btnValidar.addActionListener(e -> validarFormulario());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        // Config ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack(); // Ajusta la ventana al tamaño preferido de los componentes
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --- Métodos de Lógica de Negocio ---

    private void validarFormulario() {
        String nombre = tfNombre.getText().trim();
        String apellido = tfApellido.getText().trim();
        String telefono = tfTelefono.getText().trim();
        String dni = tfDni.getText().trim();
        String direccion = tfDireccion.getText().trim();
        String email = tfEmail.getText().trim();

        // 1. Validación de campos obligatorios
        if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || dni.isEmpty() || direccion.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Validación de Teléfono (solo números)
        if (!telefono.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "El teléfono debe contener solo números", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Validación de DNI (Formato simple: 8 dígitos y 1 letra)
        if (!validarDNI(dni)) {
            JOptionPane.showMessageDialog(this, "El DNI no tiene un formato válido (8 números y 1 letra)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. Validación de Email (Formato básico)
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            JOptionPane.showMessageDialog(this, "El formato del Email no es válido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Si todas las validaciones pasan:
        guardarEnArchivo(nombre, apellido, telefono, dni, direccion, email);
        JOptionPane.showMessageDialog(this, "Formulario válido y guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        limpiarFormulario(); // Limpiar después de guardar
    }

    // Método auxiliar para la validación de DNI (simple)
    private boolean validarDNI(String dni) {
        Pattern pattern = Pattern.compile("^[0-9]{8}[A-Za-z]$");
        Matcher matcher = pattern.matcher(dni);
        return matcher.matches();
    }

    private void guardarEnArchivo(String nombre, String apellido, String telefono, String dni, String direccion, String email) {
        // Usa 'true' para FileWriter para añadir (append) al archivo existente
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("usuarios.txt", true))) {
            writer.write("Nombre: " + nombre + "\n");
            writer.write("Apellidos: " + apellido + "\n");
            writer.write("Teléfono: " + telefono + "\n");
            writer.write("Dni: " + dni + "\n");
            writer.write("Direccion: " + direccion + "\n");
            writer.write("Email: " + email + "\n");
            writer.write("---------------\n");
            writer.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar archivo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        tfNombre.setText("");
        tfApellido.setText("");
        tfTelefono.setText("");
        tfDni.setText("");
        tfDireccion.setText("");
        tfEmail.setText("");
    }

    //Contar registros
    public static int contarRegistros(String archivo) {
        int contador = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                // Un registro termina cuando se encuentra la línea separadora
                if (linea.equals("---------------")) {
                    contador++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo " + archivo + ": " + e.getMessage());
            return -1; // Devuelve -1 en caso de error o si el archivo no existe
        }
        return contador;
    }

    // --- Método Principal ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FormularioContactoExamen::new);
        int totalRegistros = contarRegistros("usuarios.txt");
        if (totalRegistros != -1) {
            System.out.println("Registro guardados correctamente");
            System.out.println("El archivo usuarios.txt contiene " + totalRegistros + " registros.");
        }
    }
}