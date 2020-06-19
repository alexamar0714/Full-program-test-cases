package eu.drus.jpa.unit.api;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Verifies state of underlying data store using data sets defined by this annotation. Verification
 * is invoked after test's execution (including transaction if enabled).
 */
@Target({
        TYPE, METHOD
})
@Retention(RUNTIME)
@Inherited
public @interface ExpectedDataSets {

    /**
     * List of data set files used for comparison.
     */
    String[] value();

    /**
     * List of columns to be used for sorting rows to determine order of data sets comparison.
     */
    String[] orderBy() default {};

    /**
     * List of columns to be excluded.
     */
    String[] excludeColumns() default {};

    /**
     * Custom filters to be applied in the specified order. Each concrete implementation is expected
     * to have default non-argument constructor which will be used when creating an instance of the
     * filter. As of today filter are supported for SQL databases only and have to be of type
     * <a href="http://www.dbunit.org/faq.html#columnfilter">IColumnFilter</a>.
     */
    Class<?>[] filter() default {};

    /**
     * Defines whether the performed verification about expected data sets is strict or not. In
     * strict mode all tables and entries not defined in the expected data sets are considered to be
     * an error.
     */
    boolean strict() default false;
}
