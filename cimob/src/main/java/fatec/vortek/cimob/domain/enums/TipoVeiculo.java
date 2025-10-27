package fatec.vortek.cimob.domain.enums;

public enum TipoVeiculo {
    CARRO("Carro"),
    INDEFINIDO("Indefinido"),
    CAMIONETE("Camionete"),
    ONIBUS("Ônibus"),
    MOTO("Moto"),
    VAN("Van"),
    CAMINHAO_GRANDE("Caminhão grande");

    private final String descricao;

    TipoVeiculo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoVeiculo fromDescricao(String descricao) {
        for (TipoVeiculo tipo : values()) {
            if (tipo.descricao.equalsIgnoreCase(descricao)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de veículo inválido: " + descricao);
    }
}
