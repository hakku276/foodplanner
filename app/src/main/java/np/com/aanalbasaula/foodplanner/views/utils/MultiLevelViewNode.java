package np.com.aanalbasaula.foodplanner.views.utils;

import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.LinkedMultiTreeNode;

import java.util.LinkedList;
import java.util.List;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A class representing a node within the multi level view hierarchy
 */
@EqualsAndHashCode(of = {"type", "item"})
public class MultiLevelViewNode {

    // the type of node. This is user defined
    @Getter
    private final int type;

    // The item this node holds
    @Getter
    private final Object item;

    /**
     * Creates a node element to be added into another node or into the root
     *
     * @param type the type of item this is. Is directly linked to the view type
     * @param item the value that is stored within this element.
     */
    public MultiLevelViewNode(int type, Object item) {
        this.type = type;
        this.item = item;
    }

}
