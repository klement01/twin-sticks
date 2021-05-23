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

    // Proíbe a edição da posição e das dimensões
    // do objeto.
    @Override
    protected void setPosicao(Point2D.Double posicao) {}

    @Override
    protected void setDimensoes(Point2D.Double dimensoes) {}
}
