/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uros.citlab.tokenizer;

import de.uros.citlab.tokenizer.interfaces.ITokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * The TokenizerConfig is used to tokenized strings. The main idea is that is being used with a configuration file. If different use cases call for different types of tokenization, the same tokenizer with different configuration files can be used.
 * <p>
 * Rules are: - Normalization - Dehyphanation signs - Delimiter signs - Delimiter signs being kept as tokens
 * <p>
 * Further explanation:
 * <p>
 * - Normalization: The Java normalizer tackles the representation problem of characters like á or ö. These characters can be represented as a single character (á or ö) or as a basic character with additional diacritic. The java normalizer changes the representation to either representation type.
 * <p>
 * - Dehypenation signs When a word at the end of the line is being cut off and continued on the next line, there often is a hyphenation sign. The tokenizer looks for a given set of files, a following \n and a following small letter in the next line. If that expression is found, the split up word is being put together.
 * <p>
 * - Delimiter signs Delemiter are used for splitting tokens. Common signs among others are spaces, newlines and dots.
 * <p>
 * - Delimiter signs being kept as tokens When there is a token like 'is, ', the user may be interested in getting 'is' as a token and the comma as a dedicated token.
 *
 * @author max
 */
public class TokenizerConfig implements ITokenizer {
    private static final Logger LOG = LoggerFactory.getLogger(TokenizerConfig.class.getName());
    //    public enum NormalizerOption
//    {
//
//        None,
//        NFC,
//        NFD
//    }
    private final Properties properties;

    public TokenizerConfig() {
        properties = new Properties();
    }

    public TokenizerConfig(Properties properties) {
        this.properties = properties;
    }

    public TokenizerConfig(String pathToConfig) {
        this(new File(pathToConfig));
    }

    public TokenizerConfig(File configFile) {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(configFile));
        } catch (IOException ex) {
            LOG.error("Could not load given property file with path: {} ", configFile.getAbsolutePath(), ex);
            throw new RuntimeException("Could not load given property file with path: " + configFile.getAbsolutePath(), ex);
        }
    }

    @Override
    public List<String> tokenize(String text) {
        String normalizerString = properties.getProperty("normalizer", null);
        Normalizer.Form normalizer = null;
        if (normalizerString != null && !normalizerString.equals("None")) {
            switch (normalizerString) {
                case "NFC":
                    normalizer = Normalizer.Form.NFC;
                    break;
                case "NFD":
                    normalizer = Normalizer.Form.NFD;
                    break;
                case "NFKC":
                    normalizer = Normalizer.Form.NFKC;
                    break;
                case "NFKD":
                    normalizer = Normalizer.Form.NFKD;
                    break;
            }
        }

        String dehyphenationSigns = properties.getProperty("dehyphenation_signs", "");
        String delimiterSigns = properties.getProperty("delimiter_signs", "\n ");
        String keepDelimiterSigns = properties.getProperty("keep_delimiter_signs", "");
        boolean tokenizeCharacterWise = properties.getProperty("tokenize_character_wise", "false").equals("true");
        boolean splitAllDelimiterSigns = properties.getProperty("split_all_delimiter_signs", "true").equals("true");
        boolean keepAllDelimiterSigns = properties.getProperty("keep_all_delimiter_signs", "true").equals("true");
        boolean splitNumbers = properties.getProperty("split_numbers", "true").equals("true");

        return tokenize(text, normalizer, dehyphenationSigns, delimiterSigns, keepDelimiterSigns, splitAllDelimiterSigns, keepAllDelimiterSigns, tokenizeCharacterWise, splitNumbers);
    }

    public String normalize(String text, Normalizer.Form normalizer) {
        if (normalizer == null) {
            return text;
        }
        return Normalizer.normalize(text, normalizer);
    }

    public List<String> tokenize(
            String text,
            Normalizer.Form normalizer,
            String dehyphenizationSignsString,
            String delimiterSignsString,
            String keepDelimiterSignsString,
            boolean splitAllDelimiterSigns,
            boolean keepAllDelimiterSigns,
            boolean tokenizeCharacterWise,
            boolean splitNumbers) {
        if (normalizer != null) {
            text = normalize(text, normalizer);
        }

        List<Character> dehyphenizationSigns = createSignListFromString(dehyphenizationSignsString);
        List<Character> delimiterSigns = createSignListFromString(delimiterSignsString);
        List<Character> keepDelimiterSigns = createSignListFromString(keepDelimiterSignsString);
        List<String> tokenizedText = new ArrayList<>();

        int lenText = text.length();
        char charCurrent;
        char charPrev;
        char charNext;
        char charNext2;

        StringBuilder nextToken = new StringBuilder();
        boolean dehyphenize = false;
        boolean finishToken = false;

        for (int index = 0; index < lenText; index++) {
            charCurrent = text.charAt(index);
            boolean isPunctuation = Pattern.matches("\\p{Punct}", Character.toString(charCurrent));

            if (tokenizeCharacterWise) {
                tokenizedText.add(Character.toString(charCurrent));
            } else {
                if (index + 2 < lenText && dehyphenizationSigns.contains(charCurrent)) {

                    charNext = text.charAt(index + 1);
                    if ("\n".charAt(0) == charNext) {

                        charNext2 = text.charAt(index + 2);
                        if (isLowercase(charNext2)) {
                            dehyphenize = true;
                            continue;
                        }
                    }
                }

                boolean isSplit = splitNumbers && index > 0
                        && Character.isDigit(charCurrent) != Character.isDigit(text.charAt(index - 1))
                        && !isPunctuation
                        && !Pattern.matches("\\p{Punct}", Character.toString(text.charAt(index - 1)))
                        && !delimiterSigns.contains(charCurrent)
                        && !delimiterSigns.contains(charCurrent);
                finishToken |= isSplit;

                if (splitAllDelimiterSigns && isPunctuation) {
                    finishToken = true;
                }

                dehyphenize = !delimiterSigns.contains(charCurrent);
                if (!finishToken) {
                    finishToken = !dehyphenize;
                }

                if (finishToken) {
                    if (nextToken.toString().length() > 0) {
                        tokenizedText.add(nextToken.toString());
                        nextToken = new StringBuilder();
                    }

                    if (keepDelimiterSigns.contains(charCurrent)
                            || (keepAllDelimiterSigns && isPunctuation)) {
                        tokenizedText.add(Character.toString(charCurrent));
                    }
                    if (isSplit) {
                        nextToken.append(charCurrent);
                    }
                    finishToken = false;
                } else {
                    nextToken.append(charCurrent);
                }
            }
        }

        if (nextToken.toString().length() > 0) {
            tokenizedText.add(nextToken.toString());
        }

        return tokenizedText;
    }

    private List<Character> createSignListFromString(String signString) {
        int numSigns = signString.length();
        List<Character> signList = new ArrayList<>(numSigns);

        for (int index = 0; index < numSigns; index++) {
            signList.add(signString.charAt(index));
        }

        return signList;
    }

    private boolean isLowercase(char c) {
        return Character.toLowerCase(c) == c;
    }
}
