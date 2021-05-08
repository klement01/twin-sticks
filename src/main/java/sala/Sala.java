/**
 * Representa uma sala contendo paredes,
 * entradas e inimigos.
 */
package sala;

import static app.Comum.DIMENSOES_QUADRADOS;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import app.Comum.Cardinalidade;
import elemento.Colisoes;
import elemento.Elemento;
import elemento.ator.Ator;
import elemento.ator.Jogador;
import elemento.parede.Parede;
import elemento.parede.ParedePadrao;
import elemento.parede.Porta;

public class Sala {
    /*
     * Caminho para os arquivos de sala.
     */
    private static final String SALAS = "/salas/";

    /*
     * Direções em que a sala pode ser acessada.
     */
    /* private final EnumSet<Cardinalidade> cardinalidades; */

    /*
     * Listas dos elementos contidos na sala.
     */
    private Jogador jogador = null;

    private ArrayList<Elemento> elementos = new ArrayList<Elemento>();
    private ArrayList<Ator> atores = new ArrayList<Ator>();
    private ArrayList<Colisoes> colisores = new ArrayList<Colisoes>();

    /**
     * Inicializa a sala da pasta de salas com o nome especificado.
     */
    public Sala(String nome) throws IOException {
        var matriz = obterMatrizDeArquivo(SALAS + nome);
        processarMatrizDeSala(matriz);
    }

    private enum TiposElemento {
        VAZIO, PAREDE_PADRAO, PORTA;

        private static final EnumSet<TiposElemento> TiposParede = EnumSet.of(PAREDE_PADRAO);

        public static TiposElemento getEnum(String chave) {
            switch (chave) {
                case "#":
                    return PAREDE_PADRAO;
                case "P":
                    return PORTA;
                default:
                    return VAZIO;
            }
        }

        public boolean isParede() {
            return TiposParede.contains(this);
        }
    }

    /**
     * Converte o arquivo em uma matriz 2D contendo todos os caracteres do arquivo.
     */
    private ArrayList<ArrayList<TiposElemento>> obterMatrizDeArquivo(String caminho) throws IOException {
        var matriz = new ArrayList<ArrayList<TiposElemento>>();

        InputStream streamArquivo = getClass().getResourceAsStream(caminho);
        BufferedReader arquivo = new BufferedReader(new InputStreamReader(streamArquivo));
        ;

        String linha;
        while ((linha = arquivo.readLine()) != null) {
            var valoresLinha = Arrays.asList(linha.trim().split(""));
            var linhaProcessada = new ArrayList<TiposElemento>();
            for (var i : valoresLinha) {
                linhaProcessada.add(TiposElemento.getEnum(i));
            }
            matriz.add(linhaProcessada);
        }

        arquivo.close();

        return matriz;
    }

    /**
     * Processa uma matriz representando uma sala, convertendo seus caracteres em
     * objetos do jogo.
     */
    private void processarMatrizDeSala(ArrayList<ArrayList<TiposElemento>> matriz) {
        // Itera sobre todos os elementos.
        for (int i = 0; i < matriz.size(); i++) {
            var linha = matriz.get(i);
            for (int j = 0; j < linha.size(); j++) {
                TiposElemento t = linha.get(j);

                // Determina a posição no grid.
                var posicao = new Point2D.Double(j * DIMENSOES_QUADRADOS.x, i * DIMENSOES_QUADRADOS.y);

                if (t == TiposElemento.PORTA) {
                    // Elemento é uma porta.
                    Porta porta;
                    if (i == 0 || i == matriz.size() - 1) {
                        porta = Porta.portaHorizontal(posicao);
                    } else {
                        porta = Porta.portaVertical(posicao);
                    }
                    registrar(porta);

                } else if (t == TiposElemento.PAREDE_PADRAO) {
                    // Elemento é uma parede padrão.
                    var cardinalidades = determinarCardinalidades(matriz, i, j);
                    var parede = new ParedePadrao(posicao, cardinalidades);
                    registrar(parede);
                }
            }
        }
    }

    /**
     * Determina as direções em que uma parede da matriz na posição especificada
     * deve empurrar.
     */
    private EnumSet<Cardinalidade> determinarCardinalidades(ArrayList<ArrayList<TiposElemento>> matriz, int i, int j) {
        // Determina em que direções a parede deve empurrar, checando se existe
        // paredes, acima, abaixo e aos lados.
        var cardinalidades = new ArrayList<Cardinalidade>();
        if (i > 0 && !matriz.get(i - 1).get(j).isParede()) {
            cardinalidades.add(Cardinalidade.NORTE);
        }
        if (i < matriz.size() - 1 && !matriz.get(i + 1).get(j).isParede()) {
            cardinalidades.add(Cardinalidade.SUL);
        }
        if (j > 0 && !matriz.get(i).get(j - 1).isParede()) {
            cardinalidades.add(Cardinalidade.OESTE);
        }
        if (j < matriz.get(i).size() - 1 && !matriz.get(i).get(j + 1).isParede()) {
            cardinalidades.add(Cardinalidade.LESTE);
        }
        // Cria um conjunto com as cardinalidades adequadas.
        if (cardinalidades.size() == 0) {
            return EnumSet.noneOf(Cardinalidade.class);
        }
        return EnumSet.copyOf(cardinalidades);
    }

    /**
     * Métodos para adicionar e remover objetos nas arrays adequadas.
     */
    public void registrarJogador(Jogador a) {
        registrar(a);
        this.jogador = a;
    }

    private void registrar(Ator a) {
        atores.add(a);
        colisores.add(a);
        elementos.add(a);
    }

    private void registrar(Parede a) {
        colisores.add(a);
        elementos.add(a);
    }

    public void removerJogador(Jogador a) {
        remover(a);
        this.jogador = null;
    }

    private void remover(Ator a) {
        atores.remove(a);
        colisores.remove(a);
        elementos.remove(a);
    }

    private void remover(Parede a) {
        colisores.remove(a);
        elementos.remove(a);
    }

    /**
     * Atualiza os elementos que devem ser atualizados e resolve colisões.
     */
    public void atualizar(double dt) {
        for (var i : atores) {
            var vivo = i.atualizar(dt);
            if (!vivo) {
                remover(i);
            }
        }
        for (int i = 0; i < colisores.size() - 1; i++) {
            for (int j = i + 1; j < colisores.size(); j++) {
                colisores.get(i).resolverColisaoCom(colisores.get(j));
            }
        }
    }

    public void desenhar(Point2D.Double camera, Graphics2D g) {
        // TODO: manipular câmera para permitir scrolling.

        /**
         * Desenha todos os elementos na tela.
         */
        for (var i : elementos) {
            i.desenhar(camera, g);
        }
    }
}
