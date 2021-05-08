/**
 * Implementa o loop principal de atualização e renderização
 * dos elementos do jogo.
 */
package main.java.app;

import static main.java.app.Comum.DIMENSOES_CAMPO;
import static main.java.app.Comum.FPS_ALVO;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.Timer;

import main.java.elemento.ator.Jogador;
import main.java.sala.Sala;

@SuppressWarnings("serial")
class Controlador extends JPanel {
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
        var executarFrame = new ActionListener() {
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

    // Objeto do jogador.
    private Jogador jogador;

    // TODO: remover sala de teste.
    private Sala sala;

    private void inicializarObjetos() {
        this.camera = new Point2D.Double(0, 0);
        this.jogador = new Jogador(
                new Point2D.Double(DIMENSOES_CAMPO.getWidth() / 2 - 30, DIMENSOES_CAMPO.getHeight() / 2 - 30), this);
        try {
            this.sala = new Sala("sala_teste.txt");
        } catch (IOException e) {
            System.err.println("Erro carregando arquivo: " + e);
            System.exit(1);
        }
        this.sala.registrarJogador(jogador);
    }

    private void atualizar(double dt) {
        // TODO: remover sala teste.
        sala.atualizar(dt);

        // Redesenha a cena.
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Muda a cor de fundo.
        this.setBackground(Color.BLACK);

        // Converte o gráfico para um gráfico 2D.
        var g2d = (Graphics2D) g;

        // Configura as qualidades do gráfico.
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // TODO: remover sala teste.
        sala.desenhar(camera, g2d);

        // Sincroniza o estado dos gráficos, garantindo que
        // a imagem mostrada seja atual. Útil para animação.
        // <https://docs.oracle.com/javase/7/docs/api/java/awt/Toolkit.html#sync()>
        Toolkit.getDefaultToolkit().sync();
    }
}
