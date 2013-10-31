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
            final List<Combination> newCombiniations = new ArrayList<>();

            for (Combination combination : combinations) {
                newCombiniations.addAll(combination.addDigit(digit));
            }
            combinations = newCombiniations;
        }

        final List<String> postProcessedList = new ArrayList<>();
        for (Combination combination : combinations) {
            if (combination.getCurrentPart().isEmpty() || combination.isCurrentPartNumeric()) {
                postProcessedList.add(combination.toString());
            }
        }

        return postProcessedList;
    }

    // private List<String> generateCombinations(String numbersLeft,
    // List<String> existingCombinations, List<String> dict, ConverterSettings
    // settings) {
    // if (numbersLeft.length() == 0) {
    // return existingCombinations;
    // }
    //
    // final int currentDigit = Integer.parseInt(numbersLeft.charAt(0) + "");
    // final String numbersLeftNow = numbersLeft.substring(1);
    //
    // final List<String> extendedList = new ArrayList<>();
    //
    // for (String value : existingCombinations) {
    // final String lastPart = getLastPartOfCombination(value);
    // final boolean isLastPartPureNumeric = !lastPart.matches(".*[^0-9].*");
    // final boolean isLastPartPossibleWord = !lastPart.matches(".*[2-9].*");
    // final boolean isCurrentDigitUsedAsChar =
    // isDigitUsedAsCharacter(currentDigit);
    //
    // // Logger.debug(isLastPartPureNumeric+"; "+value);
    // if (isLastPartPureNumeric) {
    // // extend row of digits
    // if (settings.getMaxSeriesOfDigits() >= lastPart.length() + 1) {
    // extendedList.add(value + currentDigit);
    // }
    //
    // if (isLastPartPossibleWord) {
    // extendedList.addAll(appendAsCharacter(value, currentDigit,
    // settings.getMinLengthOfWords(), dict));
    // }
    //
    // // start a new part with currentDigit
    // final String valueWithNewPart = value + "-";
    // extendedList.addAll(appendAsCharacter(valueWithNewPart, currentDigit,
    // settings.getMinLengthOfWords(), dict));
    // } else {
    // if (isWord(lastPart, settings.getMinLengthOfWords(), dict)) {
    // final String valueWithNewPart = value + "-";
    // extendedList.addAll(appendAsCharacter(valueWithNewPart, currentDigit,
    // settings.getMinLengthOfWords(), dict));
    // }
    //
    // extendedList.addAll(appendAsCharacter(value, currentDigit,
    // settings.getMinLengthOfWords(), dict));
    // }
    // }
    //
    // // }
    // // final List<String> listWithFinishedWordsAtEnd =
    // // lookForFinishedWords(existingCombinations,
    // // settings.getMinLengthOfWords(), dict);
    // // final List<String> possibleCombinations = new ArrayList<String>();
    // //
    // // possibleCombinations.addAll(appendAsDigit(listWithFinishedWordsAtEnd,
    // // currentDigit));
    // //
    // //
    // possibleCombinations.addAll(appendAsCharacter(listWithFinishedWordsAtEnd,
    // // currentDigit, settings.getMinLengthOfWords(), dict));
    // //
    // return generateCombinations(numbersLeftNow, extendedList, dict,
    // settings);
    // }
    //
    // private List<String> lookForFinishedWords(List<String> currentList, int
    // minWordLength, List<String> dict) {
    // final List<String> possibleCombinations = new ArrayList<String>();
    //
    // for (String value : currentList) {
    // final char lastCharacter = value.charAt(value.length() - 1);
    // final String lastPart = getLastPartOfCombination(value);
    //
    // if (NumberUtils.isDigits("" + lastCharacter)) {
    // possibleCombinations.add(value + "-");
    //
    // if (lastCharacter == '0' || lastCharacter == '1') {
    // possibleCombinations.add(value);
    // }
    // } else {
    // possibleCombinations.add(value);
    // if (isWord(lastPart, minWordLength, dict)) {
    // possibleCombinations.add(value + "-");
    // }
    // }
    //
    // }
    //
    // return possibleCombinations;
    // }
    //
    // private List<String> appendAsDigit(List<String> currentPossibilities, int
    // currentDigit) {
    // final List<String> possibleCombinations = new ArrayList<String>();
    //
    // // look if current solutions make sense
    // for (String combination : currentPossibilities) {
    // char lastCharacter = combination.charAt(combination.length() - 1);
    //
    // if (lastCharacter == '-') {
    // char foreLastCharacter = combination.charAt(combination.length() - 2);
    // if (NumberUtils.isDigits(foreLastCharacter + "")) {
    // possibleCombinations.add(combination.substring(0, combination.length() -
    // 1) + currentDigit);
    // } else {
    // possibleCombinations.add(combination + currentDigit);
    // }
    // }
    //
    // }
    //
    // return possibleCombinations;
    // }
    //
    // private List<String> appendAsCharacter(String currentCombination, int
    // nextDigit, int minWordLenght, List<String> dict) {
    // final List<String> possibleCombinations = new ArrayList<String>();
    // final String[] possibleCharacters =
    // numberToPossibleCharacters(nextDigit);
    // final String lastPart = getLastPartOfCombination(currentCombination);
    //
    // if (lastPart.length() == 0) {
    // for (String character : possibleCharacters) {
    // possibleCombinations.add(currentCombination + character);
    // }
    // } else {
    // for (String character : possibleCharacters) {
    // if (isBeginningOfAWord(lastPart + character, dict) || isWord(lastPart +
    // character, minWordLenght, dict)) {
    // possibleCombinations.add(currentCombination + character);
    // }
    // }
    // }
    //
    // return possibleCombinations;
    // }
    //
    // private List<String> appendAsCharacter(List<String> currentList, int
    // digit, int minWordLength, List<String> dict) {
    // final List<String> possibleCombinations = new ArrayList<String>();
    // final String[] possibleCharacters = numberToPossibleCharacters(digit);
    //
    // for (String existing : currentList) {
    // for (String character : possibleCharacters) {
    // final String newCombination = existing + character;
    // final String lastPart = getLastPartOfCombination(newCombination);
    //
    // if (isBeginningOfAWord(lastPart, dict) || isWord(lastPart, minWordLength,
    // dict)) {
    // possibleCombinations.add(newCombination);
    // }
    // }
    // }
    //
    // return possibleCombinations;
    // }
    //
    // private String getLastPartOfCombination(String combination) {
    // final int lastSeparatorPos = combination.lastIndexOf("-");
    // if (lastSeparatorPos >= 0) {
    // return combination.substring(lastSeparatorPos + 1);
    // } else {
    // return combination;
    // }
    // }
    //
    // private int lentghOfLongestSeriesOfNumbers(String combination) {
    // int longest = 0;
    // String[] parts = combination.split("-");
    // for (String part : parts) {
    // if (NumberUtils.isDigits(part) && part.length() > longest) {
    // longest = part.length();
    // }
    // }
    // return longest;
    // }
}
