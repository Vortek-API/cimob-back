package fatec.vortek.cimob.domain.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;

import fatec.vortek.cimob.domain.enums.TimelineAcao;

@Converter(autoApply = true)
public class TimelineAcaoConverter implements AttributeConverter<TimelineAcao, String> {

    @Override
    public String convertToDatabaseColumn(TimelineAcao tipo) {
        return tipo != null ? tipo.getDescricao() : null;
    }

    @Override
    public TimelineAcao convertToEntityAttribute(String valor) {
        if (valor == null) return null;
        return Arrays.stream(TimelineAcao.values())
                .filter(tv -> tv.getDescricao().equalsIgnoreCase(valor))
                .findFirst()
                .orElse(TimelineAcao.INDEFINIDO);
    }
}
