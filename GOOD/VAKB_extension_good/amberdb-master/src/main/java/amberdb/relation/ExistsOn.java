package amberdb.relation;

/**
 * Many to many relation from articles to the pages they exist on.
 */
public interface ExistsOn extends Relation {
	public static final String label = "existsOn";
}
