import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Aplicación para generar informes de notas en PDF con agrupamiento por asignatura.
 * Solución al error 'cannot find symbol HELVETICA_BOLDITALIC'.
 */
public class InformeNotasPDF extends JFrame {

    // Clase interna para representar el modelo de datos
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
    private JTextField txtNombre, txtApellido, txtAsignatura, txtNota;

    public InformeNotasPDF() {
        setTitle("Generador de Informes de Notas - Diseño Jerárquico");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // --- Panel de Entrada de Datos (Formulario) ---
        JPanel panelInput = new JPanel(new GridLayout(5, 2, 10, 10));
        panelInput.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        panelInput.add(new JLabel("Nombre del Alumno:"));
        txtNombre = new JTextField();
        panelInput.add(txtNombre);

        panelInput.add(new JLabel("Apellido del Alumno:"));
        txtApellido = new JTextField();
        panelInput.add(txtApellido);

        panelInput.add(new JLabel("Nombre de la Asignatura:"));
        txtAsignatura = new JTextField();
        panelInput.add(txtAsignatura);

        panelInput.add(new JLabel("Nota Final (0-10):"));
        txtNota = new JTextField();
        panelInput.add(txtNota);

        JButton btnAgregar = new JButton("Añadir Alumno a la Lista");
        btnAgregar.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        panelInput.add(btnAgregar);

        add(panelInput, BorderLayout.NORTH);

        // --- Tabla para previsualizar los datos ingresados ---
        String[] columnas = {"Nombre", "Apellido", "Asignatura", "Nota"};
        tableModel = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(tableModel);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // --- Panel de Acciones Finales ---
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGenerar = new JButton("Generar Informe PDF");
        btnGenerar.setBackground(new Color(41, 128, 185));
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setFocusPainted(false);
        panelAcciones.add(btnGenerar);
        add(panelAcciones, BorderLayout.SOUTH);

        // --- Lógica de los Botones ---

        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nom = txtNombre.getText().trim();
                    String ape = txtApellido.getText().trim();
                    String asig = txtAsignatura.getText().trim();
                    String notaStr = txtNota.getText().trim();

                    if (nom.isEmpty() || ape.isEmpty() || asig.isEmpty() || notaStr.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.");
                        return;
                    }

                    double nota = Double.parseDouble(notaStr);
                    if (nota < 0 || nota > 10) {
                        JOptionPane.showMessageDialog(null, "La nota debe estar entre 0 y 10.");
                        return;
                    }

                    listaDatos.add(new NotaAlumno(nom, ape, asig, nota));
                    tableModel.addRow(new Object[]{nom, ape, asig, nota});

                    txtNombre.setText("");
                    txtApellido.setText("");
                    txtAsignatura.setText("");
                    txtNota.setText("");
                    txtNombre.requestFocus();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Error: Ingrese un valor numérico válido para la nota.");
                }
            }
        });

        btnGenerar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listaDatos.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No hay datos para generar el informe.");
                    return;
                }
                generarInformePDF();
            }
        });
    }

    private void generarInformePDF() {
        // Ordenar por asignatura para el agrupamiento (Corte de Control)
        Collections.sort(listaDatos, new Comparator<NotaAlumno>() {
            @Override
            public int compare(NotaAlumno a1, NotaAlumno a2) {
                return a1.asignatura.compareToIgnoreCase(a2.asignatura);
            }
        });

        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, new FileOutputStream("Informe_Calificaciones.pdf"));
            doc.open();

            // CORRECCIÓN: Uso de constantes de estilo explícitas de iText para evitar errores de símbolo
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

                String textoAlumno = String.format(" - %s %s: %.2f", alumno.nombre, alumno.apellido, alumno.nota);
                doc.add(new Paragraph(textoAlumno));

                sumaAsignatura += alumno.nota;
                contadorAsignatura++;
                sumaGlobal += alumno.nota;
                contadorGlobal++;
            }

            // Media del último grupo
            doc.add(new Paragraph("   > Nota media de " + asignaturaActual + ": " +
                    String.format("%.2f", (sumaAsignatura / contadorAsignatura)), fMedia));

            // Media general final
            doc.add(new Paragraph("\n\n==============================================="));
            double mediaGeneral = sumaGlobal / contadorGlobal;
            Paragraph pFinal = new Paragraph("NOTA MEDIA GENERAL DE TODOS LOS ALUMNOS: " + String.format("%.2f", mediaGeneral), fGlobal);
            pFinal.setAlignment(Element.ALIGN_RIGHT);
            doc.add(pFinal);

            doc.close();
            JOptionPane.showMessageDialog(null, "Informe 'Informe_Calificaciones.pdf' generado con éxito.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al generar el PDF: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InformeNotasPDF().setVisible(true);
            }
        });
    }
}