package PositionKeeper;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.voltdb.client.Client;

public class TradeGenerator {
	
	private int maxAccounts = 0;
	private int maxProducts = 0;
	private Random random = new Random();
    public static class Trade {
        public long tradeId;
        public String accountId;
        public String productCusip;
        public String exchange;
        public String status;
        public String sourcesystemId;
        public Date knowledgeDate;
        public Date effectiveDate;
        public Date settlementDate;
        public long positionDelta;
        public String createUser;
        public Date createTimestamp;
        public String lastUpdateUser;
        public Date lastUpdateTimestamp;
        protected Trade(long tradeId, String accountId, String productCusip, String exchange, String status, String sourcesystemId, 
        		Date knowledgeDate, Date effectiveDate, Date settlementDate, long positionDelta, 
        		String createUser, Date createTimestamp, String lastUpdateUser, Date lastUpdateTimestamp) {
            this.tradeId = tradeId;
        	this.accountId = accountId;
            this.productCusip = productCusip;
            this.exchange = exchange;
            this.status = status;
            this.sourcesystemId = sourcesystemId;
            this.knowledgeDate = knowledgeDate;
            this.effectiveDate = effectiveDate;
            this.positionDelta = positionDelta;
        }
    }
    
    public TradeGenerator(int maxAccounts, int maxProducts){
    	this.maxAccounts = maxAccounts;
    	this.maxProducts = maxProducts;
    }
    
    public Trade CreateTrade(long tradeId, Date knowledgeDate, Date effectiveDate, Client client, int probabilityByIsin){
        String accountId = "account" + (random.nextInt(maxAccounts)+1);
        
    	int productId = random.nextInt(maxProducts)+1;
    	String productCusip = "cusip" + productId;
    	String productIsin = StringUtils.rightPad(String.valueOf(productId), 12, "0");
    	
        //Look up product cusip by product isin.
    	boolean getProductByIsin = random.nextInt(100)<probabilityByIsin;
    	if(getProductByIsin){
    	}
    	
    	
    	String exchange = "XX";
    	String status = "Y";
    	String sourcesystemId = "testsystem" + tradeId;
    	
    	//Set settlement Date
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(effectiveDate);
    	calendar.add(Calendar.DAY_OF_YEAR, 3);
    	Date settlementDate = calendar.getTime();
         
        long positionDelta = random.nextInt(1000)*(random.nextBoolean()?1:-1);
        String user = "TestAccount";
    	return new Trade(tradeId, accountId, productCusip, exchange, status, sourcesystemId, 
    			knowledgeDate, effectiveDate, settlementDate, positionDelta,
    			user, new Date(), user, new Date());
    }
}
