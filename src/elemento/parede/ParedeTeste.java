/**
 * Parede que pode ser instanciada, usada para testes.
 */
package elemento.parede;

import java.awt.geom.Point2D;
import java.util.EnumSet;

public class ParedeTeste extends Parede {
    public ParedeTeste(Point2D.Double posicao, Point2D.Double dimensoes) {
        super(posicao, dimensoes);
    }

    public ParedeTeste(
            Point2D.Double posicao, Point2D.Double dimensoes, EnumSet<Cardinalidade> cardinalidades)
            throws IllegalArgumentException {
        super(posicao, dimensoes, cardinalidades);
    }
}
