/*
 * Created on Mar 17, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.etools.common.tests.xml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * THE MASTER COPY of this class is in com.ibm.etools.commontests
 * Please update the copy in commontests and then copy this class to
 * where you need it if you are looking at a different copy
 * 
 * @author jsholl
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DomComparitor {
    private static HashSet attributeList;

    public static String compareDoms(InputSource source1, InputSource source2, HashSet ignorableAtts, EntityResolver entityResolver) throws Exception {
//        attributeList = ignorableAtts;
//        DOMParser parser = new DOMParser();
//        if (entityResolver == null) {
//            parser.setEntityResolver(new EntityResolver() {
//                public InputSource resolveEntity(String arg0, String arg1) throws SAXException, IOException {
//                    return null;
//                }
//            });
//        } else {
//            parser.setEntityResolver(entityResolver);
//        }
//        parser.parse(source1);
//        Document doc1 = parser.getDocument();
//        parser.parse(source2);
//        Document doc2 = parser.getDocument();
//        return compareNodes(doc1, doc2);
    	return null;
    }
    public static String compareDoms(InputSource source1, InputSource source2, HashSet ignorableAtts) throws Exception {
        return compareDoms(source1, source2, ignorableAtts, null);
    }

    public static String compareDoms(InputSource source1, InputSource source2) throws Exception {
        return compareDoms(source1, source2, null);
    }

    public static String compareNodes(Node node1, Node node2) throws Exception {
        //        System.out.println("checking A:" + node1);
        //        System.out.println("checking B:" + node2);
        //        System.out.println("nodeType=" + node1.getNodeType());
        //        System.out.println("getNodeName=" + node1.getNodeName());
        //        System.out.println("getNodeValue=" + node1.getNodeValue());

        //Generic Node Testing 
        //        if (node1 == null && node2 == null)
        //            return null;
        //        else 
        if ((node1 != null && node2 == null) || node1 == null && node2 != null)
            return nullNodeEncountered(node1, node2);
        else if (node1.getNodeType() != node2.getNodeType()) {
            return mismatch("Node.getNodeType() " + node1.getNodeType() + " " + node2.getNodeType(), node1, node2);
        } else if (node1.getNodeName() != node2.getNodeName()) {
            return mismatch("Node.getNodeName() <" + node1.getNodeName() + "> <" + node2.getNodeName() + ">", node1, node2);
        } else if (!(node1.getNodeValue() == null && node2.getNodeValue() == null)) {
            if (node1.getNodeValue() == null) {
                return mismatch("Node.getNodeValue() node A is null", node1, node2);
            } else if (node2.getNodeValue() == null) {
                return mismatch("Node.getNodeValue() node B is null", node1, node2);
            } else if (!node1.getNodeValue().trim().equals(node2.getNodeValue().trim())) {
                return mismatch("Node.getNodeValue() <" + node1.getNodeValue() + "> <" + node2.getNodeValue() + ">", node1, node2);
            }
        }
        //TODO strengthen node comparisons as necessary
        //Specialized Node Testing
        switch (node1.getNodeType()) {
            case Node.TEXT_NODE :
            case Node.CDATA_SECTION_NODE :
                CharacterData cdata1 = (CharacterData) node1;
                CharacterData cdata2 = (CharacterData) node2;
                if (!cdata1.getData().trim().equals(cdata2.getData().trim())) {
                    return mismatch("CharacterData.getData() " + cdata1.getData() + " " + cdata2.getData(), node1, node2);
                }
                break;
            case Node.ATTRIBUTE_NODE :
                Attr attr1 = (Attr) node1;
                Attr attr2 = (Attr) node2;
                if (!attr1.getName().equals(attr2.getName())) {
                    return mismatch("Attr.getName() " + attr1.getName() + " " + attr2.getName(), node1, node2);
                } else if (!attr1.getValue().equals(attr2.getValue())) {
                    return mismatch("Attr.getValue() " + attr1.getValue() + " " + attr2.getValue(), node1, node2);
                } else if (attr1.getSpecified() != attr2.getSpecified()) {
                    return mismatch("Attr.getSpecified() " + attr1.getSpecified() + " " + attr2.getSpecified(), node1, node2);
                }
                break;
            case Node.DOCUMENT_NODE :
                Document doc1 = (Document) node1;
                Document doc2 = (Document) node2;
                String result = compareNodes(doc1.getDoctype(), doc2.getDoctype());
                if (result != null) {
                    return result;
                }
                break;
            case Node.DOCUMENT_TYPE_NODE :
                DocumentType docType1 = (DocumentType) node1;
                DocumentType docType2 = (DocumentType) node2;
                if (!docType1.getPublicId().equals(docType2.getPublicId())) {
                    return mismatch("DocumentType.getPublicId() " + docType1.getPublicId() + " " + docType2.getPublicId(), node1, node2);
                }
                break;
            case Node.PROCESSING_INSTRUCTION_NODE :
                ProcessingInstruction pInst1 = (ProcessingInstruction) node1;
                ProcessingInstruction pInst2 = (ProcessingInstruction) node2;
                //System.out.println("ProcessingInstruction todo");
                break;
            case Node.DOCUMENT_FRAGMENT_NODE :
                DocumentFragment frag1 = (DocumentFragment) node1;
                DocumentFragment frag2 = (DocumentFragment) node2;
                //System.out.println("DocumentFragment todo");
                break;

            case Node.ELEMENT_NODE :
            case Node.COMMENT_NODE :
            case Node.ENTITY_NODE :
            case Node.NOTATION_NODE :
                break;

        }

        //Recursion
        NamedNodeMap attributes1 = node1.getAttributes();
        NamedNodeMap attributes2 = node2.getAttributes();

        if (attributes1 != null && attributes2 != null) {
            ignoreAttributes(attributes1, attributes2);
            if (attributes1.getLength() != attributes2.getLength()) {
                return mismatch("getAttributes().getLength() " + attributes1.getLength() + " " + attributes2.getLength(), node1, node2);
            }
            for (int i = 0; i < attributes1.getLength(); i++) {
                Attr attr1 = (Attr) attributes1.item(i);
                Attr attr2 = (Attr) attributes2.item(i);
                String results = compareNodes(attr1, attr2);
				if (null != results) {
					return results;
				}
            }

        } else if (attributes1 != null || attributes2 != null) {
            return mismatch("getAttributes() null", node1, node2);
        }

        ArrayList children1 = getAsArrayList(node1.getChildNodes());
        ArrayList children2 = getAsArrayList(node2.getChildNodes());

        ignoreComments(children1);
        ignoreComments(children2);

        ignoreEmptyTextNodes(children1);
        ignoreEmptyTextNodes(children2);

        if (children1.size() != children2.size()) {
            return mismatch("Node.hasChildNodes() " + children1.size() + " " + children2.size(), node1, node2);
        }

        for (int i = 0; i < children1.size(); i++) {
            Node child1 = (Node) children1.get(i);
            Node child2 = (Node) children2.get(i);
            String results = compareNodes(child1, child2);
            if (null != results) {
                return results;
            }
        }
        return null;
    }

    private static ArrayList getAsArrayList(NodeList originalList) {
        ArrayList newList = new ArrayList();
        if (originalList != null) {
            for (int i = 0; i < originalList.getLength(); i++) {
                newList.add(originalList.item(i));
            }
        }
        return newList;
    }

    private static void ignoreComments(ArrayList list) {
        ArrayList toRemove = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Node node = (Node) list.get(i);
            if (node.getNodeType() == Node.COMMENT_NODE) {
                toRemove.add(node);
            }
        }
        for (int i = 0; i < toRemove.size(); i++) {
            list.remove(toRemove.get(i));
        }
    }

    private static void ignoreEmptyTextNodes(ArrayList list) {
        ArrayList toRemove = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Node node = (Node) list.get(i);
            if (node.getNodeType() == Node.TEXT_NODE && (node.getNodeValue() == null || node.getNodeValue().trim().equals(""))) {
                toRemove.add(node);
            }
        }
        for (int i = 0; i < toRemove.size(); i++) {
            list.remove(toRemove.get(i));
        }
    }

    /**
     * @param attributes1
     */
    private static void ignoreAttributes(NamedNodeMap attributes1, NamedNodeMap attributes2) {
        if (attributeList != null) {
            Iterator it = attributeList.iterator();
            String ignore;
            while (it.hasNext()) {
                ignore = (String) it.next();
                if (attributes1.getNamedItem(ignore) != null)
                    attributes1.removeNamedItem(ignore);
                if (attributes2.getNamedItem(ignore) != null)
                    attributes2.removeNamedItem(ignore);
            }
        }
    }

    public static String nullNodeEncountered(Node node1, Node node2) {
        String message = "Null node encountered";
        Node nonNullNode = node1 == null ? node2 : node1;
        char source = node1 == null ? 'B' : 'A';
        while (nonNullNode != null) {
            message += source + nonNullNode.getNodeName() + "\n";
            nonNullNode = nonNullNode.getParentNode();
        }
        return message;
    }

    public static String nodeNotCompared(Node node) {
        String message = "Node node compared:";
        while (node != null) {
            message += node.getNodeName() + "\n";
            node = node.getParentNode();
        }

        return message;
    }

    public static String mismatch(String mismatchtype, Node node1, Node node2) throws Exception {
        String message = "Nodes A and B do not match because of node." + mismatchtype + "\n";
        while (node1 != null && node2 != null) {
            message += "A:" + node1.getNodeName() + "\n";
            message += "B:" + node2.getNodeName() + "\n";
            node1 = node1.getParentNode();
            node2 = node2.getParentNode();
        }

        return message;
    }

    public static void main(String[] args) {
    }

}