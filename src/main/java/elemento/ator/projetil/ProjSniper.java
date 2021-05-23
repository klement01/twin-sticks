/**
 * Projétil disparado por um sniper.
 */
package elemento.ator.projetil;

import static java.lang.Math.hypot;

import elemento.ator.inimigo.Sniper;

import grafico.Grafico;

import java.awt.geom.Point2D;

public class ProjSniper extends Projetil {
    private static final Grafico GRAFICO = Grafico.carregar(CAMINHO + "jogador.png");
    private static final int DANO = 1;
    private static final double VELOCIDADE_MAX = 200;

    public ProjSniper(Point2D.Double posicao, Point2D.Double orientacao, Sniper sniper) {
        super(centralizarProjetil(posicao, GRAFICO), orientacao, sniper, GRAFICO);

        // Calcula a velocidade inicial baseado na orientação e
        // na velocidade máxima.
        double escala = ProjSniper.VELOCIDADE_MAX / hypot(orientacao.getX(), orientacao.getY());
        double vx = orientacao.getX() * escala;
        double vy = orientacao.getY() * escala;
        this.velocidade = new Point2D.Double(vx, vy);

        // Se move para diminuir colisão com o dono.
        this.atualizar(20 / VELOCIDADE_MAX);
    }

    @Override
    public int getDano() {
        return DANO;
    }
}
