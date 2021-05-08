/**
 * Parede que pode ser instanciada, usada para testes.
 */
package elemento.parede;

import java.awt.geom.Point2D;
import java.util.EnumSet;

public class ParedeTeste extends Parede {
    private static final Point2D.Double DIMENSOES = app.Comum.DIMENSOES_QUADRADOS;

    public ParedeTeste(Point2D.Double posicao) {
        super(posicao, DIMENSOES);
    }

    public ParedeTeste(Point2D.Double posicao, EnumSet<Cardinalidade> cardinalidades) {
        super(posicao, DIMENSOES, cardinalidades);
    }
}