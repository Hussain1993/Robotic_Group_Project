package Rumbaugh;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerPose2d;

/**
 * Class that provides an A* searching algorithm, as well as
 * methods to return the walkable neighbours of a specific node
 * and calculate heuristics. It also provides a method that makes
 * the robot to actually follow that path.
 * @author Ionut
 *
 */

public class PathPlanner {
	ArrayList<String> closedset = new ArrayList<String>();		//contains nodes that have been already visited
	ArrayList<String> parent = new ArrayList<String>();			// contains all nodes that have been visited, with their parents
	ArrayList<String> openset = new ArrayList<String>();		//contains potential nodes to explore in next step
	ArrayList<String> openset2 = new ArrayList<String>();		//same as above, except it doesn't hold the 3rd value (f*(n)), which
																//eases the 'contains' search

	ArrayList<String> neighbours = new ArrayList<String>();		//will hold walkable neighbours of each node, via getNeighbours method
	static int[][] mapArray;										//the map
	String current;												//current node
	String target;												//target node
	static int[][] array = null;

	Position2DInterface pos2d;
	RangerInterface rngi;
	
	public PathPlanner(Position2DInterface pos2d , RangerInterface rngi) {
		this.pos2d = pos2d;
		this.rngi = rngi;
		
		// Load the map from testfile.txt
	//	try { testMap(); } catch (IOException e) { }
	}
	
	public void goToPoint(Point target) {
//		while (!pos2d.isDataReady());
//		Point startPoint = new Point(getLocation());
//		System.out.println("Going from " + startPoint + " to " + target);
		mapArray = RobotData.INSTANCE.getMap();
		ArrayList<Point> path = getPath(getLocation(), target);
		ArrayList<Point> straight = straightLines(path);
    	for(int i=0;i<path.size();i++){
    		System.out.println(path.get(i));
    	}
    	executePath(straight,false);
		
		//return when it gets to the point
	}
	
	public ArrayList<Point> getPath(Point start, Point end) {
		Asearch(start, end);
		return reconstructPath();
	}
	
	public static double getAngle(double a,double b,double c, double d){
		if((a>=c&&b>=d)||(a>=c&&b<=d))
			return Math.asin((d-b)/
	        		Math.sqrt(Math.pow((a-c),2)+
	        				Math.pow((b-d), 2)));
			else if(a<c&&b<=d)
				
				return Math.toRadians(90.0)+Math.acos((b-d)/
		        		Math.sqrt(Math.pow((a-c),2)+
		        				Math.pow((b-d), 2)));
			else return -Math.toRadians(90.0)+Math.asin((a-c)/
	        		Math.sqrt(Math.pow((a-c),2)+
	        				Math.pow((b-d), 2)));
	}
	
	public void executePath(ArrayList<Point> nodes, boolean skipLast) {
		int i=1;
		//for(int j=0;j<nodes.size();j++)
			//mapArray[nodes.get(j).x][nodes.get(j).y] = 4;
		double yaw;
		while(i<nodes.size()){
			int j = i+1;
			
			if (j < nodes.size()) {
				Point nextNode = nodes.get(j);
				Point currentNode = nodes.get(i);
				int x = nextNode.x - currentNode.x;
				int y = nextNode.y - currentNode.y;
				yaw = getAngle(nextNode.y, nextNode.x, currentNode.y, currentNode.x);
			} else yaw = 0;
			
			PlayerPose2d pp2d = new PlayerPose2d(transformX(nodes.get(i).y), transformY(nodes.get(i).x), yaw);
			pos2d.setPosition( pp2d, new PlayerPose2d(1, 1, 1), 0);
			System.out.println(pp2d.getPx() + "  " + pp2d.getPy());
			boolean b= true;
			System.out.println(i);
			while(b){
				while(!rngi.isDataReady());
				double[] sonars = rngi.getData().getRanges();
				double min = sonars[0];
				if(min<sonars[1])
					min = sonars[1];
				if(min<sonars[2])
					min = sonars[2];
				while(!pos2d.isDataReady());
				if(inRange(pp2d.getPx(),pos2d.getX(), 0.4) && inRange(pp2d.getPy(),pos2d.getY(),0.4) || min < 0.3){
					b= false;
	
				}
			}
			// skip the last node
			if (i == nodes.size()-2 && skipLast == true) break;
			i++;
		}
		System.out.println("Done");
			
		}
		
	
    public Point getLocation() {
    	while (!pos2d.isDataReady()) {};
        return new Point((int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(RobotData.HEIGHT_OFFSET + pos2d.getY())),
        				 (int)Math.round(RobotData.RESOLUTION*(RobotData.LENGTH_OFFSET+pos2d.getX())));
    }
    public static String[][] mapFromFile(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        ArrayList<String[]> strng = new ArrayList<String[]>();
        String line = "";
        while((line = bufferedReader.readLine()) != null) {
        		String[] lineArray = line.split(" ");
        		strng.add(lineArray);
        }
        bufferedReader.close();
        return strng.toArray(new String[0][0]);
    }
    public static void testMap() throws IOException{
    	String[][] mapArray = mapFromFile("src/Rumbaugh/testfile.txt");
    	int h = mapArray.length;
    	int l = mapArray[0].length;
    	int[][] arr = new int[h][l];
    	array = arr;
    	for(int i=0;i<h;i++)
    		for(int j=0;j<l;j++)
    			arr[i][j] = Integer.parseInt(mapArray[i][j]);
    	
    	RobotData.INSTANCE.setMap(arr);
    	
    	ArrayList<Point> garb = new ArrayList<Point>();
    	garb.add(new Point((int)RobotData.convertY(0),(int)RobotData.convertX(-8)));
    	garb.add(new Point((int)RobotData.convertY(0),(int)RobotData.convertX(-9)));
    	garb.add(new Point((int)RobotData.convertY(-6),(int)RobotData.convertX(-7)));
    	garb.add(new Point((int)RobotData.convertY(4),(int)RobotData.convertX(-6)));
    	garb.add(new Point((int)RobotData.convertY(4),(int)RobotData.convertX(-1)));
    	garb.add(new Point((int)RobotData.convertY(6),(int)RobotData.convertX(-2)));
    	//garb.add(new Point((int)RobotData.convertY(4),(int)RobotData.convertX(-1)));

    	
    	RobotData.INSTANCE.setGarbage(garb);
    }
	
