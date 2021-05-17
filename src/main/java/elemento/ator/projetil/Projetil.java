/**
 * Implementa um projétil que se desloca até
 * atingir outro elemento, causado dano se
 * aplicável.
 */
package elemento.ator.projetil;

import elemento.Elemento;
import elemento.ator.Ator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class Projetil extends Ator {
    protected final Elemento dono;

    private boolean vivo = true;

    public Projetil(Point2D.Double posicao, Point2D.Double dimensao, Elemento dono) {
        super(posicao, dimensao);
        this.dono = dono;
    }

    @Override
    public boolean atualizar(double dt) {
        // Processa as colisões e checa se o projétil
        // ainda está vivo.
        if (!super.atualizar(dt)) {
            return false;
        }
        var velocidade = getVelocidade();
        double dx = velocidade.getX() * dt;
        double dy = velocidade.getY() * dt;
        var posicao = getPosicao();
        setPosicao(new Point2D.Double(posicao.getX() + dx, posicao.getY() + dy));
        return true;
    }

    // Retorna o dano causado pelo projétil após uma colisão.
    // Dano pode depender do objeto.
    public abstract int getDano();

    // Retorna a velocidade do projétil no frame atual.
    public abstract Point2D.Double getVelocidade();

    // Checa se o elemento é dono desse projétil.
    public final boolean isDono(Elemento c) {
        return c == this.dono;
    }

    // Projétil "morre" ao colidir com qualquer coisa que
    // não seja a) seu dono ou b) outro projétil.
    @Override
    protected void resolverColisaoPassada(Colisao c, double dt) {
        var colisor = c.getColisor();
        if (colisor != dono && !(colisor instanceof Projetil)) {
            vivo = false;
        }
    }

    @Override
    public boolean atorVivo() {
        return this.vivo;
    }

    // Desenha um projétil genérico na tela.
    // TODO: gráficos para projéteis.
    @Override
    public void desenhar(Point2D.Double camera, Graphics2D g) {
        g.setColor(Color.MAGENTA);
        var rect =
                new Rectangle2D.Double(
                        this.getPosicao().getX() - camera.getX(),
                        this.getPosicao().getY() - camera.getY(),
                        this.getDimensoes().getX(),
                        this.getDimensoes().getY());
        g.fill(rect);
    }
}
