package node;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;

import java.util.ArrayList;

@Slf4j
public class TreeNodeTest {

    @Test
    public void checkNode() {

        val child1OfChild1 = new NodeImpl<String>("child1OfChild1");
        val child2OfChild1 = new NodeImpl<String>("child2OfChild1");

        val children = new ArrayList<Node<String>>();
        children.add(child1OfChild1);
        children.add(child2OfChild1);

        val child1 = new NodeImpl<String>("child1", children);
        val child2 = new NodeImpl<String>("child2");

        children.clear();
        children.add(child1);
        children.add(child2);
        val parent = new NodeImpl<String>("parent", children);

        val helper = new NodeHelper<String>(parent);
        helper.iterator().forEachRemaining(node -> log.info(node.getPayload()));
    }
}
