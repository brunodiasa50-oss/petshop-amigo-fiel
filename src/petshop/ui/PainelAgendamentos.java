package petshop.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import petshop.model.Agendamento;
import petshop.model.Animal;
import petshop.repositorio.RepositorioDados;

/**
 * Tela de Agendamentos: permite listar, cadastrar, editar e excluir
 * agendamentos de servicos, sempre vinculados a um animal ja
 * cadastrado, conforme o wireframe da Etapa 2.
 *
 * A partir da Etapa 4, os dados sao persistidos no banco de dados
 * MySQL. Chamadas ao repositorio sao protegidas com try/catch para
 * exibir mensagens de erro compreensiveis em caso de falha de banco.
 */
public class PainelAgendamentos extends JPanel {

    private final RepositorioDados repositorio = RepositorioDados.getInstancia();
    private static final String[] SERVICOS = {"Banho", "Tosa", "Banho e Tosa", "Consulta", "Vacina"};
    private static final String[] STATUS = {"Agendado", "Concluido", "Cancelado"};
    private static final DateTimeFormatter FORMATO_TABELA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private DefaultTableModel modeloTabela;
    private JTable tabela;

    private JComboBox<Animal> comboAnimal;
    private JComboBox<String> comboServico;
    private JTextField campoData;
    private JTextField campoHora;
    private JComboBox<String> comboStatus;
    private JLabel tituloFormulario;

    private List<Agendamento> agendamentosCarregados;
    private Agendamento agendamentoEmEdicao;

    public PainelAgendamentos() {
        setLayout(new BorderLayout());
        setBackground(EstiloApp.COR_FUNDO);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        add(criarCabecalho(), BorderLayout.NORTH);

        JPanel meio = new JPanel(new BorderLayout(20, 0));
        meio.setBackground(EstiloApp.COR_FUNDO);
        meio.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        meio.add(criarPainelLista(), BorderLayout.CENTER);
        meio.add(criarPainelFormulario(), BorderLayout.EAST);

        add(meio, BorderLayout.CENTER);

        atualizarDados();
    }

