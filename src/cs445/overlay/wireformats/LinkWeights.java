package cs445.overlay.wireformats;

public class LinkWeights implements Protocol {

    int messageType = LINK_WEIGHTS;
    int numberOfLinks;
    //link info - hostnameA:portnumA hostnameB:portnumB weight
}
