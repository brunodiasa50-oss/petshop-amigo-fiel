package petshop.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitaria responsavel por abrir e fechar a conexao com o
 * banco de dados MySQL "db_petshop".
 *
 * O banco e as tabelas devem ser criados previamente executando o
 * script "sql/schema_petshop.sql" (incluido na raiz deste projeto) no
 * MySQL Workbench ou na linha de comando do MySQL. Esse script ja cria
 * o usuario abaixo (petshop / petshop123), entao normalmente nao e
 * necessario alterar nada aqui. Caso prefira usar outro usuario (ex.:
 * root), ajuste as constantes USUARIO e SENHA.
 *
 * A biblioteca "MySQL Connector/J" (driver JDBC) ja esta incluida na
 * pasta lib/ deste projeto e configurada no classpath do NetBeans.
 */
public class ConexaoBD {

    private static final String URL =
            "jdbc:mysql://localhost:3306/db_petshop?useTimezone=true&serverTimezone=UTC&useSSL=false";
    private static final String USUARIO = "petshop";
    private static final String SENHA = "petshop123";

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver do MySQL nao encontrado. "
                    + "Verifique se a biblioteca MySQL Connector/J (pasta lib/) "
                    + "esta no classpath do projeto.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    public static void fechar(Connection conexao) {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexao: " + e.getMessage());
            }
        }
    }
}