    private JPanel criarCabecalho() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(EstiloApp.COR_FUNDO);
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        painel.add(ComponentesUI.tituloTela("Agendamentos"));
        painel.add(Box.createVerticalStrut(4));
        painel.add(ComponentesUI.subtitulo("Servicos agendados para os animais cadastrados"));
        return painel;
    }

    private JPanel criarPainelLista() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(EstiloApp.COR_FUNDO);

        String[] colunas = {"ID", "Animal", "Servico", "Data/Hora", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(26);
        tabela.setFont(EstiloApp.FONTE_CORPO);
        tabela.getTableHeader().setFont(EstiloApp.FONTE_CORPO_NEGRITO);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getColumnModel().getColumn(4).setCellRenderer(new RenderizadorStatus());
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarSelecaoNoFormulario();
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(EstiloApp.COR_BORDA));
        scroll.setPreferredSize(new Dimension(10, 440));
        painel.add(scroll, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));
        rodape.setBackground(EstiloApp.COR_FUNDO);
        var botaoExcluir = ComponentesUI.botaoLink("Excluir selecionado", EstiloApp.COR_ERRO);
        botaoExcluir.addActionListener(this::excluirSelecionado);
        rodape.add(botaoExcluir);
        painel.add(rodape, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(EstiloApp.COR_CARTAO);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstiloApp.COR_PRIMARIA, 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        painel.setPreferredSize(new Dimension(330, 10));

        tituloFormulario = new JLabel("Novo Agendamento");
        tituloFormulario.setFont(EstiloApp.FONTE_SUBTITULO);
        tituloFormulario.setForeground(EstiloApp.COR_PRIMARIA);
        tituloFormulario.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(tituloFormulario);
        painel.add(Box.createVerticalStrut(16));

        painel.add(ComponentesUI.rotuloObrigatorio("Animal"));
        comboAnimal = ComponentesUI.caixaSelecao();
        comboAnimal.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Animal) {
                    setText(((Animal) value).getNome());
                }
                return this;
            }
        });
        painel.add(comboAnimal);
        painel.add(Box.createVerticalStrut(12));

        painel.add(ComponentesUI.rotuloObrigatorio("Servico"));
        comboServico = ComponentesUI.caixaSelecao();
        for (String s : SERVICOS) {
            comboServico.addItem(s);
        }
        painel.add(comboServico);
        painel.add(Box.createVerticalStrut(12));

        JPanel linhaDataHora = new JPanel(new java.awt.GridLayout(1, 2, 10, 0));
        linhaDataHora.setBackground(EstiloApp.COR_CARTAO);
        linhaDataHora.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaDataHora.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JPanel colData = new JPanel();
        colData.setLayout(new BoxLayout(colData, BoxLayout.Y_AXIS));
        colData.setBackground(EstiloApp.COR_CARTAO);
        colData.add(ComponentesUI.rotuloObrigatorio("Data"));
        campoData = ComponentesUI.campoTexto();
        campoData.setToolTipText("dd/mm/aaaa");
        colData.add(campoData);

        JPanel colHora = new JPanel();
        colHora.setLayout(new BoxLayout(colHora, BoxLayout.Y_AXIS));
        colHora.setBackground(EstiloApp.COR_CARTAO);
        colHora.add(ComponentesUI.rotuloObrigatorio("Hora"));
        campoHora = ComponentesUI.campoTexto();
        campoHora.setToolTipText("hh:mm");
        colHora.add(campoHora);

        linhaDataHora.add(colData);
        linhaDataHora.add(colHora);
        painel.add(linhaDataHora);
        painel.add(Box.createVerticalStrut(12));

        painel.add(ComponentesUI.rotulo("Status"));
        comboStatus = ComponentesUI.caixaSelecao();
        for (String s : STATUS) {
            comboStatus.addItem(s);
        }
        painel.add(comboStatus);
        painel.add(Box.createVerticalStrut(10));

        JLabel legenda = new JLabel("* Campos obrigatorios (data no formato dd/mm/aaaa, hora hh:mm)");
        legenda.setFont(EstiloApp.FONTE_LEGENDA);
        legenda.setForeground(EstiloApp.COR_TEXTO_CLARO);
        legenda.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(legenda);
        painel.add(Box.createVerticalStrut(18));

        JPanel botoes = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        botoes.setBackground(EstiloApp.COR_CARTAO);
        botoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        var botaoSalvar = ComponentesUI.botaoPrimario("Salvar");
        botaoSalvar.addActionListener(this::salvar);
        var botaoNovo = ComponentesUI.botaoSecundario("Novo / Limpar");
        botaoNovo.addActionListener(e -> limparFormulario());
        botoes.add(botaoSalvar);
        botoes.add(botaoNovo);
        painel.add(botoes);

        painel.add(Box.createVerticalGlue());
        return painel;
    }

    private void carregarAnimaisNoCombo() throws SQLException {
        Object selecionadoAntes = comboAnimal.getSelectedItem();
        comboAnimal.removeAllItems();
        for (Animal a : repositorio.getAnimais()) {
            comboAnimal.addItem(a);
        }
        if (selecionadoAntes != null) {
            comboAnimal.setSelectedItem(selecionadoAntes);
        }
    }

    private void adicionarLinha(Agendamento ag) {
        String nomeAnimal = "(desconhecido)";
        for (int i = 0; i < comboAnimal.getItemCount(); i++) {
            Animal a = comboAnimal.getItemAt(i);
            if (a.getIdAnimal() == ag.getIdAnimal()) {
                nomeAnimal = a.getNome();
                break;
            }
        }
        modeloTabela.addRow(new Object[]{
            ag.getIdAgendamento(), nomeAnimal, ag.getServico(),
            ag.getDataHora().format(FORMATO_TABELA), ag.getStatus()
        });
    }

    private void carregarSelecaoNoFormulario() {
        int linha = tabela.getSelectedRow();
        if (linha < 0 || agendamentosCarregados == null) {
            return;
        }
        int id = (int) modeloTabela.getValueAt(linha, 0);
        for (Agendamento ag : agendamentosCarregados) {
            if (ag.getIdAgendamento() == id) {
                agendamentoEmEdicao = ag;
                for (int i = 0; i < comboAnimal.getItemCount(); i++) {
                    Animal a = comboAnimal.getItemAt(i);
                    if (a.getIdAnimal() == ag.getIdAnimal()) {
                        comboAnimal.setSelectedItem(a);
                        break;
                    }
                }
                comboServico.setSelectedItem(ag.getServico());
                campoData.setText(ag.getDataHora().format(FORMATO_DATA));
                campoHora.setText(ag.getDataHora().format(FORMATO_HORA));
                comboStatus.setSelectedItem(ag.getStatus());
                tituloFormulario.setText("Editar Agendamento");
                break;
            }
        }
    }

    private void limparFormulario() {
        agendamentoEmEdicao = null;
        if (comboAnimal.getItemCount() > 0) {
            comboAnimal.setSelectedIndex(0);
        }
        comboServico.setSelectedIndex(0);
        campoData.setText("");
        campoHora.setText("");
        comboStatus.setSelectedIndex(0);
        tituloFormulario.setText("Novo Agendamento");
        tabela.clearSelection();
    }

    private void salvar(ActionEvent e) {
        Animal animal = (Animal) comboAnimal.getSelectedItem();
        String servico = (String) comboServico.getSelectedItem();
        String status = (String) comboStatus.getSelectedItem();
        String textoData = campoData.getText().trim();
        String textoHora = campoHora.getText().trim();

        if (animal == null || textoData.isEmpty() || textoHora.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Preencha os campos obrigatorios: Animal, Data e Hora.\n"
                    + "Cadastre um animal antes de criar um agendamento.",
                    "Campos obrigatorios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDateTime dataHora;
        try {
            LocalDate data = LocalDate.parse(textoData, FORMATO_DATA);
            LocalTime hora = LocalTime.parse(textoHora, FORMATO_HORA);
            dataHora = LocalDateTime.of(data, hora);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Data ou hora invalida. Use o formato Data: dd/mm/aaaa e Hora: hh:mm.",
                    "Formato invalido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (agendamentoEmEdicao == null) {
                repositorio.salvarAgendamento(new Agendamento(0, animal.getIdAnimal(), servico, dataHora, status));
                JOptionPane.showMessageDialog(this, "Agendamento cadastrado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                agendamentoEmEdicao.setIdAnimal(animal.getIdAnimal());
                agendamentoEmEdicao.setServico(servico);
                agendamentoEmEdicao.setDataHora(dataHora);
                agendamentoEmEdicao.setStatus(status);
                repositorio.salvarAgendamento(agendamentoEmEdicao);
                JOptionPane.showMessageDialog(this, "Agendamento atualizado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            limparFormulario();
            atualizarDados();
        } catch (SQLException ex) {
            mostrarErroBanco("Nao foi possivel salvar o agendamento.", ex);
        }
    }

    private void excluirSelecionado(ActionEvent e) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um agendamento na lista para excluir.",
                    "Nenhum agendamento selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabela.getValueAt(linha, 0);
        Agendamento agendamento = null;
        if (agendamentosCarregados != null) {
            for (Agendamento ag : agendamentosCarregados) {
                if (ag.getIdAgendamento() == id) {
                    agendamento = ag;
                    break;
                }
            }
        }
        if (agendamento == null) {
            return;
        }

        int opcao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir este agendamento?",
                "Confirmar exclusao", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (opcao == JOptionPane.YES_OPTION) {
            try {
                repositorio.excluirAgendamento(agendamento);
                limparFormulario();
                atualizarDados();
            } catch (SQLException ex) {
                mostrarErroBanco("Nao foi possivel excluir o agendamento.", ex);
            }
        }
    }

    /** Recarrega a tabela de agendamentos e a lista de animais disponiveis a partir do banco. */
    public final void atualizarDados() {
        if (modeloTabela == null) {
            return;
        }
        try {
            carregarAnimaisNoCombo();
            agendamentosCarregados = repositorio.getAgendamentos();
            modeloTabela.setRowCount(0);
            for (Agendamento ag : agendamentosCarregados) {
                adicionarLinha(ag);
            }
        } catch (SQLException ex) {
            mostrarErroBanco("Nao foi possivel carregar a lista de agendamentos.", ex);
        }
    }

    private void mostrarErroBanco(String mensagem, SQLException ex) {
        JOptionPane.showMessageDialog(this,
                mensagem + "\nVerifique se o MySQL esta em execucao e se o banco \"db_petshop\" "
                + "foi criado (veja o script em sql/schema_petshop.sql).\n\nDetalhe tecnico: " + ex.getMessage(),
                "Erro de banco de dados", JOptionPane.ERROR_MESSAGE);
    }

    /** Colore o texto da coluna Status conforme o valor. */
    private static class RenderizadorStatus extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setFont(EstiloApp.FONTE_CORPO_NEGRITO);
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
