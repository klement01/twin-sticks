/**
 * Representa um inimigo genérico que tenta atacar
 * o jogador.
 * */
package elemento.ator.inimigo;

import static app.Comum.DIMENSOES_QUADRADOS;

import elemento.ator.Ator;
import elemento.ator.projetil.ProjJogador;

import java.awt.geom.Point2D;

public abstract class Inimigo extends Ator {
    protected Point2D.Double posJogador = new Point2D.Double();

    public Inimigo(Point2D.Double posicao, Point2D.Double dimensoes) {
        super(posicao, dimensoes);
    }

    public final void registrarPosJogador(Point2D.Double posJogador) {
        this.posJogador = posJogador;
    }

    protected static final Point2D.Double centralizar(
            Point2D.Double posicao, Point2D.Double dimensoes) {
        double px = posicao.getX() + (DIMENSOES_QUADRADOS.getX() - dimensoes.getX()) / 2;
        double py = posicao.getY() + (DIMENSOES_QUADRADOS.getY() - dimensoes.getX()) / 2;
        return new Point2D.Double(px, py);
    }

    protected final Point2D.Double getPosJogador() {
        return this.posJogador;
    }

    public abstract int getDano();

    protected abstract int getVida();

    protected abstract void setVida(int novaVida);

    protected final boolean atorVivo() {
        return getVida() > 0;
    }

    @Override
    protected void resolverColisaoPassada(Colisao c, double dt) {
        var colisor = c.getColisor();
        if (colisor instanceof ProjJogador) {
            ProjJogador p = (ProjJogador) colisor;
            this.setVida(this.getVida() - p.getDano());
        }
    }
}
