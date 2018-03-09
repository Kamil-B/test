package node;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import node.utils.NodeHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class NodeHelperTest {

    private Node<String> parent;
    private List<Node<String>> expectedChildren;

    @Before
    public void setUpData() {
        val child1OfChild1OfChild1 = new NodeImpl<String>("child1OfChild1OfChild1");
        val child2OfChild1OfChild1 = new NodeImpl<String>("child2OfChild1OfChild1");

        val children = new ArrayList<Node<String>>();
        children.add(child1OfChild1OfChild1);
        children.add(child2OfChild1OfChild1);

        val child1OfChild1 = new NodeImpl<String>("child1OfChild1", children);
        val child2OfChild1 = new NodeImpl<String>("child2OfChild1");

        children.clear();
        children.add(child1OfChild1);
        children.add(child2OfChild1);

        val child1 = new NodeImpl<String>("child1", children);

        val child1OfChild2 = new NodeImpl<String>("child1OfChild2");
        val child2OfChild2 = new NodeImpl<String>("child2OfChild2");

        children.clear();
        children.add(child1OfChild2);
        children.add(child2OfChild2);

        val child2 = new NodeImpl<String>("child2", children);

        children.clear();
        children.add(child1);
        children.add(child2);
        parent = new NodeImpl<>("parent", children);
        expectedChildren = new ArrayList<>(Arrays.asList(child1, child2, child1OfChild1, child2OfChild1,
                child1OfChild2, child2OfChild2, child1OfChild1OfChild1, child2OfChild1OfChild1));
    }

    @Test
    public void when_getAllChildrenOfTree_returnTrue() {
        val helper = new NodeHelper<String>(parent);
        List<Node<String>> actualChildren = new ArrayList<>();

        helper.iterator().forEachRemaining(actualChildren::add);
        assertThat(actualChildren).isEqualTo(expectedChildren);
    }

    @Test
    public void when_getStreamOfTree_returnTrue() {
        val helper = new NodeHelper<String>(parent);
        Stream<Node<String>> actualStream = helper.asStream();
        Stream<Node<String>> expectedStream = expectedChildren.stream();
        assertThat(actualStream.collect(Collectors.toList())).containsAll(expectedStream.collect(Collectors.toList()));
    }

}
