/* This file is part of VoltDB.
 * Copyright (C) 2008-2013 VoltDB Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */


//
// Accepts a vote, enforcing business logic: make sure the vote is for a valid
// contestant and that the voter (phone number of the caller) is not above the
// number of allowed votes.
//

package PositionKeeper.procedures;

import java.util.Date;

import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
import org.voltdb.types.TimestampType;

@ProcInfo (
    partitionInfo = "trades.account_id:1",
    singlePartition = true
)
public class DoTrade extends VoltProcedure {

    // Records a vote
    public final SQLStmt insertTradeStmt = new SQLStmt("INSERT INTO trades "
								    					+ "(account_id, trade_id, product_cusip, exchange, status, sourcesystem_id, "
								    					+ "knowledge_date, effective_date, settlement_date, position_delta,"
									            		+ "create_user, create_timestamp, last_update_user, last_update_timestamp) "
									            		+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);");

    public long run(long tradeId, String accountId, String productCusip, String exchange, String status, String sourcesystemId, 
    		Date knowledgeDate, Date effectiveDate, Date settlementDate, long positionDelta, 
    		String createUser, Date createTimestamp, String lastUpdateUser, Date lastUpdateTimestamp) {
        // Post the vote
        voltQueueSQL(insertTradeStmt, EXPECT_SCALAR_MATCH(1), accountId, tradeId,productCusip, exchange, status, sourcesystemId, 
        		new TimestampType(knowledgeDate), new TimestampType(effectiveDate), new TimestampType(settlementDate), positionDelta,
        		createUser, new TimestampType(createTimestamp), lastUpdateUser, new TimestampType(lastUpdateTimestamp));
        voltExecuteSQL(true);

        // Set the return value to 0: successful vote
        return 0;
    }
}
