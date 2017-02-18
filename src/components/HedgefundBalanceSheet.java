package components;

import Jama.Matrix;

/**
 * Created by eva on 24/01/2017.
 */
public class HedgefundBalanceSheet {

    //for now we only have capacity for 5 hedgefunds and 5 banks. Omega_1 as a variable for a hedgefund means the amount of repo it receives
    //from bank 1. (ideally this system changes because it's very ugly and has limited capacity).

    double phi;

    double C;
    Hedgefund hedgefund;

    public HedgefundBalanceSheet(Hedgefund hedgefund){
        this.phi=0;
        this.hedgefund=null;
        this.C=0;

        this.hedgefund = hedgefund;

    }

    public void addStocks(double newStocks){
        phi=phi+newStocks;
    }


    public double getTotalFunding(){

        double sum = 0;
        double[] reposColumn = hedgefund.getBehaviour().infoExchange.repos.transpose().getArray()[hedgefund.id];

        for (double repo : reposColumn) {
            sum += repo;
        }

        return sum;
    }

    public double getPhi(){
        return phi;
    }

    public void addCash(double Cash){
        this.C=C+Cash;
    }

    public double getCash(){return this.C;}

    public double calculateEquity(){
        return (phi*hedgefund.getBehaviour().market.S+C)-getTotalFunding();
    }


    public void printHedgefund(){
        System.out.println(this.hedgefund.name);
    }
}
