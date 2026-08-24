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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import petshop.model.Cliente;
import petshop.repositorio.RepositorioDados;

/**
 * Tela de Clientes: permite listar, buscar, cadastrar, editar e
 * excluir clientes, conforme o wireframe da Etapa 2.
 *
 * A partir da Etapa 4, os dados sao persistidos no banco de dados
 * MySQL (atraves de RepositorioDados / ClienteDAO). Como toda
 * operacao de banco pode falhar (ex.: MySQL parado, sem rede), todas
 * as chamadas ao repositorio sao protegidas com try/catch, exibindo
 * uma mensagem de erro compreensivel ao usuario em vez de travar a
 * aplicacao.
 */
public class PainelClientes extends JPanel {

    private final RepositorioDados repositorio = RepositorioDados.getInstancia();

    private DefaultTableModel modeloTabela;
    private JTable tabela;
    private JTextField campoBusca;

    private JTextField campoNome;
    private JTextField campoTelefone;
    private JTextField campoEmail;
    private JLabel tituloFormulario;

    private List<Cliente> clientesCarregados;
    private Cliente clienteEmEdicao;

    public PainelClientes() {
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

        JLabel titulo = ComponentesUI.tituloTela("Clientes");
        JLabel subtitulo = ComponentesUI.subtitulo("Cadastro e consulta de clientes (tutores)");
        painel.add(titulo);
        painel.add(Box.createVerticalStrut(4));
        painel.add(subtitulo);
        return painel;
    }

    private JPanel criarPainelLista() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(EstiloApp.COR_FUNDO);

        JPanel topo = new JPanel(new BorderLayout(10, 0));
        topo.setBackground(EstiloApp.COR_FUNDO);
        campoBusca = ComponentesUI.campoTexto();
        campoBusca.setToolTipText("Buscar por nome");
        topo.add(campoBusca, BorderLayout.CENTER);

        var botaoBuscar = ComponentesUI.botaoSecundario("Buscar");
        botaoBuscar.addActionListener(this::buscar);
        topo.add(botaoBuscar, BorderLayout.EAST);
        painel.add(topo, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome", "Telefone", "E-mail"};
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

        tituloFormulario = new JLabel("Novo Cliente");
        tituloFormulario.setFont(EstiloApp.FONTE_SUBTITULO);
        tituloFormulario.setForeground(EstiloApp.COR_PRIMARIA);
        tituloFormulario.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(tituloFormulario);
        painel.add(Box.createVerticalStrut(16));

        painel.add(ComponentesUI.rotuloObrigatorio("Nome completo"));
        campoNome = ComponentesUI.campoTexto();
        painel.add(campoNome);
        painel.add(Box.createVerticalStrut(12));

        painel.add(ComponentesUI.rotuloObrigatorio("Telefone"));
        campoTelefone = ComponentesUI.campoTexto();
        painel.add(campoTelefone);
        painel.add(Box.createVerticalStrut(12));

        painel.add(ComponentesUI.rotulo("E-mail"));
        campoEmail = ComponentesUI.campoTexto();
        painel.add(campoEmail);
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
        if (clientesCarregados == null) {
            return;
        }
        for (Cliente c : clientesCarregados) {
            if (termo.isEmpty() || c.getNome().toLowerCase().contains(termo)) {
                modeloTabela.addRow(new Object[]{c.getIdCliente(), c.getNome(), c.getTelefone(), c.getEmail()});
            }
        }
    }

    private void carregarSelecaoNoFormulario() {
        int linha = tabela.getSelectedRow();
        if (linha < 0 || clientesCarregados == null) {
            return;
        }
        int id = (int) modeloTabela.getValueAt(linha, 0);
        for (Cliente c : clientesCarregados) {
            if (c.getIdCliente() == id) {
                clienteEmEdicao = c;
                campoNome.setText(c.getNome());
                campoTelefone.setText(c.getTelefone());
                campoEmail.setText(c.getEmail());
                tituloFormulario.setText("Editar Cliente");
                break;
            }
        }
    }

    private void limparFormulario() {
        clienteEmEdicao = null;
        campoNome.setText("");
        campoTelefone.setText("");
        campoEmail.setText("");
        tituloFormulario.setText("Novo Cliente");
        tabela.clearSelection();
    }

    private void salvar(ActionEvent e) {
        String nome = campoNome.getText().trim();
        String telefone = campoTelefone.getText().trim();
        String email = campoEmail.getText().trim();

        if (nome.isEmpty() || telefone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Preencha os campos obrigatorios: Nome completo e Telefone.",
                    "Campos obrigatorios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (clienteEmEdicao == null) {
                repositorio.salvarCliente(new Cliente(0, nome, telefone, email));
                JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                clienteEmEdicao.setNome(nome);
                clienteEmEdicao.setTelefone(telefone);
                clienteEmEdicao.setEmail(email);
                repositorio.salvarCliente(clienteEmEdicao);
                JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            limparFormulario();
            atualizarDados();
        } catch (SQLException ex) {
            mostrarErroBanco("Nao foi possivel salvar o cliente.", ex);
        }
    }

    private void excluirSelecionado(ActionEvent e) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na lista para excluir.",
                    "Nenhum cliente selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabela.getValueAt(linha, 0);

        try {
            Cliente cliente = repositorio.buscarClientePorId(id);
            if (cliente == null) {
                return;
            }

            String aviso = repositorio.clienteEmUso(cliente)
                    ? "Este cliente possui animais cadastrados, que tambem serao excluidos.\n\n"
                    : "";
            int opcao = JOptionPane.showConfirmDialog(this,
                    aviso + "Deseja realmente excluir o cliente \"" + cliente.getNome() + "\"?",
                    "Confirmar exclusao", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (opcao == JOptionPane.YES_OPTION) {
                repositorio.excluirCliente(cliente);
                limparFormulario();
                atualizarDados();
            }
        } catch (SQLException ex) {
            mostrarErroBanco("Nao foi possivel excluir o cliente.", ex);
        }
    }

    /** Recarrega a tabela de clientes a partir do banco de dados. */
    public final void atualizarDados() {
        if (modeloTabela == null) {
            return;
        }
        campoBusca.setText("");
        try {
            clientesCarregados = repositorio.getClientes();
            modeloTabela.setRowCount(0);
            for (Cliente c : clientesCarregados) {
                modeloTabela.addRow(new Object[]{c.getIdCliente(), c.getNome(), c.getTelefone(), c.getEmail()});
            }
        } catch (SQLException ex) {
            mostrarErroBanco("Nao foi possivel carregar a lista de clientes.", ex);
        }
    }

    private void mostrarErroBanco(String mensagem, SQLException ex) {
        JOptionPane.showMessageDialog(this,
                mensagem + "\nVerifique se o MySQL esta em execucao e se o banco \"db_petshop\" "
                + "foi criado (veja o script em sql/schema_petshop.sql).\n\nDetalhe tecnico: " + ex.getMessage(),
                "Erro de banco de dados", JOptionPane.ERROR_MESSAGE);
    }
}
