/**
 * Representa um elemento gráfico com posição
 * e dimensões constantes.
 */
package elemento;

import java.awt.geom.Point2D;

public abstract class ElemEstatico extends Elemento {
    protected ElemEstatico(Point2D.Double posicao, Point2D.Double dimensoes) {
        super(posicao, dimensoes);
    }
}
