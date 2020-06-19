package amberdb.graph;

import amberdb.AmberSession;
import amberdb.model.*;
import com.google.common.collect.ImmutableList;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

import java.util.ArrayList;
import java.util.List;

public abstract class AmberPreCommitHook<T> {
    public static List<String> WORK_TYPES = ImmutableList.<String>builder().add(
            Work.class.getAnnotation(TypeValue.class).value(),
            EADWork.class.getAnnotation(TypeValue.class).value(),
            Section.class.getAnnotation(TypeValue.class).value()
    ).build();

    public static List<String> COPY_TYPES= ImmutableList.<String>builder().add(
            Copy.class.getAnnotation(TypeValue.class).value()
    ).build();


    public static List<String> SOUND_FILE_TYPES= ImmutableList.<String>builder().add(
            SoundFile.class.getAnnotation(TypeValue.class).value()
    ).build();

    public abstract boolean shouldHook(List<T> added, List<T> modified, List<T> deleted);

    public abstract void runHook(List<T> added, List<T> modified, List<T> deleted, AmberSession amberSession);

    public abstract List<String> getValidNodeTypes();

    public abstract Class getExpectedNodeType();

    public void hook(List<Vertex> addedVertices,
                     List<Vertex> modifiedVertices,
                     List<Vertex> deletedVertices,
                     List addedEdges,
                     List modifiedEdges,
                     List deletedEdges,
                     AmberSession amberSession) {

        List<T> addedNodes = retrieveVerticesOfCorrectTypeForThisHook(addedVertices, amberSession.getGraph());
        List<T> modifiedNodes = retrieveVerticesOfCorrectTypeForThisHook(modifiedVertices, amberSession.getGraph());
        List<T> removedNodes = retrieveVerticesOfCorrectTypeForThisHook(deletedVertices, amberSession.getGraph());

        if (getExpectedNodeType() == Edge.class) {
            if (shouldHook(addedEdges, modifiedEdges, deletedEdges)) {
                runHook(addedEdges, modifiedEdges, deletedEdges, amberSession);
            }
        } else {
            if (shouldHook(addedNodes, modifiedNodes, removedNodes)) {
                runHook(addedNodes, modifiedNodes, removedNodes, amberSession);
            }
        }
    }

    public List<T> retrieveVerticesOfCorrectTypeForThisHook(List<Vertex> vertices, FramedGraph graph) {
        List nodesOfCorrectType = new ArrayList();

        for (Vertex element: vertices) {
            if (element.getProperty("type") != null && getValidNodeTypes().contains(element.getProperty("type"))) {
                nodesOfCorrectType.add(graph.frame(element, getExpectedNodeType()));
            }
        }

        return nodesOfCorrectType;
    }
}
