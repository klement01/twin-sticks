/**
 * Carrega um gráfico aleatório de chão e
 * o desenha.
 */
package elemento;

import static app.Comum.DIMENSOES_CAMPO;
import static app.Comum.rand;

import grafico.Grafico;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class Chao extends ElemEstatico {
    private static final String CAMINHO = "chao/chao.png";

    private Grafico grafico;

    public Chao() {
        super(
                new Point2D.Double(0, 0),
                new Point2D.Double(DIMENSOES_CAMPO.width, DIMENSOES_CAMPO.height));

        // Carrega a imagem do chão e aplica um offset aleatório.
        this.grafico = Grafico.carregar(Chao.CAMINHO);
        int ox = rand.nextInt((int) grafico.getDimensoes().getX() - DIMENSOES_CAMPO.width);
        int oy = rand.nextInt((int) grafico.getDimensoes().getY() - DIMENSOES_CAMPO.height);
        this.grafico.cortarRegiao(ox, oy, DIMENSOES_CAMPO.width, DIMENSOES_CAMPO.height);
    }

    @Override
    public void desenhar(Point2D.Double camera, Graphics2D g) {
        double px = -camera.getX();
        double py = -camera.getY();
        this.grafico.desenhar(g, new Point2D.Double(px, py));
    }
}
