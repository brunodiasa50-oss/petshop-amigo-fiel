package petshop.repositorio;

import java.sql.SQLException;
import java.util.List;
import petshop.dao.AgendamentoDAO;
import petshop.dao.AnimalDAO;
import petshop.dao.ClienteDAO;
import petshop.model.Agendamento;
import petshop.model.Animal;
import petshop.model.Cliente;

/**
 * Ponto unico de acesso aos dados do sistema, usado por todas as
 * telas (pacote petshop.ui).
 *
 * A partir da Etapa 4, esta classe passou a delegar todas as
 * operacoes para o banco de dados MySQL atraves das classes DAO
 * (pacote petshop.dao), substituindo o armazenamento em memoria
 * usado provisoriamente ate a Etapa 3. As telas nao precisaram ser
 * alteradas na sua logica de navegacao: apenas passaram a tratar
 * SQLException, ja que agora as operacoes dependem de uma conexao
 * real com o banco.
 */
public class RepositorioDados {

    private static final RepositorioDados INSTANCIA = new RepositorioDados();

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final AnimalDAO animalDAO = new AnimalDAO();
    private final AgendamentoDAO agendamentoDAO = new AgendamentoDAO();

    private RepositorioDados() {
    }

    public static RepositorioDados getInstancia() {
        return INSTANCIA;
    }

    // ---------------- Clientes ----------------

    public List<Cliente> getClientes() throws SQLException {
        return clienteDAO.listarTodos();
    }

    public Cliente salvarCliente(Cliente cliente) throws SQLException {
        return clienteDAO.salvar(cliente);
    }

    /** Exclui o cliente. Animais e agendamentos vinculados sao removidos
     * automaticamente pelo banco (restricao ON DELETE CASCADE). */
    public void excluirCliente(Cliente cliente) throws SQLException {
        clienteDAO.excluir(cliente.getIdCliente());
    }

    public boolean clienteEmUso(Cliente cliente) throws SQLException {
        return clienteDAO.possuiAnimaisVinculados(cliente.getIdCliente());
    }

    public Cliente buscarClientePorId(int idCliente) throws SQLException {
        return clienteDAO.buscarPorId(idCliente);
    }

    // ---------------- Animais ----------------

    public List<Animal> getAnimais() throws SQLException {
        return animalDAO.listarTodos();
    }

    public List<Animal> getAnimaisDoCliente(int idCliente) throws SQLException {
        return animalDAO.listarPorCliente(idCliente);
    }

    public Animal salvarAnimal(Animal animal) throws SQLException {
        return animalDAO.salvar(animal);
    }

    /** Exclui o animal. Agendamentos vinculados sao removidos
     * automaticamente pelo banco (restricao ON DELETE CASCADE). */
    public void excluirAnimal(Animal animal) throws SQLException {
        animalDAO.excluir(animal.getIdAnimal());
    }

    public Animal buscarAnimalPorId(int idAnimal) throws SQLException {
        return animalDAO.buscarPorId(idAnimal);
    }

    // ---------------- Agendamentos ----------------

    public List<Agendamento> getAgendamentos() throws SQLException {
        return agendamentoDAO.listarTodos();
    }

    public Agendamento salvarAgendamento(Agendamento agendamento) throws SQLException {
        return agendamentoDAO.salvar(agendamento);
    }

    public void excluirAgendamento(Agendamento agendamento) throws SQLException {
        agendamentoDAO.excluir(agendamento.getIdAgendamento());
    }
}
