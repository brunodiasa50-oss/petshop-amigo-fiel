package petshop.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Constantes visuais do sistema, seguindo a paleta de cores e a
 * tipografia definidas no projeto de UX/UI da Etapa 2.
 */
public final class EstiloApp {

    public static final Color COR_PRIMARIA = new Color(0x2F, 0x54, 0x96);
    public static final Color COR_SECUNDARIA = new Color(0x8F, 0xAA, 0xDC);
    public static final Color COR_ACENTO = new Color(0xED, 0x7D, 0x31);
    public static final Color COR_FUNDO = new Color(0xF5, 0xF7, 0xFA);
    public static final Color COR_CARTAO = Color.WHITE;
    public static final Color COR_TEXTO = new Color(0x26, 0x26, 0x26);
    public static final Color COR_TEXTO_CLARO = new Color(0x6B, 0x72, 0x80);
    public static final Color COR_BORDA = new Color(0xD0, 0xD5, 0xDD);
    public static final Color COR_ERRO = new Color(0xC0, 0x00, 0x00);
    public static final Color COR_SUCESSO = new Color(0x2E, 0x7D, 0x32);

    public static final Font FONTE_TITULO = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONTE_SUBTITULO = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONTE_CORPO = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONTE_CORPO_NEGRITO = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONTE_LEGENDA = new Font("SansSerif", Font.PLAIN, 11);

    private EstiloApp() {
    }
}
