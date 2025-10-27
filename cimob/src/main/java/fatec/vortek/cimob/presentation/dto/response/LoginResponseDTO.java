package fatec.vortek.cimob.presentation.dto.response;

public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;

    public LoginResponseDTO(String accessToken){
        this.accessToken = accessToken;
    }

    public String getAccessToken() { return accessToken; }
}
