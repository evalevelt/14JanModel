package components;

/**
 * Created by eva on 24/01/2017.
 */
public class Bank {
    String name;
   public int D;
   public int B;
    public double x;
    public double y;
    public double z;
    public double D_;
    double NO;
    public int Def_1;
    public int Def_2;
    public int Def_3;
    public int Def_4;
    public int Def_5;

    BankBalanceSheet Balancesheet;
    BankBehaviour Behaviour;

    public Bank(String name){
        this.name=name;
        this.D=1;
        this.B=0;
        this.x=0;
        this.y=0;
        this.z=0;
        this.D_=1;
        this.Behaviour=new BankBehaviour();
        this.Behaviour.bank=this;
        this.NO=0;
    }

    public void setBalancesheet(BankBalanceSheet Balancesheet){
        this.Balancesheet=Balancesheet;
        Balancesheet.bank=this;
    }

    public BankBalanceSheet getBalancesheet(){
        return this.Balancesheet;
    }

    public void printBalanceSheet(){
        System.out.println("Balancesheet "+this.name);
        System.out.println("Assets:");
        System.out.println("Matchbook Repo funding "+this.Balancesheet.getTotalRepo());
        System.out.println("Stockvalue "+this.Balancesheet.phi*this.getBehaviour().market.S);
        System.out.println("Cash "+this.Balancesheet.C);
        System.out.println("Liabilities:");
        System.out.println("Matchbook Repo funding "+this.Balancesheet.getTotalRepo());
        System.out.println("General Liabilities "+this.Balancesheet.L);
        System.out.println("---------------------------------------------");


    }

    public void printName(){
        System.out.println(this.name);
    }

    public void printStockValue(){
        System.out.println(this.Balancesheet.phi);
    }

    public BankBehaviour getBehaviour(){
        return this.Behaviour;
    }

    public void updateBalancesheet(){
        this.Balancesheet.phi=(this.Balancesheet.phi-y/this.getBehaviour().market.S)*D_*D;
        this.Balancesheet.C=(this.Balancesheet.C-z)*D*D_;
        this.Balancesheet.Omega_1=this.Def_1*this.D*this.D_*this.getBalancesheet().Omega_1*(1-(this.x/this.getBalancesheet().getTotalRepo()));
        this.Balancesheet.Omega_2=this.Def_2*this.D*this.D_*this.getBalancesheet().Omega_2*(1-(this.x/this.getBalancesheet().getTotalRepo()));
        this.Balancesheet.Omega_3=this.Def_3*this.D*this.D_*this.getBalancesheet().Omega_3*(1-(this.x/this.getBalancesheet().getTotalRepo()));
        this.Balancesheet.Omega_4=this.Def_4*this.D*this.D_*this.getBalancesheet().Omega_4*(1-(this.x/this.getBalancesheet().getTotalRepo()));
        this.Balancesheet.Omega_5=this.Def_5*this.D*this.D_*this.getBalancesheet().Omega_5*(1-(this.x/this.getBalancesheet().getTotalRepo()));
        this.Balancesheet.L=(this.Balancesheet.L-y-z)*D*D_;


    }


}
