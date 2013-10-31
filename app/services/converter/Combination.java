package services.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

public class Combination {

    private final List<String> dict;
    private final List<String> parts;
    private final String currentPart;
    private final boolean isCurrentPartNumeric;
    private final boolean isLastPartNumeric;
    private final ConverterSettings settings;

    public Combination(List<String> dict, ConverterSettings settings) {
        this.dict = dict;
        parts = new ArrayList<>();
        currentPart = "";
        this.settings = settings;
        isCurrentPartNumeric = true;
        isLastPartNumeric = false;

    }

    Combination(List<String> dict, List<String> parts, String currentPart, ConverterSettings settings) {
        this.dict = dict;
        this.parts = parts;
        this.currentPart = currentPart;
        this.settings = settings;
        this.isCurrentPartNumeric = currentPart.matches("[0-9]+");
        this.isLastPartNumeric = parts.size() == 0 ? false : parts.get(parts.size() - 1).matches("[0-9]+");
    }

    public String getCurrentPart() {
        return currentPart;
    }

    public List<String> getParts() {
        return parts;
    }

    public boolean isCurrentPartNumeric() {
        return isCurrentPartNumeric;
    }

    public List<Combination> addDigit(int digit) {
        final List<Combination> newCombinations = new ArrayList<>();

        final String[] possibleCharacters = numberToPossibleCharacters(digit);

        if (currentPart.isEmpty()) {
            // last part was a word, so now a new combination with a number has
            // to start from here
            if (!isLastPartNumeric) {
                newCombinations.add(new Combination(dict, parts, "" + digit, settings));
            }
            for (String cha : possibleCharacters) {
                newCombinations.add(new Combination(dict, parts, cha, settings));
            }
        } else {
            if (isCurrentPartNumeric) {
                if (currentPart.length() < settings.getMaxSeriesOfDigits()) {
                    // currently a numeric part, add digit
                    newCombinations.add(new Combination(dict, parts, currentPart + digit, settings));
                }

                for (String cha : possibleCharacters) {
                    newCombinations.add(Combination.fromCombination(dict, settings, parts, currentPart, cha));
                }

            } else {
                for (String cha : possibleCharacters) {
                    final String extendedPart = currentPart + cha;
                    if (isWord(extendedPart)) {

                        String correctString = "";
                        char[] chars = extendedPart.toCharArray();
                        for (int i = 0; i < chars.length; i++) {
                            correctString += chars[i];
                            if (NumberUtils.isDigits(chars[i] + "")) {
                                i++;
                            }
                        }
                        newCombinations.add(Combination.fromCombination(dict, settings, parts, correctString, ""));
                    }

                    if (isBeginningOfAWord(extendedPart)) {
                        newCombinations.add(new Combination(dict, parts, extendedPart, settings));
                    }
                }

            }

        }
        return newCombinations;
    }

    private boolean isBeginningOfAWord(String part) {
        String partToTest = part.replaceAll("\\d", "");
        for (String word : dict) {
            if (word.startsWith(partToTest) && !word.equals(partToTest))
                return true;
        }
        return false;
    }

    private boolean isWord(String part) {
        if (part.startsWith("reces2")) {
            int i = 1;
        }
        String partToTest = part.replaceAll("\\d", "");
        if (partToTest.length() < settings.getMinLengthOfWords()) {
            System.out.println(part + "; " + partToTest + "; " + settings.getMinLengthOfWords());
            return false;
        }

        return dict.contains(partToTest);
    }

    private static String[] numberToPossibleCharacters(int digit) {
        switch (digit) {
        case 0:
            return new String[] { "0o" };
        case 1:
            return new String[] { "1i" };
        case 2:
            return new String[] { "a", "b", "c", "2s" };
        case 3:
            return new String[] { "d", "e", "f" };
        case 4:
            return new String[] { "g", "h", "i", "4a" };
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

    private static Combination fromCombination(List<String> dict, ConverterSettings settings, List<String> parts, String newPart, String startOfCurrent) {
        final List<String> newPartsList = new ArrayList<String>(parts);
        newPartsList.add(newPart);
        return new Combination(dict, newPartsList, startOfCurrent, settings);
    }

    @Override
    public String toString() {
        String s = "";
        for (String part : parts) {
            s += part + "-";
        }
        s += currentPart;
        if (s.endsWith("-"))
            s = s.substring(0, s.length() - 1);
        return s;
    }
}