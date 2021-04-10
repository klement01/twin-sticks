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

// Teste.
import java.util.ArrayList;
import elemento.Elemento;
import elemento.parede.ParedeTeste;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.Math;

class Controlador extends JPanel {
    // Constantes do jogo.
    private static final Dimension DIMENSAO = new Dimension(768, 432);

    private static final int FPS_ALVO = 60;

    Controlador() {
        // Muda o tamanho da área de jogo.
        setPreferredSize(DIMENSAO);

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

    // Paredes teste.
    private ArrayList<Elemento> elementos = new ArrayList<Elemento>();

    private void inicializarObjetos() {
        this.camera = new Point2D.Double(0, 0);

        // Paredes teste.
        var size = 48;
        var dimensoes = new Point2D.Double(size, size);

        elementos.add(new ParedeTeste(new Point2D.Double(size, size), dimensoes));
        elementos.add(new ParedeTeste(new Point2D.Double(2*size, size), dimensoes));
        elementos.add(new ParedeTeste(new Point2D.Double(size, 2*size), dimensoes));
        elementos.add(new ParedeTeste(new Point2D.Double(size, 3*size), dimensoes));
        elementos.add(new ParedeTeste(new Point2D.Double(2*size, 3*size), dimensoes));
        elementos.add(new ParedeTeste(new Point2D.Double(2*size, 4*size), dimensoes));
        elementos.add(new ParedeTeste(new Point2D.Double(2*size, 6*size), dimensoes));
        elementos.add(new ParedeTeste(new Point2D.Double(4*size, 6*size), dimensoes));
    }

    private void atualizar(double dt) {
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

        // Desenha todos os elementos gráficos.
        for (var i : elementos) {
            i.desenhar(this.camera, g2d);
        }

        // Sincroniza o estado dos gráficos, garantindo que
        // a imagem mostrada seja atual. Útil para animação.
        // <https://docs.oracle.com/javase/7/docs/api/java/awt/Toolkit.html#sync()>
        Toolkit.getDefaultToolkit().sync();
    }
}
