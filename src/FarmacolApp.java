import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * FarmacolApp - Versión con Imágenes, Carrito Visual, Actualización de Precios y Login con Contraseña.
 * Ejecutable en IntelliJ IDEA.
 */

// --- 1. MODELO DE DATOS ---
class Producto {
    private final int id;
    private final String nombre;
    private final double precio;
    private final String imagenUrl;
    private final String emojiFallback;

    public Producto(int id, String nombre, double precio, String imagenUrl, String emojiFallback) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
        this.emojiFallback = emojiFallback;
    }

    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public String getImagenUrl() { return imagenUrl; }
    public String getEmojiFallback() { return emojiFallback; }
}

// --- 2. VIEWMODEL (Gestión de Estado) ---
class FarmacolViewModel {
    private final List<Producto> carrito = new ArrayList<>();
    private Consumer<List<Producto>> listener;

    public List<Producto> getListaProductos() {
        return Arrays.asList(
                new Producto(1, "Aspirina", 15.50, "https://cdn-icons-png.flaticon.com/512/822/822143.png", "💊"),
                new Producto(2, "Vitamina C", 22.90, "https://cdn-icons-png.flaticon.com/512/2903/2903513.png", "🍊"),
                new Producto(3, "Ibuprofeno", 18.20, "https://cdn-icons-png.flaticon.com/512/4320/4320348.png", "🩹"),
                new Producto(4, "Jarabe", 35.00, "https://cdn-icons-png.flaticon.com/512/3047/3047871.png", "🧪")
        );
    }

    public List<Producto> getCarrito() { return carrito; }

    public void setOnCarritoChanged(Consumer<List<Producto>> listener) {
        this.listener = listener;
    }

    public void agregarProducto(Producto p) {
        carrito.add(p);
        if (listener != null) listener.accept(carrito);
    }

    public void vaciarCarrito() {
        carrito.clear();
        if (listener != null) listener.accept(carrito);
    }

    public double calcularTotal() {
        return carrito.stream().mapToDouble(Producto::getPrecio).sum();
    }
}

// --- 3. UI PRINCIPAL ---
public class FarmacolApp extends JFrame {
    private final FarmacolViewModel viewModel = new FarmacolViewModel();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainContainer = new JPanel(cardLayout);

    private JLabel lblContadorCarrito;
    private JLabel lblTotalPago;
    private JButton btnPagarTienda;

