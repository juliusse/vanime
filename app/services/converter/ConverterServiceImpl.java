package services.converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;

import play.Play;

public class ConverterServiceImpl implements ConverterService {

    private List<String> wordList = null;

    public ConverterServiceImpl() {

        wordList = new ArrayList<String>();
        try {
            final String words = IOUtils.toString(Play.application().resourceAsStream("en.dict")).toLowerCase();
            final String[] wordsArray = words.split("\r\n");
            wordList = Arrays.asList(wordsArray);
        } catch (IOException e) {
        }
    }

    @Override
    public List<String> convert(String number) throws IllegalArgumentException {
        number = replaceLeadingPlus(number);
        number = replaceSpacings(number);

        if (!containsOnlyDigits(number)) {
            throw new IllegalArgumentException("Input should only contain digits");
        }

        List<String> allCombinations = generateCombinations(number);

        return allCombinations;
    }

    private String replaceLeadingPlus(String number) {
        return number.startsWith("+") ? number.replaceFirst("+", "00") : number;
    }

    private String replaceSpacings(String number) {
        return number.replaceAll(" ", "");
    }

    private boolean containsOnlyDigits(String number) {
        return NumberUtils.isDigits(number);
    }

    private List<String> generateCombinations(String number) {

        final int currentDigit = Integer.parseInt(number.charAt(0) + "");
        final String numbersLeftNow = number.substring(1);

        final List<String> combinations = new ArrayList<>();
        if (currentDigit < 2) {
            combinations.addAll(generateCombinations(numbersLeftNow, Arrays.asList(new String[] { "-" + currentDigit + "-" })));
        } else {
            final String[] possibleCharacters = numberToPossibleCharacters(currentDigit);
            combinations.addAll(generateCombinations(numbersLeftNow, Arrays.asList(possibleCharacters)));
        }

//        Iterator<String> it = combinations.iterator();
//        while (it.hasNext()) {
//            final String value = it.next();
//            final String lastPart = getLastPartOfCombination(value);
//            if (!isAWord(lastPart, 3)) {
//                it.remove();
//            }
//        }

        return combinations;

    }

    private List<String> generateCombinations(String numbersLeft, List<String> existingCombinations) {
        if (numbersLeft.length() == 0) {
            return existingCombinations;
        }

        final List<String> possibleCombinations = new ArrayList<String>();

        final int currentDigit = Integer.parseInt(numbersLeft.charAt(0) + "");
        final String numbersLeftNow = numbersLeft.substring(1);

        final String[] possibleCharacters = numberToPossibleCharacters(currentDigit);

        possibleCombinations.addAll(appendAsDigit(existingCombinations, currentDigit));

        for (String existing : existingCombinations) {
            for (String character : possibleCharacters) {
                final String newCombination = existing + character;
                final String lastPart = getLastPartOfCombination(newCombination);

                if (isAWord(lastPart, 3)) {
                    possibleCombinations.add(newCombination + "-");
                }
                if (isBeginningOfAWord(lastPart)) {
                    possibleCombinations.add(newCombination);
                }
            }
        }

        return generateCombinations(numbersLeftNow, possibleCombinations);
    }

    private List<String> appendAsDigit(List<String> currentPossibilities, int currentDigit) {
        final List<String> possibleCombinations = new ArrayList<String>();

        final String appendPart = "-" + currentDigit + "-";
        // look if current solutions make sense
        for (String combination : currentPossibilities) {
            final String lastPart = getLastPartOfCombination(combination);

            // no last part
            if (lastPart.length() == 0) {
                char foreLastCharacter = combination.charAt(combination.length() - 2);
                if (NumberUtils.isDigits(foreLastCharacter + "")) {
                    possibleCombinations.add(combination.substring(0, combination.length() - 1) + currentDigit + "-");
                } else {
                    possibleCombinations.add(combination + currentDigit + "-");
                }
            } else if (isAWord(lastPart, 3)) {
                possibleCombinations.add(combination + appendPart);
            }
        }

        return possibleCombinations;
    }

    private String[] numberToPossibleCharacters(int digit) {
        switch (digit) {
        case 0:
            return new String[] { "0" };
        case 1:
            return new String[] { "1" };
        case 2:
            return new String[] { "a", "b", "c" };
        case 3:
            return new String[] { "d", "e", "f" };
        case 4:
            return new String[] { "g", "h", "i" };
        case 5:
            return new String[] { "j", "k", "l" };
        case 6:
            return new String[] { "m", "n", "o" };
        case 7:
            return new String[] { "p", "q", "r", "s" };
        case 8:
            return new String[] { "t", "u", "v" };
        case 9:
            return new String[] { "w", "x", "y", "z" };
        default:
            throw new IllegalArgumentException("parameter was no single digit");
        }
    }

    private boolean isBeginningOfAWord(String part) {
        for (String word : wordList) {
            if (word.startsWith(part) && !word.equals(part))
                return true;
        }
        return false;
    }

    private boolean isAWord(String part, int minLength) {
        if (part.length() < minLength) {
            return false;
        }
        for (String word : wordList) {
            if (word.equals(part))
                return true;
        }
        return false;
    }

    private String getLastPartOfCombination(String combination) {
        final int lastSeparatorPos = combination.lastIndexOf("-");
        if (lastSeparatorPos >= 0) {
            return combination.substring(lastSeparatorPos + 1);
        } else {
            return combination;
        }
    }
}
