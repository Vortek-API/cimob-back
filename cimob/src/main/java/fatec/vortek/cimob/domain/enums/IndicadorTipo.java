package fatec.vortek.cimob.domain.enums;

public enum IndicadorTipo {
    
    VELOCIDADE_MEDIA("VELOCIDADE_MEDIA", "Velocidade Média"),
    MEDIA_ULTRAPASSARAM_VELOCIDADE("MEDIA_ULTRAPASSARAM_VELOCIDADE", "Média de veículos acima do limite"),
    VARIACAO_VELOCIDADE_10MIN("VARIACAO_VELOCIDADE_10MIN", "Variação de velocidade (últimos 10 minutos)"),
    TIPO_VEICULO("TIPO_VEICULO", "Distribuição de veículos por categoria");

    private final String mnemonico;
    private final String descricao;

    IndicadorTipo(String mnemonico, String descricao) {
        this.mnemonico = mnemonico;
        this.descricao = descricao;
    }

    public String getMnemonico() {
        return mnemonico;
    }

    public String getDescricao() {
        return descricao;
    }

    public static IndicadorTipo fromMnemonico(String mnemonico) {
        for (IndicadorTipo tipo : values()) {
            if (tipo.getMnemonico().equalsIgnoreCase(mnemonico)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Mnemonico inválido: " + mnemonico);
    }
}
