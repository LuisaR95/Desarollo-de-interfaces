import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

public class FormularioContacto extends JFrame {

    private JTextField tfNombre, tfApellido, tfTelefono;
    private JTextArea taMensaje;
    private JButton btnValidar, btnLimpiar;

    public FormularioContacto() {
        super("Formulario de Contacto");
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        tfNombre = new JTextField(20);
        add(tfNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Apellido:"), gbc);
        gbc.gridx = 1;
        tfApellido = new JTextField(20);
        add(tfApellido, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        tfTelefono = new JTextField(20);
        add(tfTelefono, gbc);

        // Mensaje
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Mensaje:"), gbc);
        gbc.gridx = 1;
        taMensaje = new JTextArea(5, 20);
        JScrollPane scroll = new JScrollPane(taMensaje);
        add(scroll, gbc);

        // Botones
        JPanel panelBotones = new JPanel();
        btnValidar = new JButton("Validar");
        btnLimpiar = new JButton("Limpiar");
        panelBotones.add(btnValidar);
        panelBotones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(panelBotones, gbc);

        // Acciones
        btnValidar.addActionListener(e -> validarFormulario());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        // Config ventana
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

        if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || mensaje.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!telefono.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "El teléfono debe contener solo números", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        guardarEnArchivo(nombre, apellido, telefono, mensaje);
        JOptionPane.showMessageDialog(this, "Formulario válido y guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void guardarEnArchivo(String nombre, String apellido, String telefono, String mensaje) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("usuarios.txt", true))) {
            writer.write("Nombre: " + nombre + "\n");
            writer.write("Apellidos: " + apellido + "\n");
            writer.write("Teléfono: " + telefono + "\n");
            writer.write("Mensaje: " + mensaje + "\n");
            writer.write("---------------\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar archivo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        FormularioContacto.convertirTxtAXml("usuarios.txt", "usuarios.xml");
    }

    // 🔽 Método adicional para convertir TXT a XML
    public static void convertirTxtAXml(String archivoTXT, String archivoXML) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoTXT))) {

            // Crear documento XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("usuarios");
            doc.appendChild(root);

            String linea, nombre = "", apellidos = "", telefono = "", mensaje = "";
            while ((linea = reader.readLine()) != null) {
                if (linea.startsWith("Nombre:")) nombre = linea.substring(7).trim();
                else if (linea.startsWith("Apellidos:")) apellidos = linea.substring(10).trim();
                else if (linea.startsWith("Teléfono:")) telefono = linea.substring(9).trim();
                else if (linea.startsWith("Mensaje:")) mensaje = linea.substring(8).trim();
                else if (linea.startsWith("---------------")) {
                    // Crear elemento usuario
                    Element usuario = doc.createElement("usuario");

                    Element eNombre = doc.createElement("nombre");
                    eNombre.setTextContent(nombre);
                    usuario.appendChild(eNombre);

                    Element eApellidos = doc.createElement("apellidos");
                    eApellidos.setTextContent(apellidos);
                    usuario.appendChild(eApellidos);

                    Element eTelefono = doc.createElement("telefono");
                    eTelefono.setTextContent(telefono);
                    usuario.appendChild(eTelefono);

                    Element eMensaje = doc.createElement("mensaje");
                    eMensaje.setTextContent(mensaje);
                    usuario.appendChild(eMensaje);

                    root.appendChild(usuario);

                    // Reiniciar variables para siguiente usuario
                    nombre = apellidos = telefono = mensaje = "";
                }
            }

            // Guardar XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(archivoXML));
            transformer.transform(source, result);

            System.out.println("Archivo XML generado correctamente: " + archivoXML);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
