package cs455.overlay.tests;

public class MiscellaneousTests {


    public void testSplit() {
        String lineToSplit = "test" + "\n";
        String[] separate = lineToSplit.split("\\n");
        if (separate.length == 1) {
            System.out.println("one item in array");
        }
    }

    public void testRemoveFromArray() {
        String listOfNodes = "node1:1234\nnode2:3445\nnode3:1358\nnode4:1111";
        String listMinusFirstNode = "";
        String[] split = listOfNodes.split("\\n");
        split[0] = null;
        for (String string : split) {
            if(string != null) {
                listMinusFirstNode += string;
                listMinusFirstNode += "\n";
            }
        }
        System.out.println(listMinusFirstNode);
    }

    public static void main(String[] args) {
        MiscellaneousTests miscellaneousTests = new MiscellaneousTests();
        miscellaneousTests.testRemoveFromArray();
    }


}
