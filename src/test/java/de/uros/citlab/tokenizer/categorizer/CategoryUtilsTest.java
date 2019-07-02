package de.uros.citlab.tokenizer.categorizer;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoryUtilsTest {
    private static Logger LOG = LoggerFactory.getLogger(CategoryUtilsTest.class);

    @Test
    public void getCategoryGeneral() {
        String category1 = CategoryUtils.getCategory('a');
        String category2 = CategoryUtils.getCategory('\u1C88');
        Assert.assertEquals("character has wrong Unicode category", "Ll", category1);
        Assert.assertEquals("character has wrong Unicode category", "Ll", category2);
    }
}