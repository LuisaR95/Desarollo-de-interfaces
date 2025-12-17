import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Aplicación de Solicitud de Beca Refactorizada.
 * Sigue principios de Clean Code y separación de responsabilidades.
 */
public class BecaFormularioSwing extends JFrame {

    // --- CONSTANTES DE DISEÑO ---
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color SECONDARY_COLOR = new Color(75, 85, 99);
    private static final Color BACKGROUND_COLOR = new Color(249, 250, 251);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private static final Color ERROR_COLOR = new Color(239, 68, 68);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);

    // --- ESTADO DE LA APLICACIÓN ---
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardsPanel = new JPanel(cardLayout);
    private final Map<String, FormField> formFields = new HashMap<>();
    private final Map<String, JLabel> summaryLabels = new HashMap<>();
    private int currentStep = 1;
    private final int totalSteps = 4;

    // Componentes de control
    private JButton btnAtras, btnSiguiente, btnConfirmar;
    private JLabel progressLabel;
    private JPanel progressIndicatorPanel;

    public BecaFormularioSwing() {
        setupFrame();
        initUI();
    }

    private void setupFrame() {
        setTitle("Portal de Solicitud de Becas v2.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(0, 0));
    }

    private void initUI() {
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Formulario (Center)
        cardsPanel.setBackground(BACKGROUND_COLOR);
        cardsPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        cardsPanel.add(createStep1(), "Paso1");
        cardsPanel.add(createStep2(), "Paso2");
        cardsPanel.add(createStep3(), "Paso3");
        cardsPanel.add(createStep4(), "Paso4");

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Footer
        add(createButtonPanel(), BorderLayout.SOUTH);

        updateNavigationUI();
    }

    // --- COMPONENTES DE LA INTERFAZ ---

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 25, 15, 25));

        JLabel titleLabel = new JLabel("Solicitud de Beca Académica");
        titleLabel.setFont(TITLE_FONT.deriveFont(24f));
        titleLabel.setForeground(SECONDARY_COLOR);

        progressLabel = new JLabel("Paso 1 de 4");
        progressLabel.setFont(MAIN_FONT.deriveFont(Font.BOLD));
        progressLabel.setForeground(PRIMARY_COLOR);

        progressIndicatorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        progressIndicatorPanel.setBackground(Color.WHITE);
        for (int i = 1; i <= totalSteps; i++) {
            progressIndicatorPanel.add(createCircleIndicator(i));
        }

        JPanel topInfo = new JPanel(new BorderLayout());
        topInfo.setOpaque(false);
        topInfo.add(titleLabel, BorderLayout.WEST);
        topInfo.add(progressLabel, BorderLayout.EAST);

        header.add(topInfo, BorderLayout.NORTH);
        header.add(progressIndicatorPanel, BorderLayout.CENTER);
        header.add(new JSeparator(), BorderLayout.SOUTH);

        return header;
    }

    private JComponent createCircleIndicator(int num) {
        JLabel label = new JLabel(String.valueOf(num), SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(30, 30));
        label.setOpaque(true);
        label.setFont(MAIN_FONT.deriveFont(Font.BOLD));
        label.setBackground(new Color(229, 231, 235));
        label.setForeground(SECONDARY_COLOR);
        // Usamos un borde redondo si el LookAndFeel lo permite o simplemente un panel
        return label;
    }

    private JPanel createStep1() {
        JPanel p = createStepBasePanel("1. Información Personal");
        addFormField(p, "Nombre", "nombre", "");
        addFormField(p, "Apellidos", "apellidos", "");
        addFormField(p, "DNI / NIE", "dni", "");
        addFormField(p, "Correo Electrónico", "email", "");
        addFormField(p, "Teléfono de Contacto", "telefono", "");
        addFormField(p, "Fecha de Nacimiento (AAAA-MM-DD)", "fechaNac", "2000-01-01");
        return p;
    }

    private JPanel createStep2() {
        JPanel p = createStepBasePanel("2. Trayectoria Académica");
        addFormField(p, "Centro de Estudios", "centro", "");
        addFormField(p, "Grado / Titulación", "grado", "");
        addFormField(p, "Curso Actual", "curso", "");
        return p;
    }

    private JPanel createStep3() {
        JPanel p = createStepBasePanel("3. Situación Socioeconómica");
        addFormField(p, "Nombre Tutor/a Legal", "tutor", "");
        addFormField(p, "Renta Anual Familiar (€)", "renta", "0.00");
        addFormField(p, "Miembros de la Unidad Familiar", "miembros", "1");
        return p;
    }

    private JPanel createStep4() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(BACKGROUND_COLOR);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR), "4. Resumen de Solicitud",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, TITLE_FONT, PRIMARY_COLOR));

        JPanel grid = new JPanel(new GridLayout(0, 1, 5, 5));
        grid.setBackground(Color.WHITE);
        grid.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] summaryFields = {"Nombre Completo", "DNI", "Titulación", "Renta Familiar"};
        String[] keys = {"resNombre", "resDni", "resGrado", "resRenta"};

        for (int i = 0; i < summaryFields.length; i++) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            JLabel lbl = new JLabel(summaryFields[i] + ":");
            lbl.setFont(MAIN_FONT.deriveFont(Font.BOLD));
            JLabel val = new JLabel("---");
            val.setForeground(PRIMARY_COLOR.darker());
            summaryLabels.put(keys[i], val);
            row.add(lbl, BorderLayout.WEST);
            row.add(val, BorderLayout.EAST);
            grid.add(row);
        }

        p.add(grid, BorderLayout.NORTH);

        JTextArea notice = new JTextArea("Al confirmar, declara que todos los datos son verídicos. Se generará un archivo XML legal para su presentación.");
        notice.setWrapStyleWord(true);
        notice.setLineWrap(true);
        notice.setEditable(false);
        notice.setBackground(new Color(239, 246, 255));
        notice.setBorder(new EmptyBorder(10,10,10,10));
        p.add(notice, BorderLayout.CENTER);

        return p;
    }

    private JPanel createButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 231, 235)));

        btnAtras = createStyledButton("Anterior", SECONDARY_COLOR);
        btnSiguiente = createStyledButton("Siguiente", PRIMARY_COLOR);
        btnConfirmar = createStyledButton("Enviar Solicitud", SUCCESS_COLOR);

        btnAtras.addActionListener(e -> navigate(-1));
        btnSiguiente.addActionListener(e -> navigate(1));
        btnConfirmar.addActionListener(this::handleFinalize);

        p.add(btnAtras);
        p.add(btnSiguiente);
        p.add(btnConfirmar);
        return p;
    }

    // --- LÓGICA DE AYUDA (HELPERS) ---

    private JPanel createStepBasePanel(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BACKGROUND_COLOR);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), title,
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, TITLE_FONT, SECONDARY_COLOR));
        return p;
    }

    private void addFormField(JPanel container, String label, String key, String defaultValue) {
        FormField field = new FormField(label, defaultValue);
        formFields.put(key, field);
        container.add(field);
        container.add(Box.createVerticalStrut(15));
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(MAIN_FONT.deriveFont(Font.BOLD));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        return btn;
    }

    private void navigate(int delta) {
        if (delta > 0 && !validateCurrentStep()) return;

        currentStep += delta;
        updateNavigationUI();
        cardLayout.show(cardsPanel, "Paso" + currentStep);
    }

    private boolean validateCurrentStep() {
        boolean valid = true;
        // Definir qué campos validar según el paso
        String[] fieldsToValidate = switch (currentStep) {
            case 1 -> new String[]{"nombre", "apellidos", "dni", "email", "fechaNac"};
            case 2 -> new String[]{"centro", "grado"};
            case 3 -> new String[]{"renta", "miembros"};
            default -> new String[]{};
        };

        for (String key : fieldsToValidate) {
            FormField f = formFields.get(key);
            if (f.getText().isEmpty()) {
                f.markInvalid(true);
                valid = false;
            } else {
                f.markInvalid(false);
            }
        }
        return valid;
    }

    private void updateNavigationUI() {
        btnAtras.setVisible(currentStep > 1);
        btnSiguiente.setVisible(currentStep < totalSteps);
        btnConfirmar.setVisible(currentStep == totalSteps);

        progressLabel.setText("Paso " + currentStep + " de " + totalSteps);

        // Actualizar círculos
        Component[] circles = progressIndicatorPanel.getComponents();
        for (int i = 0; i < circles.length; i++) {
            JLabel c = (JLabel) circles[i];
            int stepNum = i + 1;
            if (stepNum < currentStep) c.setBackground(SUCCESS_COLOR);
            else if (stepNum == currentStep) c.setBackground(PRIMARY_COLOR);
            else c.setBackground(new Color(229, 231, 235));
            c.setForeground(stepNum <= currentStep ? Color.WHITE : SECONDARY_COLOR);
        }

        if (currentStep == totalSteps) updateSummary();
    }

    private void updateSummary() {
        summaryLabels.get("resNombre").setText(formFields.get("nombre").getText() + " " + formFields.get("apellidos").getText());
        summaryLabels.get("resDni").setText(formFields.get("dni").getText());
        summaryLabels.get("resGrado").setText(formFields.get("grado").getText());
        summaryLabels.get("resRenta").setText(formFields.get("renta").getText() + " €");
    }

    private void handleFinalize(ActionEvent e) {
        try {
            BecaData data = collectData();
            saveToFile(data.toXML(), data.dni);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en los datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BecaData collectData() throws Exception {
        BecaData d = new BecaData();
        try {
            d.nombre = formFields.get("nombre").getText();
            d.apellidos = formFields.get("apellidos").getText();
            d.dni = formFields.get("dni").getText();
            d.email = formFields.get("email").getText();
            d.fechaNac = LocalDate.parse(formFields.get("fechaNac").getText());
            d.renta = Double.parseDouble(formFields.get("renta").getText());
            d.miembros = Integer.parseInt(formFields.get("miembros").getText());
            d.centro = formFields.get("centro").getText();
            d.grado = formFields.get("grado").getText();
        } catch (DateTimeParseException ex) {
            throw new Exception("Formato de fecha inválido (AAAA-MM-DD)");
        } catch (NumberFormatException ex) {
            throw new Exception("La renta y miembros deben ser valores numéricos.");
        }
        return d;
    }

    private void saveToFile(String content, String id) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("beca_" + id + ".xml"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                fw.write(content);
                JOptionPane.showMessageDialog(this, "¡Solicitud guardada con éxito!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al escribir archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- CLASES INTERNAS (MODULARIZACIÓN) ---

    /**
     * Componente personalizado que agrupa Label + TextField con validación visual.
     */
    private static class FormField extends JPanel {
        private final JTextField textField;
        private final JLabel label;

        public FormField(String labelText, String defaultValue) {
            setLayout(new BorderLayout(5, 5));
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            label = new JLabel(labelText);
            label.setFont(MAIN_FONT.deriveFont(Font.BOLD));
            label.setForeground(SECONDARY_COLOR);

            textField = new JTextField(defaultValue);
            textField.setFont(MAIN_FONT);
            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(209, 213, 219)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));

            add(label, BorderLayout.NORTH);
            add(textField, BorderLayout.CENTER);
        }

        public String getText() { return textField.getText().trim(); }

        public void markInvalid(boolean invalid) {
            Color color = invalid ? ERROR_COLOR : new Color(209, 213, 219);
            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, invalid ? 2 : 1),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
        }
    }

    /**
     * Modelo de datos puro (POJO) con lógica de exportación.
     */
    private static class BecaData {
        String nombre, apellidos, dni, email, centro, grado;
        LocalDate fechaNac;
        double renta;
        int miembros;

        public String toXML() {
            return String.format(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<Solicitud>\n" +
                            "  <Personal>\n" +
                            "    <Nombre>%s %s</Nombre>\n" +
                            "    <DNI>%s</DNI>\n" +
                            "    <Email>%s</Email>\n" +
                            "    <Nacimiento>%s</Nacimiento>\n" +
                            "  </Personal>\n" +
                            "  <Academico>\n" +
                            "    <Centro>%s</Centro>\n" +
                            "    <Grado>%s</Grado>\n" +
                            "  </Academico>\n" +
                            "  <Economico>\n" +
                            "    <Renta>%.2f</Renta>\n" +
                            "    <Miembros>%d</Miembros>\n" +
                            "  </Economico>\n" +
                            "</Solicitud>",
                    nombre, apellidos, dni, email, fechaNac, centro, grado, renta, miembros
            );
        }
    }

    public static void main(String[] args) {
        // Intentar usar FlatLaf o Nimbus para un look moderno
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { /* Fallback al sistema */ }

        SwingUtilities.invokeLater(() -> new BecaFormularioSwing().setVisible(true));
    }
}