/**
 * Parede treliçada.
 */
package elemento.parede;

import static java.lang.Math.PI;

import app.Comum.Cardinalidade;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.EnumSet;

public class ParedePadrao extends Parede {
    private static final Point2D.Double DIMENSOES = app.Comum.DIMENSOES_QUADRADOS;

    public ParedePadrao(Point2D.Double posicao, EnumSet<Cardinalidade> cardinalidades) {
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

        // Desenha os enfeites e as bordas basado nas
        // cardinalidades de colisão da parede.
        for (var i : this.getCardinalidades()) {
            Grafico.ENFEITE.desenharNosEixos(posicao, g, i);
            Grafico.ENFEITE_CANTO.desenharNasDiagonais(posicao, g, i);
        }
        for (var i : this.getCardinalidades()) {
            Grafico.BORDA.desenharNosEixos(posicao, g, i);
        }
    }

    private enum Grafico {
        BASE("base.png"),
        BORDA("borda.png"),
        ENFEITE_CANTO("enfeite_canto.png"),
        ENFEITE("enfeite.png");

        private static final String CAMINHO = "parede_padrao/";

        private final grafico.Grafico graf;

        Grafico(String arquivo) {
            this.graf = grafico.Grafico.carregar(Grafico.CAMINHO + arquivo);
        }

        public void desenhar(Point2D.Double posicao, Graphics2D g) {
            this.graf.desenhar(g, posicao);
        }

        // Desenha um gráfico alinhado com os eixos x e y.
        public void desenharNosEixos(
                Point2D.Double posicao, Graphics2D g, Cardinalidade cardinalidade) {
            double angulo = 0;
            switch (cardinalidade) {
                case LESTE:
                    angulo = 0;
                    break;
                case NORTE:
                    angulo = 3 * PI / 2;
                    break;
                case OESTE:
                    angulo = PI;
                    break;
                case SUL:
                    angulo = PI / 2;
                    break;
                default:
                    return;
            }
            this.graf.desenhar(g, posicao, 1, angulo);
        }

        // Desenha um gráfico alinhado com as diagonais x = y
        // e x = -y (na prática, as bordas da imagem.)
        public void desenharNasDiagonais(
                Point2D.Double posicao, Graphics2D g, Cardinalidade cardinalidade) {
            double angulo = 0;
            switch (cardinalidade) {
                case NORDESTE:
                    angulo = 0;
                    break;
                case NOROESTE:
                    angulo = 3 * PI / 2;
                    break;
                case SUDOESTE:
                    angulo = PI;
                    break;
                case SUDESTE:
                    angulo = PI / 2;
                    break;
                default:
                    return;
            }
            this.graf.desenhar(g, posicao, 1, angulo);
        }
    }
}
