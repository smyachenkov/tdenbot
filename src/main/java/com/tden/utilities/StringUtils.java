package com.tden.utilities;

/**
 * Created by Stanislav Myachenkov on 10.12.2016.
 */
public class StringUtils {

    public static int nthIndexOf(final String string,
                                 final String token,
                                 final int index) {
        int j = 0;
        for (int i = 0; i < index; i++) {
            j = string.indexOf(token, j + 1);
            if (j == -1) break;
        }
        return j;
    }
}

