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

@ProcInfo (
	    partitionInfo = "trades.account_id:0",
	    singlePartition = true
	)

public class SumPositionForAccountByProductTester extends VoltPerformanceTester{
	
	public SumPositionForAccountByProductTester() {
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
        while(result.advanceRow()) {
            String output = "SumPositionForAccountGroupByProduct," + queryDuration + "," + result.getRowCount() + "," + SumPositionForAccountGroupByProduct.resultStmt.getText();
            System.out.println(output);
        }

        // block until all outstanding txns return
        client.drain();

        // close down the client connections
        client.close();
    }
	
    public static void main(String[] args) throws Exception {
        
        SumPositionByAccountAndProductTester tester = new SumPositionByAccountAndProductTester();
        tester.run();
    }
}
