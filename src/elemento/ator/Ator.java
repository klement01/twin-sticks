/**
 * Representa um elemento dinâmico que tem colisão.
 */
package elemento.ator;

import elemento.Colisoes;
import elemento.ElemDinamico;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public abstract class Ator extends ElemDinamico implements Colisoes {
    protected ArrayList<Colisao> filaDeColisoes = new ArrayList<Colisao>();

    protected Ator(Point2D.Double posicao, Point2D.Double dimensao) {
        super(posicao, dimensao);
    }

    // Por padrão, processa todas as colisões não concluídas e
    // apaga a lista de colisões para processar. Em seguida,
    // checa se o ator continua vivo.
    @Override
    public boolean atualizar(double dt) {
        for (var i : filaDeColisoes) {
            this.resolverColisaoPassada(i, dt);
        }
        this.filaDeColisoes.clear();
        return this.atorVivo();
    }

    // Termina de resolver uma colisão do frame anterior.
    protected abstract void resolverColisaoPassada(Colisao c, double dt);

    // Checha se o objeto ainda está vivo, ou seja, se deve
    // continuar existindo ou se deve ser destruído.
    protected abstract boolean atorVivo();

    // Obtem o retângulo que representa a colisão do objeto.
    @Override
    public Rectangle2D.Double getRectColisao() {
        return new Rectangle2D.Double(this.getPosicao().getX(), this.getPosicao().getY(), this.getDimensoes().getX(),
                this.getDimensoes().getY());
    }

    // Obter o vetor para o centro do objeto.
    @Override
    public Point2D.Double getVetorCentro() {
        var rectColisao = this.getRectColisao();
        return new Point2D.Double(rectColisao.getCenterX(), rectColisao.getCenterY());
    }

    // Realiza qualquer ação necessária durante a resolução
    // de uma colisão entre dois objetos.
    @Override
    public void resolverColisaoCom(Colisoes c) {
        if (c instanceof Ator) {
            // Se o colisor for outro Ator, empurra ambos
            // os atores em direções opostas e os colocam na
            // fila de resolução.

            // Primeiro, checa se houve colisão.
            // Se não houve, retorna.
            Rectangle2D.Double colisaoT = this.getRectColisao();
            Rectangle2D.Double colisaoC = c.getRectColisao();
            var intersecao = colisaoT.createIntersection(colisaoC);

            if (intersecao.getWidth() <= 0 || intersecao.getHeight() <= 0) {
                return;
            }

            // Empurra os dois atores "para trás", fazendo
            // com que não estejam mais em colisão.
            var deslocamentoT = new Point2D.Double();
            var deslocamentoC = new Point2D.Double();
            if (intersecao.getWidth() > intersecao.getHeight()) {
                // Empurra os objetos no eixo vertical.
                double dy = intersecao.getHeight() / 2;
                if (colisaoT.getY() < colisaoC.getY()) {
                    dy = -dy;
                }
                deslocamentoT.setLocation(0, dy);
                deslocamentoC.setLocation(0, -dy);
            } else {
                // Empurra os objetos no eixo horizontal.
                double dx = intersecao.getWidth() / 2;
                if (colisaoT.getX() < colisaoC.getX()) {
                    dx = -dx;
                }
                deslocamentoT.setLocation(dx, 0);
                deslocamentoC.setLocation(-dx, 0);
            }
            this.empurrar(deslocamentoT);
            c.empurrar(deslocamentoC);

            // Registra a colisão para ambos os atores lidarem
            // com qualquer efeito necessário durante o próximo
            // frame.
            this.registrarColisao(new Colisao(c, deslocamentoT));
            c.registrarColisao(new Colisao(this, deslocamentoC));
        } else {
            // Se o colisor não for uma ator, deixa o colisor
            // resolver a colisão.
            c.resolverColisaoCom(this);
        }
    }

    // Registra uma colisão para que sua resolução
    // seja concluída no próximo frame.
    @Override
    public void registrarColisao(Colisao c) {
        this.filaDeColisoes.add(c);
    }

    // Altera a posição do objeto no frame atual.
    @Override
    public void empurrar(Point2D.Double p) {
        this.setPosicao(new Point2D.Double(this.getPosicao().getX() + p.getX(), this.getPosicao().getY() + p.getY()));
    }
}
