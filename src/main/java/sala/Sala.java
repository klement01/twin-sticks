/**
 * Representa uma sala contendo paredes,
 * entradas e inimigos.
 */
package sala;

import static app.Comum.DIMENSOES_QUADRADOS;

import app.Comum.Cardinalidade;

import elemento.Chao;
import elemento.Colisoes;
import elemento.Elemento;
import elemento.ator.Ator;
import elemento.ator.Jogador;
import elemento.ator.inimigo.*;
import elemento.ator.projetil.Projetil;
import elemento.parede.*;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.function.BiPredicate;
import java.util.function.IntFunction;

public class Sala {
    /*
     * Direções em que a sala pode ser acessada.
     */
    private EnumSet<Cardinalidade> acessos = null;

    /*
     * Listas dos elementos contidos na sala.
     */
    private Jogador jogador = null;

    private Chao chao = null;

    private ArrayList<Elemento> elementos = new ArrayList<Elemento>();
    private ArrayList<Inimigo> inimigos = new ArrayList<Inimigo>();
    private ArrayList<Ator> atores = new ArrayList<Ator>();
    private ArrayList<Colisoes> colisores = new ArrayList<Colisoes>();
    private ArrayList<Porta> portas = new ArrayList<Porta>();

    /**
     * Inicializa a sala da pasta de salas com o nome especificado.
     */
    public Sala(Planta planta, EnumSet<Cardinalidade> acessos) throws IllegalArgumentException {
        if (!planta.getAcessosPossiveis().containsAll(acessos)) {
            throw new IllegalArgumentException("Planta não contém os acessos necessário.");
        }
        this.acessos = acessos;
        processarMatrizDeSala(planta.getMatrizSala());
    }

