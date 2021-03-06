package ca.uhn.fhir.parser;

import org.hl7.fhir.dstu2016may.model.Identifier;
import org.hl7.fhir.dstu2016may.model.MessageHeader;
import org.hl7.fhir.exceptions.FHIRException;

import ca.uhn.fhir.model.api.annotation.Block;
import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;

@ResourceDef(name = "FooMessageHeader")
public class FooMessageHeaderWithExplicitField extends MessageHeader {

	private static final long serialVersionUID = 1L;

	/**
	 * The source application from which this message originated.
	 */
	@Child(name = "source", type = {}, order = Child.REPLACE_PARENT, min = 1, max = 1, modifier = false, summary = true)
	@Description(shortDefinition = "Message Source Application", formalDefinition = "The source application from which this message originated.")
	protected FooMessageSourceComponent source;

	public void setSourceNew(FooMessageSourceComponent theSource) {
		source = theSource;
	}

	@Block()
	public static class FooMessageSourceComponent extends MessageHeader.MessageSourceComponent {

		private static final long serialVersionUID = 1L;
		@Child(name = "ext-messageheader-application-id", type = Identifier.class, modifier = true)
		@Description(shortDefinition = "Message Header Application ID")
		@Extension(url = "http://foo", definedLocally = false, isModifier = false)
		private Identifier messageHeaderApplicationId;

		/*
		 * Get messageHeaderApplicationId
		 */
		public Identifier getMessageHeaderApplicationId() throws FHIRException {

			if (messageHeaderApplicationId == null) {
				messageHeaderApplicationId = new Identifier();
			}
			return messageHeaderApplicationId;
		}

		/*
		 * Set messageHeaderApplicationId
		 */
		public void setmessageHeaderApplicationId(Identifier messageHeaderApplicationId) {
			this.messageHeaderApplicationId = messageHeaderApplicationId;
		}

	}

}