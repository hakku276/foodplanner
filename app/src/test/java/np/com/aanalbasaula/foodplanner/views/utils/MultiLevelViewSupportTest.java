package np.com.aanalbasaula.foodplanner.views.utils;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

public class MultiLevelViewSupportTest {

    private static final int TEST_TYPE_1 = 1;
    private static final int TEST_TYPE_2 = 2;

    /**
     * Test that a simple Node creation works fine.
     */
    @Test
    public void testNodeCreation() {
        MultiLevelViewSupport.Node node = new MultiLevelViewSupport.Node(TEST_TYPE_1, "test");

        Assert.assertEquals(TEST_TYPE_1, node.getType());
        Assert.assertEquals("test", node.getItem());
    }

    /**
     * Test that the Root Element produces a flat list structure even with only 1 depth
     */
    @Test
    public void testFlatTreeStructureSingleLevel() {
        MultiLevelViewSupport.RootElement root = new MultiLevelViewSupport.RootElement();

        root.addChildNode(new MultiLevelViewSupport.Node(TEST_TYPE_1, "type 1"));
        root.addChildNode(new MultiLevelViewSupport.Node(TEST_TYPE_2, "type 2"));

        List<MultiLevelViewSupport.Node> flat = root.getChildElementsInOrder();

        Assert.assertEquals(2, flat.size());
        Assert.assertEquals(TEST_TYPE_1, flat.get(0).getType());
        Assert.assertEquals(TEST_TYPE_2, flat.get(1).getType());
    }

    /**
     * Test that the Root Element produces a flat list structure even with only 2 depth
     */
    @Test
    public void testFlatTreeStructureTwoLevel() {
        MultiLevelViewSupport.RootElement root = new MultiLevelViewSupport.RootElement();

        MultiLevelViewSupport.Node node0 = new MultiLevelViewSupport.Node(TEST_TYPE_1, "0");
        MultiLevelViewSupport.Node node01 = new MultiLevelViewSupport.Node(TEST_TYPE_2, "0-1");
        MultiLevelViewSupport.Node node02 = new MultiLevelViewSupport.Node(TEST_TYPE_2, "0-2");

        node0.addChild(node01);
        node0.addChild(node02);

        MultiLevelViewSupport.Node node1 = new MultiLevelViewSupport.Node(TEST_TYPE_1, "1");
        MultiLevelViewSupport.Node node11 = new MultiLevelViewSupport.Node(TEST_TYPE_2, "1-1");
        MultiLevelViewSupport.Node node12 = new MultiLevelViewSupport.Node(TEST_TYPE_2, "1-2");

        node1.addChild(node11);
        node1.addChild(node12);

        root.addChildNode(node0);
        root.addChildNode(node1);

        List<MultiLevelViewSupport.Node> flat = root.getChildElementsInOrder();

        Assert.assertEquals(6, flat.size());
        Assert.assertEquals("0", flat.get(0).getItem());
        Assert.assertEquals("0-1", flat.get(1).getItem());
        Assert.assertEquals("0-2", flat.get(2).getItem());
        Assert.assertEquals("1", flat.get(3).getItem());
        Assert.assertEquals("1-1", flat.get(4).getItem());
        Assert.assertEquals("1-2", flat.get(5).getItem());
    }

    /**
     * Test That the node cannot be changed after added to the root
     */
    @Test(expected = RuntimeException.class)
    public void testChangeAfterCommit() {
        MultiLevelViewSupport.RootElement root = new MultiLevelViewSupport.RootElement();

        MultiLevelViewSupport.Node node0 = new MultiLevelViewSupport.Node(TEST_TYPE_1, "0");
        root.addChildNode(node0);

        node0.addChild(new MultiLevelViewSupport.Node(TEST_TYPE_2, "0-1"));

    }
}
