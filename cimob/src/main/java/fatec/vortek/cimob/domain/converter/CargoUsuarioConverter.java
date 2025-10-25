package fatec.vortek.cimob.domain.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;

import fatec.vortek.cimob.domain.enums.CargoUsuario;
import fatec.vortek.cimob.domain.enums.CargoUsuario;

@Converter(autoApply = true)
public class CargoUsuarioConverter implements AttributeConverter<CargoUsuario, String> {

    @Override
    public String convertToDatabaseColumn(CargoUsuario tipo) {
        return tipo != null ? tipo.getDescricao() : null;
    }

    @Override
    public CargoUsuario convertToEntityAttribute(String valor) {
        if (valor == null) return null;
        return Arrays.stream(CargoUsuario.values())
                .filter(tv -> tv.getDescricao().equalsIgnoreCase(valor))
                .findFirst()
                .orElse(CargoUsuario.INDEFINIDO);
    }
}
