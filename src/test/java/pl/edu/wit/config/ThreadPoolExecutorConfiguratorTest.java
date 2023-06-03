package pl.edu.wit.config;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * @author Katarzyna Nowak
 */
class ThreadPoolExecutorConfiguratorTest {

    @Test
    void threadPoolConfiguratorTest_poolSizePropertyAvailable() {
        // given
        PropertySource propertySource = mock(PropertySource.class);
        doReturn("5").when(propertySource).getProperty("pool-size");

        // when
        ExecutorService actual = ExecutorConfigurator.getConfiguredExecutor(propertySource);

        // then
        assertEquals(actual.getClass(), ThreadPoolExecutor.class);

        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) actual;
        assertEquals(5, threadPoolExecutor.getCorePoolSize());
    }

    @Test
    void threadPoolConfiguratorTest_poolSizePropertyUnavailable() {
        // given
        PropertySource propertySource = mock(PropertySource.class);
        doReturn(null).when(propertySource).getProperty("pool-size");

        // when
        ExecutorService actual = ExecutorConfigurator.getConfiguredExecutor(propertySource);

        // then
        assertEquals(actual.getClass(), ThreadPoolExecutor.class);

        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) actual;
        assertEquals(3, threadPoolExecutor.getCorePoolSize());
    }

    @Test
    void threadPoolConfiguratorTest_propertySourceNull() {
        // when
        ExecutorService actual = ExecutorConfigurator.getConfiguredExecutor(null);

        // then
        assertEquals(actual.getClass(), ThreadPoolExecutor.class);

        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) actual;
        assertEquals(3, threadPoolExecutor.getCorePoolSize());
    }
}