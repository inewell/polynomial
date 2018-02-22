import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public class OpOrderComparator implements Comparator<Node> {
    @Override
    public int compare(Node n1, Node n2)
    {
        ArrayList<String> opOrder = new ArrayList<String>();
        opOrder.add("fraction");
        opOrder.add("i");
        opOrder.add("radical");

        int x = opOrder.indexOf(n1.getOp());
        int y = opOrder.indexOf(n2.getOp());

        if (x < y)
        {
            return -1;
        }
        if (x > y)
        {
            return 1;
        }
        return 0;
    }
}
