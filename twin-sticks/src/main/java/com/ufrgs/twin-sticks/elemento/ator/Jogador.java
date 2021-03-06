/**
 * Representa o ator controlado pelo jogador.
 */
package elemento.ator;

import static app.Comum.Cardinalidade;
import static app.Comum.DIMENSOES_CAMPO;
import static app.Comum.DIMENSOES_QUADRADOS;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.floor;
import static java.lang.Math.hypot;

import elemento.HUD;
import elemento.ator.inimigo.Inimigo;
import elemento.ator.projetil.ProjJogador;
import elemento.ator.projetil.Projetil;
import elemento.parede.Parede;
import elemento.parede.Porta;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;

import javax.swing.JPanel;

public class Jogador extends Ator implements KeyListener {
    // Constantes do jogador.
    private static final int VIDA_MAX = 6;
    private static final Point2D.Double DIMENSOES = new Point2D.Double(29, 29);
    private static final double VELOCIDADE_MIN = 10;
    private static final double VELOCIDADE_MAX = 175;
    private static final double ACELERACAO_MAX = 700;
    private static final double ESCALA_ATRITO = 5;
    private static final double ATAQUE_PERIODO = 0.25;
    private static final double ATAQUE_PESO_VELOCIDADE = 0.3;
    private static final double ANIMACAO_DIST_MAX = 10;

    // Variáveis do jogador.
    private int vida;
    private Point2D.Double velocidade;
    private Point2D.Double orientacaoCanhao;
    private Point2D.Double orientacaoTanque;
    private Point2D.Double aceleraçãoJogador;
    private LinkedHashSet<Tecla> filaDeEntradas = new LinkedHashSet<Tecla>();
    private double ataqueTimer;
    private double animacaoDist;
    private Cardinalidade transicao;
    private HUD hud;

    public Jogador(JPanel raiz) {
        super(new Point2D.Double(), Jogador.DIMENSOES);

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
        this.orientacaoTanque = new Point2D.Double(0, 1);
        this.orientacaoCanhao = new Point2D.Double(0, 1);
        this.aceleraçãoJogador = new Point2D.Double(0, 1);
        this.ataqueTimer = 0;
        this.animacaoDist = 0;
        this.transicao = null;
        this.hud = new HUD();
        this.setPosicao(Cardinalidade.CENTRO);
    }

    // Move o jogador para um local pré-definido do campo.
    public void setPosicao(Cardinalidade posicao) {
        // Offset causado pelo tamanho do tanque.
        double offX = (DIMENSOES_QUADRADOS.getX() - this.getDimensoes().getX()) / 2;
        double offY = (DIMENSOES_QUADRADOS.getY() - this.getDimensoes().getY()) / 2;

        // Posição central.
        double posX =
                DIMENSOES_QUADRADOS.getX()
                        * floor((DIMENSOES_CAMPO.width / 2) / DIMENSOES_QUADRADOS.getX());
        double posY =
                DIMENSOES_QUADRADOS.getY()
                        * floor((DIMENSOES_CAMPO.height / 2) / DIMENSOES_QUADRADOS.getY());

        // Offsets causados pela posição do tanque dentro
        // da sala.
        double OFF_X =
                DIMENSOES_QUADRADOS.getX()
                        * (floor((DIMENSOES_CAMPO.width / 2) / DIMENSOES_QUADRADOS.getX()) - 1);
        double OFF_Y =
                DIMENSOES_QUADRADOS.getY()
                        * (floor((DIMENSOES_CAMPO.height / 2) / DIMENSOES_QUADRADOS.getY()) - 1);
        switch (posicao) {
            case NORTE:
                offY -= OFF_Y;
                break;
            case SUL:
                offY += OFF_Y;
                break;
            case OESTE:
                offX -= OFF_X;
                break;
            case LESTE:
                offX += OFF_X;
                break;
            default:
                break;
        }

        this.setPosicao(new Point2D.Double(posX + offX, posY + offY));
        this.velocidade.setLocation(0, 0);
    }

