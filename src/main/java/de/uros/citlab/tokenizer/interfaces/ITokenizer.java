package de.uros.citlab.tokenizer.interfaces;

import java.util.List;

/**
 * The tokenizer should split the string into its atomic tokens
 *
 * @author gundram
 */
public interface ITokenizer {

    /**
     * tokenize sequence into atomic tokens
     *
     * @param string  input string
     * @return list of tokens, without delimiters/separators
     */
    public List<String> tokenize(String string);

}