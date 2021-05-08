/**
 * Universidade Federal do Rio Grande do Sul
 * Programação Orientada a Objetos (2020/2)
 * Trabalho Final
 *
 * Grupo:
 * Gabriel Sadigursky Nunes Cunha
 * Lucas Klement Reinehr
 * Yasmin Rodrigues Koppenhagen
 *
 * =========================================
 *
 * Cria a janela principal do jogo e inicia
 * seu controlador.
 */
package app;

import java.awt.EventQueue;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class App extends JFrame {
    private static final String TITULO = app.Comum.TITULO;

    public static void main(String argv[]) {
        // Ativa o modo OpenGL para usar aceleração por hardware.
        // <https://docs.oracle.com/javase/7/docs/technotes/guides/2d/flags.html#opengl>
        System.setProperty("sun.java2d.opengl", "true");

        // Cria uma janela e a torna visível.
        EventQueue.invokeLater(() -> {
            var jogo = new App();
            jogo.setVisible(true);
        });
    }

    private App() {
        // Cria uma janela e adiciona um título.
        super(TITULO);

        // Torna a dimensão da janela estática.
        setResizable(false);

        // Muda o comportamento da janela quando ela é fechada.
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Cria um controlador do jogo e o adiciona à fila
        // de renderização.
        setContentPane(new Controlador());

        // Ajusta o tamanho da janela para acomodar a área
        // do jogo.
        pack();

        // Centra a janela.
        setLocationRelativeTo(null);
    }
}
