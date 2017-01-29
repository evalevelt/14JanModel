package components;

/**
 * Created by eva on 24/01/2017.
 */
public class BankBalanceSheet {
    double phi;
    Bank bank;
    double Omega_1;
    double Omega_2;
    double Omega_3;
    double Omega_4;
    double Omega_5;
    double C;
    double L;



    public BankBalanceSheet(){
        this.phi=0;
        this.Omega_1=0;
        this.Omega_2=0;
        this.Omega_3=0;
        this.Omega_4=0;
        this.Omega_5=0;
        this.bank=null;
        this.C=0;
        this.L=0;
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
        return Omega_1+Omega_2+Omega_3+Omega_4+Omega_5;
    }

    public void printBank(){
        System.out.println(this.bank.name);
    }
}