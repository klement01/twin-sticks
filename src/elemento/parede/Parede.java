/**
 * Representa uma parede estática.
 */
package elemento.parede;

import static java.lang.Math.abs;

import elemento.Colisoes;
import elemento.ElemEstatico;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.EnumSet;

public abstract class Parede extends ElemEstatico implements Colisoes {
    /*
     * Direções em que uma parede pode empurrar um objeto
     * em colisão com ela.
     */
    public enum Cardinalidade {
        NORTE,
        LESTE,
        SUL,
        OESTE
    };

    private final EnumSet<Cardinalidade> cardinalidades;

    /*
     * Variáveis de colisão estáticas.
     */
    private final Rectangle2D.Double rectColisao;
    private final Point2D.Double vetorCentro;

    /*
     * Cria um parede com colisão em todas suas superfícies
     * ou em um subconjunto de suas superfícies.
     */
    protected Parede(Point2D.Double posicao, Point2D.Double dimensoes) {
        // Se não forem passadas cardinalidades para colisores
        // serem empurrados, assume que a parede empurra para
        // qualquer lado.
        this(posicao, dimensoes, EnumSet.allOf(Cardinalidade.class));
    }

    protected Parede(
            Point2D.Double posicao, Point2D.Double dimensoes, EnumSet<Cardinalidade> cardinalidades)
            throws IllegalArgumentException {
        // Construtor genérico de ElementoEstático.
        super(posicao, dimensoes);

        // Inicializa as variáveis de colisão constantes.
        this.rectColisao =
                new Rectangle2D.Double(
                        posicao.getX(), posicao.getY(),
                        dimensoes.getX(), dimensoes.getY());
        this.vetorCentro = new Point2D.Double(rectColisao.getCenterX(), rectColisao.getCenterY());

        // Guarda as cardinalidades em que colisores podem ser empurrados.
        if (cardinalidades.isEmpty()) {
            throw new IllegalArgumentException("Parede deve empurrar em pelo menos uma direção.");
        }
        this.cardinalidades = cardinalidades;
    }

    /*
     * Implementação da interface Colisão.
     */
    @Override
    public final Rectangle2D.Double getRectColisao() {
        return rectColisao;
    }

    @Override
    public final Point2D.Double getVetorCentro() {
        return vetorCentro;
    }

    @Override
    public void resolverColisaoCom(Colisoes c) {
        // Determina a interseção entre a parede e
        // o colisor c.
        var colisaoC = c.getRectColisao();
        var intersecao = this.getRectColisao().createIntersection(colisaoC);

        // Se a interseção for nula, retorna.
        if (intersecao.getWidth() <= 0 || intersecao.getHeight() <= 0) {
            return;
        }

        // Se não, determina em que direção empurrar
        // o colisor para resolver a colisão.
        var centroParede = this.getVetorCentro();
        var centroC = c.getVetorCentro();

        // O eixo com maior distância tem prioridade.
        var dx = centroParede.getX() - centroC.getX();
        var dy = centroParede.getY() - centroC.getY();

        // Determina a ordem de prioridade em que empurrar
        // o colisor.
        var ordemEmpurrar = new ArrayDeque<Cardinalidade>(4);

        Runnable ordenarVertical =
                () -> {
                    if (dy > 0) {
                        ordemEmpurrar.addFirst(Cardinalidade.NORTE);
                        ordemEmpurrar.addLast(Cardinalidade.SUL);
                    } else {
                        ordemEmpurrar.addFirst(Cardinalidade.SUL);
                        ordemEmpurrar.addLast(Cardinalidade.NORTE);
                    }
                };

        Runnable ordenarHorizontal =
                () -> {
                    if (dx > 0) {
                        ordemEmpurrar.addFirst(Cardinalidade.OESTE);
                        ordemEmpurrar.addLast(Cardinalidade.LESTE);
                    } else {
                        ordemEmpurrar.addFirst(Cardinalidade.LESTE);
                        ordemEmpurrar.addLast(Cardinalidade.OESTE);
                    }
                };

        if (abs(dx) >= abs(dy)) {
            ordenarVertical.run();
            ordenarHorizontal.run();
        } else {
            ordenarHorizontal.run();
            ordenarVertical.run();
        }

        // Empurra o colisor na primeira cardinalidade que
        // aparecer na lista de cardinalidades da parede.
        for (var i : ordemEmpurrar) {
            if (this.cardinalidades.contains(i)) {
                var deslocamento = new Point2D.Double();
                switch (i) {
                    case NORTE:
                        deslocamento.setLocation(0, -intersecao.getHeight());
                        break;
                    case LESTE:
                        deslocamento.setLocation(intersecao.getWidth(), 0);
                        break;
                    case SUL:
                        deslocamento.setLocation(0, intersecao.getHeight());
                        break;
                    case OESTE:
                        deslocamento.setLocation(-intersecao.getWidth(), 0);
                        break;
                }
                c.empurrar(deslocamento);
                c.registrarColisao(new Colisoes.Colisao(this, deslocamento));
                return;
            }
        }
    }

    @Override
    public void registrarColisao(Colisao c) {}

    @Override
    public void empurrar(Point2D.Double p) {}
}
