import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aplicación profesional para generar informes de notas.
 * Incluye un manual de usuario con estética mejorada mediante HTML/CSS.
 */
public class InformeNotasPDF extends JFrame {

    static class NotaAlumno {
        String nombre, apellido, asignatura;
        double nota;

        NotaAlumno(String nom, String ape, String asig, double n) {
            this.nombre = nom;
            this.apellido = ape;
            this.asignatura = asig;
            this.nota = n;
        }
    }

    private List<NotaAlumno> listaDatos = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTextField txtNombre, txtApellido, txtNota;
    private JComboBox<String> cbAsignatura;

    public InformeNotasPDF() {
        setTitle("Generador de Informes de Notas - Pro Edition");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // --- Panel Superior ---
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSuperior.setBackground(new Color(236, 240, 241));

        JButton btnManual = new JButton("Manual de Usuario");
        JButton btnAcercaDe = new JButton("Acerca de");

        panelSuperior.add(btnManual);
        panelSuperior.add(btnAcercaDe);
        add(panelSuperior, BorderLayout.NORTH);

        // --- Panel Central ---
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        JPanel panelInput = new JPanel(new GridLayout(5, 2, 10, 10));
        panelInput.setBorder(BorderFactory.createTitledBorder("Registro de Calificaciones"));

        panelInput.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelInput.add(txtNombre);

        panelInput.add(new JLabel("Apellido:"));
        txtApellido = new JTextField();
        panelInput.add(txtApellido);

        panelInput.add(new JLabel("Asignatura:"));
        String[] materias = {"Matemáticas", "Física", "Programación", "Historia", "Literatura", "Química", "Inglés"};
        cbAsignatura = new JComboBox<>(materias);
        panelInput.add(cbAsignatura);

        panelInput.add(new JLabel("Nota (0-10):"));
        txtNota = new JTextField();
        panelInput.add(txtNota);

        JButton btnAgregar = new JButton("Añadir a la Lista");
        btnAgregar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnAgregar.setBackground(new Color(39, 174, 96));
        btnAgregar.setForeground(Color.WHITE);
        panelInput.add(btnAgregar);

        panelCentral.add(panelInput, BorderLayout.NORTH);

        String[] columnas = {"Nombre", "Apellido", "Asignatura", "Nota"};
        tableModel = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(tableModel);
        tabla.setRowHeight(25);
        panelCentral.add(new JScrollPane(tabla), BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);

        // --- Panel Inferior ---
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelAcciones.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 20));

        JButton btnGenerar = new JButton("GENERAR INFORME PDF");
        btnGenerar.setPreferredSize(new Dimension(240, 50));
        btnGenerar.setBackground(new Color(21, 67, 96));
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnGenerar.setFocusPainted(false);

        panelAcciones.add(btnGenerar);
        add(panelAcciones, BorderLayout.SOUTH);

        // --- Eventos ---

        btnAgregar.addActionListener(e -> agregarAlumno());

        btnGenerar.addActionListener(e -> {
            if (listaDatos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe añadir al menos un alumno.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            generarInformePDF();
        });

        btnAcercaDe.addActionListener(e -> mostrarAcercaDe());

        btnManual.addActionListener(e -> mostrarManualBonito());
    }

    private void mostrarManualBonito() {
        // Usamos HTML y CSS para darle estilo al manual
        String htmlContent = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: 'SansSerif'; margin: 20px; color: #2c3e50; }" +
                "h1 { color: #2980b9; border-bottom: 2px solid #2980b9; padding-bottom: 5px; }" +
                "h2 { color: #16a085; margin-top: 15px; }" +
                ".step { font-weight: bold; color: #e67e22; }" +
                "ul { margin-left: 20px; }" +
                "li { margin-bottom: 8px; }" +
                ".footer { font-size: 9px; color: #95a5a6; margin-top: 20px; text-align: center; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h1>Guía de Usuario</h1>" +
                "<p>Bienvenido al generador de informes. Siga estos pasos para crear su documento:</p>" +
                "<h2>1. Registro de Datos</h2>" +
                "<ul>" +
                "<li><span class='step'>Paso A:</span> Escriba el nombre y apellido en los campos superiores.</li>" +
                "<li><span class='step'>Paso B:</span> Seleccione la materia desde el menú desplegable.</li>" +
                "<li><span class='step'>Paso C:</span> Ingrese la calificación final (ejemplo: <b>9.5</b>).</li>" +
                "</ul>" +
                "<h2>2. Gestión de Lista</h2>" +
                "<ul>" +
                "<li>Haga clic en el botón verde <b>'Añadir a la Lista'</b> para guardar temporalmente al alumno.</li>" +
                "<li>Podrá ver los datos reflejados en la tabla central inmediatamente.</li>" +
                "</ul>" +
                "<h2>3. Exportación</h2>" +
                "<ul>" +
                "<li>Presione el botón azul <b>'GENERAR INFORME PDF'</b>.</li>" +
                "<li>El sistema creará un archivo llamado <i>'Informe_Calificaciones.pdf'</i> en la carpeta del programa.</li>" +
                "</ul>" +
                "<p style='background-color: #fcf8e3; padding: 10px; border: 1px solid #faebcc;'>" +
                "<b>Nota:</b> El informe organiza automáticamente a los alumnos por asignatura y calcula los promedios.</p>" +
                "<div class='footer'>Asistente de Usuario v2.0</div>" +
                "</body>" +
                "</html>";

        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setText(htmlContent);
        editorPane.setEditable(false);
        editorPane.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setPreferredSize(new Dimension(500, 450));
        scrollPane.setBorder(null);

        JOptionPane.showMessageDialog(this, scrollPane, "Manual de Instrucciones", JOptionPane.PLAIN_MESSAGE);
    }

    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(this,
                "Generador de Informes Académicos\nVersión 2.0\n\nSoftware de gestión eficiente.",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }

    private void agregarAlumno() {
        try {
            String nom = txtNombre.getText().trim();
            String ape = txtApellido.getText().trim();
            String asig = (String) cbAsignatura.getSelectedItem();
            String notaStr = txtNota.getText().trim();

            if (nom.isEmpty() || ape.isEmpty() || notaStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos de texto.");
                return;
            }

            double nota = Double.parseDouble(notaStr);
            if (nota < 0 || nota > 10) {
                JOptionPane.showMessageDialog(this, "La nota debe estar en el rango de 0.0 a 10.0.");
                return;
            }

            listaDatos.add(new NotaAlumno(nom, ape, asig, nota));
            tableModel.addRow(new Object[]{nom, ape, asig, nota});

            txtNombre.setText("");
            txtApellido.setText("");
            txtNota.setText("");
            txtNombre.requestFocus();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: Use números con punto decimal (ej: 8.5).");
        }
    }

    private void generarInformePDF() {
        Collections.sort(listaDatos, (a1, a2) -> a1.asignatura.compareToIgnoreCase(a2.asignatura));

        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, new FileOutputStream("Informe_Calificaciones.pdf"));
            doc.open();

            com.itextpdf.text.Font fTitulo = FontFactory.getFont(FontFactory.HELVETICA, 18, com.itextpdf.text.Font.BOLD, BaseColor.DARK_GRAY);
            com.itextpdf.text.Font fAsignatura = FontFactory.getFont(FontFactory.HELVETICA, 14, com.itextpdf.text.Font.BOLD, BaseColor.BLUE);
            com.itextpdf.text.Font fMedia = FontFactory.getFont(FontFactory.HELVETICA, 11, com.itextpdf.text.Font.BOLDITALIC, BaseColor.DARK_GRAY);
            com.itextpdf.text.Font fGlobal = FontFactory.getFont(FontFactory.HELVETICA, 12, com.itextpdf.text.Font.BOLD, BaseColor.RED);

            Paragraph pTitulo = new Paragraph("INFORME DE NOTAS POR ASIGNATURA", fTitulo);
            pTitulo.setAlignment(Element.ALIGN_CENTER);
            pTitulo.setSpacingAfter(20);
            doc.add(pTitulo);

            String asignaturaActual = "";
            double sumaAsignatura = 0;
            int contadorAsignatura = 0;
            double sumaGlobal = 0;
            int contadorGlobal = 0;

            for (NotaAlumno alumno : listaDatos) {
                if (!alumno.asignatura.equalsIgnoreCase(asignaturaActual)) {
                    if (!asignaturaActual.equals("")) {
                        doc.add(new Paragraph("   > Nota media de " + asignaturaActual + ": " +
                                String.format("%.2f", (sumaAsignatura / contadorAsignatura)), fMedia));
                        doc.add(new Paragraph(" "));
                        sumaAsignatura = 0;
                        contadorAsignatura = 0;
                    }

                    asignaturaActual = alumno.asignatura;
                    doc.add(new Paragraph("ASIGNATURA: " + asignaturaActual, fAsignatura));
                    doc.add(new Paragraph("-----------------------------------------------------------------------"));
                }

                doc.add(new Paragraph(String.format(" - %s %s: %.2f", alumno.nombre, alumno.apellido, alumno.nota)));

                sumaAsignatura += alumno.nota;
                contadorAsignatura++;
                sumaGlobal += alumno.nota;
                contadorGlobal++;
            }

            doc.add(new Paragraph("   > Nota media de " + asignaturaActual + ": " +
                    String.format("%.2f", (sumaAsignatura / contadorAsignatura)), fMedia));

            doc.add(new Paragraph("\n\n==============================================="));
            double mediaGeneral = sumaGlobal / contadorGlobal;
            Paragraph pFinal = new Paragraph("NOTA MEDIA GENERAL: " + String.format("%.2f", mediaGeneral), fGlobal);
            pFinal.setAlignment(Element.ALIGN_RIGHT);
            doc.add(pFinal);

            doc.close();
            JOptionPane.showMessageDialog(this, "Archivo PDF guardado correctamente.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error técnico al crear el PDF.");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new InformeNotasPDF().setVisible(true));
    }
}