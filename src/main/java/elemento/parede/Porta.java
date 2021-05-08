/**
 * Parede que pode ser instanciada, usada para testes.
 */
package elemento.parede;

import static app.Comum.DIMENSOES_QUADRADOS;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Porta extends Parede {
    private static final Point2D.Double DIMENSOES_VERTICAL = new Point2D.Double(DIMENSOES_QUADRADOS.x * 9 / 10,
            DIMENSOES_QUADRADOS.y);
    private static final Point2D.Double DIMENSOES_HORIZONTAL = new Point2D.Double(DIMENSOES_VERTICAL.y,
            DIMENSOES_VERTICAL.x);

    private Porta(Point2D.Double posicao, Point2D.Double dimensoes) {
        super(posicao, dimensoes);
        this.cor = Color.GREEN;
    }

    /**
     * Cria uma porta com as dimensões adequadas para ser vertical ou horizontal
     * e a centraliza no quadrado indicado por posição.
     */
    public static Porta portaVertical(Point2D.Double posicao) {
        var posicaoReal = new Point2D.Double(posicao.x + (DIMENSOES_QUADRADOS.x - DIMENSOES_VERTICAL.x) / 2.0, posicao.y);
        return new Porta(posicaoReal, DIMENSOES_VERTICAL);
    }

    public static Porta portaHorizontal(Point2D.Double posicao) {
        var posicaoReal = new Point2D.Double(posicao.x, posicao.y + (DIMENSOES_QUADRADOS.y - DIMENSOES_HORIZONTAL.y) / 2.0);
        return new Porta(posicaoReal, DIMENSOES_HORIZONTAL);
    }
}