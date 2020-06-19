package org.opencds.cqf.cds;

import org.hl7.fhir.dstu3.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarePlanToCdsCard {

    public static List<CdsCard> convert(CarePlan carePlan) {
        List<CdsCard> cards = new ArrayList<>();

        for (CarePlan.CarePlanActivityComponent activity : carePlan.getActivity()) {
            if (activity.getReferenceTarget() != null && activity.getReferenceTarget() instanceof RequestGroup) {
                RequestGroup requestGroup = (RequestGroup) activity.getReferenceTarget();
                cards = convert(requestGroup);
            }
        }

        return cards;
    }

    private static List<CdsCard> convert(RequestGroup requestGroup) {
        List<CdsCard> cards = new ArrayList<>();

        // links
        if (requestGroup.hasExtension()) {
            CdsCard card = new CdsCard();
            List<CdsCard.Links> links = new ArrayList<>();
            for (Extension extension : requestGroup.getExtension()) {
                CdsCard.Links link = new CdsCard.Links();

                if (extension.getValue() instanceof Attachment) {
                    Attachment attachment = (Attachment) extension.getValue();
                    if (attachment.hasUrl()) {
                        link.setUrl(attachment.getUrl());
                    }
                    if (attachment.hasTitle()) {
                        link.setLabel(attachment.getTitle());
                    }
                    if (attachment.hasExtension()) {
                        link.setType(attachment.getExtensionFirstRep().getValue().primitiveValue());
                    }
                }

                else {
                    throw new RuntimeException("Invalid link extension type: " + extension.getValue().fhirType());
                }

                links.add(link);
            }
            card.setLinks(links);
            cards.add(card);
        }

        if (requestGroup.hasAction()) {
            for (RequestGroup.RequestGroupActionComponent action : requestGroup.getAction()) {
                CdsCard card = new CdsCard();
                // basic
                if (action.hasTitle()) {
                    card.setSummary(action.getTitle());
                }
                if (action.hasDescription()) {
                    card.setDetail(action.getDescription());
                }
                if (action.hasExtension()) {
                    card.setIndicator(action.getExtensionFirstRep().getValue().toString());
                }

                // source
                if (action.hasDocumentation()) {
                    // Assuming first related artifact has everything
                    RelatedArtifact documentation = action.getDocumentationFirstRep();
                    CdsCard.Source source = new CdsCard.Source();
                    if (documentation.hasDisplay()) {
                        source.setLabel(documentation.getDisplay());
                    }
                    if (documentation.hasUrl()) {
                        source.setUrl(documentation.getUrl());
                    }
                    if (documentation.hasDocument() && documentation.getDocument().hasUrl()) {
                        source.setIcon(documentation.getDocument().getUrl());
                    }

                    card.setSource(source);
                }

                // suggestions
                // TODO - uuid
                boolean hasSuggestions = false;
                CdsCard.Suggestions suggestions = new CdsCard.Suggestions();
                CdsCard.Suggestions.Action actions = new CdsCard.Suggestions.Action();
                if (action.hasLabel()) {
                    suggestions.setLabel(action.getLabel());
                    hasSuggestions = true;
                }
                if (action.hasType()) {
                    String code = action.getType().getCode();
                    actions.setType(CdsCard.Suggestions.Action.ActionType.valueOf(code.equals("remove") ? "delete" : code));
                    hasSuggestions = true;
                }
                if (action.hasResource()) {
                    actions.setResource(action.getResourceTarget());
                    hasSuggestions = true;
                }
                if (hasSuggestions) {
                    suggestions.addAction(actions);
                    card.addSuggestion(suggestions);
                }
                cards.add(card);
            }
        }

        return cards;
    }
}
