//package test;
//
//
//import Jama.Matrix;
//import components.*;
//import sim.engine.SimState;
//import sim.engine.Steppable;
//import sim.engine.Stoppable;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
///**
// * Created by eva on 24/01/2017.
// */
//public class CSVModelNOHF extends SimState implements Steppable {
//
//    // PARAMETERS
//    int N_BANKS = 4; //NUMBER OF BANKS
//    int N_HEDGEFUNDS =2; //NUMBER OF HEDGEFUNDS
//
//    private double sizeShock=0.8;
//    public double alpha=0.10;
//    public double eta=3;
//    public double delta=10000000;
//    private int N_SIMULATIONS;
//    private int N_ROWS=62;
//    private int N_COLUMNS=1;
//
//    private Matrix initialRepos;
//    private Matrix bankinfo;
//    private Matrix hedgefundinfo;
//
//    private Matrix inputdata;
//    private int nstep;
//    private int nSim;
//    private int nSim_c;
//
//    private Stoppable scheduleRepeat;
//    private ArrayList<Bank> banks;
//    private ArrayList<Hedgefund> hedgefunds;
//    private Market market;
//    private InfoExchange infoExchange;
//
//    CSVdealer csvdealer = new CSVdealer();
//
//
//    double equity[]=new double[100];
//    double allequities[][] = new double [N_ROWS][N_COLUMNS];
//    double bank0equity[]=new double[N_ROWS];
//    double bank1equity[]=new double[N_ROWS];
//    Matrix allinfo = new Matrix(N_ROWS, 6);
//    String[] allWhichOnes=new String[N_ROWS];
//    double k=0;
//
//
//    public CSVModelNOHF(long seed) {
//        super(seed);
//    }
//
//
//    //this is what youre going to change every time before running a simulation. you input parameters from a file and here say
//    //which variables you want to have equal to those parameters
//    private void setParameters(double sizeShock) {
//        this.sizeShock = sizeShock;
//    }
//
//    //this function is called once
//    public void start() {
//        super.start();
//
//        //read initialisation data
//        bankinfo = csvdealer.readFile("dataModelNOHF.csv", 9, N_BANKS);
//        hedgefundinfo = csvdealer.readFile("dataModelNO2HF.csv", 5, N_HEDGEFUNDS);
//        initialRepos = csvdealer.readFile("dataReposNOHF.csv", N_HEDGEFUNDS+1, N_BANKS);
//
//        //read all parameters you want to be trying
//        inputdata=csvdealer.readFile("testingvalues.csv", 1, N_ROWS);
//
//        //start MASON schedule
//        scheduleRepeat = schedule.scheduleRepeating(this);
//
//    }
//
//    @Override
//    public void step(SimState simState) {
//
//        //set # simulations based on how many parameters you said you want to run
//        N_SIMULATIONS = (inputdata.getRowDimension());
//
//        //start simulationrow counter
//        nSim = 0;
//        nSim_c=0;
//
//        for (nSim=0;nSim < N_SIMULATIONS; nSim++) {
//
//                System.out.println("Running simulation " + nSim + "with shocksize" + inputdata.get(nSim, 0));
//                // load the parameters for this simulation run, including a name for the output file
//                double shocksize = inputdata.get(nSim, 0);
//                setParameters(shocksize);
//
//                modelStart();
//
//                //modelStep returns true or false depending on if k==0 or not
//                boolean done = false;
//                while (!done) {
//                    done = modelStep(simState);
//                }
//                endSimulation();
//            }
//
//        simState.kill();
//        csvdealer.writeFile1("shocksandequity.csv", allequities, inputdata);
//        csvdealer.writeFile2("detailedinfo.csv", inputdata, allinfo, allWhichOnes);
//        csvdealer.writeFile3("bank0.csv", inputdata, bank0equity);
//        csvdealer.writeFile3("bank1.csv", inputdata, bank1equity);
//    }
//
//
//
//
//    public void modelStart() {
//
//        nstep = 0;
//
//        //one market is created with initial price 1, every bank and hedgefunds "signs up" to this market later
//        market = new Market(1.0, alpha, eta, delta);
//
//        infoExchange = new InfoExchange(N_BANKS, N_HEDGEFUNDS);
//
//        Matrix repos = initialRepos.getMatrix(0,initialRepos.getRowDimension()-1, 1,initialRepos.getColumnDimension()-1);
//
//        infoExchange.setrepos(repos);
//
//        assert(infoExchange.repos.getRowDimension()==N_BANKS);
//        assert(infoExchange.repos.getColumnDimension()==N_HEDGEFUNDS);
//
//
//        System.out.println("INITIAL BALANCESHEETS");
//        System.out.println("---------------------------------------");
//
//        banks = new ArrayList<>();
//        for (int j = 0; j < N_BANKS; j++) {
//            Bank bank = new Bank("Bank " + j, j);
//            bank.getBehaviour().setMarket(market);
//            bank.getBehaviour().setInfoExchange(infoExchange);
//            bank.getBalanceSheet().setLiability(bankinfo.get(j, 6));
//            bank.getBehaviour().setNO();
//            bank.getBalanceSheet().setCash(bankinfo.get(j, 3));
//            bank.getBalanceSheet().setStocks(bankinfo.get(j, 5));
//            bank.getBehaviour().setLeveragePreferences(bankinfo.get(j,7), bankinfo.get(j,8));
//
//
//            banks.add(bank);
//            bank.balanceSheet.printBalanceSheet(bank);
//        }
//
//        hedgefunds = new ArrayList<>();
//        for (int i = 0; i < N_HEDGEFUNDS; i++) {
//            Hedgefund hedgefund = new Hedgefund("Hedgefund " + i,i);
//            hedgefund.getBehaviour().setMarket(market);
//            hedgefund.getBehaviour().setInfoExchange(infoExchange);
//            hedgefund.getBalancesheet().setStocks(hedgefundinfo.get(i, 4));
//            hedgefund.getBalancesheet().setCash(hedgefundinfo.get(i, 3));
//            hedgefunds.add(hedgefund);
//            hedgefund.balanceSheet.printBalanceSheet(hedgefund);
//
//        }
//
//
//
//        //now we store current total equity
//        double equity_1=0;
//        for(int j = 0; j< N_BANKS; j++){
//            equity_1=banks.get(j).getBalanceSheet().calculateEquity()+equity_1;}
//        for(int i = 0; i< N_HEDGEFUNDS; i++){
//            equity_1=hedgefunds.get(i).getBalancesheet().calculateEquity()+equity_1;}
//        equity[0]=equity_1;
//
//        //this is where we send a shock to the price of the common stock:
//        market.setS(market.S*sizeShock);
//        System.out.println(" i used sizeshock"+sizeShock);
//
//    }
//
//    private boolean modelStep(SimState simstate) {
//
//        System.out.println("STEP " + nstep);
//        System.out.println("---------------------------------------");
//
//        System.out.println("ACTIONS TAKEN:");
//        System.out.println("---------------------------------------");
//
//        for (int j = 0; j < N_BANKS; j++) {
//            if (banks.get(j).DEFAULTED == 0.0) {
//                banks.get(j).getBehaviour().checkMinLev();
//                banks.get(j).getBehaviour().checkBufLev();
//                banks.get(j).getBehaviour().deleverRule2();
//                banks.get(j).getBehaviour().giveFundingUpdate();
//            }
//
//
//        }
//
//        for (int i = 0; i < N_HEDGEFUNDS; i++) {
//            if (hedgefunds.get(i).DEFAULTED == 0) {
//                hedgefunds.get(i).getBehaviour().checkCasesandAct();
//                hedgefunds.get(i).getBehaviour().giveUpdate();
//            }
//        }
//
//        for (int j = 0; j < N_BANKS; j++) {
//            if (banks.get(j).DEFAULTED == 0) {
//                banks.get(j).getBehaviour().performPayback();
//            }
//        }
//
//        double k_old=k;
//
//        double k = 0;
//        for (int j = 0; j < N_BANKS; j++) {
//            k = banks.get(j).B + k;
//            k = (1 - banks.get(j).D) + k;
//        }
//        for (int i = 0; i < N_HEDGEFUNDS; i++) {
//            k = (1 - hedgefunds.get(i).D) + k;
//        }
//
//        //Order is the variable that will store all the orders that the institutions are placing to buy/sell stock during a timestep
//        double Order = 0;
//
//
//        for (int i = 0; i < N_HEDGEFUNDS; i++) {
//            if (hedgefunds.get(i).DEFAULTED == 0) {
//                Order = Order + hedgefunds.get(i).getBehaviour().placeMarketOrder();
//                //the hedgefunds ONLY NOW update their actual balancesheets, once everything has been decided and calculated.
//                //until then, variables in the object "Hedgefund" itself store decisions that have been made
//                hedgefunds.get(i).getBehaviour().updateBalanceSheet();
//            }
//
//
//        }
//
//        System.out.println("------------------------------");
//        System.out.println("NEW BALANCESHEETS");
//        System.out.println("------------------------------");
//
//        for (int j = 0; j < N_BANKS; j++) {
//            if (banks.get(j).DEFAULTED == 0) {
//                //now the bank has also completed all its actions and can place its final order and update its actual Balancesheet
//                Order = Order + banks.get(j).getBehaviour().placeMarketOrder();
//                banks.get(j).getBehaviour().updateBalancesheet();
//            }
//            banks.get(j).balanceSheet.printBalanceSheet(banks.get(j));
//        }
//
//        for (int i = 0; i < N_HEDGEFUNDS; i++) {
//            hedgefunds.get(i).balanceSheet.printBalanceSheet(hedgefunds.get(i));
//        }
//
//
//        double equity_step = 0;
//        for (int j = 0; j < N_BANKS; j++) {
//            equity_step = banks.get(j).getBalanceSheet().calculateEquity() + equity_step;
//            banks.get(j).reset();
//        }
//        for (int i = 0; i < N_HEDGEFUNDS; i++) {
//
//            equity_step = hedgefunds.get(i).getBalancesheet().calculateEquity() + equity_step;
//            hedgefunds.get(i).reset();
//
//        }
//
//        equity[nstep + 1] = equity_step;
//
//        infoExchange.reset(N_BANKS, N_HEDGEFUNDS);
//
//        //now the market updates its price with a price impact function based on the price
//        market.updateMarket(Order);
//
//        nstep++;
//
//
//        return (k+k_old == 0);
//
//    }
//
//        public void endSimulation() {
//            //now we want to store the final equity
//            double equity_final=0;
//            for(int j = 0; j< N_BANKS; j++){
//                equity_final=banks.get(j).getBalanceSheet().calculateEquity()+equity_final;
//            }
//            for(int i = 0; i< N_HEDGEFUNDS; i++){
//                equity_final=hedgefunds.get(i).getBalancesheet().calculateEquity()+equity_final;
//            }
//
//            equity[nstep+1]=equity_final;
//
//            //we want to remove all the zero parts in the equity array. once all equity in the system is 0 it wont become
//            //nonzero again so we just find the first zero:
//
//            System.out.println("equity_final="+equity_final);
//
//
//
//            allinfo.set(nSim, 1, nstep);
//
//
//            //we also find how many banks have defaulted.
//
//            String WhichOnes = "";
//
//            double Defaults_banks=0;
//            for(int j = 0; j< N_BANKS; j++){
//                if(banks.get(j).getBalanceSheet().calculateEquity()==0){
//                    WhichOnes=WhichOnes+"bank"+banks.get(j).id;
//                    Defaults_banks++;
//                }
//            }
//
//            allinfo.set(nSim, 2, Defaults_banks);
//
//            double Defaults_hedgefunds=0;
//            for(int i = 0; i< N_HEDGEFUNDS; i++){
//                if(hedgefunds.get(i).getBalancesheet().calculateEquity()==0){
//                    WhichOnes=WhichOnes+"hedgefund"+hedgefunds.get(i).id;
//
//                    Defaults_hedgefunds++;
//                }
//            }
//
//
//            allWhichOnes[nSim]=WhichOnes;
//
//            allinfo.set(nSim, 3, Defaults_hedgefunds);
//
//            allinfo.set(nSim, 4, market.S);
//
//
//            double[] finalequity;
//            finalequity = new double [1];
//
//            for(int j=0;j<equity.length; j++) {
//                if (equity[j] == 0) {
//                    if (Defaults_hedgefunds == N_HEDGEFUNDS && Defaults_banks == N_BANKS) {
//                        finalequity = Arrays.copyOfRange(equity, 0, j );
//                        allinfo.set(nSim, 0, equity[j]);
//
//
//                        break;
//                    } else {
//                        finalequity = Arrays.copyOfRange(equity, 0, j);
//                        allinfo.set(nSim, 0, equity[j-1]);
//                        break;
//                    }
//                }
//            }
//
//            allequities[nstep]=finalequity;
//
//
//            bank0equity[nSim]=banks.get(0).getBalanceSheet().calculateEquity();
//            bank1equity[nSim]=banks.get(1).getBalanceSheet().calculateEquity();
//
//
//
//            System.out.println("---------------------------------");
//
//
//            System.out.println("I am stopping now. The final statistics are:");
//            System.out.println("Final Stockprice: " + market.S);
//            System.out.println("Number of steps taken: "+nstep);
//            System.out.println("Number of defaulted banks: "+Defaults_banks);
//            System.out.println("Number of defaulted hedgefunds: "+Defaults_hedgefunds);
//            System.out.println("Total Equity in the system at each step:");
//            printArray(finalequity);
//
////            csvdealer.writeFile("testwriter"+sizeShock+".csv", finalequity);
//
//            //finally we reset finalequity to zero so the next simulation can start afresh
//
//            for (int i=0; i<equity.length; i++) {
//                equity[i] = 0;
//            }
//
//        }
//
//    public static void main(String[] args){
//        doLoop(CSVModelNOHF.class, args);
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
//    private static void printDoubleArray(double[][] anArray) {
//        for (int i = 0; i < anArray.length; i++) {
//            for (int j = 0; j < anArray[i].length; j++){
//                if (i > 0 && j > 0) {
//                    System.out.print(", ");
//                }
//            System.out.print(anArray[i][j]);
//        }
//    }
//    }
//
//
//}
