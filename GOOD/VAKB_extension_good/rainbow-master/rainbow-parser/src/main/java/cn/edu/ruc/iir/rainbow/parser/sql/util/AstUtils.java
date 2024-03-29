/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.edu.ruc.iir.rainbow.parser.sql.util;

import cn.edu.ruc.iir.rainbow.parser.sql.tree.Node;
import com.google.common.collect.TreeTraverser;

import java.util.stream.Stream;

import static com.google.common.collect.Iterables.unmodifiableIterable;
import static java.util.Objects.requireNonNull;

public class AstUtils
{
    public static boolean nodeContains(Node node, Node subNode)
    {
        requireNonNull(node, "node is null");
        requireNonNull(subNode, "subNode is null");

        return preOrder(node)
                .anyMatch(childNode -> childNode == subNode);
    }

    public static Stream<Node> preOrder(Node node)
    {
        return TreeTraverser.using((Node n) -> unmodifiableIterable(n.getChildren()))
                .preOrderTraversal(requireNonNull(node, "node is null"))
                .stream();
    }

    private AstUtils() {}
}
