//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.validation;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;

/**
 * Contains all the validation messages for a single file.
 */
@Entity
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
public class FileValidationResult implements Serializable {
    /**
     * Key for validationProperties for array data file hybridization names.
     */
    public static final String HYB_NAME = "Hybridization(s)";

    private static final long serialVersionUID = -5402207496806890698L;
    private static final String UNUSED = "unused";

    private Long id;
    private Set<ValidationMessage> messageSet = new HashSet<ValidationMessage>();
    private final transient Map<String, Object> validationProperties = new HashMap<String, Object>();

    /**
     * Returns true if all the documents in the set were valid.
     * 
     * @return true if set was valid.
     */
    @Transient
    public boolean isValid() {
        for (final ValidationMessage message : this.messageSet) {
            if (ValidationMessage.Type.ERROR.equals(message.getType())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the messages ordered by type and location.
     * 
     * @return the messages.
     */
    @Transient
    public List<ValidationMessage> getMessages() {
        final List<ValidationMessage> messageList = new ArrayList<ValidationMessage>();
        messageList.addAll(getMessageSet());
        Collections.sort(messageList);
        return Collections.unmodifiableList(messageList);
    }

    /**
     * Returns the messages of given type, ordered by location.
     * 
     * @param type the type of messages to return
     * @return the messages.
     */
    @Transient
    public List<ValidationMessage> getMessages(ValidationMessage.Type type) {
        final List<ValidationMessage> messageList = new ArrayList<ValidationMessage>();
        for (final ValidationMessage message : this.messageSet) {
            if (message.getType() == type) {
                messageList.add(message);
            }
        }
        Collections.sort(messageList);
        return Collections.unmodifiableList(messageList);
    }

    /**
     * Adds a new validation message to the result.
     * 
     * @param type the type/level of the message
     * @param message the actual message content
     * @return the newly added message, if additional configuration of the message is required.
     */
    public ValidationMessage addMessage(Type type, String message) {
        final ValidationMessage validationMessage = new ValidationMessage(type, message);
        addMessage(validationMessage);
        return validationMessage;
    }

    /**
     * Adds a new validation message to the result including line and column information.
     * 
     * @param type the type/level of the message
     * @param message the actual message content
     * @param lineNumber the line number the error occurs on
     * @param columnNumber the column number the error occurs on
     * @return the newly added message, if additional configuration of the message is required.
     */
    public ValidationMessage addMessage(Type type, String message, int lineNumber, int columnNumber) {
        final ValidationMessage validationMessage = addMessage(type, message);
        validationMessage.setLine(lineNumber);
        validationMessage.setColumn(columnNumber);
        return validationMessage;
    }

    /**
     * Adds a new validation message to the result.
     * 
     * @param message the message to add
     */
    public void addMessage(ValidationMessage message) {
        this.messageSet.add(message);
    }

    /**
     * @return validation messages
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
    public Set<ValidationMessage> getMessageSet() {
        return this.messageSet;
    }

    @SuppressWarnings({UNUSED, "PMD.UnusedPrivateMethod" })
    private void setMessageSet(Set<ValidationMessage> messageSet) {
        this.messageSet = messageSet;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SuppressWarnings({UNUSED, "PMD.UnusedPrivateMethod" })
    private Long getId() {
        return this.id;
    }

    @SuppressWarnings({UNUSED, "PMD.UnusedPrivateMethod" })
    private void setId(Long id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuffer stringBuffer = new StringBuffer();
        for (final ValidationMessage message : getMessages()) {
            stringBuffer.append(message.toString());
            stringBuffer.append('\n');
        }
        return stringBuffer.toString();
    }

    /**
     * The default comparison uses the id.
     * 
     * @param o other object
     * @return equal or not
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof FileValidationResult)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (this.id == null) {
            // by default, two transient instances cannot ever be equal
            return false;
        }

        final FileValidationResult e = (FileValidationResult) o;
        return this.id.equals(e.id);
    }

    /**
     * Default hashCode goes off of id.
     * 
     * @return hashCode
     */
    @Override
    public int hashCode() {
        if (this.id == null) {
            return System.identityHashCode(this);
        }
        return this.id.hashCode();
    }

    /**
     * Validation properties based on name.
     * 
     * @param name key for properties
     * @return validation properties
     */
    @Transient
    public Object getValidationProperties(String name) {
        return this.validationProperties.get(name) == null ? new ArrayList<String>() : this.validationProperties
                .get(name);
    }

    /**
     * Add validation properties to general purpose storage for use by specific validators.
     * 
     * @param name type of properties
     * @param props the properties
     */
    @Transient
    public void addValidationProperties(String name, Object props) {
        this.validationProperties.put(name, props);
    }
}
