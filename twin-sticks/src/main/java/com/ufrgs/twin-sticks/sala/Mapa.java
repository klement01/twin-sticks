/**
 * Monta um andar constituido por salas.
 */
package sala;

import static app.Comum.Cardinalidade;
import static app.Comum.DIMENSOES_CAMPO;

import elemento.ator.Jogador;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class Mapa {
    private static final double PERIODO_TRANSICAO = 1;

    private Node nodeAtivo;
    private Node ultimoNodeAtivo = null;

    private Jogador jogador;

    private double timerTransicao = 0;
    private Point2D.Double offsetCamera = new Point2D.Double();

    public Mapa(Jogador jogador) {
        this.jogador = jogador;

        // Cria o node raiz, que contém uma sala vazia.
        this.nodeAtivo = new Node();

        // Cria o resto do layout do mapa e o popula com salas.
        // TODO: criar layout aleatório.

        Node direita = new Node();
        this.nodeAtivo.setVizinho(Cardinalidade.LESTE, direita);

        Node direita2 = new Node();
        direita.setVizinho(Cardinalidade.LESTE, direita2);

        Node direita3 = new Node();
        direita2.setVizinho(Cardinalidade.LESTE, direita3);

        Node direitaBaixo = new Node();
        direita2.setVizinho(Cardinalidade.SUL, direitaBaixo);

        Node direitaCima = new Node();
        direita2.setVizinho(Cardinalidade.NORTE, direitaCima);

        Node cima = new Node();
        this.nodeAtivo.setVizinho(Cardinalidade.NORTE, cima);

        Node baixo = new Node();
        this.nodeAtivo.setVizinho(Cardinalidade.SUL, baixo);

        Node esquerda = new Node();
        this.nodeAtivo.setVizinho(Cardinalidade.OESTE, esquerda);

        Node esquerdaBaixo = new Node();
        esquerda.setVizinho(Cardinalidade.SUL, esquerdaBaixo);
        baixo.setVizinho(Cardinalidade.OESTE, esquerdaBaixo);

        Node esquerda2 = new Node();
        esquerda.setVizinho(Cardinalidade.OESTE, esquerda2);

        // Popula todos os nós com salas;
        Mapa.popularLayout(this.nodeAtivo);

        // Cria uma sala vazia para o node raiz e passa
        // o controle do jogador para a sala.
        Sala salaBasica = Planta.criarSalaBasicaComAcessos(nodeAtivo.getAcessos());
        this.nodeAtivo.setSala(salaBasica);

        // Passa o controle do jogador para a sala inicial.
        this.nodeAtivo.getSala().registrar(jogador);
    }

    private static void popularLayout(Node node) {
        // Se a sala não é nula, o nó já foi processado.
        if (node.getSala() != null) {
            return;
        }

        // Se a sala é nula, cria uma sala e processa
        // os nós vizinhos.
        var acessos = node.getAcessos();
        var sala = Planta.criarSalaComAcessos(acessos);
        node.setSala(sala);
        for (var i : acessos) {
            Mapa.popularLayout(node.getVizinho(i));
        }
    }

    public void atualizar(double dt) {
        // Se uma transição entre salas estiver em efeito,
        // não atualiza nenhuma delas.
        if (timerTransicao != 0) {
            timerTransicao += dt;
            if (timerTransicao >= PERIODO_TRANSICAO) {
                timerTransicao = 0;
                this.nodeAtivo.getSala().registrar(jogador);
            }
            return;
        }

        // Atualiza a sala ativa e checa se houve uma transição
        // entre salas.
        Node nodeAtivo = this.nodeAtivo;
        Sala salaAtiva = nodeAtivo.getSala();
        salaAtiva.atualizar(dt);
        Cardinalidade transicao = salaAtiva.getTransicao();

        // Se o jogador tocar uma porta após ter derrotado
        // todos os inimigos da sala, inicia a transição.
        if (transicao != null) {
            // Remove o controle do jogador da sala antiga.
            salaAtiva.remover(jogador);
            this.ultimoNodeAtivo = nodeAtivo;
            this.ultimoNodeAtivo.getSala().finalizar();
            this.nodeAtivo = nodeAtivo.getVizinho(transicao);

            // Move o jogador para a posição inicial ao
            // lado da porta.
            this.jogador.setPosicao(transicao.oposto());

            // Determina o offset da câmera baseado na
            // direção de transição.
            double offX = 0;
            double offY = 0;
            switch (transicao) {
                case NORTE:
                    offY = DIMENSOES_CAMPO.height;
                    break;
                case SUL:
                    offY = -DIMENSOES_CAMPO.height;
                    break;
                case OESTE:
                    offX = DIMENSOES_CAMPO.width;
                    break;
                case LESTE:
                    offX = -DIMENSOES_CAMPO.width;
                    break;
                default:
                    break;
            }
            this.offsetCamera.setLocation(offX, offY);

            // Inicia o timer de transição.
            timerTransicao += dt;
        }
    }

    public void desenhar(Graphics2D g) {
        Point2D.Double camera = new Point2D.Double(0, 0);

        // Durante uma transição entre salas, desenha
        // a última sala ativa e muda a posição da câmera.
        if (timerTransicao != 0) {
            double escala = this.timerTransicao / Mapa.PERIODO_TRANSICAO;

            // Primeiro, determina a posição da câmera para a sala
            // antiga.
            double cx = -this.offsetCamera.getX() * escala;
            double cy = -this.offsetCamera.getY() * escala;
            camera.setLocation(cx, cy);
            this.ultimoNodeAtivo.getSala().desenhar(camera, g);

            // Depois, determina a posição da câmera para a sala
            // atual.
            cx = this.offsetCamera.getX() * (1 - escala);
            cy = this.offsetCamera.getY() * (1 - escala);
            camera.setLocation(cx, cy);
        }

        // Desenha a sala ativa.
        this.nodeAtivo.getSala().desenhar(camera, g);

        // Desenha os elementos da HUD.
        this.jogador.desenharHUD(g);
    }
}
