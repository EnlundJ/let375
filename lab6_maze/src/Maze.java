import java.util.List;

public class Maze extends Board {
	
	private DisjointSets set;
	private ExtendedGraph graph;
  		
    public Maze( int rows, int cols )
	{
    	super(rows,cols);
    	set = new DisjointSets(rows*cols);
    	graph = new ExtendedGraph();
    }
    
    public void create()
    {
    	// Draw the initial board
    	setChanged();
    	notifyObservers();

    	int knockedWalls = 0;
    	while(knockedWalls < (maxCell-1))
    	{
    		// Get a random cell
    		int cellId = (int)(Math.random()*maxCell);
    		Point first = new Point(getRow(cellId), getCol(cellId));
    		Point second = new Point(first.row, first.col);

    		// Select a random neighbor
    		Point.Direction direction;
    		switch((int)(Math.random()*4))
    		{
    		case 0: direction = Point.Direction.UP; break;
    		case 1: direction = Point.Direction.RIGHT; break;
    		case 2: direction = Point.Direction.LEFT; break;
    		case 3: direction = Point.Direction.DOWN; break;
    		default: direction = Point.Direction.ERROR;
    		}
    		second.move(direction);
    		
    		if(!isValid(second))
    		{
    			switch(direction)
    			{
    			case UP: direction = Point.Direction.DOWN; break;
    			case DOWN: direction = Point.Direction.UP; break;
    			case LEFT: direction = Point.Direction.RIGHT; break;
    			case RIGHT: direction = Point.Direction.LEFT; break;
    			default:
    				continue;
    			}
    			second.move(direction); // Move back
    			
    			// Special case where there is only one row
    			if((direction == Point.Direction.UP || direction == Point.Direction.DOWN) && maxRow == 1)
    				if(second.col == 0)
    					direction = Point.Direction.RIGHT;
    				else
    					direction = Point.Direction.LEFT; // More left than right but who cares?
    			
    			// special case where there is only one column
    			if((direction == Point.Direction.LEFT || direction == Point.Direction.RIGHT) && maxCol == 1)
    				if(second.row == 0)
    					direction = Point.Direction.DOWN;
    				else
    					direction = Point.Direction.UP; // More up than down but who cares?

    			second.move(direction); // Move in opposite direction
    		}
    		
    		int firstId = getCellId(first);
    		int secondId = getCellId(second);
    		
    		// Knock down the wall if the second cell is not a part of the same set
    		int firstRoot = set.find(firstId);
    		int secondRoot = set.find(secondId);
    		if(firstRoot != secondRoot)
    		{
    			set.union(firstRoot, secondRoot);	// Join the sets

    			// Knock down the wall 
    			setChanged();							
    			notifyObservers(new Pair<Integer, Point.Direction>(cellId, direction));
    			knockedWalls++;
    			
    			// Add edges in graph between the cells
    			graph.addEdge(firstId, secondId, 1);
    			graph.addEdge(secondId, firstId, 1);
    		}
    	}
   }
    
    public void search()
    {
    	graph.unweighted(0);
//    	graph.printPath(maxCell-1);
    	List<Integer> list = graph.getPath(maxCell-1);
    	
    	for(Integer i : list)
    	{
     		setChanged();
    		notifyObservers(i);
    	}
    }
    
}
