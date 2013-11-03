package services.converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;

import play.Logger;
import play.Play;

public class ConverterServiceImpl implements ConverterService {

    // private final Set<String> dict;
    private List<String> enList = null;
    private List<String> deList = null;

    public ConverterServiceImpl() {

        enList = new ArrayList<String>();
        deList = new ArrayList<String>();
        try {
            final String words = IOUtils.toString(Play.application().resourceAsStream("en.dict")).toLowerCase();
            final String[] wordsArray = words.split("\r\n");
            enList = Arrays.asList(wordsArray);

            final String wordsDe = IOUtils.toString(Play.application().resourceAsStream("de.dict")).toLowerCase();
            final String[] wordsArrayDe = wordsDe.split("\r\n");
            deList = Arrays.asList(wordsArrayDe);
        } catch (IOException e) {
        }
    }

    @Override
    public List<String> convert(String number, ConverterSettings settings) throws IllegalArgumentException {
        String convertedNumber = replaceLeadingPlus(number);
        Logger.debug(convertedNumber + "");
        convertedNumber = replaceSpacings(convertedNumber);

        if (!containsOnlyDigits(convertedNumber)) {
            throw new IllegalArgumentException("Input should only contain digits");
        }

        List<String> allCombinations = generateCombinations(convertedNumber, settings);

        return allCombinations;
    }

    private String replaceLeadingPlus(String number) {
        return number.startsWith("+") ? number.replaceFirst("\\+", "00") : number;
    }

    private String replaceSpacings(String number) {
        return number.replaceAll(" ", "");
    }

    private boolean containsOnlyDigits(String number) {
        return NumberUtils.isDigits(number);
    }

    private List<String> generateCombinations(String number, ConverterSettings settings) {

        final List<String> dict = enList;

        List<Combination> combinations = new ArrayList<Combination>();
        combinations.add(new Combination(dict, settings));

        for (char c : number.toCharArray()) {
            final int digit = Integer.parseInt(c + "");
            final List<Combination> newCombiniations = new ArrayList<Combination>();

            for (Combination combination : combinations) {
                newCombiniations.addAll(combination.addDigit(digit));
            }
            combinations = newCombiniations;
        }

        final List<String> postProcessedList = new ArrayList<String>();
        for (Combination combination : combinations) {
            if (combination.getCurrentPart().isEmpty() || combination.isCurrentPartNumeric()) {
                postProcessedList.add(combination.toString());
            }
        }

        return postProcessedList;
    }

}
