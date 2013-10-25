package services.converter;

public class ConverterSettings {

    private final int minLengthOfWords;
    private final int maxSeriesOfDigits;

    public ConverterSettings(int minLengthOfWords, int maxSeriesOfDigits) {
        super();
        this.minLengthOfWords = minLengthOfWords;
        this.maxSeriesOfDigits = maxSeriesOfDigits;
    }

    public int getMinLengthOfWords() {
        return minLengthOfWords;
    }

    public int getMaxSeriesOfDigits() {
        return maxSeriesOfDigits;
    }

}
