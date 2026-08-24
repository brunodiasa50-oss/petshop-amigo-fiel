package petshop.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Fabrica de componentes de UI padronizados (botoes, campos e rotulos),
 * para manter a mesma aparencia visual em todas as telas do sistema.
 */
public final class ComponentesUI {

    private ComponentesUI() {
    }

    public static JButton botaoPrimario(String texto) {
        JButton botao = new JButton(texto);
        estilizarBotao(botao, EstiloApp.COR_ACENTO, Color.WHITE);
        return botao;
    }

    public static JButton botaoSecundario(String texto) {
        JButton botao = new JButton(texto);
        estilizarBotao(botao, new Color(0xE4, 0xE7, 0xEB), EstiloApp.COR_TEXTO);
        return botao;
    }

    public static JButton botaoLink(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setFont(EstiloApp.FONTE_CORPO_NEGRITO);
        botao.setForeground(cor);
        botao.setBorderPainted(false);
        botao.setContentAreaFilled(false);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return botao;
    }

    private static void estilizarBotao(JButton botao, Color fundo, Color texto) {
        botao.setBackground(fundo);
        botao.setForeground(texto);
        botao.setFont(EstiloApp.FONTE_CORPO_NEGRITO);
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(9, 18, 9, 18));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    public static JLabel rotulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(EstiloApp.FONTE_CORPO);
        label.setForeground(EstiloApp.COR_TEXTO);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public static JLabel rotuloObrigatorio(String texto) {
        return rotulo(texto + " *");
    }

    public static JTextField campoTexto() {
        JTextField campo = new JTextField();
        campo.setFont(EstiloApp.FONTE_CORPO);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstiloApp.COR_BORDA),
                new EmptyBorder(4, 8, 4, 8)));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return campo;
    }

    public static <T> JComboBox<T> caixaSelecao() {
        JComboBox<T> combo = new JComboBox<>();
        combo.setFont(EstiloApp.FONTE_CORPO);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return combo;
    }

    public static JLabel tituloTela(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(EstiloApp.FONTE_TITULO);
        label.setForeground(EstiloApp.COR_TEXTO);
        return label;
    }

    public static JLabel subtitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(EstiloApp.FONTE_CORPO);
        label.setForeground(EstiloApp.COR_TEXTO_CLARO);
        return label;
    }
}
