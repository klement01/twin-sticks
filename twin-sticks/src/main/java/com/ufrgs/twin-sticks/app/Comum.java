/**
 * Constantes de uso comum pelo programa.
 */
package app;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.EnumMap;
import java.util.Random;

public final class Comum {
    // Título do jogo na janela.
    public static final String TITULO = "TwinSticks";

    // Tamanho da janela, em pixels.
    public static final Dimension DIMENSOES_CAMPO = new Dimension(720, 432);

    // Tamanho dos quadrados, em pixels.
    public static final Point2D.Double DIMENSOES_QUADRADOS = new Point2D.Double(48, 48);

    // Quadros por segundo a serem renderizados.
    public static final int FPS_ALVO = 60;

    // Gerador de números aleatórios.
    public static final Random rand = new Random();

    // Direções.
    public enum Cardinalidade {
        NORTE,
        LESTE,
        SUL,
        OESTE,
        NORDESTE,
        SUDESTE,
        SUDOESTE,
        NOROESTE,
        CENTRO;

        private static EnumMap<Cardinalidade, Cardinalidade> mapaOpostos;

        static {
            Cardinalidade.mapaOpostos =
                    new EnumMap<Cardinalidade, Cardinalidade>(Cardinalidade.class);
            Cardinalidade.mapaOpostos.put(NORTE, SUL);
            Cardinalidade.mapaOpostos.put(SUL, NORTE);
            Cardinalidade.mapaOpostos.put(LESTE, OESTE);
            Cardinalidade.mapaOpostos.put(OESTE, LESTE);
            Cardinalidade.mapaOpostos.put(NORDESTE, SUDOESTE);
            Cardinalidade.mapaOpostos.put(SUDOESTE, NORDESTE);
            Cardinalidade.mapaOpostos.put(NOROESTE, SUDESTE);
            Cardinalidade.mapaOpostos.put(SUDESTE, NOROESTE);
            Cardinalidade.mapaOpostos.put(CENTRO, CENTRO);
        }

        public Cardinalidade oposto() {
            return Cardinalidade.mapaOpostos.get(this);
        }
    };
}
