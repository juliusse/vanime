package utils;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<String> cloneAndAdd(List<String> list, String newItem) {
        final List<String> clonedList = new ArrayList<String>(list);
        clonedList.add(newItem);
        return clonedList;
    }
}
