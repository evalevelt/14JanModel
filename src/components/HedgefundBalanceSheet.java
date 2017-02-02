package components;

/**
 * Created by eva on 24/01/2017.
 */
public class HedgefundBalanceSheet {

    //for now we only have capacity for 5 hedgefunds and 5 banks. Omega_1 as a variable for a hedgefund means the amount of repo it receives
    //from bank 1. (ideally this system changes because it's very ugly and has limited capacity).

    double phi;
    double Omega_1;
    double Omega_2;
    double Omega_3;
    double Omega_4;
    double Omega_5;
    double C;
    Hedgefund hedgefund;

    public HedgefundBalanceSheet(){
        this.phi=0;
        this.Omega_1=0;
        this.Omega_2=0;
        this.Omega_3=0;
        this.Omega_4=0;
        this.Omega_5=0;
        this.hedgefund=null;
        this.C=0;
    }

    public void addStocks(double newStocks){
        phi=phi+newStocks;
    }

    public void addRepo(double one, double two, double three, double four, double five){
        Omega_1=Omega_1+one;
        Omega_2=Omega_2+two;
        Omega_3=Omega_3+three;
        Omega_4=Omega_4+four;
        Omega_5=Omega_5+five;
    }

    public double getTotalFunding(){
        return Omega_1+Omega_2+Omega_3+Omega_4+Omega_5;
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
