package components;

/**
 * Created by eva on 24/01/2017.
 */
public class Bank {

    //this object stores variables that are basically auxiliary in the timestep. only at the end of the timestep are they
    //all used to update the balancesheet. they are set by Behaviour. public/nonpublic has solely been determined by convenience
    //as some need to be accessed in the testcase, this should be improved.

    String name;
   public int D;
   public int B;
     double x;
     double y;
     double z;
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

    public void printStockAmount(){
        System.out.println(this.Balancesheet.phi);
    }

    public BankBehaviour getBehaviour(){
        return this.Behaviour;
    }




}
