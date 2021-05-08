/**
 * Representa uma sala contendo paredes,
 * entradas e inimigos.
 */
package sala;

import static app.Comum.ASSETS;
import static app.Comum.DIMENSOES_QUADRADOS;
import static elemento.parede.Parede.Cardinalidade;

import elemento.Colisoes;
import elemento.Elemento;
import elemento.ator.Ator;
import elemento.ator.Jogador;
import elemento.parede.Parede;
import elemento.parede.ParedeTeste;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

public class Sala {
    private static final Path SALAS = Paths.get(ASSETS.toString(), "salas");

    private ArrayList<Elemento> elementos = new ArrayList<Elemento>();
    private ArrayList<Ator> atores = new ArrayList<Ator>();
    private ArrayList<Colisoes> colisores = new ArrayList<Colisoes>();

    public Sala(String nome) throws IOException {
        /**
         * Inicializa a sala da pasta de salas com o nome especificado.
         */
        this(Paths.get(SALAS.toString(), nome));
    }

    public Sala(Path caminho) throws IOException {
        /**
         * Inicializa a sala usando um arquivo de dados especificado pelo argumento
         * caminho.
         */

        var matriz = obterMatrizDeArquivo(caminho);
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
    private ArrayList<ArrayList<TiposElemento>> obterMatrizDeArquivo(Path caminho) throws IOException {
        var matriz = new ArrayList<ArrayList<TiposElemento>>();

        BufferedReader arquivo = Files.newBufferedReader(caminho);

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
        for (int i = 0; i < matriz.size(); i++) {
            var linha = matriz.get(i);
            for (int j = 0; j < linha.size(); j++) {
                TiposElemento t = linha.get(j);
                var posicao = new Point2D.Double(j * DIMENSOES_QUADRADOS.x, i * DIMENSOES_QUADRADOS.y);
                if (t == TiposElemento.PORTA) {
                    // TODO: Adicionar portas.
                } else if (t == TiposElemento.PAREDE_PADRAO) {
                    var cardinalidades = determinarCardinalidades(matriz, i, j);
                    var parede = new ParedeTeste(posicao, cardinalidades);
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
        if (i > 0 && !matriz.get(i-1).get(j).isParede()) {
            cardinalidades.add(Cardinalidade.NORTE);
        }
        if (i < matriz.size() - 1 && !matriz.get(i+1).get(j).isParede()) {
            cardinalidades.add(Cardinalidade.SUL);
        }
        if (j > 0 && !matriz.get(i).get(j-1).isParede()) {
            cardinalidades.add(Cardinalidade.OESTE);
        }
        if (j < matriz.get(i).size() - 1 && !matriz.get(i).get(j+1).isParede()) {
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

    /**
     * Atualiza os elementos que devem ser atualizados e resolve colisões.
     */
    public void atualizar(double dt) {
        for (var i : atores) {
            i.atualizar(dt);
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
