/**
 * Implementa um projétil que se desloca até
 * atingir outro elemento, causado dano se
 * aplicável.
 */
package elemento.ator.projetil;

import static java.lang.Math.atan2;

import elemento.Colisoes;
import elemento.Elemento;
import elemento.ator.Ator;

import grafico.Grafico;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public abstract class Projetil extends Ator {
    protected static final String CAMINHO = "projeteis/";

    protected final Elemento dono;
    protected final Grafico grafico;
    protected Point2D.Double velocidade = new Point2D.Double();

    private boolean vivo = true;

    protected Projetil(
            Point2D.Double posicao, Point2D.Double orientacao, Elemento dono, Grafico grafico) {
        super(posicao, grafico.getDimensoes());
        this.dono = dono;
        this.grafico = grafico;
    }

    protected static Point2D.Double centralizarProjetil(Point2D.Double posicao, Grafico grafico) {
        Point2D.Double dimensoes = grafico.getDimensoes();
        double px = posicao.getX() - dimensoes.getX() / 2;
        double py = posicao.getY() - dimensoes.getY() / 2;
        return new Point2D.Double(px, py);
    }

    @Override
    public boolean atualizar(double dt) {
        // Processa as colisões e checa se o projétil
        // ainda está vivo.
        if (!super.atualizar(dt)) {
            return false;
        }

        // Calcula a nova posição do projétil baseado
        // em sua velocidade.
        var velocidade = getVelocidade();
        double dx = velocidade.getX() * dt;
        double dy = velocidade.getY() * dt;

        var posicao = getPosicao();
        this.setPosicao(new Point2D.Double(posicao.getX() + dx, posicao.getY() + dy));

        return true;
    }

    // Retorna o dano causado pelo projétil após uma colisão.
    // Dano pode depender do objeto.
    public abstract int getDano();

    // Retorna a velocidade do projétil no frame atual.
    protected Point2D.Double getVelocidade() {
        return this.velocidade;
    }

    // Retorna o ângulo com o semieixo x positivo no sentido
    // anti-horário.
    protected final double getAngulo() {
        return atan2(this.getVelocidade().getY(), this.getVelocidade().getX());
    }

    // Checa se o elemento é dono desse projétil.
    public final boolean isDono(Elemento c) {
        return c == this.dono;
    }

    // Projétil "morre" ao colidir com qualquer coisa que
    // não seja a) seu dono ou b) outro projétil.
    @Override
    protected void resolverColisaoPassada(Colisao c, double dt) {
        var colisor = c.getColisor();
        if (colisor != dono) {
            vivo = false;
        }
    }

    @Override
    public boolean atorVivo() {
        return this.vivo;
    }

    @Override
    public void desenhar(Point2D.Double camera, Graphics2D g) {
        double cx = this.getPosicao().getX() - camera.getX();
        double cy = this.getPosicao().getY() - camera.getY();
        this.grafico.desenhar(g, new Point2D.Double(cx, cy), 1, this.getAngulo());
    }

    // Se ambos os colisores forem projéteis, ignora a colisão.
    // Se não, deixa o outro objeto lidar com a colisão.
    @Override
    public void resolverColisaoCom(Colisoes c) {
        if (c instanceof Projetil) {
            return;
        } else {
            c.resolverColisaoCom(this);
        }
    }
}
