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
        convertedNumber = replaceSpacings(convertedNumber);

        if (!containsOnlyDigits(convertedNumber)) {
            throw new IllegalArgumentException("Input should only contain digits");
        }

        List<String> allCombinations = generateCombinations(convertedNumber, settings);

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

    private List<String> generateCombinations(String number, ConverterSettings settings) {

        final List<String> dict = enList;
        final int currentDigit = Integer.parseInt(number.charAt(0) + "");
        final String numbersLeftNow = number.substring(1);

        final List<String> combinations = new ArrayList<>();
        final List<String> startList = new ArrayList<>();
        startList.add(currentDigit + "");
        if (currentDigit > 1) {
            final String[] possibleCharacters = numberToPossibleCharacters(currentDigit);
            startList.addAll(Arrays.asList(possibleCharacters));

        }

        combinations.addAll(generateCombinations(numbersLeftNow, startList, dict, settings));

        Iterator<String> it = combinations.iterator();
        while (it.hasNext()) {
            final String value = it.next();
            final String lastPart = getLastPartOfCombination(value);
            final char lastCharacter = value.charAt(value.length() - 1);
            if (!isWord(lastPart, settings.getMinLengthOfWords(), dict) && !NumberUtils.isDigits(lastCharacter + "") || NumberUtils.isDigits(value)
                    || lentghOfLongestSeriesOfNumbers(value) > settings.getMaxSeriesOfDigits()) {
                it.remove();
            }
        }

        return combinations;

    }

    private List<String> generateCombinations(String numbersLeft, List<String> existingCombinations, List<String> dict, ConverterSettings settings) {
        if (numbersLeft.length() == 0) {
            return existingCombinations;
        }

        final List<String> listWithFinishedWordsAtEnd = lookForFinishedWords(existingCombinations, settings.getMinLengthOfWords(), dict);
        final List<String> possibleCombinations = new ArrayList<String>();

        final int currentDigit = Integer.parseInt(numbersLeft.charAt(0) + "");
        final String numbersLeftNow = numbersLeft.substring(1);

        possibleCombinations.addAll(appendAsDigit(listWithFinishedWordsAtEnd, currentDigit));

        possibleCombinations.addAll(appendAsCharacter(listWithFinishedWordsAtEnd, currentDigit, settings.getMinLengthOfWords(), dict));

        return generateCombinations(numbersLeftNow, possibleCombinations, dict, settings);
    }

    private List<String> lookForFinishedWords(List<String> currentList, int minWordLength, List<String> dict) {
        final List<String> possibleCombinations = new ArrayList<String>();

        for (String value : currentList) {
            final char lastCharacter = value.charAt(value.length() - 1);
            final String lastPart = getLastPartOfCombination(value);

            if (NumberUtils.isDigits("" + lastCharacter)) {
                possibleCombinations.add(value + "-");
            } else {
                possibleCombinations.add(value);
                if (isWord(lastPart, minWordLength, dict)) {
                    possibleCombinations.add(value + "-");
                }
            }
        }

        return possibleCombinations;
    }

    private List<String> appendAsDigit(List<String> currentPossibilities, int currentDigit) {
        final List<String> possibleCombinations = new ArrayList<String>();

        // look if current solutions make sense
        for (String combination : currentPossibilities) {
            char lastCharacter = combination.charAt(combination.length() - 1);

            if (lastCharacter == '-') {
                char foreLastCharacter = combination.charAt(combination.length() - 2);
                if (NumberUtils.isDigits(foreLastCharacter + "")) {
                    possibleCombinations.add(combination.substring(0, combination.length() - 1) + currentDigit);
                } else {
                    possibleCombinations.add(combination + currentDigit);
                }
            }

        }

        return possibleCombinations;
    }

    private List<String> appendAsCharacter(List<String> currentList, int digit, int minWordLength, List<String> dict) {
        final List<String> possibleCombinations = new ArrayList<String>();
        final String[] possibleCharacters = numberToPossibleCharacters(digit);

        for (String existing : currentList) {
            for (String character : possibleCharacters) {
                final String newCombination = existing + character;
                final String lastPart = getLastPartOfCombination(newCombination);

                if (isBeginningOfAWord(lastPart, dict) || isWord(lastPart, minWordLength, dict)) {
                    possibleCombinations.add(newCombination);
                }
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

    private boolean isBeginningOfAWord(String part, List<String> dict) {
        for (String word : dict) {
            if (word.startsWith(part) && !word.equals(part))
                return true;
        }
        return false;
    }

    private boolean isWord(String part, int minLength, List<String> dict) {
        if (part.length() < minLength) {
            return false;
        }
        for (String word : dict) {
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

    private int lentghOfLongestSeriesOfNumbers(String combination) {
        int longest = 0;
        String[] parts = combination.split("-");
        for (String part : parts) {
            if (NumberUtils.isDigits(part) && part.length() > longest) {
                longest = part.length();
            }
        }
        return longest;
    }
}
