package fatec.vortek.cimob.domain.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;

import fatec.vortek.cimob.domain.enums.TimelineTipo;

@Converter(autoApply = true)
public class TimelineTipoConverter implements AttributeConverter<TimelineTipo, String> {

    @Override
    public String convertToDatabaseColumn(TimelineTipo tipo) {
        return tipo != null ? tipo.getDescricao() : null;
    }

    @Override
    public TimelineTipo convertToEntityAttribute(String valor) {
        if (valor == null) return null;
        return Arrays.stream(TimelineTipo.values())
                .filter(tv -> tv.getDescricao().equalsIgnoreCase(valor))
                .findFirst()
                .orElse(TimelineTipo.INDEFINIDO);
    }
}
