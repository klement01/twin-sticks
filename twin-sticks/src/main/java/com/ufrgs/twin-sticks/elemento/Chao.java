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
    private static final String CAMINHO_CHAO = "chao/chao.png";
    private static final String CAMINHO_TUTORIAL = "chao/tutorial.png";

    private boolean exibirTutorial;
    private Grafico grafChao;
    private Grafico grafTutorial;

    public Chao(boolean tutorial) {
        super(
                new Point2D.Double(0, 0),
                new Point2D.Double(DIMENSOES_CAMPO.width, DIMENSOES_CAMPO.height));

        // Carrega a imagem do chão e aplica um offset aleatório.
        this.grafChao = Grafico.carregar(Chao.CAMINHO_CHAO);
        int ox = rand.nextInt((int) grafChao.getDimensoes().getX() - DIMENSOES_CAMPO.width);
        int oy = rand.nextInt((int) grafChao.getDimensoes().getY() - DIMENSOES_CAMPO.height);
        this.grafChao.cortarRegiao(ox, oy, DIMENSOES_CAMPO.width, DIMENSOES_CAMPO.height);

        // Carrega o gráfico de tutorial, se necessário.
        this.exibirTutorial = tutorial;
        if (tutorial) {
            this.grafTutorial = Grafico.carregar(Chao.CAMINHO_TUTORIAL);
        }
    }

    @Override
    public void desenhar(Point2D.Double camera, Graphics2D g) {
        double px = -camera.getX();
        double py = -camera.getY();
        this.grafChao.desenhar(g, new Point2D.Double(px, py));
        if (this.exibirTutorial) {
            this.grafTutorial.desenhar(g, new Point2D.Double(px, py));
        }
    }
}
