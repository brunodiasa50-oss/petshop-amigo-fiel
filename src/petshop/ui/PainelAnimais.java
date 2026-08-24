package petshop.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
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
import javax.swing.table.DefaultTableModel;
import petshop.model.Animal;
import petshop.model.Cliente;
import petshop.repositorio.RepositorioDados;

/**
 * Tela de Animais: permite listar, buscar, cadastrar, editar e
 * excluir animais, sempre vinculados a um cliente ja cadastrado,
 * conforme o wireframe da Etapa 2.
 *
 * A partir da Etapa 4, os dados sao persistidos no banco de dados
 * MySQL. Chamadas ao repositorio sao protegidas com try/catch para
 * exibir mensagens de erro compreensiveis em caso de falha de banco.
 */
public class PainelAnimais extends JPanel {

    private final RepositorioDados repositorio = RepositorioDados.getInstancia();
    private static final String[] ESPECIES = {"Cachorro", "Gato", "Ave", "Roedor", "Outro"};

    private DefaultTableModel modeloTabela;
    private JTable tabela;
    private JTextField campoBusca;

    private JTextField campoNome;
    private JComboBox<String> comboEspecie;
    private JTextField campoRaca;
    private JComboBox<Cliente> comboTutor;
    private JLabel tituloFormulario;

    private List<Animal> animaisCarregados;
    private Animal animalEmEdicao;

    public PainelAnimais() {
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

        painel.add(ComponentesUI.tituloTela("Animais"));
        painel.add(Box.createVerticalStrut(4));
        painel.add(ComponentesUI.subtitulo("Cadastro de animais vinculados a um cliente"));
        return painel;
    }

    private JPanel criarPainelLista() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(EstiloApp.COR_FUNDO);

        JPanel topo = new JPanel(new BorderLayout(10, 0));
        topo.setBackground(EstiloApp.COR_FUNDO);
        campoBusca = ComponentesUI.campoTexto();
        campoBusca.setToolTipText("Buscar por nome do animal");
        topo.add(campoBusca, BorderLayout.CENTER);

        var botaoBuscar = ComponentesUI.botaoSecundario("Buscar");
        botaoBuscar.addActionListener(this::buscar);
        topo.add(botaoBuscar, BorderLayout.EAST);
        painel.add(topo, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome", "Especie", "Raca", "Tutor"};
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
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarSelecaoNoFormulario();
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(EstiloApp.COR_BORDA));
        scroll.setPreferredSize(new Dimension(10, 400));
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

        tituloFormulario = new JLabel("Novo Animal");
        tituloFormulario.setFont(EstiloApp.FONTE_SUBTITULO);
        tituloFormulario.setForeground(EstiloApp.COR_PRIMARIA);
        tituloFormulario.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(tituloFormulario);
        painel.add(Box.createVerticalStrut(16));

        painel.add(ComponentesUI.rotuloObrigatorio("Nome do animal"));
        campoNome = ComponentesUI.campoTexto();
        painel.add(campoNome);
        painel.add(Box.createVerticalStrut(12));

        painel.add(ComponentesUI.rotuloObrigatorio("Especie"));
        comboEspecie = ComponentesUI.caixaSelecao();
        for (String especie : ESPECIES) {
            comboEspecie.addItem(especie);
        }
        painel.add(comboEspecie);
        painel.add(Box.createVerticalStrut(12));

        painel.add(ComponentesUI.rotulo("Raca"));
        campoRaca = ComponentesUI.campoTexto();
        painel.add(campoRaca);
        painel.add(Box.createVerticalStrut(12));

