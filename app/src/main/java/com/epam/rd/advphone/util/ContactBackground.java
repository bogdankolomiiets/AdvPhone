package com.epam.rd.advphone.util;

import android.content.Context;

import com.epam.rd.advphone.R;

public class ContactBackground {
    private static int i;
    private static char previousChar;
    private static int[] arrayOfColors;


    public static int getColor(Context context, char presentChar) {
        if (arrayOfColors == null) {
            arrayOfColors = context.getResources().getIntArray(R.array.colorPaletteForContacts);
        }

        if (previousChar != presentChar) {
            previousChar = presentChar;
            if (i < arrayOfColors.length - 1) i++;
            else i=0;
        }
        return arrayOfColors[i];
    }
}
