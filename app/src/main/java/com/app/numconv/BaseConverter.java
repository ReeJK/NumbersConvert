package com.app.numconv;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;

public final class BaseConverter {
    private static class DigitizedNumber {
        public boolean Negative;
        public Integer[] IntegerDigits;
        public Integer[] FractionDigits;
        public boolean Endless;

        public DigitizedNumber() {
            IntegerDigits = FractionDigits = new Integer[0];
        }

        public DigitizedNumber(String number) {
            Negative = number.charAt(0) == '-';

            int[] offset = { Negative ? 1 : 0 };
            IntegerDigits = Extensions.toArray(parseDigits(number, offset));
            FractionDigits = Extensions.toArray(parseDigits(number, offset));

            if(offset[0] < number.length())
                Endless = true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if(Negative)
                builder.append('-');

            appendDigits(builder, IntegerDigits);

            if(FractionDigits.length > 0) {
                builder.append('.');
                appendDigits(builder, FractionDigits);
            }

            if(Endless)
                builder.append("...");
            return builder.toString();
        }

        private static ArrayList<Integer> parseDigits(String number, int[] offset1) {
            ArrayList<Integer> digits = new ArrayList<>();

            int offset = offset1[0];
            for(; offset < number.length(); offset++) {
                char ch = number.charAt(offset);
                if(ch == '.' || ch == ',') {
                    offset++;
                    break;
                }

                if (ch == '(') {
                    offset++;
                    int endIndex = number.indexOf(')', offset);
                    if (endIndex == -1 || offset == endIndex)
                        throw new NumberFormatException(Extensions.format(R.string.exception_no_custom_digit, offset));

                    String digit = number.substring(offset, endIndex);
                    try {
                        digits.add(Integer.parseInt(digit));
                    } catch (NumberFormatException e) {
                        throw new NumberFormatException(Extensions.format(R.string.exception_cant_parse_digit, ch, offset));
                    }
                    offset = endIndex;
                } else {
                    if ('0' <= ch && ch <= '9') {
                        digits.add(ch - '0');
                    } else if ('a' <= ch && ch <= 'z') {
                        digits.add(ch - 'a' + 10);
                    } else if ('A' <= ch && ch <= 'Z') {
                        digits.add(ch - 'a' + 10);
                    } else {
                        throw new NumberFormatException(Extensions.format(R.string.exception_cant_parse_digit, ch, offset));
                    }
                }
            }

            offset1[0] = offset;
            return digits;
        }

        private static void appendDigits(StringBuilder builder, Integer[] digits) {
            for (Integer digit : digits) {
                builder.append(digitToString(digit));
            }
        }

        public static String digitToString(int digit) {
            if (digit < 0)
                throw new NumberFormatException(Extensions.format(R.string.exception_negative_digit));

            if (digit < 10)
                return String.valueOf((char) ('0' + digit));
            else if (digit <= (10 + 'z' - 'a'))
                return String.valueOf((char) ('a' + digit - 10));
            else
                return String.format("(%d)", digit);
        }
    }

    public static int MaximumFractionSteps = 16;

    private static void checkDigits(Integer[] digits, int base) {
        for (Integer digit : digits) {
            if (digit >= base)
                throw new NumberFormatException(Extensions.format(R.string.exception_out_of_base, base, DigitizedNumber.digitToString(digit)));
        }
    }

    private static int nextIntegerNumber(DigitizedNumber number, int source, int target) {
        int temp = 0;

        for(int i = 0; i < number.IntegerDigits.length; i++) {
            temp = temp * source + number.IntegerDigits[i];
            number.IntegerDigits[i] = temp / target;
            temp = temp % target;
        }

        return temp;
    }

    private static int nextFractionalNumber(DigitizedNumber number, int source, int target) {
        if(number.FractionDigits.length == 0)
            return 0;

        number.FractionDigits[number.FractionDigits.length - 1] *= target;

        for(int i = number.FractionDigits.length - 1; i >= 1; i--) {
            number.FractionDigits[i - 1] *= target;
            number.FractionDigits[i - 1] += number.FractionDigits[i] / source;
            number.FractionDigits[i] %= source;
        }

        int result = number.FractionDigits[0] / source;
        number.FractionDigits[0] %= source;
        return result;
    }

    private static DigitizedNumber convert(DigitizedNumber number, int source, int target) {
        ArrayList<Integer> digits = new ArrayList<>();

        do {
            digits.add(nextIntegerNumber(number, source, target));
        } while (!Extensions.isZero(number.IntegerDigits));

        Collections.reverse(digits);
        DigitizedNumber result = new DigitizedNumber();
        result.Negative = number.Negative;
        result.Endless = number.Endless;
        result.IntegerDigits = Extensions.toArray(digits);

        if (!Extensions.isZero(number.FractionDigits)) {
            digits.clear();
            for (int i = 0; i < MaximumFractionSteps && !Extensions.isZero(number.FractionDigits); i++) {
                digits.add(nextFractionalNumber(number, source, target));
            }

            result.FractionDigits = Extensions.toArray(digits);
            if (!Extensions.isZero(number.FractionDigits))
                result.Endless = true;
        }

        return result;
    }

    public static boolean isValid(String number, int base) {
        try {
            DigitizedNumber dn = new DigitizedNumber(number);
            checkDigits(dn.IntegerDigits, base);
            checkDigits(dn.FractionDigits, base);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String convert(String number, int source, int target) {
        return convert(number, source, target, true);
    }

    public static String convert(String number, int source, int target, boolean endlessDots) {
        DigitizedNumber dn = new DigitizedNumber(number);
        checkDigits(dn.IntegerDigits, source);
        checkDigits(dn.FractionDigits, source);

        DigitizedNumber result = convert(dn, source, target);
        if(!endlessDots)
            result.Endless = false;

        return result.toString();
    }

    public static void getSolutionRepresentation(SolutionBuilder sb, String number, int source) {
        DigitizedNumber dn = new DigitizedNumber(number);
        checkDigits(dn.IntegerDigits, source);
        checkDigits(dn.FractionDigits, source);

        for(int i = 0; i < dn.IntegerDigits.length; i++) {
            if(i > 0 || dn.Negative) {
                if(dn.Negative)
                    sb.minus();
                else
                    sb.plus();
            }

            sb.number(dn.IntegerDigits[i]).cross().number(source).power(dn.IntegerDigits.length - i - 1);
        }

        for(int i = 0; i < dn.FractionDigits.length; i++) {
            if(dn.Negative)
                sb.minus();
            else
                sb.plus();

            sb.number(dn.FractionDigits[i]).cross().number(source).power(-(i + 1));
        }
    }

    public static void getSolutionMid(SolutionBuilder sb, String number, int source) {
        DigitizedNumber dn = new DigitizedNumber(number);

        for(int i = 0; i < dn.IntegerDigits.length; i++) {
            if(i > 0 || dn.Negative) {
                if(dn.Negative)
                    sb.minus();
                else
                    sb.plus();
            }

            BigInteger bi = BigInteger.valueOf(source);
            bi = bi.pow(dn.IntegerDigits.length - i - 1);
            bi = bi.multiply(BigInteger.valueOf(dn.IntegerDigits[i]));
            sb.number(bi);
        }
        for(int i = 0; i < dn.FractionDigits.length; i++) {
            if(dn.Negative)
                sb.minus();
            else
                sb.plus();

            BigDecimal bi = BigDecimal.valueOf(source);
            bi = bi.pow(i + 1);
            bi = BigDecimal.valueOf(dn.FractionDigits[i]).divide(bi, 2, RoundingMode.HALF_UP);
            sb.number(bi);
        }
    }
}
