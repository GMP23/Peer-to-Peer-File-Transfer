/*
 * file: NodeList
 * author garret patten
 * date: 12/2/17
 */

public class NodeList {
	int nodeCount;
	Node pre;
	Node post;
	
	public NodeList(){
		nodeCount = 0;
		pre = new Node();
		post = new Node();
		pre.next = post;
		post.previous = pre;
	}
}
