package amberdb.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import amberdb.AmberSession;
import amberdb.model.Work;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.tinkerpop.blueprints.Vertex;

class CmdEdit extends Command {

    ObjectMapper json = new ObjectMapper();

    public CmdEdit() {
        super("edit", "<objId>", "opens $EDITOR on an object's metadata");
    }

    void execute(Arguments args) throws Exception {
        String id = args.first();
        try (AmberSession db = openAmberDb()) {
            Work work = db.findWork(id);
            int changes = save(work, parse(runEditor(id, format(work))));
            System.out.println(work.getObjId() + " properties changed: " + changes);
        }
    }

    String format(Work work) throws JsonProcessingException {
        StringBuilder out = new StringBuilder();
        for (String key : work.asVertex().getPropertyKeys()) {
            out.append(key + ": " + json.writeValueAsString(work.asVertex().getProperty(key)));
            out.append('\n');
        }
        return out.toString();
    }

    Map<String, Object> parse(String metadata) throws IOException {
        Map<String,Object> map = new LinkedHashMap<String,Object>();
        for (String line : metadata.split("\n")) {
            String parts[] = line.split(": ", 2);
            map.put(parts[0], json.readValue(parts[1], Object.class));
        }
        return map;
    }

    int save(Work work, Map<String,Object> data) {
        int changes = 0;
        Vertex v = work.asVertex();
        for (Entry<String, Object> entry : data.entrySet()) {
            if (!Objects.equal(v.getProperty(entry.getKey()), entry.getValue())) {
                v.setProperty(entry.getKey(), entry.getValue());
                changes++;
            }
        }
        return changes;
    }

    String runEditor(String name, String contents) throws Exception {
        Path path = Files.createTempFile(name + ".", ".txt");
        Files.write(path, contents.getBytes(Charsets.UTF_8));
        try {
            String[] cmd = { "sh", "-c", findEditor() + " '" + path + "' >/dev/tty </dev/tty" };
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            return new String(Files.readAllBytes(path), Charsets.UTF_8);
        } finally {
            Files.deleteIfExists(path);
        }
    }

    String findEditor() {
        String editor = System.getenv("EDITOR");
        if (editor == null) {
            editor = "vi";
        }
        return editor;
    }

}
