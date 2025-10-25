package fatec.vortek.cimob.domain.enums;

public enum CargoUsuario {
    ADMIN("ADMIN"),
    INDEFINIDO("INDEFINIDO"),
    USUARIO("USUARIO    ");

    private final String descricao;

    CargoUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
