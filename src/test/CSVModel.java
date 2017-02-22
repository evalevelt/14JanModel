package test;


import Jama.Matrix;
import components.*;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by eva on 24/01/2017.
 */
public class CSVModel extends SimState implements Steppable {

    // PARAMETERS
    int N_BANKS = 4; //NUMBER OF BANKS
    int N_HEDGEFUNDS =8; //NUMBER OF HEDGEFUNDS

    private double sizeShock;
    public double alpha=0.10;
    private int N_SIMULATIONS;

    private Matrix initialRepos;
    private Matrix bankinfo;
    private Matrix hedgefundinfo;

    private Matrix shockSizes;
    private int nstep;
    private int nSim;

    private Stoppable scheduleRepeat;
    private ArrayList<Bank> banks;
    private ArrayList<Hedgefund> hedgefunds;
    private Market market;
    private InfoExchange infoExchange;

    CSVdealer csvdealer = new CSVdealer();


    double equity[]=new double[100];
    double allequities[][] = new double [100][100];
    Matrix allinfo = new Matrix(53, 5);


    public CSVModel(long seed) {
        super(seed);
    }


    //this is what youre going to change every time before running a simulation. you input parameters from a file and here say
    //which variables you want to have equal to those parameters
    private void setParameters(double shockSize) {
        sizeShock = shockSize;
    }

    //this function is called once
    public void start() {
        super.start();

        //read initialisation data
        bankinfo = csvdealer.readFile("dataModel.csv", 7, N_BANKS);
        hedgefundinfo = csvdealer.readFile("dataModel2.csv", 5, N_HEDGEFUNDS);
        initialRepos = csvdealer.readFile("dataRepos.csv", 9, N_BANKS);

        //read all parameters you want to be trying
        shockSizes=csvdealer.readFile("testingvalues.csv", 1, 51);

        //start MASON schedule
        scheduleRepeat = schedule.scheduleRepeating(this);

    }

    @Override
    public void step(SimState simState) {

        //set # simulations based on how many parameters you said you want to run
        N_SIMULATIONS = shockSizes.getRowDimension();

        //start simulation counter
        nSim = 0;

        while (nSim < N_SIMULATIONS) {

            System.out.println("Running simulation "+nSim+"with shocksize"+shockSizes.get(nSim, 0));
            // load the parameters for this simulation run, including a name for the output file
            double shockSize = shockSizes.get(nSim, 0);
            setParameters(shockSize);

            modelStart();

            //modelStep returns true or false depending on if k==0 or not
            boolean done = false;
            while (!done) {
                done = modelStep(simState);
            }
            endSimulation();
            nSim++;
        }

        simState.kill();
        csvdealer.writeFile1("shocksandequity.csv", allequities, shockSizes);
        csvdealer.writeFile2("detailedinfo.csv", shockSizes, allinfo);



    }




    public void modelStart() {

        nstep = 0;

        //one market is created with initial price 1, every bank and hedgefunds "signs up" to this market later
        market = new Market(1.0, alpha);

        infoExchange = new InfoExchange(N_BANKS, N_HEDGEFUNDS);

        Matrix repos = initialRepos.getMatrix(0,initialRepos.getRowDimension()-1, 1,initialRepos.getColumnDimension()-1);

        infoExchange.setrepos(repos);
        assert(infoExchange.repos.getRowDimension()==N_BANKS);
        assert(infoExchange.repos.getColumnDimension()==N_HEDGEFUNDS);

        System.out.println("INITIAL BALANCESHEETS");
        System.out.println("---------------------------------------");

        banks = new ArrayList<>();
        for (int j = 0; j < N_BANKS; j++) {
            Bank bank = new Bank("Bank " + j, j);
            bank.getBehaviour().setMarket(market);
            bank.getBehaviour().setInfoExchange(infoExchange);
            bank.getBalanceSheet().addLiability(bankinfo.get(j, 6));
            bank.getBehaviour().setNO(); //THIS IS CURRENTLY NOT ACTUALLY USED
            bank.getBalanceSheet().addCash(bankinfo.get(j, 3));
            bank.getBalanceSheet().addStocks(bankinfo.get(j, 5));

            banks.add(bank);
            bank.balanceSheet.printBalanceSheet(bank);
        }

        hedgefunds = new ArrayList<>();
        for (int i = 0; i < N_HEDGEFUNDS; i++) {
            Hedgefund hedgefund = new Hedgefund("Hedgefund " + i,i);
            hedgefund.getBehaviour().setMarket(market);
            hedgefund.getBehaviour().setInfoExchange(infoExchange);
            hedgefund.getBalancesheet().addStocks(hedgefundinfo.get(i, 4));
            hedgefund.getBalancesheet().addCash(hedgefundinfo.get(i, 3));
            hedgefunds.add(hedgefund);
            hedgefund.balanceSheet.printBalanceSheet(hedgefund);

        }



        //now we store current total equity
        double equity_1=0;
        for(int j = 0; j< N_BANKS; j++){
            equity_1=banks.get(j).getBalanceSheet().calculateEquity()+equity_1;}
        for(int i = 0; i< N_HEDGEFUNDS; i++){
            equity_1=hedgefunds.get(i).getBalancesheet().calculateEquity()+equity_1;}
        equity[0]=equity_1;

        //this is where we send a shock to the price of the common stock:
        market.setS(market.S*sizeShock);

    }

