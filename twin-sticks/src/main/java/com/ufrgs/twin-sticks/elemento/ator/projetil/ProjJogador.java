/**
 * Projétil disparado pelo jogador.
 */
package elemento.ator.projetil;

import static java.lang.Math.hypot;

import elemento.ator.Jogador;

import grafico.Grafico;

import java.awt.geom.Point2D;

public class ProjJogador extends Projetil {
    private static final Grafico GRAFICO = Grafico.carregar(CAMINHO + "jogador.png");
    private static final int DANO = 1;
    private static final double VELOCIDADE_MAX = 300;

    public ProjJogador(Point2D.Double posicao, Point2D.Double orientacao, Jogador jogador) {
        super(centralizarProjetil(posicao, GRAFICO), orientacao, jogador, GRAFICO);

        // Calcula a velocidade inicial baseado na orientação e
        // na velocidade máxima.
        double escala = ProjJogador.VELOCIDADE_MAX / hypot(orientacao.getX(), orientacao.getY());
        double vx = orientacao.getX() * escala;
        double vy = orientacao.getY() * escala;
        this.velocidade = new Point2D.Double(vx, vy);

        // Se move para diminuir colisão com o dono.
        this.atualizar(30 / VELOCIDADE_MAX);
    }

    @Override
    public int getDano() {
        return DANO;
    }
}
