package petshop.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import petshop.model.Cliente;
import petshop.util.ConexaoBD;

/**
 * Classe responsavel pelas operacoes de CRUD da entidade Cliente
 * na tabela "cliente" do banco de dados.
 */
public class ClienteDAO {

    public void inserir(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nome, telefone, email) VALUES (?, ?, ?)";
        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getTelefone());
            stmt.setString(3, cliente.getEmail());
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    cliente.setIdCliente(chaves.getInt(1));
                }
            }
        }
    }

    public void atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET nome = ?, telefone = ?, email = ? WHERE idCliente = ?";
        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getTelefone());
            stmt.setString(3, cliente.getEmail());
            stmt.setInt(4, cliente.getIdCliente());
            stmt.executeUpdate();
        }
    }

    public void excluir(int idCliente) throws SQLException {
        String sql = "DELETE FROM cliente WHERE idCliente = ?";
        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            stmt.executeUpdate();
        }
    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT idCliente, nome, telefone, email FROM cliente ORDER BY nome";

        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapear(rs));
            }
        }
        return clientes;
    }

    public Cliente buscarPorId(int idCliente) throws SQLException {
        String sql = "SELECT idCliente, nome, telefone, email FROM cliente WHERE idCliente = ?";
        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    /** Insere (se idCliente == 0) ou atualiza (caso contrario) e retorna o cliente salvo. */
    public Cliente salvar(Cliente cliente) throws SQLException {
        if (cliente.getIdCliente() == 0) {
            inserir(cliente);
        } else {
            atualizar(cliente);
        }
        return cliente;
    }

    /** Verifica se ha pelo menos um animal cadastrado para este cliente. */
    public boolean possuiAnimaisVinculados(int idCliente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM animal WHERE idCliente = ?";
        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("idCliente"),
                rs.getString("nome"),
                rs.getString("telefone"),
                rs.getString("email")
        );
    }
}
