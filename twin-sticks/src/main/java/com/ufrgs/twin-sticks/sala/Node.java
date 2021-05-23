/**
 * Representa um nó do mapa, que contém uma
 * sala e até quatro vizinhos.
 */
package sala;

import static app.Comum.Cardinalidade;

import java.util.EnumMap;
import java.util.EnumSet;

public class Node {
    private Sala sala = null;
    private EnumMap<Cardinalidade, Node> vizinhos = null;
    private EnumSet<Cardinalidade> acessos = null;

    public Node() {
        this.vizinhos = new EnumMap<Cardinalidade, Node>(Cardinalidade.class);
        this.acessos = EnumSet.noneOf(Cardinalidade.class);
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Sala getSala() {
        return this.sala;
    }

    public void setVizinho(Cardinalidade cardinalidade, Node node) {
        if (this.vizinhos.put(cardinalidade, node) == null) {
            node.setVizinho(cardinalidade.oposto(), this);
        }
        this.acessos.add(cardinalidade);
    }

    public Node getVizinho(Cardinalidade cardinalidade) {
        return this.vizinhos.get(cardinalidade);
    }

    public EnumSet<Cardinalidade> getAcessos() {
        return this.acessos;
    }
}
