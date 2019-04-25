import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FP_Growth 
{
	/*
	 *  FP-Growth  (Frequent Pattern Growth) 
	 *  Recursive algorithm 
	 *  
	 *  Its recursive process consists of two main steps: 
	 *  1. Scan database to find frequent items 
	 *  2. Construct FP-Tree
	 *  
	 *  2018/06/24 yu-huei
	 */
	
	static int min_support = 100; // theta
	static int count = 0; // Calculate the number of combinations
	
	public static void main(String args[]) throws IOException
	{
		
		long startTime = System.currentTimeMillis();
		
		// the original database .
		ArrayList<ArrayList <Integer>> Database = new ArrayList<ArrayList <Integer>>(); 
		
		String filePath = "D:\\T10I4D100K.txt";  
		
		scanDatabase(filePath, Database);
		
		// recursive algorithm .
		FPGrowth(Database, "[");
		
		System.out.println(count + " combinations");
		
		long endtime = System.currentTimeMillis();
		
		System.out.println("Need: " + (endtime - startTime) + " ms");
	}
	
	// Scan the original database .
	public static void scanDatabase(String filePath, ArrayList<ArrayList <Integer>> db)throws IOException
	{
		FileReader fr = new FileReader(filePath);
		BufferedReader bfr = new BufferedReader(fr);
		
		ArrayList <Integer> tmp = new ArrayList <Integer>();
		int numTran = 0;
		
		String tran = "";
		while((tran = bfr.readLine()) != null)
		{
			String s[] = tran.split(" ");
			
			for(int i = 0; i < s.length; i++)
			{
				int item = Integer.parseInt(s[i]);
				tmp.add(item);
			}
			
			tmp.add(1); // add the count of the transaction
			db.add(tmp); 
			
			tmp = new ArrayList<Integer>();	
			numTran++;
		}
		System.out.println("The num of the transaction: " + numTran);
			
		fr.close();
		bfr.close();
	}
	
	// Print the Two-dimensional ArrayList of the database .
	public static void showDatabase(ArrayList<ArrayList <Integer>> db)
	{
		for(int i = 0; i < db.size(); i++)
		{
			System.out.println(db.get(i));
		}
	}
	
	// Create the header table .
	public static ArrayList<treenode> GenHeaderTable(ArrayList<ArrayList <Integer>> db)
	{
		ArrayList<treenode> headertable = new ArrayList<treenode>();
		
		// Count the items .
		ArrayList <Integer> item = new ArrayList <Integer>();
		ArrayList <Integer> count = new ArrayList <Integer>();

		for(int i = 0; i < db.size(); i++)
		{
			for(int j = 0; j < db.get(i).size() - 1; j++) 
			{
				if(!item.contains(db.get(i).get(j)))
				{
					item.add(db.get(i).get(j));
					count.add(db.get(i).get(db.get(i).size() - 1)); //every transaction of the last number is the count . 
				}
				else 
				{
					count.set(item.indexOf(db.get(i).get(j)), count.get(item.indexOf(db.get(i).get(j))) + db.get(i).get(db.get(i).size() - 1));
				}
			}
		}

		// Build the header table (tree node) .
		for(int i = 0; i < item.size(); i++)
		{
			if(count.get(i) >= min_support)
			{
				headertable.add(new treenode(item.get(i), count.get(i)));
			}
		}
		
		// Sort the header table (Bubble Sort) .
		for(int i = headertable.size() - 1; i > 0; i--)  
		{
			for(int j = 0; j < i; j++)
			{
				if(headertable.get(j).count < headertable.get(j + 1).count)
				{
					treenode tmp = headertable.get(j);
					headertable.set(j, headertable.get(j + 1));
					headertable.set(j + 1, tmp);
				}
			}
		}
	    return headertable;	
	}
	
	// Print the ArrayList of the header table .
	public static void showHeadertable(ArrayList<treenode> headertable)
	{
		for(int i = 0; i < headertable.size(); i++)
		{
			System.out.println(headertable.get(i).item + " " + headertable.get(i).count);
		}		
	}

	// Generate the ordered Frequent Item .
	public static ArrayList<ArrayList<Integer>> GenFrequentItem(ArrayList<ArrayList <Integer>> db, ArrayList<treenode> headertable)
	{
		ArrayList<ArrayList <Integer>> FI = new ArrayList<ArrayList <Integer>>(); //Frequent Items
		ArrayList <Integer> tmp = new ArrayList <Integer>();
		
		for(int i = 0; i < db.size(); i++)
		{
			if(db.get(i).size() - 1 >= 0)
			{
				Object count = db.get(i).remove(db.get(i).size() - 1);
				for(int j = 0; j < headertable.size(); j++)
				{
					if(db.get(i).contains(headertable.get(j).item))
					{
						tmp.add(headertable.get(j).item);
					}
				}
				db.get(i).add((Integer) count);
				
				tmp.add((Integer) count);
				FI.add(tmp);
			}
			tmp = new ArrayList<Integer>();
		}
		return FI;
	}
	
	// Show the ordered Frequent Items.
	public static void showFrequentItem(ArrayList<ArrayList <Integer>> FI)
	{
		for(int i = 0; i < FI.size(); i++)
		{
			System.out.println(FI.get(i));
		}
	}
	
	// Create the FP tree .
	public static treenode FPtree(ArrayList<ArrayList <Integer>> FI, ArrayList<treenode> headertable)
	{
		treenode root_tmp = new treenode(-1, 0); // Define the root as minus 1 
		treenode root = root_tmp; // Save the original root
		
		for(int i = 0; i < FI.size(); i++)
		{
			for(int j = 0; j < FI.get(i).size() - 1 ; j++)
			{   
				boolean find = false;
				treenode node = new treenode(FI.get(i).get(j), FI.get(i).get(FI.get(i).size() - 1));
				
				// Traverse the child node of root_tmp
				for(int k = 0; k < root_tmp.child.size(); k++)
				{
					// Find the same child then add the count
					if(root_tmp.child.get(k).item == node.item)
					{
						root_tmp.child.get(k).count = root_tmp.child.get(k).count + FI.get(i).get(FI.get(i).size() - 1);
						root_tmp = root_tmp.child.get(k);
						find = true;
				    }
				}
				
				// Create the new child node 
				if(find == false)
				{
					root_tmp.child.add(node);
					node.parent = root_tmp;
					root_tmp = node;
					
					// Link the header table node to the tree node
					for(int k = 0; k < headertable.size(); k++)
					{
						// the item of the header table == the item of the tree node
						if(headertable.get(k).item == node.item)
							headertable.get(k).nodelink.add(node);
					}
				}
			}
			root_tmp = root; // Return to original root
		}
		
		//TraverseTree(root, 0);
		//System.out.println();
		return root;
	}
	
	// Traverse the tree .
	public static void TraverseTree(treenode n, int depth) 
	{
		System.out.println("[" + n.item + ", " + n.count + "]");
		depth++;
		for (int i = 0; i < n.child.size(); i++) 
		{
			for(int j = 0; j < depth; j++)
			{
				System.out.print("     ");
			}
			System.out.print(" |------->");
			TraverseTree(n.child.get(i), depth);
		}
	}
	
	// Frequent Pattern Growth .
	public static void FPGrowth(ArrayList<ArrayList <Integer>> db, String item_flag)
	{
		// header
		ArrayList<treenode> headertable = GenHeaderTable(db);
		
		// FrequentItem
		ArrayList<ArrayList <Integer>> FI = GenFrequentItem(db, headertable);
		treenode root = FPtree(FI, headertable);
		if(headertable.size() <= 0)
		{
			return;
		}

		// Find the parent node 
		ArrayList<ArrayList<Integer>> newDB = new ArrayList<ArrayList<Integer>>();
	    ArrayList<Integer> tmpDB = new ArrayList<Integer>();
		treenode tmp;
		String item_flag2;
		
		for(int i = headertable.size() - 1; i >= 0; i--)
		{
			newDB = new ArrayList<ArrayList<Integer>>(); //clear the new database
			
			item_flag2 = item_flag + " " + String.valueOf(headertable.get(i).item);
			
			System.out.println(item_flag2 + "]    count:" + headertable.get(i).count);
			
			count ++; // Calculate the number of combinations
			
			for(int j = 0; j < headertable.get(i).nodelink.size(); j++)
			{
				
				treenode tmp2 = headertable.get(i).nodelink.get(j).parent;
				if(tmp2.item != -1)
				{
					tmpDB.add(headertable.get(i).nodelink.get(j).parent.item);
				}
				
				while(tmp2 != root)
				{
					tmp = tmp2;
					tmp2 = tmp.parent;
					
					if(tmp2.item != -1)
					{
						tmpDB.add(tmp2.item);
					}
					if(tmp2.item == -1)
					{
						tmpDB.add(headertable.get(i).nodelink.get(j).count);
					}
				}
				newDB.add(tmpDB);
				tmpDB = new ArrayList<Integer>();
			}
			
			// recursive
			FPGrowth(newDB, item_flag2);
		}
	}
	
	public static class treenode
	{
		int item; 
		int count;
		
		treenode parent = null; 
		
		ArrayList <treenode> child;
		ArrayList <treenode> nodelink; // Storage the node link 
		
		// Constructor
		public treenode(int item, int count)
		{
			this.item = item;
			this.count = count;
			this.parent = null;
			this.child = new ArrayList <treenode>(0);
			this.nodelink = new ArrayList <treenode>(0);
		}
	}
}