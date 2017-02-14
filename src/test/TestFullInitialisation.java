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
//public class TestFullInitialisation extends SimState implements Steppable {
//    private int nstep;
//    private int NSTEPS=1;
//    public Stoppable scheduleRepeat;
//    private ArrayList<Bank> banks;
//    private ArrayList<Hedgefund> hedgefunds;
//    public Parameters parameters;
//
//
////STILL GETS STOCKPRICE FROM PARAMETERS RATHER THAN MARKET
//
//
//    public TestFullInitialisation(long seed) {
//        super(seed);
//    }
//
//    public void start() {
//        super.start();
//        parameters = new Parameters();
//        parameters.initialise();
//
//        double[][] repos={{5,2,3,0,0},{1,3,2,0,0},{4,8,4,0,0},{0,0,0,0,0},{0,0,0,0,0}};
//        double [] bankliabilities={10,12,13,0,0};
//
//        //TODO insert test for this matrix
//
//        banks = new ArrayList<>();
//        for (int j = 1; j < 4; j++) {
//            BankBalanceSheet sheet = new BankBalanceSheet();
//            Bank bank = new Bank("Bank " + j);
//            bank.setBalanceSheet(sheet);
//            bank.getBalanceSheet().addRepo(repos[j-1][0], repos[j-1][1], repos[j-1][2], repos[j-1][3], repos[j-1][4]);
//            bank.getBalanceSheet().addLiability(bankliabilities[j-1]);
//            bank.getBalanceSheet().addCash(bank.getBalanceSheet().getLiability()*parameters.getGlobalParameters().get("kappa_T")*parameters.getGlobalParameters().get("beta"));
//            double K = ((1-parameters.getGlobalParameters().get("lambda_T"))*bank.getBalanceSheet().getCash()+parameters.getGlobalParameters().get("lambda_T")*bank.getBalanceSheet().getTotalRepo()+bank.getBalanceSheet().getLiability())/(parameters.getGlobalParameters().get("InitialStockPrice")*(1-parameters.getGlobalParameters().get("lambda_T")));
//            bank.getBalanceSheet().addStocks(K);
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
//            hedgefund.getBalancesheet().addRepo(repos[0][i-1], repos[1][i-1], repos[2][i-1], repos[3][i-1], repos[4][i-1]);
//            hedgefund.getBalancesheet().addStocks(hedgefund.getBalancesheet().getTotalFunding()/(1-(parameters.getGlobalParameters().get("alpha"))*parameters.getGlobalParameters().get("InitialStockPrice"))*1.2);
//            hedgefund.getBalancesheet().addCash((hedgefund.getBalancesheet().getPhi()*parameters.getGlobalParameters().get("InitialStockPrice"))*0.3);
//            hedgefunds.add(hedgefund);
//
//        }
//
//        System.out.println("INITIAL BALANCESHEETS");
//
//        System.out.println("---------------------------------------");
//
//        banks.get(0).printBalanceSheet();
//        banks.get(1).printBalanceSheet();
//        banks.get(2).printBalanceSheet();
//        hedgefunds.get(0).printBalanceSheet();
//        hedgefunds.get(1).printBalanceSheet();
//        hedgefunds.get(2).printBalanceSheet();
//
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
//        //TODO test whether these initialisations have worked
//        nstep++;
//
//
//        if (nstep>=NSTEPS) simstate.kill();
//
//    }
//
//    public static void main(String[] args){
//        doLoop(TestFullInitialisation.class, args);
//        System.exit(0);
//
//
//    }
//
//
//
//
//
//
//}
