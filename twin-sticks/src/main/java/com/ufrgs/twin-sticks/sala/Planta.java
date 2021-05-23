/**
 * Carrega uma "planta" de um arquivo, contendo
 * informações para criar uma sala.
 */
package sala;

import static app.Comum.Cardinalidade;

import elemento.ator.inimigo.*;
import elemento.parede.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;

public class Planta {
    /*
     * Carrega todas as plantas e as guarda em arrays com mapas.
     */
    private static final int NUM_PLANTAS = 13;
    private static Planta plantaBasica;
    private static HashSet<Planta> plantas = new HashSet<Planta>();
    private static HashSet<Planta> plantasN = new HashSet<Planta>();
    private static HashSet<Planta> plantasE = new HashSet<Planta>();
    private static HashSet<Planta> plantasS = new HashSet<Planta>();
    private static HashSet<Planta> plantasW = new HashSet<Planta>();
    private static EnumMap<Cardinalidade, HashSet<Planta>> mapaPlantas;

    // Mapeia as cardinalidades aos conjuntos apropriados.
    static {
        Planta.mapaPlantas = new EnumMap<Cardinalidade, HashSet<Planta>>(Cardinalidade.class);
        Planta.mapaPlantas.put(Cardinalidade.NORTE, Planta.plantasN);
        Planta.mapaPlantas.put(Cardinalidade.LESTE, Planta.plantasE);
        Planta.mapaPlantas.put(Cardinalidade.SUL, Planta.plantasS);
        Planta.mapaPlantas.put(Cardinalidade.OESTE, Planta.plantasW);
    }

    // Carrega as plantas.
    static {
        for (int i = 1; i <= Planta.NUM_PLANTAS; i++) {
            // Tenta gerar uma planta.
            Planta planta;
            try {
                planta = new Planta(i + ".txt");
            } catch (IOException e) {
                System.err.println("Erro lendo planta: " + e.toString());
                continue;
            } catch (IllegalArgumentException e) {
                System.err.println("Planta inválida: " + e.toString());
                continue;
            }

            // Usa a primeira planta como planta básica.
            if (i == 1) {
                Planta.plantaBasica = planta;
                continue;
            }

            // Guarda a planta nas listas adequadas.
            Planta.plantas.add(planta);
            for (var j : planta.getAcessosPossiveis()) {
                Planta.mapaPlantas.get(j).add(planta);
            }
        }
    }

    public static Sala criarSalaComAcessos(EnumSet<Cardinalidade> acessos) {
        // Primeiro, cria a lista de todas as plantas que têm os
        // acessos necessário.
        @SuppressWarnings("unchecked")
        HashSet<Planta> plantas = (HashSet<Planta>) Planta.plantas.clone();
        for (var i : acessos) {
            plantas.retainAll(Planta.mapaPlantas.get(i));
        }

        // Então, escolha uma planta aleatória da lista
        // e cria uma sala baseada nela. Código baseado em
        // <https://stackoverflow.com/a/25410520>.
        int indice = app.Comum.rand.nextInt(plantas.size());
        var iterador = plantas.iterator();
        for (int i = 0; i < indice; i++) {
            iterador.next();
        }
        var sala = new Sala(iterador.next(), acessos);
        return sala;
    }

    public static Sala criarSalaBasicaComAcessos(EnumSet<Cardinalidade> acessos) {
        return new Sala(Planta.plantaBasica, acessos, true);
    }

    /*
     * Caminho para os arquivos de sala.
     */
    private static final String CAMINHO = "/salas/";

    /*
     * Matriz representando os tipos de elementos do
     * arquivo da planta.
     */
    private final ArrayList<ArrayList<TipoElemento>> matrizSala;

    /*
     * Conjunto com todas as direções de acesso da
     * sala.
     */
    private final EnumSet<Cardinalidade> acessosPossiveis;

    public Planta(String nome) throws IOException, IllegalArgumentException {
        // Forma o caminho da planta.
        String caminho = Planta.CAMINHO + nome;

        // Tenta abrir o arquivo.
        InputStream streamArquivo = getClass().getResourceAsStream(caminho);
        BufferedReader arquivo = new BufferedReader(new InputStreamReader(streamArquivo));

        /*
         * Converte os caracteres do arquivo em tipos de elemento.
         */
        var acessosPossiveis = new ArrayList<Cardinalidade>();
        this.matrizSala = new ArrayList<ArrayList<TipoElemento>>();

        // Itera sobre cada linha.
        String linha;
        int i = 0;
        while ((linha = arquivo.readLine()) != null) {
            // Itera sobre cada char da linha.
            var valoresLinha = Arrays.asList(linha.trim().split(""));
            var linhaProcessada = new ArrayList<TipoElemento>();
            for (int j = 0; j < valoresLinha.size(); j++) {
                var valor = valoresLinha.get(j);
                var tipo = TipoElemento.getTipoElemento(valor);
                linhaProcessada.add(tipo);

                // Se o tipo do elemento é uma porta, adiciona
                // uma cardinalidade às cardinalidades possíveis
                // de acesso.
                if (tipo == TipoElemento.PORTA) {
                    if (i == 0) {
                        acessosPossiveis.add(Cardinalidade.NORTE);
                    } else if (j == 0) {
                        acessosPossiveis.add(Cardinalidade.OESTE);
                    } else if (j == valoresLinha.size() - 1) {
                        acessosPossiveis.add(Cardinalidade.LESTE);
                    } else {
                        acessosPossiveis.add(Cardinalidade.SUL);
                    }
                }
            }
            matrizSala.add(linhaProcessada);
            i++;
        }

        arquivo.close();

        // Cria um conjunto de cardinalidades ou, se impossível,
        // gera uma exceção.
        if (acessosPossiveis.size() == 0) {
            throw new IllegalArgumentException("Sala sem acesso: " + caminho);
        } else {
            this.acessosPossiveis = EnumSet.copyOf(acessosPossiveis);
        }
    }

    /*
     * Retorna os elementos da planta necessários para gerar uma sala.
     */
    public ArrayList<ArrayList<TipoElemento>> getMatrizSala() {
        var matriz = new ArrayList<ArrayList<TipoElemento>>();
        for (var i : this.matrizSala) {
            @SuppressWarnings("unchecked")
            var linha = (ArrayList<TipoElemento>) i.clone();
            matriz.add(linha);
        }
        return matriz;
    }

    public EnumSet<Cardinalidade> getAcessosPossiveis() {
        return EnumSet.copyOf(this.acessosPossiveis);
    }
}
