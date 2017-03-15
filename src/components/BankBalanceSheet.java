package components;

import Jama.Matrix;

/**
 * Created by eva on 24/01/2017.
 */
public class BankBalanceSheet {

    //these variables are basically the balancesheet items. they are all in pounds EXCEPT phi, which is a number of stocks
    //repos are stored in infoExchange because the values need to be shared with hedgefunds. getTotalRepo reads these.

    double phi;
    double C;
    double L;

    Bank bank;


    public BankBalanceSheet(Bank bank){
        this.phi=0;
        this.bank=bank;
        this.C=0;
        this.L=0;
    }

    public void setStocks(double newStocks){
        phi=newStocks;
    }

    public void setCash(double Cash){
        this.C=Cash;
    }

    public void setLiability(double Liability){
        this.L=L+Liability;
    }

    public double getLiability(){
        return L;
    }

    public double getCash() {
        return C;
    }

    public double getTotalRepo(){

        double sum = 0;
        double[] reposRow = bank.getBehaviour().infoExchange.repos.getArray()[bank.id];

        for (double repo : reposRow) {
            sum += repo;
        }

        return sum*(1-bank.DEFAULTED);
    }

    public double calculateEquity(){
        return (phi*bank.getBehaviour().market.S+C)-L;
    }

    public double totalAssets(){
        return getTotalRepo()+C+phi*bank.getBehaviour().market.S;
    }

    public void printBalanceSheet(Bank bank){
        System.out.println("balanceSheet "+ bank.name);
        System.out.println("Assets:");
        System.out.println("Matchbook Repo funding "+ getTotalRepo());
        System.out.println("Stockvalue "+ phi* bank.getBehaviour().market.S);
        System.out.println("Cash "+ C);
        System.out.println("Liabilities:");
        System.out.println("Matchbook Repo funding "+ getTotalRepo());
        System.out.println("General Liabilities "+ L);
        System.out.println("---------------------------------------------");
    }
}
