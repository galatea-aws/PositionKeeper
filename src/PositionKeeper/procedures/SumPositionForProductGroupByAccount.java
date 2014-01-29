package PositionKeeper.procedures;

import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class SumPositionForProductGroupByAccount extends VoltProcedure{
    public static final SQLStmt resultStmt = new SQLStmt(
            "SELECT account_id,sum(position_delta) FROM trades WHERE product_cusip = ? GROUP BY account_id");

    public VoltTable run(String productCusip)
    {
        voltQueueSQL(resultStmt,productCusip);
        VoltTable summary = voltExecuteSQL()[0];
        return summary;
    }
}
