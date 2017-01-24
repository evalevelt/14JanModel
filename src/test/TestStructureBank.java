package test;

import components.Bank;
import components.BankBalanceSheet;
import components.BankBehaviour;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by eva on 24/01/2017.
 */
public class TestStructureBank extends SimState implements Steppable {
 private int nstep;
 private int NSTEPS=10;
 public Stoppable scheduleRepeat;
 private ArrayList<Bank> banks;




    public TestStructureBank(long seed) {
        super(seed);
    }

    public void start() {
        super.start();

        banks = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            BankBalanceSheet sheet = new BankBalanceSheet();
            Bank bank = new Bank("Bank " + i);
            bank.setBalancesheet(sheet);
            banks.add(bank);

        }
        nstep=0;
        scheduleRepeat = schedule.scheduleRepeating(this);


    }

    @Override
    public void step(SimState simstate) {
        nstep++;
        banks.get(1).getBehaviour().updateBalanceSheet();
        banks.get(1).printStockValue();
        if (nstep>=NSTEPS) simstate.kill();

    }

    public static void main(String[] args){
        doLoop(TestStructureBank.class, args);
        System.exit(0);


}






}
