package fatec.vortek.cimob.presentation.dto.request;

public class LoginRequestDTO {
    private String userName;
    private String senha;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
