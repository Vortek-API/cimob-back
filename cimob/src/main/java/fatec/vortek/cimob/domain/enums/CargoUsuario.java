package fatec.vortek.cimob.domain.enums;

public enum CargoUsuario {
    ADMIN("Admin"),
    INDEFINIDO("Indisponivel"),
    USUARIO("Usuario");

    private final String descricao;

    CargoUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
