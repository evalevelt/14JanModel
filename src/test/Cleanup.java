//package test;
//
//import components.*;
//import sim.engine.SimState;
//import sim.engine.Steppable;
//import sim.engine.Stoppable;
//
// import java.util.Arrays;
//import java.util.ArrayList;
//
///**
// * Created by eva on 24/01/2017.
// */
//public class Cleanup extends SimState implements Steppable {
//    private int nstep;
//    public Stoppable scheduleRepeat;
//    private ArrayList<Bank> banks;
//    private ArrayList<Hedgefund> hedgefunds;
//    public Market market;
//    //this is the array in which we're going to store the equity in each timestep
//    double equity[]=new double[100];
//
//    int J = 4; //NUMBER OF BANKS
//    int I=3; //NUMBER OF HEDGEFUNDS
//
//    //note, you need to change repos matrix and liabilities vector below when you change the number of institutions
//
//    double sizeShock = 0.4;
//    //this is the factor by which we initially reduce assets. with current settings this has to be quite small to get any action happening
//    //0.5 gives actual defaults among hedgefunds
//    //0.4 also gives defaults among banks
//
//
//
//
//    public Cleanup(long seed) {
//        super(seed);
//    }
//
//    //in "start" you choose many parameters, initialise all the balancesheets
//
//    public void start() {
//        super.start();
//
//        //one market is created with initial price 1, every bank and hedgefunds "signs up" to this market later
//        market = new Market(1.0);
//
//        //here you input the matrix of repo contracts, row gives bank, column hedgefund.
//        // there's currently capacity for 5 banks, 5 hedgefunds, leave 0 if you're working with less.
//        double[][] repos={{5,2,3,0,0},{1,3,2,0,0},{4,8,4,0,0},{1,3,9,0,0},{0,0,0,0,0}};
//
//        //these are the generalised liabilities you assign to each bank.
//        double [] bankliabilities={40,25,1,19,0};
//
//        //with the repos and bankliabilities, we calculate the whole rest of the balancesheets
//
//       //extra is the factor by which you multiply the collateral needs for the repo to get the amount of stock
//        //you want the hedgefund to hold initially
//        double extra_HF = 1.5;
//
//        //for the bank, cash is calculated based on the LCR target ratio and the liabilities
//        //stock is then calculated so that the overall Leverage ratio is on target, except this gives a very
//        //small amount of stock in current configuration. you may therefore want to have some extra here too
//        double extra_B=1;
//
//
//        //now we initialise the banks and the hedgefunds. They have a name, balancesheet, behaviour and market.
//        //we  then fill the balancesheet with repos, liabilities, cash, stock according to rules above.
//        //we immediately print all their initial balancesheets.
//
//        System.out.println("INITIAL BALANCESHEETS");
//        System.out.println("---------------------------------------");
//
//        banks = new ArrayList<>();
//        for (int j = 1; j < J+1; j++) {
//            BankBalanceSheet sheet = new BankBalanceSheet();
//            Bank bank = new Bank("Bank " + j);
//            bank.setBalanceSheet(sheet);
//            bank.getBehaviour().setMarket(market);
//            bank.getBalanceSheet().addRepo(repos[j-1][0], repos[j-1][1], repos[j-1][2], repos[j-1][3], repos[j-1][4]);
//            bank.getBalanceSheet().addLiability(bankliabilities[j-1]);
//            //NO is the constant denominator in the LCR ratio, which we set once here based on liabilities
//            bank.getBehaviour().setNO();
//            bank.getBalanceSheet().addCash(bank.getBehaviour().kappa_T*bank.getBehaviour().getNO());
//            double K = ((bank.getBehaviour().lambda_T-1)*bank.getBalanceSheet().getCash()+bank.getBehaviour().lambda_T*bank.getBalanceSheet().getTotalRepo()+bank.getBalanceSheet().getLiability())/(market.S*(1-bank.getBehaviour().lambda_T));
//            bank.getBalanceSheet().addStocks(K*extra_B);
//
//            banks.add(bank);
//            bank.printBalanceSheet();
//        }
//
//        hedgefunds = new ArrayList<>();
//        for (int i = 1; i < I+1; i++) {
//            HedgefundBalanceSheet sheet = new HedgefundBalanceSheet();
//            Hedgefund hedgefund = new Hedgefund("Hedgefund " + i);
//            hedgefund.setBalancesheet(sheet);
//            hedgefund.getBehaviour().setMarket(market);
//            hedgefund.getBalancesheet().addRepo(repos[0][i-1], repos[1][i-1], repos[2][i-1], repos[3][i-1], repos[4][i-1]);
//            hedgefund.getBalancesheet().addStocks(hedgefund.getBalancesheet().getTotalFunding()/((1-(hedgefund.getBehaviour().alpha))*market.S)*extra_HF);
//            hedgefund.getBalancesheet().addCash((hedgefund.getBalancesheet().getPhi()*market.S)*0.3);
//            hedgefunds.add(hedgefund);
//            hedgefund.printBalanceSheet();
//
//        }
//
//
//
//        //now we store current equity
//        double equity_1=0;
//        for(int j=0;j<J;j++){
//            equity_1=banks.get(j).getBalanceSheet().calculateEquity()+equity_1;
//        }
//        for(int i=0; i<I; i++){
//            equity_1=hedgefunds.get(i).getBalancesheet().calculateEquity()+equity_1;
//        }
//
//        equity[0]=equity_1;
//
//        //this is where we send a shock to the price of the common stock:
//        market.setS(market.S*sizeShock);
//
//        nstep=0;
//        scheduleRepeat = schedule.scheduleRepeating(this);
//
//    }
//
//    @Override
//    public void step(SimState simstate) {
//
//        System.out.println("STEP "+nstep);
//        System.out.println("---------------------------------------");
//
//        //we store the equity at the start of the timestep
//        double equity_step=0;
//        for(int j=0;j<J;j++){
//            equity_step=banks.get(j).getBalanceSheet().calculateEquity()+equity_step;
//        }
//        for(int i=0; i<I; i++){
//            equity_step=hedgefunds.get(i).getBalancesheet().calculateEquity()+equity_step;
//        }
//
//        equity[nstep+1]=equity_step;
//
//        //these are all the actions for the bank in a time step. every action is defined in behaviour. you can flip between LCR and non LCR
//        //by choosing one of these options:
//        //LCR:
//        //banks.get(j).getBehaviour().checkSolvency();
//        //banks.get(j).getBehaviour().checkLeverage();
//        // banks.get(j).getBehaviour().deleverRule2();
//        //banks.get(j).getBehaviour().checkLCR();
//        //NONLCR:
//        //banks.get(j).getBehaviour().checkSolvency();
//        //banks.get(j).getBehaviour().checkLeverage();
//        //banks.get(j).getBehaviour().deleverRule1();
//
//        System.out.println("ACTIONS TAKEN:");
//        System.out.println("---------------------------------------");
//
//        for (int j = 0; j < J; j++) {
//            banks.get(j).getBehaviour().checkSolvency();
//            banks.get(j).getBehaviour().checkLeverage();
//            banks.get(j).getBehaviour().deleverRule2();
//            banks.get(j).getBehaviour().checkLCR();
//
//        }
//
//        //Order is the variable that will store all the orders that the institutions are placing to buy/sell stock during a timestep
//        double Order=0;
//
//        //these are the actions for the hedgefund in a timestep, they act after the banks.
//        //it starts with the banks informing the hedgefunds of any reduction of repo-funding
//        for(int i=0; i<I; i++){
//            for (int j=0; j<J; j++) {
//                hedgefunds.get(i).getBehaviour().getFundingUpdate(j+1, banks.get(j).getBehaviour().giveFundingUpdate(i+1));
//            }
//            hedgefunds.get(i).getBehaviour().checkSolvency();
//            hedgefunds.get(i).getBehaviour().repayFunding();
//            hedgefunds.get(i).getBehaviour().marginCall();
//
//            //here the hedgefunds tell the banks their orders
//            Order=Order+hedgefunds.get(i).getBehaviour().placeMarketOrder();
//
//
//            //the hedgefunds ONLY NOW update their actual balancesheets, once everything has been decided and calculated.
//            //until then, variables in the object "Hedgefund" itself store decisions that have been made
//            hedgefunds.get(i).getBehaviour().updateBalanceSheet();
//
//
//        }
//
//        System.out.println("------------------------------");
//        System.out.println("NEW BALANCESHEETS");
//        System.out.println("------------------------------");
//
//
//
//        //banks now get told whether any hedgefunds they were providing funding to have defaulted, so that they can
//        //take this into account for their balancesheets
//        //NOTE THIS NOW ONLY WORKS IF THERE'S THE SAME NUMBER OF HEDGEFUNDS AND BANKS
//
//
//        for (int j=0; j<J;j++){
//            for (int i=0; i<I; i++) {
//                banks.get(j).getBehaviour().getDefaultInfo(i+1, hedgefunds.get(i).D*hedgefunds.get(i).D_);
//
//            }
//
//            //now the bank has also completed all its actions and can place its final order and update its actual Balancesheet
//            Order=Order+banks.get(j).getBehaviour().placeMarketOrder();
//
//            banks.get(j).getBehaviour().updateBalancesheet();
//            banks.get(j).printBalanceSheet();
//        }
//
//        for (int i=0; i<I; i++) {
//            hedgefunds.get(i).printBalanceSheet();
//        }
//
//        //variable k now checks if anyone actually acted this timestep, using the temporary variables B and D in the hedgefund and
//        //bank objects that decide whether someone defaulted or delevered. if k=0, nothing happened this step and the simulation ends.
//
//        double k=0;
//        for(int j=0;j<J;j++){
//            k=banks.get(j).B+k;
//            k=(1-banks.get(j).D)+k;
//        }
//        for(int i=0; i<I; i++){
//            k=(1-hedgefunds.get(i).D)+k;
//        }
//
//
//        //now the market updates its price with a price impact function based on the price
//        market.updateMarket(Order);
//
//        nstep++;
//
//        if (k==0) {
//            simstate.kill();
//            //now we want to store the final equity
//            double equity_final=0;
//            for(int j=0;j<J;j++){
//                equity_final=banks.get(j).getBalanceSheet().calculateEquity()+equity_final;
//            }
//            for(int i=0; i<I; i++){
//                equity_final=hedgefunds.get(i).getBalancesheet().calculateEquity()+equity_final;
//            }
//
//            //we want to remove all the zero parts in the equity array. once all equity in the system is 0 it wont become
//            //nonzero again so we just find the first zero:
//
//            double[] finalequity;
//            finalequity = new double [1];
//
//            for(int j=0;j<equity.length; j++){
//                if (equity[j]==0){
//                     finalequity=Arrays.copyOfRange(equity, 0, j);
//                     break;
//                }
//            }
//
//            //we also find how many banks have defaulted
//
//            double Defaults_banks=0;
//            for(int j=0;j<J;j++){
//                if(banks.get(j).getBalanceSheet().calculateEquity()==0){
//                    Defaults_banks++;
//                };
//            }
//
//            double Defaults_hedgefunds=0;
//            for(int i=0;i<I;i++){
//                if(hedgefunds.get(i).getBalancesheet().calculateEquity()==0){
//                    Defaults_hedgefunds++;
//                };
//            }
//
//
//            System.out.println("---------------------------------");
//
//
//            System.out.println("N_HEDGEFUNDS am stopping now. The final statistics are:");
//            System.out.println("Final Stockprice: " + market.S);
//            System.out.println("Number of steps taken: "+nstep);
//            System.out.println("Number of defaulted banks: "+Defaults_banks);
//            System.out.println("Number of defaulted hedgefunds: "+Defaults_hedgefunds);
//            System.out.println("Total Equity in the system at each step:");
//            printArray(finalequity);
//
//
//        }
//
//
//
//    }
//
//    public static void main(String[] args){
//        doLoop(Cleanup.class, args);
//        System.exit(0);
////
//
//    }
//
//
//
//
//    private static void printArray(double[] anArray) {
//        for (int i = 0; i < anArray.length; i++) {
//            if (i > 0) {
//                System.out.print(", ");
//            }
//            System.out.print(anArray[i]);
//        }
//    }
//
//
//
//
//
//}
