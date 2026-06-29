import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class TransportasiGUI extends JFrame {

    // --- Data ---
    private ManajemenData db;
    private TransportasiService service;
    private List<Kendaraan> daftarKendaraan = new ArrayList<>();
    private List<Voucher> daftarVoucher = new ArrayList<>();
    private List<Perjalanan> daftarPerjalanan = new ArrayList<>();
    private List<Pembayaran> daftarPembayaran = new ArrayList<>();
    private Map<Kendaraan, Driver> pemilikKendaraan = new HashMap<>();
    private Map<Perjalanan, String> statusPerjalanan = new HashMap<>();
    private int perjalananCounter = 1;

    // --- Warna minimal ---
    private static final Color C_BG = new Color(240, 240, 240);
    private static final Color C_PANEL = Color.WHITE;
    private static final Color C_ACCENT = new Color(0, 112, 192);

    // --- Font standar ---
    private static final Font FONT_TITLE = new Font("Dialog", Font.BOLD, 16);
    private static final Font FONT_BODY = new Font("Dialog", Font.PLAIN, 12);
    private static final Font FONT_MONO = new Font("Monospaced", Font.PLAIN, 12);

    // --- Referensi komponen ---
    private JTabbedPane tabs;

    // Dashboard
    private JLabel lblStatPenumpang, lblStatDriver, lblStatPerjalanan, lblStatTransaksi;
    private JTextArea logArea;

    // Tabel
    private DefaultTableModel modelPenumpang, modelDriver, modelKendaraan,
            modelPerjalanan, modelPembayaran, modelLeaderboard;

    // ComboBox lintas tab
    private JComboBox<String> cbPerjalananCbDriver, cbPerjalananCbPenumpang, cbPerjalananCbKendaraan;
    private JComboBox<String> cbPembayaranPerjalanan, cbPembayaranVoucher;
    private JComboBox<String> cbRatingDriver;
    private JComboBox<String> cbKendaraanDriver;

    // Field ID otomatis
    private JTextField fPenumpangId, fDriverId;

    // Tabel referensi (untuk hapus)
    private JTable tablePenumpang, tableDriver, tableKendaraan;

    public TransportasiGUI() {
        db = new ManajemenData();
        service = new TransportasiService(db);
        seedData();

        setTitle("Sistem Transportasi Online");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);

        buildUI();
        refreshAll();
        setVisible(true);
    }

    // =========================================================
    // SEED DATA
    // =========================================================
    private void seedData() {
        Driver D1 = new Driver("D001", "Malven", "malven@mail.com", "081234", "100-200-300");
        Driver D2 = new Driver("D002", "Kevin", "kevin@mail.com", "082345", "110-220-330");
        Driver D3 = new Driver("D003", "Satria", "satria@mail.com", "083456", "120-240-360");
        service.daftarDriver(D1);
        service.daftarDriver(D2);
        service.daftarDriver(D3);

        Penumpang P1 = new Penumpang("P001", "Jeremy", "jeremy@mail.com", "081111", "Jl. A No.1", 150000);
        Penumpang P2 = new Penumpang("P002", "Danny", "danny@mail.com", "082222", "Jl. B No.2", 200000);
        Penumpang P3 = new Penumpang("P003", "Elsa", "elsa@mail.com", "083333", "Jl. C No.3", 100000);
        service.daftarPenumpang(P1);
        service.daftarPenumpang(P2);
        service.daftarPenumpang(P3);

        tambahKendaraan(new Motor(D1, "H 3612 IR", "Yamaha NMax"), D1);
        tambahKendaraan(new Mobil(D2, "H 4102 PIC", "Honda Jazz", "Reguler"), D2);
        tambahKendaraan(new Mobil(D3, "H 2656 VIO", "Toyota Innova", "Deluxe"), D3);

        daftarVoucher.add(new Voucher("V001", 0.10));
        daftarVoucher.add(new Voucher("V002", 0.20));
        daftarVoucher.add(new Voucher("V003", 0.30));
    }

    // =========================================================
    // BUILD UI
    // =========================================================
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildHeader(), BorderLayout.NORTH);

        tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(FONT_BODY);

        tabs.addTab("Dashboard", buildDashboardTab());
        tabs.addTab("Penumpang", buildPenumpangTab());
        tabs.addTab("Driver", buildDriverTab());
        tabs.addTab("Kendaraan", buildKendaraanTab());
        tabs.addTab("Perjalanan", buildPerjalananTab());
        tabs.addTab("Pembayaran", buildPembayaranTab());
        tabs.addTab("Leaderboard", buildLeaderboardTab());

        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
    }

    // =========================================================
    // HEADER
    // =========================================================
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_ACCENT);
        p.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("Sistem Transportasi Online");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshAll());

        p.add(title, BorderLayout.WEST);
        p.add(btnRefresh, BorderLayout.EAST);
        return p;
    }

    // =========================================================
    // TAB: DASHBOARD
    // =========================================================
    private JPanel buildDashboardTab() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Stat cards
        JPanel stats = new JPanel(new GridLayout(1, 4, 8, 0));
        lblStatPenumpang = new JLabel("0", SwingConstants.CENTER);
        lblStatDriver = new JLabel("0", SwingConstants.CENTER);
        lblStatPerjalanan = new JLabel("0", SwingConstants.CENTER);
        lblStatTransaksi = new JLabel("0", SwingConstants.CENTER);
        stats.add(statCard(lblStatPenumpang, "Penumpang"));
        stats.add(statCard(lblStatDriver, "Driver"));
        stats.add(statCard(lblStatPerjalanan, "Perjalanan"));
        stats.add(statCard(lblStatTransaksi, "Transaksi"));
        p.add(stats, BorderLayout.NORTH);

        // Log
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(FONT_MONO);
        logArea.setText(buildActivityLog());
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Ringkasan Sistem"));
        p.add(scroll, BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton bP = new JButton("+ Perjalanan Baru");
        JButton bB = new JButton("+ Proses Pembayaran");
        JButton bS = new JButton("Simpan Data");
        bP.addActionListener(e -> tabs.setSelectedIndex(4));
        bB.addActionListener(e -> tabs.setSelectedIndex(5));
        bS.addActionListener(e -> simpanSemuaDataGUI());
        actions.add(bP);
        actions.add(bB);
        actions.add(bS);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    private JPanel statCard(JLabel valueLabel, String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(C_PANEL);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        valueLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        valueLabel.setForeground(C_ACCENT);
        JLabel lTitle = new JLabel(title, SwingConstants.CENTER);
        lTitle.setFont(new Font("Dialog", Font.PLAIN, 11));
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lTitle, BorderLayout.SOUTH);
        return card;
    }

    private String buildActivityLog() {
        StringBuilder sb = new StringBuilder();
        sb.append("STATUS SISTEM\\n");
        sb.append("-------------------------------------\\n");
        sb.append(String.format("  %-22s %d%n", "Driver terdaftar", service.getDaftarDriver().size()));
        sb.append(String.format("  %-22s %d%n", "Penumpang terdaftar", service.getDaftarPenumpang().size()));
        sb.append(String.format("  %-22s %d%n", "Kendaraan tersedia", daftarKendaraan.size()));
        sb.append(String.format("  %-22s %d%n", "Voucher tersedia", daftarVoucher.size()));
        sb.append(String.format("  %-22s %d%n", "Perjalanan tercatat", daftarPerjalanan.size()));
        sb.append(String.format("  %-22s %d%n", "Transaksi pembayaran", daftarPembayaran.size()));
        sb.append("\\nDRIVER\\n-------------------------------------\\n");
        for (Driver d : service.getDaftarDriver())
            sb.append(String.format("  %-14s  %s%n", d.getNama(), d.isTersedia() ? "* Tersedia" : "O Bertugas"));
        sb.append("\\nPENUMPANG\\n-------------------------------------\\n");
        for (Penumpang pp : service.getDaftarPenumpang())
            sb.append(String.format("  %-14s  Saldo: Rp %,d%n", pp.getNama(), pp.getUang()));
        if (!daftarPembayaran.isEmpty()) {
            sb.append("\\nTRANSAKSI TERAKHIR\\n-------------------------------------\\n");
            int start = Math.max(0, daftarPembayaran.size() - 3);
            for (int i = start; i < daftarPembayaran.size(); i++) {
                Pembayaran pay = daftarPembayaran.get(i);
                sb.append(String.format("  [%s]  %-12s  Rp %,d  (%s)%n",
                        pay.getKodeTransaksi(), pay.getCustomer().getNama(),
                        pay.getTarifFinal(), pay.getClass().getSimpleName()));
            }
        }
        return sb.toString();
    }

    // =========================================================
    // TAB: PENUMPANG
    // =========================================================
    private JPanel buildPenumpangTab() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = { "ID", "Nama", "Email", "No. HP", "Alamat", "Saldo (Rp)" };
        modelPenumpang = nonEditable(cols);
        tablePenumpang = new JTable(modelPenumpang);
        refreshPenumpangTable();

        JScrollPane scroll = new JScrollPane(tablePenumpang);
        scroll.setBorder(BorderFactory.createTitledBorder("Daftar Penumpang"));
        p.add(scroll, BorderLayout.CENTER);

        // Form tambah + hapus
        JPanel south = new JPanel(new GridLayout(1, 2, 8, 0));

        // Form tambah
        JPanel formAdd = new JPanel(new GridLayout(0, 2, 5, 5));
        formAdd.setBorder(BorderFactory.createTitledBorder("Tambah Penumpang"));

        fPenumpangId = new JTextField();
        fPenumpangId.setText("P" + String.format("%03d", service.getDaftarPenumpang().size() + 1));
        fPenumpangId.setEditable(false);
        JTextField fNama = new JTextField(), fEmail = new JTextField(), fHp = new JTextField(),
                fAlamat = new JTextField(), fUang = new JTextField();

        formAdd.add(new JLabel("ID:"));
        formAdd.add(fPenumpangId);
        formAdd.add(new JLabel("Nama:"));
        formAdd.add(fNama);
        formAdd.add(new JLabel("Email:"));
        formAdd.add(fEmail);
        formAdd.add(new JLabel("No. HP:"));
        formAdd.add(fHp);
        formAdd.add(new JLabel("Alamat:"));
        formAdd.add(fAlamat);
        formAdd.add(new JLabel("Saldo Rp:"));
        formAdd.add(fUang);

        JButton btnAdd = new JButton("+ Tambah");
        btnAdd.addActionListener(e -> {
            try {
                if (fNama.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int saldo = Integer.parseInt(fUang.getText().replaceAll("[^0-9]", ""));
                Penumpang np = new Penumpang(fPenumpangId.getText(), fNama.getText(),
                        fEmail.getText(), fHp.getText(), fAlamat.getText(), saldo);
                service.daftarPenumpang(np);
                refreshAll();
                clearFields(fNama, fEmail, fHp, fAlamat, fUang);
                JOptionPane.showMessageDialog(this, "Penumpang \"" + np.getNama() + "\" berhasil ditambahkan.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Saldo harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        formAdd.add(new JLabel());
        formAdd.add(btnAdd);
        south.add(formAdd);

        // Panel hapus
        JPanel formDel = new JPanel(new BorderLayout(5, 5));
        formDel.setBorder(BorderFactory.createTitledBorder("Hapus Penumpang"));
        JLabel hint = new JLabel("Pilih baris pada tabel, lalu klik Hapus.");
        hint.setFont(new Font("Dialog", Font.ITALIC, 11));
        JButton btnDel = new JButton("Hapus Penumpang Terpilih");
        btnDel.addActionListener(e -> {
            int sel = tablePenumpang.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(this, "Pilih penumpang dari tabel terlebih dahulu!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            Penumpang target = service.getDaftarPenumpang().get(sel);
            int ok = JOptionPane.showConfirmDialog(this,
                    "Hapus penumpang \"" + target.getNama() + "\"?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                db.hapusPenumpang(target);
                refreshAll();
                JOptionPane.showMessageDialog(this, "Penumpang dihapus.");
            }
        });
        formDel.add(hint, BorderLayout.NORTH);
        formDel.add(btnDel, BorderLayout.CENTER);
        south.add(formDel);

        p.add(south, BorderLayout.SOUTH);
        return p;
    }

    private void refreshPenumpangTable() {
        if (modelPenumpang == null)
            return;
        modelPenumpang.setRowCount(0);
        for (Penumpang pp : service.getDaftarPenumpang())
            modelPenumpang.addRow(new Object[] { pp.getId(), pp.getNama(), pp.getEmail(),
                    pp.getNoHp(), pp.getAlamat(), String.format("Rp %,d", pp.getUang()) });
    }

    // =========================================================
    // TAB: DRIVER
    // =========================================================
    private JPanel buildDriverTab() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = { "ID", "Nama", "Email", "No. HP", "No. SIM", "Rating", "Status" };
        modelDriver = nonEditable(cols);
        tableDriver = new JTable(modelDriver);
        refreshDriverTable();

        JScrollPane scroll = new JScrollPane(tableDriver);
        scroll.setBorder(BorderFactory.createTitledBorder("Daftar Driver"));
        p.add(scroll, BorderLayout.CENTER);

        JPanel south = new JPanel(new GridLayout(1, 3, 8, 0));

        // Form tambah driver
        JPanel formAdd = new JPanel(new GridLayout(0, 2, 5, 5));
        formAdd.setBorder(BorderFactory.createTitledBorder("Tambah Driver"));

        fDriverId = new JTextField();
        fDriverId.setText("D" + String.format("%03d", service.getDaftarDriver().size() + 1));
        fDriverId.setEditable(false);
        JTextField fNama = new JTextField(), fEmail = new JTextField(), fHp = new JTextField(), fSIM = new JTextField();

        formAdd.add(new JLabel("ID:"));
        formAdd.add(fDriverId);
        formAdd.add(new JLabel("Nama:"));
        formAdd.add(fNama);
        formAdd.add(new JLabel("Email:"));
        formAdd.add(fEmail);
        formAdd.add(new JLabel("No. HP:"));
        formAdd.add(fHp);
        formAdd.add(new JLabel("No. SIM:"));
        formAdd.add(fSIM);

        JButton btnAdd = new JButton("+ Tambah");
        btnAdd.addActionListener(e -> {
            if (fNama.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Driver nd = new Driver(fDriverId.getText(), fNama.getText(),
                    fEmail.getText(), fHp.getText(), fSIM.getText());
            service.daftarDriver(nd);
            refreshAll();
            clearFields(fNama, fEmail, fHp, fSIM);
            JOptionPane.showMessageDialog(this, "Driver \"" + nd.getNama() + "\" berhasil ditambahkan.");
        });
        formAdd.add(new JLabel());
        formAdd.add(btnAdd);
        south.add(formAdd);

        // Panel hapus driver
        JPanel formDel = new JPanel(new BorderLayout(5, 5));
        formDel.setBorder(BorderFactory.createTitledBorder("Hapus Driver"));
        JLabel hint = new JLabel("Pilih baris pada tabel, lalu klik Hapus.");
        hint.setFont(new Font("Dialog", Font.ITALIC, 11));
        JButton btnDel = new JButton("Hapus Driver Terpilih");
        btnDel.addActionListener(e -> {
            int sel = tableDriver.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(this, "Pilih driver dari tabel terlebih dahulu!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            Driver target = service.getDaftarDriver().get(sel);
            int ok = JOptionPane.showConfirmDialog(this,
                    "Hapus driver \"" + target.getNama() + "\"?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                service.getDaftarDriver().remove(target);
                refreshAll();
                JOptionPane.showMessageDialog(this, "Driver dihapus.");
            }
        });
        formDel.add(hint, BorderLayout.NORTH);
        formDel.add(btnDel, BorderLayout.CENTER);
        south.add(formDel);

        // Panel beri rating
        JPanel formRate = new JPanel(new GridLayout(0, 2, 5, 5));
        formRate.setBorder(BorderFactory.createTitledBorder("Beri Rating Driver"));

        cbRatingDriver = new JComboBox<>();
        refreshComboDriver(cbRatingDriver);

        JSlider slider = new JSlider(1, 5, 5);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        JLabel lblRating = new JLabel("* * * * *  (5)");
        slider.addChangeListener(ev -> {
            String stars = "*".repeat(slider.getValue()) + "o".repeat(5 - slider.getValue());
            lblRating.setText(stars + "  (" + slider.getValue() + ")");
        });

        formRate.add(new JLabel("Driver:"));
        formRate.add(cbRatingDriver);
        formRate.add(new JLabel("Rating:"));
        formRate.add(slider);
        formRate.add(new JLabel());
        formRate.add(lblRating);
        JButton btnRate = new JButton("Beri Rating");
        btnRate.addActionListener(e -> {
            int idx = cbRatingDriver.getSelectedIndex();
            if (idx < 0)
                return;
            Driver d = service.getDaftarDriver().get(idx);
            service.beriRating(d, slider.getValue());
            refreshAll();
            JOptionPane.showMessageDialog(this, "Rating " + slider.getValue() + " diberikan ke " + d.getNama());
        });
        formRate.add(new JLabel());
        formRate.add(btnRate);
        south.add(formRate);

        p.add(south, BorderLayout.SOUTH);
        return p;
    }

    private void refreshDriverTable() {
        if (modelDriver == null)
            return;
        modelDriver.setRowCount(0);
        for (Driver d : service.getDaftarDriver())
            modelDriver.addRow(new Object[] { d.getId(), d.getNama(), d.getEmail(), d.getNoHp(), d.getNoSIM(),
                    d.getRatingCount() > 0 ? String.format("%.2f *", d.getRating()) : "N/A",
                    d.isTersedia() ? "Tersedia" : "Bertugas" });
    }

    // =========================================================
    // TAB: KENDARAAN
    // =========================================================
    private JPanel buildKendaraanTab() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = { "Plat", "Jenis", "Model", "Tipe", "Kapasitas", "Tarif (Rp)", "Driver" };
        modelKendaraan = nonEditable(cols);
        tableKendaraan = new JTable(modelKendaraan);
        refreshKendaraanTable();

        JScrollPane scroll = new JScrollPane(tableKendaraan);
        scroll.setBorder(BorderFactory.createTitledBorder("Daftar Kendaraan"));
        p.add(scroll, BorderLayout.CENTER);

        JPanel south = new JPanel(new GridLayout(1, 2, 8, 0));

        // Form tambah kendaraan
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Tambah Kendaraan"));

        JTextField fPlat = new JTextField(), fModel = new JTextField();
        JComboBox<String> cbJenis = new JComboBox<>(new String[] { "Motor", "Mobil" });
        JComboBox<String> cbTipe = new JComboBox<>(new String[] { "Reguler", "Deluxe" });
        cbKendaraanDriver = new JComboBox<>();
        refreshComboDriver(cbKendaraanDriver);
        cbTipe.setEnabled(false);
        cbJenis.addActionListener(e -> cbTipe.setEnabled("Mobil".equals(cbJenis.getSelectedItem())));

        form.add(new JLabel("Plat Nomor:"));
        form.add(fPlat);
        form.add(new JLabel("Model:"));
        form.add(fModel);
        form.add(new JLabel("Jenis:"));
        form.add(cbJenis);
        form.add(new JLabel("Tipe Mobil:"));
        form.add(cbTipe);
        form.add(new JLabel("Driver:"));
        form.add(cbKendaraanDriver);

        JButton btnAdd = new JButton("+ Tambah Kendaraan");
        btnAdd.addActionListener(e -> {
            try {
                int dIdx = cbKendaraanDriver.getSelectedIndex();
                if (dIdx < 0) {
                    JOptionPane.showMessageDialog(this, "Pilih driver!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (fPlat.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Plat nomor harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Driver drv = service.getDaftarDriver().get(dIdx);
                Kendaraan k = "Motor".equals(cbJenis.getSelectedItem())
                        ? new Motor(drv, fPlat.getText(), fModel.getText())
                        : new Mobil(drv, fPlat.getText(), fModel.getText(), cbTipe.getSelectedItem().toString());
                tambahKendaraan(k, drv);
                refreshAll();
                clearFields(fPlat, fModel);
                JOptionPane.showMessageDialog(this, "Kendaraan berhasil ditambahkan.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        form.add(new JLabel());
        form.add(btnAdd);
        south.add(form);

        // Panel hapus kendaraan
        JPanel formDel = new JPanel(new BorderLayout(5, 5));
        formDel.setBorder(BorderFactory.createTitledBorder("Hapus Kendaraan"));
        JLabel hint = new JLabel("Pilih baris pada tabel, lalu klik Hapus.");
        hint.setFont(new Font("Dialog", Font.ITALIC, 11));
        JButton btnDel = new JButton("Hapus Kendaraan Terpilih");
        btnDel.addActionListener(e -> {
            int sel = tableKendaraan.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(this, "Pilih kendaraan dari tabel terlebih dahulu!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            Kendaraan target = daftarKendaraan.get(sel);
            int ok = JOptionPane.showConfirmDialog(this,
                    "Hapus kendaraan \"" + target.getModel() + " (" + target.getPlatNomor() + ")\"?",
                    "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                daftarKendaraan.remove(target);
                pemilikKendaraan.remove(target);
                refreshAll();
                JOptionPane.showMessageDialog(this, "Kendaraan dihapus.");
            }
        });
        formDel.add(hint, BorderLayout.NORTH);
        formDel.add(btnDel, BorderLayout.CENTER);
        south.add(formDel);

        p.add(south, BorderLayout.SOUTH);
        return p;
    }

    private void refreshKendaraanTable() {
        if (modelKendaraan == null)
            return;
        modelKendaraan.setRowCount(0);
        for (Kendaraan k : daftarKendaraan) {
            String tipe = (k instanceof Mobil) ? ((Mobil) k).getTipeMobil() : "-";
            Driver pemilik = pemilikKendaraan.get(k);
            String driverNama = pemilik != null ? pemilik.getNama() : "N/A";
            modelKendaraan.addRow(new Object[] { k.getPlatNomor(), k.getJenis(), k.getModel(), tipe,
                    k.getKapasitas(), String.format("Rp %,d", k.getTarifKendaraan()), driverNama });
        }
    }

    private void tambahKendaraan(Kendaraan kendaraan, Driver driver) {
        daftarKendaraan.add(kendaraan);
        pemilikKendaraan.put(kendaraan, driver);
    }

    // =========================================================
    // TAB: PERJALANAN
    // =========================================================
    private JPanel buildPerjalananTab() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = { "ID", "Penumpang", "Driver", "Kendaraan", "Jarak (km)", "Tarif", "Status" };
        modelPerjalanan = nonEditable(cols);
        JTable table = new JTable(modelPerjalanan);
        refreshPerjalananTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Daftar Perjalanan"));
        p.add(scroll, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0, 4, 8, 5));
        form.setBorder(BorderFactory.createTitledBorder("Pesan Perjalanan"));

        cbPerjalananCbPenumpang = new JComboBox<>();
        cbPerjalananCbDriver = new JComboBox<>();
        cbPerjalananCbKendaraan = new JComboBox<>();
        refreshComboPenumpang(cbPerjalananCbPenumpang);
        refreshComboDriver(cbPerjalananCbDriver);
        refreshComboKendaraan(cbPerjalananCbKendaraan);

        cbPerjalananCbDriver.addActionListener(e -> filterKendaraanByDriver());

        JTextField fJarak = new JTextField(8);

        JLabel lblEst = new JLabel("Estimasi tarif: -");
        lblEst.setForeground(C_ACCENT);

        Runnable updateEst = () -> {
            try {
                double jarak = Double.parseDouble(fJarak.getText());
                int ki = cbPerjalananCbKendaraan.getSelectedIndex();
                int di = cbPerjalananCbDriver.getSelectedIndex();
                List<Kendaraan> kendaraanDriver = getKendaraanByDriverIndex(di);
                if (ki >= 0 && ki < kendaraanDriver.size()) {
                    Kendaraan k = kendaraanDriver.get(ki);
                    int tarif = (int) ((jarak * Perjalanan.hargaPerkm + k.getTarifKendaraan()) * 1.1);
                    lblEst.setText(String.format("Estimasi tarif: Rp %,d", tarif));
                }
            } catch (NumberFormatException ex) {
                lblEst.setText("Estimasi tarif: -");
            }
        };
        fJarak.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateEst.run();
            }
        });
        cbPerjalananCbKendaraan.addActionListener(e -> updateEst.run());

        form.add(new JLabel("Penumpang:"));
        form.add(cbPerjalananCbPenumpang);
        form.add(new JLabel("Driver:"));
        form.add(cbPerjalananCbDriver);
        form.add(new JLabel("Kendaraan:"));
        form.add(cbPerjalananCbKendaraan);
        form.add(new JLabel("Jarak (km):"));
        form.add(fJarak);
        form.add(new JLabel());
        form.add(lblEst);

        JButton btnPesan = new JButton("Pesan Perjalanan");
        btnPesan.addActionListener(e -> {
            try {
                int pi = cbPerjalananCbPenumpang.getSelectedIndex();
                int di = cbPerjalananCbDriver.getSelectedIndex();
                int ki = cbPerjalananCbKendaraan.getSelectedIndex();
                if (pi < 0 || di < 0 || ki < 0) {
                    JOptionPane.showMessageDialog(this, "Pilih semua data!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Penumpang pp = service.getDaftarPenumpang().get(pi);
                Driver drv = service.getDaftarDriver().get(di);
                List<Kendaraan> kendaraanDriver = getKendaraanByDriverIndex(di);
                if (ki >= kendaraanDriver.size()) {
                    JOptionPane.showMessageDialog(this, "Kendaraan tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Kendaraan k = kendaraanDriver.get(ki);
                if (!drv.isTersedia()) {
                    JOptionPane.showMessageDialog(this, "Driver " + drv.getNama() + " tidak tersedia!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double jarak = Double.parseDouble(fJarak.getText());
                String id = "GO" + String.format("%03d", perjalananCounter++);
                Perjalanan go = service.pesanPerjalanan(id, pp, drv, jarak, k);
                if (go != null) {
                    daftarPerjalanan.add(go);
                    statusPerjalanan.put(go, "Aktif");
                    refreshAll();
                    fJarak.setText("");
                    lblEst.setText("Estimasi tarif: -");
                    JOptionPane.showMessageDialog(this, "Perjalanan " + id + " berhasil dipesan!\\nTarif: Rp "
                            + String.format("%,d", go.hitungTarif()));
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Jarak harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnSelesai = new JButton("Selesaikan Perjalanan");
        btnSelesai.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(this, "Pilih perjalanan yang akan diselesaikan!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            Perjalanan go = daftarPerjalanan.get(sel);
            if ("Dibayar".equals(statusPerjalanan.get(go))) {
                JOptionPane.showMessageDialog(this, "Perjalanan sudah dibayar.", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            go.selesai();
            statusPerjalanan.put(go, "Selesai");
            refreshAll();
            JOptionPane.showMessageDialog(this, "Perjalanan selesai!");
        });

        form.add(btnPesan);
        form.add(btnSelesai);
        p.add(form, BorderLayout.SOUTH);
        return p;
    }

    private List<Kendaraan> getKendaraanByDriverIndex(int driverIdx) {
        List<Kendaraan> result = new ArrayList<>();
        if (driverIdx < 0 || driverIdx >= service.getDaftarDriver().size())
            return result;
        Driver drv = service.getDaftarDriver().get(driverIdx);
        for (Kendaraan k : daftarKendaraan) {
            if (drv.equals(pemilikKendaraan.get(k)))
                result.add(k);
        }
        return result;
    }

    private void filterKendaraanByDriver() {
        if (cbPerjalananCbKendaraan == null)
            return;
        int di = cbPerjalananCbDriver.getSelectedIndex();
        cbPerjalananCbKendaraan.removeAllItems();
        List<Kendaraan> filtered = getKendaraanByDriverIndex(di);
        for (Kendaraan k : filtered)
            cbPerjalananCbKendaraan.addItem(k.getModel() + " (" + k.getPlatNomor() + ")");
    }

    private void refreshPerjalananTable() {
        if (modelPerjalanan == null)
            return;
        modelPerjalanan.setRowCount(0);
        for (Perjalanan go : daftarPerjalanan)
            modelPerjalanan.addRow(new Object[] { go.getIDPerjalanan(), go.penumpang.getNama(),
                    go.driver.getNama(), go.kendaraan.getModel(),
                    go.getJarak(), String.format("Rp %,d", go.hitungTarif()), getStatusPerjalanan(go) });
    }

    // =========================================================
    // TAB: PEMBAYARAN
    // =========================================================
    private JPanel buildPembayaranTab() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = { "Kode", "Pelanggan", "Metode", "Tarif Final", "Voucher", "Tanggal" };
        modelPembayaran = nonEditable(cols);
        JTable table = new JTable(modelPembayaran);
        refreshPembayaranTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Riwayat Pembayaran"));
        p.add(scroll, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0, 4, 8, 5));
        form.setBorder(BorderFactory.createTitledBorder("Proses Pembayaran"));

        cbPembayaranPerjalanan = new JComboBox<>();
        cbPembayaranVoucher = new JComboBox<>();
        refreshComboPerjalanan(cbPembayaranPerjalanan);
        refreshComboVoucher(cbPembayaranVoucher);

        JComboBox<String> cbMetode = new JComboBox<>(new String[] { "Cash", "Digital" });
        JTextField fProvider = new JTextField("GoPay");
        fProvider.setEnabled(false);
        JTextField fAdmin = new JTextField("1000");
        fAdmin.setEnabled(false);
        cbMetode.addActionListener(e -> {
            boolean dig = "Digital".equals(cbMetode.getSelectedItem());
            fProvider.setEnabled(dig);
            fAdmin.setEnabled(dig);
        });

        JLabel lblEst = new JLabel("Tarif final: -");
        lblEst.setForeground(C_ACCENT);
        JLabel lblSaldo = new JLabel("Saldo penumpang: -");

        Runnable updateEst = () -> {
            int pi = cbPembayaranPerjalanan.getSelectedIndex();
            int vi = cbPembayaranVoucher.getSelectedIndex();
            List<Perjalanan> perjalananBelumDibayar = getPerjalananBelumDibayar();
            List<Voucher> voucherBelumDipakai = getVoucherBelumDipakai();
            if (pi < 0 || vi < 0 || pi >= perjalananBelumDibayar.size() || vi >= voucherBelumDipakai.size()) {
                lblEst.setText("Tarif final: -");
                lblSaldo.setText("Saldo penumpang: -");
                return;
            }
            Perjalanan go = perjalananBelumDibayar.get(pi);
            Voucher vou = voucherBelumDipakai.get(vi);
            int tarif = go.hitungTarif();
            if ("Digital".equals(cbMetode.getSelectedItem())) {
                try {
                    tarif += Integer.parseInt(fAdmin.getText().replaceAll("[^0-9]", ""));
                } catch (Exception ex) {
                }
            }
            if (!vou.getSudahDipakai())
                tarif -= (int) (tarif * vou.getDiskon());
            int saldo = go.penumpang.getUang();
            lblEst.setText(String.format("Tarif final: Rp %,d", tarif));
            lblEst.setForeground(saldo >= tarif ? new Color(0, 128, 0) : Color.RED);
            lblSaldo.setText(String.format("Saldo: Rp %,d  %s", saldo, saldo >= tarif ? "Cukup" : "Tidak cukup"));
            lblSaldo.setForeground(saldo >= tarif ? new Color(0, 128, 0) : Color.RED);
        };
        cbPembayaranPerjalanan.addActionListener(e -> updateEst.run());
        cbPembayaranVoucher.addActionListener(e -> updateEst.run());
        cbMetode.addActionListener(e -> updateEst.run());

        form.add(new JLabel("Perjalanan:"));
        form.add(cbPembayaranPerjalanan);
        form.add(new JLabel("Voucher:"));
        form.add(cbPembayaranVoucher);
        form.add(new JLabel("Metode:"));
        form.add(cbMetode);
        form.add(new JLabel("Provider:"));
        form.add(fProvider);
        form.add(new JLabel("Biaya Admin:"));
        form.add(fAdmin);
        form.add(lblEst);
        form.add(lblSaldo);

        JButton btnBayar = new JButton("Proses Pembayaran");
        btnBayar.addActionListener(e -> {
            int pi = cbPembayaranPerjalanan.getSelectedIndex();
            int vi = cbPembayaranVoucher.getSelectedIndex();
            if (pi < 0 || vi < 0) {
                JOptionPane.showMessageDialog(this, "Pilih perjalanan dan voucher!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Perjalanan> perjalananBelumDibayar = getPerjalananBelumDibayar();
            List<Voucher> voucherBelumDipakai = getVoucherBelumDipakai();
            if (pi >= perjalananBelumDibayar.size() || vi >= voucherBelumDipakai.size()) {
                JOptionPane.showMessageDialog(this, "Data tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Perjalanan go = perjalananBelumDibayar.get(pi);
            Voucher vou = voucherBelumDipakai.get(vi);
            Penumpang pen = go.penumpang;
            Kendaraan k = go.kendaraan;

            Pembayaran pay;
            if ("Cash".equals(cbMetode.getSelectedItem())) {
                pay = service.prosesPembayaranCash(pen, go, vou, k);
            } else {
                double admin = 0;
                try {
                    admin = Double.parseDouble(fAdmin.getText().replaceAll("[^0-9.]", ""));
                } catch (Exception ignored) {
                }
                pay = service.prosesPembayaranDigital(pen, go, vou, k, admin, fProvider.getText());
            }

            // CHECK FEEDBACK DARI CONTROLLER
            if (pay == null) {
                JOptionPane.showMessageDialog(this, "Transaksi Gagal! Saldo penumpang tidak mencukupi.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // GUI HANYA UPDATE STATUS TAMPILAN VISUAL LOKAL
            statusPerjalanan.put(go, "Dibayar");
            daftarPembayaran.add(pay);

            refreshAll();
            showReceipt(buildReceipt(pay, go, vou));
        });

        form.add(new JLabel());
        form.add(btnBayar);
        p.add(form, BorderLayout.SOUTH);
        return p;
    }

    private String buildReceipt(Pembayaran pay, Perjalanan go, Voucher vou) {
        StringBuilder sb = new StringBuilder();
        sb.append("==============================\\n");
        sb.append("       STRUK PEMBAYARAN       \\n");
        sb.append("==============================\\n");
        sb.append(String.format("  Kode Transaksi : %s%n", pay.getKodeTransaksi()));
        sb.append(String.format("  Penumpang      : %s%n", pay.getCustomer().getNama()));
        sb.append(String.format("  Metode         : %s%n", pay.getClass().getSimpleName()));
        sb.append(String.format("  Jarak          : %.1f km%n", go.getJarak()));
        sb.append(String.format("  Voucher        : %s (%d%% off)%n",
                vou.getKodeVoucher(), (int) (vou.getDiskon() * 100)));
        sb.append("------------------------------\\n");
        sb.append(String.format("  Tarif Final    : Rp %,d%n", pay.getTarifFinal()));
        sb.append(String.format("  Saldo Tersisa  : Rp %,d%n", pay.getCustomer().getUang()));
        sb.append(String.format("  Tanggal        : %s%n", LocalDate.now()));
        if (pay instanceof Digital) {
            sb.append(String.format("  Provider       : %s%n", ((Digital) pay).getProvider()));
            sb.append(String.format("  Biaya Admin    : Rp %,d%n", (int) ((Digital) pay).getBiayaAdmin()));
        }
        sb.append("==============================\\n");
        return sb.toString();
    }

    private void refreshPembayaranTable() {
        if (modelPembayaran == null)
            return;
        modelPembayaran.setRowCount(0);
        for (Pembayaran pay : daftarPembayaran)
            modelPembayaran.addRow(new Object[] { pay.getKodeTransaksi(), pay.getCustomer().getNama(),
                    pay.getClass().getSimpleName(), String.format("Rp %,d", pay.getTarifFinal()),
                    pay.getVoucher().getKodeVoucher(), pay.getTanggalTransaksi() });
    }

    private String getStatusPerjalanan(Perjalanan perjalanan) {
        return statusPerjalanan.getOrDefault(perjalanan, "Aktif");
    }

    private List<Perjalanan> getPerjalananBelumDibayar() {
        List<Perjalanan> result = new ArrayList<>();
        for (Perjalanan go : daftarPerjalanan) {
            if (!"Dibayar".equals(getStatusPerjalanan(go)))
                result.add(go);
        }
        return result;
    }

    private List<Voucher> getVoucherBelumDipakai() {
        List<Voucher> result = new ArrayList<>();
        for (Voucher v : daftarVoucher) {
            if (!v.getSudahDipakai())
                result.add(v);
        }
        return result;
    }

    private void simpanSemuaDataGUI() {
        service.simpanSemuaData();
        try {
            simpanKendaraan();
            simpanPerjalanan();
            simpanPembayaran();
            JOptionPane.showMessageDialog(this,
                    "Data berhasil disimpan!\n"
                            + "- data_driver.txt\n"
                            + "- data_penumpang.txt\n"
                            + "- data_kendaraan.txt\n"
                            + "- data_perjalanan.txt\n"
                            + "- data_pembayaran.txt");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void simpanKendaraan() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data_kendaraan.txt", false))) {
            bw.write("# FORMAT: plat|jenis|model|tipe|kapasitas|tarif|idDriver|namaDriver");
            bw.newLine();
            for (Kendaraan k : daftarKendaraan) {
                Driver d = pemilikKendaraan.get(k);
                String tipe = (k instanceof Mobil) ? ((Mobil) k).getTipeMobil() : "-";
                bw.write(k.getPlatNomor() + "|" + k.getJenis() + "|" + k.getModel() + "|" + tipe + "|"
                        + k.getKapasitas() + "|" + k.getTarifKendaraan() + "|"
                        + (d != null ? d.getId() : "-") + "|" + (d != null ? d.getNama() : "-"));
                bw.newLine();
            }
        }
    }

    private void simpanPerjalanan() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data_perjalanan.txt", false))) {
            bw.write("# FORMAT: id|penumpang|driver|platKendaraan|modelKendaraan|jarak|tarif|status");
            bw.newLine();
            for (Perjalanan go : daftarPerjalanan) {
                bw.write(go.getIDPerjalanan() + "|" + go.penumpang.getNama() + "|" + go.driver.getNama() + "|"
                        + go.kendaraan.getPlatNomor() + "|" + go.kendaraan.getModel() + "|"
                        + go.getJarak() + "|" + go.hitungTarif() + "|" + getStatusPerjalanan(go));
                bw.newLine();
            }
        }
    }

    private void simpanPembayaran() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data_pembayaran.txt", false))) {
            bw.write("# FORMAT: kode|pelanggan|metode|tarifFinal|voucher|tanggal");
            bw.newLine();
            for (Pembayaran pay : daftarPembayaran) {
                bw.write(pay.getKodeTransaksi() + "|" + pay.getCustomer().getNama() + "|"
                        + pay.getClass().getSimpleName() + "|" + pay.getTarifFinal() + "|"
                        + pay.getVoucher().getKodeVoucher() + "|" + pay.getTanggalTransaksi());
                bw.newLine();
            }
        }
    }

    // =========================================================
    // TAB: LEADERBOARD
    // =========================================================
    private JPanel buildLeaderboardTab() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = { "Rank", "Nama Driver", "Rating", "Jumlah Rating", "Status" };
        modelLeaderboard = nonEditable(cols);
        JTable table = new JTable(modelLeaderboard);
        refreshLeaderboard();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Leaderboard Rating Driver"));
        p.add(scroll, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btn = new JButton("Refresh Leaderboard");
        btn.addActionListener(e -> refreshLeaderboard());
        south.add(btn);
        p.add(south, BorderLayout.SOUTH);
        return p;
    }

    private void refreshLeaderboard() {
        if (modelLeaderboard == null)
            return;
        modelLeaderboard.setRowCount(0);
        List<Driver> sorted = new ArrayList<>(service.getDaftarDriver());
        sorted.sort((a, b) -> Double.compare(b.getRating(), a.getRating()));
        String[] medals = { "1st #1", "2nd #2", "3rd #3" };
        for (int i = 0; i < sorted.size(); i++) {
            Driver d = sorted.get(i);
            String rank = i < medals.length ? medals[i] : "    #" + (i + 1);
            modelLeaderboard.addRow(new Object[] { rank, d.getNama(),
                    d.getRatingCount() > 0 ? String.format("%.2f *", d.getRating()) : "N/A",
                    d.getRatingCount(), d.isTersedia() ? "Tersedia" : "Bertugas" });
        }
    }

    // =========================================================
    // REFRESH ALL
    // =========================================================
    private void refreshAll() {
        refreshPenumpangTable();
        refreshDriverTable();
        refreshKendaraanTable();
        refreshPerjalananTable();
        refreshPembayaranTable();
        refreshLeaderboard();

        if (lblStatPenumpang != null)
            lblStatPenumpang.setText(String.valueOf(service.getDaftarPenumpang().size()));
        if (lblStatDriver != null)
            lblStatDriver.setText(String.valueOf(service.getDaftarDriver().size()));
        if (lblStatPerjalanan != null)
            lblStatPerjalanan.setText(String.valueOf(daftarPerjalanan.size()));
        if (lblStatTransaksi != null)
            lblStatTransaksi.setText(String.valueOf(daftarPembayaran.size()));
        if (logArea != null)
            logArea.setText(buildActivityLog());

        if (fPenumpangId != null)
            fPenumpangId.setText("P" + String.format("%03d", service.getDaftarPenumpang().size() + 1));
        if (fDriverId != null)
            fDriverId.setText("D" + String.format("%03d", service.getDaftarDriver().size() + 1));

        refreshComboDriver(cbRatingDriver);
        refreshComboDriver(cbKendaraanDriver);
        refreshComboDriver(cbPerjalananCbDriver);
        refreshComboPenumpang(cbPerjalananCbPenumpang);
        filterKendaraanByDriver();
        refreshComboPerjalanan(cbPembayaranPerjalanan);
        refreshComboVoucher(cbPembayaranVoucher);
    }

    // =========================================================
    // COMBO HELPERS
    // =========================================================
    private void refreshComboDriver(JComboBox<String> cb) {
        if (cb == null)
            return;
        cb.removeAllItems();
        for (Driver d : service.getDaftarDriver())
            cb.addItem(d.getNama());
    }

    private void refreshComboPenumpang(JComboBox<String> cb) {
        if (cb == null)
            return;
        cb.removeAllItems();
        for (Penumpang pp : service.getDaftarPenumpang())
            cb.addItem(pp.getNama() + " (Rp " + String.format("%,d", pp.getUang()) + ")");
    }

    private void refreshComboKendaraan(JComboBox<String> cb) {
        if (cb == null)
            return;
        cb.removeAllItems();
        for (Kendaraan k : daftarKendaraan)
            cb.addItem(k.getModel() + " (" + k.getPlatNomor() + ")");
    }

    private void refreshComboPerjalanan(JComboBox<String> cb) {
        if (cb == null)
            return;
        cb.removeAllItems();
        for (Perjalanan go : getPerjalananBelumDibayar())
            cb.addItem(go.getIDPerjalanan() + " . " + go.penumpang.getNama() + " . " + go.getJarak()
                    + " km . " + getStatusPerjalanan(go));
    }

    private void refreshComboVoucher(JComboBox<String> cb) {
        if (cb == null)
            return;
        cb.removeAllItems();
        for (Voucher v : getVoucherBelumDipakai())
            cb.addItem(v.getKodeVoucher() + " - " + (int) (v.getDiskon() * 100) + "% off"
                    + (v.getSudahDipakai() ? " [Terpakai]" : ""));
    }

    // =========================================================
    // UI FACTORY HELPERS - sederhana
    // =========================================================

    private DefaultTableModel nonEditable(String[] cols) {
        return new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    private void clearFields(JTextField... fields) {
        for (JTextField f : fields)
            f.setText("");
    }

    private void showReceipt(String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setFont(FONT_MONO);
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, sp, "Struk Pembayaran", JOptionPane.PLAIN_MESSAGE);
    }

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TransportasiGUI::new);
    }
}
