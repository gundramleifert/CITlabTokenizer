package de.uros.citlab.tokenizer.categorizer;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategorizerWordMergeGroupsTest {
    static Logger logger = LoggerFactory.getLogger(CategorizerWordMergeGroupsTest.class.getName());

    @Test
    public void testCategorizer() {
        String property = System.getProperty("java.version");
        System.out.println(property);
        CategorizerWordMergeGroups cat = new CategorizerWordMergeGroups();
        char uk = '\u1C88';
        char te = '\u1C85';
        logger.info("char = " + uk);
        logger.info("Category: " + CategoryUtils.getCategory(uk));
        Assert.assertEquals("Ll",  CategoryUtils.getCategory(uk));
        try {
            cat.getCategory(uk);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info("char = " + te);
        logger.info("Category: " + CategoryUtils.getCategory(te));
        Assert.assertEquals("Ll",  CategoryUtils.getCategory(te));
        try {
            cat.getCategory(te);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}