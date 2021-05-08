/**
 * Parede que pode ser instanciada, usada para testes.
 */
package main.java.elemento.parede;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.EnumSet;

import main.java.app.Comum.Cardinalidade;

public class ParedePadrao extends Parede {
    private static final Point2D.Double DIMENSOES = main.java.app.Comum.DIMENSOES_QUADRADOS;

    public ParedePadrao(Point2D.Double posicao, EnumSet<Cardinalidade> cardinalidades) {
        super(posicao, DIMENSOES, cardinalidades);
        this.cor = Color.blue;
    }
}