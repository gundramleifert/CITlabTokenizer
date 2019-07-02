/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uros.citlab.tokenizer.categorizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

/**
 * Utility class that maps each unicode point to its general category category
 *
 * @author gundram
 */
public class CategoryUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryUtils.class.getName());
    private static TreeMap<Integer, Integer> leftRight;
    private static TreeMap<Integer, String> leftCategory;

    static {
        loadCategoryMap();
    }

    /**
     * returns the category of the codepoint. The return value has 2 characters.
     * The first (uppercase) character determines the main general category, the
     * second (lowercase) determines the sub category.
     *
     * @param c character
     * @return category
     */
    public static final String getCategory(char c) {
        int point = (int) c;
        Integer value = leftRight.floorEntry(point).getValue();
        if (point <= value) {
            return leftCategory.floorEntry(point).getValue();
        }
        return "Cn";
//        switch (Character.getType(c)) {
//            case Character.CONTROL:
//                return "Cc";
//            case Character.FORMAT:
//                return "Cf";
//            case Character.UNASSIGNED:
//                return "Cn";
//            case Character.PRIVATE_USE:
//                return "Co";
//            case Character.SURROGATE:
//                return "Cs";
//
//            case Character.LOWERCASE_LETTER:
//                return "Ll";
//            case Character.MODIFIER_LETTER:
//                return "Lm";
//            case Character.OTHER_LETTER:
//                return "Lo";
//            case Character.TITLECASE_LETTER:
//                return "Lt";
//            case Character.UPPERCASE_LETTER:
//                return "Lu";
//
//            case Character.COMBINING_SPACING_MARK:
//                return "Mc";
//            case Character.ENCLOSING_MARK:
//                return "Me";
//            case Character.NON_SPACING_MARK:
//                return "Mn";
//
//            case Character.DECIMAL_DIGIT_NUMBER:
//                return "Nd";
//            case Character.LETTER_NUMBER:
//                return "Nl";
//            case Character.OTHER_NUMBER:
//                return "No";
//
//            case Character.CONNECTOR_PUNCTUATION:
//                return "Pc";
//            case Character.DASH_PUNCTUATION:
//                return "Pd";
//            case Character.END_PUNCTUATION:
//                return "Pe";
//            case Character.FINAL_QUOTE_PUNCTUATION:
//                return "Pf";
//            case Character.INITIAL_QUOTE_PUNCTUATION:
//                return "Pi";
//            case Character.OTHER_PUNCTUATION:
//                return "Po";
//            case Character.START_PUNCTUATION:
//                return "Ps";
//
//            case Character.CURRENCY_SYMBOL:
//                return "Sc";
//            case Character.MODIFIER_SYMBOL:
//                return "Sk";
//            case Character.MATH_SYMBOL:
//                return "Sm";
//            case Character.OTHER_SYMBOL:
//                return "So";
//
//            case Character.LINE_SEPARATOR:
//                return "Zl";
//            case Character.PARAGRAPH_SEPARATOR:
//                return "Zp";
//            case Character.SPACE_SEPARATOR:
//                return "Zs";
//            default:
//                LOG.log(Level.SEVERE, "no category found for {0} - maybe bug in code.", Character.getName(c));
//                return null;
//
//        }
    }


    private static void loadCategoryMap() {
        leftRight = new TreeMap<>();
        leftCategory = new TreeMap<>();
        try (BufferedReader lis = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("DerivedGeneralCategory.txt")))) {
            String s;
            while ((s = lis.readLine()) != null) {
                if (s.isEmpty() || s.startsWith("#")) {
                    continue;
                }
                int idxDot = s.indexOf(".");
                int idxSpace = s.indexOf(" ");
                int i = s.indexOf(";");
                boolean range = idxDot > 0 && idxDot < i;
                int idx = range ? idxDot : idxSpace;
                int codepointLeft = Integer.parseInt(s.substring(0, idx), 16);
                int codepointRight = range ? Integer.parseInt(s.substring(idxDot + 2, i).trim(), 16) : codepointLeft;
                String cat = s.substring(i + 2, i + 4);
                leftRight.put(codepointLeft, codepointRight);
                leftCategory.put(codepointLeft, cat);
            }
            for (Map.Entry<Integer, Integer> integerIntegerEntry : leftRight.entrySet()) {
                while (!leftRight.floorKey(integerIntegerEntry.getValue()).equals(integerIntegerEntry.getKey())) {
                    LOG.error("for entry {} get left bound {} - set right bound to {}", integerIntegerEntry, leftRight.floorKey(integerIntegerEntry.getValue()), leftRight.floorKey(integerIntegerEntry.getValue()));
                    //                    throw new RuntimeException("error while loading");
                    leftRight.replace(integerIntegerEntry.getKey(), integerIntegerEntry.getValue(), leftRight.floorKey(integerIntegerEntry.getValue() - 1));
                }

            }

        } catch (IOException e) {
            LOG.error("cannot load resource - categorizer (and probably tokenizer) will not work correctly");
        }
    }

    /**
     * returns the general category of the codepoint. The return value has one
     * (uppercase) character which determines the main general category.
     *
     * @param c character
     * @return category
     */
    public static String getCategoryGeneral(char c) {
        return getCategory(c).substring(0, 1);
//        switch (Character.getType(c)) {
//            case Character.CONTROL:
//            case Character.FORMAT:
//            case Character.UNASSIGNED:
//            case Character.PRIVATE_USE:
//            case Character.SURROGATE:
//                return "C";
//            case Character.LOWERCASE_LETTER:
//            case Character.MODIFIER_LETTER:
//            case Character.OTHER_LETTER:
//            case Character.TITLECASE_LETTER:
//            case Character.UPPERCASE_LETTER:
//                return "L";
//            case Character.COMBINING_SPACING_MARK:
//            case Character.ENCLOSING_MARK:
//            case Character.NON_SPACING_MARK:
//                return "M";
//            case Character.DECIMAL_DIGIT_NUMBER:
//            case Character.LETTER_NUMBER:
//            case Character.OTHER_NUMBER:
//                return "N";
//            case Character.CONNECTOR_PUNCTUATION:
//            case Character.DASH_PUNCTUATION:
//            case Character.END_PUNCTUATION:
//            case Character.FINAL_QUOTE_PUNCTUATION:
//            case Character.INITIAL_QUOTE_PUNCTUATION:
//            case Character.OTHER_PUNCTUATION:
//            case Character.START_PUNCTUATION:
//                return "P";
//            case Character.CURRENCY_SYMBOL:
//            case Character.MODIFIER_SYMBOL:
//            case Character.MATH_SYMBOL:
//            case Character.OTHER_SYMBOL:
//                return "S";
//            case Character.LINE_SEPARATOR:
//            case Character.PARAGRAPH_SEPARATOR:
//            case Character.SPACE_SEPARATOR:
//                return "Z";
//            default:
//                LOG.error("no category found for {} - maybe bug in code.", Character.getName(c));
//                return null;
//
//        }
    }
}
