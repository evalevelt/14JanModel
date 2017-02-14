package components;

import Jama.Matrix;

/**
 * Created by eva on 24/01/2017.
 */
public class BankBalanceSheet {

    //these variables are basically the balancesheet items. they are all in pounds EXCEPT phi, which is a number of stocks
    //for now we only have capacity for 5 hedgefunds and 5 banks. Omega_1 as a variable for a bank means the amount of repo it passes
    //to hedgefund 1 (ideally this system changes because it's very ugly and has limited capacity).
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

    public void addStocks(double newStocks){
        phi=phi+newStocks;
    }

//    public void addRepo(double one, double two, double three, double four, double five){
//
//
//    }

    public void addCash(double Cash){
        this.C=C+Cash;
    }

    public void addLiability(double Liability){
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
        double[] reposRow = bank.repos.getArray()[bank.id];

        for (double repo : reposRow) {
            sum += repo;
        }

        return sum;
    }

    public double calculateEquity(){
        return (phi*bank.getBehaviour().market.S+C)-L;
    }

    public void printBank(){
        System.out.println(this.bank.name);
    }
}
