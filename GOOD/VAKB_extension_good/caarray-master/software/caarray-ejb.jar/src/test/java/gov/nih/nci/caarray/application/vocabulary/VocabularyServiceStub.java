//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.vocabulary;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Basic stub for tests.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class VocabularyServiceStub implements VocabularyService {

    private long id;
    private Map<List, Protocol> protocols = new HashMap<List, Protocol>();
    private Map<List, TermSource> sources = new HashMap<List, TermSource>();

    public Set<Term> getTerms(Category category) {
        return getTerms(category, null);
    }

    public Set<Term> getTerms(Category category, String value) {
        Set<Term> terms = new HashSet<Term>();
        TermSource source = getSource(ExperimentOntology.MGED_ONTOLOGY.getOntologyName(),
                ExperimentOntology.MGED_ONTOLOGY.getVersion());
        for (int i = 0; i < 10; i++) {
            Term term = createTerm(source, category, "term" + i);
            terms.add(term);
        }
        return terms;
    }

    /**
     * {@inheritDoc}
     */
    public TermSource getSource(String name, String version) {
        List key = Arrays.asList(name, version);
        TermSource source = sources.get(key);
        if (source == null) {
            source = new TermSource();
            source.setName(name);
            source.setVersion(version);
            source.setId(id++);
            sources.put(key, source);
        }        
        return source;
    }

    /**
     * {@inheritDoc}
     */
    public TermSource getSourceByUrl(String url, String version) {
        TermSource source = new TermSource();
        source.setUrl(url);
        source.setVersion(version);
        source.setName("Name for: " + url);
        return source;
    }

    /**
     * {@inheritDoc}
     */
    public Set<TermSource> getSources(String name) {
        TermSource ts = getSource(name, null);
        Set<TermSource> result = new HashSet<TermSource>();
        result.add(ts);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<TermSource> getSourcesByUrl(String url) {
        TermSource ts = getSourceByUrl(url, null);
        ts.setName("Name for: " + url);
        Set<TermSource> result = new HashSet<TermSource>();
        result.add(ts);
        return result;
    }

    public Term getTerm(TermSource source, String value) {
        Term term = new Term();
        term.setSource(source);
        term.setValue(value);
        Category cat = new Category();
        cat.setSource(source);
        cat.setName("Category for " + value);
        term.setCategory(cat);
        return term;
    }

    @SuppressWarnings("deprecation")
    public Term getTerm(Long id) {
        Term term = new Term();
        term.setId(id);
        return term;
    }

    public Organism getOrganism(Long id) {
        Organism org = new Organism();
        org.setId(id);
        return org;
    }

    public Organism getOrganism(TermSource source, String scientificName) {
        Organism org = new Organism();
        org.setTermSource(source);
        org.setScientificName(scientificName);
        return org;
    }

    public List<Organism> getOrganisms() {
        List<Organism> orgs = new ArrayList<Organism>();
        Organism o1 = new Organism();
        o1.setId(1L);
        o1.setScientificName("Mizouse");
        orgs.add(o1);
        return orgs;
    }

    public Category createCategory(TermSource source, String categoryName) {
        Category category = new Category();
        category.setSource(source);
        category.setName(categoryName);
        return category;
    }

    public Term createTerm(TermSource source, Category category, String value) {
        Term term = new Term();
        term.setSource(source);
        term.setValue(value);
        term.setCategory(category);
        return term;
    }

    public Category getCategory(TermSource source, String categoryName) {
        Category category = new Category();
        category.setName(categoryName);
        return category;
    }

    public TermSource createSource(String name, String url, String version) {
        TermSource ts = getSource(name, version);
        ts.setUrl(url);
        return ts;
    }

    public void saveTerm(Term term) {
        // do nothing
    }

    public List<TermSource> getAllSources() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Protocol> getProtocolsByProtocolType(Term type, String name) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Protocol getProtocol(String name, TermSource source) {
        List key = Arrays.asList(name, source);
        Protocol p = protocols.get(key);
        if (p == null) {
            p = new Protocol(name, null, source);
            p.setId(id++);
            protocols.put(key, p);
        }
        
        return p;
    }

    public Term findTermInAllTermSourceVersions(TermSource termSource, String value) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends AbstractCharacteristic> List<Category> searchForCharacteristicCategory(
            Class<T> characteristicClass, String keyword) {
        return Collections.emptyList();
    }    

    /**
     * {@inheritDoc}
     */
    public List<Organism> searchForOrganismNames(String keyword) {
        return new ArrayList<Organism>();
    }


}