    private static double transformX (int X){
    	return ((X/ RobotData.RESOLUTION) -RobotData.LENGTH_OFFSET);
    }
    private static double transformY (int Y){
    	return ((RobotData.ARRAY_HEIGHT - Y)/RobotData.RESOLUTION) - RobotData.HEIGHT_OFFSET;
    }
	
	public void Asearch(Point startPoint, Point goalPoint){
		String start = startPoint.x + " " + startPoint.y + " " +
				"0";
		String goal = goalPoint.x + " " + goalPoint.y;
				
		target = goal;
		openset.add(start);
		openset2.add(process(start));
		parent.add(process(start));
		while(!openset.isEmpty() && !closedset.contains(goal)){
			current = retMin(openset);
			openset.remove(current);
			openset2.remove(process(current));
			closedset.add(process(current));
			neighbours = getNeighbours(current, goal);
			for(int i = 0 ; i<neighbours.size();i++){
				String s = process(neighbours.get(i));
				if(!closedset.contains(s)){
					if(!openset2.contains(s)){
						openset.add(neighbours.get(i));
						openset2.add(s);
						parent.add(process(current)+ " " +  s);
					}
					else{
						int index= 0;
						for(int j = 0;j<openset.size();j++){
							if(openset.get(j).startsWith(s))
								index = j;
						}
					
						if(Integer.parseInt(openset.get(index).split(" ")[2]) > Integer.parseInt(neighbours.get(i).split(" ")[2])){
							openset.remove(index);
							openset.add(neighbours.get(i));
							parent.add(process(current)+ " " + s);
						}
					}
				}
					
			}
		}
		
	}

	/**
	 * This method reconstructs the path, starting at
	 * the target node, and going backwards, to corresponding
	 * parent.
	 * @return An array list with the path between the two nodes.
	 */
	
	private ArrayList<Point> reconstructPath(){
		ArrayList<String> s = new ArrayList<String>();
		ArrayList<Point> reversed = new ArrayList<Point>();
		String s1= target;
		s.add(s1);
		for(int i= parent.size()-1;i>0;i--){
			if(parent.get(i).endsWith(s1)){
				s1= process(parent.get(i));
				s.add(s1);
			}
		}
		for(int i = s.size()-1;i>=0;i--){
			reversed.add(new Point(Integer.parseInt(s.get(i).split(" ")[0]), Integer.parseInt(s.get(i).split(" ")[1])));
		}
		
		return reversed;
	}
	
	
	/**
	 * This method computes the minimum f*(n) value
	 * (3rd int) in each element of the array list.
	 * @param array - The array list to be examined
	 * @return The element of the array having the 
	 * 			smallest f*(n) value.
	 */
	
	public String retMin(ArrayList<String> array){
		int min = getH(array.get(0), target);
		int j = 0;
		for(int i=0;i<array.size();i++)
			if(getH(array.get(i),target)< min){
				min = getH(array.get(i),target);
				j=i;
			}
		return array.get(j);
	}
	
	
	/**
	 * This method retrieves all walkable neighbours
	 * of a particular node, and computes the f*(n)
	 * value for each of them, via getH() method.
	 * @param str The node to get neighbours for
	 * @param tar  The target node, used to calculate f*(n) value
	 * @return	An array list with all walkable nodes
	 */
	
