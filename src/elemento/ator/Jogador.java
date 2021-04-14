/**
 * Representa o ator controlado pelo jogador.
 */
package elemento.ator;

import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.LinkedHashSet;

// TODO: gráficos para o jogador.
import java.awt.Color;
import java.awt.geom.Rectangle2D;

public class Jogador extends Ator implements KeyListener {
    // Constantes do jogador.
    private static final int MAX_VIDA = 3;

    private static final Point2D.Double DIMENSOES = new Point2D.Double(48, 48);

    public enum Tecla {
        MOVER_N (KeyEvent.VK_W),
        MOVER_E (KeyEvent.VK_D),
        MOVER_S (KeyEvent.VK_S),
        MOVER_W (KeyEvent.VK_A);

        private int valor;
        Tecla(int valor) {
            this.valor = valor;
        }
        public int getValor() {
            return valor;
        }
    }

    // Variáveis do jogador.
    private int vida;

    private Point2D.Double velocidade;

    private Point2D.Double aceleracao;

    private LinkedHashSet<Tecla> filaDeEntradas = new LinkedHashSet<Tecla>();

    public Jogador(Point2D.Double posicao, JPanel raiz) {
        super(posicao, Jogador.DIMENSOES);

        // Registra a resposta de entradas do jogador no
        // componente raiz do jogo.
        raiz.addKeyListener(this);

        // Inicializa o jogador.
        this.condicoesIniciais();
    }

    // Configura o estado inicial do jogador.
    private void condicoesIniciais() {
        this.vida = Jogador.MAX_VIDA;
        this.velocidade = new Point2D.Double();
        this.aceleracao = new Point2D.Double();
    }

    /*
     * Implementa a interface KeyListener para responde às
     * entradas do jogador.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();
        for (var i : Tecla.values()) {
            if (tecla == i.getValor()) {
                this.filaDeEntradas.add(i);
                return;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int tecla = e.getKeyCode();
        for (var i : Tecla.values()) {
            if (tecla == i.getValor()) {
                this.filaDeEntradas.remove(i);
                return;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // Por padrão, processa todas as colisões não concluídas e
    // apaga a lista de colisões para processar. Em seguida,
    // checa se o ator continua vivo.
    @Override
    public boolean atualizar(double dt) {
        // Processa as colisões e checa se o jogador ainda
        // está vivo.
        if (!super.atualizar(dt)) {
            return false;
        }

        // TODO: fazer entradas causar aceleração, não
        // velocidade imediata.
        final int V_MAX = 100;
        int vx = 0;
        int vy = 0;
        for (var i : filaDeEntradas) {
            switch (i) {
                case MOVER_N:
                    vy = -V_MAX;
                    break;
                case MOVER_S:
                    vy = V_MAX;
                    break;
                case MOVER_W:
                    vx = -V_MAX;
                    break;
                case MOVER_E:
                    vx = V_MAX;
                    break;
            }
        }
        this.velocidade.setLocation(vx, vy);

        // TODO: calcular a velocidade baseada na aceleração.

        // Adiciona a velocidade à posição.
        this.setPosicao(new Point2D.Double(
                this.getPosicao().getX() + this.velocidade.getX() * dt,
                this.getPosicao().getY() + this.velocidade.getY() * dt));

        return true;
    }

    // Termina de resolver uma colisão do frame anterior.
    // TODO: determinar efeitos de colisões.
    @Override
    protected void resolverColisaoPassada(Colisao c, double dt) {}

    // Checha se o objeto ainda está vivo, ou seja, se deve
    // continuar existindo ou se deve ser destruído.
    @Override
    protected boolean atorVivo() {
        return this.vida > 0;
    }

    // Desenha o objeto na tela, levando em consideração a posição
    // da câmera relativa ao número.
    // TODO: gráficos para o jogador.
    @Override
    public void desenhar(Point2D.Double camera, Graphics2D g) {
        g.setColor(Color.RED);
        var rect =
                new Rectangle2D.Double(
                        this.getPosicao().getX() - camera.getX(),
                        this.getPosicao().getY() - camera.getY(),
                        this.getDimensoes().getX(),
                        this.getDimensoes().getY());
        g.fill(rect);
    }
}
