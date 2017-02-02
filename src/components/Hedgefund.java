package components;

/**
 * Created by eva on 24/01/2017.
 */
public class Hedgefund {

    //this object stores variables that are basically auxiliary in the timestep. only at the end of the timestep are they
    //all used to update the balancesheet. they are set by Behaviour. public/nonpublic has solely been determined by convenience
    //as some need to be accessed in the testcase, this should be improved.

    String name;
    HedgefundBalanceSheet Balancesheet;
    HedgefundBehaviour Behaviour;
    public int D;
    public int D_;
    double Omega_1_;
    double Omega_2_;
    double Omega_3_;
    double Omega_4_;
    double Omega_5_;
   public double y;
   public double z;
   public double MC;
    public double alpha;


    public Hedgefund(String name){
        this.name=name;
        this.D=1;
        this.D_=1;
        this.MC=0;
        this.Behaviour=new HedgefundBehaviour();
        this.Behaviour.hedgefund=this;
    }


    public void setBalancesheet(HedgefundBalanceSheet Balancesheet){
        this.Balancesheet=Balancesheet;
        Balancesheet.hedgefund=this;
    }

    public HedgefundBalanceSheet getBalancesheet(){
        return this.Balancesheet;
    }

    public void printBalanceSheet(){
        System.out.println("Balancesheet "+this.name);
        System.out.println("Assets:");
        System.out.println("Stockvalue "+this.Balancesheet.phi*this.getBehaviour().market.S);
        System.out.println("Cash "+this.Balancesheet.C);
        System.out.println("Liabilities:");
        System.out.println("Repo funding "+this.Balancesheet.getTotalFunding());
        System.out.println("---------------------------------------------");


    }

    public void printNewFunding(){
        System.out.println(Omega_1_);
        System.out.println(Omega_2_);

        System.out.println(Omega_3_);

        System.out.println(Omega_4_);

        System.out.println(Omega_5_);

    }

    public void printName(){
        System.out.println(this.name);
    }

    public void printStockValue(){
        System.out.println(this.Balancesheet.phi);
    }

    public HedgefundBehaviour getBehaviour(){
        return this.Behaviour;
    }

    public double getTotalNewFunding(){
        return Omega_1_+Omega_2_+Omega_3_+Omega_4_+Omega_5_;
    }


}