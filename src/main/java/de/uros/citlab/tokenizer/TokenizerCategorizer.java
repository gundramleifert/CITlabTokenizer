/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uros.citlab.tokenizer;

import de.uros.citlab.tokenizer.interfaces.ICategorizer;
import de.uros.citlab.tokenizer.interfaces.ITokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * This tokenizer uses a categorizer to tokenize a String. The main idea is that
 * the String is divided into a sequence of codepoints/characters. From the
 * {@link ICategorizer} each codepoint has a category and can be an isolated
 * token or/and a delimiter/separator. Tokens are separated by
 * delimiters/separators (see {@link ICategorizer#isDelimiter(char)}.
 * Codepoints, which are delimiters are not a token itself. When there are two
 * subsequent delimiters, there is no empty token. Tokens with different
 * categories (see {@link ICategorizer#getCategory(char)} are split into own
 * tokens. If a codepoint is isolated (see
 * {@link ICategorizer#isIsolated(char)}, the single codepoint is a token and
 * cannot be combined with other codepoints to other tokens, even if they have
 * the same category.
 *
 * @author gundram
 */
public class TokenizerCategorizer implements ITokenizer {

    private final ICategorizer categorizer;
    public static Logger LOG = LoggerFactory.getLogger(TokenizerCategorizer.class.getName());

    public TokenizerCategorizer(ICategorizer categorizer) {
        this.categorizer = categorizer;
    }

    /**
     * tokenize the String
     *
     * @param string input string
     * @return list of tokens
     */
    @Override
    public List<String> tokenize(String string) {
        List<String> res = new LinkedList<>();
        if (string.isEmpty()) {
            return res;
        }
        char[] toCharArray = string.toCharArray();
        StringBuilder sb = new StringBuilder();
        char aChar = toCharArray[0];
        if (!categorizer.isDelimiter(aChar)) {
            sb.append(toCharArray[0]);
        }
        boolean doSeperate = categorizer.isIsolated(aChar);
        String category = categorizer.getCategory(aChar);
        for (int i = 1; i < toCharArray.length; i++) {
            final char c = toCharArray[i];
            final String categoryCurrent = categorizer.getCategory(c);
            if (doSeperate || !categoryCurrent.equals(category) || categorizer.isIsolated(c)) {
                String token = sb.toString();
                if (!token.isEmpty()) {
                    res.add(token);
                    sb = new StringBuilder();
                }
                category = categoryCurrent;
            }
            if (!categorizer.isDelimiter(c)) {
                sb.append(c);
                doSeperate = categorizer.isIsolated(c);
            }
        }
        String token = sb.toString();
        if (!token.isEmpty()) {
            res.add(token);
        }
        return res;
    }

}
