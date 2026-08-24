package petshop.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import petshop.model.Agendamento;
import petshop.model.Animal;
import petshop.repositorio.RepositorioDados;

/**
 * Tela Inicial (Dashboard): mostra um resumo rapido do sistema e a
 * lista dos proximos agendamentos, conforme o wireframe da Etapa 2.
 *
 * A partir da Etapa 4, os numeros e a lista vem do banco de dados
 * MySQL. Se o banco estiver indisponivel, uma mensagem de erro e
 * exibida em vez de travar a tela.
 */
public class PainelInicio extends JPanel {

    private final RepositorioDados repositorio = RepositorioDados.getInstancia();
    private final MainFrame frame;

    private JLabel valorClientes;
    private JLabel valorAnimais;
    private JLabel valorAgendamentos;
    private DefaultTableModel modeloTabela;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    public PainelInicio(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(EstiloApp.COR_FUNDO);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        add(criarCabecalho(), BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBackground(EstiloApp.COR_FUNDO);
        centro.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        centro.add(criarCartoes());
        centro.add(javax.swing.Box.createVerticalStrut(20));
        centro.add(criarTabelaProximos());

        add(centro, BorderLayout.CENTER);

        atualizarDados();
    }

    private JPanel criarCabecalho() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(EstiloApp.COR_FUNDO);
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = ComponentesUI.tituloTela("Bem-vindo(a)!");
        JLabel subtitulo = ComponentesUI.subtitulo("Resumo geral do petshop");
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        painel.add(titulo);
        painel.add(javax.swing.Box.createVerticalStrut(4));
        painel.add(subtitulo);
        return painel;
    }

    private JPanel criarCartoes() {
        JPanel painel = new JPanel(new GridLayout(1, 3, 18, 0));
        painel.setBackground(EstiloApp.COR_FUNDO);
        painel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 110));

        JPanel cartaoClientes = criarCartao("Clientes cadastrados", EstiloApp.COR_PRIMARIA);
        valorClientes = (JLabel) cartaoClientes.getClientProperty("labelValor");
        JPanel cartaoAnimais = criarCartao("Animais cadastrados", EstiloApp.COR_SECUNDARIA);
        valorAnimais = (JLabel) cartaoAnimais.getClientProperty("labelValor");
        JPanel cartaoAgendamentos = criarCartao("Agendamentos ativos", EstiloApp.COR_ACENTO);
        valorAgendamentos = (JLabel) cartaoAgendamentos.getClientProperty("labelValor");

        painel.add(cartaoClientes);
        painel.add(cartaoAnimais);
        painel.add(cartaoAgendamentos);
        return painel;
    }

    private JPanel criarCartao(String rotulo, Color corFaixa) {
        JPanel cartao = new JPanel(new BorderLayout());
        cartao.setBackground(EstiloApp.COR_CARTAO);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstiloApp.COR_BORDA),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)));

        JPanel faixa = new JPanel();
        faixa.setBackground(corFaixa);
        faixa.setPreferredSize(new java.awt.Dimension(6, 10));
        cartao.add(faixa, BorderLayout.WEST);

        JPanel textoPainel = new JPanel();
        textoPainel.setLayout(new BoxLayout(textoPainel, BoxLayout.Y_AXIS));
        textoPainel.setBackground(EstiloApp.COR_CARTAO);
        textoPainel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        JLabel labelRotulo = new JLabel(rotulo);
        labelRotulo.setFont(EstiloApp.FONTE_LEGENDA);
        labelRotulo.setForeground(EstiloApp.COR_TEXTO_CLARO);

        JLabel labelValor = new JLabel("0");
        labelValor.setFont(new Font("SansSerif", Font.BOLD, 30));
        labelValor.setForeground(EstiloApp.COR_TEXTO);

        textoPainel.add(labelRotulo);
        textoPainel.add(labelValor);
        cartao.add(textoPainel, BorderLayout.CENTER);

        cartao.putClientProperty("labelValor", labelValor);
        return cartao;
    }

    private JPanel criarTabelaProximos() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(EstiloApp.COR_CARTAO);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstiloApp.COR_BORDA),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Proximos agendamentos");
        titulo.setFont(EstiloApp.FONTE_SUBTITULO);
        titulo.setForeground(EstiloApp.COR_TEXTO);
        painel.add(titulo, BorderLayout.NORTH);

        String[] colunas = {"Animal", "Servico", "Data/Hora", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabela = new JTable(modeloTabela);
        tabela.setRowHeight(28);
        tabela.setFont(EstiloApp.FONTE_CORPO);
        tabela.getTableHeader().setFont(EstiloApp.FONTE_CORPO_NEGRITO);
        tabela.setShowGrid(false);
        tabela.getColumnModel().getColumn(3).setCellRenderer(new RenderizadorStatus());

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        scroll.setPreferredSize(new java.awt.Dimension(10, 230));
        painel.add(scroll, BorderLayout.CENTER);

        return painel;
    }

    /** Recarrega os numeros dos cartoes e a lista de proximos agendamentos a partir do banco. */
    public final void atualizarDados() {
        if (valorClientes == null) {
            return; // ainda construindo a tela
        }
        try {
            int totalClientes = repositorio.getClientes().size();
            int totalAnimais = repositorio.getAnimais().size();
            List<Agendamento> agendamentos = repositorio.getAgendamentos();

            valorClientes.setText(String.valueOf(totalClientes));
            valorAnimais.setText(String.valueOf(totalAnimais));

            long ativos = agendamentos.stream()
                    .filter(a -> "Agendado".equals(a.getStatus()))
                    .count();
            valorAgendamentos.setText(String.valueOf(ativos));

            modeloTabela.setRowCount(0);
            for (Agendamento ag : agendamentos) {
                Animal animal = repositorio.buscarAnimalPorId(ag.getIdAnimal());
                String nomeAnimal = animal != null ? animal.getNome() : "(removido)";
                modeloTabela.addRow(new Object[]{
                    nomeAnimal, ag.getServico(), ag.getDataHora().format(FORMATO_DATA), ag.getStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Nao foi possivel carregar os dados do painel inicial.\n"
                    + "Verifique se o MySQL esta em execucao e se o banco \"db_petshop\" "
                    + "foi criado (veja o script em sql/schema_petshop.sql).\n\nDetalhe tecnico: " + ex.getMessage(),
                    "Erro de banco de dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Colore o texto da coluna Status conforme o valor (Agendado/Concluido/Cancelado). */
    private static class RenderizadorStatus extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setFont(EstiloApp.FONTE_CORPO_NEGRITO);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            String status = String.valueOf(value);
            switch (status) {
                case "Concluido":
                    label.setForeground(EstiloApp.COR_SUCESSO);
                    break;
                case "Cancelado":
                    label.setForeground(EstiloApp.COR_ERRO);
                    break;
                default:
                    label.setForeground(EstiloApp.COR_ACENTO);
            }
            return label;
        }
    }
}
