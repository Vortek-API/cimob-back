package fatec.vortek.cimob.domain.enums;

public enum IndicadorMnemonico {
    EXCESSO_VELOCIDADE("Excesso de Velocidade Grave"),
    VARIABILIDADE_VELOCIDADE("Variabilidade de Velocidade"),
    VEICULOS_LENTOS("Veículos Muito Lentos"),
    FLUXO_VEICULOS("Fluxo de Veículos"),
    DIFERENCA_MEDIA_VELOCIDADE("Diferença Média de Velocidade"),
    HOMOGENEIDADE_VELOCIDADE("Coeficiente de Homogeneidade de Velocidade"),
    FREQUENCIA_PICOS_HORA("Frequência de Picos por Hora");

    private final String descricao;

    IndicadorMnemonico(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
