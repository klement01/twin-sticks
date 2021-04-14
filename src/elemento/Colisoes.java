/**
 * Define um método para resolver colisões entre
 * objetos que a implementam.
 */
package elemento;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface Colisoes {
    // Representa uma colisão que já foi parcialmente
    // resolvida, incluindo o deslocamento causado por ela.
    public final class Colisao {
        private final Colisoes colisor;
        private final Point2D.Double deslocamento;

        public Colisao(Colisoes colisor, Point2D.Double deslocamento) {
            this.colisor = colisor;
            this.deslocamento = deslocamento;
        }

        public Colisoes getColisor() {
            return this.colisor;
        }

        public Point2D.Double getDeslocamento() {
            return this.deslocamento;
        }
    }

    // Obtem o retângulo que representa a colisão do objeto.
    public Rectangle2D.Double getRectColisao();

    // Obtem o vetor para o centro do objeto.
    public Point2D.Double getVetorCentro();

    // Realiza qualquer ação necessária durante a resolução
    // de uma colisão entre dois objetos.
    public void resolverColisaoCom(Colisoes c);

    // Registra uma colisão incluindo o deslocamento causado
    // para que sua resolução seja concluída no próximo frame.
    public void registrarColisao(Colisao c);

    // Altera a posição do objeto no frame atual.
    public void empurrar(Point2D.Double p);
}
