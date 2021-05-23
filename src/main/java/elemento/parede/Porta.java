/**
 * Parede que pode ser instanciada, usada para testes.
 */
package elemento.parede;

import static app.Comum.DIMENSOES_QUADRADOS;

import static java.lang.Math.PI;
import static java.lang.Math.floor;
import static java.lang.Math.min;
import static java.lang.Math.round;

import app.Comum.Cardinalidade;

import grafico.Grafico;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Porta extends Parede {
    private static final Point2D.Double DIMENSOES = DIMENSOES_QUADRADOS;
    private static final String CAMINHO = "porta/";
    private static final double PERIODO_ANIMACAO = 0.8;
    private static final int NUM_FRAMES = 6;
    private static final ArrayList<Grafico> GRAFICOS = Porta.carregarGraficos();

    private final Cardinalidade acesso;

    private double timerAnimacao = 0;

    public Porta(Point2D.Double posicao, Cardinalidade acesso) {
        super(posicao, Porta.DIMENSOES);
        this.acesso = acesso;
    }

    /*
     * Carrega os quadros de animação da porta.
     */
    private static ArrayList<Grafico> carregarGraficos() {
        var graficos = new ArrayList<Grafico>();
        for (int i = 1; i <= Porta.NUM_FRAMES; i++) {
            var grafico = Grafico.carregar(Porta.CAMINHO + i + ".png");
            graficos.add(grafico);
        }
        return graficos;
    }

    /*
     * Retorna a direção de acesso da porta.
     */
    public Cardinalidade getAcesso() {
        return this.acesso;
    }

    @Override
    public void desenhar(Point2D.Double camera, Graphics2D g) {
        // Determina a posição da porta baseado na câmera.
        double cx = this.getPosicao().getX() - camera.getX();
        double cy = this.getPosicao().getY() - camera.getY();
        var pos = new Point2D.Double(cx, cy);

        // Determina qual frame mostrar.
        int i = (int) round(floor(Porta.NUM_FRAMES * this.timerAnimacao / Porta.PERIODO_ANIMACAO));
        i = min(Porta.NUM_FRAMES - 1, i);

        // Determina se a porta deve estar inclinada.
        double angulo = 0;
        if (this.getAcesso() == Cardinalidade.SUL || this.getAcesso() == Cardinalidade.NORTE) {
            angulo = PI / 2;
        }

        Porta.GRAFICOS.get(i).desenhar(g, pos, 1, angulo);
    }

    public void avancarAnimacao(double dt) {
        this.timerAnimacao += dt;
        if (this.timerAnimacao > Porta.PERIODO_ANIMACAO) {
            this.timerAnimacao = Porta.PERIODO_ANIMACAO;
        }
    }

    public void completarAnimacao() {
        this.timerAnimacao = Porta.PERIODO_ANIMACAO;
    }
}
