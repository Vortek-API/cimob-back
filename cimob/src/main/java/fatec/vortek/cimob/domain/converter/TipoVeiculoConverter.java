package fatec.vortek.cimob.domain.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;

import fatec.vortek.cimob.domain.enums.TipoVeiculo;

@Converter(autoApply = true)
public class TipoVeiculoConverter implements AttributeConverter<TipoVeiculo, String> {

    @Override
    public String convertToDatabaseColumn(TipoVeiculo tipo) {
        return tipo != null ? tipo.getDescricao() : null;
    }

    @Override
    public TipoVeiculo convertToEntityAttribute(String valor) {
        if (valor == null) return null;
        return Arrays.stream(TipoVeiculo.values())
                .filter(tv -> tv.getDescricao().equalsIgnoreCase(valor))
                .findFirst()
                .orElse(TipoVeiculo.INDEFINIDO);
    }
}
