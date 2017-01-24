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
public class TestFullInitialisation extends SimState implements Steppable {
    private int nstep;
    private int NSTEPS=10;
    public Stoppable scheduleRepeat;
    private ArrayList<Bank> banks;
    private ArrayList<Hedgefund> hedgefunds;





    public TestFullInitialisation(long seed) {
        super(seed);
    }

    public void start() {
        super.start();

        double[][] repos={{0,2,3,0,0},{1,0,2,0,0},{4,8,0,0,0},{0,0,0,0,0},{0,0,0,0,0}};

        //TODO insert test for this matrix

        banks = new ArrayList<>();
        for (int j = 1; j < 4; j++) {
            BankBalanceSheet sheet = new BankBalanceSheet();
            Bank bank = new Bank("Bank " + j);
            bank.setBalancesheet(sheet);
            bank.getBalancesheet().addStocks(2.0);
            bank.getBalancesheet().addRepo(repos[j][0], repos[j][1], repos[j][2], repos[j][3], repos[j][4]);
            banks.add(bank);

        }

        hedgefunds = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            HedgefundBalanceSheet sheet = new HedgefundBalanceSheet();
            Hedgefund hedgefund = new Hedgefund("Hedgefund " + i);
            hedgefund.setBalancesheet(sheet);
            hedgefund.getBalancesheet().addStocks(6.0);
            hedgefund.getBalancesheet().addRepo(repos[0][i], repos[1][i], repos[2][i], repos[3][i], repos[4][i]);
            hedgefunds.add(hedgefund);

        }


        nstep=0;
        scheduleRepeat = schedule.scheduleRepeating(this);


    }

    @Override
    public void step(SimState simstate) {

        //TODO test whether these initialisations have worked
        nstep++;
        banks.get(1).getBehaviour().updateBalanceSheet();
        banks.get(1).printStockValue();
        if (nstep>=NSTEPS) simstate.kill();

    }

    public static void main(String[] args){
        doLoop(TestFullInitialisation.class, args);
        System.exit(0);


    }






}
