package services.converter;

import java.util.List;

public interface ConverterService {
    public List<String> convert(String number) throws IllegalArgumentException;

}
