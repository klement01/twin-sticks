/**
 * Mapeia caracteres para tipos de elementos do jogo.
 */
package sala;

import elemento.ator.inimigo.*;
import elemento.parede.*;

import java.util.EnumSet;
import java.util.HashMap;

enum TipoElemento {
    VAZIO("-"),
    PAREDE_PADRAO("#"),
    TRELICA("T"),
    PORTA("P"),
    PORTA_N(""),
    PORTA_E(""),
    PORTA_S(""),
    PORTA_W(""),
    SNIPER("S");

    // Inicializa o mapa após todas as chaves terem sido inicializadas.
    // Baseado em <https://stackoverflow.com/a/536461>.
    private static HashMap<String, TipoElemento> mapa;

    static {
        TipoElemento.mapa = new HashMap<String, TipoElemento>();
        for (var i : EnumSet.allOf(TipoElemento.class)) {
            TipoElemento.mapa.put(i.valor, i);
        }
    }

    // Tipos de elementos que contam como uma parede.
    private static final EnumSet<TipoElemento> TiposParede =
            EnumSet.of(PAREDE_PADRAO, TRELICA, PORTA_N, PORTA_E, PORTA_S, PORTA_W);

    private String valor;

    TipoElemento(String valor) {
        this.valor = valor;
    }

    public static TipoElemento getTipoElemento(String chave) {
        // Qualquer chave não identificada conta como vazia.
        var tipo = TipoElemento.mapa.get(chave);
        if (tipo == null) {
            tipo = VAZIO;
        }
        return tipo;
    }

    public boolean isParede() {
        return TiposParede.contains(this);
    }
}
