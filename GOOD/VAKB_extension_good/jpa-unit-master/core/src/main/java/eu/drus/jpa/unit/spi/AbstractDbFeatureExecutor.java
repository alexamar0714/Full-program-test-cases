package eu.drus.jpa.unit.spi;

import static eu.drus.jpa.unit.util.ResourceLocator.getResource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import eu.drus.jpa.unit.api.CleanupStrategy;
import eu.drus.jpa.unit.api.DataSeedStrategy;
import eu.drus.jpa.unit.api.ExpectedDataSets;

public abstract class AbstractDbFeatureExecutor<D, C> {

    private final FeatureResolver featureResolver;
    private List<D> initialDataSets;

    protected AbstractDbFeatureExecutor(final FeatureResolver featureResolver) {
        this.featureResolver = featureResolver;
    }

    public void executeBeforeTest(final C connection) throws DbFeatureException {
        getCleanUpBeforeFeature().execute(connection);
        getCleanupUsingScriptBeforeFeature().execute(connection);
        getApplyCustomScriptBeforeFeature().execute(connection);
        getSeedDataFeature().execute(connection);
    }

    public void executeAfterTest(final C connection, final boolean testHasErrors) throws DbFeatureException {
        try {
            if (!testHasErrors) {
                getVerifyDataAfterFeature().execute(connection);
            }
        } finally {
            getApplyCustomScriptAfterFeature().execute(connection);
            getCleanupUsingScriptAfterFeature().execute(connection);
            getCleanUpAfterFeature().execute(connection);
        }
    }

    protected abstract List<D> loadDataSets(final List<String> paths);

    protected abstract DbFeature<C> createCleanupFeature(CleanupStrategy cleanupStrategy, List<D> initialDataSets);

    protected abstract DbFeature<C> createApplyCustomScriptFeature(List<String> scriptPaths);

    protected abstract DbFeature<C> createSeedDataFeature(DataSeedStrategy dataSeedStrategy, List<D> initialDataSets);

    protected abstract DbFeature<C> createVerifyDataAfterFeature(ExpectedDataSets expectedDataSets);

    protected String loadScript(final String scriptPath) throws IOException, URISyntaxException {
        final URL url = getResource(scriptPath);
        return new String(Files.readAllBytes(Paths.get(url.toURI()))).trim();
    }

    private List<D> getInitialDataSets() {
        if (initialDataSets == null) {
            initialDataSets = loadDataSets(featureResolver.getSeedData());
        }
        return initialDataSets;
    }

    protected DbFeature<C> getCleanUpBeforeFeature() {
        if (featureResolver.shouldCleanupBefore()) {
            return createCleanupFeature(featureResolver.getCleanupStrategy(), getInitialDataSets());
        } else {
            return new NopFeature<>();
        }
    }

    protected DbFeature<C> getCleanUpAfterFeature() {
        if (featureResolver.shouldCleanupAfter()) {
            return createCleanupFeature(featureResolver.getCleanupStrategy(), getInitialDataSets());
        } else {
            return new NopFeature<>();
        }
    }

    protected DbFeature<C> getCleanupUsingScriptBeforeFeature() {
        if (featureResolver.shouldCleanupUsingScriptBefore()) {
            return createApplyCustomScriptFeature(featureResolver.getCleanupScripts());
        } else {
            return new NopFeature<>();
        }
    }

    protected DbFeature<C> getCleanupUsingScriptAfterFeature() {
        if (featureResolver.shouldCleanupUsingScriptAfter()) {
            return createApplyCustomScriptFeature(featureResolver.getCleanupScripts());
        } else {
            return new NopFeature<>();
        }
    }

    protected DbFeature<C> getApplyCustomScriptBeforeFeature() {
        if (featureResolver.shouldApplyCustomScriptBefore()) {
            return createApplyCustomScriptFeature(featureResolver.getPreExecutionScripts());
        } else {
            return new NopFeature<>();
        }
    }

    protected DbFeature<C> getApplyCustomScriptAfterFeature() {
        if (featureResolver.shouldApplyCustomScriptAfter()) {
            return createApplyCustomScriptFeature(featureResolver.getPostExecutionScripts());
        } else {
            return new NopFeature<>();
        }
    }

    protected DbFeature<C> getSeedDataFeature() {
        if (featureResolver.shouldSeedData()) {
            return createSeedDataFeature(featureResolver.getDataSeedStrategy(), getInitialDataSets());
        } else {
            return new NopFeature<>();
        }
    }

    protected DbFeature<C> getVerifyDataAfterFeature() {
        if (featureResolver.shouldVerifyDataAfter()) {
            return createVerifyDataAfterFeature(featureResolver.getExpectedDataSets());
        } else {
            return new NopFeature<>();
        }
    }

    protected static class NopFeature<C> implements DbFeature<C> {

        @Override
        public void execute(final C connection) throws DbFeatureException {
            // does nothing like the name implies
        }
    }
}
