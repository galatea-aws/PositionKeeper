package PositionKeeper.procedures;

import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
import org.voltdb.VoltType;

@ProcInfo (
	    singlePartition = false
)
public class SumPositionForAccountGroupByProduct extends VoltProcedure{
    public static final SQLStmt resultStmt = new SQLStmt(
            "SELECT product_cusip,sum(position_delta) FROM trades WHERE account_id = ? GROUP BY product_cusip");


    public VoltTable run(String accountId)
    {
        voltQueueSQL(resultStmt,accountId);
        VoltTable summary = voltExecuteSQL()[0];
        return summary;
    }
}
