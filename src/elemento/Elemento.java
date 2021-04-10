/**
 * Representa um elemento gráfico do jogo,
 * que é retangular e tem aparência.
 */
package elemento;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public abstract class Elemento {
    protected Point2D.Double posicao;
    protected Point2D.Double dimensoes;

    protected Elemento(Point2D.Double posicao, Point2D.Double dimensoes) {
        this.posicao = posicao;
        this.dimensoes = dimensoes;
    }

    // Desenha o objeto na tela, levando em consideração a posição
    // da câmera relativa ao número.
    public abstract void desenhar(Point2D.Double camera, Graphics2D g);
}
