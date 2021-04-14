/**
 * Implementa o loop principal de atualização e renderização
 * dos elementos do jogo.
 */
package app;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.Timer;

// TODO: remover teste.
import java.util.ArrayList;
import elemento.Elemento;
import elemento.ator.Ator;
import elemento.ator.Jogador;
import elemento.parede.Parede;
import elemento.parede.ParedeTeste;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.Math;
import elemento.Colisoes;
import java.util.EnumSet;
import static elemento.parede.Parede.Cardinalidade;

class Controlador extends JPanel {
    // Constantes do jogo.
    private static final Dimension DIMENSOES_CAMPO = new Dimension(768, 432);

    private static final int FPS_ALVO = 60;

    Controlador() {
        // Muda o tamanho da área de jogo.
        setPreferredSize(DIMENSOES_CAMPO);

        // Torna a janela focável e solicita foco.
        setFocusable(true);
        requestFocusInWindow();

        // Inicializa os objetos do jogo.
        inicializarObjetos();

        // Atualiza o jogo em intervalos definidos, tentando
        // manter o FPS alvo. Baseado em snippet da documentação do Java:
        // <https://docs.oracle.com/javase/7/docs/api/javax/swing/Timer.html>
        var executarFrame =
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // dt é medido em segundos.
                        atualizar(1.0d / FPS_ALVO);
                    }
                };
        // delay do timer é medido em milissegundos.
        var timer = new Timer(1000 / FPS_ALVO, executarFrame);
        timer.start();
    }

    // Posição da camera em relação ao mundo.
    private Point2D.Double camera;

    // TODO: delegar posse dos elementos para sala.
    private Jogador jogador;
    private ArrayList<Elemento> elementos = new ArrayList<Elemento>();
    private ArrayList<Ator> atores = new ArrayList<Ator>();
    private ArrayList<Colisoes> colisores = new ArrayList<Colisoes>();

    private void inicializarObjetos() {
        this.camera = new Point2D.Double(0, 0);

        // TODO: delegar posse dos elementos para sala.

        // Cria o jogador.
        this.jogador = new Jogador(new Point2D.Double(
                    DIMENSOES_CAMPO.getWidth()/2 - 30, DIMENSOES_CAMPO.getHeight()/2 - 30), this);
        elementos.add(this.jogador);
        atores.add(this.jogador);
        colisores.add(this.jogador);

        // Cria as paredes.
        int size = 48;

        // | esquerdo
        var dim = new Point2D.Double(size, size);
        ParedeTeste parede = new ParedeTeste(
                new Point2D.Double(0, 0),
                dim,
                EnumSet.of(Cardinalidade.NORTE, Cardinalidade.LESTE, Cardinalidade.OESTE));
        elementos.add(parede);
        colisores.add(parede);
        parede = new ParedeTeste(
                new Point2D.Double(0, size),
                dim,
                EnumSet.of(Cardinalidade.LESTE, Cardinalidade.OESTE));
        elementos.add(parede);
        colisores.add(parede);
        parede = new ParedeTeste(
                new Point2D.Double(0, 2*size),
                dim,
                EnumSet.of(Cardinalidade.SUL, Cardinalidade.LESTE, Cardinalidade.OESTE));
        elementos.add(parede);
        colisores.add(parede);

        // | direito
        parede = new ParedeTeste(
                new Point2D.Double(2*size, 0),
                dim,
                EnumSet.of(Cardinalidade.NORTE, Cardinalidade.LESTE, Cardinalidade.OESTE));
        elementos.add(parede);
        colisores.add(parede);
        parede = new ParedeTeste(
                new Point2D.Double(2*size, size),
                dim,
                EnumSet.of(Cardinalidade.LESTE, Cardinalidade.OESTE));
        elementos.add(parede);
        colisores.add(parede);
        parede = new ParedeTeste(
                new Point2D.Double(2*size, 2*size),
                dim,
                EnumSet.of(Cardinalidade.SUL, Cardinalidade.LESTE, Cardinalidade.OESTE));
        elementos.add(parede);
        colisores.add(parede);

        // L esquerdo
        parede = new ParedeTeste(
                new Point2D.Double(0, 7*size),
                dim,
                EnumSet.of(Cardinalidade.NORTE, Cardinalidade.LESTE, Cardinalidade.OESTE));
        elementos.add(parede);
        colisores.add(parede);
        parede = new ParedeTeste(
                new Point2D.Double(0, 8*size),
                dim,
                EnumSet.of(Cardinalidade.OESTE, Cardinalidade.SUL));
        elementos.add(parede);
        colisores.add(parede);
        parede = new ParedeTeste(
                new Point2D.Double(size, 8*size),
                dim,
                EnumSet.of(Cardinalidade.SUL, Cardinalidade.NORTE));
        elementos.add(parede);
        colisores.add(parede);
        parede = new ParedeTeste(
                new Point2D.Double(2*size, 8*size),
                dim,
                EnumSet.of(Cardinalidade.SUL, Cardinalidade.LESTE, Cardinalidade.NORTE));
        elementos.add(parede);
        colisores.add(parede);

        // h central
        parede = new ParedeTeste(
                new Point2D.Double(6*size, 0),
                dim,
                EnumSet.of(Cardinalidade.NORTE, Cardinalidade.LESTE, Cardinalidade.OESTE));
        elementos.add(parede);
        colisores.add(parede);
        parede = new ParedeTeste(
                new Point2D.Double(6*size, size),
                dim,
                EnumSet.of(Cardinalidade.OESTE));
        elementos.add(parede);
        colisores.add(parede);
        parede = new ParedeTeste(
                new Point2D.Double(6*size, 2*size),
                dim,
                EnumSet.of(Cardinalidade.SUL, Cardinalidade.OESTE, Cardinalidade.LESTE));
        elementos.add(parede);
        colisores.add(parede);
        parede = new ParedeTeste(
                new Point2D.Double(7*size, size),
                dim,
                EnumSet.of(Cardinalidade.SUL, Cardinalidade.NORTE));
        elementos.add(parede);
        colisores.add(parede);
        parede = new ParedeTeste(
                new Point2D.Double(8*size, size),
                dim,
                EnumSet.of(Cardinalidade.LESTE,Cardinalidade.NORTE));
        elementos.add(parede);
        colisores.add(parede);
        parede = new ParedeTeste(
                new Point2D.Double(8*size, 2*size),
                dim,
                EnumSet.of(Cardinalidade.SUL, Cardinalidade.OESTE, Cardinalidade.LESTE));
        elementos.add(parede);
        colisores.add(parede);

        // monobloco
        parede = new ParedeTeste(
                new Point2D.Double(10*size, 4*size),
                dim);
        elementos.add(parede);
        colisores.add(parede);

        // | pequeno
        size = 30;
        dim = new Point2D.Double(size, size);
        parede = new ParedeTeste(
                new Point2D.Double(8*size, 9*size),
                dim,
                EnumSet.of(Cardinalidade.NORTE, Cardinalidade.LESTE, Cardinalidade.OESTE));
        elementos.add(parede);
        colisores.add(parede);
        parede = new ParedeTeste(
                new Point2D.Double(8*size, 10*size),
                dim,
                EnumSet.of(Cardinalidade.LESTE, Cardinalidade.OESTE));
        elementos.add(parede);
        colisores.add(parede);
        parede = new ParedeTeste(
                new Point2D.Double(8*size, 11*size),
                dim,
                EnumSet.of(Cardinalidade.SUL, Cardinalidade.LESTE, Cardinalidade.OESTE));
        elementos.add(parede);
        colisores.add(parede);

        // | grande
        size = 70;
        dim = new Point2D.Double(size, size);
        parede = new ParedeTeste(
                new Point2D.Double(7*size, 6*size),
                dim,
                EnumSet.of(Cardinalidade.NORTE, Cardinalidade.LESTE, Cardinalidade.OESTE));
        elementos.add(parede);
        colisores.add(parede);
        parede = new ParedeTeste(
                new Point2D.Double(7*size, 7*size),
                dim,
                EnumSet.of(Cardinalidade.LESTE, Cardinalidade.OESTE));
        elementos.add(parede);
        colisores.add(parede);
        parede = new ParedeTeste(
                new Point2D.Double(7*size, 8*size),
                dim,
                EnumSet.of(Cardinalidade.SUL, Cardinalidade.LESTE, Cardinalidade.OESTE));
        elementos.add(parede);
        colisores.add(parede);
    }

    private void atualizar(double dt) {
        // TODO: delegar responsabilidade de atualização para
        // as salas.
        for (var i : atores) {
            i.atualizar(dt);
        }
        for (int i = 0; i < colisores.size() - 1; i++) {
            for (int j = i + 1; j < colisores.size(); j++) {
                colisores.get(i).resolverColisaoCom(colisores.get(j));
            }
        }

        // TODO: tirar isso. Muda a câmera para seguir
        // o jogador.
        var centroJogador = jogador.getVetorCentro();
        this.camera = new Point2D.Double(
                centroJogador.getX() - DIMENSOES_CAMPO.getWidth() / 2,
                centroJogador.getY() - DIMENSOES_CAMPO.getHeight() / 2);


        // Redesenha a cena.
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Muda a cor de fundo.
        this.setBackground(Color.BLACK);

        // Converte o gráfico para um gráfico 2D.
        var g2d = (Graphics2D)g;

        // Configura as qualidades do gráfico.
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // TODO: delegar responsabilidade de renderização
        // para sala.
        for (var i : elementos) {
            i.desenhar(this.camera, g2d);
        }

        // Sincroniza o estado dos gráficos, garantindo que
        // a imagem mostrada seja atual. Útil para animação.
        // <https://docs.oracle.com/javase/7/docs/api/java/awt/Toolkit.html#sync()>
        Toolkit.getDefaultToolkit().sync();
    }
}
