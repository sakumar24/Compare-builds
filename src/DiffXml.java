
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DiffXml 
{
	public boolean nodeTypeDiff = true;
	public boolean nodeValueDiff = true;
	public String fileName = null;
	/**
	 * Diff 2 nodes and put the diffs in the list
	 */
	public boolean diff(Node srcNode, Node dstNode, List<ReportStatistics> diffs) throws Exception
	{
		if (! diffNodeExists(srcNode, dstNode, diffs))
		{
			// if any of then doesn't exists then no need to check further.
			return true;
		}

		if (nodeTypeDiff)
		{
			diffNodeType(srcNode, dstNode, diffs);
		}

		if (nodeValueDiff) 
		{
			diffNodeValue(srcNode, dstNode, diffs);
		}

		System.out.println(srcNode.getNodeName() + "/" + dstNode.getNodeName());

		diffAttributes(srcNode, dstNode, diffs);
		diffNodes(srcNode, dstNode, diffs);

		return diffs.size() > 0;
	}

	public boolean diffAttributes(Node srcNode, Node dstNode, List<ReportStatistics> diffs) throws Exception
	{
		/*
		 *  TODO : getPath(node) in-case of a attribute is not giving the path correctly. 
		 */
		// Create map<NodeName, Node> for attributes of srcNode
		NamedNodeMap nodeMap1 = srcNode.getAttributes();
		Map<String, Node> attributes1 = new LinkedHashMap<String, Node>();
		for (int index = 0; nodeMap1 != null && index < nodeMap1.getLength(); index++) {
			attributes1.put(nodeMap1.item(index).getNodeName(), nodeMap1.item(index));
		}

		// Create map<NodeName, Node> for attributes of dstNode
		NamedNodeMap nodeMap2 = dstNode.getAttributes();
		Map<String, Node> attributes2 = new LinkedHashMap<String, Node>();
		for (int index = 0; nodeMap2 != null && index < nodeMap2.getLength(); index++) {
			attributes2.put(nodeMap2.item(index).getNodeName(), nodeMap2.item(index));

		}

		// Diff all the attributes1
		for (Node attribute1 : attributes1.values()) {
			Node attribute2 = attributes2.remove(attribute1.getNodeName());
			diff(attribute1, attribute2, diffs);
		}

		// Diff all the attributes2 left over
		for (Node attribute2 : attributes2.values()) {
			Node attribute1 = attributes1.get(attribute2.getNodeName());
			diff(attribute1, attribute2, diffs);
		}

		return diffs.size() > 0;
	}

	/**
	 * Check that the nodes exist, if any of then doesn't exists then no need to check further
	 */
	public boolean diffNodeExists(Node srcNode, Node dstNode, List<ReportStatistics> diffs) throws Exception 
	{

		/*
		 * We should never get this condition
		 * 	if (srcNode == null && dstNode == null)
		{
			curDiff.setProperty(getPath(dstNode) + ":node " + srcNode + "!=" + dstNode + "\n");
			return false;
		}
		 */
		if (srcNode == null && dstNode != null)
		{
			ReportStatistics curDiff = new ReportStatistics();
			curDiff.setFilePath(fileName);
			curDiff.setProperty(getPath(dstNode));
			curDiff.setNewValue("**Node not present in srcFile**");
			curDiff.setPreviousValue(dstNode.getTextContent());
			diffs.add(curDiff);

			return false;
		}
		if (srcNode != null && dstNode == null) 
		{
			ReportStatistics curDiff = new ReportStatistics();
			curDiff.setFilePath(fileName);
			curDiff.setProperty(getPath(srcNode));
			curDiff.setNewValue(srcNode.getTextContent());
			curDiff.setPreviousValue("**Node not present in dstFile**");
			diffs.add(curDiff);

			return false;
		}
		return true;
	}

	/**
	 * Diff the nodes with children
	 */
	public boolean diffNodes(Node srcNode, Node dstNode, List<ReportStatistics> diffs) throws Exception {
		//Create map<NodeName, Node> for children of srcNode
		Map<String, Node> children1 = new LinkedHashMap<String, Node>();
		for (Node child1 = srcNode.getFirstChild(); child1 != null; child1 = child1.getNextSibling()) {
			children1.put(child1.getNodeName(), child1);
		}

		// Create map<NodeName, Node> for children of dstNode
		Map<String, Node> children2 = new LinkedHashMap<String, Node>();
		for (Node child2 = dstNode.getFirstChild(); child2 != null; child2 = child2.getNextSibling()) {
			children2.put(child2.getNodeName(), child2);
		}

		// Diff all the children1
		for (Node child1 : children1.values()) {
			Node child2 = children2.remove(child1.getNodeName());
			diff(child1, child2, diffs);
		}

		// Diff all the children2 left over
		for (Node child2 : children2.values()) {
			Node child1 = children1.get(child2.getNodeName());
			diff(child1, child2, diffs);
		}

		return diffs.size() > 0;
	}

	/**
	 * Diff the Node Type
	 */
	public boolean diffNodeType(Node srcNode, Node dstNode, List<ReportStatistics> diffs) throws Exception 
	{	
		if (srcNode.getNodeType() != dstNode.getNodeType())
		{
			ReportStatistics curDiff = new ReportStatistics();
			curDiff.setFilePath(fileName);
			curDiff.setProperty(getPath(srcNode));
			curDiff.setNewValue("Type:"+srcNode.getNodeType());
			curDiff.setPreviousValue("Type:"+dstNode.getNodeType());
			diffs.add(curDiff);

			return true;
		}
		return false;
	}

	/**
	 * Diff the Node Value
	 */
	public boolean diffNodeValue(Node srcNode, Node dstNode, List<ReportStatistics> diffs) throws Exception 
	{
		
		if (srcNode.getNodeValue() == null && dstNode.getNodeValue() == null)
		{
			return false;
		}

		if (srcNode.getNodeValue() == null && dstNode.getNodeValue() != null)
		{
			ReportStatistics curDiff = new ReportStatistics();
			curDiff.setFilePath(fileName);
			curDiff.setProperty(getPath(srcNode));
			curDiff.setNewValue(srcNode.getNodeValue());
			curDiff.setPreviousValue(dstNode.getNodeValue());

			diffs.add(curDiff);

			return true;
		}

		if (srcNode.getNodeValue() != null && dstNode.getNodeValue() == null)
		{
			ReportStatistics curDiff = new ReportStatistics();
			curDiff.setFilePath(fileName);
			curDiff.setProperty(getPath(srcNode));
			curDiff.setNewValue(srcNode.getNodeValue());
			curDiff.setPreviousValue(dstNode.getNodeValue());

			diffs.add(curDiff);

			return true;
		}

		if(! srcNode.getNodeValue().equals(dstNode.getNodeValue()))
		{
			System.out.println("Difference in value in:"+srcNode.getNodeName());
			ReportStatistics curDiff = new ReportStatistics();
			curDiff.setFilePath(fileName);
			curDiff.setProperty(getPath(srcNode));
			curDiff.setNewValue(srcNode.getNodeValue());
			curDiff.setPreviousValue(dstNode.getNodeValue());

			diffs.add(curDiff);

			return true;
		}
		return false;
	}

	/**
	 * Get the node path
	 */
	public String getPath(Node node) {
		StringBuilder path = new StringBuilder();

		do {
			path.insert(0, node.getNodeName());
			path.insert(0, "/");
		} while ((node = node.getParentNode()) != null);

		return path.toString();
	}
}
