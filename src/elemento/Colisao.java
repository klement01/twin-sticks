/**
 * Define um método para resolver colisões entre
 * objetos que a implementam.
 */
package elemento;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface Colisao {
    // Obtem o retângulo que representa a colisão do objeto.
    public Rectangle2D.Double getRectColisao();

    // Obter o vetor para o centro do objeto.
    public Point2D.Double getVetorCentro();

    // Obtem o vetor velocidade do objeto.
    public Point2D.Double getVetorVelocidade();

    // Realiza qualquer ação necessária durante a resolução
    // de uma colisão entre dois objetos.
    public void resolverColisaoCom(Colisao c);

    // Registra uma colisão para que sua resolução
    // seja concluída no próximo frame.
    public void registrarColisaoCom(Colisao c);

    // Altera a posição do objeto no frame atual.
    public void moverPara(Point2D.Double p);
}
