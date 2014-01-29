package PositionKeeper;

import java.io.IOException;

import org.voltdb.VoltTable;
import org.voltdb.client.NoConnectionsException;
import org.voltdb.client.ProcCallException;

import PositionKeeper.procedures.SumPositionForAccountGroupByProduct;
import PositionKeeper.procedures.SumPositionForProductGroupByAccount;

public class SumPositionForProductGroupByAccountTester  extends VoltPerformanceTester{
	
	public SumPositionForProductGroupByAccountTester() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public void run() throws NoConnectionsException, IOException, ProcCallException, InterruptedException{
        connect();
        
    	long queryStartTS = System.currentTimeMillis();

    	String productCusip = procedureProp.getProperty("SumPositionForProductGroupByAccount.","cusip2");
    	
    	VoltTable result = client.callProcedure("SumPositionForProductGroupByAccount",
    			productCusip).getResults()[0];
    	
    	String queryDuration = String.valueOf((double)(System.currentTimeMillis()-queryStartTS)/1000f);
    	System.out.println("SumPositionForProductGroupByAccount");
    	System.out.println(SumPositionForProductGroupByAccount.resultStmt.getText());
    	System.out.println(queryDuration);
    	System.out.println(result.getRowCount());

        // block until all outstanding txns return
        client.drain();

        // close down the client connections
        client.close();
    }
	
    public static void main(String[] args) throws Exception {
        
    	SumPositionForProductGroupByAccountTester tester = new SumPositionForProductGroupByAccountTester();
        tester.run();
    }
}
