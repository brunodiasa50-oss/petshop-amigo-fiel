package petshop.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import petshop.model.Agendamento;
import petshop.util.ConexaoBD;

/**
 * Classe responsavel pelas operacoes de CRUD da entidade Agendamento
 * na tabela "agendamento" do banco de dados. Cada agendamento esta
 * sempre vinculado a um animal (idAnimal), com restricao de chave
 * estrangeira definida no banco (ON DELETE CASCADE).
 */
public class AgendamentoDAO {

    public void inserir(Agendamento agendamento) throws SQLException {
        String sql = "INSERT INTO agendamento (idAnimal, servico, dataHora, status) VALUES (?, ?, ?, ?)";
        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, agendamento.getIdAnimal());
            stmt.setString(2, agendamento.getServico());
            stmt.setTimestamp(3, Timestamp.valueOf(agendamento.getDataHora()));
            stmt.setString(4, agendamento.getStatus());
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    agendamento.setIdAgendamento(chaves.getInt(1));
                }
            }
        }
    }

    public void atualizar(Agendamento agendamento) throws SQLException {
        String sql = "UPDATE agendamento SET idAnimal = ?, servico = ?, dataHora = ?, status = ? "
                + "WHERE idAgendamento = ?";
        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, agendamento.getIdAnimal());
            stmt.setString(2, agendamento.getServico());
            stmt.setTimestamp(3, Timestamp.valueOf(agendamento.getDataHora()));
            stmt.setString(4, agendamento.getStatus());
            stmt.setInt(5, agendamento.getIdAgendamento());
            stmt.executeUpdate();
        }
    }

    public void excluir(int idAgendamento) throws SQLException {
        String sql = "DELETE FROM agendamento WHERE idAgendamento = ?";
        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idAgendamento);
            stmt.executeUpdate();
        }
    }

    /** Lista todos os agendamentos, ordenados por data/hora (os mais proximos primeiro). */
    public List<Agendamento> listarTodos() throws SQLException {
        List<Agendamento> agendamentos = new ArrayList<>();
        String sql = "SELECT idAgendamento, idAnimal, servico, dataHora, status "
                + "FROM agendamento ORDER BY dataHora";

        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                agendamentos.add(mapear(rs));
            }
        }
        return agendamentos;
    }

    public Agendamento buscarPorId(int idAgendamento) throws SQLException {
        String sql = "SELECT idAgendamento, idAnimal, servico, dataHora, status "
                + "FROM agendamento WHERE idAgendamento = ?";
        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idAgendamento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    /** Insere (se idAgendamento == 0) ou atualiza (caso contrario) e retorna o agendamento salvo. */
    public Agendamento salvar(Agendamento agendamento) throws SQLException {
        if (agendamento.getIdAgendamento() == 0) {
            inserir(agendamento);
        } else {
            atualizar(agendamento);
        }
        return agendamento;
    }

    private Agendamento mapear(ResultSet rs) throws SQLException {
        LocalDateTime dataHora = rs.getTimestamp("dataHora").toLocalDateTime();
        return new Agendamento(
                rs.getInt("idAgendamento"),
                rs.getInt("idAnimal"),
                rs.getString("servico"),
                dataHora,
                rs.getString("status")
        );
    }
}
