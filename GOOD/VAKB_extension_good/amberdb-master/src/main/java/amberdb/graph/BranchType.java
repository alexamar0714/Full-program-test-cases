package amberdb.graph;

/**
 * The different ways of branching for an AmberQuery.   
 * <ul>
 * <li><code>BRANCH_FROM_ALL</code>: Branch from any vertex already in the result set</li> 
 * <li><code>BRANCH_FROM_LISTED</code>: Only branch from vertices found in particular steps (nb: the initial vertex set is branch 0)</li>
 * <li><code>BRANCH_FROM_PREVIOUS</code>: Only branch from the vertices returned via the previous branching</li>
 * <li><code>BRANCH_FROM_UNLISTED</code>: Only branch from vertices not listed in the the numbered steps</li>
 * </ul>
 */
public enum BranchType {
    BRANCH_FROM_ALL, 
    BRANCH_FROM_LISTED, 
    BRANCH_FROM_PREVIOUS, 
    BRANCH_FROM_UNLISTED;
}