    private boolean modelStep(SimState simstate) {

        System.out.println("STEP " + nstep);
        System.out.println("---------------------------------------");

        System.out.println("ACTIONS TAKEN:");
        System.out.println("---------------------------------------");

        for (int j = 0; j < N_BANKS; j++) {
            if (banks.get(j).DEFAULTED == 0.0) {
                banks.get(j).getBehaviour().checkMinLev();
                banks.get(j).getBehaviour().checkBufLev();
                banks.get(j).getBehaviour().deleverRule2();
                banks.get(j).getBehaviour().giveFundingUpdate();
            }


        }



        for (int i = 0; i < N_HEDGEFUNDS; i++) {
            if (hedgefunds.get(i).DEFAULTED == 0) {
                hedgefunds.get(i).getBehaviour().checkDefault();
                hedgefunds.get(i).getBehaviour().repayFunding();
                hedgefunds.get(i).getBehaviour().marginCall();
                hedgefunds.get(i).getBehaviour().giveUpdate();
            }


        }

        for (int j = 0; j < N_BANKS; j++) {
            if (banks.get(j).DEFAULTED == 0) {
                banks.get(j).getBehaviour().checkDefault();
                banks.get(j).getBehaviour().payBack2();
            }
        }

        double k = 0;
        for (int j = 0; j < N_BANKS; j++) {
            k = banks.get(j).B + k;
            k = (1 - banks.get(j).D) + k;
        }
        for (int i = 0; i < N_HEDGEFUNDS; i++) {
            k = (1 - hedgefunds.get(i).D) + k;
        }

        //Order is the variable that will store all the orders that the institutions are placing to buy/sell stock during a timestep
        double Order = 0;


        for (int i = 0; i < N_HEDGEFUNDS; i++) {
            if (hedgefunds.get(i).DEFAULTED == 0) {
                Order = Order + hedgefunds.get(i).getBehaviour().placeMarketOrder();
                //the hedgefunds ONLY NOW update their actual balancesheets, once everything has been decided and calculated.
                //until then, variables in the object "Hedgefund" itself store decisions that have been made
                hedgefunds.get(i).getBehaviour().updateBalanceSheet();
            }


        }

        System.out.println("------------------------------");
        System.out.println("NEW BALANCESHEETS");
        System.out.println("------------------------------");

        for (int j = 0; j < N_BANKS; j++) {
            if (banks.get(j).DEFAULTED == 0) {
                //now the bank has also completed all its actions and can place its final order and update its actual Balancesheet
                Order = Order + banks.get(j).getBehaviour().placeMarketOrder();
                banks.get(j).getBehaviour().updateBalancesheet();
            }
            banks.get(j).balanceSheet.printBalanceSheet(banks.get(j));
        }

        for (int i = 0; i < N_HEDGEFUNDS; i++) {
            hedgefunds.get(i).balanceSheet.printBalanceSheet(hedgefunds.get(i));
        }


        double equity_step = 0;
        for (int j = 0; j < N_BANKS; j++) {
            equity_step = banks.get(j).getBalanceSheet().calculateEquity() + equity_step;
        }
        for (int i = 0; i < N_HEDGEFUNDS; i++) {

            equity_step = hedgefunds.get(i).getBalancesheet().calculateEquity() + equity_step;

        }

        equity[nstep + 1] = equity_step;

        //now the market updates its price with a price impact function based on the price
        market.updateMarket(Order);

        nstep++;


        return (k == 0);

    }

        public void endSimulation() {
            //now we want to store the final equity
            double equity_final=0;
            for(int j = 0; j< N_BANKS; j++){
                equity_final=banks.get(j).getBalanceSheet().calculateEquity()+equity_final;
            }
            for(int i = 0; i< N_HEDGEFUNDS; i++){
                equity_final=hedgefunds.get(i).getBalancesheet().calculateEquity()+equity_final;
            }

            //we want to remove all the zero parts in the equity array. once all equity in the system is 0 it wont become
            //nonzero again so we just find the first zero:

            double[] finalequity;
            finalequity = new double [1];

            for(int j=0;j<equity.length; j++){
                if (equity[j]==0){
                     finalequity=Arrays.copyOfRange(equity, 0, j);
                     allinfo.set(nSim, 0, equity[j-1]);
                     break;
                }
            }

            allinfo.set(nSim, 1, nstep);


            //we also find how many banks have defaulted. THINK ABOUT METHOD HERE

            double Defaults_banks=0;
            for(int j = 0; j< N_BANKS; j++){
                if(banks.get(j).getBalanceSheet().calculateEquity()==0){
                    Defaults_banks++;
                }
            }

            allinfo.set(nSim, 2, Defaults_banks);

            double Defaults_hedgefunds=0;
            for(int i = 0; i< N_HEDGEFUNDS; i++){
                if(hedgefunds.get(i).getBalancesheet().calculateEquity()==0){
                    Defaults_hedgefunds++;
                }
            }

            allinfo.set(nSim, 3, Defaults_hedgefunds);

            allinfo.set(nSim, 4, market.S);




            System.out.println("---------------------------------");


            System.out.println("I am stopping now. The final statistics are:");
            System.out.println("Final Stockprice: " + market.S);
            System.out.println("Number of steps taken: "+nstep);
            System.out.println("Number of defaulted banks: "+Defaults_banks);
            System.out.println("Number of defaulted hedgefunds: "+Defaults_hedgefunds);
            System.out.println("Total Equity in the system at each step:");
            printArray(finalequity);

            allequities[nSim]=finalequity;
//            csvdealer.writeFile("testwriter"+sizeShock+".csv", finalequity);

            //finally we reset finalequity to zero so the next simulation can start afresh

            for (int i=0; i<equity.length; i++) {
                equity[i] = 0;
            }

        }

    public static void main(String[] args){
        doLoop(CSVModel.class, args);
        System.exit(0);
//

    }




    private static void printArray(double[] anArray) {
        for (int i = 0; i < anArray.length; i++) {
            if (i > 0) {
                System.out.print(", ");
            }
            System.out.print(anArray[i]);
        }
    }


}
