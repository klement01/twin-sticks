package elemento.ator.inimigo;

import static java.lang.Math.atan2;

import elemento.ator.projetil.ProjSniper;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public final class Sniper extends Inimigo {
    // Constantes.
    private static final Point2D.Double DIMENSOES = new Point2D.Double(35, 35);
    private static final int MAX_VIDA = 3;
    private static final int DANO_DE_CONTATO = 0;
    private static final double TIRO_PERIODO = 1;

    // Variáveis.
    private int vida = Sniper.MAX_VIDA;
    private Point2D.Double orientacaoCano = new Point2D.Double();
    private double tiroTimer = 0;

    public Sniper(Point2D.Double posicao) {
        super(centralizar(posicao, Sniper.DIMENSOES), Sniper.DIMENSOES);
    }

    @Override
    public int getDano() {
        return Sniper.DANO_DE_CONTATO;
    }

    @Override
    protected int getVida() {
        return this.vida;
    }

    @Override
    protected void setVida(int novaVida) {
        this.vida = novaVida;
    }

    @Override
    public boolean atualizar(double dt) {
        if (!super.atualizar(dt)) {
            return false;
        }

        // Determina a orientação do cano.
        double vx = this.getPosJogador().getX() - this.getPosicao().getX();
        double vy = this.getPosJogador().getY() - this.getPosicao().getY();
        this.orientacaoCano.setLocation(vx, vy);

        // Atira numa cadência fixa.
        this.tiroTimer += dt;
        if (this.tiroTimer >= Sniper.TIRO_PERIODO) {
            this.tiroTimer -= Sniper.TIRO_PERIODO;

            var proj = new ProjSniper(this.getVetorCentro(), this.orientacaoCano, this);
            this.addSpawn(proj);
        }

        return true;
    }

    @Override
    public void desenhar(Point2D.Double camera, Graphics2D g) {
        double px = this.getPosicao().getX() - camera.getX();
        double py = this.getPosicao().getY() - camera.getY();
        var pos = new Point2D.Double(px, py);

        Grafico.BASE.desenhar(pos, g);
        Grafico.CANO.desenhar(pos, g, this.orientacaoCano);
    }

    @Override
    public void empurrar(Point2D.Double d) {}
}

/**
 * Carrega os arquivos de gráfico do sniper.
 */
enum Grafico {
    BASE("base.png"),
    CANO("cano.png");

    private static final String CAMINHO = "sniper/";

    private final grafico.Grafico graf;

    Grafico(String nome) {
        this.graf = grafico.Grafico.carregar(CAMINHO + nome);
    }

    public void desenhar(Point2D.Double posicao, Graphics2D g) {
        desenhar(posicao, g, new Point2D.Double(1, 0));
    }

    public void desenhar(Point2D.Double posicao, Graphics2D g, Point2D.Double orientacao) {
        double angulo = atan2(orientacao.getY(), orientacao.getX());
        // Centraliza o elemento sobre a base.
        double px = posicao.getX() + (BASE.getDimensoes().getX() - this.getDimensoes().getX()) / 2;
        double py = posicao.getY() + (BASE.getDimensoes().getY() - this.getDimensoes().getY()) / 2;
        this.graf.desenhar(g, new Point2D.Double(px, py), 1, angulo);
    }

    // Retorna as dimensoes do frame atual.
    public Point2D.Double getDimensoes() {
        return this.graf.getDimensoes();
    }
}
