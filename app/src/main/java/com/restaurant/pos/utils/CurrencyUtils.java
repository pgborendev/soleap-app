package com.restaurant.pos.utils;
import java.text.NumberFormat;
import java.util.Locale;
public class CurrencyUtils {
    public static String format(double amount){return String.format(Locale.US,"$%.2f",amount);}
    public static String formatKHR(double usd){return String.format(Locale.US,"%,.0f ៛",usd*4100);}
}
