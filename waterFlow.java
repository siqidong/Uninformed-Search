//CS561 Project 1
//Author: Siqi Dong

package waterFlow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class waterFlow {
	
	//Node represents the pipe point
	public class Node {
		String name;
		boolean visited = false;
		int time;

		public Node(String name, boolean visited, int time) {
			this.name = name;
			this.visited = visited;
			this.time = time;
		}

		public String getName() {
			return name;
		}

		public int getTime() {
			return time;
		}
	}

	//Edge represents the pipe
	public class Edge implements Comparable<Edge>{
		String startNode;
		String endNode;
		int length;
		ArrayList<Integer> brokeTime;

		public Edge() {
			startNode = null;
			endNode = null;
			length = 0;
			brokeTime = null;
		}

		//using this method to sort the Edge objects to alphabetical order
		public int compareTo(Edge anotherEdge) {
			String startNameOne = startNode;
			String endNameOne = endNode;

			String startNameTwo = anotherEdge.startNode;
			String endNameTwo = anotherEdge.endNode;

			if (startNameOne.compareTo(startNameTwo) < 0) {
				return -1;
			}
			else if (startNameOne.compareTo(startNameTwo) > 0) {
				return 1;
			}
			else {
				return endNameOne.compareTo(endNameTwo);
			}
		}
	}

	//Path records the possible first found end point
	public class Path {
		String name;
		int time;

		public Path() {
			name = "";
			time = -1;
		}
	}
	
	/* per waterFlow object, there are algorithm, nodes, edges, possible path and start time
	 * node[0] is the starting point
	 * endNum records the number of end points
	 */
	String method;
	int endNum;
	Node[] node;
	Edge[] edge;
	Path path;
	int startTime;

	public waterFlow() {
		method = "";
		endNum = 0;
		node = null;
		edge = null;
		path = null;
		startTime = 0;
	}

	public Node getNode(String nodeName) {
		for (int i = 0; i < node.length; i++) {
			if (node[i].name.equals(nodeName)) {
				return node[i];
			}
		}
		return null;
	}

	public static void main(String[] args) {
		waterFlow[] water = null;
		try {
			water = readFile(args[3]);
			if (water!=null) {
				for (int i = 0; i < water.length; i++) {
					Arrays.sort(water[i].edge);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (water!=null) {
			for (int i = 0; i < water.length; i++) {
				if (water[i].method.equals("BFS")) {
					water[i].BFS();
				}
				if (water[i].method.equals("DFS")) {
					water[i].DFS();
				}
				if (water[i].method.equals("UCS")) {
					water[i].UCS();
				}
			}
		}
		try {	
			writeFile(water);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//read from the file given and create related objects
	public static waterFlow[] readFile(String file)
			throws IOException {
		//read the input file
		BufferedReader in = new BufferedReader(new FileReader(file));
		int testNumber = Integer.parseInt(in.readLine());
		if (testNumber <= 0) {
			in.close();
			return null;
		}
		//create waterFlow object for each case
		waterFlow[] water = new waterFlow[testNumber];
		String line = null;
		int i = 0;
		
		while ((line = in.readLine()) != null) {
			water[i] = new waterFlow();
			
			//extract info from the file
			water[i].method = line;
			String startNodeName = in.readLine();
			String[] endNodeName = in.readLine().split(" ");
			String[] otherNodeName = in.readLine().split(" ");
			int edgeNum = Integer.parseInt(in.readLine());
			String[] pipe = new String[edgeNum];
			for (int j = 0; j < edgeNum; j++) {
				pipe[j] = in.readLine();
			}
			int startTime = Integer.parseInt(in.readLine());

			//initiate each waterFlow object
			water[i].startTime = startTime;
			Node[] node = new Node[1 + endNodeName.length + otherNodeName.length];
			node[0] = water[i].new Node(startNodeName, false, startTime);
			for (int j = 0; j < endNodeName.length; j++) {
				node[1 + j] = water[i].new Node(endNodeName[j], false, startTime);
			}
			for (int j = 0; j < otherNodeName.length; j++) {
				if (!otherNodeName[j].equals("")) {
					node[1 + endNodeName.length + j] = water[i].new Node(otherNodeName[j], false, startTime);
				}
			}

			water[i].endNum = endNodeName.length;
			water[i].node = node;

			Edge[] edge = new Edge[edgeNum];
			for (int j = 0; j < edgeNum; j++) {
				edge[j] = water[i].new Edge();
				String[] edgeElement = pipe[j].split(" ");
				String startEdge = edgeElement[0];
				String endEdge = edgeElement[1];
				ArrayList<Integer> brokeList = new ArrayList<Integer>();
				int edgeLength = Integer.parseInt(edgeElement[2]);
				int duration = Integer.parseInt(edgeElement[3]);
				if (duration != 0) {
					for (int k = 0; k < duration; k++) {
						String[] perBreak= edgeElement[4 + k].split("-");
						int startBreak = Integer.parseInt(perBreak[0]);
						int endBreak = Integer.parseInt(perBreak[1]);
						while (startBreak <= endBreak) {
							brokeList.add(startBreak);
							startBreak++;
						}
					}
				}
				else {
					brokeList.add(-1);
				}
				edge[j].startNode = startEdge;
				edge[j].endNode = endEdge;
				edge[j].length = edgeLength;
				edge[j].brokeTime = brokeList;
			}
			water[i].edge = edge;
			water[i].path = water[i].new Path();
			in.readLine();
			i++;
		}
		in.close();
		return water;
	}

	//create a file and write output to this file
	public static void writeFile(waterFlow[] water) throws IOException {
		
		File outputFile = new File("output4.txt");
		FileOutputStream fos = new FileOutputStream(outputFile);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		if (water!=null) {
			for (int i = 0; i < water.length; i++) {
				if (water[i].path.time <= 0) {
					bw.write("None");
				}
				else {				
					bw.write(water[i].path.name + " " + water[i].path.time%24);
				}
				bw.newLine();
			}
		}
		bw.close();
	}

	//implement of breath-first search
	public void BFS() {
		//endNodeSet contains all end nodes
		ArrayList<Node> endNodeSet = new ArrayList<Node>();
		for (int i = 0; i < endNum; i++) {
			endNodeSet.add(node[1+i]);
		}
		
		//using queue to keep track of the nodes
		Queue<Node> queue = new LinkedList<Node>();
		queue.add(node[0]);

		Node currentNode;
		boolean loop = true;

		while (!queue.isEmpty() && loop) {
			currentNode = (Node) queue.remove();
			currentNode.visited = true;
			
			for (int j = 0; j < edge.length; j++) {
				Node edgeStart = getNode(edge[j].startNode);
				Node edgeEnd = getNode(edge[j].endNode);

				if (currentNode.name.equals(edgeStart.name) && edgeEnd.visited==false) {

					//the first node is found
					if (endNodeSet.contains(edgeEnd)) {
						queue.add(edgeEnd);
						edgeEnd.time = currentNode.time+1;
						path.name = edgeEnd.name;
						path.time = edgeEnd.time;
						loop = false;
						break;
					}
					//add next available ndoes to the queue
					queue.add(edgeEnd);
					edgeEnd.visited = true;
					edgeEnd.time = currentNode.time+1;
				}
			}
		}
		//no path available
		if (queue.isEmpty()) {
			path.time = -1;
		}
	}

	//implement of depth-first search
	public void DFS() {
		//endNodeSet contains all end node names
		ArrayList<String> endNodeSet = new ArrayList<String>();
		for (int i = 0; i < endNum; i++) {
			endNodeSet.add(node[1+i].name);
		}
		//using stack to keep track of the nodes
		Stack<Node> stack = new Stack<Node>();
		stack.push(new Node(node[0].name, node[0].visited, node[0].time));
		Node currentNode;
		
		while (!stack.isEmpty()) {
			currentNode = stack.peek();
			if (currentNode.visited==false) {
				//this node is not one of the end nodes, visit this node
				if (!endNodeSet.contains(currentNode.name) ) {
					
					Iterator<Node> iter = stack.iterator();
					while (iter.hasNext()) {
						Node iterNode = iter.next();
						if (iterNode.name.equals(currentNode.name)) {
							iterNode.visited = true;
						}
					}
					getNode(currentNode.name).visited = true;
					stack.pop();
					//add next available nodes to the stack
					for (int j = edge.length - 1; j >= 0; j--) {
						Node edgeEnd = getNode(edge[j].endNode);
						if (currentNode.name.equals(edge[j].startNode) && edgeEnd.visited==false) {
							stack.push(new Node(edgeEnd.name, false, currentNode.time+1));
						}
					}
				}
				//first node is found
				else {
					currentNode.visited = true;
					path.name = currentNode.name;
					path.time = currentNode.time;
					break;
				}
			}
			//this node has been visited before
			else {
				stack.pop();
			}
		}
		//no path available
		if (stack.isEmpty()) {
			path.time = -1;
		}
	}

	//implement of uniform-cost search
	public void UCS() {
		//endNodeSet contains all end node names
		ArrayList<String> endNodeSet = new ArrayList<String>();
		for (int i = 0; i < endNum; i++) {
			endNodeSet.add(node[1+i].name);
		}
		//using self-defined comparator priority queue to keep track of the nodes
		PriorityQueue<Node> pqueue = new PriorityQueue<Node>(new Comparator<Node>() {
			public int compare(Node node1, Node node2) {
				String name1 = node1.getName();
				String name2 = node2.getName();
				int time1 = node1.getTime();
				int time2 = node2.getTime();
				if (time1 != time2) {
					return time1<time2?-1:1;
				}
				else {
					return name1.compareTo(name2);
				}
			}
		});

		pqueue.add(new Node(node[0].name, node[0].visited, node[0].time));
		Node currentNode;

		while(!pqueue.isEmpty()) {
			currentNode = pqueue.peek();
			if (currentNode.visited == false) {
				//this node is not one of the end nodes, visit this node
				if (!endNodeSet.contains(currentNode.name)) {

					pqueue.poll();
					getNode(currentNode.name).visited = true;

					Iterator<Node> iter = pqueue.iterator();
					while (iter.hasNext()) {
						Node iterNode = iter.next();
						if (iterNode.name.equals(currentNode.name)) {
							iterNode.visited = true;
						}
					}
					//add next available nodes to the priority queue
					for (int j = 0; j < edge.length; j++) {

						Node edgeEnd = getNode(edge[j].endNode);

						if (currentNode.name.equals(edge[j].startNode) && edgeEnd.visited==false) {
							if(!edge[j].brokeTime.contains(currentNode.time % 24)) {
								pqueue.add(new Node (edgeEnd.name, false, currentNode.time + edge[j].length));

							}
						}
					}
				}
				//first node is found
				else {
					path.name = currentNode.name;
					path.time = currentNode.time;
					break;
				}
			}
			//this node has been visited before
			else {
				pqueue.poll();
			}
		}
		//no path available
		if (pqueue.isEmpty()) {
			path.time = -1;
		}
	}
}

