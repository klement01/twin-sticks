/**
 * Desenha um indicador gráfico da vida
 * do jogador.
 */
package elemento;

import grafico.Grafico;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class HUD extends ElemEstatico {
    private int vida = 0;

    public HUD() {
        super(new Point2D.Double(0, 0), new Point2D.Double(0, 0));
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    @Override
    public void desenhar(Point2D.Double camera, Graphics2D g) {
        int cheio = this.vida / 2;
        int metade = this.vida % 2;

        double passoX = Grafico.CHEIO.getDimensoes().getX();
        double offsetX = 0;
        for (int i = 0; i < cheio; i++) {
            Grafico.CHEIO.desenhar(new Point2D.Double(offsetX, 0), g);
            offsetX += passoX;
        }
        if (metade != 0) {
            Grafico.METADE.desenhar(new Point2D.Double(offsetX, 0), g);
        }
    }

    /**
     * Carrega as imagens dos corações.
     */
    enum Grafico {
        CHEIO("cheio.png"),
        METADE("metade.png");

        private static final String CAMINHO = "hud/";
        private final grafico.Grafico graf;

        Grafico(String nome) {
            this.graf = grafico.Grafico.carregar(Grafico.CAMINHO + nome);
        }

        public Point2D.Double getDimensoes() {
            return this.graf.getDimensoes();
        }

        public void desenhar(Point2D.Double posicao, Graphics2D g) {
            this.graf.desenhar(g, posicao);
        }
    }
}
