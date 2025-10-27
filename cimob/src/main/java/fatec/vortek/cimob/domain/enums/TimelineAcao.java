package fatec.vortek.cimob.domain.enums;

public enum TimelineAcao {
    LOGIN("LOGIN"),
    LOGOUT("LOGOUT"),
    ALTERACAO("ALTERACAO"),
    CRIACAO("CRIACAO"),
    EXCLUSAO("EXCLUSAO"),
    INDEFINIDO("INDEFINIDO");

    private final String descricao;

    TimelineAcao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