	public ArrayList<String> getNeighbours(String str, String tar){
		ArrayList<String> neighbors = new ArrayList<String>();
		int i = Integer.parseInt(str.split(" ")[0]);
		int j = Integer.parseInt(str.split(" ")[1]);
		int k = Integer.parseInt(str.split(" ")[2]);
        for(int alfa = i-1; alfa<= i+1; alfa++)
            for(int beta = j-1; beta<=j+1;beta++){
				if(mapArray[alfa][beta] == 3 &&(alfa != i || beta != j)){
					if((alfa+beta)%2 != (i+j)%2){
						neighbors.add(alfa + " " + beta+ " " + (10+k + getH(alfa+" " + beta, tar)));
					}
					else{
						if((alfa == i-1) && (beta == j-1) && 
							mapArray[i-1][j] == 3 && 
							mapArray[i][j-1] == 3)
							neighbors.add(alfa + " " + beta + " " + (14+k + getH(alfa+" " + beta, tar)));
						else if((alfa == i-1) && (beta == j+1) && 
								mapArray[i-1][j] == 3 && 
								mapArray[i][j+1] == 3)
								neighbors.add(alfa+ " " + beta + " " + (14+k + getH(alfa+" " + beta, tar)));
							else if((alfa == i+1) && (beta == j-1) && 
									mapArray[i][j-1] == 3 && 
									mapArray[i+1][j] == 3)
									neighbors.add(alfa + " " + beta + " " + (14+k + getH(alfa+" " + beta, tar)));
								else if((alfa == i+1) && (beta == j+1) && 
										mapArray[i+1][j] == 3 && 
										mapArray[i][j+1] == 3)
										neighbors.add(alfa+ " " + beta + " " + (14+k + getH(alfa+" " + beta, tar)));
					}
				}
				
			}
		

		return neighbors;
	}
	

	/**
	 * Gets the heuristic for a particular node,
	 * calculating the direct distance (straight line)
	 * between the node and the target
	 * @param cur	Current node
	 * @param tar	Target node
	 * @return	Integer value, representing the heuristic for a node
	 */
	
    public int getH(String cur, String tar){
        int xCur = Integer.parseInt(cur.split(" ")[0]);
        int yCur = Integer.parseInt(cur.split(" ")[1]);
        int xTar = Integer.parseInt(tar.split(" ")[0]);
        int yTar = Integer.parseInt(tar.split(" ")[1]);
        
        return (int) (10 *Math.sqrt(((xCur - xTar)* (xCur - xTar)) + ((yCur - yTar)* (yCur - yTar))));
}

	
	/**
	 * Processes a string, to retrieve only the
	 * first 2 integers (array indexes)
	 * @param string	String to be processed
	 * @return	A string containing the first 2 
	 * 			integers of the param.
	 */
	
	public String process(String string){
		return string.split(" ")[0] + " " + string.split(" ")[1];
	}
	public ArrayList<Point> straightLines(ArrayList<Point> array){
		ArrayList<Point> sLines = new ArrayList<Point>();
		sLines.add(array.get(0));
		if(array.size()>1){
		int prevDir = getDirection(array.get(0), array.get(1));
		for(int i= 2;i<array.size();i++){
			if(prevDir != getDirection(array.get(i-1), array.get(i))){
				sLines.add(array.get(i-1));
				prevDir = getDirection(array.get(i-1), array.get(i));
			}
		}
		}
		sLines.add(array.get(array.size()-1));
		
		return sLines;
	}
	
    public void goToPenultimate(Point target) {
		mapArray = RobotData.INSTANCE.getMap();
        ArrayList<Point> path = getPath(RobotData.INSTANCE.getLocation(), target);
        ArrayList<Point> straight = straightLines(path);
        executePath(straight, true);
        
    }
	public int getDirection(Point p, Point q){
		if(p.x == q.x && p.y+1 == q.y)
			return 0;
		else if(p.x == q.x+1 && p.y+1 == q.y)
			return 1;
		else if(p.x == q.x+1 && p.y == q.y)
			return 2;
		else if(p.x == q.x+1 && p.y == q.y+1)
			return 3;
		else if(p.x == q.x && p.y == q.y+1)
			return 4;
		else if(p.x+1 == q.x && p.y == q.y+1)
			return 5;
		else if(p.x+1 == q.x && p.y == q.y)
			return 6;
		else
			return 7;
	}
	public static boolean inRange(double a, double b, double c){
		if((a<b+c)&&(a>b-c))
			return true;
		else return false;
	}
	
}