    /**
     * Processa uma matriz representando uma sala, convertendo seus caracteres em
     * objetos do jogo.
     */
    private void processarMatrizDeSala(ArrayList<ArrayList<TipoElemento>> matriz)
            throws IllegalArgumentException {
        this.chao = new Chao();

        // Substitui as portas genéricas por portas adequadas
        // ou por paredes.
        preprocessarPortas(matriz);

        // Itera sobre todos os elementos.
        for (int i = 0; i < matriz.size(); i++) {
            var linha = matriz.get(i);
            for (int j = 0; j < linha.size(); j++) {
                TipoElemento t = linha.get(j);

                // Se o tipo de elemento é VAZIO, não faz mais nada.
                if (t == TipoElemento.VAZIO) {
                    continue;
                }

                // Determina a posição no grid.
                var posicao =
                        new Point2D.Double(j * DIMENSOES_QUADRADOS.x, i * DIMENSOES_QUADRADOS.y);

                // Determina as cardinalidades de colisão, caos o objeto seja uma parede.
                var cardinalidades = determinarCardinalidades(matriz, i, j);

                // Instancia o elemento adequado e o registra na sala.
                switch (t) {
                    case PORTA:
                        throw new IllegalArgumentException("Porta não alinhada com paredes.");
                        // break;
                    case PORTA_N:
                        registrar(new Porta(posicao, Cardinalidade.NORTE));
                        break;
                    case PORTA_E:
                        registrar(new Porta(posicao, Cardinalidade.LESTE));
                        break;
                    case PORTA_S:
                        registrar(new Porta(posicao, Cardinalidade.SUL));
                        break;
                    case PORTA_W:
                        registrar(new Porta(posicao, Cardinalidade.OESTE));
                        break;
                    case PAREDE_PADRAO:
                        registrar(new ParedePadrao(posicao, cardinalidades));
                        break;
                    case TRELICA:
                        registrar(new Trelica(posicao, cardinalidades));
                        break;
                    case SNIPER:
                        registrar(new Sniper(posicao));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Transforma as portas genéricas nas portas adequadas
     * ou paredes, dependendo dos acessos da sala.
     */
    private void preprocessarPortas(ArrayList<ArrayList<TipoElemento>> matriz)
            throws IllegalArgumentException {
        /// Itera sobre todos os elementos da matriz.
        for (var i = 0; i < matriz.size(); i++) {
            var linha = matriz.get(i);
            for (var j = 0; j < linha.size(); j++) {
                // Se o elemento não é uma porta, não faz nada.
                if (linha.get(j) != TipoElemento.PORTA) {
                    continue;
                }

                // Se o elemento é uma porta, o substitui por
                // uma porta adequada ou parede.
                if (i == 0) {
                    if (this.acessos.contains(Cardinalidade.NORTE)) {
                        linha.set(j, TipoElemento.PORTA_N);
                    } else {
                        linha.set(j, TipoElemento.PAREDE_PADRAO);
                    }
                } else if (j == 0) {
                    if (this.acessos.contains(Cardinalidade.OESTE)) {
                        linha.set(j, TipoElemento.PORTA_W);
                    } else {
                        linha.set(j, TipoElemento.PAREDE_PADRAO);
                    }
                } else if (j == linha.size() - 1) {
                    if (this.acessos.contains(Cardinalidade.LESTE)) {
                        linha.set(j, TipoElemento.PORTA_E);
                    } else {
                        linha.set(j, TipoElemento.PAREDE_PADRAO);
                    }
                } else if (i == matriz.size() - 1) {
                    if (this.acessos.contains(Cardinalidade.SUL)) {
                        linha.set(j, TipoElemento.PORTA_S);
                    } else {
                        linha.set(j, TipoElemento.PAREDE_PADRAO);
                    }
                } else {
                    throw new IllegalArgumentException("Porta não alinhada com paredes.");
                }
                matriz.set(i, linha);
            }
        }
    }

    /**
     * Determina as direções em que uma parede da matriz na posição especificada
     * deve empurrar.
     */
    private EnumSet<Cardinalidade> determinarCardinalidades(
            ArrayList<ArrayList<TipoElemento>> matriz, int i, int j) {
        // Limites das arrays.
        int MAX_I = matriz.size() - 1;
        IntFunction<Integer> maxJ = k -> matriz.get(k).size() - 1;
        BiPredicate<Integer, Integer> isLivre = (a, b) -> !matriz.get(a).get(b).isParede();

        // Determina em que direções a parede deve empurrar, checando se existe
        // paredes, acima, abaixo e aos lados.
        var cardinalidades = new ArrayList<Cardinalidade>();
        if (i > 0 && isLivre.test(i - 1, j)) {
            cardinalidades.add(Cardinalidade.NORTE);
        }
        if (i < MAX_I && isLivre.test(i + 1, j)) {
            cardinalidades.add(Cardinalidade.SUL);
        }
        if (j > 0 && isLivre.test(i, j - 1)) {
            cardinalidades.add(Cardinalidade.OESTE);
        }
        if (j < maxJ.apply(i) && isLivre.test(i, j + 1)) {
            cardinalidades.add(Cardinalidade.LESTE);
        }

        // Confere as diagonais.
        if (i > 0 && j < maxJ.apply(i) && isLivre.test(i - 1, j + 1)) {
            cardinalidades.add(Cardinalidade.NORDESTE);
        }
        if (i < MAX_I && j < maxJ.apply(i) && isLivre.test(i + 1, j + 1)) {
            cardinalidades.add(Cardinalidade.SUDESTE);
        }
        if (i < MAX_I && j > 0 && isLivre.test(i + 1, j - 1)) {
            cardinalidades.add(Cardinalidade.SUDOESTE);
        }
        if (i > 0 && j > 0 && isLivre.test(i - 1, j - 1)) {
            cardinalidades.add(Cardinalidade.NOROESTE);
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

    // Adição.
    public void registrar(Ator a) {
        atores.add(a);
        colisores.add(a);
        elementos.add(a);
        if (a instanceof Inimigo) {
            _registrar((Inimigo) a);
        } else if (a instanceof Jogador) {
            _registrar((Jogador) a);
        }
    }

    private void _registrar(Jogador a) {
        this.jogador = a;
    }

    private void _registrar(Inimigo a) {
        inimigos.add(a);
    }

    public void registrar(Parede a) {
        colisores.add(a);
        elementos.add(a);
        if (a instanceof Porta) {
            _registrar((Porta) a);
        }
    }

    private void _registrar(Porta a) {
        portas.add(a);
    }

    // Remoção.
    public void remover(Ator a) {
        atores.remove(a);
        colisores.remove(a);
        elementos.remove(a);
        if (a instanceof Inimigo) {
            _remover((Inimigo) a);
        } else if (a instanceof Jogador) {
            _remover((Jogador) a);
        }
    }

    private void _remover(Jogador a) {
        this.jogador = null;
    }

    private void _remover(Inimigo a) {
        inimigos.remove(a);
    }

    /**
     * Atualiza os elementos que devem ser atualizados
     * e resolve colisões e spawns.
     */
    private ArrayList<Ator> filaDeRemocao = new ArrayList<Ator>();

    private ArrayList<Ator> filaDeAdicao = new ArrayList<Ator>();

    public void atualizar(double dt) {
        // Se a sala tentar atualizar enquanto o jogador não
        // estiver registrado, retorna.
        if (this.jogador == null) {
            return;
        }

        // Adiciona os novos atores.
        for (var i : filaDeAdicao) {
            registrar(i);
        }
        filaDeAdicao.clear();

        // Informa a posição do jogador aos inimigos.
        var posJogador = jogador.getPosicao();

        for (var i : inimigos) {
            i.registrarPosJogador(posJogador);
        }

        // Resolve as colisões, garantindo que não haja
        // overlap entre elementos.
        for (int i = 0; i < colisores.size() - 1; i++) {
            for (int j = i + 1; j < colisores.size(); j++) {
                colisores.get(i).resolverColisaoCom(colisores.get(j));
            }
        }

        // Atualiza os atores, calculando movimento,
        // dano, etc.
        for (var i : atores) {
            var vivo = i.atualizar(dt);
            if (!vivo) {
                this.filaDeRemocao.add(i);
            }
            for (var j : i.getSpawns()) {
                this.filaDeAdicao.add(j);
            }
        }

        // Remove os atores mortos.
        for (var i : filaDeRemocao) {
            remover(i);
        }

        // Se todos os inimigos estiverem mortos, abre as portas.
        if (this.inimigos.isEmpty()) {
            for (var i : portas) {
                i.avancarAnimacao(dt);
            }
        }
    }

    public void desenhar(Point2D.Double camera, Graphics2D g) {
        /**
         * Desenha todos os elementos na tela.
         */
        this.chao.desenhar(camera, g);
        for (var i : elementos) {
            i.desenhar(camera, g);
        }
    }

    public Cardinalidade getTransicao() {
        if (!this.inimigos.isEmpty()) {
            return null;
        } else {
            return jogador.getTransicao();
        }
    }

    /*
     * Finaliza qualquer processo em andamento antes de ceder
     * controle.
     */
    public void finalizar() {
        // Completa as animações de todas as portas.
        for (var i : this.portas) {
            i.completarAnimacao();
        }

        // Limpa as filas de adição e remoção
        // e remove todos os projéteis.
        this.filaDeAdicao.clear();
        for (var i : this.atores) {
            if (i instanceof Projetil) {
                this.filaDeRemocao.add(i);
            }
        }
        for (var i : filaDeRemocao) {
            this.remover(i);
        }
        this.filaDeRemocao.clear();
    }
}