        painel.add(ComponentesUI.rotuloObrigatorio("Tutor (cliente)"));
        comboTutor = ComponentesUI.caixaSelecao();
        comboTutor.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cliente) {
                    setText(((Cliente) value).getNome());
                }
                return this;
            }
        });
        painel.add(comboTutor);
        painel.add(Box.createVerticalStrut(10));

        JLabel legenda = new JLabel("* Campos obrigatorios");
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

    private void buscar(ActionEvent e) {
        String termo = campoBusca.getText().trim().toLowerCase();
        modeloTabela.setRowCount(0);
        if (animaisCarregados == null) {
            return;
        }
        for (Animal a : animaisCarregados) {
            if (termo.isEmpty() || a.getNome().toLowerCase().contains(termo)) {
                adicionarLinha(a);
            }
        }
    }

    private void adicionarLinha(Animal a) {
        String nomeTutor = "(desconhecido)";
        for (int i = 0; i < comboTutor.getItemCount(); i++) {
            Cliente c = comboTutor.getItemAt(i);
            if (c.getIdCliente() == a.getIdCliente()) {
                nomeTutor = c.getNome();
                break;
            }
        }
        modeloTabela.addRow(new Object[]{a.getIdAnimal(), a.getNome(), a.getEspecie(), a.getRaca(), nomeTutor});
    }

    private void carregarSelecaoNoFormulario() {
        int linha = tabela.getSelectedRow();
        if (linha < 0 || animaisCarregados == null) {
            return;
        }
        int id = (int) modeloTabela.getValueAt(linha, 0);
        for (Animal a : animaisCarregados) {
            if (a.getIdAnimal() == id) {
                animalEmEdicao = a;
                campoNome.setText(a.getNome());
                comboEspecie.setSelectedItem(a.getEspecie());
                campoRaca.setText(a.getRaca());
                for (int i = 0; i < comboTutor.getItemCount(); i++) {
                    Cliente c = comboTutor.getItemAt(i);
                    if (c.getIdCliente() == a.getIdCliente()) {
                        comboTutor.setSelectedItem(c);
                        break;
                    }
                }
                tituloFormulario.setText("Editar Animal");
                break;
            }
        }
    }

    private void limparFormulario() {
        animalEmEdicao = null;
        campoNome.setText("");
        campoRaca.setText("");
        if (comboEspecie.getItemCount() > 0) {
            comboEspecie.setSelectedIndex(0);
        }
        if (comboTutor.getItemCount() > 0) {
            comboTutor.setSelectedIndex(0);
        }
        tituloFormulario.setText("Novo Animal");
        tabela.clearSelection();
    }

    private void salvar(ActionEvent e) {
        String nome = campoNome.getText().trim();
        String raca = campoRaca.getText().trim();
        String especie = (String) comboEspecie.getSelectedItem();
        Cliente tutor = (Cliente) comboTutor.getSelectedItem();

        if (nome.isEmpty() || tutor == null) {
            JOptionPane.showMessageDialog(this,
                    "Preencha os campos obrigatorios: Nome do animal e Tutor.\n"
                    + "Cadastre um cliente antes de cadastrar um animal.",
                    "Campos obrigatorios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (animalEmEdicao == null) {
                repositorio.salvarAnimal(new Animal(0, nome, especie, raca, tutor.getIdCliente()));
                JOptionPane.showMessageDialog(this, "Animal cadastrado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                animalEmEdicao.setNome(nome);
                animalEmEdicao.setEspecie(especie);
                animalEmEdicao.setRaca(raca);
                animalEmEdicao.setIdCliente(tutor.getIdCliente());
                repositorio.salvarAnimal(animalEmEdicao);
                JOptionPane.showMessageDialog(this, "Animal atualizado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            limparFormulario();
            atualizarDados();
        } catch (SQLException ex) {
            mostrarErroBanco("Nao foi possivel salvar o animal.", ex);
        }
    }

    private void excluirSelecionado(ActionEvent e) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um animal na lista para excluir.",
                    "Nenhum animal selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabela.getValueAt(linha, 0);

        try {
            Animal animal = repositorio.buscarAnimalPorId(id);
            if (animal == null) {
                return;
            }

            int opcao = JOptionPane.showConfirmDialog(this,
                    "Deseja realmente excluir o animal \"" + animal.getNome() + "\"?\n"
                    + "Os agendamentos vinculados a ele tambem serao removidos.",
                    "Confirmar exclusao", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (opcao == JOptionPane.YES_OPTION) {
                repositorio.excluirAnimal(animal);
                limparFormulario();
                atualizarDados();
            }
        } catch (SQLException ex) {
            mostrarErroBanco("Nao foi possivel excluir o animal.", ex);
        }
    }

    private void carregarClientesNoCombo() throws SQLException {
        Object selecionadoAntes = comboTutor.getSelectedItem();
        comboTutor.removeAllItems();
        for (Cliente c : repositorio.getClientes()) {
            comboTutor.addItem(c);
        }
        if (selecionadoAntes != null) {
            comboTutor.setSelectedItem(selecionadoAntes);
        }
    }

    /** Recarrega a tabela de animais e a lista de tutores disponiveis a partir do banco. */
    public final void atualizarDados() {
        if (modeloTabela == null) {
            return;
        }
        campoBusca.setText("");
        try {
            carregarClientesNoCombo();
            animaisCarregados = repositorio.getAnimais();
            modeloTabela.setRowCount(0);
            for (Animal a : animaisCarregados) {
                adicionarLinha(a);
            }
        } catch (SQLException ex) {
            mostrarErroBanco("Nao foi possivel carregar a lista de animais.", ex);
        }
    }

    private void mostrarErroBanco(String mensagem, SQLException ex) {
        JOptionPane.showMessageDialog(this,
                mensagem + "\nVerifique se o MySQL esta em execucao e se o banco \"db_petshop\" "
                + "foi criado (veja o script em sql/schema_petshop.sql).\n\nDetalhe tecnico: " + ex.getMessage(),
                "Erro de banco de dados", JOptionPane.ERROR_MESSAGE);
    }
}
