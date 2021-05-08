/**
 * Representa o ator controlado pelo jogador.
 */
package main.java.elemento.ator;

import static java.lang.Math.abs;
import static java.lang.Math.hypot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedHashSet;

import javax.swing.JPanel;

import main.java.elemento.parede.Parede;
import main.java.sala.Sala;

public class Jogador extends Ator implements KeyListener {
    // Constantes do jogador.
    private static final int VIDA_MAX = 6;
    private static final Point2D.Double DIMENSOES = main.java.app.Comum.DIMENSOES_QUADRADOS;
    private static final double VELOCIDADE_MIN = 10;
    private static final double VELOCIDADE_MAX = 175;
    private static final double ACELERACAO_MAX = 1250;
    private static final double ESCALA_ATRITO = 10;

    public enum Tecla {
        MOVER_N(KeyEvent.VK_W), MOVER_E(KeyEvent.VK_D), MOVER_S(KeyEvent.VK_S), MOVER_W(KeyEvent.VK_A);

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
    private Sala sala;
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
        this.vida = Jogador.VIDA_MAX;
        this.velocidade = new Point2D.Double(0, 0);
        this.sala = null;
    }

    /*
     * Implementa a interface KeyListener para responder às entradas do jogador.
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
    public void keyTyped(KeyEvent e) {
    }

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

        // Determina a aceleração causada pelo jogador.
        double axAtrito = 0;
        double ayAtrito = 0;
        double axJogador = 0;
        double ayJogador = 0;
        for (var i : filaDeEntradas) {
            switch (i) {
                case MOVER_N:
                    ayJogador = -ACELERACAO_MAX;
                    break;
                case MOVER_S:
                    ayJogador = ACELERACAO_MAX;
                    break;
                case MOVER_W:
                    axJogador = -ACELERACAO_MAX;
                    break;
                case MOVER_E:
                    axJogador = ACELERACAO_MAX;
                    break;
            }
        }
        // Calcula a aceleração causada por atrito.
        if (axJogador == 0) {
            axAtrito = -this.velocidade.getX() * ESCALA_ATRITO;
        }
        if (ayJogador == 0) {
            ayAtrito = -this.velocidade.getY() * ESCALA_ATRITO;
        }
        // Calcula a aceleração total.
        double ax = axAtrito + axJogador;
        double ay = ayAtrito + ayJogador;
        // Adiciona a aceleração à velocidade enquanto limita
        // ambas a seus valores máximos.
        double escalaAceleracao = abs(ACELERACAO_MAX / hypot(ax, ay));
        if (escalaAceleracao < 1) {
            ax *= escalaAceleracao;
            ay *= escalaAceleracao;
        }
        double vx = this.velocidade.getX() + ax * dt;
        double vy = this.velocidade.getY() + ay * dt;
        double escalaVelocidade = abs(VELOCIDADE_MAX / hypot(vx, vy));
        if (escalaVelocidade < 1) {
            vx *= escalaVelocidade;
            vy *= escalaVelocidade;
        }
        // Normaliza valores pequenos de velocidade.
        if (abs(vx) < VELOCIDADE_MIN && axJogador == 0) {
            vx = 0;
        }
        if (abs(vy) < VELOCIDADE_MIN && ayJogador == 0) {
            vy = 0;
        }
        // Adiciona a velocidade à posição.
        this.setPosicao(new Point2D.Double(this.getPosicao().getX() + vx * dt, this.getPosicao().getY() + vy * dt));
        // Registra a velocidade.
        this.velocidade.setLocation(vx, vy);

        return true;
    }

    // Termina de resolver uma colisão do frame anterior.
    // TODO: determinar efeitos de colisões.
    @Override
    protected void resolverColisaoPassada(Colisao c, double dt) {
        // Se o jogador se chocar com uma parede, reseta sua velocidade no
        // componente adequado.
        if (c.getColisor() instanceof Parede) {
            if (c.getDeslocamento().getX() != 0) {
                this.velocidade.setLocation(0, this.velocidade.getY());
            }
            if (c.getDeslocamento().getY() != 0) {
                this.velocidade.setLocation(this.velocidade.getX(), 0);
            }
        }
    }

    // Checha se o objeto ainda está vivo, ou seja, se deve
    // continuar existindo ou se deve ser destruído.
    @Override
    protected boolean atorVivo() {
        return this.vida > 0;
    }

    // Registra uma sala com o jogar para que ele possa criar novos objetos
    // como projéteis.
    public void registraSala(Sala sala) {
        this.sala = sala;
    }

    // Desenha o objeto na tela, levando em consideração a posição
    // da câmera relativa ao número.
    // TODO: gráficos para o jogador.
    @Override
    public void desenhar(Point2D.Double camera, Graphics2D g) {
        g.setColor(Color.RED);
        var rect = new Rectangle2D.Double(this.getPosicao().getX() - camera.getX(),
                this.getPosicao().getY() - camera.getY(), this.getDimensoes().getX(), this.getDimensoes().getY());
        g.fill(rect);
    }
}
