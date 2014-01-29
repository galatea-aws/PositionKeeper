package PositionKeeper;

import java.io.IOException;

import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
import org.voltdb.client.NoConnectionsException;
import org.voltdb.client.ProcCallException;

import PositionKeeper.procedures.SumPositionByAccountAndProduct;
import PositionKeeper.procedures.SumPositionForAccountGroupByProduct;
import PositionKeeper.procedures.SumPositionForProductGroupByAccount;

public class SumPositionForAccountGroupByProductTester extends VoltPerformanceTester{
	
	public SumPositionForAccountGroupByProductTester() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public void run() throws NoConnectionsException, IOException, ProcCallException, InterruptedException{
        connect();
        
    	long queryStartTS = System.currentTimeMillis();

    	String accountId = procedureProp.getProperty("SumPositionForAccountGroupByProduct.accountid","account1");
    	
    	VoltTable result = client.callProcedure("SumPositionForAccountGroupByProduct",
    			accountId).getResults()[0];
    	
    	String queryDuration = String.valueOf((double)(System.currentTimeMillis()-queryStartTS)/1000f);
    	System.out.println("SumPositionForAccountGroupByProduct");
    	System.out.println(SumPositionForAccountGroupByProduct.resultStmt.getText());
    	System.out.println(queryDuration);
    	System.out.println(result.getRowCount());

        // block until all outstanding txns return
        client.drain();

        // close down the client connections
        client.close();
    }
	
    public static void main(String[] args) throws Exception {
        
    	SumPositionForAccountGroupByProductTester tester = new SumPositionForAccountGroupByProductTester();
        tester.run();
    }
}
