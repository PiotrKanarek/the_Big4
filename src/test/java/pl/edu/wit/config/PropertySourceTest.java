package pl.edu.wit.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Katarzyna Nowak
 */
class PropertySourceTest {

    @Test
    void getPropertyTest_validSource() {
        // given
        PropertySource propertySource = new PropertySource("src/test/resources/test.properties");

        // when
        String actual = propertySource.getProperty("pool-size");

        // then
        assertEquals("5", actual);
    }

    @Test
    void getPropertyTest_invalidSource() {
        // given
        PropertySource propertySource = new PropertySource("nonexistent");

        // when
        String actual = propertySource.getProperty("");

        // then
        assertNull(actual);
    }

    @Test
    void getPropertyTest_nullSource() {
        // given
        PropertySource propertySource = new PropertySource(null);

        // when
        String actual = propertySource.getProperty("");

        // then
        assertNull(actual);
    }
}