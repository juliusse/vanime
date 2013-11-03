package services.converter;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class CombinationTests {

    private List<String> dict;

    @Before
    public void setUp() throws IOException {
        final String wordsDe = IOUtils.toString(CombinationTests.class.getResourceAsStream("/en.dict")).toLowerCase();
        final String[] wordsArrayDe = wordsDe.split("\r\n");
        dict = Arrays.asList(wordsArrayDe);
    }

    @Test
    public void testAddDigit() {
        final ConverterSettings settings = new ConverterSettings(3, 5);
        // test empty combination new digit
        final Combination emptyCombination = new Combination(dict, settings);

        // 2 - 2,a,b,c,2s
        List<Combination> results = emptyCombination.addDigit(2);
        assertThat(results.size()).isEqualTo(5);

        // 0 - o, 0
        results = emptyCombination.addDigit(0);
        assertThat(results.size()).isEqualTo(2);

        // test with a numeric current part
        final Combination combination = new Combination(dict, new ArrayList<String>(), "012", settings);

        // 4 - GHI 4a
        results = combination.addDigit(4);
        assertThat(results.size()).isEqualTo(5);

        // test combination with start of word
        final Combination combination2 = new Combination(dict, new ArrayList<String>(), "ap", settings);

        // 3 - d, e, f
//        results = combination2.addDigit(3);

//        Combination combinationWithApe = null;
//        for (Combination c : results) {
//            if (c.getParts().size() == 1 && c.getParts().get(0).equals("ape") && c.getCurrentPart().isEmpty()) {
//                assertThat(combinationWithApe).isNull();
//                combinationWithApe = c;
//            }
//        }
//        assertThat(combinationWithApe).isNotNull();
//
//        // 5 - JKL
//        results = combinationWithApe.addDigit(5);
//
//        assertThat(results.size()).isEqualTo(4);
//
//        // test combination with numbers in word
//        final Combination combination3 = new Combination(dict, new ArrayList<String>(), "p0o0o", settings);
//        
//        // estimated result with pool
//        results = combination3.addDigit(5);
//
//        Combination combinationWithPool = null;
//        for (Combination c : results) {
//            if (c.getParts().size() == 1 && c.getParts().get(0).equals("p00l") && c.getCurrentPart().isEmpty()) {
//                assertThat(combinationWithPool).isNull();
//                combinationWithPool = c;
//            }
//        }
//        assertThat(combinationWithPool).isNotNull();
    }
}
