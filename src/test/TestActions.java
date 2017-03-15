//package test;
//
//import components.*;
//import sim.engine.SimState;
//import sim.engine.Steppable;
//import sim.engine.Stoppable;
//
//
//import java.util.ArrayList;
//
///**
// * Created by eva on 24/01/2017.
// */
//public class TestActions extends SimState implements Steppable {
//    private int nstep;
//    private int NSTEPS=1;
//    public Stoppable scheduleRepeat;
//    private ArrayList<Bank> banks;
//    private ArrayList<Hedgefund> hedgefunds;
//    public Parameters parameters;
//    public Market market;
//
//
//
//
//
//    public TestActions(long seed) {
//        super(seed);
//    }
//
//    public void start() {
//        super.start();
//        parameters = new Parameters();
//        parameters.initialise();
//        market = new Market(1.0);
//
//        double[][] repos={{5,2,3,0,0},{1,3,2,0,0},{4,8,4,0,0},{0,0,0,0,0},{0,0,0,0,0}};
//        double [] bankliabilities={10,12,13,0,0};
//
//        banks = new ArrayList<>();
//        for (int j = 1; j < 4; j++) {
//            BankBalanceSheet sheet = new BankBalanceSheet();
//            Bank bank = new Bank("Bank " + j);
//            bank.setBalanceSheet(sheet);
//            bank.getBehaviour().setMarket(market);
//            bank.getBalanceSheet().addRepo(repos[j-1][0], repos[j-1][1], repos[j-1][2], repos[j-1][3], repos[j-1][4]);
//            bank.getBalanceSheet().setLiability(bankliabilities[j-1]);
//            bank.getBehaviour().setNO();
//            bank.getBalanceSheet().setCash(bank.getBehaviour().kappa_T*bank.getBehaviour().getNO());
//            double K = ((bank.getBehaviour().lambda_T-1)*bank.getBalanceSheet().getCash()+bank.getBehaviour().lambda_T*bank.getBalanceSheet().getTotalRepo()+bank.getBalanceSheet().getLiability())/(market.S*(1-bank.getBehaviour().lambda_T));
//            bank.getBalanceSheet().setStocks(K+10);
//
//            banks.add(bank);
//
//        }
//
//        hedgefunds = new ArrayList<>();
//        for (int i = 1; i < 4; i++) {
//            HedgefundBalanceSheet sheet = new HedgefundBalanceSheet();
//            Hedgefund hedgefund = new Hedgefund("Hedgefund " + i);
//            hedgefund.setBalancesheet(sheet);
//            hedgefund.getBehaviour().setMarket(market);
//            hedgefund.getBalancesheet().addRepo(repos[0][i-1], repos[1][i-1], repos[2][i-1], repos[3][i-1], repos[4][i-1]);
//            hedgefund.getBalancesheet().setStocks(hedgefund.getBalancesheet().getTotalFunding()/(1-(parameters.getGlobalParameters().get("alpha"))*market.S)*1.2);
//            hedgefund.getBalancesheet().setCash((hedgefund.getBalancesheet().getPhi()*market.S)*0.3);
//            hedgefunds.add(hedgefund);
//
//        }
//
//        System.out.println("INITIAL BALANCESHEETS");
//
//        System.out.println("---------------------------------------");
//
//        banks.get(0).printBalanceSheet();
//        System.out.println(banks.get(0).getBehaviour().returnLeverage());
//        banks.get(1).printBalanceSheet();
//        System.out.println(banks.get(1).getBehaviour().returnLeverage());
//
//        banks.get(2).printBalanceSheet();
//        System.out.println(banks.get(2).getBehaviour().returnLeverage());
//
//        hedgefunds.get(0).printBalanceSheet();
//        hedgefunds.get(1).printBalanceSheet();
//        hedgefunds.get(2).printBalanceSheet();
//
//        market.setS(market.S*0.7);
//
//        nstep=0;
//        scheduleRepeat = schedule.scheduleRepeating(this);
//
//
//    }
//
//    @Override
//    public void step(SimState simstate) {
//
//        System.out.println("N_HEDGEFUNDS'm starting step "+nstep+" with price"+market.S);
//        System.out.println("-------------------------------------");
//
//
//        for (int j = 0; j < 3; j++) {
//           banks.get(j).printBalanceSheet();
//            banks.get(j).getBehaviour().checkSolvency();
//            banks.get(j).getBehaviour().checkLeverage();
//            banks.get(j).getBehaviour().deleverRule2();
//            banks.get(j).getBehaviour().checkLCR();
//
//        }
//        double Order=0;
//
//        for(int i=0; i<3; i++){
//            for (int j=0; j<3; j++) {
//                hedgefunds.get(i).getBehaviour().getFundingUpdate(j+1, banks.get(j).getBehaviour().giveFundingUpdate(i+1));
//            }
//            hedgefunds.get(i).getBehaviour().checkSolvency();
//            hedgefunds.get(i).getBehaviour().repayFunding();
//            hedgefunds.get(i).getBehaviour().marginCall();
//            Order=Order+hedgefunds.get(i).getBehaviour().placeMarketOrder();
//
//
//            hedgefunds.get(i).getBehaviour().updateBalanceSheet();
//            hedgefunds.get(i).printBalanceSheet();
//
//
//        }
//
//
//        for (int j=0; j<3;j++){
//            for (int i=0; i<3; i++) {
//                banks.get(j).getBehaviour().getDefaultInfo(i+1, hedgefunds.get(i).D*hedgefunds.get(i).D_);
//
//            }
//
//            //NOTE THIS NOW ONLY WORKS IF THERE'S THE SAME NUMBER OF HEDGEFUNDS AND BANKS
//
//            Order=Order+banks.get(j).getBehaviour().placeMarketOrder();
//
//            banks.get(j).getBehaviour().updateBalancesheet();
//            banks.get(j).printBalanceSheet();
//        }
//
//        double k=0;
//        k=0;
//        for(int j=0;j<3;j++){
//            k=banks.get(j).B+k;
//            k=(1-banks.get(j).D)+k;
//
//        }
//
//        for(int i=0; i<3; i++){
//            k=(1-banks.get(i).D)+k;
//
//        }
//
//        market.updateMarket(Order);
//
//        nstep++;
//        System.out.println("k is "+k);
//        if (k==0) {
//            simstate.kill();
//            System.out.println("N_HEDGEFUNDS am stopping now. The final statistics are");
//            System.out.println("Final Stockprice" + market.S);
//            System.out.println("Number of steps"+nstep);
//        }
//
//
//
//        }
//
//    public static void main(String[] args){
//        doLoop(TestActions.class, args);
//        System.exit(0);
////
//
//    }
//
//
//
//
//
//
//}
