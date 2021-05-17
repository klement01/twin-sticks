/**
 * Projétil disparado pelo jogador.
 */
package elemento.ator.projetil;

import static java.lang.Math.abs;
import static java.lang.Math.hypot;

import elemento.ator.Jogador;

import java.awt.geom.Point2D;

public class ProjJogador extends Projetil {
    private static final Point2D.Double DIMENSOES = new Point2D.Double(15, 15);
    private static final int DANO = 1;
    private static final double VELOCIDADE_MAX = 300;
    private final Point2D.Double velocidade;

    public ProjJogador(Point2D.Double posicao, Point2D.Double orientacao, Jogador jogador) {
        super(calcularPosicao(posicao), DIMENSOES, jogador);

        // Determina a velocidade (constante) baseada na orientação.
        double vx = orientacao.getX() * VELOCIDADE_MAX;
        double vy = orientacao.getY() * VELOCIDADE_MAX;
        double escalaVelocidade = abs(VELOCIDADE_MAX / hypot(vx, vy));
        vx *= escalaVelocidade;
        vy *= escalaVelocidade;
        this.velocidade = new Point2D.Double(vx, vy);

        // Se move para diminuir colisão com o dono.
        this.atualizar(30 / VELOCIDADE_MAX);
    }

    // Determina a posição real do projétil para ele iniciar
    // centralizado na posição desejada.
    private static Point2D.Double calcularPosicao(Point2D.Double posicao) {
        double px = posicao.getX() - DIMENSOES.getX() / 2;
        double py = posicao.getY() - DIMENSOES.getY() / 2;
        return new Point2D.Double(px, py);
    }

    @Override
    public int getDano() {
        return DANO;
    }

    @Override
    public Point2D.Double getVelocidade() {
        return velocidade;
    }
}
