package test;

import components.*;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by eva on 24/01/2017.
 */
public class TestActions extends SimState implements Steppable {
    private int nstep;
    private int NSTEPS=1;
    public Stoppable scheduleRepeat;
    private ArrayList<Bank> banks;
    private ArrayList<Hedgefund> hedgefunds;
    public Parameters parameters;
    public Market market;





    public TestActions(long seed) {
        super(seed);
    }

    public void start() {
        super.start();
        parameters = new Parameters();
        parameters.initialise();
        market = new Market(2.0);

        double[][] repos={{5,2,3,0,0},{1,3,2,0,0},{4,8,4,0,0},{0,0,0,0,0},{0,0,0,0,0}};
        double [] bankliabilities={10,12,13,0,0};

        banks = new ArrayList<>();
        for (int j = 1; j < 4; j++) {
            BankBalanceSheet sheet = new BankBalanceSheet();
            Bank bank = new Bank("Bank " + j);
            bank.setBalancesheet(sheet);
            bank.getBehaviour().setMarket(market);
            bank.getBalancesheet().addRepo(repos[j-1][0], repos[j-1][1], repos[j-1][2], repos[j-1][3], repos[j-1][4]);
            bank.getBalancesheet().addLiability(bankliabilities[j-1]);
            bank.getBehaviour().setNO();
            bank.getBalancesheet().addCash(bank.getBehaviour().kappa_T*bank.getBehaviour().getNO());
            double K = ((bank.getBehaviour().lambda_T-1)*bank.getBalancesheet().getCash()+bank.getBehaviour().lambda_T*bank.getBalancesheet().getTotalRepo()+bank.getBalancesheet().getLiability())/(market.S*(1-bank.getBehaviour().lambda_T));
            bank.getBalancesheet().addStocks(K);

            banks.add(bank);

        }

        hedgefunds = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            HedgefundBalanceSheet sheet = new HedgefundBalanceSheet();
            Hedgefund hedgefund = new Hedgefund("Hedgefund " + i);
            hedgefund.setBalancesheet(sheet);
            hedgefund.getBehaviour().setMarket(market);
            hedgefund.getBalancesheet().addRepo(repos[0][i-1], repos[1][i-1], repos[2][i-1], repos[3][i-1], repos[4][i-1]);
            hedgefund.getBalancesheet().addStocks(hedgefund.getBalancesheet().getTotalFunding()/(1-(parameters.getGlobalParameters().get("alpha"))*market.S)*1.2);
            hedgefund.getBalancesheet().addCash((hedgefund.getBalancesheet().getPhi()*market.S)*0.3);
            hedgefunds.add(hedgefund);

        }

        System.out.println("INITIAL BALANCESHEETS");

        System.out.println("---------------------------------------");

        banks.get(0).printBalanceSheet();
        System.out.println(banks.get(0).getBehaviour().returnLeverage());
        banks.get(1).printBalanceSheet();
        System.out.println(banks.get(1).getBehaviour().returnLeverage());

        banks.get(2).printBalanceSheet();
        System.out.println(banks.get(2).getBehaviour().returnLeverage());

        hedgefunds.get(0).printBalanceSheet();
        hedgefunds.get(1).printBalanceSheet();
        hedgefunds.get(2).printBalanceSheet();


        nstep=0;
        scheduleRepeat = schedule.scheduleRepeating(this);


    }

    @Override
    public void step(SimState simstate) {
        market.setS(market.S*0.7);


        for (int j = 0; j < 3; j++) {
           banks.get(j).printBalanceSheet();
//           System.out.println("leverage is now"+banks.get(j).getBehaviour().returnLeverage());

            banks.get(j).getBehaviour().checkSolvency();
            banks.get(j).getBehaviour().checkLeverage();
            banks.get(j).getBehaviour().deleverRule2();
            banks.get(j).getBehaviour().checkLCR();

//            System.out.println("unsolvent?" +banks.get(j).D);
//            System.out.println("Delevering?"+banks.get(j).B);
//            System.out.println("decrease in funding"+banks.get(j).x);
//            System.out.println("decrease in assets"+banks.get(j).y);
//            System.out.println("decrease in cash"+banks.get(j).z);
//            System.out.println("LCR breached?"+banks.get(j).D_);
        }

        for(int i=0; i<3; i++){
            for (int j=0; j<3; j++) {
                hedgefunds.get(i).getBehaviour().getFundingUpdate(j+1, banks.get(j).getBehaviour().giveFundingUpdate(i+1));
            }
            hedgefunds.get(i).printName();
            //hedgefunds.get(i).printNewFunding();
            hedgefunds.get(i).getBehaviour().checkSolvency();
            hedgefunds.get(i).getBehaviour().repayFunding();
//            System.out.println(hedgefunds.get(i).z);
//            System.out.println(hedgefunds.get(i).y);
            hedgefunds.get(i).getBehaviour().marginCall();
//            System.out.println("y is "+(hedgefunds.get(i).y));
//
//            System.out.println("collateral needed"+hedgefunds.get(i).getBehaviour().collateralNeeded());
//            System.out.println("I have "+(hedgefunds.get(i).getBalancesheet().getPhi()*market.S-hedgefunds.get(i).y));
//            System.out.println("do we have a margin call?"+hedgefunds.get(i).D_);
//            System.out.println("by how much"+hedgefunds.get(i).MC);

            hedgefunds.get(i).updateBalanceSheet();
            hedgefunds.get(i).printBalanceSheet();


        }

        for (int j=0; j<3;j++){
            for (int i=0; i<3; i++) {
                banks.get(j).getBehaviour().getDefaultInfo(i+1, hedgefunds.get(i).D*hedgefunds.get(i).D_);
            }

            banks.get(j).updateBalancesheet();
            banks.get(j).printBalanceSheet();
            //BANK REPO IS GOING NEGATIVE
        }



        nstep++;
        if (nstep>=NSTEPS) simstate.kill();

    }

    public static void main(String[] args){
        doLoop(TestActions.class, args);
        System.exit(0);
//

    }






}
