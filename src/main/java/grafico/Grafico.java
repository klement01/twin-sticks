/**
 * Carrega um gráfico (imagem) e o desenha na tela,
 * podendo executar também transformações lineares.
 */
package grafico;

import static app.Comum.DIMENSOES_QUADRADOS;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class Grafico {
    // Caminho para a pasta de gráficos.
    private static final String GRAFICOS = "/graficos/";

    // Gráfico padrão, usado quando há um erro enquanto um
    // gráfico é carregado.
    private static final Grafico GRAFICO_PADRAO = new Grafico();

    // Transformação identidade, usada quando nenhuma transformação
    // é desejada.
    private static final AffineTransformOp TRANFORMACAO_IDENTIDADE =
            new AffineTransformOp(new AffineTransform(), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

    private BufferedImage imagem;

    // Cria um gráfico baseado em um arquivo.
    public Grafico(String arquivo) throws IOException, IllegalArgumentException {
        InputStream entrada = getClass().getResourceAsStream(Grafico.GRAFICOS + arquivo);
        this.imagem = ImageIO.read(entrada);
    }

    // Retorna um gráfico padrão.
    private Grafico() {
        this.imagem =
                new BufferedImage(
                        (int) DIMENSOES_QUADRADOS.getX(),
                        (int) DIMENSOES_QUADRADOS.getY(),
                        BufferedImage.TYPE_INT_RGB);
        var g = this.imagem.getGraphics();
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, (int) DIMENSOES_QUADRADOS.getX(), (int) DIMENSOES_QUADRADOS.getY());
    }

    // Retorna um gráfico contendo uma imagem arbitrária.
    private Grafico(BufferedImage img) {
        this.imagem = img;
    }

    // Tenta ler um gráfico. Se houver problemas na leitura, usa um gráfico
    // padrão.
    public static Grafico carregar(String arquivo) {
        Grafico g = null;
        try {
            g = new Grafico(arquivo);
        } catch (IOException e) {
            System.err.format("Erro carregando imagem: %s: %s\n", arquivo, e);
            g = Grafico.GRAFICO_PADRAO;
        } catch (IllegalArgumentException e) {
            System.err.format("Argumento ilegal: %s: %s\n", arquivo, e);
            g = Grafico.GRAFICO_PADRAO;
        }
        return g;
    }

    // Retorna as dimensoes do gráfico.
    public Point2D.Double getDimensoes() {
        return new Point2D.Double(imagem.getWidth(), imagem.getHeight());
    }

    // Desenha o gráfico na posição desejada, sem realizar nenhuma transformação.
    public void desenhar(Graphics2D g, Point2D.Double posicao) {
        desenhar(g, posicao, TRANFORMACAO_IDENTIDADE);
    }

    // Desenha o gráfico na posição desejada, mudando sua escala e o girando
    // no sentido anti-horário ao redor do centro.
    public void desenhar(Graphics2D g, Point2D.Double posicao, double escala, double rotacao) {
        var matrizTransformacao = new AffineTransform();
        matrizTransformacao.translate(imagem.getWidth() / 2, imagem.getHeight() / 2);
        matrizTransformacao.rotate(rotacao);
        matrizTransformacao.scale(escala, escala);
        matrizTransformacao.translate(-imagem.getWidth() / 2, -imagem.getHeight() / 2);
        var transformacao =
                new AffineTransformOp(matrizTransformacao, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        desenhar(g, posicao, transformacao);
    }

    // Desenha o gráfico na posição desejada, realizando uma transformação linear
    // arbitrária.
    public void desenhar(Graphics2D g, Point2D.Double posicao, AffineTransformOp transformacao) {
        g.drawImage(this.imagem, transformacao, (int) posicao.getX(), (int) posicao.getY());
    }

    // Seleciona uma região da imagem.
    public void cortarRegiao(int x, int y, int w, int h) {
        this.imagem = this.imagem.getSubimage(x, y, w, h);
    }
}