    public FarmacolApp() {
        setTitle("FarmaLocal");
        setSize(450, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainContainer.add(crearPantallaBienvenida(), "WELCOME");
        mainContainer.add(crearPantallaLogin(), "LOGIN");
        mainContainer.add(crearPantallaTienda(), "TIENDA");
        mainContainer.add(crearPantallaPago(), "PAY");

        add(mainContainer);

        viewModel.setOnCarritoChanged(lista -> {
            actualizarUI(lista);
        });

        cardLayout.show(mainContainer, "WELCOME");
    }

    private void actualizarUI(List<Producto> lista) {
        int cantidad = lista.size();
        double total = viewModel.calcularTotal();

        if (lblContadorCarrito != null) {
            lblContadorCarrito.setText("🛒 Items: " + cantidad);
        }

        if (btnPagarTienda != null) {
            btnPagarTienda.setText("PAGAR: $" + String.format("%.2f", total));
            btnPagarTienda.setEnabled(cantidad > 0);
        }

        if (lblTotalPago != null) {
            lblTotalPago.setText("TOTAL: $" + String.format("%.2f", total));
        }
    }

    private JPanel crearPantallaBienvenida() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(crearHeaderSimple("WELCOME"), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(50, 20, 50, 20));

        JLabel logo = new JLabel("FL");
        logo.setOpaque(true);
        logo.setBackground(Color.BLACK);
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 30));
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setMaximumSize(new Dimension(80, 80));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnEntrar = crearBoton("EMPEZAR COMPRA", new Color(110, 73, 122));
        btnEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEntrar.addActionListener(e -> cardLayout.show(mainContainer, "LOGIN"));

        JLabel welcomeLabel = new JLabel("Bienvenido a FarmaLocal");
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(Box.createVerticalGlue());
        content.add(logo);
        content.add(Box.createVerticalStrut(20));
        content.add(welcomeLabel);
        content.add(Box.createVerticalStrut(40));
        content.add(btnEntrar);
        content.add(Box.createVerticalGlue());

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPantallaLogin() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(crearHeaderSimple("ACCESO"), BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(50, 40, 50, 40));
        form.setBackground(Color.WHITE);

        // Campo de Usuario
        JTextField txtUser = new JTextField();
        txtUser.setBorder(BorderFactory.createTitledBorder("Usuario"));
        txtUser.setMaximumSize(new Dimension(400, 50));

        // Campo de Contraseña
        JPasswordField txtPass = new JPasswordField();
        txtPass.setBorder(BorderFactory.createTitledBorder("Contraseña"));
        txtPass.setMaximumSize(new Dimension(400, 50));

        JButton btnLogin = crearBoton("ENTRAR", Color.BLACK);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> {
            // Aquí se podría validar si el usuario y la contraseña son correctos
            cardLayout.show(mainContainer, "TIENDA");
        });

        form.add(txtUser);
        form.add(Box.createVerticalStrut(15));
        form.add(txtPass);
        form.add(Box.createVerticalStrut(30));
        form.add(btnLogin);

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPantallaTienda() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(110, 73, 122));
        header.setPreferredSize(new Dimension(400, 80));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel title = new JLabel("FarmaLocal");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        lblContadorCarrito = new JLabel("🛒 Items: 0");
        lblContadorCarrito.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        header.add(lblContadorCarrito, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 10, 10));
        grid.setBackground(Color.WHITE);
        grid.setBorder(new EmptyBorder(15, 15, 15, 15));

        for (Producto p : viewModel.getListaProductos()) {
            grid.add(crearTarjetaProducto(p));
        }

        panel.add(new JScrollPane(grid), BorderLayout.CENTER);

        btnPagarTienda = crearBoton("PAGAR: $0.00", Color.BLACK);
        btnPagarTienda.setPreferredSize(new Dimension(400, 70));
        btnPagarTienda.setEnabled(false);
        btnPagarTienda.addActionListener(e -> cardLayout.show(mainContainer, "PAY"));
        panel.add(btnPagarTienda, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPantallaPago() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(crearHeaderSimple("RESUMEN"), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        lblTotalPago = new JLabel("TOTAL: $0.00");
        lblTotalPago.setFont(new Font("Arial", Font.BOLD, 22));
        lblTotalPago.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnFin = crearBoton("CONFIRMAR Y PAGAR", new Color(46, 125, 50));
        btnFin.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "¡Pago procesado correctamente!");
            viewModel.vaciarCarrito();
            cardLayout.show(mainContainer, "WELCOME");
        });

        content.add(new JLabel("Gracias por su confianza"), BorderLayout.NORTH);
        content.add(lblTotalPago, BorderLayout.CENTER);
        content.add(btnFin, BorderLayout.SOUTH);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearHeaderSimple(String texto) {
        JPanel h = new JPanel(new GridBagLayout());
        h.setBackground(new Color(110, 73, 122));
        h.setPreferredSize(new Dimension(400, 60));
        JLabel l = new JLabel(texto);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Arial", Font.BOLD, 16));
        h.add(l);
        return h;
    }

    private JButton crearBoton(String texto, Color bg) {
        JButton b = new JButton(texto);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        return b;
    }

    private JPanel crearTarjetaProducto(Producto p) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(248, 248, 248));
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        JLabel imgLabel = new JLabel(p.getEmojiFallback());
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imgLabel.setPreferredSize(new Dimension(100, 100));

        new Thread(() -> {
            try {
                ImageIcon icon = new ImageIcon(new URL(p.getImagenUrl()));
                Image scaled = icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                SwingUtilities.invokeLater(() -> {
                    imgLabel.setIcon(new ImageIcon(scaled));
                    imgLabel.setText("");
                });
            } catch (Exception ignored) {}
        }).start();

        JLabel name = new JLabel(p.getNombre());
        name.setFont(new Font("Arial", Font.BOLD, 14));
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel price = new JLabel("$" + String.format("%.2f", p.getPrecio()));
        price.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnAdd = crearBoton("ADD", Color.BLACK);
        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdd.addActionListener(e -> viewModel.agregarProducto(p));

        card.add(Box.createVerticalStrut(10));
        card.add(imgLabel);
        card.add(name);
        card.add(price);
        card.add(Box.createVerticalStrut(10));
        card.add(btnAdd);
        card.add(Box.createVerticalStrut(10));

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FarmacolApp().setVisible(true));
    }
}