package amberdb.model.builder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class Document {
    final JsonNode document;
    final ObjectMapper mapper = new ObjectMapper();

    public Document(JsonNode structure, JsonNode content) {
        this.document = mapper.createObjectNode();
        ((ObjectNode) document).put("structure", structure);
        ((ObjectNode) document).put("content", content);
    }

    public JsonNode getStructure() {
        return document.get("structure");
    }

    public JsonNode getContent() {
        return document.get("content");
    }

    public JsonNode getReport() {
        return document.get("report");
    }

    public void setStatusReport(JsonNode report) {
        ((ObjectNode) document).put("report", report);
    }

    public String toJson() throws IOException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(document);
    }
}
