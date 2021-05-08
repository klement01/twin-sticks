/**
 * Representa um elemento gráfico que tem a capacidade
 * de se mover e realizar outras ações dentro da área de
 * jogo.
 */
package main.java.elemento;

import java.awt.geom.Point2D;

public abstract class ElemDinamico extends Elemento {
    protected ElemDinamico(Point2D.Double posicao, Point2D.Double dimensoes) {
        super(posicao, dimensoes);
    }

    // Move ou atualiza o objeto de alguma maneira de
    // acordo com o tempo passado desde o último frame.
    // Retorna true se o elemento dever continuar no jogo,
    // false se ele for destruído.
    public abstract boolean atualizar(double dt);
}
