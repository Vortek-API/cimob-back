package fatec.vortek.cimob.domain.enums;

public enum TimelineTipo {
    USUARIO("USUARIO"),
    INDICADOR("INDICADOR"),
    EVENTO("EVENTO"),
    INDEFINIDO("INDEFINIDO");

    private final String descricao;

    TimelineTipo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
