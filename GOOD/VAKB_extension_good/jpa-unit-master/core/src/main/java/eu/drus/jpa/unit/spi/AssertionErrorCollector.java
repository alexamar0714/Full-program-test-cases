package eu.drus.jpa.unit.spi;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects all assertion errors which occurred during test execution to report them back at the end
 * of test lifecycle. This approach allows to run through entire test execution and executed all
 * required phases (like cleaning up database at the end of each persistence test) - so called soft
 * assertion.
 *
 * @author <a href="mailto:<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="2d4f4c5f59425e5703404c475e4c466d4a404c4441034e4240">[email protected]</a>">Bartosz Majsak</a>
 */
public class AssertionErrorCollector {
    private final List<String> assertionErrors = new ArrayList<>();

    public void collect(final AssertionError error) {
        collect(error.getMessage());
    }

    public void collect(final String errorMessage) {
        assertionErrors.add(errorMessage);
    }

    public void report() {
        if (assertionErrors.isEmpty()) {
            return;
        }

        throw new AssertionError(createErrorMessage());
    }

    public int amountOfErrors() {
        return assertionErrors.size();
    }

    private String createErrorMessage() {
        final StringBuilder builder = new StringBuilder();

        final int amountOfErrors = amountOfErrors();
        builder.append("Test failed in ").append(amountOfErrors).append(" case").append(amountOfErrors > 1 ? "s" : "").append(". \n");
        for (final String errorMessage : assertionErrors) {
            builder.append(errorMessage).append('\n');
        }
        return builder.toString();
    }

}
