package np.com.aanalbasaula.foodplanner.views.utils;

import java.util.LinkedList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * A supporting class to ease the creation of Recycler View adapters with different types
 * of view types. Enabling a hierarchy of elements.
 */
public class MultiLevelViewSupport {

    /**
     * The Root Element that represents the complete structure. A creation of the root element
     * is necessary and then addition of Nodes into the Root Element. Adding nodes into the root
     * element orders the elements in the proper order in a list which can be later used in a simple
     * fashion with any Adapters
     */
    public static class RootElement {

        // The child elements in complete order.
        private List<Node> childElements = new LinkedList<>();

        /**
         * Adds a child node and all its children into the Root. It commits the node and therefore
         * any new nodecannot be added to this or any child nodes.
         *
         * @param node the node to add into the root structure
         */
        public void addChildNode(Node node) {
            childElements.add(node);
            node.addedToRoot = true;

            for (Node child :
                    node.children) {
                addChildNode(child);
            }

        }

        /**
         * Returns the child elements in order ready to be used within any adapter.
         *
         * @return the list of all child elements
         */
        public List<Node> getChildElementsInOrder() {
            return childElements;
        }
    }

    /**
     * A node element that represents a node in the hierarchy.
     */
    public static class Node {

        // the type of node. This is user defined
        @Getter
        private final int type;

        // The item this node holds
        @Getter
        private final Object item;

        // the children within this node
        private final List<Node> children;

        // determines whether the node is already added to root. To disable further changes
        private boolean addedToRoot;

        /**
         * Creates a node element to be added into another node or into the root
         *
         * @param type the type of item this is. Is directly linked to the view type
         * @param item the value that is stored within this element.
         */
        public Node(int type, Object item) {
            this.type = type;
            this.item = item;
            children = new LinkedList<>();
        }

        /**
         * Adds a child to the current node
         */
        public void addChild(Node child) {
            if (addedToRoot) {
                throw new RuntimeException("Cannot add child to a committed node. " +
                        "Please consider adding all child elements and then adding it to the root element");
            }
            children.add(child);
        }

        /**
         * Get a total child count within the node
         * @param node the node whose child count was to be determined
         * @return the number of children within the node
         */
        public static int getTotalChildCount(Node node) {
            if (node.children.isEmpty()) {
                return 0;
            }

            int size = 0;
            for (Node child :
                    node.children) {
                size += getTotalChildCount(child);
            }

            return size + node.children.size();
        }
    }
}