    /*
     * Implementa a interface KeyListener para responder às entradas do jogador.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int valor = e.getKeyCode();
        Tecla tecla = Tecla.getTecla(valor);
        if (tecla != null) {
            this.filaDeEntradas.add(tecla);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int valor = e.getKeyCode();
        Tecla tecla = Tecla.getTecla(valor);
        if (tecla != null) {
            this.filaDeEntradas.remove(tecla);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    /*
     * Determina o movimento do jogador, processa seus ataques
     * e os desenha.
     */
    @Override
    public boolean atualizar(double dt) {
        // Processa as colisões e checa se o jogador ainda
        // está vivo.
        this.transicao = null;
        if (!super.atualizar(dt)) {
            return false;
        }

        atualizarPosicao(dt);

        processarAtaques(dt);

        return true;
    }

    private void atualizarPosicao(double dt) {
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
                default:
                    break;
            }
        }
        this.aceleraçãoJogador.setLocation(axJogador, ayJogador);
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
        this.setPosicao(
                new Point2D.Double(
                        this.getPosicao().getX() + vx * dt, this.getPosicao().getY() + vy * dt));
        // Registra a velocidade.
        this.velocidade.setLocation(vx, vy);
        // Determina a distância total percorrida pra fins
        // de animacao.
        this.animacaoDist += hypot(vx, vy) * dt;
    }

    public void processarAtaques(double dt) {
        // Determina a direção do canhão.
        double direcaoX = 0;
        double direcaoY = 0;
        for (var i : filaDeEntradas) {
            switch (i) {
                case ATIRAR_N:
                    direcaoY = -1;
                    break;
                case ATIRAR_S:
                    direcaoY = 1;
                    break;
                case ATIRAR_W:
                    direcaoX = -1;
                    break;
                case ATIRAR_E:
                    direcaoX = 1;
                    break;
                default:
                    break;
            }
        }
        if (direcaoX != 0 || direcaoY != 0) {
            this.orientacaoCanhao.setLocation(direcaoX, direcaoY);
        }

        // Se ataqueTimer for maior que 0, ataque ainda
        // está em cooldown.
        if (ataqueTimer > 0) {
            ataqueTimer -= dt;
        }

        // Se ataqueTimer for menor ou igual a zero,
        // checa se o jogador quer atacar.
        if (ataqueTimer <= 0) {
            // Periodo de ataque é somado ao timer em
            // vez de ataqueTimer = ATAQUE_PERIODO para
            // garantir periodo consistente quando o jogador
            // segurar o botão de tiro.
            if (direcaoX != 0 || direcaoY != 0) {
                ataqueTimer += ATAQUE_PERIODO;
                var orientacao =
                        new Point2D.Double(
                                direcaoX * VELOCIDADE_MAX
                                        + ATAQUE_PESO_VELOCIDADE * velocidade.getX(),
                                direcaoY * VELOCIDADE_MAX
                                        + ATAQUE_PESO_VELOCIDADE * velocidade.getY());
                var projetil = new ProjJogador(getVetorCentro(), orientacao, this);
                addSpawn(projetil);
            } else {
                ataqueTimer = 0;
            }
        }

        return;
    }

    // Termina de resolver uma colisão do frame anterior.
    @Override
    protected void resolverColisaoPassada(Colisao c, double dt) {
        var colisor = c.getColisor();
        // Se o jogador se chocar com uma parede, reseta sua velocidade no
        // componente adequado.
        if (colisor instanceof Parede) {
            if (c.getDeslocamento().getX() != 0) {
                this.velocidade.setLocation(0, this.velocidade.getY());
            }
            if (c.getDeslocamento().getY() != 0) {
                this.velocidade.setLocation(this.velocidade.getX(), 0);
            }
        }
        // Se o jogador se chocar com um projétil, ou
        // inimigo, leva dano.
        if (colisor instanceof Projetil) {
            Projetil p = (Projetil) colisor;
            this.vida -= p.getDano();
        }
        if (colisor instanceof Inimigo) {
            Inimigo i = (Inimigo) colisor;
            this.vida -= i.getDano();
        }
        // Se o jogador entrar em contato com uma porta, muda
        // o valor de transição do jogador.
        if (colisor instanceof Porta) {
            Porta p = (Porta) colisor;
            this.transicao = p.getAcesso();
        }
    }

    // Checha se o objeto ainda está vivo, ou seja, se deve
    // continuar existindo ou se deve ser destruído.
    @Override
    protected boolean atorVivo() {
        return this.vida > 0;
    }

    // Desenha o objeto na tela, levando em consideração a posição
    // da câmera relativa ao número.
    @Override
    public void desenhar(Point2D.Double camera, Graphics2D g) {
        // Se o tanque não estiver parado, atualiza sua orientação
        // de acordo com sua velocidade.
        if (this.aceleraçãoJogador.getX() != 0 || this.aceleraçãoJogador.getY() != 0) {
            this.orientacaoTanque.setLocation(velocidade);
        }

        // Se o tanque se movimentou o suficiente, avança sua animação.
        if (this.animacaoDist >= Jogador.ANIMACAO_DIST_MAX) {
            Grafico.BASE.avancarAnimacao();
            this.animacaoDist -= Jogador.ANIMACAO_DIST_MAX;
        }

        // Calcula a posição do tanque levando a câmera em
        // consideração.
        double px = this.getPosicao().getX() - camera.getX();
        double py = this.getPosicao().getY() - camera.getY();
        var pos = new Point2D.Double(px, py);

        // Desenha a base a o canhão centralizados sobre o tanque.
        Grafico.SOMBRA.desenhar(pos, g, this.orientacaoTanque);
        Grafico.BASE.desenhar(pos, g, this.orientacaoTanque);
        Grafico.CANO.desenhar(pos, g, this.orientacaoCanhao);
    }

    public void desenharHUD(Graphics2D g) {
        this.hud.setVida(this.vida);
        this.hud.desenhar(new Point2D.Double(), g);
    }

    // Retorna a direção de transição.
    public Cardinalidade getTransicao() {
        return this.transicao;
    }

    // Torna a posição do jogador pública.
    @Override
    public Point2D.Double getPosicao() {
        return super.getPosicao();
    }

    /**
     * Carrega os arquivos de gráfico do jogador.
     */
    enum Grafico {
        BASE("base", 4),
        SOMBRA("sombra", 1),
        CANO("cano", 1);

        private static final String CAMINHO = "tanque";
        private static final String EXTENSAO = "png";

        private final ArrayList<grafico.Grafico> grafs;

        private int frame;

        Grafico(String pasta, int numFrames) {
            this.grafs = new ArrayList<grafico.Grafico>();
            for (var i = 1; i <= numFrames; i++) {
                var caminho = String.format("%s/%s/%d.%s", CAMINHO, pasta, i, EXTENSAO);
                grafs.add(grafico.Grafico.carregar(caminho));
            }
            this.frame = 0;
        }

        public void desenhar(Point2D.Double posicao, Graphics2D g, Point2D.Double orientacao) {
            double angulo = atan2(orientacao.getY(), orientacao.getX());
            // Centraliza o elemento sobre a base.
            double px =
                    posicao.getX() + (BASE.getDimensoes().getX() - this.getDimensoes().getX()) / 2;
            double py =
                    posicao.getY() + (BASE.getDimensoes().getY() - this.getDimensoes().getY()) / 2;
            this.grafs.get(this.frame).desenhar(g, new Point2D.Double(px, py), 1, angulo);
        }

        // Avança um frame da animação, recomeçando do início se
        // necessário.
        public void avancarAnimacao() {
            this.frame++;
            if (this.frame >= this.grafs.size()) {
                this.frame = 0;
            }
        }

        // Retorna as dimensoes do frame atual.
        public Point2D.Double getDimensoes() {
            return this.grafs.get(frame).getDimensoes();
        }
    }

    /**
     * Mapeia valores de teclas pressionados pelo usuário para ações do jogo.
     */
    enum Tecla {
        MOVER_N(KeyEvent.VK_W),
        MOVER_E(KeyEvent.VK_D),
        MOVER_S(KeyEvent.VK_S),
        MOVER_W(KeyEvent.VK_A),
        ATIRAR_N(KeyEvent.VK_UP),
        ATIRAR_E(KeyEvent.VK_RIGHT),
        ATIRAR_S(KeyEvent.VK_DOWN),
        ATIRAR_W(KeyEvent.VK_LEFT);

        // Inicializa o mapa após todas as chaves terem sido inicializadas.
        // Baseado em <https://stackoverflow.com/a/536461>.
        private static HashMap<Integer, Tecla> mapa;

        static {
            Tecla.mapa = new HashMap<Integer, Tecla>();
            for (var i : EnumSet.allOf(Tecla.class)) {
                Tecla.mapa.put(i.valor, i);
            }
        }

        private int valor;

        Tecla(int valor) {
            this.valor = valor;
        }

        public static Tecla getTecla(int valor) {
            return Tecla.mapa.get(valor);
        }
    }
}
