/**
 * Constantes de uso comum pelo programa.
 */
package app;

import java.awt.Dimension;
import java.awt.geom.Point2D;

public class Comum {
    // Título do jogo na janela.
    public static final String TITULO = "TwinSticks";

    // Tamanho da janela, em pixels.
    public static final Dimension DIMENSOES_CAMPO = new Dimension(720, 432);

    // Tamanho dos quadrados, em pixels.
    public static final Point2D.Double DIMENSOES_QUADRADOS = new Point2D.Double(48, 48);

    // Quadros por segundo a serem renderizados.
    public static final int FPS_ALVO = 60;

    // Direções.
    public enum Cardinalidade {
        NORTE,
        LESTE,
        SUL,
        OESTE,
        NORDESTE,
        SUDESTE,
        SUDOESTE,
        NOROESTE
    };
}
