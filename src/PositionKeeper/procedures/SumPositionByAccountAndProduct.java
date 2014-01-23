package PositionKeeper.procedures;

import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
import org.voltdb.VoltType;

@ProcInfo (
	    partitionInfo = "trades.account_id:0",
	    singlePartition = true
	)

public class SumPositionByAccountAndProduct  extends VoltProcedure{
    public static final SQLStmt resultStmt = new SQLStmt(
            "SELECT sum(position_delta) from trades where account_id = ? and product_cusip = ?");

    static class Result {
        public final long count;

        public Result(long count) {
            this.count = count;
        }
    }

    public VoltTable run(String accountId, String productCuip)
    {
    	VoltTable result = new VoltTable(
                 new VoltTable.ColumnInfo("Position", VoltType.BIGINT));
        voltQueueSQL(resultStmt,accountId,productCuip);
        VoltTable summary = voltExecuteSQL()[0];
        while(summary.advanceRow()) {
        	result.addRow(new Object[]{summary.getLong(0)});
        }  
        return result;
    }
}
