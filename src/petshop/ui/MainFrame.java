package petshop.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Janela principal do sistema. Contem o menu lateral fixo e a area de
 * conteudo, que alterna entre as telas (Inicio, Clientes, Animais,
 * Agendamentos) atraves de um CardLayout, conforme o wireframe
 * definido na Etapa 2.
 */
public class MainFrame extends javax.swing.JFrame {

    private static final String CARD_INICIO = "inicio";
    private static final String CARD_CLIENTES = "clientes";
    private static final String CARD_ANIMAIS = "animais";
    private static final String CARD_AGENDAMENTOS = "agendamentos";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel painelConteudo = new JPanel(cardLayout);

    private PainelInicio painelInicio;
    private PainelClientes painelClientes;
    private PainelAnimais painelAnimais;
    private PainelAgendamentos painelAgendamentos;

    private JButton botaoInicioAtivo;

    public MainFrame() {
        setTitle("Petshop Amigo Fiel - Sistema de Gestao");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(1180, 720);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(criarMenuLateral(), BorderLayout.WEST);
        getContentPane().add(criarConteudo(), BorderLayout.CENTER);
    }

    private JPanel criarConteudo() {
        painelConteudo.setBackground(EstiloApp.COR_FUNDO);

        painelInicio = new PainelInicio(this);
        painelClientes = new PainelClientes();
        painelAnimais = new PainelAnimais();
        painelAgendamentos = new PainelAgendamentos();

        painelConteudo.add(painelInicio, CARD_INICIO);
        painelConteudo.add(painelClientes, CARD_CLIENTES);
        painelConteudo.add(painelAnimais, CARD_ANIMAIS);
        painelConteudo.add(painelAgendamentos, CARD_AGENDAMENTOS);

        return painelConteudo;
    }

    private JPanel criarMenuLateral() {
        JPanel menu = new JPanel();
        menu.setLayout(null);
        menu.setPreferredSize(new Dimension(210, 0));
        menu.setBackground(EstiloApp.COR_PRIMARIA);

        JLabel titulo = new JLabel("<html><center>Petshop<br>Amigo Fiel</center></html>", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(0, 25, 210, 55);
        menu.add(titulo);

        String[] itens = {"Inicio", "Clientes", "Animais", "Agendamentos", "Sair"};
        int y = 100;
        for (String item : itens) {
            JButton botao = criarBotaoMenu(item);
            botao.setBounds(10, y, 190, 42);
            menu.add(botao);
            if (item.equals("Inicio")) {
                botaoInicioAtivo = botao;
                marcarAtivo(botao);
            }
            y += 50;
        }

        return menu;
    }

    private JButton criarBotaoMenu(String texto) {
        JButton botao = new JButton(texto);
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));
        botao.setFont(new Font("SansSerif", Font.BOLD, 13));
        botao.setForeground(new Color(0xD9, 0xE1, 0xF2));
        botao.setBackground(EstiloApp.COR_PRIMARIA);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setContentAreaFilled(false);
        botao.setOpaque(true);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (botao.getBackground().equals(EstiloApp.COR_PRIMARIA)) {
                    botao.setBackground(EstiloApp.COR_PRIMARIA.darker());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!botao.getBackground().equals(EstiloApp.COR_SECUNDARIA)) {
                    botao.setBackground(EstiloApp.COR_PRIMARIA);
                }
            }
        });

        botao.addActionListener(e -> navegarPara(texto, botao));
        return botao;
    }

    private void navegarPara(String destino, JButton botaoClicado) {
        switch (destino) {
            case "Inicio":
                painelInicio.atualizarDados();
                cardLayout.show(painelConteudo, CARD_INICIO);
                marcarAtivo(botaoClicado);
                break;
            case "Clientes":
                painelClientes.atualizarDados();
                cardLayout.show(painelConteudo, CARD_CLIENTES);
                marcarAtivo(botaoClicado);
                break;
            case "Animais":
                painelAnimais.atualizarDados();
                cardLayout.show(painelConteudo, CARD_ANIMAIS);
                marcarAtivo(botaoClicado);
                break;
            case "Agendamentos":
                painelAgendamentos.atualizarDados();
                cardLayout.show(painelConteudo, CARD_AGENDAMENTOS);
                marcarAtivo(botaoClicado);
                break;
            case "Sair":
                int opcao = JOptionPane.showConfirmDialog(this,
                        "Deseja realmente sair do sistema?", "Confirmar saida",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (opcao == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
                break;
            default:
                break;
        }
    }

    private JButton botaoAtivo;

    private void marcarAtivo(JButton botao) {
        if (botaoAtivo != null) {
            botaoAtivo.setBackground(EstiloApp.COR_PRIMARIA);
            botaoAtivo.setForeground(new Color(0xD9, 0xE1, 0xF2));
        }
        botao.setBackground(EstiloApp.COR_SECUNDARIA);
        botao.setForeground(EstiloApp.COR_PRIMARIA);
        botaoAtivo = botao;
    }

    /**
     * Permite que outras telas (ex.: Painel Inicio) solicitem a navegacao
     * programaticamente, por exemplo ao clicar em um atalho do dashboard.
     */
    public void irParaClientes() {
        painelClientes.atualizarDados();
        cardLayout.show(painelConteudo, CARD_CLIENTES);
    }

    public void irParaAnimais() {
        painelAnimais.atualizarDados();
        cardLayout.show(painelConteudo, CARD_ANIMAIS);
    }

    public void irParaAgendamentos() {
        painelAgendamentos.atualizarDados();
        cardLayout.show(painelConteudo, CARD_AGENDAMENTOS);
    }
}
