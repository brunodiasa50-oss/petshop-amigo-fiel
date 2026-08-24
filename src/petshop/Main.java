package petshop;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import petshop.ui.MainFrame;

/**
 * Classe principal do sistema Petshop Amigo Fiel.
 *
 * A partir da Etapa 3, o sistema passa a ser executado atraves de uma
 * interface grafica em Java Swing (pacote petshop.ui), implementada
 * conforme o projeto de UX/UI definido na Etapa 2. Os dados continuam
 * sendo mantidos em memoria (petshop.repositorio.RepositorioDados),
 * ja que o uso de banco de dados ainda nao e exigido nesta etapa; as
 * classes do pacote petshop.dao e petshop.util seguem disponiveis
 * para quando a persistencia em MySQL for implementada.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Se o look and feel do sistema nao estiver disponivel,
            // o sistema segue com o padrao do Swing sem interromper a execucao.
            System.err.println("Nao foi possivel aplicar o look and feel do sistema: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame janelaPrincipal = new MainFrame();
            janelaPrincipal.setVisible(true);
        });
    }
}
