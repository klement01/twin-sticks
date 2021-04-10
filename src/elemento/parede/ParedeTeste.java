/**
 * Parede para testar colisão.
 */
package elemento.parede;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EnumSet;

public class ParedeTeste extends Parede {
    /*
     * Cria um parede com colisão em todas suas superfícies
     * ou em um subconjunto de suas superfícies.
     */
    public ParedeTeste(Point2D.Double posicao, Point2D.Double dimensoes) {
        // Se não forem passadas cardinalidades para colisores
        // serem empurrados, assume que a parede empurra para
        // qualquer lado.
        this(posicao, dimensoes, EnumSet.allOf(Cardinalidade.class));
    }

    private EnumSet<Cardinalidade> card;

    public ParedeTeste(
            Point2D.Double posicao, Point2D.Double dimensoes, EnumSet<Cardinalidade> cardinalidades)
            throws IllegalArgumentException {
        // Construtor genérico de Parede.
        super(posicao, dimensoes, cardinalidades);
        this.card = cardinalidades;
    }

    @Override
    public void desenhar(Point2D.Double camera, Graphics2D g) {
        g.setColor(Color.BLUE);
        var rect =
                new Rectangle2D.Double(
                        this.posicao.getX() - camera.getX(),
                        this.posicao.getY() - camera.getY(),
                        this.dimensoes.getX(),
                        this.dimensoes.getY());
        g.fill(rect);
        g.setColor(Color.ORANGE);
        g.draw(rect);
        var w = 7;
        var h = w;
        if (card.contains(Cardinalidade.NORTE)) {
            g.fill(
                    new Ellipse2D.Double(
                            rect.getX() + rect.getWidth() / 2 - w / 2, rect.getY(), w, h));
        }
        if (card.contains(Cardinalidade.OESTE)) {
            g.fill(
                    new Ellipse2D.Double(
                            rect.getX(), rect.getY() + rect.getHeight() / 2 - h / 2, w, h));
        }
        if (card.contains(Cardinalidade.LESTE)) {
            g.fill(
                    new Ellipse2D.Double(
                            rect.getX() + rect.getWidth() - w,
                            rect.getY() + rect.getHeight() / 2 - h / 2,
                            w,
                            h));
        }
        if (card.contains(Cardinalidade.SUL)) {
            g.fill(
                    new Ellipse2D.Double(
                            rect.getX() + rect.getWidth() / 2 - w / 2,
                            rect.getY() + rect.getHeight() - h,
                            w,
                            h));
        }
    }
}
