package components;

import Jama.Matrix;

/**
 * Created by eva on 24/01/2017.
 */
public class HedgefundBalanceSheet {

    //these variables are basically the balancesheet items. they are all in pounds EXCEPT phi, which is a number of stocks
    //repos are stored in infoExchange because the values need to be shared with banks. getTotalRepo reads these.

    double phi;
    double C;
    Hedgefund hedgefund;

    public HedgefundBalanceSheet(Hedgefund hedgefund){
        this.phi=0;
        this.C=0;
        this.hedgefund = hedgefund;
    }

    public void addStocks(double newStocks){
        phi=phi+newStocks;
    }

    public void addCash(double Cash){
        this.C=C+Cash;
    }

    public double getTotalFunding(){

        double sum = 0;
        double[] reposColumn = hedgefund.getBehaviour().infoExchange.repos.transpose().getArray()[hedgefund.id];

        for (double repo : reposColumn) {
            sum += repo;
        }

        return sum*(1-hedgefund.DEFAULTED);
    }

    public double getPhi(){
        return phi;
    }

    public double getCash(){return this.C;}

    public double calculateEquity(){
        return (phi*hedgefund.getBehaviour().market.S+C)-getTotalFunding();
    }

    public void printBalanceSheet(Hedgefund hedgefund){
        System.out.println("balanceSheet "+ hedgefund.name);
        System.out.println("Assets:");
        System.out.println("Stockvalue "+ phi* hedgefund.getBehaviour().market.S);
        System.out.println("Cash "+ C);
        System.out.println("Liabilities:");
        System.out.println("Repo funding "+ getTotalFunding());
        System.out.println("---------------------------------------------");
    }
}
