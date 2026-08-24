package petshop.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import petshop.model.Animal;
import petshop.util.ConexaoBD;

/**
 * Classe responsavel pelas operacoes de CRUD da entidade Animal na
 * tabela "animal" do banco de dados. Cada animal esta sempre
 * vinculado a um cliente (idCliente), com restricao de chave
 * estrangeira definida no banco (ON DELETE CASCADE).
 */
public class AnimalDAO {

    public void inserir(Animal animal) throws SQLException {
        String sql = "INSERT INTO animal (nome, especie, raca, idCliente) VALUES (?, ?, ?, ?)";
        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, animal.getNome());
            stmt.setString(2, animal.getEspecie());
            stmt.setString(3, animal.getRaca());
            stmt.setInt(4, animal.getIdCliente());
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    animal.setIdAnimal(chaves.getInt(1));
                }
            }
        }
    }

    public void atualizar(Animal animal) throws SQLException {
        String sql = "UPDATE animal SET nome = ?, especie = ?, raca = ?, idCliente = ? WHERE idAnimal = ?";
        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, animal.getNome());
            stmt.setString(2, animal.getEspecie());
            stmt.setString(3, animal.getRaca());
            stmt.setInt(4, animal.getIdCliente());
            stmt.setInt(5, animal.getIdAnimal());
            stmt.executeUpdate();
        }
    }

    public void excluir(int idAnimal) throws SQLException {
        String sql = "DELETE FROM animal WHERE idAnimal = ?";
        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idAnimal);
            stmt.executeUpdate();
        }
    }

    public List<Animal> listarTodos() throws SQLException {
        List<Animal> animais = new ArrayList<>();
        String sql = "SELECT idAnimal, nome, especie, raca, idCliente FROM animal ORDER BY nome";

        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                animais.add(mapear(rs));
            }
        }
        return animais;
    }

    public List<Animal> listarPorCliente(int idCliente) throws SQLException {
        List<Animal> animais = new ArrayList<>();
        String sql = "SELECT idAnimal, nome, especie, raca, idCliente FROM animal "
                + "WHERE idCliente = ? ORDER BY nome";

        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    animais.add(mapear(rs));
                }
            }
        }
        return animais;
    }

    public Animal buscarPorId(int idAnimal) throws SQLException {
        String sql = "SELECT idAnimal, nome, especie, raca, idCliente FROM animal WHERE idAnimal = ?";
        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idAnimal);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    /** Insere (se idAnimal == 0) ou atualiza (caso contrario) e retorna o animal salvo. */
    public Animal salvar(Animal animal) throws SQLException {
        if (animal.getIdAnimal() == 0) {
            inserir(animal);
        } else {
            atualizar(animal);
        }
        return animal;
    }

    private Animal mapear(ResultSet rs) throws SQLException {
        return new Animal(
                rs.getInt("idAnimal"),
                rs.getString("nome"),
                rs.getString("especie"),
                rs.getString("raca"),
                rs.getInt("idCliente")
        );
    }
}
