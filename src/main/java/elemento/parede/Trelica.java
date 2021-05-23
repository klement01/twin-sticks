/**
 * Parede padrão com bordas.
 */
package elemento.parede;

import app.Comum.Cardinalidade;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.EnumSet;

public class Trelica extends Parede {
    private static final Point2D.Double DIMENSOES = app.Comum.DIMENSOES_QUADRADOS;

    public Trelica(Point2D.Double posicao, EnumSet<Cardinalidade> cardinalidades) {
        super(posicao, DIMENSOES, cardinalidades);
    }

    @Override
    public void desenhar(Point2D.Double camera, Graphics2D g) {
        // Determina a posição de desenho baseado na posição
        // da parede e da câmera.
        double px = this.getPosicao().getX() - camera.getX();
        double py = this.getPosicao().getY() - camera.getY();
        var posicao = new Point2D.Double(px, py);

        // Desenha a base.
        Grafico.BASE.desenhar(posicao, g);
    }

    enum Grafico {
        BASE("base.png");

        private static final String CAMINHO = "trelica/";

        private final grafico.Grafico graf;

        Grafico(String arquivo) {
            this.graf = grafico.Grafico.carregar(Grafico.CAMINHO + arquivo);
        }

        public void desenhar(Point2D.Double posicao, Graphics2D g) {
            this.graf.desenhar(g, posicao);
        }
    }
}
