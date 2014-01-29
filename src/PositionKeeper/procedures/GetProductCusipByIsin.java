package PositionKeeper.procedures;

import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
import org.voltdb.VoltType;

public class GetProductCusipByIsin extends VoltProcedure{
    public final static SQLStmt resultStmt = new SQLStmt(
            "SELECT product_cusip from products where product_isin = ?");
    

    public VoltTable run(String productIsin)
    {
        voltQueueSQL(resultStmt,productIsin);
        VoltTable summary = voltExecuteSQL()[0];
        
        return summary;
    }
}
